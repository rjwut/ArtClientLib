package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;

import net.dhleong.acl.net.PlayerUpdatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.BaseArtemisShip;

/**
 * Connects to a server and listens for
 *  packets that were incorrectly parsed,
 *  stopping when it finds one
 *  
 * @author dhleong
 *
 */
public class BadParseDetectingRunner {

    public static void main(String[] args) {
        String tgtIp = "10.211.55.4";
        final int tgtPort = 2010;
        
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
                    
                    if (PlayerUpdatePacket.isExtensionOf(sys)) {
                        PlayerUpdatePacket up = null;
                        try {
                            up = new PlayerUpdatePacket(sys);
                            
                            ArtemisPlayer p = up.getPlayer();
                            testPlayer(p);
                            
                        } catch (RuntimeException e) {
                            up.debugPrint();
                            System.out.println("--> " + up);
                            net.stop();
                            throw e;
                        }
                    }
                }
            }
        });
        
        net.start();
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));
        
    }
    
    public static void testPlayer(ArtemisPlayer p) {
        testShip(p);
        
        assertRange(-1, 5000, p.getEnergy(), "energy");
        assertRange(-1, 6, p.getShipIndex(), "shipIndex");
        
        for (SystemType sys : SystemType.values()) {
            if (p.getSystemEnergy(sys) != -1)
                assertRange(0, 1, p.getSystemEnergy(sys), sys + "energy");
            assertRange(-1, 1, p.getSystemHeat(sys), sys + "heat");
            assertRange(-1, 16, p.getSystemCoolant(sys), sys + "coolant");
        }
    }

    public static void testShip(BaseArtemisShip p) {
        testPositionable(p);
        
        assertRange(-1, 10000, p.getHullId(), "hullId");
        
        if (p.getBearing() != Float.MIN_VALUE)
            assertRange(-4, 4, p.getBearing(), "bearing");
        
        assertRange(-1, 1000, p.getShieldsFrontMax(), "shieldFrontMax");
        assertRange(-1, 1000, p.getShieldsRearMax(), "shieldRearMax");
        assertRange(-1, 1000, p.getShieldsFront(), "shieldFront");
        assertRange(-1, 1000, p.getShieldsRear(), "shieldRear");
    }

    public static void testPositionable(ArtemisPositionable p) {
        assertRange(-1, 100000, p.getX(), "x");
        assertRange(-1, 100, p.getY(), "y");
        assertRange(-1, 100000, p.getZ(), "z");
    }

    private static void assertRange(float low, float high, float value, String label) {
        if (value < low || value > high) {
            throw new RuntimeException(
                    String.format("Value ``%s'' (%f) out of range [%f,%f]",
                            label, value, low, high));
        }
    }
}
