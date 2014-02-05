package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.world.Artemis;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player data related to the weapons station.
 * @author dhleong
 */
public class WeapPlayerUpdatePacket extends PlayerUpdatePacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER,
				ObjectUpdatingPacket.WORLD_TYPE,
				ObjectType.WEAPONS_BRIDGE_STATION.getId(), new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return WeapPlayerUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new WeapPlayerUpdatePacket(reader);
			}
		});
	}

	private enum Bit {
		TORP_HOMING,
		TORP_NUKES,
		TORP_MINES,
		TORP_EMPS,
		UNK_0,
		TUBE_TIME_1,
		TUBE_TIME_2,
		TUBE_TIME_3,

		TUBE_TIME_4,
		TUBE_TIME_5,
		TUBE_TIME_6,
		TUBE_USE_1,
		TUBE_USE_2,
		TUBE_USE_3,
		TUBE_USE_4,
		TUBE_USE_5,

		TUBE_USE_6,
		TUBE_TYPE_1,
		TUBE_TYPE_2,
		TUBE_TYPE_3,
		TUBE_TYPE_4,
		TUBE_TYPE_5,
		TUBE_TYPE_6
	}

    private static final Bit[] TORPEDOS = {
        Bit.TORP_HOMING, Bit.TORP_NUKES, Bit.TORP_MINES, Bit.TORP_EMPS
    };

    private static final Bit[] TUBE_TIMES = {
        Bit.TUBE_TIME_1, Bit.TUBE_TIME_2, Bit.TUBE_TIME_3,
        Bit.TUBE_TIME_4, Bit.TUBE_TIME_5, Bit.TUBE_TIME_6
    };

    private static final Bit[] TUBE_USES = {
        Bit.TUBE_USE_1, Bit.TUBE_USE_2, Bit.TUBE_USE_3,
        Bit.TUBE_USE_4, Bit.TUBE_USE_5, Bit.TUBE_USE_6
    };

    private static final Bit[] TUBE_TYPES = {
        Bit.TUBE_TYPE_1, Bit.TUBE_TYPE_2, Bit.TUBE_TYPE_3,
        Bit.TUBE_TYPE_4, Bit.TUBE_TYPE_5, Bit.TUBE_TYPE_6
    };
    
    private WeapPlayerUpdatePacket(PacketReader reader) {
        int[] torps = new int[ TORPEDOS.length ];
        float[] tubeTimes = new float[Artemis.MAX_TUBES];
        int[] tubeContents = new int[Artemis.MAX_TUBES];

        reader.startObject(Bit.values());

        for (int i = 0; i < torps.length; i++) {
            torps[i] = ((byte) 0xff & reader.readByte(TORPEDOS[i], (byte) -1));
        }

        reader.readObjectUnknown(Bit.UNK_0, 1);
           
        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            tubeTimes[i] = reader.readFloat(TUBE_TIMES[i], -1);
        }

        // after this, tubeContents[i]...
        // = 0 means that tube is EMPTY; 
        // > 0 means that tube is IN USE;
        // < 0  means we DON'T KNOW
        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            tubeContents[i] = reader.readByte(TUBE_USES[i], (byte) -1);
        }

        // after this, tubeContents[i]...
        // = -1 means EMPTY;
        // = Integer.MIN_VALUE means we DON'T KNOW
        // else the type of torpedo there
        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            byte torpType = reader.readByte(TUBE_TYPES[i], (byte)-1);

            if (tubeContents[i] == 0) {
                tubeContents[i] = ArtemisPlayer.TUBE_EMPTY; // empty tube
            } else if (tubeContents[i] < 0) {
                // what's there? I don't even know
                tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN;
            } else if (tubeContents[i] > 0 && torpType != (byte) -1) {
                tubeContents[i] = torpType;
            } else {
                // IE: it's "in use" but type is unspecified/changed
                //  DO we need another constant for this?
                tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN;
            }
        }

        mPlayer = new ArtemisPlayer(reader.getObjectId());

        for (int i = 0; i < TORPEDOS.length; i++) {
        	mPlayer.setTorpedoCount(i, torps[i]);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	mPlayer.setTubeStatus(i, tubeTimes[i], tubeContents[i]);
        }
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.startObject(mPlayer, Bit.values());
		OrdnanceType[] ordTypes = OrdnanceType.values();

		for (int i = 0; i < TORPEDOS.length; i++) {
			OrdnanceType type = ordTypes[i];
			writer.writeByte(TORPEDOS[i], (byte) mPlayer.getTorpedoCount(type), (byte) -1);
		}

        writer.writeUnknown(Bit.UNK_0);

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            writer.writeFloat(TUBE_TIMES[i], mPlayer.getTubeCountdown(i), -1);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	int content = mPlayer.getTubeContents(i);
        	byte b = (byte) (content == 0 ? 0 : (content > 0 ? content : -1));
        	writer.writeByte(TUBE_TYPES[i], b, (byte) -1);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	int content = mPlayer.getTubeContents(i);
        	byte b = (byte) (content > 0 ? content : -1);
        	writer.writeByte(TUBE_TYPES[i], b, (byte) -1);
        }
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (OrdnanceType type : OrdnanceType.values()) {
			b.append(type).append('=').append(mPlayer.getTorpedoCount(type)).append(' ');
		}

		for (int i = 0; i < Artemis.MAX_TUBES; i++) {
			b.append("\n\tTube #").append(i).append(": ")
			.append(mPlayer.getTubeContents(i));
			float countdown = mPlayer.getTubeCountdown(i);

			if (countdown > 0.01f) {
				b.append(" (").append(countdown).append(')');
			}
        }
	}
}