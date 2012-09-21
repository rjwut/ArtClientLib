package net.dhleong.acl;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.UnknownHostException;

import net.dhleong.acl.net.CommsIncomingPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.SetStationPacket;
import net.dhleong.acl.net.SetStationPacket.StationType;
import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;

public class TestRunner {

    public static void main(String[] args) throws Exception {
        
        // quick test
        int value = 1424;
        byte[] bytes = new byte[4];
        PacketParser.putLendInt(value, bytes);
        if (value != PacketParser.getLendInt(bytes))
            throw new Exception("putLendInt fails; got" + PacketParser.getLendInt(bytes));
        
        PipedInputStream in = new PipedInputStream(100);
        PipedOutputStream out = new PipedOutputStream(in);
        
        SetStationPacket srcPkt = new SetStationPacket(StationType.ENGINEERING, true);
        srcPkt.write(out);
        ArtemisPacket destPkt = new PacketParser().readPacket(in);
        if (destPkt.getMode() != 0x02)
            throw new Exception("Wrong mode: " + destPkt.getMode());
        if (destPkt.getType() != SetStationPacket.TYPE)
            throw new Exception("Wrong type: " + Integer.toHexString(destPkt.getType()));
         
        String tgtIp = "localhost";
        int tgtPort = 2010;
        final ArtemisNetworkInterface net; 
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
        
        net.addOnPacketListener(new OnPacketListener() {
            
            @Override
            public void onPacket(ArtemisPacket pkt) {
                if (pkt instanceof SystemInfoPacket &&
                        ((SystemInfoPacket)pkt).isEmpty)
                    return;
                
//                if (pkt instanceof SystemInfoPacket)
//                    return; // ignore system info packets for now
                    
                if (pkt instanceof SystemInfoPacket) {
                    SystemInfoPacket sys = (SystemInfoPacket) pkt;
                    if (SysCreatePacket.isExtensionOf(sys)) {
                        SysCreatePacket create = new SysCreatePacket(sys);
//                        create.debugPrint();
                        return;
                    }
                } else if (pkt instanceof CommsIncomingPacket) {
                    CommsIncomingPacket comms = (CommsIncomingPacket) pkt;
                    System.out.println("** From ``"+comms.getFrom()+"'': " + 
                            comms.getMessage());
                    return;
                }
//                        EngSystemUpdatePacket.isExtensionOf((SystemInfoPacket)pkt))  {
//                    EngSystemUpdatePacket eng = new EngSystemUpdatePacket(
//                            (SystemInfoPacket)pkt);
////                    System.out.println("** Energy:" + eng.mShipEnergy + 
//////                            "; SysDamg:" + eng.mSystemDamage +
////                            "; SysHeat:" + eng.mSystemHeat);
////                    System.out.println("** --> " + eng);
//                    eng.debugPrint();
                
                 // default
//                System.out.println("<< " + pkt);
            }
        });
        
        SystemManager mgr = new SystemManager();
        net.addOnPacketListener(mgr);
        
        net.start();
        
        /* ENG test 
        net.send(new SetStationPacket(StationType.ENGINEER, true));
//        net.send(new EngSetEnergyPacket(SystemType.IMPULSE, 1f));
//        net.send(new EngSetCoolantPacket(SystemType.IMPULSE, 0));
//        
//        net.send(new EngSetEnergyPacket(SystemType.JUMP, 100));
//        net.send(new EngSetCoolantPacket(SystemType.JUMP, 0));
        
        for (SystemType type : SystemType.values()) {
            net.send(new EngSetEnergyPacket(type, 0f));
            net.send(new EngSetCoolantPacket(type, 0));
        }
        */
        
        net.send(new SetStationPacket(StationType.COMMS, true));
        

//        net.stop();
    }
}
