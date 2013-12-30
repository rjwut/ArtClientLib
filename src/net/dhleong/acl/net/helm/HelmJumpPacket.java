package net.dhleong.acl.net.helm;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Initiate a jump. There is no confirmation; that's all client-side.
 * @author dhleong
 */
public class HelmJumpPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    public static final int SUBTYPE = 0x05;
    
    private float mBearing;
    private float mDistance;

    /**
     * Initiates a jump for the indicated direction and distance.
     * @param bearing Bearing as a percentage of 360
     * @param distance Distance as a percentage of the max possible jump
     * 		distance, 50K
     */
    public HelmJumpPacket(float bearing, float distance) {
        super(ConnectionType.CLIENT, TYPE);

        if (bearing < 0 || bearing > 1) {
        	throw new IllegalArgumentException("Bearing out of range");
        }

        if (distance < 0 || distance > 1) {
        	throw new IllegalArgumentException("Distance out of range");
        }
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeFloat(mBearing)
    			.writeFloat(mDistance);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("bearing = ").append(mBearing * 360)
		.append(" deg; distance = ").append(mDistance * 50).append('k');
	}
}