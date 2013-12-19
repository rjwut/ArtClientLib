package net.dhleong.acl.net.comms;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class AudioCommandPacket extends BaseArtemisPacket {
    public enum Command {
        PLAY, DELETE;
    }
    
    private static final int TYPE = 0x6AADC57F;

    public AudioCommandPacket(int audioId, Command cmd) {
        super(ConnectionType.CLIENT, TYPE, new byte[8]);
        PacketParser.putLendInt(audioId, mData);
        PacketParser.putLendInt(cmd.ordinal(), mData, 4);
    }
}