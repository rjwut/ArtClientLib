package net.dhleong.acl.test;

import java.util.List;

import junit.framework.TestCase;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket.GridDamage;

public class EngGridTests extends TestCase {

    public void testGrid() {
        String[] tests = new String[] {
                "000204000000403f020407cdcccc3e0303010000803f0402060000003fff0b000000000000000003000000020000000300000003000000ee7c7f3f060000000c040000000300000002000000020000000700000007000000ee7c7f3f06000000fe"
        };
        
        GridDamage expectedDamages[][] = new GridDamage[][] {
                {
                    new GridDamage(2, 4, 0, 0.75f),
                    new GridDamage(2, 4, 7, 0.4f),
                    new GridDamage(3, 3, 1, 1.0f),
                    new GridDamage(4, 2, 6, 0.5f),
                }
        };
        
        final int len = tests.length;
        for (int i=0; i<len; i++) {
            String raw = tests[i];
            byte[] bytes = ObjectParsingTests.hexStringToByteArray(raw);
            EngGridUpdatePacket pkt = new EngGridUpdatePacket(0, bytes);
            
            pkt.debugPrint();
            
            List<GridDamage> damages = pkt.getDamage();
            final int dmgCount = damages.size();
            for (int j=0; j<dmgCount; j++) {
                assertEquals(expectedDamages[i][j], damages.get(j));
            }
        }
    }
}
