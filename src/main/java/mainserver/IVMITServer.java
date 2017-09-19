package mainserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import http.HTTPClientHeader;
import serverconfig.ServerConfig;

/**
 * 
 *
 */
public class IVMITServer extends Thread{
    Socket s;
    int hs=0;

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
    	try(InputStream is = s.getInputStream(); OutputStream os = s.getOutputStream();){
 
    		List<String> lhs = readHTTPHeader(is);
    		HTTPClientHeader httpClientHeader = HTTPClientHeader.parseHTTPHeader(lhs);
    		if (httpClientHeader.contentLength>0){
    			readHTTPBody(is,httpClientHeader.contentLength,hs);
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

    	} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    
    }
    
    private List<String> readHTTPHeader(InputStream is) throws Throwable {
    	List<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while(true) {
            String s = br.readLine();
            hs +=s.length();
            if(s == null || s.trim().length() == 0) {
                break;
            }
            result.add(s);
        }
        return result;
    }
    
    private void readHTTPBody(InputStream is, int bodySize, int offset) throws Throwable {
        int r = 0;
        int count=0;
        byte buf[] = new byte[32*1024];
        System.out.println("read body "+bodySize);
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();){
	        while (count<bodySize){
	        	r = is.read(buf,count+hs,bodySize-count);
	        	System.out.println("readed "+r+" bytes from client");
	        	bos.write(buf,count,r); 
	        	count +=r;
	        	System.out.println("writed "+r+" bytes into bos");
	        }
	        System.out.println(new String(bos.toByteArray()));
        }

    }
}