package net.dhleong.acl;

import java.io.IOException;

import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.SetStationPacket;
import net.dhleong.acl.net.setup.SetStationPacket.StationType;

public class ArtemisTest {
	public static void main(String[] args) throws IOException {
        ThreadedArtemisNetworkInterface server = new ThreadedArtemisNetworkInterface(args[0], 2010);
        server.addOnPacketListener(new OnPacketListener() {
    	  @Override
    	  public void onPacket(ArtemisPacket pkt) {
    	    System.out.println(pkt);
    	  }
    	});
        server.start();
        server.send(new SetStationPacket(StationType.SCIENCE, true));
        server.send(new ReadyPacket());
	}
}