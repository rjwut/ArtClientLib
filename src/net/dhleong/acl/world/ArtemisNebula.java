package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisNebula extends BaseArtemisObject {
    private boolean hasColor;
	private int mColor;

	public ArtemisNebula(int objId) {
        super(objId, "NEBULA");
    }

	@Override
	public ObjectType getType() {
		return ObjectType.NEBULA;
	}

    public boolean hasColor() {
    	return hasColor;
    }

    /**
     * The color of the nebula. This is specified as an ARGB int value. To
     * specify each channel separately, use the setARGB() method.
     * Unspecified: 0
     */
    public int getColor() {
        return mColor;
    }

    public int getAlpha() {
    	return (mColor >>> 24) & 0xff;
    }

    public int getRed() {
    	return (mColor >>> 16) & 0xff;
    }

    public int getGreen() {
    	return (mColor >>> 8) & 0xff;
    }

    public int getBlue() {
    	return mColor & 0xff;
    }

    /**
     * Sets the color of the nebula, specifying each channel as a value between
     * 0 and 255.
     */
    public void setARGB(int a, int r, int g, int b) {
        mColor = 0;
        mColor |= ((a & 0xff) << 24);
        mColor |= ((r & 0xff) << 16);
        mColor |= ((g & 0xff) << 8);
        mColor |= (b & 0xff);
    	hasColor = true;
    }
    
    /**
     * Sets the color that will be used to render this object on sensor views,
     * specifying each channel as a value between 0 and 1.
     */
    public void setARGB(float a, float r, float g, float b) {
        setARGB(
            (int)(255 * a),
            (int)(255 * r), 
            (int)(255 * g), 
            (int)(255 * b)
        );
    }
}