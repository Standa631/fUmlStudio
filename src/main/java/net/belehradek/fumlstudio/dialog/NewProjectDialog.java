package net.belehradek.fumlstudio.dialog;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class NewProjectDialog {
	
	protected Stage stage;
	protected File location;
	
    @FXML
    private TextField rootLocation;
    @FXML
    private TextField projectName;
    
    protected abstract void finishCallback(File projectRoot);
    
    public NewProjectDialog() {
    	
    }
    
    @FXML
    public void finish() throws IOException {
    	stage.close();
    	finishCallback(new File(new File(rootLocation.getText()), projectName.getText()));
    }
    
    @FXML
    public void cancel() throws IOException {
        stage.close();
    }
    
    @FXML
    public void openLocationDialog() throws IOException {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	if (location != null)
			directoryChooser.setInitialDirectory(location);
    	else {
	    	File fromText = new File(rootLocation.getText());
	    	if (fromText.isDirectory())
	    		directoryChooser.setInitialDirectory(fromText);
    	}
    	
        location = directoryChooser.showDialog(stage);
         
        if (location != null) {
        	rootLocation.setText(location.getAbsolutePath());
        }
    }
    
    @FXML
    public void locationTextChanged() {
    	
    }
   
    public void showWindow() throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProjectDialog.fxml"));
        loader.setController(this);
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        
        //defaultni jmeno projektu
        projectName.setText("fUmlProject");
        projectName.requestFocus();
        
        //defaultni umisteni
        location = new File(System.getProperty("user.home"));
        rootLocation.setText(location.getAbsolutePath());
    }
}
