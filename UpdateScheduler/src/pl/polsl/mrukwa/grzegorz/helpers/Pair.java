/*
 *  Pair
 * 
 *  Version 1.0 - 10.05.2016
 * 
 *  Author: Grzegorz Mrukwa
 * 
 *  COPYRIGHT (C) 2016 Grzegorz Mrukwa. All Rights Reserved.
 */
package pl.polsl.mrukwa.grzegorz.helpers;

/**
 * Represents generic pair.
 * @author Grzegorz Mrukwa
 */
public class Pair<L,R> {

    /**
     * First object of a pair.
     */
    private final L left;
    
    /**
     * Second object of a pair.
     */
    private final R right;

    /**
     * Creates a pair of 'left' and 'right' objects.
     * @param left First object of a pair.
     * @param right Second object of a pair.
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * The value of the first item.
     * @return the value of the first item.
     */
    public L getLeft() { return left; }
    
    /**
     * The value of the second item.
     * @return the value of the second item.
     */
    public R getRight() { return right; }

    /**
     * Simplifies entering those to hashmaps.
     * @return 
     */
    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    /**
     * Allows comparisons.
     * @param o object to compare to.
     * @return true when both are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
               this.right.equals(pairo.getRight());
    }

}
