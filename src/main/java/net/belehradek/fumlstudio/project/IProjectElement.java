package net.belehradek.fumlstudio.project;

import java.io.File;

import javafx.scene.Node;

public interface IProjectElement {

	public Node getIcon();
	
	public File getFile();
	
	public String getType();
	
	public String getName();
	
	public IProject getProject();
	
//	public void save();
}
