package br.com.alura.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.Servidor;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

public class ClientTest {
	
	private HttpServer server;
	private WebTarget target;
	private Client client;

	@Before
	public void before() {
        this.server = Servidor.startServer();
        ClientConfig config = new ClientConfig(); // Retorna o Log dos Request e Response
        config.register(new LoggingFilter());
        this.client = ClientBuilder.newClient(config);
        this.target = client.target(Servidor.HOST);
		//this.target = client.target("http://wservicec.dellavolpe.com.br:6917/tdv/aposta_jogo_simul/?codigo=40");
		//this.target = client.target("http://www.mocky.io");        
	}
	 
	@After
	public void after() {
		Servidor.stopServer(server);
	}
	
	@Test
	public void pegaCarrinhoVerificaItem() {
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		System.out.println(conteudo);
		Assert.assertTrue(conteudo.contains("Videogame"));
	}
	
	@Test
	public void pegaCarrinhoXmlJaxBVerificaItem() {
		String conteudo = target.path("/carrinhos/by_jaxb/1").request().get(String.class);
		System.out.println(conteudo);
		Assert.assertTrue(conteudo.contains("<carrinho><produtos><preco>4000.0</preco><id>6237</id><nome>Videogame 4</nome>"
				+"<quantidade>1</quantidade></produtos><produtos><preco>60.0</preco><id>3467</id><nome>Jogo de esporte</nome>"
				+"<quantidade>2</quantidade></produtos><rua>Rua Vergueiro 3185, 8 andar</rua><cidade>S‹o Paulo</cidade><id>1</id></carrinho>"));
		Carrinho carrinho = target.path("/carrinhos/by_jaxb/1").request().get(Carrinho.class);
		Assert.assertTrue(carrinho.getId()==1);
	}	
	
	@Test
	public void adicionaProdutoCarrinho() {
		Carrinho c = new Carrinho();
		c.adiciona(new Produto(1, "Atari", 1000, 2));
		c.adiciona(new Produto(2, "Monitor Dell", 350, 1));
		c.setCidade("Sao Paulo");
		c.setId(2);
		c.setRua("Rua Lidice 22");
		// utilizando JaxB ao inves de mandar um xml, mandaria um carrinho que automaticamente 
		//   deserializa o objeto >>> Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		String xml  = new XStream().toXML(c);
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		Response response = this.target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		System.out.println(location);
		String conteudo = this.client.target(location).request().get(String.class);
		System.out.println(conteudo);
		Assert.assertTrue(conteudo.contains("Atari")); 
	}
	
	@Test
	public void removeProdutoDoCarrinho() {
		System.out.println("removeProdutoDoCarrinho()");
		Carrinho c = new Carrinho();
		c.adiciona(new Produto(1, "Atari", 1000, 2));
		c.adiciona(new Produto(2, "Monitor Dell", 350, 1));
		c.setCidade("Sao Paulo");
		c.setId(3);
		c.setRua("Rua Lidice 22");
		String xml  = new XStream().toXML(c);
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		Response response = this.target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		System.out.println("Location: " + location);
		String conteudo = this.client.target(location).request().get(String.class);
		System.out.println("Carrinho >>>>> " + conteudo);
		Assert.assertTrue(conteudo.contains("Monitor Dell")); 	
		Response responseDelete = this.target.path("/carrinhos/"+c.getId()+"/produto/2").request().delete();
		Assert.assertEquals(Response.ok().build().getStatus(), responseDelete.getStatus());
		conteudo = this.client.target(location).request().get(String.class);
		System.out.println("Carrinho >>>>> " + conteudo);
		Assert.assertFalse(conteudo.contains("Monitor"));
	}
	
	@Test
	public void alteraProdutoCarrinho() {
		System.out.println("============ alteraProdutoCarrinho() =============");
		Carrinho c = new Carrinho();
		c.adiciona(new Produto(1, "Atari", 1000, 2));
		Produto monitor = new Produto(2, "Monitor Dell", 350, 1);
		c.adiciona(monitor);
		c.setCidade("Sao Paulo");
		c.setId(4);
		c.setRua("Rua Lidice 22");
		String xml  = new XStream().toXML(c);
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		Response response = this.target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		System.out.println(location);
		String conteudo = this.client.target(location).request().get(String.class);
		System.out.println(conteudo);
		Assert.assertTrue(conteudo.contains("{\"preco\":350.0,\"id\":2,\"nome\":\"Monitor Dell\",\"quantidade\":1}"));
		monitor.setQuantidade(2);
		String xmlMonitor = new XStream().toXML(monitor);
		Entity<String> entityAltera = Entity.entity(xmlMonitor, MediaType.APPLICATION_XML);
		Response responseAltera = this.target.path("/carrinhos/"+c.getId()+"/produto/2").request().put(entityAltera);
		Assert.assertEquals( Response.ok().build().getStatus(), responseAltera.getStatus());
		conteudo = this.client.target(location).request().get(String.class);
		System.out.println("Carrinho >>>>> " + conteudo);
		Assert.assertTrue(conteudo.contains("{\"preco\":350.0,\"id\":2,\"nome\":\"Monitor Dell\",\"quantidade\":2}"));
	}	
	
	@Test
	public void adicionaProdutoCarrinhoJaxB() {
		Carrinho c = new Carrinho();
		c.adiciona(new Produto(1, "Atari", 1000, 2));
		c.adiciona(new Produto(2, "Monitor Dell", 350, 1));
		c.setCidade("Sao Paulo");
		c.setId(5);
		c.setRua("Rua Lidice 22");
		Entity<Carrinho> entity = Entity.entity(c, MediaType.APPLICATION_XML);
		Response response = this.target.path("/carrinhos/addproduto").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		System.out.println(location);
		Carrinho carrinho = this.client.target(location).request().get(Carrinho.class);
		Assert.assertTrue("Atari".equals(carrinho.getProdutos().get(0).getNome())); 
	}	
	
	
	
	

}
