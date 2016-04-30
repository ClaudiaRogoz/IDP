package client;

import java.util.Map;

import decoder.BDecoder;

public class TrackerConnection extends Thread{
	BDecoder metaInfo;
	Integer port;
	
	TrackerConnection(BDecoder metaInfo, int port) {
		this.metaInfo = metaInfo;
		this.port = port;
		
		
	}
	
	protected void createRequest() {
		
		
	}
	
	@Override
    public void run ()
    {
		String announce = metaInfo.getAnnounce();
		/* TODO info_hash!!! */
		//String info_hash = m
		//String peer_id = 
    }
}
