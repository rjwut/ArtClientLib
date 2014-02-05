package net.dhleong.acl.iface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks methods which are to be invoked when a packet is
 * received. To be eligible to be a PacketListener, a method must: 1) be public,
 * 2) have a void return type, and 3) have exactly one argument of type
 * ArtemisPacket or any of its subtypes. The method will only be notified of
 * packets that are assignable to the argument's type.
 * 
 * Annotating the method alone is not enough to get notifications; you must
 * register the object that has the annotated method with the
 * ArtemisNetworkInterface implementation that will be receiving the packets.
 * 
 * @author rjwut
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface PacketListener {
	// no properties
}