package generator.controller;

import generator.commons.Serializer;
import generator.commons.exception.CheckedObjectIsNotRegisteredException;
import generator.commons.exception.CouldNotRegisterNullObjectException;
import generator.model.FuelTank;
import generator.model.Nozzle;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generator danych paliwowych.
 * Główna klasa generatora danych paliwowych. Tworzy wątki pełniące funkcje
 * pistoletów paliwowych i zbiorników. Prywatna klasa jest odpowiedzialna za
 * stworzenie połączenia na odpowiednim porcie i nadawanie danych.
 * Przed wysłaniem na określony port, kontroler (symulator) modyfikuje dane.
 *
 * @author Artur Drzeniek <artudrz156@student.polsl.pl>
 */
public class Generator {

    private static final Logger log = Logger.getLogger(Generator.class.getName());
    private static final int STARTING_PORT = 60000;
    private static final int THREADS = 12;
    private static final double FUELTANK_CAPACITY = 10000;
    private static int SCHEDULE_INTERVAL = 500;
    public  static final ObjectController objectController = new ObjectControllerImpl();

    /**
     * Metoda główna tworząca pulę wątków nadawczych.
     * Inicjuje przygotowanie obiektów i tworzy oraz uruchamia pulę wątków.
     *
     * @param args Not used.
     * @throws generator.commons.exception.CouldNotRegisterNullObjectException
     */
    public static void main(String[] args) throws CouldNotRegisterNullObjectException {
        if (args.length > 0) {
            try {
                SCHEDULE_INTERVAL = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + "must be an integer.");
                System.exit(1);
            }
        }

        //ArrayList<Object> objects = initObjects1();
        ArrayList<Object> objects = initObjects2();
        //ArrayList<Object> objects = initObjects3();

        for (Object o : objects) {
            if (!objectController.registerObject(o)){
                log.log(Level.SEVERE, "Nie udalo sie zarejestrowac obiektu.", o);
                return;
            }
        }
        //----------------------- Pula wątków --------------------------------//
        log.log(Level.INFO, "Uruchamiam pulę wątków.");
        int threads = objects.size();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            Runnable worker = new SingleSender(STARTING_PORT + i, objects.get(i));
            executor.scheduleWithFixedDelay(worker, 0, SCHEDULE_INTERVAL, TimeUnit.MILLISECONDS);
        }
        //--------------------------------------------------------------------//
    }
    /**
     * Tworzenie konfiguracji.
     * @return Listę obiektów do uruchomienia.
     */
    private static ArrayList<Object> initObjects1() {
        ArrayList<Object> array = new ArrayList<>();
        FuelTank fuelTank_1 = new FuelTank(STARTING_PORT + 0, FUELTANK_CAPACITY);
        array.add(fuelTank_1);
        array.add(new Nozzle(STARTING_PORT + 1, 0.0, fuelTank_1));
        array.add(new Nozzle(STARTING_PORT + 2, 0.0, fuelTank_1));
        FuelTank fuelTank_2 = new FuelTank(STARTING_PORT + 3, FUELTANK_CAPACITY);
        array.add(fuelTank_2);
        array.add(new Nozzle(STARTING_PORT + 4, 0.0, fuelTank_2));
        array.add(new Nozzle(STARTING_PORT + 5, 0.0, fuelTank_2));

        return array;
    }

    private static ArrayList<Object> initObjects2() {
        ArrayList<Object> array = new ArrayList<>();

        FuelTank fuelTank_1 = new FuelTank(STARTING_PORT + 0, FUELTANK_CAPACITY);
        array.add(fuelTank_1);
        array.add(new Nozzle(STARTING_PORT + 1, 0.0, fuelTank_1));
        array.add(new Nozzle(STARTING_PORT + 2, 0.0, fuelTank_1));

        FuelTank fuelTank_2 = new FuelTank(STARTING_PORT + 3, FUELTANK_CAPACITY);
        array.add(fuelTank_2);
        array.add(new Nozzle(STARTING_PORT + 4, 0.0, fuelTank_2));
        array.add(new Nozzle(STARTING_PORT + 5, 0.0, fuelTank_2));
        array.add(new Nozzle(STARTING_PORT + 6, 0.0, fuelTank_2));

        FuelTank fuelTank_3 = new FuelTank(STARTING_PORT + 7, FUELTANK_CAPACITY);
        array.add(fuelTank_3);
        array.add(new Nozzle(STARTING_PORT + 8, 0.0, fuelTank_3));
        array.add(new Nozzle(STARTING_PORT + 9, 0.0, fuelTank_3));

        FuelTank fuelTank_4 = new FuelTank(STARTING_PORT + 10, FUELTANK_CAPACITY);
        array.add(fuelTank_4);
        array.add(new Nozzle(STARTING_PORT + 11, 0.0, fuelTank_4));
        array.add(new Nozzle(STARTING_PORT + 12, 0.0, fuelTank_4));
        array.add(new Nozzle(STARTING_PORT + 13, 0.0, fuelTank_4));

        FuelTank fuelTank_5 = new FuelTank(STARTING_PORT + 14, FUELTANK_CAPACITY);
        array.add(fuelTank_5);
        array.add(new Nozzle(STARTING_PORT + 15, 0.0, fuelTank_5));
        array.add(new Nozzle(STARTING_PORT + 16, 0.0, fuelTank_5));
        array.add(new Nozzle(STARTING_PORT + 17, 0.0, fuelTank_5));
        array.add(new Nozzle(STARTING_PORT + 18, 0.0, fuelTank_5));
        array.add(new Nozzle(STARTING_PORT + 19, 0.0, fuelTank_5));

        return array;
    }

    private static ArrayList<Object> initObjects3() {
        ArrayList<Object> array = new ArrayList<>();
        FuelTank fuelTank_1 = new FuelTank(STARTING_PORT + 0, FUELTANK_CAPACITY);
        FuelTank fuelTank_2 = new FuelTank(STARTING_PORT + 5, FUELTANK_CAPACITY);
        FuelTank fuelTank_3 = new FuelTank(STARTING_PORT + 10, FUELTANK_CAPACITY);
        FuelTank fuelTank_4 = new FuelTank(STARTING_PORT + 15, FUELTANK_CAPACITY);
        FuelTank fuelTank_5 = new FuelTank(STARTING_PORT + 20, FUELTANK_CAPACITY);
        FuelTank fuelTank_6 = new FuelTank(STARTING_PORT + 30, FUELTANK_CAPACITY);

        array.add(fuelTank_1);
        array.add(new Nozzle(fuelTank_1.getId() + 1, 0.0, fuelTank_1));
        array.add(fuelTank_2);
        array.add(new Nozzle(fuelTank_2.getId() + 1, 0.0, fuelTank_2));
        array.add(new Nozzle(fuelTank_2.getId() + 2, 0.0, fuelTank_2));
        array.add(fuelTank_3);
        array.add(new Nozzle(fuelTank_3.getId() + 1, 0.0, fuelTank_3));
        array.add(new Nozzle(fuelTank_3.getId() + 2, 0.0, fuelTank_3));
        array.add(new Nozzle(fuelTank_3.getId() + 3, 0.0, fuelTank_3));
        array.add(fuelTank_4);
        array.add(new Nozzle(fuelTank_4.getId() + 1, 0.0, fuelTank_4));
        array.add(new Nozzle(fuelTank_4.getId() + 2, 0.0, fuelTank_4));
        array.add(new Nozzle(fuelTank_4.getId() + 3, 0.0, fuelTank_4));
        array.add(new Nozzle(fuelTank_4.getId() + 4, 0.0, fuelTank_4));
        array.add(fuelTank_5);
        array.add(new Nozzle(fuelTank_5.getId() + 1, 0.0, fuelTank_5));
        array.add(new Nozzle(fuelTank_5.getId() + 2, 0.0, fuelTank_5));
        array.add(new Nozzle(fuelTank_5.getId() + 3, 0.0, fuelTank_5));
        array.add(new Nozzle(fuelTank_5.getId() + 4, 0.0, fuelTank_5));
        array.add(new Nozzle(fuelTank_5.getId() + 5, 0.0, fuelTank_5));
        array.add(fuelTank_6);
        array.add(new Nozzle(fuelTank_6.getId() + 1, 0.0, fuelTank_6));
        array.add(new Nozzle(fuelTank_6.getId() + 2, 0.0, fuelTank_6));
        array.add(new Nozzle(fuelTank_6.getId() + 3, 0.0, fuelTank_6));
        array.add(new Nozzle(fuelTank_6.getId() + 4, 0.0, fuelTank_6));
        array.add(new Nozzle(fuelTank_6.getId() + 5, 0.0, fuelTank_6));
        array.add(new Nozzle(fuelTank_6.getId() + 6, 0.0, fuelTank_6));

        return array;
    }
}

class SingleSender implements Runnable {

    private static final Logger log = Logger.getLogger(SingleSender.class.getName());
    private String name;
    private Integer port;
    private Object sender;      // Tym obiektem może być Nozzle lub FuelTank
    private DatagramSocket datagramSocket;
    private SocketAddress socketAddress;
    private ObjectController objectController = Generator.objectController;

    public SingleSender(Integer port, Object sender) {
        this.name = sender.getClass().getSimpleName();
        this.port = port;
        this.sender = sender;
        this.socketAddress = new InetSocketAddress("localhost", port);
        try {
            this.datagramSocket = new DatagramSocket();
            log.log(Level.INFO, "Single sender bind to port: {0}", port);
        }
        catch (SocketException e) {
            log.log(Level.WARNING, null, e);
        }
    }

    @Override
    public void run() {
        if (sender instanceof FuelTank) { // Wątek zawiera obiekt typu Zbiornik paliwa.
            try {

                if (objectController.doesObjectChanged(sender)) {
                    byte[] serializedMessage = Serializer.serialize(((FuelTank) sender).sendTuple());
                    DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, socketAddress);
                    datagramSocket.send(packet);

                    //log.log(Level.SEVERE, "Fuel Tank {0} send tuple in packet with size {1}",
                    //        new Object[]{((FuelTank) sender).getId(), packet.getLength()});
                }
            } catch (IOException | CheckedObjectIsNotRegisteredException e) {
                log.log(Level.WARNING, null, e);
            }
        }
        else if (sender instanceof Nozzle) { // Wątek zawiera obiekt typu Pistolet paliwa.
            try {
                objectController.impactObject(sender);

                if (objectController.doesObjectChanged(sender)) {
                    byte[] serializedMessage = Serializer.serialize(((Nozzle) sender).sendTuple());
                    DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, socketAddress);
                    datagramSocket.send(packet);

                    //log.log(Level.SEVERE, "Nozzle {0} send tuple in packet with size {1}",
                    //        new Object[]{((Nozzle) sender).getId(), packet.getLength()});
                }
            } catch (IOException | CheckedObjectIsNotRegisteredException e) {
                log.log(Level.WARNING, null, e);
            }
        }
    }
}
