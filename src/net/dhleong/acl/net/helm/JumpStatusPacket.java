package net.dhleong.acl.net.helm;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Indicates that a jump has begun or ended.
 * @author dhleong
 */
public class JumpStatusPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;

	public static void register(PacketFactoryRegistry registry) {
		PacketFactory factory = new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return JumpStatusPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new JumpStatusPacket(reader);
			}
		};
		registry.register(TYPE, MSG_TYPE_BEGIN, factory);
		registry.register(TYPE, MSG_TYPE_END, factory);
	}

    /**
     * Jump "begin"; that is, the countdown has begun
     */
    public static final byte MSG_TYPE_BEGIN = 0x0c;

    /**
     * Jump "end"; there's still some cooldown (~5 seconds)
     */
    public static final byte MSG_TYPE_END = 0x0d;

    private final boolean begin;

    private JumpStatusPacket(PacketReader reader) throws ArtemisPacketException {
        super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype == MSG_TYPE_BEGIN) {
        	begin = true;
        } else if (subtype == MSG_TYPE_END) {
        	begin = false;
        } else {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE_BEGIN + " or " +
        			MSG_TYPE_END + ", got " + subtype
        	);
        }
    }
    
    /**
     * Returns true if the jump is starting (countdown has begun); false if the
     * jump has ended.
     * @return
     */
    public boolean isCountdown() {
        return begin;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(begin ? "begin" : "end");
	}
}