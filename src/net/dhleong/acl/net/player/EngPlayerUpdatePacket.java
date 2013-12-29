package net.dhleong.acl.net.player;

import java.util.Arrays;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.net.PacketReader;
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

	private static final int SYSTEM_COUNT = ShipSystem.values().length;
	private static final Bit[] HEAT;
	private static final Bit[] ENERGY;
	private static final Bit[] COOLANT;

	static {
		Bit[] values = Bit.values();
		HEAT = Arrays.copyOfRange(values, 0, SYSTEM_COUNT);
		ENERGY = Arrays.copyOfRange(values, SYSTEM_COUNT, SYSTEM_COUNT * 2);
		COOLANT = Arrays.copyOfRange(values, SYSTEM_COUNT * 2, SYSTEM_COUNT * 3);
	}

    public EngPlayerUpdatePacket(PacketReader reader) {
        try {
            float[] heat = new float[ SYSTEM_COUNT ];
            float[] sysEnergy = new float[ SYSTEM_COUNT ];
            int[] coolant = new int[ SYSTEM_COUNT ];
            reader.startObject(Bit.values());
        
            for (int i = 0; i < SYSTEM_COUNT; i++) {
                heat[i] = reader.readFloat(HEAT[i], -1);
            }

            for (int i = 0; i < SYSTEM_COUNT; i++) {
                sysEnergy[i] = reader.readFloat(ENERGY[i], -1);
            }

            for (int i = 0; i < SYSTEM_COUNT; i++) {
                coolant[i] = reader.readByte(COOLANT[i], (byte) -1);
            }
            
            mPlayer = new ArtemisPlayer(reader.getObjectId());
            
            for (int i = 0; i < SYSTEM_COUNT; i++) {
                ShipSystem sys = ShipSystem.values()[i];
                mPlayer.setSystemHeat(sys, heat[i]);
                mPlayer.setSystemEnergy(sys, sysEnergy[i]);
                mPlayer.setSystemCoolant(sys, coolant[i]);
            }
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            System.out.println("this -->" + this);
            throw e;
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
        for (ShipSystem system : ShipSystem.values()) {
        	b.append(system)
        	.append(": energy=").append(mPlayer.getSystemEnergy(system))
        	.append(", heat=").append(mPlayer.getSystemHeat(system))
        	.append(", coolant=").append(mPlayer.getSystemCoolant(system));
        }
	}
}