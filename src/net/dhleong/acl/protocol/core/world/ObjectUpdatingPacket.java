package net.dhleong.acl.protocol.core.world;

import java.util.List;

import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * A type of packet which is used to update or create world objects.
 * @author dhleong
 */
public interface ObjectUpdatingPacket extends ArtemisPacket {
    /**
     * Type for packets that create and/or update various world objects
     */
	public static final int WORLD_TYPE = 0x80803df9;

	/**
     * Returns the list of updates.
     */
    public List<ArtemisObject> getObjects();
}
