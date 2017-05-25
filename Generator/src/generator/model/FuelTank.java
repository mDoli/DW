package generator.model;

import generator.commons.RandomDouble;
import java.time.LocalDateTime;

/**
 * Klasa zbiornika paliwa.
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 * @version 2.0
 * @since 11.10.2014
 */ 
public class FuelTank {
    // Liczba mierników temperatury w zbiorniku.
    private static final int SENSORS = 6;    
    // Procent wypełnienia oznaczający pełny zbiornik.
    private static final double MAX_FULL_LEVEL = 0.85;        
    // Procent wypełnienia oznaczającego minimalny dopuszczalny poziom paliwa.
    private static final double MIN_FUEL_LEVEL = 0.10;
    
    private int id;                         // Identyfikator zbiornika.
    private volatile double fuelLevel;      // Poziom paliwa w zbiorniku.
    private double waterLevel;              // Poziom wody w zbiorniku.
    private double temperature;             // Temperatura w zbiorniku paliwa.
    private double capacity;                // Objętość zbiornika paliwa.
    private boolean full;                   // Flaga czy zbiornik jest pełny.
    private boolean empty;                  // Flaga, czy zbiornik jest pusty.
    
    /**
     * Tworzy instancję zbiornika paliwa.
     * 
     * @param id Numer portu, który staje się identyfikatorem.
     * @param capacity Pojemność zbiornika w litrach.
     */
    public FuelTank(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.full = true;
        this.empty = false;
        this.fuelLevel = fuelLevelFromCapacity();
        this.waterLevel = 0.0;
        this.temperature = generateTemperature(5.0, 5.5);
    }
    
    /**
     * Generuje krotkę z aktualnymi danymi o stanie obiektu.
     * 
     * @return Krotka z danymi o stanie zbiornika.
     */
    public Object[] sendTuple() {
        Object[] tuple = new Object[6];
        
        tuple[0] = this.id;
        tuple[1] = LocalDateTime.now();
        tuple[2] = this.fuelLevel;
        tuple[3] = this.waterLevel;
        tuple[4] = this.temperature;
        tuple[5] = this.capacity;
        
        return tuple;
    }
    
    /**
     * Sprawdza czy zbiornik jest pełny.
     * 
     * Jeśli poziom paliwa w zbiorniku jest równy lub przekracza 85% pojemności,
     * wtedy oznacza zbiornik jako pełny.
     */
    public void checkIsFull() {
        if (fuelLevel >= (getCapacity() * MAX_FULL_LEVEL))
            this.setFull(true);
        else
            this.setFull(false);
    }
    
    /**
     * Sprawdza czy zbiornik jest pusty.
     * 
     * Jeśli poziom paliwa jest równy lub jest niższy niż 10% pojemności,
     * oznacza zbiornik jako pusty.
     */
    public void checkIsEmpty() {
        if (fuelLevel <= (getCapacity() * MIN_FUEL_LEVEL))
            this.setEmpty(true);
        else
            this.setEmpty(false);
    }
    
    /**
     * Porównuje obiekty między sobą.
     *
     * @param fuelTank obiekt do porównania.
     * @return TRUE w przypadku bycia identycznym.
     */
    public Boolean compareTo(FuelTank fuelTank) {
        if (this.getCapacity() != fuelTank.getCapacity()) return false;
        if (this.getFuelLevel() != fuelTank.getFuelLevel()) return false;
        if (this.getId() != fuelTank.getId()) return false;
        if (this.getTemperature() != fuelTank.getTemperature()) return false;
        if (this.getWaterLevel() != fuelTank.getWaterLevel()) return false;
        
        return true;
    }
    
    /**
     * Porównuje obiekty między sobą ignorując idenetyfikator.
     *
     * @param fuelTank obiekt do porównania.
     * @return TRUE w przypadku bycia identycznym.
     */
    public Boolean compareToWithMissIdentify(FuelTank fuelTank) {
        if (this.getCapacity() != fuelTank.getCapacity()) return false;
        if (this.getFuelLevel() != fuelTank.getFuelLevel()) return false;
        if (this.getTemperature() != fuelTank.getTemperature()) return false;
        if (this.getWaterLevel() != fuelTank.getWaterLevel()) return false;
        
        return true;
    }
    
    /**
     * Kopiuje właściwości z podanego obiektu ignorujac identyfikator.
     *
     * @param fuelTank Zbiornik paliwa.
     */
    public void copyProperties(FuelTank fuelTank) {
        this.setFuelLevel(fuelTank.getFuelLevel());
        this.setTemperature(fuelTank.getTemperature());
        this.setWaterLevel(fuelTank.getWaterLevel());
        this.setFull(fuelTank.isFull());
        this.setCapacity(fuelTank.getCapacity());
    }
    
    /**
     * Zasilenie zbiornika z cysterny.
     * 
     * @param volume Objętość, którą cysterna ma wlać do zbiornika.
     * @return Zwraca objętość, która się nie zmieściła do zbiornika.
     */
    public double refuelTank(double volume) {
        double freeSpace;               // Wolne miejsce w zbiorniku
        double tmp = 0.0;               // Zmienna pomocnicza
        
        // Sprzawdzam czy zbiornik jest pełny
        checkIsFull();
        
        // Jeśli nie jest pełny to ile ma jeszcze miejsca
        if (!this.isFull()) {
            freeSpace = (getCapacity() * 0.85) - fuelLevel;
            // Ile się nie zmieści
            tmp = volume - freeSpace;
            // Tankowanie do pełna, a reszta w return funkcji
            fuelLevel += (volume - tmp);
        }
        
        checkIsEmpty();
        return tmp;
    }
    
    /**
     * Odjęcie podanej objętości ze zbiornika.
     * Funkcja wykorzystywana przy tankowaniu pistoletem paliwowym.
     * 
     * @param volume Objętość wylanego przez pistolet paliwa.
     */
    public void subtractFuel(double volume) {
        this.fuelLevel -= volume;
    }
    
    /**
     * Generuje temperature z mitycznych sensorów zbiorników.
     * Pobiera dane z sensorów do tablicy typu double i wylicza
     * średnią arytmetyczną. Zwraca wynik uśredniania.
     * 
     * @param min Minimalna wartość przedziału.
     * @param max Maxymalna wartość przedziału.
     * @return Średnia arytmetyczna z sensorów.
     */
    private double generateTemperature(double min, double max) {        
        double[] sensorReading = new double[SENSORS];
        for (int i = 0; i < SENSORS; i++) {
            sensorReading[i] = RandomDouble.generateRandomDouble(min, max);
        }        
        return avarageTemperature(sensorReading);
    }
    
    private double fuelLevelFromCapacity() {
        double level = this.getCapacity() * MAX_FULL_LEVEL;
        return level;
    }
    
    /**
     * Generuje poziom wody w zbiorniku.
     * @param min Minimum zakresu.
     * @param max Maksimum zakresu.
     * @return Poziom wody w zbiorniku.
     */
    private double generateWaterLevel(double min, double max) {
        double level = RandomDouble.generateRandomDouble(min, max);
        return level;
    }
    
    /**
     * Oblicza średnią temperaturę z sensorów.
     * @param temperature Odczyty sensorów.
     * @return Średnia temperatura.
     */
    private double avarageTemperature(double [] temperature) {        
        double sum = 0;
        for (int i = 0; i < SENSORS; i++) {
            sum += temperature[i];
        }
        return (sum/SENSORS);
    }
    
    /**
     * Getter identyfikatora zbiornika.
     * @return Identyfikator zbiornika.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter poziomu paliwa w zbiorniku.
     * @return Poziom paliwa w zbiorniku w [L].
     */
    public double getFuelLevel() {
        return fuelLevel;
    }

    /**
     * Getter poziomu wody w zbiorniku.
     * @return Poziom wody w zbiorniku w litrach.
     */
    public double getWaterLevel() {
        return waterLevel;
    }

    /**
     * Getter temperatury w zbiorniku.
     * @return Temperatura w stopniach Celciusza.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Setter identyfikatora zbiornika.
     * @param id Nr portu, który staje się identyfikatorem zbiornika.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter poziomu paliwa.
     * @param fuelLevel Poziom paliwa.
     */
    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    /**
     * Setter poziomu wody.
     * @param waterLevel Poziom wody w litrach.
     */
    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }

    /**
     * Setter temperatury.
     * @param temperature Temperatura w stopniach Celciusza.
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Getter pojemności zbiornika.
     * @return the capacity Pojemność zbiornika w litrach.
     */
    public double getCapacity() {
        return capacity;
    }

    /**
     * Getter stanu zbiornika.
     * @return the full.
     */
    public boolean isFull() {
        return full;
    }

    /**
     * Getter stanu zbiornika.
     * @return the empty.
     */
    public boolean isEmpty() {
        return empty;
    }
 
    /**
     * Setter pojemności zbiornika.
     * @param capacity Pojemność jaką ma mieć zbiornik w litrach.
     */
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    /**
     * Setter stanu pełnego zbiornika.
     * @param full the full to set
     */
    public void setFull(boolean full) {
        this.full = full;
    }

    /**
     * Setter stanu pustego zbiornika.
     * @param empty the empty to set
     */
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
