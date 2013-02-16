package net.dhleong.acl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.util.TextUtil;

public class TraceDumper {

    private static class HexDecodingIS extends InputStream {

        private final InputStream mWrapped;

        public HexDecodingIS(InputStream bufferedInputStream) {
            mWrapped = bufferedInputStream;
        }

        @Override
        public int read() throws IOException {
            char read1 = (char) mWrapped.read();
            char read2 = (char) mWrapped.read();
            if (read1 == -1 || read2 == -1)
                return -1;
            //System.out.println(read1+read2 + " -> " + TextUtil.hexToInt(read1, read2));
            return TextUtil.hexToInt(read1, read2);
        }

    }

    public TraceDumper(String filePath) {
        try {
            System.out.println("Tracing: " + filePath);
            InputStream is = new HexDecodingIS(new BufferedInputStream(new FileInputStream(new File(filePath))));
            PacketParser parser = new PacketParser();
            
            while (true) {
                final ArtemisPacket pkt = parser.readPacket(is);
                System.out.println("--> " + pkt);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ArtemisPacketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TraceDumper(args.length > 0 ? args[0] : "/Users/dhleong/code/android/artemis-client/scanning-stream.full");
    }
}
