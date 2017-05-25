package stretl.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javafx.util.converter.LocalDateTimeStringConverter;


/**
 * Klasa obiektu krotki. Obiekt ten dziedziczy po pewnej implementacji Listy.
 * Dzięki czemu możliwe jest rzutowanie bajtowych reprezentacji KROTEK na
 * obiekty oraz wypisywanie krotek w formie tablic dwuwymiarowuch.
  *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 * @param <T> Dane w krotce występują w różnych typach.
 */
public class Tuple<T> extends ArrayList<T> implements Cloneable {
    
    public Tuple() { };
    
    public Tuple(int size) { super(size); }
    
    public Tuple(Collection<? extends T> clctn) {
        super(clctn);
    }

    /**
     * Tworzy nową instancję krotki z kopią zawartości (shallow copy).
     * @return
     */
    @Override
    public Tuple<T> clone() {
        return (Tuple<T>) super.clone();
    }

    /**
     * Zwraca zawartość krotki w formie tekstowej.
     * @return
     */
    @Override
    public String toString() {
        return super.toString().replace("[", "").replace("]", "");
    }
    
    /**
     * Pobiera źródło krotki.
     * @return Źródło kortki.
     */
    public Integer getSource() {
        if (this.get(0) != null && this.get(0) instanceof Integer)
            return (Integer)this.get(0);
        else
            return null;
    }
    
    /**
     * Ustawia źródło krotki
     * @param source Źródło krotki.
     */
    public void setSource(Integer source)
    {
        if (this.size() > 0)
            this.set(0, (T)source);
        else
            this.add(0, (T)source);
    }
    
    /**
     * Pobiera stempel czasowy utworzenia krotki.
     * @return Stempel czasowy.
     */
    public LocalDateTime getTimeStamp(){
        if (this.get(1) != null && this.get(1) instanceof LocalDateTime)
            return (LocalDateTime)this.get(1);
        else
            return null;
    }
    /**
     * Pobiera stempel czasowy utworzenia krotki.
     * @return Stempel czasowy.
     */
    public Timestamp getTimestamp() {
        LocalDateTime ldt = getTimeStamp();
        if (ldt != null) return Timestamp.valueOf(ldt);
        else return null;
    }
    
    /**
     * Ustawia stempel czasowy krotki.
     * @param timestamp Stempel czasowy.
     */
    public void setTimeStamp(LocalDateTime timestamp) {
        if (this.size() > 1)
            this.set(1, (T)timestamp);
        else
            this.add(1, (T)timestamp);
    }    
    
    /**
     * Ustawia stempel czasowy krotki.
     * @param timestamp Stempel czasowy.
     */
    public void setTimeStamp(Timestamp timestamp) {
        if (this.size() > 1)
        {
            if (timestamp != null)
                this.set(1, (T)(timestamp.toLocalDateTime()));
            else
                this.set(1, null);
        }
        else
        {
            if (timestamp != null)
                this.add(1, (T)(timestamp.toLocalDateTime()));
            else
                this.add(1, null);
        }
    }
        
    /**
     * Pobiera wartość objętości paliwa z krotki.
     * @return Objętość paliwa.
     */
    public Double getVolume() {
        if (this.get(2) != null && this.get(2) instanceof Double)
            return (Double)this.get(2);
        else
            return null;
    }
    
    /**
     * Ustawia objętość paliwa w krotce.
     * @param volume Objętość paliwa.
     */
    public void setVolume(Double volume) {
        if (this.size() > 2)
            this.set(2, (T)volume);
        else
            this.add(2, (T)volume);
    }
    
    /**
     * Pobiera wysokość poziomu wody.
     * @return Poziom wody.
     */
    public Double getWaterLevel() {
        if (this.get(3) != null && this.get(3) instanceof Double)
            return (Double)this.get(3);
        else
            return null;
    }
    
    /**
     * Ustawia poziom wody.
     * @param e Poziom wody.
     */
    public void setWaterLevel(Double e) {
        if (this.size() > 3)
            this.set(3, (T)e);
        else
            this.add(3, (T)e);
    }
    
    /**
     * Pobiera wartośc temperatury.
     * @return Temperatura.
     */
    public Double getTemperature() {
        if (this.get(4) != null && this.get(4) instanceof Double)
            return (Double)this.get(4);
        else
            return 0.0;
    }
    
    /**
     * Ustawia wartośc temperatury.
     * @param temperature Temperatura.
     */
    public void setTemperature(Double temperature) {
        if (this.size() > 4)
            this.set(4, (T)temperature);
        else
            this.add(4, (T)temperature);
    }
    
    /**
     * Pobiera identyfukator schematu w którym krotka była.
     * @return Identyfikator schematy ETL.
     */
    public Integer getSchemaElementId() {
        if (this.get(5) != null && this.get(5) instanceof Integer)
            return (Integer)this.get(5);
        else
            return -1;
    }
    
    /**
     * Ustawia identyfikator schematu ETL.
     * @param schemaElementId Identyfikator schematu ETL.
     */
    public void setSchemaElementId(Integer schemaElementId) {
        if (this.size() > 5)
            this.set(5, (T)schemaElementId);
        else
            this.add(5, (T)schemaElementId);
    }
    
    /**
     * Ustawia stempel czasowy wejścia krotki do systemu wyrazony w ms.
     * @param timestamp Stempel czasowy w ms.
     */
    public void setOpeningTimestamp(Long timestamp)
    {
        if (this.size() > 6)
            this.set(6, (T)timestamp);
        else
            this.add(6, (T)timestamp);
    }
    
    /**
     * Pobiera stempel czasowy wejścia krotki do systemu wyrazony w ms.
     * @return Stempel czasowy wyrażony w ms.
     */
    public Long getOpeningTimestamp()
    {
        if (this.get(6) != null && this.get(6) instanceof Long)
            return (Long)this.get(6);
        else
            return null;
    }
    
    /**
     * Ustawia identyfikator (unikalny) krotki.
     * @param id Identyfikator krotki.
     */
    public void setId(Long id)
    {
        if (this.size() > 7)
            this.set(7, (T)id);
        else
            this.add(7, (T)id);
    }
    
    /**
     * Pobiera identyfikator krotki.
     * @return Identyfikator krotki.
     */
    public Long getId()
    {
        if (this.get(7) != null && this.get(7) instanceof Long)
            return (Long)this.get(7);
        else
            return null;
    }
    
    // Nozzle
    public Double getNetVolume() {
        if (this.get(6) != null && this.get(6) instanceof Double)
            return (Double)this.get(6);
        else
            return null;
    }
    
    public void setNetVolume(Double e) {
        this.set(6, (T)e);
    }
    
    public double getNozzleTotalCounter()
    {
        if (Enums.MeterType.fromInteger(this.getSource()) == Enums.MeterType.NOZZLE)
        {
            if (this.get(2) instanceof Double)
            {
                return (Double)this.get(2);
            }
        }
        return Double.NaN;
    }
    
    /**
     *
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Tuple obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }
    
    /**
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Tuple deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);        
        Object obj = o.readObject();
        
        if (obj instanceof Object[])        
            return new Tuple(Arrays.asList((Object[])obj));        
        else if (obj instanceof Tuple)        
            return (Tuple)obj;        
        else
            return null;
    }
    
    /**
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserializeToObj(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);        
        Object obj = o.readObject();
        return obj;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Tuple)
        {
            Tuple t = (Tuple)o;
            return t.getSource()== this.getSource() && this.getId()== t.getId() && this.getTimeStamp() == t.getTimeStamp();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
    
    
}
