package net.dhleong.acl.protocol.core.helm;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

/**
 * Set impulse power.
 * @author dhleong
 */
public class HelmSetImpulsePacket extends BaseArtemisPacket {
    private static final int TYPE = 0x0351A5AC;
    private static final byte SUBTYPE = 0x00;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, SUBTYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return HelmSetImpulsePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new HelmSetImpulsePacket(reader);
			}
		});
	}

    private float mPower;

    /**
     * @param power Impulse power percentage (value between 0 and 1, inclusive)
     */
    public HelmSetImpulsePacket(float power) {
        super(ConnectionType.CLIENT, TYPE);

        if (power < 0 || power > 1) {
        	throw new IllegalArgumentException("Impulse power out of range");
        }

        mPower = power;
    }

    private HelmSetImpulsePacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
    	int subtype = reader.readInt();

    	if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
    	}

    	mPower = reader.readFloat();
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(SUBTYPE).writeFloat(mPower);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mPower * 100).append('%');
	}
}
