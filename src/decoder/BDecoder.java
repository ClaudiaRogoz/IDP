package decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private String pieces = "";
	private int length = 0;
	private List<Long> lengths = null;
	private List<List<String>> paths = null;
	private boolean toRead = true;
	
	
	public BDecoder(InputStream in) throws BDecodeException, IOException {
		this.in = in;
		
		map = processValueAsDict().toDict();
		getMetaData();
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

		
		while(!finishedMetaNumber()) {
			assert(currentValue >= '1' && currentValue <= '9');
			String nr = String.valueOf((char)currentValue);
			number = number.concat(nr);
			currentValue = in.read();
		}

		int lengthToRead = (new BigInteger(number)).intValue();
		
		for (int i = 0; i< lengthToRead; i++) {
			currentValue = in.read();
			value = value.concat(String.valueOf((char) currentValue));
		}
		//System.out.println("read string = " + value);
		toRead = true;
		return new BDGenericValue(value);
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
		
		//System.out.println("Process Dict");
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

	public void getMetaData () {
		
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
		this.setPieces(pieces.toChars());
		
		BDGenericValue length = (BDGenericValue) infoDict.get(Constants.LENGTH);
		BDGenericValue files = (BDGenericValue) infoDict.get(Constants.FILES);
		if (length != null)
			this.setLength(length.toNumber().intValue());
		
		else if (files != null) {
			List<BDGenericValue> fileList = files.toList();
			lengths = new ArrayList<Long>(fileList.size());
			paths = new ArrayList<List<String>>(fileList.size());
			
			for (int i = 0; i< fileList.size(); i++) {
				Map<String, BDGenericValue> fileDict = ((BDGenericValue) fileList.get(i)).toDict();
				
				length = (BDGenericValue) fileDict.get(Constants.LENGTH);
				lengths.add(length.toNumber().longValue());
				
				path =  (BDGenericValue) fileDict.get(Constants.PATH);
				List<BDGenericValue> pathList = path.toList();
				List<String> tempFiles = new ArrayList<String>(pathList.size());
				
				for (int j = 0; j < pathList.size(); j++) {
					BDGenericValue filePath = (BDGenericValue) pathList.get(j);
					tempFiles.add(filePath.toChars());	
				}
				
				paths.add(tempFiles);
			
			}
			
		}
		
		
	}
	
	public void prettyPrintMeta() {
		
		System.out.println("Announce = " + announce);
		System.out.println("name = " + name);
		System.out.println("Pieces length = " + piece_length);
		//System.out.println("Pieces = " + pieces);
		if (length != 0)
			System.out.println("len = " + length);
		else if (paths != null) {
			System.out.println("paths = ");
			for (int i = 0; i< paths.size(); i++)
				System.out.println(paths.get(i) + " " + lengths.get(i));
		}
		System.out.println("Done");
	}
	
	public byte[] getInfoHash() {
		
		BDGenericValue info = (BDGenericValue) map.get(Constants.INFO);
		Map<String, BDGenericValue> infoDict = info.toDict();
		BEncoder be = new BEncoder(new ByteArrayOutputStream());
		be.bencode(infoDict);
		return null;
		
		
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

	public String getPieces() {
		return pieces;
	}

	public void setPieces(String pieces) {
		this.pieces = pieces;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	public Map getMetaDict() {
		return map;
	}
	

}
