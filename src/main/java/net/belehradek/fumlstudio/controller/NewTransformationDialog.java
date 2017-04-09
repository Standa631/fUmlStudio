package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class NewTransformationDialog {
	
	protected Stage stage;
	protected String id;
    
    @FXML
    private TextField transformationId;
    
    protected abstract void finishCallback(String transformationId);
    
    public NewTransformationDialog() {
    	
    }
    
    @FXML
    public void finish() throws IOException {
    	stage.close();
    	finishCallback(transformationId.getText());
    }
    
    @FXML
    public void cancel() throws IOException {
        stage.close();
    }
   
    public void showWindow() throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("NewTransformationDialog.fxml"));
        loader.setController(this);
        final Parent root = loader.load();
        //final Scene scene = new Scene(root, 250, 150);
        final Scene scene = new Scene(root);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.initOwner(emailField.getScene().getWindow());
        stage.setScene(scene);
        stage.show();
        
        //defaultni jmeno projektu
        //diagramName.setText("ClassDiagram1");
        transformationId.requestFocus();
    }
}