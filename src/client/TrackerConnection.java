package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import decoder.BDGenericValue;
import decoder.BDecodeException;
import decoder.BDecoder;

public class TrackerConnection extends Thread{
	private BDecoder metaInfo;
	private TorrentManager manag;
	
	TrackerConnection(BDecoder metaInfo, TorrentManager manag) {
		this.metaInfo = metaInfo;
		this.manag = manag;

	}
	
	
	@Override
    public void run ()
    {
		
		try {
			byte[] info_hash = metaInfo.getInfoHash();
			manag.setHash(info_hash);
			byte[] peer_id = manag.getPeer_id();
			String announce = metaInfo.getAnnounce();
			
			int port = manag.getPort();
			long uploaded = manag.getUploaded();
			long downloaded = manag.getDownloaded();
			long left = manag.getLeft();
			System.out.println("Peer_id " + peer_id);
			//creates a getRequest https://wiki.theory.org/BitTorrent_Tracker_Protocol#Example_GET_Request
			System.out.println("infohash " + info_hash);
			String request = announce
						+ "?info_hash=" + urlencode(info_hash) 
								+ "&peer_id=" +urlencode(peer_id) + 
								"&port=" + port  + "&uploaded=" + uploaded + "&downloaded="
								+ downloaded + "&left=" + left + "&event=started"; 
			System.out.println("request = " + request);
			URL url = new URL(request);
			URLConnection urlConnection = url.openConnection();
			
			urlConnection.connect();
			System.out.println("Trying to Connect..." + urlConnection);
			System.out.println("Connect done");
			InputStream in = urlConnection.getInputStream();
			
			System.out.println("Received yuhuu + " + in);
			
			ByteArrayOutputStream into = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];

			// inputStream is your original stream. 
			for (int n; 0 < (n = in.read(buf));) {
			    into.write(buf, 0, n);
			}
			into.close();

			byte[] data = into.toByteArray();

			//This is your data in string format.
			String stringData = new String(data, "UTF-8"); // Or whatever encoding  
System.out.println("Data = " + stringData);
	System.out.println();
			//This is the new stream that you can pass it to other code and use its data.    
			ByteArrayInputStream newStream = new ByteArrayInputStream(data);
			BDecoder newBd = new BDecoder(newStream);
			
			manag.setPeers(newBd.peers);
			manag.setInterval(newBd.interval);
			
			System.out.println("Interval = " + newBd.interval + " " + newBd.peers);
			
		} catch (NoSuchAlgorithmException | IOException | BDecodeException e) {
			e.printStackTrace();
		}
    }

	static String urlencode (byte[] bs)
    {
        StringBuffer sb = new StringBuffer(bs.length * 3);
        for (byte element : bs) {
            int c = element & 0xFF;
            sb.append('%');
            if (c < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(c));
        }

        return sb.toString();
    }
}
