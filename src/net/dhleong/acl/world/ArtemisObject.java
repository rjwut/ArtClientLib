package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;

/**
 * Some sort of object in the World.
 *  Every object has a Type, a Name
 *  and an ID
 * 
 * @author dhleong
 *
 */
public interface ArtemisObject {
    public ObjectType getType();
    public String getName();
    int getId();
}