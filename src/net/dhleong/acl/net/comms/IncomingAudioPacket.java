package net.dhleong.acl.net.comms;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;

/**
 *  
 * @author dhleong
 *
 */
public class IncomingAudioPacket implements ArtemisPacket {

    public static final int TYPE = 0xae88e058;
    
    /** 
     * The server has commenced playback;
     * {@link #getFileName()} and {@link #getTitle()}
     * will return NULL for these packets  
     */
    public static final int MODE_PLAYING  = 1;
    
    /**
     * Incoming audio; {@link #getFileName()}
     *  and {@link #getTitle()} will be populated
     */
    public static final int MODE_INCOMING = 2;
    
    private final int mId;
    private final String mTitle;
    private final String mFile;
    private final int mMode;

    public IncomingAudioPacket(byte[] bucket) {
        ObjectParser p = new ObjectParser(bucket, 0);

        mId = p.readInt();
        
        // what is this?
        mMode = p.readInt();
        
        if (mMode == MODE_INCOMING) {
            mTitle = p.readName();
            mFile = p.readName();
        } else {
            mTitle = null;
            mFile = null;
        }
    }
    
    public int getAudioId() {
        return mId;
    }
    
    public String getFileName() {
        return mFile;
    }
    
    public String getTitle() {
        return mTitle;
    }

    @Override
    public long getMode() {
        return 0x01;
    }
    
    /**
     * @see {@link #MODE_INCOMING} and {@link #MODE_PLAYING}
     * @return
     */
    public int getAudioMode() {
        return mMode;
    }
    
    /**
     * Convenience to check if this is a "new" message
     * @return
     */
    public boolean isIncoming() {
        return mMode == MODE_INCOMING;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        // nop; incoming only
        return false;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
