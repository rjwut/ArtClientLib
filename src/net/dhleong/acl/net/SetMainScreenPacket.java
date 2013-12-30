package net.dhleong.acl.net;

import net.dhleong.acl.enums.MainScreenView;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 */
public class SetMainScreenPacket extends ShipActionPacket {
	/**
	 * @param screen The enum value representing the desired view
	 */
    public SetMainScreenPacket(MainScreenView screen) {
        super(TYPE_MAINSCREEN, screen != null ? screen.ordinal() : -1);

        if (screen == null) {
        	throw new IllegalArgumentException("You must specify a view");
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(MainScreenView.values()[mArg]);
	}
}