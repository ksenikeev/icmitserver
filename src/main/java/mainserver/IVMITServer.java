package mainserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import errors.EnumUtils;
import errors.HTTPResponseCode;
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
    		
    		//Response:
    		// По умолчанию возвращаем статус OK
    		HTTPResponseCode rCode = HTTPResponseCode.OK;
    		// По умолчанию возвращаем сообщение об ошибке
    		String contents = "<!DOCTYPE html><html><head><meta charset='utf-8'></head><body>"+
					"<h1>Server error</h1>";
    		// Смотрим что запросил клиент
    		// Если запрашивается корневая папка
    		if (httpClientHeader.resourcePath.equals("/")){
    			// Ищем файл из параметра конфигурации ServerConfig.defaultPage
    			String fileName = ServerConfig.htmlPath + File.separator + ServerConfig.defaultPage;
    			try{
    				contents = new String(Files.readAllBytes(Paths.get(fileName))); 
    			} catch (IOException e){
        			System.out.println("проблемы с "+fileName);
    				rCode = HTTPResponseCode.NotFound;
    			}
    		} else {
    			// пытаемся найти запрашиваемый файл
    			String fileName = ServerConfig.htmlPath + File.separator + httpClientHeader.resourcePath.replace("/", "");
    			try{
    				contents = new String(Files.readAllBytes(Paths.get(fileName))); 
    			} catch (IOException e){
    				// не смогли найти запрашиваемый файл
        			System.out.println("проблемы с "+fileName);
    				rCode = HTTPResponseCode.NotFound;
        			try{
        				fileName = ServerConfig.htmlPath + File.separator + ServerConfig.defaultErrorPage;
        				contents = new String(Files.readAllBytes(Paths.get(fileName)));
            			contents=contents.replace("#{errorCode}", EnumUtils.enumCode(rCode));
            			contents=contents.replace("#{errorDescription}", EnumUtils.enumDescription(rCode));
        			}catch (IOException e1){
        				System.out.println(e1.getMessage());
        			}
    			}
    		}
    		
    		// что уйдет клиенту окончательно
            String data ="HTTP/1.1 "+EnumUtils.enumCode(rCode)+" "+
            		EnumUtils.enumDescription(rCode)+"\r\n"+
            		// general-header
    				"Connection: Close"+"\r\n"+
                	"Date: "+new Date()+"\r\n"+
    				// response-header
    				"Server: ICMIT/0.0.1"+"\r\n"+
    				// entity-header
    				"Content-Length: "+ (contents.getBytes()).length+"\r\n" +
    				// пустая строка отделяет заголовок от контента
    				"\r\n" + 
    				// сам контент
    				contents;
    				
                os.write(data.getBytes());

                // завершаем соединение
                s.close();
                // Подсчитать и вывести время сеанса
                System.out.println(data);
            	System.out.println("socket close: "+new Date());
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}   
    }    
}