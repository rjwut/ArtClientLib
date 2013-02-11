package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.eng.EngSetEnergyPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;

public class RawPacketDumper {

    public static final void main(String[] args) {
        
        String tgtIp = "10.211.55.3";
        final int tgtPort = 2010;
        
        final ThreadedArtemisNetworkInterface net; 
        try {
            net = new ThreadedArtemisNetworkInterface(tgtIp, tgtPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        PacketParser dummy = new PacketParser();
        dummy.setNoParseMode(true);
        
        net.setPacketParser(dummy);
        
        net.addOnPacketListener(new OnPacketListener() {
            
            @Override
            public void onPacket(ArtemisPacket pkt) {
                // filter out noise
                BaseArtemisPacket base = (BaseArtemisPacket) pkt;
                byte[] data = base.getData();
                if (base.getType() == ArtemisPacket.WORLD_TYPE
                        && data.length == 4 
                        && PacketParser.getLendInt(data) == 0)
                    return;
                
                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        
        net.send(new ReadyPacket2());
        net.send(new ReadyPacket2());
        
        //net.send(new SetStationPacket(StationType.SCIENCE, true));
        net.send(new SetStationPacket(StationType.ENGINEERING, true));
        net.send(new ReadyPacket());
        
        net.send(new ReadyPacket2());
        net.send(new EngSetEnergyPacket(SystemType.IMPULSE, 0));
        net.send(new ReadyPacket2());
    }
}
