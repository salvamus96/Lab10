package it.polito.tdp.porto;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Model;
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
    	
    	this.txtResult.appendText("Coautori di " + autore.toString() + ":\n");
    	for (Author a : model.getCoautoriFromAutore(autore))
    		this.txtResult.appendText(a.toString() + "\n");
    	
    }

    @FXML
    void handleSequenza(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";

        this.boxSecondo.setDisable(true);
    }

	public void setModel(Model model) {
		this.model = model;
		
		this.boxPrimo.getItems().addAll(this.model.getAutori());
		this.boxSecondo.getItems().clear();
	}
}
