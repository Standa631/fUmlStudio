package net.belehradek.fumlstudio.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class TextEditor extends FileEditor {

	protected TextArea view = new TextArea();

	public TextEditor() {
		super();
	}

	@Override
	protected void load() {
		try {
			// StringBuilder stringBuffer = new StringBuilder();
			BufferedReader bufferedReader = null;
			bufferedReader = new BufferedReader(new FileReader(file));
			String text;

			while ((text = bufferedReader.readLine()) != null) {
				// stringBuffer.append(text);
				view.appendText(text + '\n');
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try (PrintWriter out = new PrintWriter(file)){
		    out.println(view.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Node getView() {
		return view;
	}
}
