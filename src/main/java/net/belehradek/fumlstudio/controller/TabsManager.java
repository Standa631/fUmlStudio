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
			ProjectElementEditor editor = null;
			tab.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {					
					Tab tab = (Tab) event.getSource();
					System.out.println("Zavreni tabu: " + tab.getText());
					tabs.values().remove(tab);
				}
			});
			if (element instanceof ProjectElementAlf) {
				editor = new TextEditor();
				editor.setProjectElement(element);
				tab.setContent(editor.getView());
			} else if (element instanceof ProjectElementFuml) {
				editor = new GraphicEditor();
				editor.setProjectElement(element);
				tab.setContent(editor.getView());
			} else if (element instanceof ProjectElementFtl) {
				editor = new TextEditor();
				editor.setProjectElement(element);
				tab.setContent(editor.getView());
			} else {
				System.out.println("Neznamy typ souboru pri vytvareni tabu s editorem: " + element.getFile().getAbsolutePath());
			}
			editor.load();
			tabs.put(element, tab);
			tab.setUserData(editor);
			getTabs().add(tab);
			getSelectionModel().select(tab);
		}
	}
	
	public void saveActive() {
		getActiveEditor().save();
	}
	
	public ProjectElementEditor getActiveEditor() {
		Tab tab = getSelectionModel().getSelectedItem();
		ProjectElementEditor editor = (ProjectElementEditor) tab.getUserData();
		return editor;
	}
	
	public void clear() {
		tabs.clear();
		if (getTabs() != null)
			getTabs().clear();
	}
}
