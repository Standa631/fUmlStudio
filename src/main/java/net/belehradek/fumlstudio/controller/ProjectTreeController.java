package net.belehradek.fumlstudio.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.belehradek.fumlstudio.AwesomeIcon;
import net.belehradek.fumlstudio.event.Event;
import net.belehradek.fumlstudio.event.EventProjectElementSelected;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.fUmlProject;

public class ProjectTreeController extends TreeView<IProjectElement> {

	protected fUmlProject project;

	public ProjectTreeController() {
		super();
		
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
				TreeItem selectedItem = newValue;
				Object value = selectedItem.getValue();
				if (value instanceof IProjectElement) {
					IProjectElement elem = (IProjectElement) value;
					System.out.println("Selected Text : " + elem.getFile().getName());
					EventRouter.sendEvent(ProjectTreeController.this, new EventProjectElementSelected(elem));
				}
			}
		});
	}

	public void showProject(IProject project) {
		TreeItem rootItem = new TreeItem(project, AwesomeIcon.PROJECT_FILE.node());
		rootItem.setExpanded(true);

		// TODO: kategorie

		for (IProjectElement elem : project.getElements()) {
			TreeItem<IProjectElement> item = new TreeItem<>(elem);
			item.setGraphic(elem.getIcon());
			rootItem.getChildren().add(item);
		}

		setRoot(rootItem);
	}
}
