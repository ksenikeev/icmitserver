package icmit.mainserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import icmit.errors.EnumUtils;
import icmit.errors.HTTPResponseCode;
import icmit.http.HTTPClientHeader;
import icmit.http.HTTPReader;
import icmit.serverconfig.ServerConfig;

/**
 * 
 *
 */
public class ICMITServer extends Thread{
    Socket s;

    public ICMITServer(Socket s){
        this.s = s;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }
    
    public void run(){
    	// Техническая информация из сокета
		System.out.println("NEW connection. ThreadId: " + currentThread().getId() +
				"\nsocket info: "+new Date()+"\n"+
		"address: "+s.getInetAddress().getHostAddress()+"\n"+
		"port: "+s.getLocalPort()+"\n"+
		"local address: "+s.getLocalAddress().getHostAddress()+"\n"+
		"local port: "+s.getLocalPort()+"\n"+
		"remote address: "+s.getRemoteSocketAddress().toString()+"\n");
   		//TODO по сети могут приходить разные пакеты, рассмотреть вопрос об отбрасывании технических
    	
    	// Подключаемся к потокам ввода-вывода ассоциированных с сокетом
    	try(InputStream is = s.getInputStream(); 
    			OutputStream os = s.getOutputStream();){
    		// Считываем заголовок запроса от клиента в список строк
    		List<String> lhs = HTTPReader.readHTTPHeader(is);
    		HTTPReader.printHTTPHeader(lhs);
    		// Разбираем заголовок
			HTTPClientHeader httpClientHeader=null;
			if (lhs!=null && lhs.size()>0) {
				httpClientHeader = HTTPClientHeader.parseHTTPHeader(lhs);
				// Если в запросе есть еще что-то пытаемся прочитать
				if (httpClientHeader.contentLength>0){
					HTTPReader.readHTTPBody(is,httpClientHeader.contentLength);
				}

				// Ответ:
				// По умолчанию возвращаем статус OK
				HTTPResponseCode rCode = HTTPResponseCode.OK;
				// По умолчанию возвращаем сообщение об ошибке
				String contents = "<!DOCTYPE html><html><head><meta charset='utf-8'></head><body>"+
						"<h1>Server error</h1>";
				// Смотрим что запросил клиент
				// Если запрашивается корневая папка
				if (httpClientHeader.resourcePath.equals("/")){
					// Ищем файл из параметра конфигурации ServerConfig.defaultPage = index.html
					String fileName = ServerConfig.htmlPath + File.separator + ServerConfig.defaultPage;
					// Пытаемся прочитать этот файл, чтобы вернуть его клиенту
					try{
						contents = new String(Files.readAllBytes(Paths.get(fileName)));
					} catch (NoSuchFileException e){
						// Если не смогли найти
						System.out.println(e.getMessage() +" not found");
						rCode = HTTPResponseCode.NotFound;
					}
				} else {
					// пытаемся найти запрашиваемый файл
					String fileName = ServerConfig.htmlPath + File.separator + httpClientHeader.resourcePath;
					try{
						contents = new String(Files.readAllBytes(Paths.get(fileName)));
					} catch (NoSuchFileException e){
						System.out.println(e.getMessage() +" not found");
						// не смогли найти запрашиваемый файл или еще какие-то ошибки ввода-вывода
						// попытаемся вывести страницу с кодом и описанием ошибки
						rCode = HTTPResponseCode.NotFound;
						try{
							fileName = ServerConfig.errorPagePath + File.separator + ServerConfig.defaultErrorPage;
							contents = new String(Files.readAllBytes(Paths.get(fileName)));
							contents=contents.replace("#{errorCode}", EnumUtils.enumCode(rCode));
							contents=contents.replace("#{errorDescription}", EnumUtils.enumDescription(rCode));
						}catch (NoSuchFileException e1){
							System.out.println(e1.getMessage() +" not found");
						}
					}
				}

				// что уйдет клиенту окончательно
				// заголовок:
				String data ="HTTP/1.1 "+EnumUtils.enumCode(rCode)+" "+
            		EnumUtils.enumDescription(rCode)+"\r\n"+
            		// general-header
    				"Connection: close"+"\r\n"+
                	"Date: "+new Date()+"\r\n"+
    				// response-header
    				"Server: ICMIT/0.0.1"+"\r\n"+
    				// entity-header
						(contents!=null?
    				"Content-Length: "+ (contents.getBytes()).length+"\r\n":"") +
    				// пустая строка отделяет заголовок от контента
    				"\r\n" + 
    				// сам контент:
					(contents!=null?contents:"");
    				
                os.write(data.getBytes());
			} else {
				System.out.println("Заголовок пустой! ThreadId: " + currentThread().getId());
			}

                // завершаем соединение
                s.close();
                // TODO Подсчитать и вывести время сеанса
            	System.out.println("socket close: "+new Date() +" ThreadId: " + currentThread().getId());
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}   
    }    
}