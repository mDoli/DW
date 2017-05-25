/*
 *  FiveJobsGlobalPropLocalEdf
 * 
 *  Version 1.0 - 14.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scenarios;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static pl.polsl.mrukwa.grzegorz.Resources.getBasicSchedulerFactories;
import pl.polsl.mrukwa.grzegorz.helpers.Observer;
import pl.polsl.mrukwa.grzegorz.scheduler.global.GlobalScheduler;
import pl.polsl.mrukwa.grzegorz.scheduler.global.ProportionalPartitioning;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Io;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Job;
import pl.polsl.mrukwa.grzegorz.warehouse.model.Table;

/**
 * Allows to test everything with use of 5 jobs of different periods updating
 * tables of different priorities by Proportional Partitioning global scheduling
 * and Prioritized EDF local schedulers.
 * @author Grzegorz Mrukwa
 */
public class FiveJobsGlobalPropLocalEdf extends TestScenario {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "FiveJobsGlobalPropLocalEdf";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return " * Allows to test everything with use of 5 jobs of different"
                + " periods updating\n * tables of different priorities by"
                + " Proportional Partitioning global scheduling\n * and"
                + " Prioritized EDF local schedulers.";
    }

    /**
     * Allows to run the simulation as Runnable on another thread.
     */
    @Override
    public void run() {
        Consumer<String> print = s -> System.out.println(s);
        GlobalScheduler scheduler = new ProportionalPartitioning(
                getBasicSchedulerFactories().get("PrioritizedEDF"), 10);
        Io mock = new Io(scheduler);
        
        ArrayList<Table> tables = new ArrayList<>(5);
        Thread observers_t[] = new Thread[5];
        ArrayList<Integer> indexes = new ArrayList<>(5);
        for(int i=0; i<5; ++i){
            indexes.add(i);
            tables.add(new Table(i+1));
        }
        for(int i : indexes){
            Supplier<String> checkStaleness = () -> String.format(
                    "Staleness of table %d: %d",
                    i+1,tables.get(i).getStaleness());
            Observer observer = new Observer(checkStaleness,100);
            Job job = new Job(10*(6-i),tables.get(i));
            mock.append(job);
            observers_t[i] = new Thread(observer);
            observers_t[i].start();
        }
        
        Thread mock_t = new Thread(mock);
        mock_t.start();
        try{
//            System.in.read();
            Thread.sleep(5000);
//        } catch(IOException ex) {
//            print.accept("IOException.");
        } catch(InterruptedException ex) {
            print.accept("InterruptedException.");
        }
        mock.stop();
        print.accept("Stopped mock.");
        try{
            mock_t.interrupt();
            print.accept("Mock interrupted.");
            mock_t.join();
            print.accept("Mock joined.");
        } catch(InterruptedException ex){
            print.accept("Mock join failed.");
        }
        try{
            for(Thread observer_t: observers_t){
                observer_t.interrupt();
                print.accept("Observer interrupted.");
                observer_t.join();
                print.accept("Observer joined.");
            }
        } catch(InterruptedException ex){
            print.accept("Observer join failed.");
        }
        try{
            scheduler.close();
            print.accept("Closed scheduler.");
        } catch(InterruptedException ex) {
            print.accept("Ended execution.");
        }
    }
    
    
    
}
