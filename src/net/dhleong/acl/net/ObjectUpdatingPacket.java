package net.dhleong.acl.net;

import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisPositionable;

/**
 * A type of packet which is used to update
 *  or create world objects
 *  
 * @author dhleong
 *
 */
public interface ObjectUpdatingPacket extends ArtemisPacket {
    
    /** Get a list of objects updated/created here */
    public List<ArtemisPositionable> getObjects();
}
