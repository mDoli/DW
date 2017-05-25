/*
 *  Io
 * 
 *  Version 1.0 - 12.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.warehouse.model;

import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;
import pl.polsl.mrukwa.grzegorz.scheduler.global.GlobalScheduler;

/**
 * This class is responsible for mocking input and output for a warehouse.
 * @author Grzegorz Mrukwa
 */
public class Io implements Runnable, Comparator<Job>{
    
    /**
     * Responsible for stopping execution.
     */
    private final boolean[] run;
    
    /**
     * All jobs which are simulated.
     */
    private final PriorityBlockingQueue<Job> jobs;
    
    /**
     * Scheduler used to assign jobs to tracks.
     */
    private final GlobalScheduler scheduler;
    
    /**
     * Creates new mock of warehouse IO.
     * @param scheduler Global scheduler to be used with the simulation.
     */
    public Io(GlobalScheduler scheduler){
        run = new boolean[1];
        jobs = new PriorityBlockingQueue<>(11,this);
        this.scheduler = scheduler;
    }
    
    /**
     * Allows to add new job before running the simulation.
     * @param job Job to be simulated.
     */
    public void append(Job job){
        if(run[0])
            throw new UnsupportedOperationException("Cannot add new job while "
                    + "running.");
        jobs.add(job);
    }
    
    /**
     * Stops the execution of simulation.
     */
    public void stop(){
        run[0] = false;
    }
    
    /**
     * Runs the simulation itself.
     */
    @Override
    public void run(){
        scheduler.assignToTracks(jobs);
        run[0] = true;
        while(run[0]){
            try{
                Job job = jobs.take();
                long timeout = job.getNextExpectedReleaseTime().getTime() 
                        - (new Date()).getTime(); 
                if(timeout>0)
                    Thread.sleep(timeout);
                job.release();
                jobs.add(job);
                scheduler.schedule(job);
            } catch(InterruptedException ex) {
                // program ends.
            }
        }
    }
    
    /**
     * Allows to simulate jobs' releases in proper order.
     * @param job1 First job to compare.
     * @param job2 Second job to comapre.
     * @return -1,0,1 when job1 will be released earlier/at the same time/later
     * than job2
     */
    @Override
    public int compare(Job job1, Job job2){
        Date r1 = job1.getNextExpectedReleaseTime();
        Date r2 = job2.getNextExpectedReleaseTime();
        if(r1.after(r2)){
            return 1;
        } else if(r2.after(r1)) {
            return -1;
        }
        return 0;
    }
}
