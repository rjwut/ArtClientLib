package net.dhleong.acl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.EnemyUpdatePacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.player.MainPlayerUpdatePacket;
import net.dhleong.acl.net.player.WeapPlayerUpdatePacket;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;
import net.dhleong.acl.test.ObjectParsingTests;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisObject;

/**
 * This is kind of a (huge) mess, but it won't be used in
 *  production code, so who cares?
 *  
 * @author dhleong
 *
 */
public abstract class PacketDemystifier implements OnPacketListener {
    
    /**
     * The Demystifier we want to use for this run
     */
    private static final OnPacketListener THIS_DEMYSITIFIER = 
            new EnemyPacketDemystifier();
            //new SimpleWorldPacketDemystifier(OtherShipUpdatePacket.class, ArtemisObject.TYPE_OTHER);

    static class UserPacketDemystifier extends SimpleWorldPacketDemystifier {
        
        public UserPacketDemystifier() {
            super( MainPlayerUpdatePacket.class, ArtemisObject.TYPE_PLAYER_MAIN);
        }
    }
    
    static class UserWeapPacketDemystifier extends SimpleWorldPacketDemystifier {
        
        public UserWeapPacketDemystifier() {
            super(WeapPlayerUpdatePacket.class,  ArtemisObject.TYPE_PLAYER_WEAP);
        }
    }
    static class EnemyPacketDemystifier extends SimpleWorldPacketDemystifier {

        public EnemyPacketDemystifier() {
            super(EnemyUpdatePacket.class, ArtemisObject.TYPE_ENEMY);
        }
        
    }
    
    /* Utility implementations */
    
    static class SimpleWorldPacketDemystifier extends WorldPacketDemystifier {
        
        private final Class<?> mClass;
        private final int mType;

        public SimpleWorldPacketDemystifier(Class<?> packetClass, int worldType) {
            mClass = packetClass;
            mType = worldType;
        }

        @Override
        protected Class<?> getPacketClass() {
            return mClass;
        }

        @Override
        protected int getWorldType() {
            return mType;
        }
        
    }
    
    static abstract class WorldPacketDemystifier extends SimplePacketDemystifier {
        
        private final LinkedList<String> flags = new LinkedList<String>();
        
        @Override
        protected void displayEntry(Entry e) {
            super.displayEntry(e);
            if (e.type == FieldType.FLAGS) {
                flags.clear();
                byte action = bytes[e.offset];
                System.out.println("Action = " + TextUtil.byteToHex(action));
                
                for (int i=0; i < 8; i++) {
                    byte val = (byte) (1 << i);
                    if ((action & val) != 0) {
                        String flag = TextUtil.byteToHex(val);
                        flags.addLast(flag);
                        System.out.println(" - " + flag);
                    }
                }
                
                int args = PacketParser.getLendInt(bytes, e.offset+1);
                System.out.println("Args = " + TextUtil.intToHex(args));
                for (int i=0; i < 32; i++) {
                    int val = (1 << i);
                    if ((args & val) != 0) {
                        String flag = TextUtil.intToHex(val);
                        flags.addLast(flag);
                        System.out.println(" - " + flag);
                    }
                }
            } else if (!flags.isEmpty()) {
                System.out.println(" --> 0x" + flags.removeFirst());
                System.out.println();
            }
        }
        
        protected abstract Class<?> getPacketClass() ;
        
        @Override
        protected int getPacketType() {
            return ArtemisPacket.WORLD_TYPE;
        }
        
        protected int getFlagBytes() {
            return 5;
        }
        
        @Override
        protected boolean isHandled(ArtemisPacket pkt) {
            return super.isHandled(pkt) 
                    && getWorldType() == ((BaseArtemisPacket) pkt).getData()[0];
        }

        protected abstract int getWorldType();
    }
    
    static abstract class SimplePacketDemystifier extends PacketDemystifier {
        
        @Override
        protected boolean isHandled(ArtemisPacket pkt) {
            return getPacketType() == pkt.getType();
        }
        
        protected abstract int getPacketType();
    }
    
    enum FieldType {
        BYTE,
        INT,
        FLOAT, 
        STRING,
        ID, 
        FLAGS
    }
    
    static class Entry {
        final int offset;
        FieldType type;
        String entryValue;
        
        private Entry(int offset) {
            this.offset = offset;
        }
        
        public Entry(int offset, byte byteVal) {
            this(offset);
            type = FieldType.BYTE;
            entryValue = String.valueOf(byteVal);
        }
        
        public Entry(int offset, int intVal) {
            this(offset, intVal, false);
        }
        
        public Entry(int offset, int intVal, boolean isId) {
            this(offset);
            type = isId ? FieldType.ID : FieldType.INT;
            entryValue = String.valueOf(intVal);
        }
        
        public Entry(int offset, float floatVal) {
            this(offset);
            type = FieldType.FLOAT;
            entryValue = String.valueOf(floatVal);
        }
        
        public Entry(int offset, String string) {
            this(offset);
            type = FieldType.STRING;
            entryValue = string;
        }
        
        public Entry(int offset, byte[] data, int flagsLength) {
            this(offset);
            type = FieldType.FLAGS;
            
            StringBuilder b = new StringBuilder();
            
//            final int width = 2; // 2 -> hex strings
            for (int i=offset; i < offset+flagsLength; i++) {
                
                if (i != offset)
                    b.append(":");
                
                b.append(TextUtil.byteToHex(data[i]));
            }
            
            entryValue = b.toString();
            
//            entryValue = String.format("(flags, len %d)", length);
        }

        @Override
        public String toString() {
            return String.format("@%3d--%6s=%s", offset, type, entryValue);
        }
    }

    
    /* member variables */
    byte[] bytes;
    private int offset;
    
    private final ArrayList<Entry> entries = new ArrayList<Entry>();
    
    
    /* Passes to implementation stuff */

    @Override
    public void onPacket(ArtemisPacket pkt) {
        if (isHandled(pkt)) {
            System.out.println("<< " + pkt);
            
            entries.clear();
            demystify((BaseArtemisPacket) pkt);
        }
        
    }
    
    
    protected synchronized void demystify(BaseArtemisPacket pkt) {
        bytes = pkt.getData();
        offset = 0;
//        offset = 1;
//        int id = PacketParser.getLendInt(bytes, offset);
//        System.out.println("id=" + id);
//        offset += 4;
        
//        offset += getFlagBytes();
        
        // first sweep
        while (offset+3 < bytes.length) {
            offset += guessNextType();
        }
        
//        boolean compact = false;
//        for (int i=0; ; i++) {
//            // manual re-evaluation
//            if (i > entries.size()-4)
//                break;
//            
//            // check for 4 in a row
//            compact = true;
//            for (int j=i; j < i+3; j++) {
//                if (entries.get(j).type != FieldType.BYTE) {
//                    compact = false;
//                    break;
//                }
//            }
//            
//            if (compact) {
//                // remove the extras
//                entries.remove(i+1);
//                entries.remove(i+1);
//                entries.remove(i+1);
//                
//                float floatVal = PacketParser.getLendFloat(bytes, i);
//                entries.set(i, new Entry(i, floatVal));
//            }
//        }
        
        displayEntries();
    }
    
    private void displayEntries() {
        for (Entry e : entries) {
            displayEntry(e);
//            if (e.type == FieldType.STRING) {
//                break;
//            }
        }        
    }
    
    protected void displayEntry(Entry e) {
        System.out.println(e.toString());        
    }

    /* Implement these */


    private int guessNextType() {
        float floatVal = PacketParser.getLendFloat(bytes, offset);
        if (floatVal > -20f && floatVal < 200000f
                && !((floatVal > 0 && floatVal < 0.00001)
                    || (floatVal < 0 && floatVal > -0.00001))) {
            entries.add(new Entry(offset, floatVal));
            return 4;
        } 
        
        int intVal = PacketParser.getLendInt(bytes, offset);
        if (intVal > -500 && intVal < 10000) {
            
            // hax...
            if (this instanceof WorldPacketDemystifier
                    && getLastType() == FieldType.BYTE 
                    && bytes[getLastEntry(1).offset] == 
                        ((WorldPacketDemystifier)this).getWorldType()) {
                // ooh, it's an ID!
                Entry last = entries.remove(entries.size()-1); // remove byte
                entries.add(new Entry(last.offset+1, intVal, true));
                
                // add the flags
                int flagBytes = ((WorldPacketDemystifier)this).getFlagBytes();
                entries.add(new Entry(last.offset+5, bytes, flagBytes));
                
                return 4 + flagBytes; 
            } else {
                // just an int
                entries.add(new Entry(offset, intVal));
                return 4;
            }
        }
        
//        System.out.println(getLastType() + "; " + bytes[offset+1]);
        byte val = bytes[offset];
        int stringLen;
        if (getLastType() == FieldType.INT
                && offset < bytes.length-1
                && bytes[offset+1] == 0
                && (stringLen = Integer.parseInt(getLastValue())) < 200) {
            // oh shit, this is probably a string!
            StringBuilder buf = new StringBuilder();
            
//            while (bytes[offset] != 0) {
            for (int i=0; i<stringLen; i++) {
                char charVal = (char) bytes[offset];
                if (charVal != 0)
                    buf.append(charVal);
                offset += 2;
            }
            
            String stringVal = buf.toString();
            Entry sizeEntry = entries.remove(entries.size()-1); // remove the int
            entries.add(new Entry(sizeEntry.offset, stringVal));
            
            return 2 + 2 * stringVal.length(); // skip the null byte
        }
        
        if (offset > 3 && getLastType() == FieldType.BYTE
                && getLastType(2) == FieldType.BYTE
                && getLastType(3) == FieldType.BYTE) {
            // 4 bytes in a row? probably actually a float!
            entries.remove(entries.size()-1); // remove the int
            entries.remove(entries.size()-1); // remove the int
            entries.remove(entries.size()-1); // remove the int
            
            float compactedFloat = PacketParser.getLendFloat(bytes, offset-3);
            entries.add(new Entry(offset-3, compactedFloat));
            return 1; // the new byte
        }
        
//        // don't think there are any negative bytes...
//        if (val < 0) {
//            int start;
//            for (start=1; start<4; start++) {
//                if (getLastType(start) == FieldType.BYTE) {
//                    // remove the old byte while here
//                    entries.remove(entries.size() - 1);                
//                } else if (start == 1) {
//                    start = 0;
//                    break;
//                }
//            }
//            
//            if (start == 0) {
//                entries.add(new Entry(offset, PacketParser.getLendFloat(bytes, offset)));
//                return 4;
//            } else {
//                final int startPos = offset - start;
//                entries.add(new Entry(startPos, PacketParser.getLendFloat(bytes, startPos)));
//                return 4-start;
//            }
//        }
        
        // just a byte, I guess
        entries.add(new Entry(offset, val));
        
        return 1;
    }

    private Entry getLastEntry(int offset) {
        final int size = entries.size();
        if (size-offset >= 0)
            return entries.get(size-offset);
        
        return null;
    }
    
    private FieldType getLastType() {
        return getLastType(1);
    }

    private FieldType getLastType(int offset) {
        Entry last = getLastEntry(offset);
        if (last == null)
            return null;
        
        return last.type;
    }
    
    private String getLastValue() {
        Entry last = getLastEntry(1);
        if (last == null)
            return null;
        
        return last.entryValue;
    }


    protected abstract boolean isHandled(ArtemisPacket pkt);
    
    /* Runner */

    public static final void main(String[] args) {
//        demystTest();
        demystNetwork();
    }
    
    private static void demystTest() {
        
        //String raw = "01f8030000bc2af924000000003f9a99193f6f12833b0179007a440100000019184e4776c44a47db0f49400800000041007200740065006d006900730000000000a0420000a0420000a0420000a042005043480800000000";
        //String raw = "0475040000fb3a5f007c0400000049003400340000000000803f9a99993e6f12033b01000000d107000050896d472650c3474215013ed0ccccbd9a99993e0000a0420000a0420000a0420000a042010030000000a8a9203ff0bbc43e6c5b363fe644263fe234243f0476040000fb3a5f007c0400000058003100350000000000803f9a99993e6f12033b01000000d0070000501f6e472650c3474215013ed0ccccbd9a99993e000020420000204200002042000020420100060000005c18943e04d01b3f9d539b3e4495553ffacb163f00000000";
        String raw = "0279060000ef075408020604030012000200000069640003150015002002f70079002e0001f6ce00000000";
        byte[] bytes = ObjectParsingTests.hexStringToByteArray(raw);
        BaseArtemisPacket pkt = new BaseArtemisPacket(0, 0, ArtemisPacket.WORLD_TYPE, bytes);
        THIS_DEMYSITIFIER.onPacket(pkt);
    }
     
    @SuppressWarnings("unused")
    private static void demystNetwork() {
        String tgtIp = "10.211.55.3";
        final int tgtPort = 2010;
        
        final ThreadedArtemisNetworkInterface net; 
        try {
            net = new ThreadedArtemisNetworkInterface(tgtIp, tgtPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        PacketParser dummy = new PacketParser();
        dummy.setNoParseMode(true);
        
        net.setPacketParser(dummy);
        
        net.addOnPacketListener(THIS_DEMYSITIFIER);
        
        net.start();
        
        net.send(new ReadyPacket2());
        net.send(new ReadyPacket2());
        
        net.send(new SetStationPacket(StationType.HELM, true));
        net.send(new ReadyPacket());
        
        //net.send(new EngSetEnergyPacket(SystemType.IMPULSE, 300));
//        
//        try{ Thread.sleep(750); } catch (Throwable e) {}
//        System.out.println("********READY 2 NOW");
//        net.send(new ReadyPacket2());
    }
}
