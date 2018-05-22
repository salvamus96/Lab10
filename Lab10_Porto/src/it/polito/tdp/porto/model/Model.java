package it.polito.tdp.porto.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.porto.db.PortoDAO;

public class Model {

	private List <Author> autori;
	private PortoDAO pdao;
	
	private Graph <Author, DefaultEdge> graph;
	
	
	public Model () {
		this.pdao = new PortoDAO();
		this.autori = pdao.getListAutori();
		
		this.graph = new SimpleGraph<>(DefaultEdge.class);
		
		this.createGraph();
	}
	
	public List <Author> getAutori (){
		if (this.autori == null)
			return new ArrayList<>();
		return this.autori;
	}
	
	public void createGraph () {
		Graphs.addAllVertices(this.graph, this.getAutori());
		
		for (Author a : this.graph.vertexSet()) {
			List <Author> coautori = this.pdao.getListCoautori(a);
			for(Author au : coautori)
				this.graph.addEdge(a, au);
		}
		
	}
	
	public List <Author> getCoautoriFromAutore (Author a){
		return Graphs.neighborListOf(this.graph, a);
	}
	

}
