package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;


/**
 * The official client sends this sometimes in 1.7....
 *   
 * @author dhleong
 *
 */
public class ReadyPacket2 extends ShipActionPacket {
    private static final int FLAGS = 0x0c;
    public ReadyPacket2() {
        super(FLAGS, TYPE_READY2, 0);
    }
}
