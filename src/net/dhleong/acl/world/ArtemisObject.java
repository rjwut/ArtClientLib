package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

/**
 * Some sort of object in the World.
 *  Every object has a Type, a Name
 *  and an ID
 * @author dhleong
 */
public interface ArtemisObject {
    public int getId();
    public ObjectType getType();
    public String getName();
    public SortedMap<String, Object> getProps(boolean includeUnknown);
    public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnknown);
}