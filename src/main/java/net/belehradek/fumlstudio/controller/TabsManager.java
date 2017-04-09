package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import net.belehradek.fumlstudio.event.EventProjectElementSelected;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.ProjectElementAlf;
import net.belehradek.fumlstudio.project.ProjectElementFtl;
import net.belehradek.fumlstudio.project.ProjectElementFuml;

public class TabsManager extends TabPane {

	protected Map<IProjectElement, Tab> tabs = new HashMap<>();
	protected Map<String, FileEditor> editors = new HashMap<>();

	public TabsManager() {
		super();
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				if (newValue == null) return;
				Tab selectedTab = newValue;
				Object value = selectedTab.getUserData();
				if (value instanceof IProjectElement) {
					IProjectElement elem = (IProjectElement) value;
					System.out.println("Selected Text : " + elem.getFile().getName());
					EventRouter.sendEvent(TabsManager.this, new EventProjectElementSelected(elem));
				}
			}
		});
	}

	public void addElementTab(IProjectElement element) {
		if (tabs.containsKey(element)) {
			Tab tab = tabs.get(element);
			getSelectionModel().select(tab);
		} else {
			Tab tab = new Tab(element.getName());
			tab.setUserData(element);
			tab.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					Tab tab = (Tab) event.getSource();
					IProjectElement element = (IProjectElement) tab.getUserData();
					tabs.remove(element);
					editors.remove(tab);
				}
			});
			if (element instanceof ProjectElementAlf) {
				TextEditor textEditor = new TextEditor();
				textEditor.setFile(element.getFile());
				tab.setContent(textEditor.getView());
				editors.put(tab.getId(), textEditor);
			} else if (element instanceof ProjectElementFuml) {
				GraphicEditor graphicEditor = new GraphicEditor();
				graphicEditor.setFile(element.getFile());
				tab.setContent(graphicEditor.getView());
				editors.put(tab.getId(), graphicEditor);
			} else if (element instanceof ProjectElementFtl) {
				TextEditor textEditor = new TextEditor();
				textEditor.setFile(element.getFile());
				tab.setContent(textEditor.getView());
				editors.put(tab.getId(), textEditor);
			} else {
				System.out.println("Neznamy typ souboru pri vytvareni tabu s editorem: " + element.getFile().getAbsolutePath());
			}
			tabs.put(element, tab);
			getTabs().add(tab);
			getSelectionModel().select(tab);
		}
	}
	
	public void saveActive() {
		Tab tab = getSelectionModel().getSelectedItem();
		FileEditor editor = editors.get(tab.getId());
		editor.save();
	}
	
	public FileEditor getActiveEditor() {
		Tab tab = getSelectionModel().getSelectedItem();
		FileEditor editor = editors.get(tab.getId());
		return editor;
	}
	
	public void clear() {
		editors.clear();
		tabs.clear();
		if (getTabs() != null)
			getTabs().clear();
	}
}
