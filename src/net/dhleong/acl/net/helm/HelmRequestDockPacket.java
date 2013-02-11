package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Request to dock. There must be some ACK when it 
 *  works, but I haven't figured it out yet...
 * @author dhleong
 *
 */
public class HelmRequestDockPacket extends ShipActionPacket {
    
    private static final int FLAGS = 0x0c;
    
    public HelmRequestDockPacket() {
        super(FLAGS, TYPE_REQUEST_DOCK, 0);
    }
}
