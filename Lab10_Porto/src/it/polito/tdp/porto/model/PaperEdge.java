package it.polito.tdp.porto.model;

import org.jgrapht.graph.DefaultEdge;

public class PaperEdge extends DefaultEdge{

	private static final long serialVersionUID = 1L;
	private Paper paper;
	
	public Paper getPaper() {
		return paper;
	}
	
	public void setPaper(Paper paper) {
		this.paper = paper;
	}
	
	
	
}
