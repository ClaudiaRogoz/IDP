package decoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class BDGenericValue {
	private Object genericValue;

	public BDGenericValue(BigInteger number) {
		this.genericValue = number;
		
	}
	public BDGenericValue(Map<String, BDGenericValue> dict) {
		this.genericValue = dict;
	}

	public BDGenericValue(List<BDGenericValue> list) {
		this.genericValue = list;
	}

	public BDGenericValue(byte[] bytes) {
		this.genericValue = bytes;
	}
	public BDGenericValue(String value) {
		this.genericValue = value;
	}
	
	public Number toNumber() {
		return (Number)genericValue;
	}
	
	public byte[] toBytes() {
		return (byte[])genericValue;
	}
	
	public List<BDGenericValue> toList() {
		return (List<BDGenericValue>)genericValue;
	}
	
	public String toChars() throws UnsupportedEncodingException {
		return new String(toBytes(),  "UTF-8");
	}
 
	public Map<String, BDGenericValue> toDict(){
		return (Map<String, BDGenericValue>)genericValue;
	}
	
}
