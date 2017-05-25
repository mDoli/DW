/*
 * Table
 * 
 * Version 1.0 - 10.04.2016
 * 
 * Author: Grzegorz Mrukwa
 * 
 * COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.warehouse.model;

import java.util.Date;

/**
 * This represents abstraction of data table.
 * @author Grzegorz Mrukwa
 */
public class Table {
    
    /**
     * Table priority.
     */
    private final int priority;
    
    /**
     * Date and time of last update.
     */
    private Date lastUpdate;
    
    /**
     * Default constructor specifying table priority.
     * @param priority Priority of the table.
     */
    public Table(int priority){
        this.priority = priority;
        lastUpdate = new Date();
    }
    
    /**
     * Table priority.
     * @return Table priority.
     */
    public int getPriority(){
        return priority;
    }
    
    /**
     * Date and time of last update.
     * @return Date and time of last update.
     */
    public Date getLastUpdate(){
        return lastUpdate;
    }
    
    /**
     * Updates table to specified date.
     * @param date Date to update table to.
     */
    public void update(Date date){
        lastUpdate = date;
    }
    
    /**
     * Calculates staleness of the table at current time.
     * @return Staleness in ms.
     */
    public long getStaleness(){
        return (new Date()).getTime() - lastUpdate.getTime();
    }
}
