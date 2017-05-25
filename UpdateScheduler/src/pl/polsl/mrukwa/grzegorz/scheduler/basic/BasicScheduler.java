/*
 * BasicScheduler
 * 
 * Version 1.0 - 10.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.basic;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import pl.polsl.mrukwa.grzegorz.helpers.Loggable;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;

/**
 * This is the base class for local schedulers.
 * @author Grzegorz Mrukwa
 */
public abstract class BasicScheduler
        extends Loggable
        implements Comparator<Job> {
    protected Queue<Job> jobs;
    
    /**
     * Protects jobs queue from concurrent access.
     */
    protected Semaphore jobsSemaphore;
    
    /**
     * Allows waiting for an element.
     */
    protected Semaphore sizeSemaphore;
    
    protected BasicScheduler(){
        jobs = new PriorityQueue<>(this);
        jobsSemaphore = new Semaphore(1);
        sizeSemaphore = new Semaphore(0);
    }
    
    /**
     * Enqueues job on a group scheduler.
     * @param job Update job.
     * @throws java.lang.InterruptedException when interrupting a thread.
     */
    public void add(Job job) throws InterruptedException{
        jobsSemaphore.acquire();
        //HACK: instead of decrease key
//        if(jobs.contains(job))
//            jobs.remove(job);
        jobs.add(job);
        jobsSemaphore.release();
        sizeSemaphore.release();
    }
    
    /**
     * Retrieves and removes the first job to run.
     * @return First job to run.
     * @throws java.lang.InterruptedException when interrupting a thread.
     */
    public Job pop() throws InterruptedException{
        try{
            sizeSemaphore.acquire();
        } catch (InterruptedException ex) {
            throw new InterruptedException(ex.getMessage());
        }
        jobsSemaphore.acquire();
        Job job = nonemptyPoll();
        jobs.remove(job);
        jobsSemaphore.release();
        return job;
    }
    
    /**
     * Retrieves but not removes the first job to run.
     * @return First job to run.
     * @throws java.lang.InterruptedException when interrupting a thread.
     */
    public Job poll() throws InterruptedException{
        sizeSemaphore.acquire();
        jobsSemaphore.acquire();
        Job job = nonemptyPoll();
        jobsSemaphore.release();
        sizeSemaphore.release();
        return job;
    }
    
    /**
     * Retrieves but not removes the first job to run, assuming that there is
     * released job in the queue.
     * @return First job to run.
     */
    protected Job nonemptyPoll(){
        return jobs.element();
    }
    
    /**
     * States whether the scheduler queue contains any released jobs.
     * @return True when there are unprocessed released jobs. False otherwise.
     */
    public boolean isEmpty(){
        return sizeSemaphore.availablePermits()==0;
    }
    
    /**
     * Function comparing two jobs for prioritization of the queue.
     * @param job1 First job to compare.
     * @param job2 Second job to compare.
     * @return -1,0,1 if job1 is less than, equal, higher than job2.
     */
    @Override
    public abstract int compare(Job job1, Job job2);
}
