package net.belehradek.fumlstudio.controller;

import java.io.File;

import javafx.scene.Node;

public abstract class FileEditor {

	protected File file;

	public FileEditor() {
		
	}

	public void setFile(File file) {
		this.file = file;
		load();
	}

	protected abstract void load();

	public abstract void save();

	public void refresh() {
		load();
	}

	public abstract Node getView();
}