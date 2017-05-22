package net.belehradek.fumlstudio.controller;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.fx.code.editor.fx.TextEditor;
import org.eclipse.fx.code.editor.services.InputDocument;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.event.SimpleEventBus;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.fx.code.editor.SourceFileInput;
import org.eclipse.fx.code.editor.LocalSourceFileInput;
import org.eclipse.fx.code.editor.StringInput;

import javafx.event.Event;
import javafx.scene.Node;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import net.belehradek.fumlstudio.project.IProjectElement;

public class CodeEditor extends ProjectElementEditor {

	protected SourceViewer viewer;
	protected IProjectElement projectElement;
	protected EventBus eventBus = new SimpleEventBus();
	
	public CodeEditor() {
		super();

		viewer = new SourceViewer();
	}
	
	@Override
	public void setProjectElement(IProjectElement projectElement) {
		this.projectElement = projectElement;
	}
	
	@Override
	public IProjectElement getProjectElement() {
		return projectElement;
	}

	@Override
	public void refresh() {
		load();
	}

	@Override
	public Node getView() {
		return viewer;
	}

	@Override
	public void save() {
		try (PrintWriter out = new PrintWriter(projectElement.getFile())){
			String text = viewer.getDocument().get();
		    out.print(text);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
        Path path = Paths.get(projectElement.getFile().getPath());
        viewer.setDocument(new InputDocument(new LocalSourceFileInput(path, eventBus)));
        
        viewer.getDocument().addDocumentListener(new IDocumentListener() {
			@Override
			public void documentChanged(DocumentEvent event) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {

			}
		});
	}
}
