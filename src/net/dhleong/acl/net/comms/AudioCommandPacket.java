package net.dhleong.acl.net.comms;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Plays or deletes an audio message.
 */
public class AudioCommandPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x6AADC57F;

    public enum Command {
        PLAY, DELETE;
    }

    private int audioId;
    private Command cmd;

    public AudioCommandPacket(int audioId, Command cmd) {
        super(ConnectionType.CLIENT, TYPE);

        if (cmd == null) {
        	throw new IllegalArgumentException("You must provide a command");
        }

        this.audioId = audioId;
        this.cmd = cmd;
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(mType)
    			.writeInt(audioId)
    			.writeInt(cmd.ordinal());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(cmd).append(" msg #").append(audioId);
	}
}