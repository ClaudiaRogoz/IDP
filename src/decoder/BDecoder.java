package decoder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.Peer;
import utils.Constants;



public class BDecoder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, BDGenericValue> map; 
	private InputStream in;
	private int currentValue;
	private String announce = "";
	private String name = "";
	private int piece_length = 0;
	private byte[] pieces;
	private long length = 0;
	private List<Long> lengths = null;
	private List<List<String>> paths = null;
	private boolean toRead = true;
	private byte[] info_hash;
	public int interval;
	public List<Peer> peers;
	
	public BDecoder(File f) throws BDecodeException, IOException, NoSuchAlgorithmException {
		InputStream is = new FileInputStream(f);
		String torrentFile = convertStreamToString(is);
		int infoOffset = torrentFile.indexOf("infod");
		this.in = new FileInputStream(f);
		
		map = processValueAsDict().toDict();
		getMetaData();
	}
	
	public BDecoder(InputStream in) throws BDecodeException, IOException {
		this.in = in;
		this.peers = new ArrayList();
		getTrackerList();
		

	}

	public void getTrackerList() throws BDecodeException, IOException {
		
		map = processValueAsDict().toDict();
		System.out.println(map);
		BDGenericValue genInterval = (BDGenericValue)map.get(Constants.INTERVAL);
		interval = genInterval.toNumber().intValue();
		BDGenericValue genPeers = (BDGenericValue)map.get(Constants.PEERS);
		List<BDGenericValue> listPeers = ((BDGenericValue) genPeers).toList();

		Iterator<BDGenericValue> it = listPeers.iterator();
        while (it.hasNext()) {
        	Map<String, BDGenericValue> mapPeer = ((BDGenericValue)it.next()).toDict();
        	
        	byte[] peerId = mapPeer.get(Constants.PEER_ID).toBytes();
        	String peerIp = mapPeer.get(Constants.IP).toChars();
        	int port = mapPeer.get(Constants.PORT).toNumber().intValue();
            peers.add(new Peer(peerId, peerIp, port));
        }
		
	}

	static byte[] convertStreamtoByte(InputStream is) throws IOException {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[20];

				try {
					while ((nRead = is.read(data, 0, data.length)) != -1) {
					  buffer.write(data, 0, nRead);
					}
					buffer.flush();
					
					return buffer.toByteArray();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return data;
				
		
	}
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	boolean isDictionary(int currentValue) {
		//System.out.println("" + currentValue == Constants.DICTIONARY + "\n");
		return currentValue == Constants.DICTIONARY;
	}
	
	boolean isList() {
		return currentValue == Constants.LIST;
	}
	
	boolean isNumber() {
		return currentValue == Constants.NUMBER;
	}
	
	
	boolean didFinish() {
		return currentValue == Constants.FINISHED;
		
	}
	
	int inRead() throws IOException {
		
		if (toRead) {
			toRead = false;
			return in.read();
		} 
		
		return currentValue;
	}
	
	void sanityCheck(int list) throws IOException, BDecodeException {
		currentValue = inRead();
		//System.out.println("value = " + (char)currentValue + "\n");
		switch(list){
		case Constants.DICTIONARY : 
			if (!isDictionary(currentValue)) {
				in.close();
				throw new BDecodeException("Incorrect format! Expected 'd' obtained " + (char)currentValue);
			}
			//System.out.println("Is a dict");
			break;
		case Constants.LIST : 
			if (!isList()) {
				in.close();
				throw new BDecodeException("Incorrect format! Expected 'l' obtained " + (char)currentValue);
				
			}
			//System.out.println("Is a list");
			break;
		case Constants.NUMBER : 
			if (!isNumber()) {
				in.close();
				throw new BDecodeException("Incorrect format! Expected 'i' obtained " + (char)currentValue);
			}
			//System.out.println("Is a no = " + (char)currentValue);
			break;
		}
	}
	
	boolean isMetaNumber(){
		return currentValue >= '1' && currentValue <='9';
	}
	
	BDGenericValue processCurrentValues() throws IOException, BDecodeException{
		
		currentValue = inRead();
		//System.out.println("CurrentProcess = " + (char)currentValue);
		switch(currentValue) {
		case Constants.NUMBER : 
			return processValueAsNumber();
		case Constants.LIST : 
			return processValueAsList();
		case Constants.DICTIONARY : 
			return processValueAsDict();	
		default : 
				if (isMetaNumber()) {
					//System.out.println("Is Meta Number");
					return processValueAsMetaNumber();
					
				} else {
					in.close();
					throw new BDecodeException("No known meta data with value = " + (char)currentValue);
				}
		}
		
	}
	private boolean finishedMetaNumber() {
		return currentValue == ':';
		
	}
	private BDGenericValue processValueAsMetaNumber() throws IOException {
		
		String number = "";
		String value = "";
		currentValue = inRead();
		toRead = true;
		while(!finishedMetaNumber()) {
			assert(currentValue >= '0' && currentValue <= '9');
			String nr = String.valueOf((char)currentValue);
			number = number.concat(nr);
			currentValue = in.read();
		}

		int lengthToRead = (new BigInteger(number)).intValue();
		byte [] bytes = new byte[lengthToRead];
		
		for (int read = 0; read < lengthToRead; read++) {
			bytes[read] = (byte) in.read();
		}

		return new BDGenericValue(bytes);
	}

	private BDGenericValue processValueAsList() throws IOException, BDecodeException {
		
		sanityCheck(Constants.LIST);
		
		List<BDGenericValue> list = new ArrayList<BDGenericValue>();
		toRead = true;
		currentValue = inRead();
		while (!didFinish()) {
			BDGenericValue value = processCurrentValues();
			list.add(value);
			
			currentValue = inRead();	
		}
		
		toRead = true;
		return new BDGenericValue(list);
	}

	public void decodeTrackerResponse() {
		
		
	}
	int sanityNumberChecks() throws IOException, BDecodeException {
		
		if (currentValue == '0') {
			currentValue = in.read();
			if (didFinish())
				return -1;
			else {
				in.close();
				throw new BDecodeException("Format i0<number>e not permitted!");	
			}
		}
		
		if (currentValue == '-') {
			currentValue = in.read();
			if (currentValue == '0' || didFinish()) {
				in.close();
				throw new BDecodeException("Format i-0e not permitted!");
			}
			
			return 1;
		}
		
		return 0;
		
	}
	
	private BDGenericValue processValueAsNumber() throws IOException, BDecodeException {
		
		String finalNumber = "";
		
		sanityCheck(Constants.NUMBER);
		toRead = true;
		
		currentValue = inRead();
		//System.out.println("Val = " + (char)currentValue);
		toRead = true;
		int isNegative = sanityNumberChecks();
		
		switch (isNegative) {
		case -1:
			return new BDGenericValue(new BigInteger("0"));
		case 1:
			finalNumber.concat(Constants.MINUS);
			break;
		case 0:
			break;
		}
		 
		while(!didFinish()) {
			
			assert(currentValue >= '1' && currentValue <= '9');
			//System.out.println("Val = " + (char)currentValue);
			finalNumber = finalNumber.concat(String.valueOf((char) currentValue));
			
			currentValue = in.read();	
		}
		
		return new BDGenericValue(new BigInteger(finalNumber));
	}

	public BDGenericValue processValueAsDict() throws BDecodeException, IOException{
		
		sanityCheck(Constants.DICTIONARY);
		
		Map<String, BDGenericValue> dict = new HashMap<String, BDGenericValue>();
		toRead = true;
		
		currentValue = inRead();
		
		while(!didFinish()) {
			
			BDGenericValue key = processCurrentValues();
			BDGenericValue value = processCurrentValues();
			dict.put(key.toChars(), value);
			
			currentValue = inRead();
		}
		
		toRead = true;
		return new BDGenericValue(dict)	;
	}

	public void getMetaData () throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		BDGenericValue path;
		
		BDGenericValue announce = (BDGenericValue) map.get(Constants.ANNOUNCE);
		this.setAnnounce(announce.toChars());
		
		BDGenericValue info = (BDGenericValue) map.get(Constants.INFO);
		Map<String, BDGenericValue> infoDict = info.toDict();
	
		BDGenericValue name = (BDGenericValue) infoDict.get(Constants.NAME);
		this.setName(name.toChars());
		
		BDGenericValue pieces_length = (BDGenericValue) infoDict.get(Constants.PIECE_LENGTH);
		this.setPiece_length(pieces_length.toNumber().intValue());
		
		BDGenericValue pieces = (BDGenericValue) infoDict.get(Constants.PIECES);
		this.setPieces(pieces.toBytes());
		
		BDGenericValue length = (BDGenericValue) infoDict.get(Constants.LENGTH);
		BDGenericValue files = (BDGenericValue) infoDict.get(Constants.FILES);
		if (length != null)
			this.setLength(length.toNumber().intValue());
		
		else if (files != null) {
			List<BDGenericValue> fileList = files.toList();
			lengths = new ArrayList<Long>(fileList.size());
			paths = new ArrayList<List<String>>(fileList.size());
			long len = 0;
			for (int i = 0; i< fileList.size(); i++) {
				Map<String, BDGenericValue> fileDict = ((BDGenericValue) fileList.get(i)).toDict();
				
				length = (BDGenericValue) fileDict.get(Constants.LENGTH);
				lengths.add(length.toNumber().longValue());
				len += length.toNumber().longValue();
				path =  (BDGenericValue) fileDict.get(Constants.PATH);
				List<BDGenericValue> pathList = path.toList();
				List<String> tempFiles = new ArrayList<String>(pathList.size());
				
				for (int j = 0; j < pathList.size(); j++) {
					BDGenericValue filePath = (BDGenericValue) pathList.get(j);
					tempFiles.add(filePath.toChars());	
				}
				
				paths.add(tempFiles);
			
			}
			this.setLength(len);
		}

		
	}
	
	public void prettyPrintMeta() {
		
		System.out.println("Announce = " + announce);
		System.out.println("name = " + name);
		System.out.println("Pieces length = " + piece_length);
		System.out.println("Pieces = " + pieces.length /20);
		if (length != 0)
			System.out.println("len = " + length);
		else if (paths != null) {
			System.out.println("paths = ");
			for (int i = 0; i< paths.size(); i++)
				System.out.println(paths.get(i) + " " + lengths.get(i));
		}
		System.out.println("Done");
	}

	public byte[] getInfoHash() throws NoSuchAlgorithmException {
		
		Map<String, Object> info = createInfoMap();
		byte[] infoBytes = BEncoder.bencode(info);
		MessageDigest digest = MessageDigest.getInstance("SHA");
		System.out.println("Byte = " + infoBytes + " " + digest.digest(infoBytes));
		this.info_hash = infoBytes;
		return digest.digest(infoBytes);	
		
		
	}
	public byte[] retInfoHash() {
		
		return info_hash;
	}
	
	private Map<String, Object> createInfoMap ()
    {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("name", name);
        info.put("piece length", new Integer(piece_length));
        info.put("pieces", pieces);
        //System.out.println("Here");
        System.out.println(name + " " + piece_length + " " + pieces);
        if (paths == null) {
            info.put("length", new Long(length));
        } else {
            List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < paths.size(); i++) {
                Map<String, Object> file = new HashMap<String, Object>();
                file.put("path", paths.get(i));
                file.put("length", lengths.get(i));
                //System.out.println(paths.get(i).toString() + " " + lengths.get(i).toString());
                l.add(file);
            }
            //System.out.println("List = " + l);
            info.put("files", l);
        }
        
        return info;
    }

	
	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPiece_length() {
		return piece_length;
	}

	public void setPiece_length(int piece_length) {
		this.piece_length = piece_length;
	}

	public byte[] getPieces() {
		return pieces;
	}

	public void setPieces(byte[] pieces) {
		this.pieces = pieces;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
	public Map getMetaDict() {
		return map;
	}
	

}
