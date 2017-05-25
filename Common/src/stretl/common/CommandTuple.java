/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stretl.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.time.LocalDateTime;

/**
 *
 * @author Artur
 */
public class CommandTuple extends Tuple {
    
    public CommandTuple() {}
    
    public CommandTuple(SocketAddress sa, Enums.ModuleCommandType ct,
            LocalDateTime sendTime, Integer source,
            LocalDateTime dataStart, LocalDateTime dataEnd) 
    {
        this.add(sa);
        this.add(ct);
        this.add(sendTime);
        this.add(source);
        this.add(dataStart);
        this.add(dataEnd);
    }
    
    public CommandTuple(SocketAddress sa, Enums.ModuleCommandType ct,
            LocalDateTime sendTime) 
    {
        this.add(sa);
        this.add(ct);
        this.add(sendTime);
        this.add(null);
        this.add(null);
        this.add(null);
    }
    
    public SocketAddress getSourceAddress() {
        if (this.get(0) != null && this.get(0) instanceof SocketAddress)
            return (SocketAddress)this.get(0);
        else
            return null;
    }
    
    public Enums.ModuleCommandType getCommandType()
    {
        if (this.get(1) != null && this.get(1) instanceof Enums.ModuleCommandType)
            return (Enums.ModuleCommandType)this.get(1);
        else
            return null;
    }
    
    public LocalDateTime getSendTime() {
        if (this.get(2) != null && this.get(2) instanceof LocalDateTime)
            return (LocalDateTime)this.get(2);
        else
            return null;
    }

    @Override
    public Integer getSource() {
        if (this.get(3) != null && this.get(3) instanceof Integer)
            return (Integer)this.get(3);
        else
            return null;
    }
    
    public LocalDateTime getDataStartTime() {
        if (this.get(4) != null && this.get(4) instanceof LocalDateTime)
            return (LocalDateTime)this.get(4);
        else
            return null;
    }
    
    public LocalDateTime getDataEndTime() {
        if (this.get(5) != null && this.get(5) instanceof LocalDateTime)
            return (LocalDateTime)this.get(5);
        else
            return null;
    }
    
    public void setSourceAddress(SocketAddress sa) {
        if (this.size() > 0)
            this.set(0, sa);
        else
            this.add(0, sa);
    }
    
    public void setCommandType(Enums.ModuleCommandType ct) {
        if (this.size() > 1)
            this.set(1, ct.ordinal());
        else
            this.add(1, ct);
    }
    
    public void setSendTime(LocalDateTime ldt) {
        if (this.size() > 1)
            this.set(2, ldt);
        else
            this.add(2, ldt);
    }
    
    
    
}
