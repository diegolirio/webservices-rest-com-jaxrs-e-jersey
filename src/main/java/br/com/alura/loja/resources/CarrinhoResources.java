package br.com.alura.loja.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

@Path("carrinhos")
public class CarrinhoResources {
	
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON) 
	public String busca(@PathParam("id") long id) {
	    Carrinho carrinho = new CarrinhoDAO().busca(id);  
	    return carrinho.toJSON();
	}
	
	@Path("by_jaxb/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML) 
	public Carrinho busca_jaxb(@PathParam("id") long id) {
	    Carrinho carrinho = new CarrinhoDAO().busca(id); 
	    return carrinho;
	}	

	@POST
	@Consumes(MediaType.APPLICATION_XML) 
	public Response add(String xml) {
		Carrinho c = (Carrinho) new XStream().fromXML(xml);
		new CarrinhoDAO().adiciona(c);
		URI uri = URI.create("/carrinhos/"+c.getId());
		return Response.created(uri).build();
	}
	
	@Path("addproduto")
	@POST
	@Consumes(MediaType.APPLICATION_XML) 
	public Response addComjaxB(Carrinho carrinho) {
		new CarrinhoDAO().adiciona(carrinho);
		URI uri = URI.create("/carrinhos/by_jaxb/"+carrinho.getId());
		return Response.created(uri).build();
	}	
	
	@Path("{id}/produto/{produtoId}")
	@DELETE
	public Response removeProduto(@PathParam("id") long id, @PathParam("produtoId") long produtoId) {
		Carrinho c = new CarrinhoDAO().busca(id);
		c.remove(produtoId);
		return Response.ok().build();   
	}
	
	@Path("{id}/produto/{produtoId}")
	@PUT
	public Response alteraProduto(String xml, @PathParam("id") long id, @PathParam("produtoId") long produtoId) {
		Carrinho c = new CarrinhoDAO().busca(id);
		Produto p = (Produto) new XStream().fromXML(xml);
		c.trocaQuantidade(p); 
		return Response.ok().build(); 
	}
	
}
