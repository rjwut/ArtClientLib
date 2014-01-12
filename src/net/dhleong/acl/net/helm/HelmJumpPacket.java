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
    private static final int TYPE = 0x0351A5AC;
    private static final int SUBTYPE = 0x05;
    
    private float mHeading;
    private float mDistance;

    /**
     * Initiates a jump for the indicated direction and distance.
     * @param heading Heading as a percentage of 360
     * @param distance Distance as a percentage of the max possible jump
     * 		distance, 50K
     */
    public HelmJumpPacket(float heading, float distance) {
        super(ConnectionType.CLIENT, TYPE);

        if (heading < 0 || heading > 1) {
        	throw new IllegalArgumentException("Heading out of range");
        }

        if (distance < 0 || distance > 1) {
        	throw new IllegalArgumentException("Distance out of range");
        }
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeFloat(mHeading)
    			.writeFloat(mDistance);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("heading = ").append(mHeading * 360)
		.append(" deg; distance = ").append(mDistance * 50).append('k');
	}
}