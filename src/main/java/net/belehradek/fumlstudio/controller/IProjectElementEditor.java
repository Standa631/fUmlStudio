package net.belehradek.fumlstudio.controller;

import javafx.scene.Node;
import net.belehradek.fumlstudio.project.IProjectElement;

public interface IProjectElementEditor {

	void setProjectElement(IProjectElement projectElement);

	void save();
	
	void load();

	void refresh();

	Node getView();
	
	
}