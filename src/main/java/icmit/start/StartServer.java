package icmit.start;

import icmit.mainserver.ICMITServer;
import icmit.serverconfig.ServerConfig;

import java.net.ServerSocket;

public class StartServer {
    public static void main(String args[]){
        try{
            ServerSocket server = new ServerSocket(ServerConfig.mainPort);
            //, 0, InetAddress.getByName(ServerConfig.mainHost));
            System.out.println("server is started");
            // слушаем порт
            while(true) {
                // ждём нового подключения, после чего запускаем обработку клиента
                new ICMITServer(server.accept());
            }
        } catch(Exception e){
            System.out.println("init error: "+e);
        }
    }

}
