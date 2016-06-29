package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

import decoder.BDecodeException;
import decoder.BDecoder;
import utils.Constants;

public class Client {
	
	protected byte[] id = new byte[20];
	public String torrent;
	public FileInputStream in;
	public Map meta;
	private BDecoder decoder;
	private ServerSocket serverSocket;
	private Integer port;
	
	Client () {	
		
		Random random = new Random();
        int i;
        for (i = 0; i < 20; i++) {
            id[i++] = (byte)random.nextInt(256);
        }
		
		System.out.println("peer = " + id);
	}
	
	public void prepareStorage() {
		
		
	}
	
	public void getTorrentPieces() {
		
		prepareStorage();
		
	}
	
	public void processTorrent(File f) throws BDecodeException, IOException, ConnectionException, NoSuchAlgorithmException {
		
		for (int port= Constants.MIN_PORT; serverSocket == null && port < Constants.MAX_PORT; ++port) {
			serverSocket = new ServerSocket(port);
		}
		
		if (serverSocket == null) {
			throw new ConnectionException("Cannot accept connections");
		}
		port = serverSocket.getLocalPort();
		
		//in = new FileInputStream(f);
		decoder = new BDecoder(f);
		//System.out.println("Map = " +  decoder.getMetaDict());
		
		getTorrentPieces();
		
		decoder.prettyPrintMeta();
		TorrentManager manager = new TorrentManager(id, "localhost", port, decoder);
		TrackerConnection conn = new TrackerConnection(decoder, manager);
		conn.start();
		
	}
	
	
}
