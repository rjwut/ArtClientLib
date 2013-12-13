package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class AudioCommandPacket extends BaseArtemisPacket {
    public enum Command {
        PLAY(0),
        DELETE(1);
        
        private int value;

        Command(int value) {
            this.value = value;
        }
    }
    
    private static final int TYPE = 0x6AADC57F;

    public AudioCommandPacket(int audioId, Command cmd) {
        super(0x2, TYPE, new byte[8]);
        PacketParser.putLendInt(audioId, mData);
        PacketParser.putLendInt(cmd.value, mData, 4);
    }
}