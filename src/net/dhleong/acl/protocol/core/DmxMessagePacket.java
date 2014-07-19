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

public class DmxMessagePacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x10;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return DmxMessagePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new DmxMessagePacket(reader);
			}
		});
	}

    private final String mName;
    private final boolean mOn;

    private DmxMessagePacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mName = reader.readString();
        mOn = reader.readInt() == 1;
    }

    public DmxMessagePacket(String name, boolean on) {
        super(ConnectionType.SERVER, TYPE);
        mName = name;
        mOn = on;
    }

    /**
     * The name of the DMX flag.
     */
    public String getMessage() {
        return mName;
    }

    /**
     * Returns true if the DMX flag is on; false otherwise.
     */
    public boolean isOn() {
    	return mOn;
    }

    @Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeString(mName).writeInt(mOn ? 1 : 0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mName).append('=').append(mOn ? "ON" : "OFF");
	}
}