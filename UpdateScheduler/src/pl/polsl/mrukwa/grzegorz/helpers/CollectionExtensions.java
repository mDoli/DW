/*
 *  CollectionExtensions
 * 
 *  Version 1.0 - 08.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.helpers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * This class provides some extensions to the Collection class.
 * @author Grzegorz Mrukwa
 */
public final class CollectionExtensions {
    
    /**
     * Produces all subsets of a set.
     * @param <E> Type of collection element.
     * @param col collection which subsets are generated.
     * @param filter predicate stating whether all other subsets which will
     * contain current subset may be feasible.
     * @return All feasible subsets.
     * @throws InstantiationException when used collection that cannot be
     * instantinated.
     * @throws IllegalAccessException if the collection constructor was hidden.
     */
    public static <E> Collection<Collection<E>> getAllSubsets(Collection<E> col,
            Predicate<Collection<E>> filter)
            throws InstantiationException, IllegalAccessException{
        Collection<Collection<E>> result = new LinkedList<>();
        for(E item: col){
            for(Collection<E> subset : result){
                Collection<E> copy = col.getClass().newInstance();
                copy.addAll(subset);
                copy.add(item);
                if(filter.test(copy))
                    result.add(copy);
            }
            Collection<E> singleSubset = col.getClass().newInstance();
            singleSubset.add(item);
            if(filter.test(singleSubset))
                result.add(singleSubset);
        }
        return result;
    }
    
}
