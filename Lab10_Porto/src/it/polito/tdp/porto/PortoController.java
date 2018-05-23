package it.polito.tdp.porto;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Model;
import it.polito.tdp.porto.model.Paper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PortoController {

	private Model model;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Author> boxPrimo;

    @FXML
    private ComboBox<Author> boxSecondo;

    @FXML
    private TextArea txtResult;

    @FXML
    void handleCoautori(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Author autore = this.boxPrimo.getValue();
    	if (autore == null) {
    		this.txtResult.appendText("Selezionare un autore!");
    		return;
    	}
    	
    	this.txtResult.appendText("Coautori di " + autore + ":\n");
    	for (Author a : model.getCoautoriFromAutore(autore))
    		this.txtResult.appendText(a + "\n");
    	
// popolazione del secondo menù a tendina con i non coautore(escludere i coautori e se stessi)
    	this.boxSecondo.getItems().clear();
    	this.boxSecondo.getItems().addAll(model.getNonCoautore(autore));
    	this.boxSecondo.setDisable(false);
    }

    @FXML
    void handleSequenza(ActionEvent event) {
    	this.txtResult.clear();
    	
    	Author a = this.boxPrimo.getValue();
    	Author a2 = this.boxSecondo.getValue();
    	
    	if (a == null || a2 == null) {
    		this.txtResult.appendText("Selezionare due autori!");
    		return;
    	}
    	
    	this.txtResult.appendText(String.format("Lista di articoli in grado di \"collegare\" %s a %s\n", a.getLastname(), a2.getLastname()));
    	System.out.println(model.sequenzaArticoli(a, a2).size());
    	for (Paper p : this.model.sequenzaArticoli(a, a2))
    		this.txtResult.appendText(p + "\n");
    	
    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";
     	
//        this.txtResult.setStyle("-fx-font-family: monospace");

        this.boxSecondo.setDisable(true);
    }

	public void setModel(Model model) {
		this.model = model;
		
		this.boxPrimo.getItems().addAll(this.model.getAutori());
		this.boxSecondo.getItems().clear();
	}
}
