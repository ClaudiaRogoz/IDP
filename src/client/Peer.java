package client;

import java.util.Map;

public class Peer {
	private byte[] peer_id;
	private String ip;
	private int port;

	public Peer(byte[] peerId, String ip, int port) {
		this.peer_id = peerId;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString() {
		return "Client " + peer_id.toString() + " " + ip + " " + port;
		
	}
	
	public byte[] getPeerId() {
		return peer_id;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	
}
