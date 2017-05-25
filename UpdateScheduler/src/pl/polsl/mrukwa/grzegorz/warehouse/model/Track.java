/*
 * Track
 * 
 * Version 1.0 - 10.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.warehouse.model;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import pl.polsl.mrukwa.grzegorz.helpers.Loggable;

/**
 * This class is the represents the fraction of warehouse resources that
 * executes jobs in sequential.
 * @author Grzegorz Mrukwa
 */
public class Track extends Loggable {
    
    /**
     * Prevents to access the track during its work.
     */
    private final Semaphore workingAccessSemaphore;
    
    /**
     * Thread to process all jobs.
     */
    private Thread workingThread;
    
    /**
     * Simple FIFO queue with blocking to take jobs from.
     */
    private final LinkedBlockingDeque<Job> queuedJobs;
    
    /**
     * Limit of chops processed at once.
     */
    private final int chopsLimit;
    
    /**
     * Execution time of longest job assigned to the track.
     */
    private int longestExecutionTime;
    
    /**
     * Period of the most frequent job assigned to the track.
     */
    private int shortestPeriod;
    
    /**
     * Sum of utilizations of all assigned jobs.
     */
    private double utilization;
    
    /**
     * Default constructor specifying chops limit.
     * @param chopsLimit Limit of chops to be processed at once.
     */
    public Track(int chopsLimit){
        this.chopsLimit = chopsLimit;
        queuedJobs = new LinkedBlockingDeque<>();
        workingAccessSemaphore = new Semaphore(1);
        workingThread = new Thread(){
            public void run(){
                runJobs();
            }
        };
        workingThread.start();
    }
    
    /**
     * {@inheritDoc}
     * @throws Throwable 
     */
    @Override
    public void finalize() throws Throwable {
        if(workingThread!=null)
            close();
        super.finalize();
    }
    
    /**
     * Shuts down the track.
     * @throws InterruptedException when the thread was interrupted.
     */
    public void close() throws InterruptedException{
        workingThread.interrupt();
        workingThread.join();
        workingThread = null;
    }
    
    /**
     * Assigns a job to the track.
     * @param job Job to be assigned.
     */
    public void assign(Job job){
        queuedJobs.add(job);
    }
    
    /**
     * States wheteher the track is busy or not.
     * @return True, if track is processing a job currently.
     */
    public boolean isWorking() {
        return workingAccessSemaphore.availablePermits()==0;
    }
    
    /**
     * States whether there are any jobs in the FIFO queue of the track.
     * @return True, if there are no jobs in the FIFO queue of the tracks.
     */
    public boolean isQueueEmpty(){
        return queuedJobs.isEmpty();
    }
    
    /**
     * Waits for jobs to appear and processes them.
     */
    private void runJobs(){
        try{
            while(true){
                Job job = queuedJobs.take();
                workingAccessSemaphore.acquire();
                job.run(this.chopsLimit);
                workingAccessSemaphore.release();
            }
        } catch(InterruptedException ex){
            // Thread was interrupted. End of thread.
            logger.accept("Interrupted track process execution.");
        }
    }

    /**
     * The longest execution time of an assigned job.
     * @return The longest execution time of an assigned job.
     */
    public int getLongestExecutionTime(){
        return longestExecutionTime;
    }
    
    /**
     * Sets the longest execution time of an assigned job.
     * @param value New value.
     */
    public void setLongestExecutionTime(int value){
        longestExecutionTime = value;
    }
    
    /**
     * The shortest period of an assigned job.
     * @return The shortest period of an assigned job.
     */
    public int getShortedtPeriod(){
        return shortestPeriod;
    }
    
    /**
     * Sets the shortest period of an assigned job.
     * @param value New value.
     */
    public void setShortestPeriod(int value){
        shortestPeriod = value;
    }
    
    /**
     * The utilization of a track.
     * @return The utilization of a track.
     */
    public double getUtilization(){
        return utilization;
    }
    
    /**
     * Sets the utilization of a track.
     * @param value New value.
     */
    public void setUtilization(double value){
        utilization = value;
    }
}
