package net.belehradek.fumlstudio.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Property;

import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.application.Platform;
import net.belehradek.Global;
import net.belehradek.fuml.core.UmlFrameworkWrapper;
import net.belehradek.fuml.core.UmlWrapper;
import net.belehradek.umleditor.classdiagram.ClassNodeSkin;

public class GraphicEditorActivity extends GraphicEditorClass {
	
	protected List<Operation> toMakeConnection = new ArrayList<>();

	@Override
	public void mapModel(Package model) {
		toMakeConnection.clear();
		super.mapModel(model);
		createConnections();
	}
	
	@Override
	public void mapPackage(Package pack) {
		//super.mapPackage(pack);
	}
	
	@Override
	public void mapClass(Class_ class_) {
		if (UmlFrameworkWrapper.isActivity(class_))
			super.mapClass(class_);
	}
	
	@Override
	protected void mapInheritance(Class_ class_, Class_ super_) {
		//super.mapInheritance(class_, super_);
	}
	
	@Override
	protected void mapAttribute(ClassNodeSkin skin, Property p) {

	}
	
	@Override
	protected void mapOperation(ClassNodeSkin skin, Operation o) {
		if (UmlFrameworkWrapper.isStartActivity(o)) {
			//TODO: misto operace zobrazit propojeni
			super.mapOperation(skin, o);
			toMakeConnection.add(o);
		}
	}
	
	protected void createConnections() {
		for (Operation o : toMakeConnection) {
			GNode sourceNode = fingNode(o.getClass_().getQualifiedName());
			GConnector sourceConnector = findBestConnector(sourceNode);
			
			Class_ cl = UmlFrameworkWrapper.getStartActivity(o);
			GNode targetNode = fingNode(cl.getQualifiedName());
			GConnector targetConnector = findBestConnector(targetNode, sourceConnector);
			
			String id = sourceNode.getId() + "-" + targetNode.getId();			
			if (findConnection(id) == null) {
				GConnection con = addAssoctioationConnection(sourceConnector, targetConnector);
				if (con != null)
					con.setId(id);	
			}
		}
	}
	
	@Override
	protected void mapActivity(ClassNodeSkin skin, Activity a) {

	}
}
