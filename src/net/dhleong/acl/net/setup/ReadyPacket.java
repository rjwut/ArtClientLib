package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Signals to the server that this station is ready to join the game. If the
 * ReadyPacket is sent before the game has started, the server will start
 * sending updates when the game starts. If the ReadyPacket is sent after the
 * game has started, the server sends updates immediately. Once a game has
 * ended, the client must send another ReadyPacket before it will be sent
 * updates again.
 * @author dhleong
 */
public class ReadyPacket extends ShipActionPacket {
    public ReadyPacket() {
        super(TYPE_READY, 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}