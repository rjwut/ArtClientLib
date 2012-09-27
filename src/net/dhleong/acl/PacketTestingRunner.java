package net.dhleong.acl;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.ObjUpdatePacket;

public class PacketTestingRunner {
    
    public static final void main(String[] args) {
        String[] tests = new String[]{
                "027c070000dd3a6f007c04000000460037003100000000000000cdcc4c3f6f12833b89130000a94ec3473250c3474215813ed0cc4cbed0cccc3e000096430000964300001643000016433518000000d27f023fe4353f3f16bb0a3f4f7fda3e1314d63e027d070000dd3a6f007c04000000500037003400000000000000cdcc4c3f6f12833b89130000a94ec347b2f89f414215813ed0cc4cbed0cccc3e000096430000964300001643000016433623000000e2f5f03e387ce83e6b121c3fc68b623ffe60e53e00000000",
                "02470c0000853a40027c040000005900330032000000000000002d0c9247c3c1ba464215813ed0cc4cbed0cccc3e22000000002821fa3e2cee623fc6ad2f3fb3ac0c3fe9fbc03e02480c0000ffffffff7f040000004f003100360000000000803f00000000cdcc4c3f6f12833b010000008a1300003df02b470000000001189f47000000004215813ed0cc4cbed0cccc3e0000000000af430000af430000964300009643010043100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000b0f5d73e2607133f1e09423f875ff63ebfba453f00000000",
                "0262080000ff3ffffd7f0400000047003900350000000000803f000000009a99993e6f12033b01000000d0070000b2606043000000006afb9c47000000004215013ed0ccccbd9a99993e0000204200002042000020420000204201000443000000000000000000000000000000000000000000000000000000000000000000000000000000000000001d7f0e3f4265543f8bde2b3f506ac13e2011433f00000000"
        };
        
        for (String s : tests) {
            byte[] bytes = hexStringToByteArray(s);
            if (!BaseArtemisPacket.byteArrayToHexString(bytes).equals(s))
                throw new RuntimeException("byte conversion fail");
            
            ObjUpdatePacket pkt = new ObjUpdatePacket(bytes);
            pkt.debugPrint();
            System.out.println("--> " + pkt);
        }
    }

    /** from stack overflow */
    public static byte[] hexStringToByteArray(String s) {
        s = s.toUpperCase(); // yes?
        
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
