package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class NewDiagramDialog {
	
	protected Stage stage;
	protected File location;
	
    @FXML
    private RadioButton typeActivity;
    @FXML
    private RadioButton typeClass;
    @FXML
    private RadioButton formGraphical;
    @FXML
    private RadioButton formTextual;
    
    @FXML
    private TextField diagramName;
    
    protected abstract void finishCallback(String name, boolean activity, boolean graphic);
    
    public NewDiagramDialog() {
    	
    }
    
    @FXML
    public void finish() throws IOException {
    	stage.close();
    	finishCallback(diagramName.getText(), typeActivity.isSelected(), formGraphical.isSelected());
    }
    
    @FXML
    public void cancel() throws IOException {
        stage.close();
    }
   
    public void showWindow() throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("NewDiagramDialog.fxml"));
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
        diagramName.setText("ClassDiagram1");
        diagramName.requestFocus();
    }
}