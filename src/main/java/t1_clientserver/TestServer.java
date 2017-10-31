package t1_clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public static void main(String[] args) {
		try(ServerSocket server = new ServerSocket(3128)){
			System.out.println("TestServer is started!");
			Socket s = server.accept();

			try(InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream()){
				byte buf[] = new byte[64*1024];
				int r = is.read(buf);
				String line = new String(buf,0,r);
				System.out.println("data from client: "+line);
				String response = "Данные получены сервером";
				os.write(response.getBytes());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
