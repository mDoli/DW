package generator.model;

import org.joda.time.DateTime;


/**
 * Element historyczny dla typu Nozzle. Pomysł jest taki, że pistolet zazzwyczaj
 * przechodzi każde swoje kolejne stany, tj Najpierw jest IDLE, w sensie
 * nieużywany.. potem ktoś zaczyna rozpędzać machinę nalewania paliwa aż stan
 * wejdzie w tryb ciągły po jakimś czasie (CONST), wtedy zaczyna zwalniać
 * nalewanko aż w końcu jest po wszystkim i ktoś sobie autko zatankował.
 * Kończymy w IDLE_AFTER_DRAWN. To zznowu trwa jakąś chwilkę (okresy czasów będą
 * losowe) i oto całe piękno życia pistoleta.
 *
 * @author Kamil Komorek <kamikom681@student.polsl.pl>
 */
public class NozzleHistoryElement extends BaseObjectControllerHistoryElement {

    /**
     * Ustawia nowe zachowanie obiektu wraz z aktualizacją daty ostatniej zmiany
     *
     * @param newBehavior Nowe zachowanie obiektu
     */
    public void setNewBehaviorAndUpdateChangeDate(NHBehaviorEnum newBehavior) {
        this.currentBehavior = newBehavior;
        this.behaviorLastchangeDate = new DateTime();
        this.setChangeDate(new DateTime());
    }
    
    /**
     * @return the currentBehavior
     */
    public NHBehaviorEnum getCurrentBehavior() {
        return currentBehavior;
    }

    /**
     * @param currentBehavior the currentBehavior to set
     */
    public void setCurrentBehavior(NHBehaviorEnum currentBehavior) {
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
     * @return the drawnedInThisSession
     */
    public Double getDrawnedInThisSession() {
        return drawnedInThisSession;
    }

    /**
     * @param drawnedInThisSession the drawnedInThisSession to set
     */
    public void setDrawnedInThisSession(Double drawnedInThisSession) {
        this.drawnedInThisSession = drawnedInThisSession;
    }

    /**
     * @return the drawnedTotal
     */
    public Double getDrawnedTotal() {
        return drawnedTotal;
    }

    /**
     * @param drawnedTotal the drawnedTotal to set
     */
    public void setDrawnedTotal(Double drawnedTotal) {
        this.drawnedTotal = drawnedTotal;
    }

    /**
     * @return the drawnSpeed
     */
    public Double getDrawnSpeed() {
        return drawnSpeed;
    }

    /**
     * @param drawnSpeed the drawnSpeed to set
     */
    public void setDrawnSpeed(Double drawnSpeed) {
        this.drawnSpeed = drawnSpeed;
    }

    public enum NHBehaviorEnum {

        NOZZLE_IDLE,
        NOZZLE_SPEEDING_UP,
        NOZZLE_SLOWING_DOWN,
        NOZZLE_CONST_SPEED,
        NOZZLE_IDLE_AFTER_DRAWNING
    }

    /**
     * Deleguje typową preparacje obiektu BaseObjectControllerHistoryElement
     *
     * @param checksum Checksuma obiektu do przepisania
     * @param nozzle Obiekt typu Nozzle sam w sobie
     */
    public NozzleHistoryElement(String checksum, Nozzle nozzle) {
        super(checksum, OCHistoryObjectType.NOZZLE, nozzle);
    }

    /**
     * Aktualne zachowanie pistoletu...
     */
    private NHBehaviorEnum currentBehavior;

    /**
     * Kiedy ostatnio zmieniło się zachowanie
     */
    private DateTime behaviorLastchangeDate;

    /**
     * Wylanego napoju w "tej sesji"
     */
    private Double drawnedInThisSession;

    /**
     * Liczba wylanego paliwka od początku życia
     */
    private Double drawnedTotal;

    /**
     * Szybkość wylewania
     */
    private Double drawnSpeed;

    public NozzleHistoryElement() {
        super();
    }

}
