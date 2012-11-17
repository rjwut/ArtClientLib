package net.dhleong.acl.world;

/**
 * This is some "generic mesh" in the world
 * 
 * @author dhleong
 *
 */
public class ArtemisMesh extends BaseArtemisObject {

    public ArtemisMesh(int objId, String name) {
        super(objId, name);
    }

    @Override
    public int getType() {
        return ArtemisObject.TYPE_MESH;
    }

    @Override
    public String toString() {
        return String.format("%s%s", getName(), super.toString());
    }
}
