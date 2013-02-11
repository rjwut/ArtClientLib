package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 *
 */
public class SetMainScreenPacket extends ShipActionPacket {
    
    private static final int FLAGS = 0x0c;

    public SetMainScreenPacket(MainScreen screen) {
        super(FLAGS, TYPE_MAINSCREEN, screen.ordinal());
    }
}
