package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Random;

import decoder.BDecodeException;
import decoder.BDecoder;
import utils.Constants;

public class Client {
	
	public String peer_id = "";
	public String torrent;
	protected byte[] id = new byte[20];
	public FileInputStream in;
	public Map meta;
	private BDecoder decoder;
	private ServerSocket serverSocket;
	private Integer port;
	
	Client () {	
		Random rand = new Random();
	
		for (int i = 0; i< Constants.PEER_LENGTH; i++)
			 peer_id.concat(String.valueOf((char)rand.nextInt(256)));
	}
	
	public void prepareStorage() {
		
		
	}
	
	public void getTorrentPieces() {
		
		prepareStorage();
		
	}
	
	public void processTorrent(File f) throws BDecodeException, IOException, ConnectionException {
		
		for (int port= Constants.MIN_PORT; serverSocket == null && port < Constants.MAX_PORT; ++port) {
			serverSocket = new ServerSocket(port);
		}
		
		if (serverSocket == null) {
			throw new ConnectionException("Cannot accept connections");
		}
		port = serverSocket.getLocalPort();
		
		in = new FileInputStream(f);
		decoder = new BDecoder(in);
		//System.out.println("Map = " +  decoder.getMetaDict());
		
		getTorrentPieces();
		
		decoder.prettyPrintMeta();
		
	}
	
	
}
