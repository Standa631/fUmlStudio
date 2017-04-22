package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.modeldriven.alf.eclipse.papyrus.execution.Fuml.ElementResolutionError;
import org.modeldriven.alf.uml.Activity;
import org.modeldriven.alf.uml.Class_;
import org.modeldriven.alf.uml.Classifier;
import org.modeldriven.alf.uml.NamedElement;
import org.modeldriven.alf.uml.Operation;
import org.modeldriven.alf.uml.Package;
import org.modeldriven.alf.uml.Property;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.model.impl.GNodeImpl;
import de.tesis.dynaware.grapheditor.window.WindowPosition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import net.belehradek.Lib;
import net.belehradek.fuml.core.XmiModelLoader;
import net.belehradek.fuml.core.XmiModelSaver;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.ProjectElementFuml;
import net.belehradek.umleditor.Constants;
import net.belehradek.umleditor.classdiagram.ClassNodeSkin;
import net.belehradek.umleditor.classdiagram.TitledConnectorSkin;
import net.belehradek.umleditor.classdiagram.TitledTailSkin;

public class GraphicEditor extends ProjectElementEditor {
	
	protected static final String FILE_EXTENSION = ".graph";

	protected static final int NODE_INITIAL_X = 19;
	protected static final int NODE_INITIAL_Y = 19;

	protected static final double SCALE_INITIAL = 1.0;

	protected GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
	protected GraphEditor graphEditor = new DefaultGraphEditor();
	//protected GraphEditorPersistence graphEditorPersistence = new GraphEditorPersistence();
	protected Scale scaleTransform;

	protected ProjectElementFuml projectElementFuml;
	
	protected org.modeldriven.alf.uml.Package model;

	public GraphicEditor() {
		super();

		graphEditorContainer = new GraphEditorContainer();
		Lib.setZeroAnchor(graphEditorContainer);
		graphEditorContainer.setGraphEditor(graphEditor);

		graphEditor.setNodeSkin(Constants.CLASS_NODE, ClassNodeSkin.class);
		graphEditor.setConnectorSkin(Constants.TITLED_INPUT_CONNECTOR, TitledConnectorSkin.class);
		graphEditor.setConnectorSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledConnectorSkin.class);
		graphEditor.setTailSkin(Constants.TITLED_INPUT_CONNECTOR, TitledTailSkin.class);
		graphEditor.setTailSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledTailSkin.class);

		scaleTransform = new Scale(SCALE_INITIAL, SCALE_INITIAL, 0, 0);
		scaleTransform.yProperty().bind(scaleTransform.xProperty());
		graphEditor.getView().getTransforms().add(scaleTransform);

		// TEST: zobrazit minimapu, super pro ladeni
		graphEditorContainer.getMinimap().setVisible(true);
	}
	
	@Override
	public void setProjectElement(IProjectElement projectElement) {
		super.setProjectElement(projectElement);
		projectElementFuml = (ProjectElementFuml) projectElement;
	}

	@Override
	protected void load() {
		System.out.println("Load file graph: " + projectElementFuml.getGraphicFile().getAbsolutePath());
		
		loadModel(projectElementFuml.getGraphicFile(), graphEditor);
		loadContent();

		graphEditorContainer.panTo(WindowPosition.CENTER);
	}

	protected void loadContent() {
		System.out.println("Load file xmi: " + projectElement.getFile().getAbsolutePath());
		XmiModelLoader m = new XmiModelLoader(new File("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries"), projectElement.getFile().getParentFile());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					//model = m.loadModel("App");
					String name = projectElement.getName();
					name = name.substring(0, name.lastIndexOf("."));
					model = m.loadModel(name);
					//model = modelLoader.getModel();
					/*for (Class_ c : modelLoader.getClasses(model)) {
						GNode node = null;
						
						node = fingNode(c.getQualifiedName());
						if (node == null) {
							node = addNode(Constants.CLASS_NODE, c.getQualifiedName(), SCALE_INITIAL);
						}
						
						ClassNodeSkin skin = (ClassNodeSkin) graphEditor.getSkinLookup().lookupNode(node);
						
						skin.setName(c.getName());
						for (Property p : modelLoader.getAttributes(c)) {
							skin.addAttribuet(modelLoader.getAttributeString(p));
						}
						for (Operation o : modelLoader.getOperations(c)) {
							skin.addOperation(modelLoader.getOperationString(o));
						}
						for (Classifier l : modelLoader.getActivities(c)) {
							if (l instanceof Activity) {
								skin.addOperation(modelLoader.getActivityString((Activity)l));
							}
						}
						
						graphEditor.reload();
					}*/
					for (NamedElement c : model.getMember()) {
						if (c instanceof Class_) {
							GNode node = fingNode(c.getQualifiedName());
							if (node == null) {
								node = addNode(Constants.CLASS_NODE, c.getQualifiedName(), SCALE_INITIAL);
							}
							
							ClassNodeSkin skin = (ClassNodeSkin) graphEditor.getSkinLookup().lookupNode(node);
							
							skin.setName(c.getName());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected GNode fingNode(String id) {
		for (GNode g : graphEditor.getModel().getNodes()) {
			if (g.getId().equals(id))
				return g;
		}
		return null;
	}

	public void setZoom(double scale) {
		scaleTransform.setX(scale);
	}

	@Override
	public void save() {
		System.out.println("Save .graph file: " + projectElementFuml.getGraphicFile().getAbsolutePath());
		saveModel(projectElementFuml.getGraphicFile(), graphEditor.getModel());
		System.out.println("Saved");
		
		String path = Lib.toFilePath(projectElement.getFile().getParent());
		System.out.println("Save .xmi file: " + path + "/" + projectElement.getFile().getName());
		XmiModelSaver saver = new XmiModelSaver();
		try {
			saver.saveModel(model, projectElement.getFile());
			System.out.println("Saved");
		} catch (IOException e) {
			System.out.println("Not saved!");
			e.printStackTrace();
		}
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

        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith(FILE_EXTENSION)) {
            absolutePath += FILE_EXTENSION;
        }

        final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(model);

        final URI fileUri = URI.createFileURI(absolutePath);
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

        //initialDirectory = file.getParentFile();
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

		final GConnector output = GraphFactory.eINSTANCE.createGConnector();
		node.getConnectors().add(output);
		output.setType(Constants.TITLED_OUTPUT_CONNECTOR);

		System.out.println("Add " + nodeType + ":" + id + " node: [" + node.getX() + "," + node.getY() + "]");

		Commands.addNode(graphEditor.getModel(), node);

		return node;
	}

	protected String allocateNewId() {

		final List<GNode> nodes = graphEditor.getModel().getNodes();
		final OptionalInt max = nodes.stream().mapToInt(node -> Integer.parseInt(node.getId())).max();

		if (max.isPresent()) {
			return Integer.toString(max.getAsInt() + 1);
		} else {
			return "1";
		}
	}
}