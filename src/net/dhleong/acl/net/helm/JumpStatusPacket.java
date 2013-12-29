package net.dhleong.acl.net.helm;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * From the server to share what engines, ship types, and ship names will be
 * used.
 * @author dhleong
 */
public class JumpStatusPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;

    /**
     * Jump "begin"; that is, the countdown has begun
     */
    public static final byte MSG_TYPE_BEGIN = 0x0c;

    /**
     * Jump "end"; there's still some cooldown (~5 seconds)
     */
    public static final byte MSG_TYPE_END = 0x0d;

    private final boolean begin;

    public JumpStatusPacket(PacketReader reader) throws ArtemisPacketException {
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
     * Is this the countdown started packet?
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