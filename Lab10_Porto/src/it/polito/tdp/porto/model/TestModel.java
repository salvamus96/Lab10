package it.polito.tdp.porto.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		
		model.createGraph();
		Author a = model.getAutori().get(31); // Baralis
		System.out.println(model.getCoautoriFromAutore(a));
		
		Author a2 = model.getAutori().get(70); // Cagliero
		
		System.out.println(model.sequenzaArticoli(a, a2));
	}

}
