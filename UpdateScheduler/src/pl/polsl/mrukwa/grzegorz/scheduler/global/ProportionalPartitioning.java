/*
 *  ProportionalPartitioning
 * 
 *  Version 1.0 - 10.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scheduler.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import static pl.polsl.mrukwa.grzegorz.Resources.getCores;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.BasicScheduler;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Track;

/**
 * This class performs global scheduling of jobs by proportional partitioning.
 * @author Grzegorz Mrukwa
 */
public class ProportionalPartitioning extends GlobalScheduler {
    
    /**
     * Coefficient of similarity used during job clusterization.
     */
    private final double similarityRate;
    
    /**
     * Default constructor creating scheduler running Proportional Partitioning
     * algorithm.
     * @param factory Function creating basic schedulers.
     * @param chopsLimit Limit of chops to be processed at a time.
     */
    public ProportionalPartitioning(
            Callable<BasicScheduler> factory, int chopsLimit){
        super(factory,chopsLimit);
        similarityRate = 10;        
    }
    
    /**
     * Constructor creating scheduler running Proportional Partitioning
     * algorithm which allows to specify coefficient of similarity.
     * @param factory Function creating basic schedulers.
     * @param chopsLimit Limit of chops to be processed at a time.
     * @param similarityRate Coefficient of similarity.
     */
    public ProportionalPartitioning(Callable<BasicScheduler> factory,
            int chopsLimit, double similarityRate){
        super(factory,chopsLimit);
        this.similarityRate = similarityRate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void assignToTracksInternal(Collection<Job> jobs) {
        Job[] sorted = jobs.stream().sorted(
                (j1,j2) -> {
                    if(j1.getPeriodicalEstimate()<j2.getPeriodicalEstimate())
                        return -1;
                    if(j1.getPeriodicalEstimate()==j2.getPeriodicalEstimate())
                        return 0;
                    return 1;
                }).toArray(Job[]::new);
        //clusters assignment
        int currentGroup = 0;
        int minimalPeriodInGroup = Integer.MAX_VALUE / (int)(similarityRate+1);
        ArrayList<Double> utilization = new ArrayList<>();
        double totalUtilization = 0;
        double currentGroupUtilization = 0;
        for(Job job: sorted){
            if(job.getPeriodicalEstimate() 
                    < similarityRate * minimalPeriodInGroup){
                job.setGroup(currentGroup);
                double jobUtilization = (double)job.getPeriodicalEstimate()
                        / (double)job.getPeriodInMs();
                currentGroupUtilization += jobUtilization;
                totalUtilization += jobUtilization;
                if(minimalPeriodInGroup > job.getPeriodInMs())
                    minimalPeriodInGroup = job.getPeriodInMs();
            } else {
                job.setGroup(++currentGroup);
                utilization.add(currentGroupUtilization);
                currentGroupUtilization = (double)job.getPeriodicalEstimate()
                        / (double)job.getPeriodInMs();
                minimalPeriodInGroup = job.getPeriodInMs();
                basicSchedulers.add(createBasicScheduler());
            }
        }
        utilization.add(currentGroupUtilization);
        basicSchedulers.add(createBasicScheduler());
        //resources assignment
        //this way of adjustment factor selection allows us to stick to the
        //number of tracks proportional to the number of cores or at least it
        //should.
        double adjustmentFactor = (getCores()-1)/totalUtilization;
        int groupsNumber = currentGroup+1;
        int trackLo[] = new int[groupsNumber];
        int trackHi[] = new int[groupsNumber];
        trackLo[groupsNumber-1] = 0;
        trackHi[groupsNumber-1] = (int)(adjustmentFactor
                * utilization.get(groupsNumber-1));
        int maxTrackNum = trackHi[groupsNumber-1];
        for(int i = groupsNumber-2; i>=0; --i){
            trackLo[i] = trackHi[i+1];
            trackHi[i] = trackHi[i+1]
                    + (int)(adjustmentFactor * utilization.get(i));
            if(trackHi[i]>maxTrackNum)
                maxTrackNum = trackHi[i];
        }
        for(Job job : sorted){
            job.setTrackLo(trackLo[job.getGroup()]);
            job.setTrackHi(trackHi[job.getGroup()]);
        }
        for(int i=0; i<getCores(); ++i)
            tracks.add(createTrack());
        freeTracksNumber = getCores() - 1 - maxTrackNum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enqueueOnTrack(Job job) {
        int j = job.getGroup();
        for(int i=job.getTrackLo()+1; i<=job.getTrackHi(); ++i){
            Track track = tracks.get(i);
            if(track.isWorking())
                continue;
            track.assign(job);
            return true;
        }
        if(!tracks.get(job.getTrackLo()).isWorking()){
            tracks.get(job.getTrackLo()).assign(job);
            return true;
        }
        for(int i=tracks.size()-1; i>=tracks.size()-freeTracksNumber; --i){
            Track track = tracks.get(i);
            if(track.isWorking())
                continue;
            track.assign(job);
            return true;
        }
        for(int i=0; i<job.getTrackLo(); ++i){
            Track track = tracks.get(i);
            BasicScheduler scheduler = basicSchedulers.get(i);
            if(!scheduler.isEmpty() || track.isWorking())
                continue;
            track.assign(job);
            return true;
        }
        return false;
    }
    
}
