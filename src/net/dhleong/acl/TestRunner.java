package net.dhleong.acl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.UnknownHostException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.NpcUpdatePacket;
import net.dhleong.acl.net.GameMessagePacket;
import net.dhleong.acl.net.GameStartPacket;
import net.dhleong.acl.net.GenericUpdatePacket;
import net.dhleong.acl.net.ObjectUpdatingPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.StationPacket;
import net.dhleong.acl.net.comms.AudioCommandPacket;
import net.dhleong.acl.net.comms.AudioCommandPacket.Command;
import net.dhleong.acl.net.comms.CommsIncomingPacket;
import net.dhleong.acl.net.comms.IncomingAudioPacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.player.PlayerUpdatePacket;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ShipSystemGrid;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.BaseArtemisShip;

public class TestRunner {

    public static void main(final String[] args) throws Exception {
        
        // configs
//        final String tgtIp = "localhost";
        final String tgtIp = "10.211.55.4";
        final int tgtPort = 2010;
        
        // quick test
        final int value = 1424;
        final byte[] bytes = new byte[4];
        PacketParser.putLendInt(value, bytes);
        if (value != PacketParser.getLendInt(bytes))
            throw new Exception("putLendInt fails; got" + PacketParser.getLendInt(bytes));
        
        // test grid; also used with testing system damage later
        final String sntFile = "/Users/dhleong/Documents/workspace/" +
                "ArtemisClient/res/raw/artemis";
        System.out.println("- Reading grid: " + sntFile);
        final InputStream is = new FileInputStream(sntFile);
        final ShipSystemGrid grid = new ShipSystemGrid(is);
        for (final ShipSystem system : ShipSystem.values()) {
            System.out.println("--+ " + system +": " + grid.getSystemCount(system));
            for (final GridCoord c : grid.getCoordsFor(system))
                System.out.println("--+--+" + c);
        }
        
//        // testing LRU
//        GridCoord.getInstance(3, 2, 7);
//        GridCoord.getInstance(99, 99, 99);
        
        
        final PipedInputStream in = new PipedInputStream(100);
        final PipedOutputStream out = new PipedOutputStream(in);
        
        final SetStationPacket srcPkt = new SetStationPacket(StationType.ENGINEERING, true);
        srcPkt.write(out);
        final ArtemisPacket destPkt = new PacketParser().readPacket(in);

        if (destPkt.getConnectionType() != ConnectionType.CLIENT) {
            throw new Exception("Wrong connection type: " + destPkt.getConnectionType());
        }

        if (destPkt.getType() != SetStationPacket.TYPE) {
            throw new Exception("Wrong type: " + Integer.toHexString(destPkt.getType()));
        }
         
        final ArtemisNetworkInterface net; 
        try {
            net = new ThreadedArtemisNetworkInterface(tgtIp, tgtPort);
        } catch (final UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }

        final SystemManager mgr = new SystemManager();
        mgr.setSystemGrid(grid);
        
        // this will be BEFORE the mgr updates
        net.addPacketListener(new Object() {
            private int noHull = -1;

            @PacketListener
            public void onPacket(final ArtemisPacket pkt) {
                if (pkt instanceof ObjectUpdatingPacket) {
                    final ObjectUpdatingPacket up = (ObjectUpdatingPacket) pkt;
//                    up.debugPrint();
                    boolean created = false;
                    for (final ArtemisObject obj : up.getObjects()) {
//                        ArtemisObject full = mgr.getObject(obj.getId());
//                        System.out.println(" + " + obj + " vel=" +
//                                ((ArtemisBearable)full)
//                                    .getVelocity());
                        final ArtemisObject old = mgr.getObject(obj.getId());
                        if (old == null) {
                            
                            if (obj.getType() == ObjectType.NPC_SHIP) {
                                System.out.println("Total created enemies=" + 
                                        (mgr.getObjects(ObjectType.NPC_SHIP).size()+1));
                                created = true;
                            }
                            
                            if (obj instanceof BaseArtemisShip 
                                    && ((BaseArtemisShip) obj).getName() == null) {
                            System.out.println("create w/o name!: " + 
//                                    Integer.toHexString(obj.getId())
                                    obj.getId()
                                    + " " + obj);
                            }
                        } else if (old instanceof BaseArtemisShip) {
                            final BaseArtemisShip ship = (BaseArtemisShip) old;
                            if (ship.getName() == null && ((BaseArtemisShip)obj).getName() != null)
                                System.out.println("** Update to missing name: " + obj);
                        }
                    }
                    
                    if (created) {
                        System.out.println("create!");
                        up.debugPrint();
                        System.out.println("--> " + up);
                    }
                }
                
                if (pkt instanceof NpcUpdatePacket) {
                    final NpcUpdatePacket up = (NpcUpdatePacket) pkt;
                    
                    for (final ArtemisPositionable obj : up.getObjects()) {
                        if (obj.getName() != null) {

//                            System.out.println("** Update: ");
//                            up.debugPrint();
//                            System.out.println("--> " + up);
                            
                            if (obj instanceof BaseArtemisShip) {
                                final BaseArtemisShip ship = (BaseArtemisShip) obj;
                                if (ship.getHullId() == -1 && noHull == -1) {
                                    noHull = ship.getId();
                                    final BaseArtemisShip filled = (BaseArtemisShip) mgr
                                            .getObject(ship.getId());
                                    if (filled == null || filled.getHullId() == -1) {
                                        System.out.println("\n---------");
                                        up.debugPrint();
                                        System.out.println("--> " + up);
                                        System.out.println("Ship[" + 
//                                                Integer.toHexString(ship.getId())
                                                ship.getId()
                                                +"] " + obj.getName() + " has no hull! old=" + filled);        
//                                        net.stop();
                                    }
                                }
                            }
                            
                            break;
//                        } else {
//                            net.stop();
                        }
                    }
                } else if (pkt instanceof DestroyObjectPacket) {
                    final DestroyObjectPacket destroy = (DestroyObjectPacket) pkt;
                    System.out.println("** " + destroy 
                            +":" +
                            mgr.getObject(destroy.getTarget()));
                    return;
                } 
            }
        });
        
        net.addPacketListener(mgr);
        
        net.addPacketListener(new Object() {
            protected int destroyedEnemies = 0;

            @PacketListener
            public void onPacket(final ArtemisPacket pkt) {
                
                if (pkt.getType() == 0xe548e74a) {
                    System.out.println("GOT SERVER VERSION");
//                    net.send(new ReadyPacket2());
                }
                    
                if (pkt instanceof StationPacket) {
//                    StationPacket create = (StationPacket) pkt;
//                    create.debugPrint();
//                    System.out.println("--> " + create);
                    return;
                } else if (pkt instanceof NpcUpdatePacket) {
//                    System.out.println("** Update: ");
//                    ObjectUpdatingPacket up = (ObjectUpdatingPacket) pkt;
////                    up.debugPrint();
//                    for (ArtemisObject obj : up.getObjects()) {
//                        ArtemisObject full = mgr.getObject(obj.getId());
//                        System.out.println(" + " + obj + " scan: " + ((ArtemisEnemy) obj).getScanLevel()
//                                + " ; " + ((ArtemisEnemy) full).getScanLevel());
////                        System.out.println(" + " + obj + " vel=" +
////                                ((ArtemisBearable)full)
////                                    .getVelocity());
//                    }
//                    
                    
                    return;
                } else if (pkt instanceof GenericUpdatePacket) {
//                    System.out.println("** Update: ");
//                    GenericUpdatePacket up = new GenericUpdatePacket(sys);
////                    up.debugPrint();
//                    for (ArtemisObject obj : up.mObjects)
//                        System.out.println(" + " + mgr.getObject(obj.getId()));
//                    System.out.println("--> " + up);
//                    return;

                } else if (pkt instanceof PlayerUpdatePacket) {
                    final PlayerUpdatePacket up = (PlayerUpdatePacket) pkt;
//                    ArtemisPlayer plr = (ArtemisPlayer) mgr.getObject(up.getPlayer().getId());
                    
//                    for (int i=0; i<ArtemisPlayer.MAX_TUBES; i++) {
//                        System.out.println(String.format("Tube#%d: (%f) %d", 
//                            i, plr.getTubeCountdown(i), plr.getTubeContents(i)));
//                    }
//
//                    if (up instanceof WeapPlayerUpdatePacket) 
//                        up.debugPrint();
//                    System.out.println("Player: " + plr);
                    
//                    for (SystemType s : SystemType.values()) {
//                        float heat = plr.getSystemHeat(s);
//                        float energy = plr.getSystemEnergy(s);
//                        int coolant = plr.getSystemCoolant(s);
//                        System.out.println("    \\_> " + s + ": " +
//                                coolant + " / " + energy + " :: " + heat);
//                    }
//
//                    System.out.println("Drive: " + plr.getDriveType());
//                    System.out.println("Revrs: " + plr.getReverseState());

//                    if (up instanceof EngPlayerUpdatePacket) {
//                        SystemType s = SystemType.MANEUVER;
//                        float energy = plr.getSystemEnergy(s);
//                        int coolant = plr.getSystemCoolant(s);
//                        System.out.println("    \\_> " + s + ": " +
//                                coolant + " / " + energy);// + " :: " + heat);
//                    }
//                    System.out.println("TR=" + plr.getTurnRate());
                    
                    
//                    System.out.println("Be=" + plr.getBearing());
//                    System.out.println("St=" + plr.getSteering());
//                    System.out.println("Mn=" + plr.getSystemEnergy(SystemType.MANEUVER));
//                    if (up instanceof WeapPlayerUpdatePacket) 
                        System.out.println("--> " + up);
//                    net.stop();
                    return;

                } else if (pkt instanceof CommsIncomingPacket) {
                    final CommsIncomingPacket comms = (CommsIncomingPacket) pkt;
                    System.out.println("** From ``"+comms.getFrom()+"'': " + 
                            comms.getMessage());
                    System.out.println("--> " + comms);
                    return;
                } else if (pkt instanceof IncomingAudioPacket) {
                    final IncomingAudioPacket audio = (IncomingAudioPacket) pkt;
                    System.out.println(String.format("** Incoming[%d]: %s", 
                            audio.getAudioId(),
                            audio.getTitle()));
                    if (audio.isIncoming()) {
                        System.out.println("Requesting playback...");
                        net.send(new AudioCommandPacket(audio.getAudioId(), Command.PLAY));
                    }
                    return;
                } else if (pkt instanceof EngGridUpdatePacket) {
                    final EngGridUpdatePacket dmg = (EngGridUpdatePacket) pkt;
//                    System.out.println("** GRID UPDATE: ");
//                    dmg.debugPrint();
//                    System.out.println("Overall healths: ");
//                    for (SystemType sys : SystemType.values()) {
//                        System.out.println("- " + sys + ": " + 
//                                mgr.getHealthOfSystem(sys));
//                    }
                    System.out.println("--> eng " + dmg);
                    return;
                } else if (pkt instanceof GameMessagePacket) {
                    final GameMessagePacket msg = (GameMessagePacket) pkt;
                    if (msg.isGameOver()) {
                        System.out.println("*** GAME OVER!!! ***");
//                        net.send(new ReadyPacket());
                    } else if (msg.hasMessage()){
                        System.out.println("\nvvv MESSAGE vvv");
                        System.out.println(msg.getMessage());
                        System.out.println("^^^ MESSAGE ^^^\n");
                    } else {
                        System.out.println("!!! Unknown msg type...");
                    }
                    System.out.println("--> " + pkt);
                    return;
                } else if (pkt instanceof DestroyObjectPacket) {
                    if (((DestroyObjectPacket)pkt).getTargetType() 
                            == ObjectType.NPC_SHIP) {
                        destroyedEnemies++;
                        System.out.println("Total enemies destroyed=" + destroyedEnemies);
                    }
                    return;
                } else if (pkt instanceof GameStartPacket) {
                    final GameStartPacket start = (GameStartPacket) pkt;
                    start.debugPrint();
//                    net.stop();
                    return;
                }

                 // default
                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        //net.send(new SetShipPacket(SetShipPacket.SHIP_1_ARTEMIS));
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
        
//        net.send(new SetShipPacket(6));
//        String name = "Hera2";
//        int nameLen = PacketParser.getNameLengthBytes(name);
//        byte[] data = new byte[4];
//        PacketParser.putLendInt(0x13, data);
////        PacketParser.putLendInt(0, data, 4);
////        PacketParser.putLendInt(0, data, 8);
////        PacketParser.putNameString(name, data, 12);
//////        PacketParser.putLendInt(0, data, 12);
//        BaseArtemisPacket pkt = new BaseArtemisPacket(0x02, 0x26, ArtemisPacket.SHIP_ACTION_TYPE, data);
//        net.send(pkt);
//        net.send(new SetShipPacket(SetShipPacket.SHIP_1_ARTEMIS));
        
        
        net.send(new ReadyPacket2());
        net.send(new ReadyPacket2());
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));
        //net.send(new SetShipSettingsPacket(DriveType.JUMP, 1, "USS Awesome"));
        net.send(new ReadyPacket());
        
        net.send(new ReadyPacket2());
        
//        net.send(new HelmToggleReversePacket());
        
        
//        net.send(new EngSetEnergyPacket(SystemType.TORPEDOS, 100));
//        net.send(new LoadTubePacket(1, LoadTubePacket.TORP_MINE));
//        net.send(new UnloadTubePacket(0));
        
//        net.send(new ToggleShieldsPacket());
        
//        net.send(new SetMainScreenPacket(MainScreen.RIGHT));
        
//        Thread.sleep(2000);
//        net.send(new HelmJumpPacket(.5f, .5f));
        
//        net.send(new HelmSetWarpPacket(2));
//        net.stop();
    }
}
