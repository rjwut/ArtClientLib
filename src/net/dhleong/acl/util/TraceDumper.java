package net.dhleong.acl.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.ListenerRegistry;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.core.world.MainPlayerUpdatePacket;

public class TraceDumper {

    private static class HexDecodingIS extends InputStream {

        private final InputStream mWrapped;

        public HexDecodingIS(InputStream bufferedInputStream) {
            mWrapped = bufferedInputStream;
        }

        @Override
        public int read() throws IOException {
            int read1 = mWrapped.read();
            if (read1 == -1)
                return -1;
            
            int read2 = mWrapped.read();
            if (read2 == -1)
                return -1;
            //System.out.println(read1+":"+read2 + " -> " + TextUtil.hexToInt((char)read1, (char)read2));
            return TextUtil.hexToInt((char) read1, (char) read2);
        }

    }

    public TraceDumper(String filePath) {
        try {
            System.out.println("Tracing: " + filePath);
            InputStream baseIs = new BufferedInputStream(new FileInputStream(new File(filePath)));
            InputStream is = new HexDecodingIS(baseIs);
            PacketReader reader = new PacketReader(
            		ConnectionType.SERVER, is,
            		new PacketFactoryRegistry(),
            		new ListenerRegistry()
            );
            
            while (true) {
                final ArtemisPacket pkt = reader.readPacket();
                if (pkt != null && filter(pkt))
                    System.out.println("--> " + pkt);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ArtemisPacketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static boolean filter(ArtemisPacket pkt) {
        return (pkt instanceof MainPlayerUpdatePacket);
    }

    public static void main(String[] args) {
        new TraceDumper(args.length > 0 ? args[0] : "/Users/dhleong/code/android/artemis-client/scanning-stream.full");
    }
}
