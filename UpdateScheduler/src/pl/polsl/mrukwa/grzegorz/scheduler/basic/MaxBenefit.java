/*
 *  MaxBenefit
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
 * This class uses Max Benefit algorithm to queue the jobs on single track.
 * @author Grzegorz Mrukwa
 */
public class MaxBenefit extends BasicScheduler {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Job job1, Job job2) {
        if(job1.wasReleased() && !job2.wasReleased())
            return 1;
        if(!job1.wasReleased() && job2.wasReleased())
            return -1;
        if(job1.isRunning() && !job2.isRunning())
            return -1;
        if(!job1.isRunning() && job2.isRunning())
            return 1;
        double benefit1 = job1.getMarginalBenefit();
        double benefit2 = job2.getMarginalBenefit();
        if(benefit1 < benefit2)
            return -1;
        if(benefit2 < benefit1)
            return 1;
        return 0;
    }
    
}
