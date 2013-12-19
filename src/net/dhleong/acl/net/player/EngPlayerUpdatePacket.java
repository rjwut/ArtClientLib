package net.dhleong.acl.net.player;

import java.util.Arrays;

import net.dhleong.acl.enums.ShipSystem;
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

    public EngPlayerUpdatePacket(byte[] data) {
        super(data);
        ObjectParser p = new ObjectParser(mData, 0);

        try {
        	while (p.hasMore()) {
	            float[] heat = new float[ HEAT.length ];
	            float[] sysEnergy = new float[ ENERGY.length ];
	            int[] coolant = new int[ COOLANT.length ];
	            p.start(Bit.values());
            
                for (int i = 0; i < heat.length; i++) {
                    heat[i] = p.readFloat(HEAT[i], -1);
                }

                for (int i = 0; i < sysEnergy.length; i++) {
                    sysEnergy[i] = p.readFloat(ENERGY[i], -1);
                }

                for (int i = 0; i < coolant.length; i++) {
                    coolant[i] = p.readByte(COOLANT[i], (byte) -1);
                }
                
                ArtemisPlayer player = new ArtemisPlayer(p.getTargetId());
                
                for (int i = 0; i < HEAT.length; i++) {
                    ShipSystem sys = ShipSystem.values()[i];
                    player.setSystemHeat(sys, heat[i]);
                    player.setSystemEnergy(sys, sysEnergy[i]);
                    player.setSystemCoolant(sys, coolant[i]);
                }

                mObjects.add(player);
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
    	for (ArtemisPlayer player : mObjects) {
            for (ShipSystem system : ShipSystem.values()) {
                System.out.println(
                		system +
                		": energy=" + player.getSystemEnergy(system) +
                        ", heat=" + player.getSystemHeat(system) + 
                        ", coolant=" + player.getSystemCoolant(system)
                );
            }
    	}
    }
}