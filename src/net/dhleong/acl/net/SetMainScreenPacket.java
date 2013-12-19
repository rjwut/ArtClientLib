package net.dhleong.acl.net;

import net.dhleong.acl.enums.MainScreenView;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 */
public class SetMainScreenPacket extends ShipActionPacket {
    public SetMainScreenPacket(MainScreenView screen) {
        super(TYPE_MAINSCREEN, screen.ordinal());
    }
}