package generator.controller;

import generator.commons.RandomDouble;
import generator.commons.exception.CheckedObjectIsNotRegisteredException;
import generator.commons.exception.CouldNotRegisterNullObjectException;
import generator.model.BaseObjectControllerHistoryElement;
import generator.model.FuelTank;
import generator.model.FuelTankHistoryElement;
import generator.model.Nozzle;
import generator.model.NozzleHistoryElement;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 * Implementacja interfejsu ObjectController
 *
 * @author Kamil Komorek <kamikom681@student.polsl.pl>
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class ObjectControllerImpl implements ObjectController {

    private final static Logger log = Logger.getLogger(ObjectControllerImpl.class.getName());
    private final static Seconds SECONDS_5 = Seconds.seconds(5);
    private final static Seconds SECONDS_10 = Seconds.seconds(10);
    private final static Seconds SECONDS_15 = Seconds.seconds(15);
    private final static Seconds SECONDS_20 = Seconds.seconds(20);
    private final static Seconds SECONDS_25 = Seconds.seconds(25);
    private final static Seconds SECONDS_30 = Seconds.seconds(30);
    
    private final HashMap<String, BaseObjectControllerHistoryElement> historyElements;    
    private final Random random;

    public ObjectControllerImpl() {
        super();
        this.historyElements = new HashMap<>();
        this.random = new Random();
    }

    @Override
    public Boolean registerObject(Object object) throws CouldNotRegisterNullObjectException {
        if (object == null) {
            throw new CouldNotRegisterNullObjectException();
        }

        if (object instanceof FuelTank) {
            FuelTank f = (FuelTank) object;
            return registerObject(f);
        } else if (object instanceof Nozzle) {
            Nozzle n = (Nozzle) object;
            return registerObject(n);
        }
        return false;
    }

    @Override
    public Boolean registerObject(FuelTank fuelTank) throws CouldNotRegisterNullObjectException {
        if (fuelTank == null) {
            throw new CouldNotRegisterNullObjectException();
        }

        String checksum = BaseObjectControllerHistoryElement.countChecksumForObject(fuelTank.getId(),
                BaseObjectControllerHistoryElement.OCHistoryObjectType.FUELTANK);
        if (historyElements.get(checksum) == null) {

            FuelTankHistoryElement newElement = new FuelTankHistoryElement(checksum, fuelTank);

            historyElements.put(checksum, newElement);
            prepareNewRegisteredObject(newElement);
            log.log(Level.INFO, "Zarejestrowano nowy obiekt typu FuelTank, id: {0}", fuelTank.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean registerObject(Nozzle nozzle) throws CouldNotRegisterNullObjectException {
        if (nozzle == null) {
            throw new CouldNotRegisterNullObjectException();
        }

        String checksum = BaseObjectControllerHistoryElement.countChecksumForObject(nozzle.getId(),
                BaseObjectControllerHistoryElement.OCHistoryObjectType.NOZZLE);
        if (historyElements.get(checksum) == null) {

            NozzleHistoryElement newElement = new NozzleHistoryElement(checksum, nozzle);

            historyElements.put(checksum, newElement);
            prepareNewRegisteredObject((NozzleHistoryElement) newElement);
            log.log(Level.INFO, "Zarejestrowano nowy obiekt typu Nozzle, id: {0}", nozzle.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean doesObjectChanged(Object object) throws CheckedObjectIsNotRegisteredException {
        if (object == null) {
            return null;
        }

        if (object instanceof FuelTank) {
            FuelTank f = (FuelTank) object;
            return doesObjectChanged(f);
        } else if (object instanceof Nozzle) {
            Nozzle n = (Nozzle) object;
            return doesObjectChanged(n);
        }

        return null;
    }

    @Override
    public Boolean doesObjectChanged(FuelTank fuelTank) throws CheckedObjectIsNotRegisteredException {
        if (fuelTank == null) {
            return null;
        }

        BaseObjectControllerHistoryElement historical = getObjectsHistorical(fuelTank);

        FuelTank hf = (FuelTank) historical.getObject();

        Boolean willReturn = fuelTank.compareToWithMissIdentify(hf);

        // Odświeżenie historii obecnym stanem, o ile to te same obiekty
        if (fuelTank.getId() == fuelTank.getId()) {
            hf.copyProperties(fuelTank);
            historical.refreshChangeDate();
        }

        return willReturn;
    }

    @Override
    public Boolean doesObjectChanged(Nozzle nozzle) throws CheckedObjectIsNotRegisteredException {
        if (nozzle == null) {
            return null;
        }

        BaseObjectControllerHistoryElement historical = getObjectsHistorical(nozzle);

        Nozzle hn = (Nozzle) historical.getObject();

        Boolean willReturn = nozzle.compareToWithMissIdentify(hn);

        // Odświeżenie historii obecnym stanem, o ile to te same obiekty
        if (nozzle.getId() == hn.getId()) {
            hn.copyProperties(nozzle);
            historical.refreshChangeDate();
        }

        return willReturn;
    }

    @Override
    public void impactObject(Object object) {
        if (object instanceof FuelTank) {
            FuelTank f = (FuelTank) object;
            impactObject(f);
        } else if (object instanceof Nozzle) {
            Nozzle n = (Nozzle) object;
            impactObject(n);
        }
    }

    @Override
    public void impactObject(FuelTank fuelTank, double volumeToSubtract) {
        try {
            // Ściągnięcie z historii ostatniego stanu i "zachowania" obiektu
            FuelTankHistoryElement fh = getObjectsHistorical(fuelTank);
            FuelTank f = (FuelTank) fh.getObject();

            // Wpłynięcie na obiekt i aktualizacja stanów
            FuelTankHistoryElement.FTHBehaviorEnum b = fh.getCurrentBehavior();
            
            switch(b) {
                case TANK_EMPTY : {
                    // Tank pusty... zacznijmy napełniać po jakims czasie
                    if (Seconds.secondsBetween(fh.getBehaviorLastchangeDate(), new DateTime())
                            .isGreaterThan(SECONDS_20)) {
                        fh.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_IS_FILLED_NOW);
                        log.log(Level.INFO, "* FuelTank ({0}), rozpoczynam napelnianie...", fuelTank.getId());
                    }
                    break;
                }
                case TANK_IS_FILLED_NOW : {
                    // Tank napełniany... napełniajmy
                    f.refuelTank(RandomDouble.generateRandomDouble(2000.0, 5000.0));

                    // Pełny już?
                    if (f.isFull()) {
                        fh.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_FULL);
                        log.log(Level.INFO, "* FuelTank ({0}), koncze napelnianie...", fuelTank.getId());
                    }
                    break;
                }
                case TANK_FULL : {
                    // Tank pełny... skończmy napełniać
                    // W sumie nic do roboty. Zacznijmy oprozniac go...
                    fh.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_DRAWNING);
                    log.log(Level.INFO, "* FuelTank ({0}), pelny, oprozniam...", fuelTank.getId());
                    break;
                }
                case TANK_DRAWNING : {
                    // Tank opróżnia się (jest wykorzystywany)                
                    f.checkIsEmpty();
                    if (f.isEmpty()) {
                        fh.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_EMPTY);
                        log.log(Level.INFO, "* FuelTank ({0}), pusty...", fuelTank.getId());
                    }
                    // Oprozniamy zboirnik o ilosc paliwa jaka wylano przez pistolet.
                    f.subtractFuel(volumeToSubtract);
                    break;
                }
            }
        } catch (CheckedObjectIsNotRegisteredException ex) {
            log.log(Level.SEVERE, null, ex);
            log.log(Level.INFO, "Pr\u00f3ba oddzia\u0142ania na niezarejestrowany obiekt: {0}", fuelTank.toString());
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void impactObject(Nozzle nozzle) {
        try {
            NozzleHistoryElement nh = getObjectsHistorical(nozzle);
            Nozzle n = (Nozzle) nh.getObject();

            // Wpłynięcie na obiekt pistoletu paliwowego.
            NozzleHistoryElement.NHBehaviorEnum b = nh.getCurrentBehavior();
            
            switch(b) {
                case NOZZLE_IDLE : {
                    // Pistolet nieużywany, po jakimś czasie przechodzi w stan tankowania.
                    if (Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime())
                            .isGreaterThan(Seconds.seconds(random.nextInt(10)))) {
                        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_SPEEDING_UP);
                        log.log(Level.INFO, "* Nozzle ({0}), IDLE -> SPEED UP...", nozzle.getId());
                    }
                    break;
                }
                case NOZZLE_SPEEDING_UP : {
                    // Tankowanie przyspiesza i wpływa na poziom paliwa w zbiorniku.
                    Double drawnSpeed = nh.getDrawnSpeed() + RandomDouble.generateRandomDouble(0.1, 0.7);
                    nh.setDrawnSpeed(drawnSpeed);

                    nh.setDrawnedInThisSession(nh.getDrawnedInThisSession() + drawnSpeed);
                    n.setCounter(n.getCounter() + drawnSpeed);
                    n.setLitleCounter(n.getLitleCounter() + drawnSpeed);
                    impactObject(n.getAssignedFuelTank(), drawnSpeed);
                    
                    // Gdy dojdzie do maksymalnej szybkości lub minie jakiś czas, tankowanie przechodzi
                    // w stan stałego tankowania.
                    if (drawnSpeed >= 10 || Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime())
                            .isGreaterThan(SECONDS_5)) {
                        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_CONST_SPEED);
                        log.log(Level.INFO, "* Nozzle ({0}), -> CONST...", nozzle.getId());
                    }
                    break;
                }
                case NOZZLE_CONST_SPEED : {
                    // Tankowanie odbywa się ze stałą prędkością i wpływa na poziom paliwa w zbiorniku.
                    Double drawnSpeed = nh.getDrawnSpeed();
                    nh.setDrawnedInThisSession(nh.getDrawnedInThisSession() + drawnSpeed);
                    n.setCounter(n.getCounter() + drawnSpeed);
                    n.setLitleCounter(n.getLitleCounter() + drawnSpeed);
                    impactObject(n.getAssignedFuelTank(), drawnSpeed);
                    
                    // Gdy minie jakiś czas, szybkość tankowania maleje.
                    if (Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime())
                            .isGreaterThan(SECONDS_10)) {
                        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_SLOWING_DOWN);
                        log.log(Level.INFO, "* Nozzle ({0}), CONST -> SPEED DOWN...", nozzle.getId());
                    }
                    break;
                }
                case NOZZLE_SLOWING_DOWN : {
                    // Szybkość tankowania maleje i wpływa na poziom paliwa w zbiorniku.
                    Double drawnSpeed = nh.getDrawnSpeed() - RandomDouble.generateRandomDouble(0.1, 0.7);
                    // Gdy szybkość tankowania jest równa lub mniejsza 0 oznacza to koniec tankowania
                    // i przejście w stan po tankowaniu.
                    if (drawnSpeed <= 0) {
                        nh.setDrawnSpeed(0.0);
                        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_IDLE_AFTER_DRAWNING);
                        log.log(Level.INFO, "* Nozzle ({0}), -> koncze tankowanie...", nozzle.getId());

                        // Koniec tankowania, zerowanie licznika pojedynczego tankowania.
                        n.resetLittleCounter();
                    } else {
                        // Szybkość tankowania jeszcze nie spadła do zera więc wpływa na zbiornik paliwa.
                        nh.setDrawnSpeed(drawnSpeed);
                        nh.setDrawnedInThisSession(nh.getDrawnedInThisSession() + drawnSpeed);
                        n.setCounter(n.getCounter() + drawnSpeed);
                        n.setLitleCounter(n.getLitleCounter() + drawnSpeed);
                        impactObject(n.getAssignedFuelTank(), drawnSpeed);
                    }
                    break;
                }
                case NOZZLE_IDLE_AFTER_DRAWNING : {
                    // Pistolet w stanie po tankowaniu.
                    if (Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime()).isGreaterThan(SECONDS_5)) {
                        nh.setDrawnedTotal(nh.getDrawnedTotal() + nh.getDrawnedInThisSession());
                        nh.setDrawnedInThisSession(0.0);
                        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_IDLE);
                        log.log(Level.INFO, "* Nozzle ({0}), idle...", nozzle.getId());
                    }
                    break;
                }
            }
        } catch (CheckedObjectIsNotRegisteredException ex) {
            log.log(Level.SEVERE, null, ex);
            log.log(Level.INFO, "Pr\u00f3ba oddzia\u0142ania na niezarejestrowany obiekt: {0}", nozzle.toString());
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BaseObjectControllerHistoryElement getObjectsHistorical(Object object) throws CheckedObjectIsNotRegisteredException {
        if (object instanceof FuelTank) {
            return getObjectsHistorical((FuelTank) object);
        } else if (object instanceof Nozzle) {
            return getObjectsHistorical((Nozzle) object);
        }
        return null;
    }

    @Override
    public NozzleHistoryElement getObjectsHistorical(Nozzle nozzle) throws CheckedObjectIsNotRegisteredException {
        String key = BaseObjectControllerHistoryElement.countChecksumForObject(nozzle.getId(),
                BaseObjectControllerHistoryElement.OCHistoryObjectType.NOZZLE);
        BaseObjectControllerHistoryElement historical = historyElements.get(key);

        if (historical == null) {
            throw new CheckedObjectIsNotRegisteredException();
        }

        return (NozzleHistoryElement) historical;
    }

    @Override
    public FuelTankHistoryElement getObjectsHistorical(FuelTank fuelTank) throws CheckedObjectIsNotRegisteredException {

        String key = BaseObjectControllerHistoryElement.countChecksumForObject(fuelTank.getId(),
                BaseObjectControllerHistoryElement.OCHistoryObjectType.FUELTANK);
        BaseObjectControllerHistoryElement historical = historyElements.get(key);

        if (historical == null) {
            throw new CheckedObjectIsNotRegisteredException();
        }

        return (FuelTankHistoryElement) historical;
    }

    /**
     * Inicjuje element historyczny, który jeszcze nie ma ustalonego własnego
     * zachowania. Operacja wymagana do przeprowadzenia, aby impactObject w
     * ogóle cokolwiek zrobiło.
     *
     * @param fuelTankHistoryElement nowy element historyczny dla FuelTank do
     * zainicjowania
     */
    private void prepareNewRegisteredObject(FuelTankHistoryElement fuelTankHistoryElement) {
        FuelTank f = (FuelTank) fuelTankHistoryElement.getObject();
        if (f.isFull()) {
            fuelTankHistoryElement.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_FULL);
        } else {
            fuelTankHistoryElement.setNewBehaviorAndUpdateChangeDate(FuelTankHistoryElement.FTHBehaviorEnum.TANK_EMPTY);
        }
    }

    /**
     * Inicjuje element historyczny, który jeszcze nie ma ustalonego własnego
     * zachowania. Operacja wymagana do przeprowadzenia, aby impactObject w
     * ogóle cokolwiek zrobiło.
     *
     * @param nozzleHistoryElement nowy element historyczny dla Nozzle do
     * zainicjowania
     */
    private void prepareNewRegisteredObject(NozzleHistoryElement nozzleHistoryElement) {
        nozzleHistoryElement.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_IDLE);

        nozzleHistoryElement.setDrawnSpeed(new Double(0));
        nozzleHistoryElement.setDrawnedInThisSession(new Double(0));
        nozzleHistoryElement.setDrawnedTotal(new Double(0));
    }

    public static void main(String[] args) {
        DateTime n = new DateTime();
        NozzleHistoryElement nh = new NozzleHistoryElement();
        System.out.println("-- Start: \t" + n);
        System.out.println("nh: \t\t" + nh.getBehaviorLastchangeDate());
        nh.setNewBehaviorAndUpdateChangeDate(NozzleHistoryElement.NHBehaviorEnum.NOZZLE_IDLE);
        System.out.println("nh change: \t" + nh.getBehaviorLastchangeDate());
        Boolean foo = Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime()).isGreaterThan(Seconds.THREE);
        System.out.println("nh > now : 3s? \t" + foo);
        foo = Seconds.secondsBetween(nh.getBehaviorLastchangeDate(), new DateTime()).isLessThan(SECONDS_5);
        System.out.println("nh < now : 5S? \t" + foo);
    }
}
