/*
 * Job
 * 
 * Version 1.0 - 10.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.warehouse.model;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import pl.polsl.mrukwa.grzegorz.helpers.Loggable;

/**
 * This class will represent the abstraction of a job.
 * @author Grzegorz Mrukwa
 */
public class Job extends Loggable {
    
    /**
     * Period the job is released with, in ms.
     */
    private final int periodInMs;
    
    /**
     * The last release time.
     */
    private Date releaseTime;
    
    /**
     * Queue containing all release times of unprocessed data.
     */
    private final Queue<Date> releaseTimes;
    
    /**
     * Table to which job loads the data.
     */
    private final Table table;
    
    /**
     * Parameters specifying the time necessary to initialize ETL process
     * and the rows number per unit time.
     */
    private double alpha = 100, beta = 0.1;
    
    /**
     * Allows to wait for releases of the jobs.
     */
    private Semaphore unprocessed;
    
    /**
     * Locks access to this job in order to prevent concurrent updates of a
     * table.
     */
    private Semaphore semaphore;
    
    /**
     * Specifies what to run when not all chops were processed yet.
     */
    private Runnable onNotAllRan;
    
    /**
     * Track boundaries assigned by global schedulers.
     */
    private int trackLo, trackHi;
    
    /**
     * Group specifying which basic scheduler should be used for that job.
     */
    private int group;
    
    /**
     * Default constructor setting basic parameters.
     * @param periodInMs period the job will be released with.
     * @param table table the job is updating.
     */
    public Job(int periodInMs, Table table){
        this.periodInMs = periodInMs;
        this.table = table;
        this.releaseTimes = new ConcurrentLinkedDeque<>();
        this.releaseTime = new Date();
        this.trackLo = this.trackHi = this.group = -1;
        this.unprocessed = new Semaphore(0);
        this.semaphore = new Semaphore(1);
    }
    
    /**
     * Simulates job work.
     * @param chopsLimit limits processing.
     * @throws InterruptedException when thread was interrupted.
     */
    public void run(int chopsLimit) throws InterruptedException {
        semaphore.acquire();
        int currentUnprocessed = unprocessed.availablePermits();
        int limit = Integer.min(currentUnprocessed,chopsLimit);
        logger.accept(String.format("Processing %d chunks.",limit));
        Thread.sleep(getEstimateChopped(limit));
        for(int i=0; i<limit; ++i){
            --currentUnprocessed;
            unprocessed.acquire();
            Date date = releaseTimes.remove();
            table.update(date);
        }
        if(currentUnprocessed>0 && onNotAllRan!=null)
            onNotAllRan.run();
        semaphore.release();
    }
    
    /**
     * States wheteher the job is already running.
     * @return True, if job is running on a track.
     */
    public boolean isRunning(){
        return semaphore.availablePermits()==0;
    }
    
    /**
     * Allows to set a callback to be used when not all chunks were processed.
     * @param callback callback to be used when not all chunks were processed.
     */
    public void setOnNotAllRan(Runnable callback){
        onNotAllRan = callback;
    }
    
    /**
     * Allows to get the estimated period the job is released with, in ms.
     * @return The period, in ms.
     */
    public int getPeriodInMs(){
        return periodInMs;
    }
    
    /**
     * Priority of the corresponding table.
     * @return Priority.
     */
    public int getPriority(){
        return table.getPriority();
    }
    
    /**
     * Releases the job.
     */
    public void release(){
        releaseTime = new Date();
        releaseTimes.add(releaseTime);
        unprocessed.release();
    }
    
    /**
     * First release time.
     * @return First release time.
     */
    public Date getReleaseTime(){
        return releaseTimes.peek();
    }
    
    /**
     * Checks wheteher the job was released.
     * @return True, if there are any unprocessed chunks.
     */
    public boolean wasReleased(){
        return unprocessed.availablePermits()>0;
    }
    
    /**
     * Freshness delta of corresponding table.
     * @return Freshness delta in ms.
     */
    public int getFreshnessDeltaInMs(){
        if(releaseTimes.isEmpty()) return 0;
        return (int)(releaseTime.getTime() - table.getLastUpdate().getTime());
    }
    
    /**
     * This method calculates estimated update time (for testing purposes only!)
     * @param chops the number of chops that will be considered.
     * @return Estimated running time.
     */
    private int getEstimateChopped(int chops){
        return (int)(alpha + beta * chops * periodInMs);
    }
    
    /**
     * Calculates estimated update time for current freshness delta (does not
     * take into account chops limit!).
     * @param freshnessDeltaInMs Freshness delta for update.
     * @return Total time necessary to initialize ETL process and insert rows.
     */
    private int getEstimate(int freshnessDeltaInMs){
        return (int)(alpha + beta * freshnessDeltaInMs);
    }
    
    /**
     * Calculated current estime of update time (does not take into account
     * chops limit!).
     * @return Time necessary to complete all updates.
     */
    public int getEstimate(){
        return (int)(alpha + beta * getFreshnessDeltaInMs());
    }
    
    /**
     * Calculates benefit of running current job.
     * @return Benefit of running current job.
     */
    public int getBenefit(){
        int freshnessDeltaInMs = getFreshnessDeltaInMs();
        return (table.getPriority() * freshnessDeltaInMs);
    }
    
    /**
     * Calculates marginal benefit of running current job.
     * @return Marginal benefit of running current job.
     */
    public double getMarginalBenefit(){
        int freshnessDeltaInMs = getFreshnessDeltaInMs();
        return (table.getPriority() * freshnessDeltaInMs)
                / getEstimate(freshnessDeltaInMs);
    }
    
    /**
     * Estimates next release time basing on last and period.
     * @return Estimated next release time.
     */
    public Date getNextExpectedReleaseTime(){
        return new Date(releaseTime.getTime() + periodInMs);
    }
    
    /**
     * Calculates estimate of processing time per data incoming in one period.
     * @return Time necessary to process update in one period.
     */
    public int getPeriodicalEstimate(){
        return getEstimate(getPeriodInMs());
    }
    
    /**
     * Calculates utilization of the job.
     * @return Utilization the job will introduce to the track it will be placed
     * on.
     */
    public double getUtilization(){
        int period = getPeriodInMs();
        return ((double) getEstimate(period)) / (double)period;
    }
    
    /**
     * Lower limit of tracks.
     * @return Lower limit of tracks.
     */
    public int getTrackLo(){
        return trackLo;
    }
    
    /**
     * Sets lower limit of tracks.
     * @param value New value.
     */
    public void setTrackLo(int value){
        trackLo = value;
    }
    
    /**
     * Upper limit of tracks.
     * @return Upper limit of tracks.
     */
    public int getTrackHi(){
        return trackHi;
    }
    
    /**
     * Sets upper limit of tracks.
     * @param value New value.
     */
    public void setTrackHi(int value){
        trackHi = value;
    }
    
    /**
     * Single track assigned.
     * @return Single track assigned.
     */
    public int getTrack(){
        if(trackLo!=trackHi)
            throw new IllegalStateException("Tried to access single track "
                    + "number while different are used.");
        return trackLo;
    }
    
    /**
     * Single track assignment.
     * @param value New value.
     */
    public void setTrack(int value){
        trackLo = trackHi = value;
    }
    
    /**
     * Group to which the job is assigned.
     * @return Group to which the job is assigned.
     */
    public int getGroup(){
        return group;
    }
    
    /**
     * Sets group to which the job is assigned.
     * @param value New value.
     */
    public void setGroup(int value){
        group = value;
    }
}
