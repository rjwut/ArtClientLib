package net.dhleong.acl.net.helm;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Set steering amount. Just like the actual station, you need to send one
 * packet to start turning, then another to reset the steering angle to stop
 * turning.
 * @author dhleong
 */
public class HelmSetSteeringPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    public static final int SUBTYPE = 0x01;

    private float mSteering;

    /**
     * @param steering float in [0, 1], where 0.5 is "centered" (no turning),
     * 0.0 is left (hard to port), 1.0 is right (hard to starboard)
     */
    public HelmSetSteeringPacket(float steering) {
        super(ConnectionType.CLIENT, TYPE);

        if (steering < 0 || steering > 1) {
        	throw new IllegalArgumentException("Steering out of range");
        }
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeFloat(mSteering);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSteering);
	}
}
