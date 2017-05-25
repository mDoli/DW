package generator.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Własny moduł serializacji  i deserializacji.
 * Serializuje obiekty w tablice i odwrotnie.
 * 
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class Serializer {
    
    /**
     * Serializuje obiekt w tablicę bajtową.
     * 
     * @param obj Objekt do serializowania.
     * @return Tablica bajtowa.
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    /**
     *
     * @param bytes Objekt jako tablica bajtowa.
     * @return Objekt zdeserializowany z tablicy bajtowej.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }
}
