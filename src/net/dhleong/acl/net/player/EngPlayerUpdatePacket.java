package net.dhleong.acl.net.player;

import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Packet with player data related to the engineering subystems
 * @author dhleong
 *
 */
public class EngPlayerUpdatePacket extends PlayerUpdatePacket {
    private static final int HEAT_BEAMS    = 0x00000001;
    private static final int HEAT_TORPS    = 0x00000002;
    private static final int HEAT_SENSR    = 0x00000004;
    private static final int HEAT_MANEU    = 0x00000008;
    private static final int HEAT_IMPLS    = 0x00000010;
    private static final int HEAT_JUMPS    = 0x00000020;
    private static final int HEAT_SFRNT    = 0x00000040;
    private static final int HEAT_SREAR    = 0x00000080;
    
    /** energy allocation from engineering */
    private static final int ENRG_BEAMS    = 0x00000100;
    private static final int ENRG_TORPS    = 0x00000200;
    private static final int ENRG_SENSR    = 0x00000400;
    private static final int ENRG_MANEU    = 0x00000800;
    private static final int ENRG_IMPLS    = 0x00001000;
    private static final int ENRG_JUMPS    = 0x00002000;
    private static final int ENRG_SFRNT    = 0x00004000;
    private static final int ENRG_SREAR    = 0x00008000;
    
    private static final int COOLANT_BEAMS = 0x00010000;
    private static final int COOLANT_TORPS = 0x00020000;
    private static final int COOLANT_SENSR = 0x00040000;
    private static final int COOLANT_MANEU = 0x00080000;
    private static final int COOLANT_IMPLS = 0x00100000;
    private static final int COOLANT_JUMPS = 0x00200000;
    private static final int COOLANT_SFRNT = 0x00400000;
    private static final int COOLANT_SREAR = 0x00800000;

    private static final int[] SYSTEMS_HEAT = {
        HEAT_BEAMS, HEAT_TORPS, HEAT_SENSR,
        HEAT_MANEU, HEAT_IMPLS, HEAT_JUMPS,
        HEAT_SFRNT, HEAT_SREAR
    };
    private static final int[] SYSTEMS_ENRG = {
        ENRG_BEAMS, ENRG_TORPS, ENRG_SENSR,
        ENRG_MANEU, ENRG_IMPLS, ENRG_JUMPS,
        ENRG_SFRNT, ENRG_SREAR
    };
    private static final int[] COOLANTS = {
        COOLANT_BEAMS, COOLANT_TORPS, COOLANT_SENSR,
        COOLANT_MANEU, COOLANT_IMPLS, COOLANT_JUMPS,
        COOLANT_SFRNT, COOLANT_SREAR
    };
    
    float[] heat = new float[ SYSTEMS_HEAT.length ];
    float[] sysEnergy = new float[ SYSTEMS_ENRG.length ];
    int[] coolant = new int[ COOLANTS.length ];
    private ArtemisPlayer mPlayer;
    
    public EngPlayerUpdatePacket(byte[] data) {
        super(data);
        
        ObjectParser p = new ObjectParser(mData, 0);
        p.startNoAction();
                    
        try {
            for (int i=0; i<heat.length; i++) {
                heat[i] = p.readFloat(SYSTEMS_HEAT[i], -1);
            }

            for (int i=0; i<sysEnergy.length; i++) {
                sysEnergy[i] = p.readFloat(SYSTEMS_ENRG[i], -1);
            }

            for (int i=0; i<coolant.length; i++) {
                coolant[i] = p.readByte(COOLANTS[i], (byte)-1);
            }
            
            
            mPlayer = new ArtemisPlayer(p.getTargetId());
            
            for (int i=0; i<SYSTEMS_HEAT.length; i++) {
                SystemType sys = SystemType.values()[i];
                mPlayer.setSystemHeat(sys, heat[i]);
                mPlayer.setSystemEnergy(sys, sysEnergy[i]);
                mPlayer.setSystemCoolant(sys, coolant[i]);
            } 
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            debugPrint();
            System.out.println("this -->" + this);
            throw e;
        }
        
    }
    
    @Override
    public void debugPrint() {
        for (int i=0; i<heat.length; i++) {
            System.out.println(SystemType.values()[i] + 
                    "= " + sysEnergy[i] +
                    " :: " + heat[i] + 
                    " :: " + coolant[i]);
        }

    }

    @Override
    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }

}
