package net.belehradek.fumlstudio.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

public class TextEditor extends ProjectElementEditor {

	protected TextArea view = new TextArea();

	public TextEditor() {
		super();
		
		view.setFont(Font.font("Monospace"));
		
		//nahrazeni tabulatoru 4 mezerami
		view.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent e) {
		        if (e.getCode() == KeyCode.TAB) {
		            String s = "    ";
		            view.insertText(view.getCaretPosition(), s);
		            e.consume();
		        }
		    }
		});
	}

	@Override
	public void load() {
		try {
			//TEST
			//AlfModelLoader loader = new AlfModelLoader();
			//org.modeldriven.alf.uml.Package p = loader.loadModel(projectElement.getFile().getParentFile().getAbsolutePath(), projectElement.getFile().getName());
			
			BufferedReader bufferedReader = null;
			FileReader fileReader = new FileReader(projectElement.getFile());
			bufferedReader = new BufferedReader(fileReader);
			String text;

			while ((text = bufferedReader.readLine()) != null) {
				view.appendText(text + '\n');
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try (PrintWriter out = new PrintWriter(projectElement.getFile())){
			String text = view.getText();
		    out.print(text);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Node getView() {
		return view;
	}
}
