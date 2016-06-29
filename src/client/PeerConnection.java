package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import utils.Constants;

public class PeerConnection extends Thread{

	private Socket sock;
	private Peer localPeer;
	private Peer remotePeer;
	private BufferedInputStream is;
	private BufferedOutputStream os;
	private DataOutputStream dos;
	private DataInputStream dis;
	private byte[] info_hash;
	private PeerTransmiter pT;
	private PeerReceiver pR;
	private TorrentManager manag;
	
	public PeerConnection(Peer localPeer, Peer remotePeer, byte[] info_hash, TorrentManager manag) {
		try {
			System.out.println("Trying to connect to " + remotePeer.getIp() + " " + remotePeer.getPort() + " " + info_hash);
			sock = new Socket(remotePeer.getIp(), remotePeer.getPort());
			is = new BufferedInputStream(sock.getInputStream());
	        os = new BufferedOutputStream(sock.getOutputStream());
	        this.localPeer = localPeer;
	        this.remotePeer = remotePeer;
	        this.info_hash = info_hash;
	        this.manag = manag;
	        createHandshake();
	        System.out.println("[DONE]Handshake with " + remotePeer.getPeerId() + "DONE");
	        pT = new PeerTransmiter(this);
	        pR = new PeerReceiver(this);
	        pT.start();
	        
		} catch (IOException e) {
			System.out.println("[ERROR]Connection refused");
			//manag.removePeer(remotePeer);
		}
		
	}
	
	
	@Override
	public void run(){
		//BitfieldSender bs = new BitfieldSender(this.manag.bitfield);
		//pT.sendBitField(bs);
		//pR.start();
		
	}
	
	
	private void createHandshake() throws IOException {
		
		dis = new DataInputStream(is);
		dos = new DataOutputStream(os);
		
		//https://wiki.theory.org/BitTorrentSpecification#Peer_wire_protocol_.28TCP.29
		dos.write(19);
		dos.write(Constants.PROTOCOL.getBytes("UTF-8"));
		byte[] reserved = new byte[8];
		dos.write(reserved);
		System.out.println(info_hash + "" + localPeer.getPeerId());
		dos.write(info_hash);
		dos.write(localPeer.getPeerId());
		dos.flush();
		
		//simmetrical response
		byte b = dis.readByte();
        if (b != 19) {
            throw new IOException("Handshake failure, expected 19, got "
                + (b & 0xff));
        }

        byte[] bs = new byte[19];
        dis.readFully(bs);
        String bittorrentProtocol = new String(bs, "UTF-8");
        if (!"BitTorrent protocol".equals(bittorrentProtocol)) {
            throw new IOException("Handshake failure, expected "
                + "'Bittorrent protocol', got '" + bittorrentProtocol + "'");
        }

        dis.readFully(reserved);
        

        bs = new byte[20];
        dis.readFully(bs);
        if (!Arrays.equals(info_hash, bs)) {
            throw new IOException("Unexpected MetaInfo hash");
        }

        dis.readFully(bs);
		
	}
	
	public DataOutputStream getDos() {
		return dos;
	}
}
