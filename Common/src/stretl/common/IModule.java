/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stretl.common;

import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Artur
 */
public interface IModule {
    public Entry<LinkedBlockingQueue<Tuple>, DatagramSocket> getInput();
    public Integer getInputPort();
}
