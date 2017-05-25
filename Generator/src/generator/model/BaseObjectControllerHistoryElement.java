package generator.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 * Element wykorzystywany do okładania w hashmapie historycznej klasy
 * ObjectController.
 *
 * @author Kamil Komorek <kamikom681@student.polsl.pl>
 */
public class BaseObjectControllerHistoryElement {

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @return the objectType
     */
    public OCHistoryObjectType getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(OCHistoryObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the changeDate
     */
    public DateTime getChangeDate() {
        return changeDate;
    }

    /**
     * @param changeDate the changeDate to set
     */
    public void setChangeDate(DateTime changeDate) {
        this.changeDate = changeDate;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * Typy obiektów znane i wykorzystywane w ObjectControllerHistoryElement.
     */
    public static enum OCHistoryObjectType {

        UNKNOWN, FUELTANK, NOZZLE
    };

    /**
     * Identyfikator obiektu historycznego w postaci sumy md5.
     */
    private final String checksum;

    /**
     * Pole opisujące przechowywany obiekt
     */
    private OCHistoryObjectType objectType;

    /**
     * Data ostatniego stanu.
     */
    private DateTime changeDate;

    /**
     * Sam obiekt lub jego historyczna postać.
     */
    private Object object;

    public BaseObjectControllerHistoryElement() {
        this.checksum = countChecksumForObject(new GregorianCalendar().getTimeInMillis(), OCHistoryObjectType.UNKNOWN);
        this.objectType = OCHistoryObjectType.UNKNOWN;
        this.changeDate = new DateTime();
        this.object = null;
    }

    public BaseObjectControllerHistoryElement(OCHistoryObjectType objectType) {
        this.checksum = countChecksumForObject(new GregorianCalendar().getTimeInMillis(), objectType);
        this.objectType = objectType;
        this.changeDate = new DateTime();
        this.object = null;
    }

    public BaseObjectControllerHistoryElement(String checksum) {
        this.checksum = checksum;
        this.objectType = OCHistoryObjectType.UNKNOWN;
        this.changeDate = new DateTime();
        this.object = null;
    }

    public BaseObjectControllerHistoryElement(String checksum, OCHistoryObjectType type, Object object) {
        this.checksum = checksum;
        this.object = object;
        this.objectType = type;
        this.changeDate = new DateTime();
    }

    public void refreshChangeDate() {
        this.changeDate = new DateTime();
    }
    
    /**
     * Deleguje przeliczenie sumy dla obiektu po konwersji typu numerycznego.
     *
     * @param uniqueSeed Unikalne ziarno typu numerycznego
     * @param objectType Typ obiektu (jako sul)
     * @return wynik funkcji countChecksumForObject wywołanej z pierwszym parametrem jako string
     */
    public static String countChecksumForObject(Number uniqueSeed, OCHistoryObjectType objectType) {
        return countChecksumForObject(uniqueSeed.toString(), objectType);
    }
    
    /**
     * Generuje checksumę specyficzną dla danego typu obiektu na podstawie
     * podanego ziarna. Więcej info:
     * http://javarevisited.blogspot.com/2013/03/generate-md5-hash-in-java-string-byte-array-example-tutorial.html
     *
     * @param uniqueSeed Unikatowe ziarno na obiekt typu
     * @param objectType Typ obiektu przeliczanego (jako sól do obliczonego
     * ciągu)
     * @return Checksuma wyliczona z podanych parametrów
     */
    public static String countChecksumForObject(String uniqueSeed, OCHistoryObjectType objectType) {
        try {
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            String toEncode = objectType + uniqueSeed;
            byte[] bytesOfString = md.digest(toEncode.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder(2 * bytesOfString.length);
            for (byte b : bytesOfString) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(BaseObjectControllerHistoryElement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("milis as string: " + new GregorianCalendar().getTimeInMillis());
        System.out.println("countChecksumForObject( \"1\", UNKNOWN ): \t" + countChecksumForObject("1", OCHistoryObjectType.UNKNOWN));
        System.out.println("countChecksumForObject( 1, UNKNOWN ): \t\t" + countChecksumForObject(1, OCHistoryObjectType.UNKNOWN));
        System.out.println("countChecksumForObject( 1, FUELTANK ): \t\t" + countChecksumForObject(1, OCHistoryObjectType.FUELTANK));
        System.out.println("new ObjectControllerHistoryElement checksum: \t" + new BaseObjectControllerHistoryElement().getChecksum());
        System.out.println("new ObjectControllerHistoryElement checksum: \t" + new BaseObjectControllerHistoryElement().getChecksum());
    }
}
