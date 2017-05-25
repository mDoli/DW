package generator.commons;

import java.util.Random;

/**
 * Generator randomowych wartości double.
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class RandomDouble {
    /**
     * Generuje randomową wartość typu double z podanego przedziału.
     * 
     * @param rangeMin Początek przedziału.
     * @param rangeMax Koniec przedziału.
     * @return Wartość typu double z przedziału.
     */
    public static double generateRandomDouble(double rangeMin, double rangeMax) {
        Random r = new Random();        
        double value = rangeMin + (rangeMax - rangeMin) * r.nextDouble();        
        return value;
    }
}
