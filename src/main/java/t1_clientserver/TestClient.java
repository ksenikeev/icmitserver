package t1_clientserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {

	public static void main(String[] args) {
		try(Socket socket = new Socket();){
			
			socket.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),3128));

			socket.getOutputStream().write("send to server".getBytes());

			byte[] buffer = new byte[64*1024];

			int r = socket.getInputStream().read(buffer);

			String line = new String(buffer,0,r);
			System.out.println(line);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
