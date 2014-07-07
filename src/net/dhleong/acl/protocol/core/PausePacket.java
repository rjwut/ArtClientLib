package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.util.BoolState;

public class PausePacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x04;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return PausePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new PausePacket(reader);
			}
		});
	}

    private final BoolState mPaused;
    
    private PausePacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mPaused = reader.readBool(4);
    }

    public PausePacket(boolean paused) {
        super(ConnectionType.SERVER, TYPE);
    	mPaused = BoolState.from(paused);
    }

    public BoolState getPaused() {
        return mPaused;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeInt(BoolState.safeValue(mPaused) ? 1 : 0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(BoolState.safeValue(mPaused) ? "PAUSED" : "RUNNING");
	}
}