package http;

import java.util.List;

/**
 * Класс описывает заголовок HTTP запроса от клиента
 *
 */
public class HTTPClientHeader {
	public String method;
	public String resourcePath;
	public String protocol;
	public String version;
	public String host;	
	public int contentLength = 0;
	public int headerLength = 0;
	
	private String[] headerKeys={"Host","Content-Type","Content-Length",
			"Accept-Language","Accept-Encoding","Origin"};
	
	public static HTTPClientHeader parseHTTPHeader(List<String> httpHeader){
		HTTPClientHeader httpClientHeader = new HTTPClientHeader();
		//TODO добавить проверки для списка httpHeader
		httpClientHeader.parseFirstHeaderString(httpHeader.get(0));
		for (int i=1;i<httpHeader.size();i++){
			httpClientHeader.parseHeaderString(httpHeader.get(i));
		}
		return httpClientHeader;
	}
	
	private void parseFirstHeaderString(String hs){
		String[] st = hs.split(" ");
		method = st[0];
		resourcePath = st[1];
		protocol = st[2];
	}
	
	//TODO исправить метод так, чтобы считывал параметр из известных по массиву headerKeys
	private void parseHeaderString(String hs){
		String[] st = hs.split(":");
		//TODO добавить проверки для массива st
		if (st[0].trim().equals("Content-Length")){
			try{
				contentLength = Integer.parseInt(st[1].trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
}
