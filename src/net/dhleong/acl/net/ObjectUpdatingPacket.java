package net.dhleong.acl.net;

import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * A type of packet which is used to update or create world objects.
 * @author dhleong
 */
public interface ObjectUpdatingPacket extends ArtemisPacket {
    /**
     * Returns the list of updates.
     */
    public List<ArtemisObject> getObjects();
}
