package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GConnectorSkin;
import de.tesis.dynaware.grapheditor.GConnectorValidator;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.connections.ConnectionCommands;
import de.tesis.dynaware.grapheditor.core.connections.DefaultConnectorValidator;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.window.WindowPosition;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import net.belehradek.Lib;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.umleditor.Constants;
import net.belehradek.umleditor.classdiagram.ClassNodeSkin;
import net.belehradek.umleditor.classdiagram.PackageNodeSkin;
import net.belehradek.umleditor.classdiagram.Test2ConnectionSkin;
import net.belehradek.umleditor.classdiagram.TestConnectionSkin;
import net.belehradek.umleditor.classdiagram.TitledConnectorSkin;
import net.belehradek.umleditor.classdiagram.TitledTailSkin;

public class GraphicEditor extends ProjectElementEditor {

	protected static final int NODE_INITIAL_X = 19;
	protected static final int NODE_INITIAL_Y = 19;

	protected static final double SCALE_INITIAL = 1.0;

	protected GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
	protected GraphEditor graphEditor = new DefaultGraphEditor();
	protected Scale scaleTransform;

	public GraphicEditor() {
		super();

		graphEditorContainer = new GraphEditorContainer();
		Lib.setZeroAnchor(graphEditorContainer);
		graphEditorContainer.setGraphEditor(graphEditor);

		scaleTransform = new Scale(SCALE_INITIAL, SCALE_INITIAL, 0, 0);
		scaleTransform.yProperty().bind(scaleTransform.xProperty());
		graphEditor.getView().getTransforms().add(scaleTransform);

		setSkins();

		// TEST: zobrazit minimapu, super pro ladeni
		graphEditorContainer.getMinimap().setVisible(true);
	}

	protected void setSkins() {
		graphEditor.setNodeSkin(Constants.CLASS_NODE, ClassNodeSkin.class);
		graphEditor.setNodeSkin(Constants.PACKAGE_NODE, PackageNodeSkin.class);
		
		graphEditor.setConnectorSkin(Constants.TITLED_INPUT_CONNECTOR, TitledConnectorSkin.class);
		graphEditor.setConnectorSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledConnectorSkin.class);
		
		graphEditor.setConnectionSkin(Constants.TEST_CONNECTION, TestConnectionSkin.class);
		graphEditor.setConnectionSkin(Constants.TEST2_CONNECTION, Test2ConnectionSkin.class);
		
		graphEditor.setTailSkin(Constants.TITLED_INPUT_CONNECTOR, TitledTailSkin.class);
		graphEditor.setTailSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledTailSkin.class);
	}

	@Override
	public void setProjectElement(IProjectElement projectElement) {
		super.setProjectElement(projectElement);
	}

	@Override
	public void load() {
		System.out.println("Load .graph file: " + projectElement.getFile().getAbsolutePath());
		loadModel(projectElement.getFile(), graphEditor);
		graphEditorContainer.panTo(WindowPosition.CENTER);
	}

	protected GNode getOrAddNode(String id, String type) {
		GNode node = fingNode(id);
		if (node == null) {
			node = addNode(type, id, SCALE_INITIAL);
		}
		return node;
	}

	protected GNode fingNode(String id) {
		for (GNode g : graphEditor.getModel().getNodes()) {
			if (g.getId().equals(id)) {
				return g;
			}
		}
		return null;
	}

	public void setZoom(double scale) {
		scaleTransform.setX(scale);
	}

	@Override
	public void save() {
		System.out.println("Save .graph file: " + projectElement.getFile().getAbsolutePath());
		saveModel(projectElement.getFile(), graphEditor.getModel());
	}

	@Override
	public Node getView() {
		return graphEditorContainer;
	}

	protected void loadModel(final File file, final GraphEditor graphEditor) {
		final URI fileUri = URI.createFileURI(file.getAbsolutePath());
		loadUri(fileUri, graphEditor);
	}

	protected void loadUri(URI fileUri, GraphEditor graphEditor) {
		final XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl();
		final Resource resource = resourceFactory.createResource(fileUri);

		try {
			resource.load(Collections.EMPTY_MAP);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (!resource.getContents().isEmpty() && resource.getContents().get(0) instanceof GModel) {

			final GModel model = (GModel) resource.getContents().get(0);
			graphEditor.setModel(model);
		}
	}

	public void saveModel(final File file, final GModel model) {
		if (file.exists()) {
			boolean ret = file.delete();
			System.out.println("Delete file: " + ret);
		}

		final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

		final URI fileUri = URI.createFileURI(file.getAbsolutePath());
		final XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl();
		final Resource resource = resourceFactory.createResource(fileUri);
		resource.getContents().add(model);

		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		editingDomain.getResourceSet().getResources().clear();
		editingDomain.getResourceSet().getResources().add(resource);
	}

	public GNode addNode(String nodeType, String id, final double currentZoomFactor) {
		final double windowXOffset = graphEditorContainer.windowXProperty().get() / currentZoomFactor;
		final double windowYOffset = graphEditorContainer.windowYProperty().get() / currentZoomFactor;

		final GNode node = GraphFactory.eINSTANCE.createGNode();

		node.setX(NODE_INITIAL_X + windowXOffset);
		node.setY(NODE_INITIAL_Y + windowYOffset);

		node.setType(nodeType);
		node.setId(id);

		final GConnector input = GraphFactory.eINSTANCE.createGConnector();
		node.getConnectors().add(input);
		input.setType(Constants.TITLED_INPUT_CONNECTOR);
//		input.setId(node.getId() + "::" + node.getConnectors().size());

		final GConnector output = GraphFactory.eINSTANCE.createGConnector();
		node.getConnectors().add(output);
		output.setType(Constants.TITLED_OUTPUT_CONNECTOR);
//		output.setId(node.getId() + "::" + node.getConnectors().size());

		System.out.println("Add " + nodeType + ":" + id + " node: [" + node.getX() + "," + node.getY() + "]");

		Commands.addNode(graphEditor.getModel(), node);

		return node;
	}
	
	protected Point2D getConnectorPosition(GConnector connector) {
		GConnectorSkin connectorSkin = (GConnectorSkin) graphEditor.getSkinLookup().lookupConnector(connector);
		
        final Node connectorRoot = connectorSkin.getRoot().getParent();

        final double x = connectorRoot.getLayoutX() + connectorSkin.getWidth() / 2;
        final double y = connectorRoot.getLayoutY() + connectorSkin.getHeight() / 2;

        return new Point2D(x, y);
	}
	
	protected GConnection addConnection(GConnector source, GConnector target) {
		GModel model = graphEditor.getModel();
		GConnectorValidator validator = new DefaultConnectorValidator();
		
        String connectionType = validator.createConnectionType(source, target);
        connectionType = Constants.TEST2_CONNECTION;
        String jointType = validator.createJointType(source, target);
        
        Point2D sourcePos = getConnectorPosition(source);
        Point2D targetPos = getConnectorPosition(target);

        List<Point2D> jointPositions = new ArrayList<>();
        Double avg = (sourcePos.getX() + targetPos.getX()) / 2;
        jointPositions.add(new Point2D(avg, sourcePos.getY()));
        jointPositions.add(new Point2D(avg, targetPos.getY()));
        
        List<GJoint> joints = new ArrayList<>();
        for (final Point2D position : jointPositions) {
            final GJoint joint = GraphFactory.eINSTANCE.createGJoint();
            joint.setX(position.getX());
            joint.setY(position.getY());
            joint.setType(jointType);
            joints.add(joint);
        }

        final CompoundCommand command = ConnectionCommands.addConnection(model, source, target, connectionType, joints);

        // Notify the event manager so additional commands may be appended to this compound command.
        final GConnection addedConnection = model.getConnections().get(model.getConnections().size() - 1);
//        connectionEventManager.notifyConnectionAdded(addedConnection, command);
        
        return addedConnection;
    }
}