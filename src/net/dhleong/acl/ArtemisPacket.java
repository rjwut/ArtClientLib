package net.dhleong.acl;

import java.io.IOException;
import java.io.OutputStream;

public interface ArtemisPacket {

    public long getMode();
    
    public boolean write(OutputStream os) throws IOException;

    int getType();
}
