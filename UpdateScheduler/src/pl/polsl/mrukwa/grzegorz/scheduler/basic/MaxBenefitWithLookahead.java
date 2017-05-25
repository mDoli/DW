/*
 *  MaxBenefitWithLookahead
 * 
 *  Version 1.0 - 03.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.basic;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import pl.polsl.mrukwa.grzegorz.helpers.CollectionExtensions;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;

/**
 * This class queue jobs using Max Benefit with lookahead algorithm on single
 * track.
 * @author Grzegorz Mrukwa
 */
public class MaxBenefitWithLookahead extends MaxBenefit {

    /**
     * {@inheritDoc}
     */
    @Override
    public Job pop() throws InterruptedException{
        try{
            sizeSemaphore.acquire();
        } catch (InterruptedException ex) {
            throw new InterruptedException(ex.getMessage());
        }
        jobsSemaphore.acquire();
        Job job = nonemptyPoll();
        jobsSemaphore.release();
        return job;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Job nonemptyPoll(){
        // We take job with highest marginal benefit of all released jobs.
        Job best = super.nonemptyPoll();
        Date bestJobEnd = new Date(best.getReleaseTime().getTime()
                + best.getEstimate());
        
        Comparator<Job> comparator =
                (Job job1,Job job2) -> this.compare(job1,job2);
        // We take all jobs released before execution of found best update
        // finishes which have higher marginal benefit and all released jobs.
        Queue<Job> jobsReleasedBeforeBestEnds = new LinkedList<>();
        jobs.stream()
                .filter((Jk) -> 
                        (Jk.getMarginalBenefit() > best.getMarginalBenefit()
                        && Jk.getNextExpectedReleaseTime().before(bestJobEnd)))
                .forEach((Jk) -> jobsReleasedBeforeBestEnds.add(Jk));
        Queue<Job> releasedJobs = new LinkedList<>();
        jobs.stream()
                .filter((job) -> (job.wasReleased()))
                .forEach((job) -> releasedJobs.add(job));
        
        // We scan through all subsets of released jobs for every job released
        // before the best ends.
        double bestB = Double.NEGATIVE_INFINITY;
        Job bestRepresentant = null;
        for(Job Jk : jobsReleasedBeforeBestEnds){
            Predicate<Collection<Job>> endsFasterThanJk = col -> {
                int estimate = 0;
                for(Job job: col)
                    estimate += job.getEstimate();
                return estimate < Jk.getEstimate();
            };
            Collection<Collection<Job>> subsetsOfEndingFaster = null;
            try {
                subsetsOfEndingFaster = CollectionExtensions
                        .getAllSubsets(releasedJobs, endsFasterThanJk);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MaxBenefitWithLookahead.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
            Stream<Collection<Job>> feasibleSubsets = subsetsOfEndingFaster
                    .stream()
                    .filter(col -> {
                        int estimate = 0;
                        for(Job job: col)
                            estimate += job.getEstimate();
                        return estimate + (new Date()).getTime()
                                >= Jk.getNextExpectedReleaseTime().getTime();
                    });
            // Scanning through all feasible subsets.
            for(Iterator<Collection<Job>> subset=feasibleSubsets.iterator();
                    subset.hasNext();){
                Queue<Job> S = new PriorityQueue<>(comparator);
                S.addAll(subset.next());
                int benefit = 0, estimate = 0;
                for(Job j : S){
                    benefit += j.getBenefit();
                    estimate += j.getEstimate();
                }
                double B = (benefit + Jk.getBenefit())
                        / (double)(estimate + Jk.getEstimate());
                if(B>bestB){
                    bestB = B;
                    bestRepresentant = S.peek();
                }
            }
        }
        if(bestB>best.getMarginalBenefit())
            return bestRepresentant;
        else
            return best;
    }
    
}
