package net.belehradek.fumlstudio.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.belehradek.Global;
import net.belehradek.fumlstudio.event.EventProjectElement;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.IProjectElementGroup;
import net.belehradek.fumlstudio.project.fUmlProject;

public class ProjectTreeController extends TreeView<IProjectElement> {

	protected fUmlProject project;

	public ProjectTreeController() {
		super();

		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
				if (newValue == null)
					return;
				TreeItem selectedItem = newValue;
				Object value = selectedItem.getValue();
				if (value instanceof IProjectElement) {
					IProjectElement elem = (IProjectElement) value;
					Global.log("Selected Text : " + elem.getName());
					EventRouter.sendEvent(ProjectTreeController.this, new EventProjectElement(elem));
				}
			}
		});
	}

	public void showProject(IProject project) {
		TreeItem rootItem = createTreeItem(project, true);
		rootItem.setExpanded(true);
		setRoot(rootItem);
	}

	protected TreeItem createTreeItem(IProjectElement elem, boolean expanded) {
		// if (elem instanceof IProject) {
		// return new TreeItem<>(elem);
		// } else
		if (elem instanceof IProjectElementGroup) {
			// Global.log("Tree group: " + elem.getName());
			TreeItem out = new TreeItem<>(elem);
			out.setGraphic(elem.getIcon());
			out.setExpanded(expanded);
			for (IProjectElement ch : ((IProjectElementGroup) elem).getElements()) {
				if (ch != null) {
					TreeItem n = createTreeItem(ch, false);
					n.setGraphic(ch.getIcon());
					out.getChildren().add(n);
				}
			}
			return out;
		} else if (elem instanceof IProjectElement) {
			// Global.log("Tree elem: " + elem);
			TreeItem out = new TreeItem<>(elem);
			out.setGraphic(elem.getIcon());
			return out;
		} else {
			Global.log("Tree unknown: " + elem);
			return null;
		}
	}
}
