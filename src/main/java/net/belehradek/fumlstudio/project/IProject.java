package net.belehradek.fumlstudio.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface IProject extends IProjectElementGroup {
	
	public void createNewProject(File folder);
	
	public void loadProject(File folder);
	
	public void build();
	
	public void debug();
	
	public void run();
	
//	public void saveElement(IProjectElement element);
}
