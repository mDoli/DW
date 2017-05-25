/*
 *  CustomTest
 * 
 *  Version 1.0 - 14.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scenarios;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static pl.polsl.mrukwa.grzegorz.Resources.getBasicSchedulerFactories;
import static pl.polsl.mrukwa.grzegorz.Resources.getGlobalSchedulerFactories;
import pl.polsl.mrukwa.grzegorz.helpers.Observer;
import pl.polsl.mrukwa.grzegorz.helpers.Pair;
import pl.polsl.mrukwa.grzegorz.scheduler.basic.BasicScheduler;
import pl.polsl.mrukwa.grzegorz.scheduler.global.GlobalScheduler;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Io;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Table;

/**
 * Allows to parametrize the test scenario.
 * @author Grzegorz Mrukwa
 */
public class CustomTest extends TestScenario {
    
    /**
     * The name of used global scheduler.
     */
    private final String globalSchedulerName;
    
    /**
     * The name of used basic schedulers.
     */
    private final String localSchedulerName;
    
    /**
     * Limit of chops in the case of transient overhead.
     */
    private final int chopsLimit;
    
    /**
     * Duration of simulation.
     */
    private final int lengthInMs;
    
    /**
     * Reports period.
     */
    private final int observersPeriodInMs;
    
    /**
     * Way of printing the reports.
     */
    private final Consumer<String> print;
    
    /**
     * Number of jobs created for simulation.
     */
    private final int jobsNumber;
    
    /**
     * Creates custom test with specified parameters.
     * @param globalSchedulerName global scheduler.
     * @param localSchedulerName local scheduler.
     * @param chopsLimit chops limit for overhead periods.
     * @param lengthInMs duration of simulation.
     * @param jobsNumber number of jobs created in the simulation.
     */
    public CustomTest(String globalSchedulerName, String localSchedulerName,
            int chopsLimit, int lengthInMs, int jobsNumber){
        this.globalSchedulerName = globalSchedulerName;
        this.localSchedulerName = localSchedulerName;
        this.chopsLimit = chopsLimit;
        this.lengthInMs = lengthInMs;
        this.observersPeriodInMs = 100;
        this.print = s -> System.out.println(s);
        this.jobsNumber = jobsNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Global: " + globalSchedulerName + "\nLocal: "
                + localSchedulerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Performs test runs on specified schedulers.";
    }

    /**
     * Gets the global scheduler using the factory methods from the resources.
     * @return Global scheduler.
     */
    private GlobalScheduler getScheduler(){
        Function<Pair<Callable<BasicScheduler>,Integer>,GlobalScheduler>
                globalSchedulerFactory =
                        getGlobalSchedulerFactories().get(globalSchedulerName);
        Callable<BasicScheduler> localSchedulerFactory =
                getBasicSchedulerFactories().get(localSchedulerName);
        GlobalScheduler scheduler = globalSchedulerFactory.apply(
                new Pair(localSchedulerFactory, chopsLimit));
        return scheduler;
    }
    
    /**
     * Allows to run the simulation as Runnable on another thread.
     */
    @Override
    public void run() {
        GlobalScheduler scheduler = getScheduler();
        Io mock = new Io(scheduler);
        
        ArrayList<Table> tables = new ArrayList<>(jobsNumber);
        Thread observers_t[] = new Thread[jobsNumber];
        ArrayList<Integer> indexes = new ArrayList<>(jobsNumber);
        for(int i=0; i<jobsNumber; ++i){
            indexes.add(i);
            tables.add(new Table(i+1));
        }
        for(int i : indexes){
            Supplier<String> checkStaleness = () -> String.format(
                    "Staleness of table %d: %d",
                    i+1,tables.get(i).getStaleness());
            Observer observer = new Observer(checkStaleness,
                    observersPeriodInMs);
            Job job = new Job(10*(jobsNumber+1-i),tables.get(i));
            mock.append(job);
            observers_t[i] = new Thread(observer);
            observers_t[i].start();
        }
        
        Thread mock_t = new Thread(mock);
        mock_t.start();
        waitTillEnd(print);
        mock.stop();
        print.accept("Stopped mock.");
        try{
            mock_t.interrupt();
//            print.accept("Mock interrupted.");
            mock_t.join();
//            print.accept("Mock joined.");
        } catch(InterruptedException ex){
            print.accept("Mock join failed: " + ex.getMessage());
        }
        Observer.closeAll();
        try{
            for(Thread observer_t: observers_t){
                observer_t.interrupt();
//                print.accept("Observer interrupted.");
                observer_t.join();
//                print.accept("Observer joined.");
            }
        } catch(InterruptedException ex){
            print.accept("Observer join failed: " + ex.getMessage());
        }
        try{
            scheduler.close();
            print.accept("Closed scheduler.");
        } catch(InterruptedException ex) {
            print.accept("Ended execution: " + ex.getMessage());
        }
    }
    
    /**
     * Allows to wait until the simulation process ends.
     * @param print exception logger.
     */
    private void waitTillEnd(Consumer<String> print){
        try{
            Thread.sleep(lengthInMs);
        } catch(InterruptedException ex) {
            print.accept("InterruptedException: " + ex.getMessage());
        }
    }
}
