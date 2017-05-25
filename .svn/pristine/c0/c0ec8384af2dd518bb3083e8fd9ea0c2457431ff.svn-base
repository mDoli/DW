package generator.controller;

import generator.commons.exception.CheckedObjectIsNotRegisteredException;
import generator.commons.exception.CouldNotRegisterNullObjectException;
import generator.model.BaseObjectControllerHistoryElement;
import generator.model.FuelTank;
import generator.model.FuelTankHistoryElement;
import generator.model.Nozzle;
import generator.model.NozzleHistoryElement;

/**
 * Interfejs kontrolera przychodzącego obiektu typu Nozzle/FuelTank
 *
 * @author Kamil Komorek <kamikom681@student.polsl.pl>
 */
public interface ObjectController {

    /**
     * Wykrywa typ obiektu i deleguje do odpowiedniej metody w celu rejestracji
     * i śledzenia go. Aby móc skorzystać z metod typu doesObjectChanged
     * wymagane jest uprzednie zarejestrowanie obiektu w tym kontrolerze.
     *
     * @param object typ obiektu do wykrycia.
     * @return TRUE w przypadku pomyślnej rejestracji. Jeśli obiekt już
     * zarejestrowany zwraca FALSE
     * @throws generator.commons.exception.CouldNotRegisterNullObjectException w
     * przypadku próby rejestracji null'a
     */
    public Boolean registerObject(Object object) throws CouldNotRegisterNullObjectException;

    /**
     * Rejestruje do śledzenia obiekt typu FuelTank.
     *
     * @param fuelTank obiekt do śledzenia typu FuelTank
     * @return TRUE w przypadku pomyślnej rejestracji. Jeśli obiekt już
     * zarejestrowany zwraca FALSE
     * @throws generator.commons.exception.CouldNotRegisterNullObjectException w
     * przypadku próby rejestracji null'a
     */
    public Boolean registerObject(FuelTank fuelTank) throws CouldNotRegisterNullObjectException;

    /**
     * Rejestruje do śledzenia obiekt typu Nozzle
     *
     * @param nozzle obiekd do śledzenia typu Nozzle
     * @return TRUE w przypadku pomyślnej rejestracji. Jeśli obiekt już
     * zarejestrowany zwraca FALSE
     * @throws generator.commons.exception.CouldNotRegisterNullObjectException w
     * przypadku próby rejestracji null'a
     */
    public Boolean registerObject(Nozzle nozzle) throws CouldNotRegisterNullObjectException;

    /**
     * Rozpoznaje typ obiektu i deleguje do odpowiedniej metody pobierającej
     * obiekt historyczny
     *
     * @param object obiekt, który wyszukać
     * @return obiekt historyczny
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public BaseObjectControllerHistoryElement getObjectsHistorical(Object object) throws CheckedObjectIsNotRegisteredException;

    /**
     * Pobiera historyczny element dla obiektu typu Nozzle
     *
     * @param nozzle obiekt na podstawie którego wyszukać historyczny obiekt
     * @return historyczny obiekt dla podanego argumntu
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public NozzleHistoryElement getObjectsHistorical(Nozzle nozzle) throws CheckedObjectIsNotRegisteredException;

    /**
     * Pobiera historyczny element dla obiektu typu FuelTank
     *
     * @param fuelTank obiekt na podstawie którego wyszukać historyczny obiekt
     * @return historyczny obiekd dla podanego argumentu
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public FuelTankHistoryElement getObjectsHistorical(FuelTank fuelTank) throws CheckedObjectIsNotRegisteredException;

    /**
     * Metoda domyślna wykrywajaca typ i routująca do specyficznej metody danego
     * typu
     *
     * @param object Obiekt do sprawdzenia
     * @return Informacja odnośnie tego, czy obiekt uległ zmianie. Zwraca null w
     * przypadku nierozpoznania obiektu lub jeśli przyszedł null.
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public Boolean doesObjectChanged(Object object) throws CheckedObjectIsNotRegisteredException;

    /**
     * Specyficzna metoda wykrywajaca zmiany w obiekcie typu FuelTank
     *
     * @param fuelTank Obiekt typu FuelTank
     * @return Boolean.TRUE w przypadku zmian w obiekcie, w przeciwnym wypadku
     * FALSE. Zwraca null, jeśli przyszedł null.
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public Boolean doesObjectChanged(FuelTank fuelTank)  throws CheckedObjectIsNotRegisteredException;

    /**
     * Specyfizcna metoda wykrywająca zmiany w obiekcie typu FuelTank
     *
     * @param nozzle Obiekt typu Nozzle
     * @return Boolean.TRUE w przypadku zmian w obiekcie, w przeciwnym wypadku
     * FALSE. Zwraca null, jeśli przyszedł null.
     * @throws generator.commons.exception.CheckedObjectIsNotRegisteredException
     * przy próbie sprawdzania obiektu, który nie został wcześniej
     * zarejestrowany.
     */
    public Boolean doesObjectChanged(Nozzle nozzle)  throws CheckedObjectIsNotRegisteredException;

    /**
     * Metoda domyślna rozpoznająca obiekt i przekazująca do odpowiedniej metody
     * wpływająca na jego stan.
     *
     * @param object Obiekt nieznanego typu
     */
    public void impactObject(Object object);

    /**
     * Metoda symulująca zmiany w obiekcie typu FuelTank
     *
     * @param fuelTank Obiekt typu FuelTank
     * @param volumeToSubtract
     */
    public void impactObject(FuelTank fuelTank, double volumeToSubtract);

    /**
     * Metoda symulująca zmiany w obiekcie typu Nozzle
     *
     * @param nozzle Obiekt typu Nozzle
     */
    public void impactObject(Nozzle nozzle);

}
