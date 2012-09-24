package net.dhleong.acl;

import java.util.ArrayList;
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
    
    public interface OnObjectCountChangeListener {
        void onObjectCountChanged(int count);
    }
    
    private static final OnObjectCountChangeListener sDummyListener = 
            new OnObjectCountChangeListener() {
        @Override
        public void onObjectCountChanged(int count) {/* nop */}
    };
    
    private final HashMap<Integer, ArtemisObject> mObjects = 
            new HashMap<Integer, ArtemisObject>();
    private OnObjectCountChangeListener mListener = sDummyListener;

    @Override
    public void onPacket(ArtemisPacket pkt) {
        if (pkt instanceof DestroyObjectPacket) {
            mObjects.remove(((DestroyObjectPacket)pkt).getTarget());
            
            // signal change
            if (mObjects.size() == 1) {
                ArtemisObject last = mObjects.values().iterator().next();
                if ("Artemis".equals(last.getName())) {
                    // special (hack?) case;
                    //  this is actually the end of the game
                    mObjects.clear();
                    mListener.onObjectCountChanged(0);
                    return;
                }
            } 

            mListener.onObjectCountChanged(mObjects.size());
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
                // signal change
                mListener.onObjectCountChanged(mObjects.size());
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

    /**
     * If you don't want/need to reuse a List, this
     *  will create a list for you
     *  
     * @param type
     * @return
     * @see #getObjects(List, int)
     */
    public List<ArtemisObject> getObjects(int type) {
        List<ArtemisObject> objs = new ArrayList<ArtemisObject>();
        getObjects(objs, type);
        return objs;
    }

    public ArtemisObject getObject(int objId) {
        return mObjects.get(objId);
    }
    
    /**
     * Get the first object with the given name
     * @param type
     * @return
     */
    public ArtemisObject getObjectByName(String name) {
        for (ArtemisObject obj : mObjects.values()) {
            if (obj.getName().equals(name))
                return obj;
        }
        
        return null;
    }
    
    public void setOnObjectCountChangedListener(OnObjectCountChangeListener listener) {
        mListener = (listener == null) ? sDummyListener : listener;
    }
}
