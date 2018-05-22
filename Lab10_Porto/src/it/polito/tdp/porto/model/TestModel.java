package it.polito.tdp.porto.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		
		model.createGraph();
		Author a = model.getAutori().get(0);
		System.out.println(model.getCoautoriFromAutore(a));
		
		
	}

}
