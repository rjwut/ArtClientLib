package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.EnemyUpdatePacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.eng.EngSetEnergyPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.sci.SciScanPacket;
import net.dhleong.acl.net.sci.SciSelectPacket;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.world.ArtemisEnemy;

public class RawPacketDumper {
    
    static boolean isSelected = false;

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
        
        final PacketParser dummy = new PacketParser();
        dummy.setNoParseMode(true);
        
        net.setPacketParser(dummy);
        
        net.addOnPacketListener(new OnPacketListener() {
            
            private ArtemisEnemy mSelectedPkt;

            @Override
            public void onPacket(ArtemisPacket pkt) {
                // filter out noise
                BaseArtemisPacket base = (BaseArtemisPacket) pkt;
                byte[] data = base.getData();
                if (base.getType() == ArtemisPacket.WORLD_TYPE
                        && data.length == 4 
                        && PacketParser.getLendInt(data) == 0)
                    return;
                
                    try {
                        dummy.setNoParseMode(false);
                        ArtemisPacket parsed = PacketParser.buildPacket(base.getType(), (int) base.getMode(), 0, base.getData());
                        dummy.setNoParseMode(true);
                        System.out.println(parsed.toString());
                        if (parsed instanceof EnemyUpdatePacket) {
                            ArtemisEnemy e = (ArtemisEnemy) ((EnemyUpdatePacket) parsed).getObjects().get(0);
                            if (e.equals(mSelectedPkt)) {
                                System.out.println("unknown = " + e);
                                //((EnemyUpdatePacket) parsed).debugPrint();
                            }
                            if (!isSelected) {
                                System.out.println("Select: " + e);
                                mSelectedPkt = e;
                                net.send(new SciSelectPacket(e));
                                net.send(new ReadyPacket2());
                                net.send(new SciScanPacket(e));
                                net.send(new ReadyPacket2());
                                isSelected = true;
                            }
                        }
                    } catch (ArtemisPacketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                System.out.println("<< " + pkt);
            }
        });
        
        net.start();
        
        net.send(new ReadyPacket2());
        net.send(new ReadyPacket2());
        
        net.send(new SetStationPacket(StationType.SCIENCE, true));
//        net.send(new SetStationPacket(StationType.ENGINEERING, true));
        net.send(new ReadyPacket());
        
        net.send(new ReadyPacket2());
        
        net.send(new EngSetEnergyPacket(SystemType.SENSORS, 10));
//        net.send(new ReadyPacket2());
    }
}
