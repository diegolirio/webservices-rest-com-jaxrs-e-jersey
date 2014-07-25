package br.com.alura.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.Servidor;

public class ClientTest {
	
	private HttpServer server;

	@Before
	public void before() {
        server = Servidor.startServer();
	}
	 
	@After
	public void after() {
		Servidor.stopServer(server);
	}
	
	@Test
	public void testando() {
		Client client = ClientBuilder.newClient();
		//WebTarget target = client.target("http://wservicec.dellavolpe.com.br:6917/tdv");
		//WebTarget target = client.target("http://www.mocky.io");
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		//String conteudo = target.path("/aposta_jogo_simul/?codigo=40").request().get(String.class);
		System.out.println(conteudo);
		//Assert.assertTrue(conteudo.contains("Rua Vergueiro 3185"));
		Assert.assertTrue(conteudo.contains("Videogame"));
	}
	
	
	

}
