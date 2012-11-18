package net.dhleong.acl;

import java.io.IOException;
import java.io.OutputStream;

public interface ArtemisPacket {

    /**
     * Special "type" of packet represented by old
     *  SystemInfoPacket; these packets create and/or
     *  update various world objects
     */
    public static final int WORLD_TYPE = 0x80803df9;

    public long getMode();
    
    public boolean write(OutputStream os) throws IOException;

    int getType();
}
