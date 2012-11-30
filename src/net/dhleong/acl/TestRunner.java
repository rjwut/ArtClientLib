package net.dhleong.acl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.UnknownHostException;

import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.GameMessagePacket;
import net.dhleong.acl.net.GenericUpdatePacket;
import net.dhleong.acl.net.ObjUpdatePacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.PlayerUpdatePacket;
import net.dhleong.acl.net.StationPacket;
import net.dhleong.acl.net.comms.CommsIncomingPacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.SetShipPacket;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ShipSystemGrid;
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
        for (SystemType type : SystemType.values()) {
            System.out.println("--+ " + type +": " + grid.getSystemCount(type));
            for (GridCoord c : grid.getCoordsFor(type))
                System.out.println("--+--+" + c);
        }
        
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
                
//                if (pkt instanceof SystemInfoPacket)
//                    return; // ignore system info packets for now
                    
                if (pkt instanceof StationPacket) {
                    StationPacket create = (StationPacket) pkt;
                    create.debugPrint();
                    System.out.println("--> " + create);
                    return;
                } else if (pkt instanceof ObjUpdatePacket) {
//                    System.out.println("** Update: ");
//                    ObjUpdatePacket up = (ObjUpdatePacket) pkt;
//                    //                        up.debugPrint();
//                    for (ArtemisObject obj : up.mObjects) {
//                        ArtemisObject full = mgr.getObject(obj.getId());
//                        System.out.println(" + " + obj + " vel=" +
//                                ((ArtemisBearable)full)
//                                    .getVelocity());
//                    }
//                    System.out.println("--> " + up);
                    return;

                } else if (pkt instanceof GenericUpdatePacket) {
//                    System.out.println("** Update: ");
//                    GenericUpdatePacket up = new GenericUpdatePacket(sys);
////                    up.debugPrint();
//                    for (ArtemisObject obj : up.mObjects)
//                        System.out.println(" + " + mgr.getObject(obj.getId()));
//                    System.out.println("--> " + up);
                    return;

                } else if (pkt instanceof PlayerUpdatePacket) {
                    PlayerUpdatePacket up = (PlayerUpdatePacket) pkt;
                    ArtemisPlayer plr = (ArtemisPlayer) mgr.getObject(up.getPlayer().getId());
                    
//                    for (int i=0; i<ArtemisPlayer.MAX_TUBES; i++) {
//                        System.out.println(String.format("Tube#%d: (%f) %d", 
//                            i, plr.getTubeCountdown(i), plr.getTubeContents(i)));
//                    }
//
//                    up.debugPrint();
//                    System.out.println("Player: " + plr);
//                    for (SystemType s : SystemType.values()) {
//                        float heat = plr.getSystemHeat(s);
//                        float energy = plr.getSystemEnergy(s);
//                        int coolant = plr.getSystemCoolant(s);
//                        System.out.println("    \\_> " + s + ": " +
//                                coolant + " / " + energy + " :: " + heat);
//                    }
//

                    System.out.println("Be=" + plr.getBearing());
                    System.out.println("St=" + plr.getSteering());
                    System.out.println("Mn=" + plr.getSystemEnergy(SystemType.MANEUVER));
                    System.out.println("--> " + up);
                    return;

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
//                    EngGridUpdatePacket dmg = (EngGridUpdatePacket) pkt;
//                    System.out.println("** GRID UPDATE: ");
//                    dmg.debugPrint();
//                    System.out.println("Overall healths: ");
//                    for (SystemType sys : SystemType.values()) {
//                        System.out.println("- " + sys + ": " + 
//                                mgr.getHealthOfSystem(sys));
//                    }
//                    System.out.println("--> " + dmg);
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
                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        net.send(new SetShipPacket(SetShipPacket.SHIP_1_ARTEMIS));
//        net.send(new SetShipPacket(SetShipPacket.SHIP_2_INTREPID));
//        net.send(new SetShipPacket(SetShipPacket.SHIP_3_AEGIS));
        
        
//        // ENG test 
//        net.send(new SetStationPacket(StationType.ENGINEERING, true));
//        Thread.sleep(2000);
//        net.send(new EngSetAutoDamconPacket(false));
        
//        net.send(new EngSendDamconPacket(0,  2,1,9));
//        net.send(new EngSendDamconPacket(1,  2,1,9));
//        net.send(new EngSendDamconPacket(2,  2,1,9));
        
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
        
//        net.send(new SetStationPacket(StationType.HELM, true));
//        net.send(new SetShipSettingsPacket(DriveType.JUMP, 1, "USS Awesome"));
        
//        net.send(new SetStationPacket(StationType.WEAPONS, true));
//        net.send(new LoadTubePacket(1, 1));
//        net.send(new UnloadTubePacket(1));
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));

        net.send(new ReadyPacket());
//        net.send(new ToggleShieldsPacket());
        
//        net.send(new SetMainScreenPacket(MainScreen.RIGHT));
        
//        Thread.sleep(2000);
//        net.send(new HelmJumpPacket(.5f, .5f));
        
//        net.send(new HelmSetWarpPacket(2));
//        net.stop();
    }
}
