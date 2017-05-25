/*
 * GlobalScheduler
 * 
 * Version 1.0 - 10.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import pl.polsl.mrukwa.grzegorz.helpers.Loggable;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.BasicScheduler;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Track;

/**
 * This is the base class for global scheduler.
 * @author Grzegorz Mrukwa
 */
public abstract class GlobalScheduler extends Loggable {
    
    /**
     * Holds all of the tracks used to run the jobs.
     */
    protected List<Track> tracks;
    /**
     * Holds all of the local schedulers for every group of jobs.
     */
    protected List<BasicScheduler> basicSchedulers;
    /**
     * The factory used to create new tracks.
     */
    protected final Callable<Track> trackFactory;
    /**
     * Factory used to create local schedulers for any new group of jobs.
     */
    protected final Callable<BasicScheduler> basicSchedulerFactory;
    /**
     * States whether the scheduler has been closed yet.
     */
    private boolean finalized = false;
    /**
     * The number of free tracks created. Free tracks are created only if the
     * number of tracks necessary for proper assignment of jobs is lower than
     * the limit specified in the resources.
     */
    protected int freeTracksNumber;
    /**
     * The thread which runs redirections from local schedulers to tracks.
     */
    private Thread runningThread;
    /**
     * The semaphore which locks the basic schedulers queue to be not iterated
     * during insertions.
     */
    private final Semaphore basicSchedulersSemaphore;
    /**
     * The semaphore which allows to end the redirecting thread.
     */
    private final Semaphore running;
    
    /**
     * Default constructor creating scheduler running custom algorithm.
     * @param factory Function creating basic schedulers.
     * @param chopsLimit Limit of chops to be processed at a time.
     */
    protected GlobalScheduler(Callable<BasicScheduler> factory, int chopsLimit){
        trackFactory = () -> new Track(chopsLimit);
        basicSchedulerFactory = factory;
        tracks = new ArrayList<>();
        basicSchedulers = new ArrayList<>();
        freeTracksNumber = 0;
        basicSchedulersSemaphore = new Semaphore(0);
        //HACK: allows to exit the loop in the redirections.
        running = new Semaphore(1);
        runningThread = new Thread(){
            public void run(){
                runRedirections();
            }
        };
        runningThread.start();
    }
    
    /**
     * Assigns all jobs to tracks in order to schedule them in clusters.
     * @param jobs Jobs to be assigned.
     */
    protected abstract void assignToTracksInternal(Collection<Job> jobs);
    
    /**
     * Assigns all jobs to tracks in order to schedule them in clusters and
     * signals to begin redirections of queued jobs into tracks.
     * @param jobs Jobs to be assigned.
     */
    public void assignToTracks(Collection<Job> jobs){
        assignToTracksInternal(jobs);
        basicSchedulersSemaphore.release();
    }
    
    /**
     * Is responsible of placing a job to be done by the track.
     * @param job Job to be performed.
     * @return True if the job was successfully enqueued.
     */
    public abstract boolean enqueueOnTrack(Job job);
    
    /**
     * Is being used to schedule incoming job in its group queue.
     * @param job Job to be scheduled.
     * @throws java.lang.InterruptedException when the application is stopped.
     */
    public void schedule(Job job) throws InterruptedException {
        int group = job.getGroup();
        if(group == -1){
            throw new UnsupportedOperationException("Tried to schedule a job "
                    + "which was not assigned to any group.");
        }
        BasicScheduler scheduler = basicSchedulers.get(group);
        scheduler.add(job);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void finalize() throws InterruptedException, Throwable{
        if(!finalized)
            close();
        super.finalize();
    }
    
    /**
     * This function ends the execution of the scheduler.
     * @throws InterruptedException 
     */
    public void close() throws InterruptedException{
        if(finalized) return;
        running.drainPermits();
        runningThread.interrupt();
        runningThread.join();
        runningThread = null;
        for(Track t: tracks)
            t.close();
        tracks.clear();
        finalized = true;
    }
    
    /**
     * This function is responsible for finding a track which is not working
     * from the free tracks.
     * @return The number of available free track when one exists or -1.
     */
    protected int getLazyFreeTrack(){
        for(int i = 0; i<freeTracksNumber; ++i){
            Track freeTrack = tracks.get(tracks.size() - 1 - i);
            if(!freeTrack.isWorking())
                return tracks.size() - 1 - i;
        }
        return -1;
    }
    
    /**
     * This function is responsible for redirection of any job enqueued in basic
     * schedulers to tracks.
     */
    private void runRedirections(){
        try{
            basicSchedulersSemaphore.acquire();
        } catch(InterruptedException ex) {
            logger.accept("Global scheduler run completed.");
            return;
        }
        logger.accept(String.format("Using %d local schedulers.",
                basicSchedulers.size()));
        //This starts new threads for redirections.
        List<Thread> threads = new LinkedList<>();
        for(BasicScheduler scheduler : basicSchedulers){
            Thread thread = new Thread(){
                public void run(){
                    while(running.availablePermits()>0){
                        try{
                            Job job = scheduler.pop();
                            if(job.isRunning())
                                continue;
                            job.setOnNotAllRan(() -> {
                                try{
                                    scheduler.add(job);
                                } catch (InterruptedException ex) {
                                    running.drainPermits();
                                }
                            });
                            if(!enqueueOnTrack(job))
                                scheduler.add(job);
                        } catch (InterruptedException ex) {
                            running.drainPermits();
                        }
                    }
                }
            };
            thread.start();
            threads.add(thread);
        }
        //This waits for interrupt in order to close all redirecting threads.
        try{
            while(true){
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch(InterruptedException ex)
        {
            for(Thread thread : threads){
                thread.interrupt();
                try{
                    thread.join();
                } catch (InterruptedException exInternal) {
                    throw new UnsupportedOperationException("Program performed"
                            + " operation which was not supported: "
                            + exInternal.getMessage());
                }
            }
        }        
        logger.accept("Global scheduler run completed.");
    }
    
    /**
     * Allows to create basic scheduler in the factory function without an
     * exception.
     * @return Created BasicScheduler.
     */
    protected BasicScheduler createBasicScheduler(){
        try{
            return basicSchedulerFactory.call();
        } catch(Exception ex) {
            throw new UnsupportedOperationException("Scheduler creation failed"
                    + ": " + ex.getMessage());
        }
    }
    
    /**
     * Allows to create track in the factory function without an exception.
     * @return Created track.
     */
    protected Track createTrack(){
        try{
            return trackFactory.call();
        } catch(Exception ex) {
            throw new UnsupportedOperationException("Scheduler creation failed"
                    + ": " + ex.getMessage());
        }
    }
}
