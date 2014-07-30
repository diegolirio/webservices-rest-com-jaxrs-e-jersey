package br.com.alura.loja;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Servidor {

	public static final String HOST = "http://localhost:8080"; 
	
	 public static void main(String[] args) throws IOException {
	        HttpServer server = startServer();
	        System.in.read();
	        stopServer(server);
	 }
	 
	 public static HttpServer startServer() {
	        ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
	        URI uri = URI.create(Servidor.HOST);
	        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
	        System.out.println("Servidor rodando");
	        return server;
	 }
	 
	 public static void stopServer(HttpServer server) {
		 server.stop();
	 }
	 

}