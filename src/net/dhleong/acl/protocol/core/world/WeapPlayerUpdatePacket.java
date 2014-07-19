package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.enums.TubeState;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.world.Artemis;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player data related to the weapons console.
 * @author dhleong
 */
public class WeapPlayerUpdatePacket extends PlayerUpdatePacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER,
				ObjectUpdatingPacket.WORLD_TYPE,
				ObjectType.WEAPONS_CONSOLE.getId(), new PacketFactory() {
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
		TUBE_STATE_1,
		TUBE_STATE_2,
		TUBE_STATE_3,
		TUBE_STATE_4,
		TUBE_STATE_5,

		TUBE_STATE_6,
		TUBE_CONTENT_1,
		TUBE_CONTENT_2,
		TUBE_CONTENT_3,
		TUBE_CONTENT_4,
		TUBE_CONTENT_5,
		TUBE_CONTENT_6
	}

    private static final Bit[] TORPEDOS = {
        Bit.TORP_HOMING, Bit.TORP_NUKES, Bit.TORP_MINES, Bit.TORP_EMPS
    };

    private static final Bit[] TUBE_TIMES = {
        Bit.TUBE_TIME_1, Bit.TUBE_TIME_2, Bit.TUBE_TIME_3,
        Bit.TUBE_TIME_4, Bit.TUBE_TIME_5, Bit.TUBE_TIME_6
    };

    private static final Bit[] TUBE_STATES = {
        Bit.TUBE_STATE_1, Bit.TUBE_STATE_2, Bit.TUBE_STATE_3,
        Bit.TUBE_STATE_4, Bit.TUBE_STATE_5, Bit.TUBE_STATE_6
    };

    private static final Bit[] TUBE_CONTENTS = {
        Bit.TUBE_CONTENT_1, Bit.TUBE_CONTENT_2, Bit.TUBE_CONTENT_3,
        Bit.TUBE_CONTENT_4, Bit.TUBE_CONTENT_5, Bit.TUBE_CONTENT_6
    };

    private WeapPlayerUpdatePacket(PacketReader reader) {
        int[] torps = new int[ TORPEDOS.length ];
        float[] tubeTimes = new float[Artemis.MAX_TUBES];
        TubeState[] tubeStates = new TubeState[Artemis.MAX_TUBES];
        byte[] tubeContents = new byte[Artemis.MAX_TUBES];

        reader.startObject(Bit.values());

        for (int i = 0; i < torps.length; i++) {
            torps[i] = (reader.readByte(TORPEDOS[i], (byte) -1));
        }

        reader.readObjectUnknown(Bit.UNK_0, 1);
           
        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            tubeTimes[i] = reader.readFloat(TUBE_TIMES[i], -1);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	byte state = reader.readByte(TUBE_STATES[i], (byte) -1);

        	if (state != -1) {
        		tubeStates[i] = TubeState.values()[state];
        	}
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	tubeContents[i] = reader.readByte(TUBE_CONTENTS[i], (byte) -1);
        }

        mPlayer = new ArtemisPlayer(reader.getObjectId());

        for (int i = 0; i < TORPEDOS.length; i++) {
        	mPlayer.setTorpedoCount(i, torps[i]);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	mPlayer.setTubeCountdown(i, tubeTimes[i]);
        	mPlayer.setTubeState(i, tubeStates[i]);
        	mPlayer.setTubeContentsValue(i, tubeContents[i]);
        }

        mPlayer.setUnknownProps(reader.getUnknownObjectProps());
        reader.skip(4);	// skip 0x00 terminator
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.startObject(mPlayer, ObjectType.WEAPONS_CONSOLE, Bit.values());
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
        	TubeState state = mPlayer.getTubeState(i);
        	byte stateByte = (byte) (state != null ? state.ordinal() : -1);
        	writer.writeByte(TUBE_STATES[i], stateByte, (byte) -1);
        }

        for (int i = 0; i < Artemis.MAX_TUBES; i++) {
        	byte type = mPlayer.getTubeContentsValue(i);
        	writer.writeByte(TUBE_CONTENTS[i], type, (byte) -1);
        }

		writer.endObject();
		writer.writeInt(0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (OrdnanceType type : OrdnanceType.values()) {
			int count = mPlayer.getTorpedoCount(type);

			if (count != -1) {
				b.append(type).append('=').append(count).append(' ');
			}
		}

		for (int i = 0; i < Artemis.MAX_TUBES; i++) {
			TubeState state = mPlayer.getTubeState(i);
			byte contents = mPlayer.getTubeContentsValue(i);
			float time = mPlayer.getTubeCountdown(i);

			if (state == null && contents == -1 && time < 0) {
				continue;
			}

			b.append("\n\tTube #").append(i).append(":");

			if (state != null) {
				b.append(" state=").append(state);
			}

			if (contents != -1) {
				String contentsStr;

				if (state == TubeState.UNLOADED) {
					contentsStr = "EMPTY";
				} else {
					contentsStr = OrdnanceType.values()[contents].name();
				}

				b.append(" contents=").append(contentsStr);
			}

			if (time >= 0) {
				b.append(" time=").append(time);
			}
        }
	}
}