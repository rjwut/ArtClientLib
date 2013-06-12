package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;

import net.dhleong.acl.net.ObjectUpdatingPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.player.PlayerUpdatePacket;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.net.weap.LoadTubePacket;
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

    public static void main(final String[] args) {
                final String tgtIp = "10.211.55.4";
//        final String tgtIp = "192.168.1.30";
        final int tgtPort = 2010;

        final ThreadedArtemisNetworkInterface net; 
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
        net.addOnPacketListener(mgr);
        net.setOnConnectedListener(new OnConnectedListener() {

            @Override
            public void onConnected() {
                System.out.println("Connected to " + tgtIp);
            }

            @Override
            public void onDisconnected(final int errorCode) {
                System.out.println("Disconnected: " + errorCode);
            }
        });

        net.addOnPacketListener(new OnPacketListener() {

            @Override
            public void onPacket(final ArtemisPacket pkt) {

                if (pkt instanceof PlayerUpdatePacket) {

                    final PlayerUpdatePacket up = (PlayerUpdatePacket) pkt;
                    try {

                        final ArtemisPlayer p = up.getPlayer();
                        testPlayer(p);

                    } catch (final RuntimeException e) {
                        up.debugPrint();
                        System.out.println("--> " + up);
                        net.stop();
                        throw e;
                    }
                } else if (pkt instanceof ObjectUpdatingPacket) {
                    final ObjectUpdatingPacket up = (ObjectUpdatingPacket) pkt;
                    try {

                        for (final ArtemisPositionable p : up.getObjects()) {
                            if (p instanceof BaseArtemisShip)
                                testShip((BaseArtemisShip)p);
                            else
                                testPositionable(p);
                        }

                    } catch (final RuntimeException e) {
                        up.debugPrint();
                        System.out.println("--> " + up);
                        net.stop();
                        throw e;
                    }
                }
            }

        });

        net.start();

        net.send(new ReadyPacket2());
        net.send(new ReadyPacket2());
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));
        
        net.send(new ReadyPacket());
        net.send(new ReadyPacket2());
    }

    public static void testPlayer(final ArtemisPlayer p) {
        testShip(p);

        assertRange(-1, 5000, p.getEnergy(), "energy");
        assertRange(-1, 6, p.getShipIndex(), "shipIndex");
        assertRange(-1, 32, p.getAvailableCoolant(), "maxCoolant");

        for (final SystemType sys : SystemType.values()) {
            if (p.getSystemEnergy(sys) != -1)
                assertRange(0, 1, p.getSystemEnergy(sys), sys + "energy");
            assertRange(-1, 1, p.getSystemHeat(sys), sys + "heat");
            assertRange(-1, 16, p.getSystemCoolant(sys), sys + "coolant");
        }

        for (int i=0; i<LoadTubePacket.TORPEDO_COUNT; i++) {
            assertRange(-1, 99, p.getTorpedoCount(i), "Torp Type#" + i);
        }
    }

    public static void testShip(final BaseArtemisShip p) {
        testPositionable(p);

        assertRange(-1, 10000, p.getHullId(), "hullId");

        if (p.getBearing() != Float.MIN_VALUE)
            assertRange(-4, 4, p.getBearing(), "bearing");

        // I guess they can go negative when destroyed...?
        assertRange(-50, 1000, p.getShieldsFrontMax(), "shieldFrontMax");
        assertRange(-50, 1000, p.getShieldsRearMax(), "shieldRearMax");
        assertNotEqual(0, p.getShieldsFrontMax(), "shieldFrontMax");
        assertNotEqual(0, p.getShieldsRearMax(), "shieldRearMax");
        assertRange(-50, 1000, p.getShieldsFront(), "shieldFront");
        assertRange(-50, 1000, p.getShieldsRear(), "shieldRear");
        
        for (int i=0; i<5; i++) {
            assertRange(-1, 1f, p.getShieldFreq(i), "shieldFreq("+i+")");
        }
    }

    private static void assertNotEqual(final float expected, final float actual, final String label) {
        if (Math.abs(expected - actual) < 0.001)
            throw new RuntimeException(
                    String.format("Value ``%s'' is illegal value (%f)", 
                            label, expected));
    }

    public static void testPositionable(final ArtemisPositionable p) {
        assertRange(-1, 100020, p.getX(), "x");
        assertRange(-300, 300, p.getY(), "y");
        assertRange(-1, 100020, p.getZ(), "z");
    }

    private static void assertRange(final float low, final float high, final float value, final String label) {
        if (value < low || value > high) {
            throw new RuntimeException(
                    String.format("Value ``%s'' (%f) out of range [%f,%f]",
                            label, value, low, high));
        }
    }
}
