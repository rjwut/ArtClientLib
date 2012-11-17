package net.dhleong.acl.world;

/**
 * There are many "generic" objects which
 *  are very similar in implementation
 *  
 * @author dhleong
 *
 */
public class ArtemisGenericObject extends BaseArtemisObject {
    
    public enum Type {
        MINE(ArtemisObject.TYPE_MINE, false),
        ANOMALY(ArtemisObject.TYPE_ANOMALY, true),
        NEBULA(ArtemisObject.TYPE_NEBULA, false),
        TORPEDO(ArtemisObject.TYPE_TORPEDO, false),
        BLACK_HOLE(ArtemisObject.TYPE_BLACK_HOLE, true),
        ASTEROID(ArtemisObject.TYPE_ASTEROID, false),
        MONSTER(ArtemisObject.TYPE_MONSTER, true),
        WHALE(ArtemisObject.TYPE_WHALE, true);
        
        private final byte mIntType;
        
        /** Whether or not this type can have a name */
        public final boolean hasName;

        Type(byte intType, boolean hasName) {
            mIntType = intType;
            this.hasName = hasName;
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
            case ArtemisObject.TYPE_TORPEDO:
                return TORPEDO;
            case ArtemisObject.TYPE_BLACK_HOLE:
                return BLACK_HOLE;
            case ArtemisObject.TYPE_ASTEROID:
                return ASTEROID;
            case ArtemisObject.TYPE_MONSTER:
                return MONSTER;
            case ArtemisObject.TYPE_WHALE:
                return WHALE;
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
