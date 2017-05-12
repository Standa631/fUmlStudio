package net.belehradek.fumlstudio.controller;

import java.io.File;

import javafx.scene.Node;
import net.belehradek.fumlstudio.project.IProjectElement;

public abstract class ProjectElementEditor implements IProjectElementEditor {

	protected IProjectElement projectElement;

	public ProjectElementEditor() {
		
	}

	public void setProjectElement(IProjectElement projectElement) {
		this.projectElement = projectElement;
	}

	public abstract void load();

	public abstract void save();

	public void refresh() {
		load();
	}

	public abstract Node getView();
}