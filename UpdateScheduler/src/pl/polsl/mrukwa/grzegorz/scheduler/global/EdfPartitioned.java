/*
 *  EdfPartitioned
 * 
 *  Version 1.0 - 10.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.global;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static pl.polsl.mrukwa.grzegorz.Resources.getCores;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.BasicScheduler;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Track;

/**
 * This class performs global scheduling of jobs by EDF Partitioned algorithm.
 * @author Grzegorz Mrukwa
 */
public class EdfPartitioned extends GlobalScheduler {

    /**
     * Default constructor creating scheduler running EDF partitioned algorithm.
     * @param factory Function creating basic schedulers.
     * @param chopsLimit Limit of chops to be processed at a time.
     */
    public EdfPartitioned(Callable<BasicScheduler> factory, int chopsLimit){
        super(factory,chopsLimit);
        
    }
    
    /**
     * Checks whether the job is schedulable on particular track.
     * @param track track to check condition for.
     * @param job job to check condition for.
     * @return True if job can be scheduled on the track without violating
     * schedulability condition.
     */
    private boolean isSchedulable(Track track, Job job){
        return track.getUtilization() + job.getUtilization() <= 1
                - ((double)track.getLongestExecutionTime())
                /((double)track.getShortedtPeriod());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void assignToTracksInternal(Collection<Job> jobs) {
        Stream<Job> sorted = jobs.stream().sorted(
                (j1,j2) -> {
                    if(j1.getPeriodInMs()<j2.getPeriodInMs())
                        return -1;
                    if(j1.getPeriodInMs()==j2.getPeriodInMs())
                        return 0;
                    return 1;
                });
        Consumer<Job> assignJobToAnyTrack = job -> {
            for(int i=0; i<tracks.size(); ++i){
                Track track = tracks.get(i);
                if(isSchedulable(track, job)){
                    job.setTrack(i);
                    job.setGroup(i);
                    if(job.getPeriodInMs() < track.getShortedtPeriod())
                        track.setShortestPeriod(job.getPeriodInMs());
                    if(job.getPeriodicalEstimate()
                            >track.getLongestExecutionTime())
                        track.setLongestExecutionTime(
                                job.getPeriodicalEstimate());
                    track.setUtilization(track.getUtilization()
                            + job.getUtilization());
                    return;
                }
            }
            job.setTrack(tracks.size());
            job.setGroup(tracks.size());
            Track track = createTrack();
            tracks.add(track);
            if(job.getPeriodInMs() < track.getShortedtPeriod())
                    track.setShortestPeriod(job.getPeriodInMs());
                if(job.getPeriodicalEstimate()
                        >track.getLongestExecutionTime())
                    track.setLongestExecutionTime(
                            job.getPeriodicalEstimate());
                track.setUtilization(track.getUtilization()
                        + job.getUtilization());
            basicSchedulers.add(createBasicScheduler());
        };
        sorted.forEachOrdered(assignJobToAnyTrack);
        while(tracks.size() < getCores()){
            ++freeTracksNumber;
            tracks.add(createTrack());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enqueueOnTrack(Job job) {
        while(!job.wasReleased())
//            Thread.yield(); // for debug - unreleased jobs should not be found
            return false;
        int trackNum = job.getTrack();
        Track homeTrack = tracks.get(trackNum);
        if(!homeTrack.isWorking()){
            homeTrack.assign(job);
            return true;
        }
        
        int lazyFreeTrackNumber = getLazyFreeTrack();
        if(lazyFreeTrackNumber != -1) {
            Track freeTrack = tracks.get(lazyFreeTrackNumber);
            freeTrack.assign(job);
            return true;
        }
        
        Optional<Track> promotableTrack = tracks.stream().filter(
                track -> !track.isWorking() && checkPromotability(track, job))
                .findAny();
        if(promotableTrack.isPresent()){
            promotableTrack.get().assign(job);
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks the promotability condition.
     * @param track track to which the job may be promoted.
     * @param job job which may be promoted.
     * @return true when job can be promoted to a track.
     */
    private boolean checkPromotability(Track track, Job job){
        double maxEstimate = Integer.max(track.getLongestExecutionTime(),
                job.getPeriodicalEstimate());
        double minPeriod = Integer.min(track.getShortedtPeriod(),
                job.getPeriodInMs());
        return track.getUtilization() <= 1-maxEstimate/minPeriod;
    }
}
