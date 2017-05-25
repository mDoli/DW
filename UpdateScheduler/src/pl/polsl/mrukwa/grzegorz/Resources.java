/*
 *  Resources
 * 
 *  Version 1.0 - 10.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.function.Function;
import pl.polsl.mrukwa.grzegorz.helpers.Pair;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.BasicScheduler;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.MaxBenefit;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.MaxBenefitWithLookahead;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.PrioritizedEDF;
import pl.polsl.mrukwa.grzegorz.scheduler.global.EdfPartitioned;
import pl.polsl.mrukwa.grzegorz.scheduler.global.GlobalScheduler;
import pl.polsl.mrukwa.grzegorz.scheduler.global.ProportionalPartitioning;

/**
 * This class provides common resources for the remaining part of application.
 * @author Grzegorz Mrukwa
 */
public class Resources {
    
    /**
     * Holds the factories statically.
     */
    private static Map<String,Callable<BasicScheduler>>
            BASIC_SCHEDULER_FACTORIES;
    
    /**
     * Allows to get all of the implemented basic scheduler factories.
     * @return Local scheduler factory methods.
     */
    public static Map<
        String,
        Callable<
            BasicScheduler>>
        getBasicSchedulerFactories(){
        if(BASIC_SCHEDULER_FACTORIES==null){
            BASIC_SCHEDULER_FACTORIES = new TreeMap<>();
            BASIC_SCHEDULER_FACTORIES.put(
                    "MaxBenefit",
                    () -> new MaxBenefit());
            BASIC_SCHEDULER_FACTORIES.put(
                    "MaxBenefitWithLookahead",
                    () -> new MaxBenefitWithLookahead());
            BASIC_SCHEDULER_FACTORIES.put(
                    "PrioritizedEDF",
                    () -> new PrioritizedEDF());
        }
        return Collections.unmodifiableMap(BASIC_SCHEDULER_FACTORIES);
    }
    
    /**
     * Holds the factories statically.
     */
    private static Map<
            String,
            Function<
                Pair<
                    Callable<BasicScheduler>,
                    Integer>,
                GlobalScheduler>>
            GLOBAL_SCHEDULER_FACTORIES;
    
    /**
     * Allows to get all of the implemented global scheduling mechanisms
     * factories.
     * @return Global scheduler factory methods.
     */
    public static Map<
            String,
            Function<
                Pair<
                    Callable<BasicScheduler>,
                    Integer>,
                GlobalScheduler>>
        getGlobalSchedulerFactories(){
        if(GLOBAL_SCHEDULER_FACTORIES==null){
            GLOBAL_SCHEDULER_FACTORIES = new TreeMap<>();
            GLOBAL_SCHEDULER_FACTORIES.put(
                    "EDF-Partitioned",
                    pair -> new EdfPartitioned(
                            pair.getLeft(),pair.getRight()));
            GLOBAL_SCHEDULER_FACTORIES.put(
                    "ProportionalPartitioning",
                    pair -> new ProportionalPartitioning(
                            pair.getLeft(),pair.getRight()));
        }
        return Collections.unmodifiableMap(GLOBAL_SCHEDULER_FACTORIES);
    }
    
    /**
     * Retrieves the number of cores.
     * @return Number of processor cores.
     */
    public static int getCores(){
        return Runtime.getRuntime().availableProcessors();
    }
    
}
