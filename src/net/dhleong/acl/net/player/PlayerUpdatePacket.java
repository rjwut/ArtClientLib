package net.dhleong.acl.net.player;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Player packet handling is so crazy it gets its own PACKAGE.
 * See implementing classes MainPlayerUpdatePacket, etc.
 * 
 * @author dhleong
 *
 */
public abstract class PlayerUpdatePacket implements ArtemisPacket {

    final byte[] mData;

    public PlayerUpdatePacket(byte[] data) {
        mData = data;
    }

    @Override
    public long getMode() {
        return 0x01;
    }

    @Override
    public int getType() {
        return ArtemisPacket.WORLD_TYPE;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return false;
    }

    @Override
    public String toString() {
        return TextUtil.byteArrayToHexString(mData); 
    }

    public abstract void debugPrint();
    public abstract ArtemisPlayer getPlayer();

}
