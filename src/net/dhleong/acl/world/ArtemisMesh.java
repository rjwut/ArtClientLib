package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.util.TextUtil;

/**
 * This is some "generic mesh" in the world
 * 
 * @author dhleong
 *
 */
public class ArtemisMesh extends BaseArtemisObject {
    private String mMesh;
    private String mTex;
    private int mColor;
    private float mShieldsFront = Float.MIN_VALUE;
    private float mShieldsRear = Float.MIN_VALUE;

    public ArtemisMesh(int objId, String name) {
        super(objId, name);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.GENERIC_MESH;
    }
    
    public String getMesh() {
        return mMesh;
    }
    
    public void setMesh(String path) {
        mMesh = path;
    }
    
    public String getTexture() {
        return mTex;
    }
    
    public void setTexture(String path) {
        mTex = path;
    }
    
    public int getColor() {
        return mColor;
    }
    
    public float getShieldsFront() {
        return mShieldsFront;
    }
    
    public float getShieldsRear() {
        return mShieldsRear;
    }
    
    public void setARGB(int a, int r, int g, int b) {
        mColor = 0;
        mColor |= ((a & 0xff) << 24);
        mColor |= ((r & 0xff) << 16);
        mColor |= ((g & 0xff) << 8);
        mColor |= (b & 0xff);
    }
    
    public void setARGB(float a, float r, float g, float b) {
        setARGB(
            (int)(255 * a),
            (int)(255 * r), 
            (int)(255 * g), 
            (int)(255 * b)
        );
    }

    @Override
    public void updateFrom(ArtemisPositionable other) {
        super.updateFrom(other);
        
        ArtemisMesh m = (ArtemisMesh) other;
        if (m.mShieldsFront != Float.MIN_VALUE) {
            mShieldsFront = m.mShieldsFront;
        }
        
        if (m.mShieldsRear != Float.MIN_VALUE) {
            mShieldsRear = m.mShieldsRear;
        }
    }

    public void setFakeShields(float shieldsFront, float shieldsRear) {
        mShieldsFront = shieldsFront;
        mShieldsRear = shieldsRear;
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Mesh", mMesh, includeUnspecified);
    	putProp(props, "Texture", mTex, includeUnspecified);
    	putProp(props, "Color", mColor, 0, includeUnspecified);
    	putProp(props, "Shields: fore", mShieldsFront, Float.MIN_VALUE, includeUnspecified);
    	putProp(props, "Shields: aft", mShieldsRear, Float.MIN_VALUE, includeUnspecified);
    }
}