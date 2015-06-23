package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.Perspective;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

/**
 * Notifies clients that the main screen perspective has changed.
 * @author rjwut
 */
public class PerspectivePacket extends BaseArtemisPacket {
	private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x12;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return PerspectivePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new PerspectivePacket(reader);
			}
		});
	}

	private Perspective mPerspective;

	private PerspectivePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mPerspective = Perspective.values()[reader.readInt()];
    }

    public PerspectivePacket(Perspective perspective) {
    	super(ConnectionType.SERVER, TYPE);

    	if (perspective == null) {
    		throw new IllegalArgumentException("You must provide a Perspective");
    	}

    	mPerspective = perspective;
    }

    /**
     * Returns the Perspective specified by this packet.
     */
    public Perspective getPerspective() {
    	return mPerspective;
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mPerspective.name());
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeInt(mPerspective.ordinal());
	}
}