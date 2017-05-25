package stretl.etl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import stretl.common.CommandTuple;
import stretl.common.Enums;
import stretl.network.NetworkTupleWriter;

/**
 * Aplikacja modułu ETL-RT.
 * @author Artur Drzeniek
 */
public class ETL {
        
    static NetworkTupleWriter networkWriter;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        networkWriter = new NetworkTupleWriter();
                
        UpdateObj updateObj = new UpdateObj();
        boolean commandSend = false;
        
        EtlModule module = createModule(updateObj);           
        module.start();
        
        System.out.println("Type 'true' and hit Enter to run process. Type false to interrupt.");
        while(true)
        {
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            if (br.ready()) 
            {            
                String cmd = br.readLine();
                if (!cmd.isEmpty()) 
                {
                    updateObj.continueFlag = Boolean.parseBoolean(cmd);
                    if (updateObj.continueFlag == false)
                    {
                        module.interrupt();
                        updateObj.startMiliseconds = System.currentTimeMillis();
                    }
                    else
                    {
                        module = createModule(updateObj);
                        module.start();
                    }
                }
            }
            else if (!module.isAlive())
            {
                System.out.println("Module is not alive! Trying to restart...");
                updateObj.continueFlag = true;
                module = createModule(updateObj);
                module.start();
            }   
            else
            {
                LocalDateTime sendTime = LocalDateTime.now();
                if (!commandSend) {

                    // Pobiera krotkę komendy z wejścia
                    DatagramSocket inputSocket = module.getInput().getValue();
                    CommandTuple command = new CommandTuple(
                            inputSocket.getLocalSocketAddress(),
                            Enums.ModuleCommandType.PING,
                            sendTime
                    );

                    // Przesyła odpowiedz spowrotem do modułu z ktorego to przyszło.
                    SocketAddress address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 60200);
                    commandSend = networkWriter.sendModuleCommand(command, inputSocket, address);
                }

                if (commandSend) {
                    // 10 razy sprawdzane jest czy nadeszła komenda. Pętla nie blokująca.
                    for (int i = 0; i < 10; i++) {
                        if (module.networkReader != null && module.networkReader.moduleCommands != null)
                        {    
                            // Metoda nie blokująca
                            CommandTuple retCommand = module.networkReader.moduleCommands.poll();
                            if (retCommand != null)
                            {//nalezy przebadac czy nowy command tuple działa prawidłowo.
                                long roznica = LocalDateTime.now().toLocalTime().toNanoOfDay() - sendTime.toLocalTime().toNanoOfDay();
                                // Aktualizacja pingu w obiekcie update.
                                updateObj.actualPing = roznica / 1000000;
                                //Logger.getLogger(ETL.class.getName()).log(Level.INFO, "{0}", updateObj.actualPing);
                                break;
                            }
                            else
                            { // Gdy nie ma wiadomosci, czeka 5 ms i sprawdza ponownie
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(ETL.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                   
                    commandSend = false;
                }
                       
                try {
                    //Logger.getLogger(ETL.class.getName()).log(Level.INFO, "Nutin to do goin' sleep.");
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ETL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    static EtlModule createModule(UpdateObj updateObj) {
        EtlModule module = new EtlModule(0, updateObj);
        module.Init();
        Logger.getLogger(ETL.class.getName()).log(Level.INFO, "Module started.");
        
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 60300);
        module.createInput(socketAddress);
        Logger.getLogger(ETL.class.getName()).log(Level.INFO, "Input created.");
        return module;
    }
}
