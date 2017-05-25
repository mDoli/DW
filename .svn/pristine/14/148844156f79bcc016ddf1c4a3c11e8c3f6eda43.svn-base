package generator.model;

import java.time.LocalDateTime;

/**
 * Klasa pistoletu paliwowego.
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 * @version 2.0
 * @since 11.10.2014
 */
public class Nozzle {
    // Domyślny współczynni kalibracji 100%
    private static final double CALIBRATION_DEFAULT = 1.0;
    
    private int id;                         // Identyfikator dystrybutora.
    private double calibration;             // Współczynnik kalibracji.
    private double counter;                 // Licznik przelanego paliwa.
    private double litleCounter;            // Licznik pojedynczej transakcji.
    private FuelTank assignedFuelTank;      // Przypisany zbiornik dla pistoletu.
        
    /**
     * Tworzy instancje pistoletu paliwowego.
     * 
     * @param id Identyfikator - nr portu.
     * @param calibration Współczynnik kalibracji.
     * @param fuelTank Zbiornik paliwa, do którego ma być przypisany pistolet.
     */
    public Nozzle(int id, double calibration, FuelTank fuelTank) {
        this.id = id;
        this.counter = 0.0;
        if (calibration != 0)
            this.calibration = calibration;
        else
            this.calibration = CALIBRATION_DEFAULT;
        this.assignedFuelTank = fuelTank;
    }
    
    /**
     * Porównuje obiekty między sobą.
     *
     * @param nozzle Obiekt do porównania.
     * @return TRUE w przypadku bycia identycznym.
     */
    public Boolean compareTo(Nozzle nozzle) {
        if (this.getCalibration() != nozzle.getCalibration()) return false;
        if (this.getId() != nozzle.getId()) return false;
        if (this.getCounter() != nozzle.getCounter()) return false;
        
        return true;
    }
    
    /**
     * Porównuje obiekty między sobą ignorując identyfikator.
     *
     * @param nozzle Obiekt do porównania.
     * @return TRUE w przypadku bycia identycznym.
     */
    public Boolean compareToWithMissIdentify(Nozzle nozzle) {
        if (this.getCalibration() != nozzle.getCalibration()) return false;
        if (this.getCounter() != nozzle.getCounter()) return false;
        
        return true;
    }
    
    /**
     * Generuje krotę z aktualnymi danymi obiektu.
     * 
     * @return Krotę z danymi reprezentującymi aktualny stan pistoletu.
     */
    public Object[] sendTuple() {
        Object[] tuple = new Object[6];
        
        tuple[0] = this.id;
        tuple[1] = LocalDateTime.now();
        tuple[2] = this.counter;
        tuple[3] = this.litleCounter;
        tuple[4] = this.assignedFuelTank.getId();
        tuple[5] = this.calibration;
        
        return tuple;
    }
    
    /**
     * Kopiuje właściwości z podanego obiektu ignorujac identyfikator.
     * 
     * @param nozzle Obiekt na podstawie którego kopiuje
     */
    public void copyProperties(Nozzle nozzle) {
        this.setCalibration(nozzle.getCalibration());
        this.setCounter(nozzle.getCounter());
    }
    
    /**
     * Resetuje/zeruje stan licznika pojedynczej tranzakcji.
     */
    public void resetLittleCounter() {
        this.litleCounter = 0.0;
    }
    
    /**
     * Getter id pistoletu.
     * @return Id pistoletu.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter id pistoletu.
     * @param id Nr portu.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter współczynnika kalibracji.
     * @return Współczynnik kalibracji pistoletu.
     */
    public double getCalibration() {
        return calibration;
    }

    /**
     * Setter współczynnika kalibracji.
     * @param calibration Współczynnik kalibracji.
     */
    public void setCalibration(double calibration) {
        this.calibration = calibration;
    }    

    /**
     * Getter głównego licznika.
     * @return Wartość głównego licznika.
     */
    public double getCounter() {
        return counter;
    }

    /**
     * Setter wartości głównego licznika.
     * @param counter Wartość licznika.
     */
    public void setCounter(double counter) {
        this.counter = counter;
    }

    /**
     * Getter obiektu zbiornika do którego przypisany jest pistolet.
     * @return Obiekt zbiornika.
     */
    public FuelTank getAssignedFuelTank() {
        return assignedFuelTank;
    }

    /**
     * Przypisuje zbiornika paliwa dla tego pistoletu.
     * @param assignedFuelTank Obiekt zbiornika paliwa, który zostanie przypisany.
     */
    public void assignFuelTank(FuelTank assignedFuelTank) {
        this.assignedFuelTank = assignedFuelTank;
    }   

    /**
     * Getter licznika pojedynczej tranzakcji.
     * @return Wartość licznika pojedynczej tranzakcji.
     */
    public double getLitleCounter() {
        return litleCounter;
    }
    
    /**
     * Setter licznika pojedynczej transakcji.
     * @param litleCounter Wartość licznika.
     */
    public void setLitleCounter(double litleCounter) {
        this.litleCounter = litleCounter;
    }
}
