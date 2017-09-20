package http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import serverconfig.Consts;

/**
 * Класс предназначен для чтения http пакета
 * @author ksenikeev
 * TODO Сейчас класс имеет только статические методы. Было бы правильнее использовать его через создание экземпляра, передав в конструктор поток
 * 
 */
public class HTTPReader {
	
    /**
     * Метод читает очередную строку заголовка, конец строки - обязательно комбинация \r\n
     * @param is - input stream
     * @return readed bytes as string
     * @throws IOException 
     * TODO протестировать скорость чтения, сейчас реализовано побайтное чтение из потока - скорее всего можно улучшить.
     * TODO Вместо String res использовать StringBuilder будет немного быстрее
     */
	public static String readHeadersNextString(InputStream is) throws IOException{
    	String res=null;
    	// Контролируем правильный конец строки, если встретили \r , то далее должен быть \n
    	boolean therIsCR=false;
    	byte[] buf = new byte[Consts.bufferHeaderReaderSize];
    	int n = 0; 
    	int r = 0;
    	while(true){
    		if(n<Consts.bufferHeaderReaderSize){
    			// Читаем очередной байт
	    		r = is.read();
	    		if (r==-1){
	    			// поток прервался
	    			return res;
				} else if (r == '\r'){
					// получили возврат каретки \r, надо проконтролировать какой будет следующий символ
					therIsCR=true;
				} else if (r == '\n' && therIsCR){
					// получили конец строки - присваиваем и выходим
					buf[n] = (byte)r;
					if (res==null){
						res = new String(buf,0,n);
					} else {
						res += new String(buf,0,n);
					}
					return res;
				} else if (r == '\n' && !therIsCR){
					// Если концу строки не предшествует возврат каретки, то бросаем исключение
					// TODO реализовать соответствующее исключение!
				}
	    		// записываем в буфер очередной прочитанный байт
				buf[n++] = (byte)r;
    		} else {
    			// Если буфер исчерпан - формируем из него строку и сбрасываем счетчики, чтобы продолжить чтение
				if (res==null){
					res = new String(buf,0,n);
				} else {
					res += new String(buf,0,n);
				}
    			n=0;
    		}
    	}
    }
    
    public static List<String> readHTTPHeader(InputStream is) throws Throwable {
    	List<String> result = new ArrayList<String>();
        while(true) {
            String s = readHeadersNextString(is);
            System.out.println(s);
            if(s == null || s.trim().length() == 0) {
                break;
            }
            result.add(s);
        }
        return result;
    }
    
    public static void readHTTPBody(InputStream is, int bodySize) throws Throwable {
        int r = 0;
        int n = 0;
        int count=0;
        byte buf[] = new byte[Consts.bufferBodyReaderSize];
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();){
	        while (count+n<bodySize){
	        	if (n<Consts.bufferBodyReaderSize){
		        	r = is.read();
		        	if (r==-1){
		        		// Если поток прерван, то сбрасываем буфер
		        		bos.write(buf, 0, n);
		        		System.out.println(new String(bos.toByteArray()));
		        		return;
		        	} else {
		        		buf[n++]=(byte)r;
		        	}
		        } else {
	        		// Исчерпали буфер - сбрасываем его в ByteArrayOutputStream и обнуляем счетчик
	        		bos.write(buf,0,n);
	        		count += n;
	        		n = 0;
	        	}
	        }
	        // Если осталось что-то не сброшенным из буфера
    		if (n>0) bos.write(buf,0,n);
	        System.out.println(new String(bos.toByteArray()));
        }
    }
}
