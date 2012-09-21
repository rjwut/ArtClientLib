package net.dhleong.acl;

import java.util.HashMap;
import java.util.List;

import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.EngSystemUpdatePacket;
import net.dhleong.acl.net.EngSystemUpdatePacket.BoolState;
import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * 
 * @author dhleong
 *
 */
public class SystemManager implements OnPacketListener {
    
    private final HashMap<Integer, ArtemisObject> mObjects = 
            new HashMap<Integer, ArtemisObject>();

    @Override
    public void onPacket(ArtemisPacket pkt) {
        if (pkt instanceof DestroyObjectPacket) {
            mObjects.remove(((DestroyObjectPacket)pkt).getTarget());
            // TODO signal change?
            return;
        }
        
        // from here, we only care about this kind
        if (!(pkt instanceof SystemInfoPacket))
            return;
        
        SystemInfoPacket info = (SystemInfoPacket) pkt;
        if (SysCreatePacket.isExtensionOf(info)) {
            // CREATE objects
            SysCreatePacket create = new SysCreatePacket(info);
            
            List<ArtemisObject> newObjs = create.getCreatedObjects();
            for (ArtemisObject obj : newObjs)
                mObjects.put(obj.getId(), obj);
            
            if (newObjs.size() > 0) {
                // TODO signal change?
            }
        } else if (EngSystemUpdatePacket.isExtensionOf(info)) {
            EngSystemUpdatePacket eng = new EngSystemUpdatePacket(info);
            
            if (eng.getRedAlert() != BoolState.UNKNOWN) {
                ArtemisPlayer p = (ArtemisPlayer) mObjects.get(info.getTarget());
                if (p != null)
                    p.setRedAlert(eng.getRedAlert().getBooleanValue());
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
        for (ArtemisObject obj : mObjects.values()) {
            if (obj.getType() == type) {
                dest.add(obj);
                count++;
            }
        }
        return count;
    }

    public ArtemisObject getObject(int objId) {
        return mObjects.get(objId);
    }
}
