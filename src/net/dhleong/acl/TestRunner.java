package net.dhleong.acl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.UnknownHostException;

import net.dhleong.acl.net.CommsIncomingPacket;
import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.EngGridUpdatePacket;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.EngSystemUpdatePacket;
import net.dhleong.acl.net.GameMessagePacket;
import net.dhleong.acl.net.ObjUpdatePacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.SetStationPacket;
import net.dhleong.acl.net.SetStationPacket.StationType;
import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.util.ShipSystemGrid;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

public class TestRunner {

    public static void main(String[] args) throws Exception {
        
        // configs
//        final String tgtIp = "localhost";
        String tgtIp = "10.211.55.4";
        final int tgtPort = 2010;
        
        // quick test
        int value = 1424;
        byte[] bytes = new byte[4];
        PacketParser.putLendInt(value, bytes);
        if (value != PacketParser.getLendInt(bytes))
            throw new Exception("putLendInt fails; got" + PacketParser.getLendInt(bytes));
        
        // test grid; also used with testing system damage later
        String sntFile = "/Users/dhleong/Documents/workspace/" +
                "ArtemisClient/res/raw/artemis";
        System.out.println("- Reading grid: " + sntFile);
        InputStream is = new FileInputStream(sntFile);
        final ShipSystemGrid grid = new ShipSystemGrid(is);
//        for (SystemType type : SystemType.values()) {
//            System.out.println("--+ " + type +": " + grid.getSystemCount(type));
//            for (GridCoord c : grid.getCoordsFor(type))
//                System.out.println("--+--+" + c);
//        }
        
//        // testing LRU
//        GridCoord.getInstance(3, 2, 7);
//        GridCoord.getInstance(99, 99, 99);
        
        
        PipedInputStream in = new PipedInputStream(100);
        PipedOutputStream out = new PipedOutputStream(in);
        
        SetStationPacket srcPkt = new SetStationPacket(StationType.ENGINEERING, true);
        srcPkt.write(out);
        ArtemisPacket destPkt = new PacketParser().readPacket(in);
        if (destPkt.getMode() != 0x02)
            throw new Exception("Wrong mode: " + destPkt.getMode());
        if (destPkt.getType() != SetStationPacket.TYPE)
            throw new Exception("Wrong type: " + Integer.toHexString(destPkt.getType()));
         
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

        final SystemManager mgr = new SystemManager();
        mgr.setSystemGrid(grid);
        
        net.addOnPacketListener(mgr);
        
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
                        create.debugPrint();
                        ArtemisObject obj = create.getCreatedObjects().get(0);
                        if (obj instanceof ArtemisPlayer) {
                            ArtemisPlayer plr = (ArtemisPlayer) obj;
                            for (SystemType s : SystemType.values()) {
                                float energy = plr.getSystemEnergy(s);
                                int coolant = plr.getSystemCoolant(s);
                                System.out.println("    \\_> " + s + ": " +
                                        coolant + " / " + energy);
                            }
                        }
                        System.out.println("--> " + create);
                        return;
                    } else if (EngSystemUpdatePacket.isExtensionOf(sys)) {
//                        EngSystemUpdatePacket eng = new EngSystemUpdatePacket(sys);
//                        eng.debugPrint();
                        return;
                    } else if (ObjUpdatePacket.isExtensionOf(sys)) {
                        System.out.println("** Update: " + mgr.getObject(sys.getTarget()));
                        ObjUpdatePacket up = new ObjUpdatePacket(sys);
                        up.debugPrint();
                        System.out.println("--> " + up);
                        return;
                    } else if (sys.getTargetType() == ArtemisObject.TYPE_PLAYER){
                        System.out.println("INFO << " + sys);
                        return;
                    }
                } else if (pkt instanceof CommsIncomingPacket) {
                    CommsIncomingPacket comms = (CommsIncomingPacket) pkt;
                    System.out.println("** From ``"+comms.getFrom()+"'': " + 
                            comms.getMessage());
                    System.out.println("--> " + comms);
                    return;
                } else if (pkt instanceof DestroyObjectPacket) {
                    DestroyObjectPacket destroy = (DestroyObjectPacket) pkt;
                    System.out.println("** DESTROYED: " + 
                            mgr.getObject(destroy.getTarget()));
                    return;
                } else if (pkt instanceof EngGridUpdatePacket) {
                    EngGridUpdatePacket dmg = (EngGridUpdatePacket) pkt;
                    System.out.println("** GRID UPDATE: ");
                    dmg.debugPrint();
                    System.out.println("Overall healths: ");
                    for (SystemType sys : SystemType.values()) {
                        System.out.println("- " + sys + ": " + 
                                mgr.getHealthOfSystem(sys));
                    }
                    return;
                } else if (pkt instanceof GameMessagePacket) {
                    GameMessagePacket msg = (GameMessagePacket) pkt;
                    if (msg.isGameOver())
                        System.out.println("*** GAME OVER!!! ***");
                    else if (msg.hasMessage()){
                        System.out.println("\nvvv MESSAGE vvv");
                        System.out.println(msg.getMessage());
                        System.out.println("^^^ MESSAGE ^^^\n");
                    } else {
                        System.out.println("!!! Unknown msg type...");
                    }
                    System.out.println("--> " + pkt);
                    return;
                }

                 // default
//                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        
        
//        // ENG test 
//        net.send(new SetStationPacket(StationType.ENGINEERING, true));
        
//        net.send(new EngSetEnergyPacket(SystemType.IMPULSE, .5f));
//        net.send(new EngSetCoolantPacket(SystemType.IMPULSE, 0));
        /*
        net.send(new EngSetEnergyPacket(SystemType.SENSORS, 0f));
        net.send(new EngSetCoolantPacket(SystemType.SENSORS, 1));
        
////        
////        net.send(new EngSetEnergyPacket(SystemType.JUMP, 100));
////        net.send(new EngSetCoolantPacket(SystemType.JUMP, 0));
//        
//        for (SystemType type : SystemType.values()) {
//            net.send(new EngSetEnergyPacket(type, 1f));
//            net.send(new EngSetCoolantPacket(type, 0));
//        }
        */
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));
        

//        net.stop();
    }
}
