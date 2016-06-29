package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PeerTransmiter extends Thread{
	private PeerConnection pc;
	private List<GenericSender> sendList;
	
	public PeerTransmiter(PeerConnection peerConnection) {
		this.pc = peerConnection;
		sendList = new ArrayList();
	}

	@Override 
	public void run() {
		while (true) {
			synchronized(sendList) {
				if (sendList.isEmpty()) {
					try {
						pc.getDos().flush();
						sendList.wait();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
					
				}
					GenericSender gen = sendList.remove(0);
					try {
						gen.writeToDataStream(pc.getDos());
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
		}
		
	}
	
	public void sendBitField(BitfieldSender bf) {
		synchronized(sendList) {
			sendList.add((GenericSender)bf);
		}
		
	}
	

}
