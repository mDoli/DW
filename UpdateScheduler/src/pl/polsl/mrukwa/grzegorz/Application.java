/*
 * Application
 * 
 * Version 1.0 - 02.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz;

import static pl.polsl.mrukwa.grzegorz.Resources.getBasicSchedulerFactories;
import static pl.polsl.mrukwa.grzegorz.Resources.getGlobalSchedulerFactories;
import pl.polsl.mrukwa.grzegorz.scenarios.CustomTest;
import pl.polsl.mrukwa.grzegorz.scenarios.FiveJobsGlobalEdfLocalEdf;
import pl.polsl.mrukwa.grzegorz.scenarios.FiveJobsGlobalPropLocalEdf;
import pl.polsl.mrukwa.grzegorz.scenarios.TestScenario;

/**
 * This is the entry point for application.
 * @author Grzegorz Mrukwa
 */
public class Application {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        TestScenario scenario = new FiveJobsGlobalEdfLocalEdf();
//        TestScenario scenario = new FiveJobsGlobalPropLocalEdf();
//        System.out.println(scenario.getName());
//        System.out.println(scenario.getDescription());
//        scenario.run();
        TestScenario scenario;
        int chopsLimit = 10;
        int durationInMs = 1000;
        int jobsNumber = 10;
        for(String globalSchedulerName : getGlobalSchedulerFactories().keySet())
            for(String localSchedulerName:
                    getBasicSchedulerFactories().keySet()){
                scenario = new CustomTest(globalSchedulerName,
                        localSchedulerName,
                        chopsLimit,
                        durationInMs,
                        jobsNumber);
                System.out.println(scenario.getName());
                System.out.println(scenario.getDescription());
                scenario.run();
            }
    }
    
}
