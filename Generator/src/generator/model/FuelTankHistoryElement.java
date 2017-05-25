package generator.model;

import org.joda.time.DateTime;

/**
 * Element historyczny dla typu FuelTank
 *
 * @author Kamil Komorek <kamikom681@student.polsl.pl>
 */
public class FuelTankHistoryElement extends BaseObjectControllerHistoryElement {
    
    /**
     * Ustawia nowe zachowanie obiektu wraz z aktualizacją daty ostatniej zmiany
     *
     * @param newBehavior Nowe zachowanie obiektu
     */
    public void setNewBehaviorAndUpdateChangeDate(FTHBehaviorEnum newBehavior) {
        this.currentBehavior = newBehavior;
        this.behaviorLastchangeDate = new DateTime();
        this.setChangeDate(new DateTime());
    }

    /**
     * @return the currentBehavior
     */
    public FTHBehaviorEnum getCurrentBehavior() {
        return currentBehavior;
    }

    /**
     * @param currentBehavior the currentBehavior to set
     */
    public void setCurrentBehavior(FTHBehaviorEnum currentBehavior) {
        this.currentBehavior = currentBehavior;
    }

    /**
     * @return the behaviorLastchangeDate
     */
    public DateTime getBehaviorLastchangeDate() {
        return behaviorLastchangeDate;
    }

    /**
     * @param behaviorLastchangeDate the behaviorLastchangeDate to set
     */
    public void setBehaviorLastchangeDate(DateTime behaviorLastchangeDate) {
        this.behaviorLastchangeDate = behaviorLastchangeDate;
    }

    /**
     * @return the currentSeason
     */
    public FTHSeasonEnum getCurrentSeason() {
        return currentSeason;
    }

    /**
     * @param currentSeason the currentSeason to set
     */
    public void setCurrentSeason(FTHSeasonEnum currentSeason) {
        this.currentSeason = currentSeason;
    }

    /**
     * @return the seasonChangeDate
     */
    public DateTime getSeasonChangeDate() {
        return seasonChangeDate;
    }

    /**
     * @param seasonChangeDate the seasonChangeDate to set
     */
    public void setSeasonChangeDate(DateTime seasonChangeDate) {
        this.seasonChangeDate = seasonChangeDate;
    }

    public enum FTHBehaviorEnum {

        TANK_EMPTY,
        TANK_IS_FILLED_NOW,
        TANK_FULL,
        TANK_DRAWNING

    }

    public enum FTHSeasonEnum {

        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }

    /**
     * Deleguje typową preparacje obiektu BaseObjectControllerHistoryElement
     *
     * @param checksum Checksuma obiektu do przepisania
     * @param fuelTank Obiekt typu FuelTank sam w sobie
     */
    public FuelTankHistoryElement(String checksum, FuelTank fuelTank) {
        super(checksum, OCHistoryObjectType.FUELTANK, fuelTank);
    }       

    /**
     * Aktualne zachowanie zbiornika...
     */
    private FTHBehaviorEnum currentBehavior;

    /**
     * Kiedy ostatnio zmieniło się zachowanie
     */
    private DateTime behaviorLastchangeDate;

    /**
     * Zależnie od aktualnego sezonu gromadzi się wiecej lub mniej wody w zbiorniku. 
     * Ofc temperatura samego tanka też tutaj jest ofiarą.
     */
    private FTHSeasonEnum currentSeason;
    
    /**
     * Data ostatniej zmiany sezonu...
     */
    private DateTime seasonChangeDate;

}
