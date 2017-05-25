/*
 *  Observer
 * 
 *  Version 1.0 - 12.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.helpers;

import java.util.function.Supplier;

/**
 * This class allows to observe some parameters during the program execution.
 * @author Grzegorz Mrukwa
 */
public class Observer extends Loggable implements Runnable {
    
    /**
     * Reports provider.
     */
    private final Supplier<String> supplier;
    
    /**
     * Period of logging.
     */
    private final int period;
    
    /**
     * States whether to stop all currently running observers.
     */
    private static boolean running = true;
    
    /**
     * Creates new Observer instance.
     * @param supplier Reports provider.
     * @param period Period of logging.
     */
    public Observer(Supplier<String> supplier, int period){
        this.supplier = supplier;
        this.period = period;
        running = true;
    }
    
    /**
     * Implementation of Runnable interface to use it on another threads.
     */
    @Override
    public void run(){
        try{
            while(running){
                logger.accept(supplier.get());
                Thread.sleep(period);
            }
        } catch(InterruptedException ex) {
            logger.accept("Observer instance closed.");
        }
    }
    
    /**
     * Stops all currently working instances.
     */
    public static void closeAll(){
        running = false;
    }
}
