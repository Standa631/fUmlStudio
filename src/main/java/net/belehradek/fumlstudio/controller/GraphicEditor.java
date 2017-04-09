package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.window.WindowPosition;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import net.belehradek.fumlstudio.GraphEditorPersistence;
import net.belehradek.fumlstudio.Lib;
import net.belehradek.umlgraphiceditor.Constants;
import net.belehradek.umlgraphiceditor.classdiagram.ClassNodeSkin;
import net.belehradek.umlgraphiceditor.classdiagram.TitledConnectorSkin;
import net.belehradek.umlgraphiceditor.classdiagram.TitledTailSkin;

public class GraphicEditor extends FileEditor {
	
    protected static final int NODE_INITIAL_X = 19;
    protected static final int NODE_INITIAL_Y = 19;

	protected GraphEditorContainer graphEditorContainer = new GraphEditorContainer();
	protected GraphEditor graphEditor = new DefaultGraphEditor();
	protected GraphEditorPersistence graphEditorPersistence = new GraphEditorPersistence();

	public GraphicEditor() {
		super();

		graphEditorContainer = new GraphEditorContainer();
		Lib.setZeroAnchor(graphEditorContainer);
		graphEditorContainer.setGraphEditor(graphEditor);
		
        graphEditor.setNodeSkin(Constants.TITLED_NODE, ClassNodeSkin.class);
        graphEditor.setConnectorSkin(Constants.TITLED_INPUT_CONNECTOR, TitledConnectorSkin.class);
        graphEditor.setConnectorSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledConnectorSkin.class);
        graphEditor.setTailSkin(Constants.TITLED_INPUT_CONNECTOR, TitledTailSkin.class);
        graphEditor.setTailSkin(Constants.TITLED_OUTPUT_CONNECTOR, TitledTailSkin.class);
	}

	@Override
	protected void load() {
		File graphFile = getGraphicFile();
		System.out.println("Load file: " + graphFile.getAbsolutePath());
		if (!graphFile.exists()) {
			try {
				File newDiagram = new File(getClass().getResource("../project/FumlProjectStructure/diagram.graph").getFile());
				Files.copy(newDiagram.toPath(), graphFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loadModel(getGraphicFile(), graphEditor);
		//test
		//loadSample(graphEditor);
	}

	@Override
	public void save() {
		System.out.println("Save file: " + getGraphicFile().getAbsolutePath());
		graphEditorPersistence.saveModel(getGraphicFile(), graphEditor.getModel());
	}

	@Override
	public Node getView() {
		return graphEditorContainer;
	}
	
    protected void loadModel(final File file, final GraphEditor graphEditor) {
        final URI fileUri = URI.createFileURI(file.getAbsolutePath());
        loadUri(fileUri, graphEditor);
    }
    
    protected void loadSample(GraphEditor graphEditor) {
    	final String samplePath = getClass().getResource("titled.graph").toExternalForm();
    	final URI fileUri = URI.createURI(samplePath);    	
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

        graphEditorContainer.panTo(WindowPosition.CENTER);
    }
    
    public File getGraphicFile() {
    	if (file != null) {
    		return new File(file.getAbsolutePath().replace(".fuml", ".graph"));
    	} else {
    		return null;
    	}
    }
    
    public void addNode(String nodeType, final double currentZoomFactor) {
        final double windowXOffset = graphEditorContainer.windowXProperty().get() / currentZoomFactor;
        final double windowYOffset = graphEditorContainer.windowYProperty().get() / currentZoomFactor;

        final GNode node = GraphFactory.eINSTANCE.createGNode();
        node.setY(NODE_INITIAL_Y + windowYOffset);

        node.setType(nodeType);
        node.setX(NODE_INITIAL_X + windowXOffset);
        node.setId(allocateNewId());

        final GConnector input = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(input);
        input.setType(Constants.TITLED_INPUT_CONNECTOR);

        final GConnector output = GraphFactory.eINSTANCE.createGConnector();
        node.getConnectors().add(output);
        output.setType(Constants.TITLED_OUTPUT_CONNECTOR);

        Commands.addNode(graphEditor.getModel(), node);
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