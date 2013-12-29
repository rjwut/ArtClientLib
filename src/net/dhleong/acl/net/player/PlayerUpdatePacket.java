package net.dhleong.acl.net.player;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player packet handling is so crazy it gets its own PACKAGE.
 * See implementing classes MainPlayerUpdatePacket, etc.
 * @author dhleong
 */
public abstract class PlayerUpdatePacket extends BaseArtemisPacket {
    public PlayerUpdatePacket() {
		super(ConnectionType.SERVER, WORLD_TYPE);
	}

	protected ArtemisPlayer mPlayer;

    public ArtemisPlayer getPlayer() {
    	return mPlayer;
    }
}