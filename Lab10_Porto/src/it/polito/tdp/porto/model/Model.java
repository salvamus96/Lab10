package it.polito.tdp.porto.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.porto.db.PortoDAO;

public class Model {

	private PortoDAO pdao;
	
	private List <Author> autori;
	private AuthorIdMap authorIdMap;
	
	private List <Paper> articoli;
	private PaperIdMap paperIdMap;
	
	private Graph <Author, DefaultEdge> graph;
	
	
	public Model () {
		this.pdao = new PortoDAO();
		this.authorIdMap = new AuthorIdMap();
		this.autori = pdao.getListAutori(this.authorIdMap);
		
		this.paperIdMap = new PaperIdMap();
		this.articoli = this.pdao.getListArticoli(this.paperIdMap);
		
		// popolazione delle liste relative alla relazione MOLTI - MOLTI
		this.pdao.getAllCreator(this.authorIdMap, this.paperIdMap);
		
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

// per ogni vertice, l'arco è rappresentato da un articolo in cui ha collaborato un altro autore (coautore)
		for (Author a : this.graph.vertexSet()) {
			List <Author> coautori = this.pdao.getListCoautori(a, this.authorIdMap);
			for(Author au : coautori)
				this.graph.addEdge(a, au);
		}
		
	}
	
	public List <Author> getCoautoriFromAutore (Author a){
		return Graphs.neighborListOf(this.graph, a);
	}

	public List<Author> getNonCoautore(Author a) {
		List <Author> au = new ArrayList<>(this.autori);
// dalla lista di autori non si considerano i coautori e l'autore stesso
		au.removeAll(this.getCoautoriFromAutore(a));
		au.remove(a);
		return au;
	}

	public List<Paper> sequenzaArticoli(Author a, Author a2) {

		List <Paper> sequenza = new ArrayList<>();
		// creazione del cammino minimo tra due autori
		DijkstraShortestPath<Author, DefaultEdge> camminoMinimo = new DijkstraShortestPath<>(this.graph, a, a2);
		
		// elenco degli archi del cammino minimo
		List <DefaultEdge> edges = camminoMinimo.getPathEdgeList();
		
		if (edges != null) {
			for (DefaultEdge e : edges) {
			// dati i vertici adiacenti (sorgente e destinazione dell'arco)
				Author aPartenza = this.graph.getEdgeSource(e);
				Author aDestinazione = this.graph.getEdgeTarget(e);
			
// consultare il DB per trovare almeno un articolo in cui hanno collaborato i vertici adiacenti
				//Paper p = pdao.articoloInComune(aPartenza, aDestinazione);
	
// attraverso ORM trovare l'intersezione tra i due insiemi di articoli di ogni vertice
				Paper p = this.intersezioneInsiemi (aPartenza, aDestinazione);
				if (p != null)
					sequenza.add(p);
			}
		}
		return sequenza;
	}

	private Paper intersezioneInsiemi(Author aPartenza, Author aDestinazione) {
		List <Paper> list1 = aPartenza.getArticoli();
		List <Paper> list2 = aDestinazione.getArticoli();
		
		// è sufficiente almeno un articolo
		for (Paper p1 : list1)
			for (Paper p2 : list2) {
				if (p1.equals(p2))
					return p1;
			}
		return null;
	}

}
