package mainserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import http.HTTPClientHeader;
import http.HTTPReader;
import serverconfig.ServerConfig;

/**
 * 
 *
 */
public class IVMITServer extends Thread{
    Socket s;

    public static void main(String args[]){
        try{
            ServerSocket server = new ServerSocket(ServerConfig.mainPort, 0, 
            		InetAddress.getByName(ServerConfig.mainHost));
            System.out.println("server is started");
            // слушаем порт
            while(true)
            {
                // ждём нового подключения, после чего запускаем обработку клиента
                // в новый вычислительный поток и увеличиваем счётчик на единичку
                new IVMITServer(server.accept());
             }
        } catch(Exception e){
        	System.out.println("init error: "+e);
        }
    }

    public IVMITServer(Socket s){
        this.s = s;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }
    
    public void run(){
    	System.out.println("socket info: "+new Date());
    	System.out.println("address: "+s.getInetAddress().getHostAddress());
    	System.out.println("port: "+s.getLocalPort());
    	System.out.println("local address: "+s.getLocalAddress().getHostAddress());
    	System.out.println("local port: "+s.getLocalPort());
    	System.out.println("remote address: "+s.getRemoteSocketAddress().toString());
    	try(InputStream is = s.getInputStream(); 
    			OutputStream os = s.getOutputStream();){
 
    		List<String> lhs = HTTPReader.readHTTPHeader(is);
    		
    		HTTPClientHeader httpClientHeader = HTTPClientHeader.parseHTTPHeader(lhs);

    		if (httpClientHeader.contentLength>0){
    			HTTPReader.readHTTPBody(is,httpClientHeader.contentLength);
    		}
    		
            String data ="HTTP/1.1 200 OK "+
                	"Date: Mon, 07 Apr 2003 14:40:25 GMT "+
    				"Server: Apache/1.3.20 (Win32) PHP/4.3.0 "+
    				"Keep-Alive: timeout=15, max=100 "+
    				"Connection: Keep-Alive "+
    				"Transfer-Encoding: chunked "+
    				"Content-Type: text/plane\n"+
    				"\n"+
    				"<!DOCTYPE html><html><head><meta charset='utf-8'></head><body>"+
    					"<h1>Ответ сервера</h1>";
                os.write(data.getBytes());

                // завершаем соединение
                s.close();
                // Подсчитать и вывести время сеанса
            	System.out.println("socket close: "+new Date());
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}   
    }    
}