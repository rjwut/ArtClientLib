package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;

import net.dhleong.acl.net.PacketParser;

public class RawPacketDumper {

    public static final void main(String[] args) {
        
        String tgtIp = "10.211.55.3";
        final int tgtPort = 2010;
        
        final ThreadedArtemisNetworkInterface net; 
        try {
            net = new ThreadedArtemisNetworkInterface(tgtIp, tgtPort);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        
        PacketParser dummy = new PacketParser();
        dummy.setNoParseMode(true);
        
        net.setPacketParser(dummy);
        
        net.addOnPacketListener(new OnPacketListener() {
            
            @Override
            public void onPacket(ArtemisPacket pkt) {
                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        
//        net.send(new SetStationPacket(station, isSelected))
    }
}
