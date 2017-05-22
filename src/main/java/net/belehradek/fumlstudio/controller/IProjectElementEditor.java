package net.belehradek.fumlstudio.controller;

import javafx.scene.Node;
import net.belehradek.fumlstudio.project.IProjectElement;

public interface IProjectElementEditor {

	void setProjectElement(IProjectElement projectElement);
	IProjectElement getProjectElement();

	void save();
	void load();
	void refresh();

	Node getView();
	
	//TODO
//	public void addOnChangeListener(Object toAdd);
//	void onChange();
//	void onSave();
}