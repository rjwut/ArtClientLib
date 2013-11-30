package net.dhleong.acl.net.player;

import java.util.Arrays;

import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Packet with player data related to the engineering subystems
 * @author dhleong
 */
public class EngPlayerUpdatePacket extends PlayerUpdatePacket {
	private enum Bit {
		HEAT_BEAMS,
		HEAT_TORPEDOES,
		HEAT_SENSORS,
		HEAT_MANEUVERING,
		HEAT_IMPULSE,
		HEAT_WARP_OR_JUMP,
		HEAT_FORE_SHIELDS,
		HEAT_AFT_SHEILDS,

		ENERGY_BEAMS,
		ENERGY_TORPEDOES,
		ENERGY_SENSORS,
		ENERGY_MANEUVERING,
		ENERGY_IMPULSE,
		ENERGY_WARP_OR_JUMP,
		ENERGY_FORE_SHIELDS,
		ENERGY_AFT_SHIELDS,

		COOLANT_BEAMS,
		COOLANT_TORPEDOES,
		COOLANT_SENSORS,
		COOLANT_MANEUVERING,
		COOLANT_IMPULSE,
		COOLANT_WARP_OR_JUMP,
		COOLANT_FORE_SHIELDS,
		COOLANT_AFT_SHIELDS
	}

	/*
	private static final int HEAT_BEAMS    = 0x00000001;
    private static final int HEAT_TORPS    = 0x00000002;
    private static final int HEAT_SENSR    = 0x00000004;
    private static final int HEAT_MANEU    = 0x00000008;
    private static final int HEAT_IMPLS    = 0x00000010;
    private static final int HEAT_JUMPS    = 0x00000020;
    private static final int HEAT_SFRNT    = 0x00000040;
    private static final int HEAT_SREAR    = 0x00000080;
    
    // energy allocation from engineering
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
    */

	private static final int SYSTEM_COUNT = 8;
	private static final Bit[] HEAT;
	private static final Bit[] ENERGY;
	private static final Bit[] COOLANT;

	static {
		Bit[] values = Bit.values();
		HEAT = Arrays.copyOfRange(values, 0, SYSTEM_COUNT);
		ENERGY = Arrays.copyOfRange(values, SYSTEM_COUNT, SYSTEM_COUNT * 2);
		COOLANT = Arrays.copyOfRange(values, SYSTEM_COUNT * 2, SYSTEM_COUNT * 3);
	}

    float[] heat = new float[ HEAT.length ];
    float[] sysEnergy = new float[ ENERGY.length ];
    int[] coolant = new int[ COOLANT.length ];
    private ArtemisPlayer mPlayer;
    
    public EngPlayerUpdatePacket(byte[] data) {
        super(data);
        ObjectParser p = new ObjectParser(mData, 0);
        p.start(Bit.values());
                    
        try {
            for (int i = 0; i < heat.length; i++) {
                heat[i] = p.readFloat(HEAT[i], -1);
            }

            for (int i = 0; i < sysEnergy.length; i++) {
                sysEnergy[i] = p.readFloat(ENERGY[i], -1);
            }

            for (int i = 0; i < coolant.length; i++) {
                coolant[i] = p.readByte(COOLANT[i], (byte) -1);
            }
            
            mPlayer = new ArtemisPlayer(p.getTargetId());
            
            for (int i = 0; i < HEAT.length; i++) {
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