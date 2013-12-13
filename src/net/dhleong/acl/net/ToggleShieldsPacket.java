package net.dhleong.acl.net;

/**
 * Toggle shields
 * @author dhleong
 */
public class ToggleShieldsPacket extends ShipActionPacket {
    public ToggleShieldsPacket() {
        super(TYPE_TOGGLE_SHIELDS, 0);
    }
}