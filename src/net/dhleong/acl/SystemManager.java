package net.dhleong.acl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.EngGridUpdatePacket;
import net.dhleong.acl.net.EngGridUpdatePacket.GridDamage;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.ObjUpdatePacket;
import net.dhleong.acl.net.PlayerUpdatePacket;
import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ShipSystemGrid;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;

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

    private static final boolean DEBUG = false;
    
    private final HashMap<Integer, ArtemisObject> mObjects = 
            new HashMap<Integer, ArtemisObject>();
    private OnObjectCountChangeListener mListener = sDummyListener;

    private HashMap<GridCoord, Float> mGridDamage;
    private ShipSystemGrid mGrid;
    
    /** Manually add an obj to the system */
    public void addObject(ArtemisObject obj) {
        synchronized(this) {
            mObjects.put(obj.getId(), obj);
        }
        mListener.onObjectCountChanged(mObjects.size());
    }

    @Override
    public void onPacket(ArtemisPacket pkt) {
        if (pkt instanceof DestroyObjectPacket) {
            synchronized(this) {
                mObjects.remove(((DestroyObjectPacket)pkt).getTarget());
            }
            // signal change
            if (mObjects.size() == 1) {
                ArtemisObject last = mObjects.values().iterator().next();
                if ("Artemis".equals(last.getName())) {
                    // special (hack?) case;
                    //  this is actually the end of the game
                    clear();
                    mListener.onObjectCountChanged(0);
                    return;
                }
            } 

            mListener.onObjectCountChanged(mObjects.size());
            return;
        } else if (pkt instanceof EngGridUpdatePacket) {
            EngGridUpdatePacket gridUp = (EngGridUpdatePacket) pkt;
            List<GridDamage> damages = gridUp.getDamage();
            if (damages.size() > 0 && mGridDamage != null) {
                for (GridDamage d : damages) {
                    mGridDamage.put(d.coord, d.damage);
                }
            }
        }
        
        // from here, we only care about this kind
        if (!(pkt instanceof SystemInfoPacket))
            return;
        
        SystemInfoPacket info = (SystemInfoPacket) pkt;
        if (SysCreatePacket.isExtensionOf(info)) {
            // CREATE objects
            SysCreatePacket create = new SysCreatePacket(info);
            
            List<ArtemisObject> newObjs = create.getCreatedObjects();
            for (ArtemisObject obj : newObjs) {
                synchronized(this) {
                    mObjects.put(obj.getId(), obj);
                }
                
                if (DEBUG) System.out.println("SystemManager#created: " + obj);
            }
            
            if (DEBUG) System.out.println("--> " + create);
            
            if (newObjs.size() > 0) {
                // signal change
                mListener.onObjectCountChanged(mObjects.size());
            }

        } else if (ObjUpdatePacket.isExtensionOf(info)) {
            
            ObjUpdatePacket e = new ObjUpdatePacket(info);
            
            for (ArtemisPositionable eng : e.mObjects) {
                updateOrCreate(eng);
            }
        } else if (PlayerUpdatePacket.isExtensionOf(info)) {
            PlayerUpdatePacket e = new PlayerUpdatePacket(info);
            
            updateOrCreate(e.getPlayer());
            
        }
    }
    
    public boolean updateOrCreate(ArtemisPositionable o) {
        ArtemisPositionable p = (ArtemisPositionable) mObjects.get(o.getId());
        if (p != null) {
            p.updateFrom(o);
            return false;
        } else {
            synchronized(this) {
                mObjects.put(o.getId(), o);
            }
            
            mListener.onObjectCountChanged(mObjects.size());
            
            return true;
        }
    }

    public synchronized void getAll(List<ArtemisObject> dest) {
        dest.addAll(mObjects.values());
    }

    /**
     * Add objects of the given type to the given list 
     * 
     * @param dest
     * @param type One of the ArtemisObject#TYPE_* constants
     * @return The number of objects added to "dest"
     */
    public synchronized int getObjects(List<ArtemisObject> dest, int type) {
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
     * Get the overall health of the given system
     * @param sys
     * @return A float [0, 1] indicating percentage health
     */
    public float getHealthOfSystem(SystemType sys) {
        if (mGrid == null)
            throw new IllegalStateException("SystemManager must have a ShipSystemGrid");
        
        final float total = mGrid.getSystemCount(sys);
        float current = total;
        for (GridCoord c : mGrid.getCoordsFor(sys))
            current -= mGridDamage.get(c);
        
        return current / total;
    }
    
    /**
     * Get the first object with the given name
     * @param type
     * @return
     */
    public synchronized ArtemisObject getObjectByName(String name) {
        for (ArtemisObject obj : mObjects.values()) {
            if (obj.getName().equals(name))
                return obj;
        }
        
        return null;
    }
    
    public boolean hasSystemGrid() {
        return mGrid != null;
    }

    public void setOnObjectCountChangedListener(OnObjectCountChangeListener listener) {
        mListener = (listener == null) ? sDummyListener : listener;
    }

    /**
     * Set the current ship's (fully loaded) grid
     * 
     * @param grid
     */
    public void setSystemGrid(ShipSystemGrid grid) {
        mGridDamage = new HashMap<GridCoord, Float>();
        mGrid = grid;
        
        // fill some default values
        for (GridCoord c : grid.getCoords()) {
            mGridDamage.put(c, 0f); // default
        }
    }

    public synchronized void clear() {
        mObjects.clear();
    }

//    @Override
//    public Iterator<ArtemisObject> iterator() {
//        return mObjects.values().iterator();
//    }
}
