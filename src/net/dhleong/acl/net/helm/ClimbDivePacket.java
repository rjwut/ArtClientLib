package net.dhleong.acl.net.helm;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Changes the ship's trim, causing it to climb, dive or level out.
 * @author rjwut
 */
public class ClimbDivePacket extends BaseArtemisPacket {
    private static final int TYPE = 0x0351A5AC;
    private static final int SUBTYPE = 0x1b;
    private static final int UP = -1;
    private static final int DOWN = 1;

    private boolean mUp;

    /**
     * Giving an "up" command while diving causes the ship to level out; giving
     * a second "up" command causes it to start climbing. The "down" command
     * does the reverse.
     * @param up True if you want to tilt the ship up, false to tilt it down.
     */
    public ClimbDivePacket(boolean up) {
        super(ConnectionType.CLIENT, TYPE);
        this.mUp = up;
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeInt(mUp ? UP : DOWN);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mUp ? "up" : "down");
	}
}