package net.dhleong.acl.net;


/**
 * Ready packets seem to get sent after the client
 *  picks a station and joins. Maybe this will 
 *  hint the server to send all info for objs?
 *   
 * @author dhleong
 *
 */
public class ReadyPacket extends BaseArtemisPacket {
    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public ReadyPacket() {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        // ??
        PacketParser.putLendInt(0x0d, mData, 0);
        PacketParser.putLendInt(0, mData, 4);
    }
}
