package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;

/**
 * From the server to share what engines, ship types, 
 *  and ship names will be used
 *  
 * @author dhleong
 *
 */
public class JumpStatusPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    
    /**
     * Jump "begin"; that is, the countdown has begun
     */
    public static final byte MSG_TYPE_BEGIN = 0x0c;
    
    /**
     * Jump "end"; there's still some cooldown (~5 seconds)
     */
    public static final byte MSG_TYPE_END = 0x0d;

    private final boolean isBegin;

    
    public JumpStatusPacket(byte[] bucket) {
        super(0x01, TYPE, bucket);
        isBegin = bucket[0] == MSG_TYPE_BEGIN;
    }
    
    /**
     * Is this the countdown started packet?
     * @return
     */
    public boolean isCountdown() {
        return isBegin;
    }
    
    @Override
    public String toString() {
        return isBegin ? "[JUMP BEGIN]" : "[JUMP COMPLETE]";
    }
}