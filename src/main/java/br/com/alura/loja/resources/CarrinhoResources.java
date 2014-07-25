package br.com.alura.loja.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;

@Path("carrinhos")
public class CarrinhoResources {
	
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON) 
	public String busca(@PathParam("id") long id) {
	    Carrinho carrinho = new CarrinhoDAO().busca(id);
	    return carrinho.toJSON();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON) 
	public String add(String json) {
		System.out.println(json);
		return "<status>sucesso</status>";
	}
	
}
