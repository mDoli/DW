/*
 *  Loggable
 * 
 *  Version 1.0 - 12.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.helpers;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * This class is responsible for providing logging mechanisms.
 * @author Grzegorz Mrukwa
 */
public class Loggable {
    
    /**
     * Function logging events.
     */
    protected Consumer<String> logger;
    /**
     * Allows to lock the default access to console.
     */
    private static Semaphore semaphore;
    
    /**
     * Creates Loggable instance with default console logging.
     */
    protected Loggable(){
        if(semaphore==null)
            semaphore = new Semaphore(1);
        logger = s -> {
            try{
                semaphore.acquire();
                System.out.println(s);
                semaphore.release();
            } catch (InterruptedException ex) {
                // thread ended.
            }
        };
    }
    
    /**
     * Allows to set logger used to log events.
     * @param logger Function logging events.
     */
    public void setLogger(Consumer<String> logger){
        if(logger!=null)
            this.logger = s -> {
                try{
                    semaphore.acquire();
                    logger.accept(s);
                    semaphore.release();
                } catch (InterruptedException ex) {
                    // thread ended.
                };
            };
        else
            this.logger = s -> {};
    }
    
}
