package generator.controller;

import generator.commons.Serializer;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serwer obierania danych. Wykorzystywany do tworzenia puli wątków
 * nasłuchujących na zadanych portach i wyświetlania odebranych danych na
 * standardowym wyjsciu systemowym (konsola).
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class UDPServer {

    private static final int STARTING_PORT = 60000;
    private static final int THREADS = 12;

    /**
     * Serwer odbierania danych. Tworzy pool wątków, które nasłuchują na
     * zadanych portach.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++) {
            Runnable worker = new SingleReciever(STARTING_PORT + i);            
            executor.execute(worker);
        }
        executor.shutdown();
        System.out.println(THREADS + " running and listening.");        
        System.out.println("Threads run on their own for now");
    }
}

/**
 * Klasa pojedynczego odbiorcy danych.
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
class SingleReciever implements Runnable {
    
    protected final Integer port;
    protected DatagramSocket datagramSocket;
    protected final SocketAddress socketAddress;
    protected Integer objectID;

    public SingleReciever(Integer port) {
        this.port = port;
        this.socketAddress = new InetSocketAddress("localhost", port);
        System.out.println("Single reciever bind to port: " + port);
        try {
            this.datagramSocket = new DatagramSocket(this.socketAddress);
        } catch (SocketException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void run() {
        try {
            byte[] serializedMessage = new byte[512];
            while (true) {
                DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length);
                datagramSocket.receive(packet);
                Object[] message = (Object[]) Serializer.deserialize(serializedMessage);
                System.out.println(Arrays.toString(message));
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SingleReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
