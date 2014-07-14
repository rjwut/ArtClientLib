package net.dhleong.acl.protocol.core.world;

import java.util.Arrays;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.world.Artemis;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Packet with player data related to the engineering console.
 * @author dhleong
 */
public class EngPlayerUpdatePacket extends PlayerUpdatePacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER,
				ObjectUpdatingPacket.WORLD_TYPE,
				ObjectType.ENGINEERING_CONSOLE.getId(),
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return EngPlayerUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new EngPlayerUpdatePacket(reader);
			}
		});
	}

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

	private static final Bit[] HEAT;
	private static final Bit[] ENERGY;
	private static final Bit[] COOLANT;

	static {
		Bit[] values = Bit.values();
		HEAT = Arrays.copyOfRange(values, Bit.HEAT_BEAMS.ordinal(), Bit.ENERGY_BEAMS.ordinal());
		ENERGY = Arrays.copyOfRange(values, Bit.ENERGY_BEAMS.ordinal(), Bit.COOLANT_BEAMS.ordinal());
		COOLANT = Arrays.copyOfRange(values, Bit.COOLANT_BEAMS.ordinal(), Bit.values().length);
	}

    private EngPlayerUpdatePacket(PacketReader reader) {
        float[] heat = new float[ Artemis.SYSTEM_COUNT ];
        float[] sysEnergy = new float[ Artemis.SYSTEM_COUNT ];
        int[] coolant = new int[ Artemis.SYSTEM_COUNT ];
        reader.startObject(Bit.values());
    
        for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
            heat[i] = reader.readFloat(HEAT[i], -1);
        }

        reader.skip(1);

        for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
            sysEnergy[i] = reader.readFloat(ENERGY[i], -1);
        }

        for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
            coolant[i] = reader.readByte(COOLANT[i], (byte) -1);
        }

        mPlayer = new ArtemisPlayer(reader.getObjectId());

        for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
            ShipSystem sys = ShipSystem.values()[i];
            mPlayer.setSystemHeat(sys, heat[i]);
            mPlayer.setSystemEnergy(sys, sysEnergy[i]);
            mPlayer.setSystemCoolant(sys, coolant[i]);
        }

        reader.skip(4);
    }

    public EngPlayerUpdatePacket() {
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();
		writer.startObject(mPlayer, ObjectType.ENGINEERING_CONSOLE, bits);

		for (ShipSystem sys : ShipSystem.values()) {
			writer.writeFloat(HEAT[sys.ordinal()], mPlayer.getSystemHeat(sys), -1);
		}

		writer.writeObjByte((byte) 0);

		for (ShipSystem sys : ShipSystem.values()) {
			writer.writeFloat(ENERGY[sys.ordinal()], mPlayer.getSystemEnergy(sys), -1);
		}

		for (ShipSystem sys : ShipSystem.values()) {
			writer.writeByte(COOLANT[sys.ordinal()], (byte) mPlayer.getSystemCoolant(sys), (byte) -1);
		}

		writer.endObject();
		writer.writeInt(0);
	}

    @Override
	protected void appendPacketDetail(StringBuilder b) {
        for (ShipSystem system : ShipSystem.values()) {
        	b.append("\n\t").append(system)
        	.append(": energy=").append(mPlayer.getSystemEnergy(system))
        	.append(", heat=").append(mPlayer.getSystemHeat(system))
        	.append(", coolant=").append(mPlayer.getSystemCoolant(system));
        }
	}
}