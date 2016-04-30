package client;

import java.util.ArrayList;
import java.util.List;

import decoder.BDecoder;
import utils.Event;

public class TorrentManager {
	private BDecoder metaInfo;
	private int peer_id;
	private List remainingPieces;
	private List<Peer> peers;
	private long uploaded;
	private long downloaded;
	private String ip;
	private int port;
	private long left;
	private Event event;
	
	public TorrentManager(int peer_id, String ip, int port, BDecoder metaInfo) {
		this.metaInfo = metaInfo;
		this.peer_id = peer_id;
		this.port = port;
		this.ip = ip;
		this.peers = new ArrayList<Peer>();
		this.remainingPieces = new ArrayList();
	}
	
	public void addPeer(Peer peer) {
		peers.add(peer);
	}
	
}
