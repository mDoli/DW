/*
 *  TestScenario
 * 
 *  Version 1.0 - 14.06.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.scenarios;

/**
 * Provides common interface for all test scenarios used.
 * @author Grzegorz Mrukwa
 */
public abstract class TestScenario implements Runnable {
    
    /**
     * Gets name of this simulations.
     * @return 
     */
    public abstract String getName();
    
    /**
     * Gets description of this simulation.
     * @return 
     */
    public abstract String getDescription();
    
}
