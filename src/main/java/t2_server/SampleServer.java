package t2_server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class SampleServer extends Thread{
    Socket s;
    int num;

    public static void main(String args[]){
        try{
            int i = 0; // счётчик подключений

            // привинтить сокет на локалхост, порт 3128
            ServerSocket server = new ServerSocket(3128, 0);//, InetAddress.getByName("localhost"));

            System.out.println("server is started");

            // слушаем порт
            while(true)
            {
                // ждём нового подключения, после чего запускаем обработку клиента
                // в новый вычислительный поток и увеличиваем счётчик на единичку
                new SampleServer(i, server.accept());
                i++;
            }
        } catch(Exception e){
        	System.out.println("init error: "+e);
        } // вывод исключений
    }

    public SampleServer(int num, Socket s){
        // копируем данные
        this.num = num;
        this.s = s;

        // и запускаем новый вычислительный поток (см. ф-ю run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run(){
        try{
            // из сокета клиента берём поток входящих данных
            InputStream is = s.getInputStream();
            // и оттуда же - поток данных от сервера к клиенту
            OutputStream os = s.getOutputStream();
            int r = 0;
            byte buf[] = new byte[32*1024];
            BufferedReader br = new BufferedReader(new InputStreamReader(is));     
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((r = is.read(buf))!=-1){
            	System.out.println("readed "+r+" bytes from client");
            	bos.write(buf,0,r);
            	System.out.println("writed "+r+" bytes into bos");
            }
            System.out.println("next step");
            // создаём строку, содержащую полученную от клиента информацию
            String data = new String(bos.toByteArray());

            // добавляем данные об адресе сокета:
            data = ""+num+": "+"\n"+data;
            System.out.println(data);
            
            data ="HTTP/1.1 200 OK "+
            	"Date: Mon, 07 Apr 2003 14:40:25 GMT "+
				"Server: Apache/1.3.20 (Win32) PHP/4.3.0 "+
				"Keep-Alive: timeout=15, max=100 "+
				"Connection: Keep-Alive "+
				"Transfer-Encoding: chunked "+
				"Content-Type: text/plane\n"+
				"\n"+
				"<!DOCTYPE html><html><head><meta charset='utf-8'></head><body>"+
					"<h1>Ответ сервера</h1>"+
					"запрос номер "+num+"\n";
            os.write(data.getBytes());

            // завершаем соединение
            s.close();
            os.close();
            bos.close();
        }catch(Exception e){
        	System.out.println("init error: "+e);
        } // вывод исключений
    }
}