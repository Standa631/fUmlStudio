package net.belehradek.fumlstudio.project;

import java.io.File;
import java.util.List;

public interface IProject {

	public String getName();
	
	public File getRootFolder();
	
	public List<IProjectElement> getElements();
	
	public void createNewProject(File folder);
	
	public void loadProject(File folder);
	
	public void addProjectElement(IProjectElement element);
	
	public void build();
}
