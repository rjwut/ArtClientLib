package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player packet handling is so crazy it gets its own PACKAGE. See implementing
 * classes MainPlayerUpdatePacket, etc.
 * @author dhleong
 */
public abstract class PlayerUpdatePacket extends BaseArtemisPacket {
    public PlayerUpdatePacket() {
		super(ConnectionType.SERVER, ObjectUpdatingPacket.WORLD_TYPE);
	}

	protected ArtemisPlayer mPlayer;

	/**
	 * Returns an ArtemisPlayer object which contains all the data in this
	 * packet.
	 */
    public ArtemisPlayer getPlayer() {
    	return mPlayer;
    }

    public void setPlayer(ArtemisPlayer player) {
    	mPlayer = player;
    }
}