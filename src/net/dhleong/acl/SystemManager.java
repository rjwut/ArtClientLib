package net.dhleong.acl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.EngGridUpdatePacket;
import net.dhleong.acl.net.EngGridUpdatePacket.GridDamage;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.EngSystemUpdatePacket;
import net.dhleong.acl.net.EngSystemUpdatePacket.BoolState;
import net.dhleong.acl.net.ObjUpdatePacket;
import net.dhleong.acl.net.ObjUpdatePacket.ObjUpdate;
import net.dhleong.acl.net.SysCreatePacket;
import net.dhleong.acl.net.SystemInfoPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.util.ShipSystemGrid;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.BaseArtemisShip;

/**
 * 
 * @author dhleong
 *
 */
public class SystemManager implements OnPacketListener, Iterable<ArtemisObject> {
    
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
        mObjects.put(obj.getId(), obj);
        mListener.onObjectCountChanged(mObjects.size());
    }

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
                mObjects.put(obj.getId(), obj);
                
                if (DEBUG) System.out.println("SystemManager#created: " + obj);
            }
            
            if (DEBUG) System.out.println("--> " + create);
            
            if (newObjs.size() > 0) {
                // signal change
                mListener.onObjectCountChanged(mObjects.size());
            }
        } else if (EngSystemUpdatePacket.isExtensionOf(info)) {
            EngSystemUpdatePacket eng = new EngSystemUpdatePacket(info);
            
            ArtemisPlayer p = (ArtemisPlayer) mObjects.get(info.getTarget());
            if (p != null) {
                
                if (eng.getRedAlert() != BoolState.UNKNOWN) {
                    p.setRedAlert(eng.getRedAlert().getBooleanValue());
                }
                
                if (eng.hasShields()) {
                    
                    if (eng.mShieldsFront > -1)
                        p.setFrontShields(eng.mShieldsFront);
                    if (eng.mShieldsMaxFront > -1)
                        p.setFrontShieldsMax(eng.mShieldsMaxFront);
                    if (eng.mShieldsRear > -1)
                        p.setRearShields(eng.mShieldsRear);
                    if (eng.mShieldsMaxRear > -1)
                        p.setRearShieldsMax(eng.mShieldsMaxRear);
                    
                }
            
                if (eng.x != -1) p.setX(eng.x);
                if (eng.y != -1) p.setY(eng.y);
                if (eng.z != -1) p.setZ(eng.z);
                if (eng.bearing != Float.MIN_VALUE) p.setBearing(eng.bearing);
            }
        } else if (ObjUpdatePacket.isExtensionOf(info)) {
            
            ObjUpdatePacket e = new ObjUpdatePacket(info);
            
            for (ObjUpdate eng : e.mUpdates) {
                BaseArtemisShip p = (BaseArtemisShip) mObjects.get(eng.targetId);
                if (p != null) {
                    
                    if (eng.x != -1) p.setX(eng.x);
                    if (eng.y != -1) p.setY(eng.y);
                    if (eng.z != -1) p.setZ(eng.z);
                    if (eng.bearing != Float.MIN_VALUE) p.setBearing(eng.bearing);
                }
            }
        }
    }

    public void getAll(List<ArtemisObject> dest) {
        dest.addAll(mObjects.values());
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

    public void clear() {
        mObjects.clear();
    }

    @Override
    public Iterator<ArtemisObject> iterator() {
        return mObjects.values().iterator();
    }
}
