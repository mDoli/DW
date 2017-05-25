/*
 *  PrioritizedEDF
 * 
 *  Version 1.0 - 03.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.basic;

import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;

/**
 * This class uses Prioritized EDF algorithm to queue the jobs on single track.
 * @author Grzegorz Mrukwa
 */
public class PrioritizedEDF extends BasicScheduler {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Job job1, Job job2) {
        if(job1.isRunning() && !job2.isRunning())
            return -1;
        if(!job1.isRunning() && job2.isRunning())
            return 1;
        if(job1.getPriority() < job2.getPriority())
            return -1;
        if(job1.getPriority() == job2.getPriority())
            return 0;
        return 1;
    }
    
}
