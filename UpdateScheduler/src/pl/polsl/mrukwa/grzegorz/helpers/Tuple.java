/*
 *  Tuple
 * 
 *  Version 1.0 - 10.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.helpers;

/**
 * This represents some variables as one.
 * @author Grzegorz Mrukwa
 */
public class Tuple<T,U,V> {
    
    /**
     * Internal structure of Tuple.
     */
    private Pair<T,Pair<U,V>> p;
    
    /**
     * Allows to construct Tuple with specified values.
     * @param t first value.
     * @param u second value.
     * @param v third value.
     */
    public Tuple(T t, U u, V v){
        p = new Pair(t,new Pair(u,v));
    }
    
    /**
     * Get first value.
     * @return First value.
     */
    public T get1(){
        return p.getLeft();
    }
    
    /**
     * Get second value.
     * @return Second value.
     */
    public U get2(){
        return p.getRight().getLeft();
    }
    
    /**
     * Get third value.
     * @return Third value.
     */
    public V get3(){
        return p.getRight().getRight();
    }
}
