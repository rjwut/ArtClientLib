package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;


/**
 * The official client sends this sometimes in 1.7....
 *   
 * @author dhleong
 *
 */
public class ReadyPacket2 extends ShipActionPacket {
    public ReadyPacket2() {
        super(TYPE_READY2, 0);
    }
}