package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 *
 */
public class SetMainScreenPacket extends BaseArtemisPacket {
    
    public static final int TYPE = 0x4C821D3C;
    private static final int FLAGS = 0x0c;

    public SetMainScreenPacket(MainScreen screen) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(0x01, mData);
        PacketParser.putLendInt(screen.ordinal(), mData, 4);
    }
}
