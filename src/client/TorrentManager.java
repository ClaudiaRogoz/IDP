package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import decoder.BDecoder;
import utils.Event;

public class TorrentManager {
	private BDecoder metaInfo;
	private List remainingPieces;
	private List<Peer> peers;
	private long uploaded;
	private long downloaded;
	private String ip;
	private int port;
	private long left;
	private Event event;
	private byte[] peer_id;
	private int interval;
	private byte[] info_hash;
	private int pieces;
	public byte[] bitfield;
	
	public TorrentManager(byte[] id, String ip, int port, BDecoder metaInfo) {
		this.setMetaInfo(metaInfo);
		this.setPeer_id(id);
		this.setPort(port);
		this.setIp(ip);
		this.setLeft(metaInfo.getLength());
		this.peers = new ArrayList<Peer>();
		this.setRemainingPieces(new ArrayList());
		//pieces maps to a string whose length is a multiple of 20. 
		//It is to be subdivided into strings of length 20, each of which is the SHA1 
		//hash of the piece at the corresponding index. = no of bitfields
		//we must compute from bits to bytes
		this.pieces = (metaInfo.getPieces().length /20 -1)/8 + 1; 
		this.bitfield = new byte[pieces];
		
	}
	
	public void pieceDownload (int pieceNo) {
		int idx = pieceNo /8;
		bitfield[idx] |= 128 >> (pieceNo % 8);	
	}
	
	public void setHash(byte[] hash){
		
		this.info_hash = hash;
	}
	public void addPeer(Peer peer) {
		peers.add(peer);
	}

	public byte[] getPeer_id() {
		return peer_id;
	}

	public void setPeer_id(byte[] id) {
		this.peer_id = id;
	}

	public void setPeers(List<Peer> peers) throws UnknownHostException, NoSuchAlgorithmException, IOException {
		
		for (Peer peer : peers) {
			
			processPeer(peer);
			
		}
	}
	
	public void processPeer(Peer peer) throws UnknownHostException, NoSuchAlgorithmException, IOException {
		synchronized(this.peers) {
			this.peers.add(peer);
			PeerConnection conn = new PeerConnection(new Peer(peer_id, ip, port), peer, info_hash, this);
			conn.start();
		}
		
	}
	
	public void removePeer(Peer peer) {
		synchronized (this.peers) {
			this.peers.remove(peer);
		}	
	}
	
	public List getRemainingPieces() {
		return remainingPieces;
	}

	public void setRemainingPieces(List remainingPieces) {
		this.remainingPieces = remainingPieces;
	}

	public BDecoder getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(BDecoder metaInfo) {
		this.metaInfo = metaInfo;
	}

	public long getUploaded() {
		return uploaded;
	}

	public void setUploaded(long uploaded) {
		this.uploaded = uploaded;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getLeft() {
		return left;
	}

	public void setLeft(long left) {
		this.left = left;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
	
}
