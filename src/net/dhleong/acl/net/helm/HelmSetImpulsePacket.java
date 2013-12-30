package net.dhleong.acl.net.helm;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Set impulse power.
 * @author dhleong
 */
public class HelmSetImpulsePacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    public static final int SUBTYPE = 0x00;

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

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeFloat(mPower);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mPower * 100).append('%');
	}
}