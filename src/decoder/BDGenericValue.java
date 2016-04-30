package decoder;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class BDGenericValue {
	private Map<String, BDGenericValue> dict;
	private String string;
	private List<BDGenericValue> list;
	private BigInteger number;
	
	public BDGenericValue(BigInteger number) {
		this.number = number;
		
	}
	public BDGenericValue(Map<String, BDGenericValue> dict) {
		this.dict = dict;
	}

	public BDGenericValue(List<BDGenericValue> list) {
		this.list = list;
	}

	public BDGenericValue(String value) {
		this.string = value;
	}
	public BigInteger toNumber() {
		return number;
	}
	
	public List<BDGenericValue> toList() {
		return list;
	}
	
	public String toChars() {
		return string;
	}

	public Map<String, BDGenericValue> toDict(){
		return dict;
	}
	
}
