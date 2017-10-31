package t1_clientserver;

import java.io.IOException;
import java.net.*;

public class TestClient {

	public static void main(String[] args) {
		try(Socket socket = new Socket()){
			
			socket.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),3128));

			(socket.getOutputStream()).write("Запрос к серверу".getBytes());

			byte[] buffer = new byte[64*1024];

			int r = (socket.getInputStream()).read(buffer);

			String line = new String(buffer,0,r);
			System.out.println(r+" "+line);

		} catch (UnknownHostException e) {
			System.out.println("Неизвестный адрес!");;
		} catch (ConnectException e){
			System.out.println("Не смогли подключиться к серверу!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
