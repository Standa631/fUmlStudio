package net.belehradek.fumlstudio.project;

import java.util.List;

public interface IProjectElementGroup extends IProjectElement {
	
	public List<IProjectElement> getElements();
	
	public void addProjectElement(IProjectElement element);
}
