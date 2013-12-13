package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 *
 */
public class SetMainScreenPacket extends ShipActionPacket {
    public SetMainScreenPacket(MainScreen screen) {
        super(TYPE_MAINSCREEN, screen.ordinal());
    }
}