/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stretl.common;

/**
 *
 * @author Artur
 */
public class Enums {
    
    /**
     * Stan kanału przetwarzania.
     */
    public static enum ChannelState {
        UNKNOWN,
        WAITING,
        WORKING,
        STOPED
    }
    
    public static enum MeterType {
        TANK,
        NOZZLE;
                
        public static MeterType fromInteger(int x) {
            switch(x) {
                case 60000: return TANK;
                case 60001: return NOZZLE;
            }
            return null;
        }
    }
    
    public static enum ModuleCommandType {
        GET_TUPLES,
        PING;
        
        public static ModuleCommandType fromInteger(int x)
        {
            switch(x) {
                case 0: return GET_TUPLES;
                case 1: return PING;
            }
            return null;
        }
    }
}
