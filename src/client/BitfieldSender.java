package client;

import java.io.DataOutputStream;
import java.io.IOException;

public class BitfieldSender implements GenericSender{
	private final static byte BITFIELD_TYPE = 5 & 0x0FF;
	private byte[] bitfield;
	private int len;
	
	public BitfieldSender(byte[] bitfield) {
		this.len = bitfield.length + 1; // 1 byte from type
		this.bitfield = bitfield;
	}
	
	public void writeToDataStream(DataOutputStream dos) throws IOException {
		dos.writeInt(len);
		dos.writeByte(BITFIELD_TYPE);
		dos.write(bitfield);
	}
	
}
