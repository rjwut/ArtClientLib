package net.dhleong.acl.net;



/**
 * Toggle shields
 * 
 * @author dhleong
 *
 */
public class ToggleShieldsPacket extends ShipActionPacket {
    private static final int FLAGS = 0x0c;
    
    public ToggleShieldsPacket() {
        super(FLAGS, TYPE_TOGGLE_SHIELDS, 0);
    }
}
