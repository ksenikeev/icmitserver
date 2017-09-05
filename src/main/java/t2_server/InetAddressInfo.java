package t2_server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressInfo {

	public static void main(String[] args) {
		try {
			InetAddress ia = InetAddress.getByName("localhost");
			System.out.println(ia.getHostAddress());
			System.out.println(ia.getAddress()[0]+"."+ia.getAddress()[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
