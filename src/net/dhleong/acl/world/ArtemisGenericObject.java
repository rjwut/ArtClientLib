package net.dhleong.acl.world;


public class ArtemisGenericObject extends BaseArtemisObject {
    
    public enum Type {
        MINE(ArtemisObject.TYPE_MINE),
        ANOMALY(ArtemisObject.TYPE_ANOMALY),
        NEBULA(ArtemisObject.TYPE_NEBULA),
        BLACK_HOLE(ArtemisObject.TYPE_BLACK_HOLE),
        ASTEROID(ArtemisObject.TYPE_ASTEROID);
        
        private final byte mIntType;

        Type(byte intType) {
            mIntType = intType;
        }
        
        public byte asInt() {
            return mIntType;
        }

        public static Type fromInt(byte targetType) {
            switch (targetType) {
            case ArtemisObject.TYPE_MINE:
                return MINE;
            case ArtemisObject.TYPE_ANOMALY:
                return ANOMALY;
            case ArtemisObject.TYPE_NEBULA:
                return NEBULA;
            case ArtemisObject.TYPE_BLACK_HOLE:
                return BLACK_HOLE;
            case ArtemisObject.TYPE_ASTEROID:
                return ASTEROID;
            }
            return null;
        }
    }
    
    private final Type mType;

    public ArtemisGenericObject(int objId, String name, Type type) {
        super(objId, (name == null ? type.toString() : name));
        
        mType = type;
    }

    @Override
    public int getType() {
        return mType.asInt();
    }

    @Override
    public String toString() {
        return mType + super.toString();
    }
}
