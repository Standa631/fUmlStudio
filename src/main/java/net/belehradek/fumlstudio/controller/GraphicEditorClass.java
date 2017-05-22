package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Property;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.window.WindowPosition;
import javafx.application.Platform;
import net.belehradek.Global;
import net.belehradek.fuml.core.MyAlfMapping;
import net.belehradek.fuml.core.UmlFrameworkWrapper;
import net.belehradek.fuml.core.UmlWrapper;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.ProjectElementActivities;
import net.belehradek.fumlstudio.project.ProjectElementFuml;
import net.belehradek.fumlstudio.project.fUmlProject;
import net.belehradek.umleditor.Constants;
import net.belehradek.umleditor.classdiagram.ClassNodeSkin;
import net.belehradek.umleditor.classdiagram.PackageNodeSkin;

public class GraphicEditorClass extends GraphicEditor {

	protected ProjectElementFuml projectElementFuml;
	protected Package model;

	@Override
	public void setProjectElement(IProjectElement projectElement) {
		super.setProjectElement(projectElement);
		projectElementFuml = (ProjectElementFuml) projectElement;
	}

	@Override
	public void load() {
		File graphFile = projectElementFuml.getGraphicFile();
		Global.log("Load file graph: " + graphFile.getAbsolutePath());

		loadModel(graphFile, graphEditor);
		loadContent();

		graphEditorContainer.panTo(WindowPosition.CENTER);
	}

	private List<GNode> notUsed;

	protected void loadContent() {
		Global.log("Load file xmi: " + projectElement.getFile().getAbsolutePath());
		model = ((fUmlProject) projectElementFuml.getProject()).loadModel();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				notUsed = new ArrayList<>(graphEditor.getModel().getNodes());
				
				mapModel(model);
				
				for (GNode n : notUsed) {
					Commands.removeNode(graphEditor.getModel(), n);
				}
			}
		});
	}
	
	public void mapModel(Package model) {
		for (Class_ c : UmlFrameworkWrapper.getAllClasses(model, false)) {
			mapClass(c);
		}
		
		for (Package p : UmlFrameworkWrapper.getAllPackages(model, false)) {
			mapPackage(p);
		}
		
		for (Class_ c : UmlFrameworkWrapper.getAllClasses(model, false)) {
			for (Class_ sc : c.getSuperClass()) {
				mapInheritance(c, sc);
			}
		}
	}

	public void mapPackage(Package pack) {
		GNode node = getOrAddNode(pack.getQualifiedName(), Constants.PACKAGE_NODE);
		PackageNodeSkin skin = (PackageNodeSkin) graphEditor.getSkinLookup().lookupNode(node);
		skin.setName(pack.getName());

		notUsed.remove(node);
	}

	public void mapClass(Class_ class_) {
		GNode node = getOrAddNode(class_.getQualifiedName(), Constants.CLASS_NODE);
		ClassNodeSkin skin = (ClassNodeSkin) graphEditor.getSkinLookup().lookupNode(node);
		skin.setName(class_.getName());

		int connectorCount = 0;
		
		for (Property p : UmlFrameworkWrapper.getAttributesNoDefault(class_)) {
			mapAttribute(skin, p);
			connectorCount++;
		}
		for (Operation o : UmlFrameworkWrapper.getOperationsNoDefault(class_)) {
			mapOperation(skin, o);
			connectorCount++;
		}
		for (Activity a : UmlFrameworkWrapper.getActivitiesNoDefault(class_)) {
			mapActivity(skin, a);
			connectorCount++;
		}
		
		if (connectorCount < 4) connectorCount = 4;
		connectorCount = connectorCount - node.getConnectors().size();
		for (int i = 0; i < connectorCount; i++) {
			addConnector(node, Constants.RIGHT_CONNECTOR);
			addConnector(node, Constants.LEFT_CONNECTOR);
		}
		
		//addConnector(node, Constants.TOP_CONNECTOR);
		//addConnector(node, Constants.BOTTOM_CONNECTOR);
		
		notUsed.remove(node);
	}

	protected void mapAttribute(ClassNodeSkin skin, Property p) {
		skin.addAttribuet(UmlWrapper.getAttributeString(p));
	}

	protected void mapOperation(ClassNodeSkin skin, Operation o) {
		skin.addOperation(UmlWrapper.getOperationString(o));
	}

	protected void mapActivity(ClassNodeSkin skin, Activity a) {
		skin.addOperation(UmlWrapper.getActivityString(a));
	}
	
	protected void mapInheritance(Class_ class_, Class_ super_) {
		GNode sourceNode = fingNode(class_.getQualifiedName());
		if (sourceNode == null) return;
		GConnector sourceConnector = findBestConnector(sourceNode);
		
		GNode targetNode = fingNode(super_.getQualifiedName());
		if (targetNode == null) return;
		GConnector targetConnector = findBestConnector(targetNode, sourceConnector);
		
		String id = sourceNode.getId() + "->" + targetNode.getId();			
		if (findConnection(id) == null) {
			GConnection con = addGeneralizationConnection(sourceConnector, targetConnector);
			con.setId(id);
		}
	}

	@Override
	protected GNode fingNode(String id) {
		for (GNode g : graphEditor.getModel().getNodes()) {
			if (g.getId().equals(id)) {
				notUsed.remove(g);
				return g;
			}
		}
		return null;
	}

	protected GConnection findConnection(String id) {
		for (GConnection g : graphEditor.getModel().getConnections()) {
			if (g.getId().equals(id)) {
				notUsed.remove(g);
				return g;
			}
		}
		return null;
	}

	@Override
	public void save() {
		Global.log("Save .graph file: " + projectElementFuml.getGraphicFile().getAbsolutePath());
		saveModel(projectElementFuml.getGraphicFile(), graphEditor.getModel());
	}
}
