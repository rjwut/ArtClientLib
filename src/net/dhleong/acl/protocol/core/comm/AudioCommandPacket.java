package net.dhleong.acl.protocol.core.comm;

import net.dhleong.acl.enums.AudioCommand;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

/**
 * Plays or deletes an audio message.
 */
public class AudioCommandPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x6aadc57f;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return AudioCommandPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new AudioCommandPacket(reader);
			}
		});
	}

    private int audioId;
    private AudioCommand cmd;

    /**
     * @param audioId The ID of the audio message to which the command applies
     * @param cmd The command to issue (PLAY or DELETE)
     */
    public AudioCommandPacket(int audioId, AudioCommand cmd) {
        super(ConnectionType.CLIENT, TYPE);

        if (cmd == null) {
        	throw new IllegalArgumentException("You must provide a command");
        }

        this.audioId = audioId;
        this.cmd = cmd;
    }

    private AudioCommandPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
    	audioId = reader.readInt();
    	cmd = AudioCommand.values()[reader.readInt()];
    }

    public int getAudioId() {
    	return audioId;
    }

    public AudioCommand getCommand() {
    	return cmd;
    }

    @Override
	protected void writePayload(PacketWriter writer) {
    	writer.writeInt(audioId).writeInt(cmd.ordinal());
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(cmd).append(" msg #").append(audioId);
	}
}