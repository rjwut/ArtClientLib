package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Toggle reverse thrusters (or whatever)
 * 
 * @author dhleong
 *
 */
public class HelmToggleReversePacket extends ShipActionPacket {
    
    private static final int FLAGS = 0x0c;
    
    public HelmToggleReversePacket() {
        super(FLAGS, TYPE_REVERSE_ENGINES, 0);
    }
}
