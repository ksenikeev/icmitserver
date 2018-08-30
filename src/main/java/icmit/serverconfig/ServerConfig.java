package icmit.serverconfig;

public class ServerConfig {
	public static int mainPort = 3128;
	public static String mainHost = "localhost";
	public static String[] listHosts = {"localhost"};
	public static String htmlPath = "html";
	public static String errorPagePath = "errorhtml";
	public static String error404Page = "error404Page.html";
	public static String error501Page = "error501Page.html";
	public static String defaultErrorPage = "errorPage.html";
	public static String defaultPage = "index.html";
	
	public static ServerConfig readConfig(){
		//TODO Реализовать чтение конфигурации из файла
		return null;
	}
}
