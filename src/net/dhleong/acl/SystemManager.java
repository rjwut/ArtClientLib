package net.dhleong.acl;

import java.util.HashSet;
import java.util.List;

import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * 
 * @author dhleong
 *
 */
public class SystemManager implements OnPacketListener {
    
    private final HashSet<ArtemisObject> mObjects = new HashSet<ArtemisObject>();

    @Override
    public void onPacket(ArtemisPacket pkt) {
        if (!(pkt instanceof SystemInfoPacket))
            return;
        
        SystemInfoPacket info = (SystemInfoPacket) pkt;
        if (SysCreatePacket.isExtensionOf(info)) {
            // CREATE objects
            SysCreatePacket create = new SysCreatePacket(info);
            
            List<ArtemisObject> newObjs = create.getCreatedObjects();
            for (ArtemisObject obj : newObjs)
                mObjects.add(obj);
            
            if (newObjs.size() > 0) {
                // TODO signal change?
            }
        }
    }

    /**
     * Add objects of the given type to the given list 
     * 
     * @param dest
     * @param type One of the ArtemisObject#TYPE_* constants
     * @return The number of objects added to "dest"
     */
    public int getObjects(List<ArtemisObject> dest, int type) {
        int count = 0;
        for (ArtemisObject obj : mObjects) {
            if (obj.getType() == type) {
                dest.add(obj);
                count++;
            }
        }
        return count;
    }
}
