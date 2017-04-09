/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.belehradek.fumlstudio.GraphEditorPersistence;
import net.belehradek.fumlstudio.Lib;
import net.belehradek.fumlstudio.fUmlStudio;
import net.belehradek.fumlstudio.event.EventHandler;
import net.belehradek.fumlstudio.event.EventProjectElementSelected;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.ProjectElementAlf;
import net.belehradek.fumlstudio.project.ProjectElementFtl;
import net.belehradek.fumlstudio.project.ProjectElementFuml;
import net.belehradek.fumlstudio.project.fUmlProject;
//import net.belehradek.umlgraphiceditor.ISkinController;
//import net.belehradek.umlgraphiceditor.SkinController;
import net.belehradek.umlgraphiceditor.Constants;

import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.window.WindowPosition;

/**
 * Controller for the {@link fUmlStudio} application.
 */
public class fUmlStudioController {

	private static final String STYLE_CLASS_TITLED_SKINS = "titled-skins";
	private static final String APPLICATION_TITLE = "Graph Editor Demo";
	private static final String DEMO_STYLESHEET = "demo.css";
	private static final String TREE_SKIN_STYLESHEET = "treeskins.css";
	private static final String TITLED_SKIN_STYLESHEET = "titledskins.css";
	private static final String FONT_AWESOME = "fontawesome.ttf";
	
	protected Stage stage;

	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu connectorTypeMenu;
	@FXML
	private MenuItem addConnectorButton;

	@FXML
	private ToggleButton minimapButton;

	@FXML
	private AnchorPane diagramTestPane;
	
	

	@FXML
	private AnchorPane projectTreeConteiner;

	private GraphEditorContainer graphEditorContainer;
	private final GraphEditor graphEditor = new DefaultGraphEditor();
	private final GraphEditorPersistence graphEditorPersistence = new GraphEditorPersistence();

	private Scale scaleTransform;
	private double currentZoomFactor = 1;

//	private ISkinController titledSkinController;

//	private final ObjectProperty<ISkinController> activeSkinController = new SimpleObjectProperty<>();

	protected IProject activeProject;
	protected ProjectTreeController projectTree;
	
	@FXML
	protected AnchorPane tabsManagerConteiner;
	public TabsManager tabsManager;
	

	public fUmlStudioController() {
		projectTree = new ProjectTreeController();
		
		EventRouter.registerHandler(projectTree, new EventHandler<EventProjectElementSelected>() {
			@Override
			public void handle(EventProjectElementSelected event) {
				System.out.println("Event projectElementTreeSelected: " + event.getElement());
				tabsManager.addElementTab(event.getElement());
			}
		});
		
		tabsManager = new TabsManager();
		EventRouter.registerHandler(tabsManager, new EventHandler<EventProjectElementSelected>() {
			@Override
			public void handle(EventProjectElementSelected event) {
				System.out.println("Event projectElementTabSelected: " + event.getElement());
				tabsManager.addElementTab(event.getElement());
			}
		});
	}

	public void showWindow() throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("fUmlStudioLayout.fxml"));
		loader.setController(this);
		final Parent root = loader.load();
		final Scene scene = new Scene(root);
		stage = new Stage();

		scene.getStylesheets().add(getClass().getResource(DEMO_STYLESHEET).toExternalForm());
		// scene.getStylesheets().add(getClass().getResource(TREE_SKIN_STYLESHEET).toExternalForm());
		scene.getStylesheets().add(getClass().getResource(TITLED_SKIN_STYLESHEET).toExternalForm());
		Font.loadFont(getClass().getResource(FONT_AWESOME).toExternalForm(), 12);

		stage.setScene(scene);
		stage.setTitle(APPLICATION_TITLE);

		stage.show();

		panToCenter();
	}

	/**
	 * Called by JavaFX when FXML is loaded.
	 */
	public void initialize() {

		final GModel model = GraphFactory.eINSTANCE.createGModel();
		graphEditor.setModel(model);

		graphEditorContainer = new GraphEditorContainer();
		Lib.setZeroAnchor(graphEditorContainer);
		tabsManagerConteiner.getChildren().add(graphEditorContainer);
		graphEditorContainer.setGraphEditor(graphEditor);
		setDetouredStyle();

//		titledSkinController = new SkinController(graphEditor, graphEditorContainer);

//		activeSkinController.set(titledSkinController);

		addActiveSkinControllerListener();

		Lib.setZeroAnchor(projectTree);
		projectTreeConteiner.getChildren().add(projectTree);
		
		Lib.setZeroAnchor(tabsManager);
		tabsManagerConteiner.getChildren().add(tabsManager);
	}

	@FXML
	public void actionNewProject() {
		/*
		 * NewProjectDialog newProjectDialog = new NewProjectDialog() {
		 * 
		 * @Override protected void finishCallback(File projectRoot) {
		 * activeProject = new fUmlProject();
		 * activeProject.createNewProject(projectRoot); } }; try {
		 * newProjectDialog.showWindow(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		// TEST:
		activeProject = new fUmlProject();
		activeProject.createNewProject(new File("C:\\Users\\Bel2\\DIP\\fUmlTest"));

		projectTree.showProject(activeProject);
	}

	@FXML
	public void actionNewElement() {
		NewDiagramDialog newDiagramDialog = new NewDiagramDialog() {
			@Override
			protected void finishCallback(String name, boolean activity, boolean graphic) {
				System.out.println("Create new element: '" + name + "' " + (activity?"activity":"class") + " " + (graphic?"graphic":"textual"));
				if (graphic) {
					activeProject.addProjectElement(new ProjectElementFuml(activeProject, name));
				} else {
					activeProject.addProjectElement(new ProjectElementAlf(activeProject, name));
				}
				projectTree.showProject(activeProject);
				tabsManager.clear();
			}
		};
		try {
			newDiagramDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void saveFile() {
		tabsManager.saveActive();
	}

	@FXML
	public void openProject() {
		/*DirectoryChooser directoryChooser = new DirectoryChooser();
    	//directoryChooser.setInitialDirectory(location);
        File location = directoryChooser.showDialog(stage);
        if (location != null) {
        	activeProject = new fUmlProject();
    		activeProject.loadProject(location);
    		projectTree.showProject(activeProject);
    		tabsManager.clear();
        }*/
		
		activeProject = new fUmlProject();
		activeProject.loadProject(new File("C:\\Users\\Bel2\\DIP\\fUmlTest"));
		projectTree.showProject(activeProject);
		tabsManager.clear();
	}

	@FXML
	public void load() {
		graphEditorPersistence.loadFromFile(graphEditor);
	}

	@FXML
	public void loadSample() {
		// defaultSkinButton.setSelected(true);
		// setTitledSkin();
		graphEditorPersistence.loadSample(graphEditor);
	}

	@FXML
	public void exitApp() {
		Platform.exit();
		System.exit(0);
	}

	@FXML
	public void loadSampleLarge() {
		// defaultSkinButton.setSelected(true);
		setDefaultSkin();
		graphEditorPersistence.loadSampleLarge(graphEditor);
	}

	@FXML
	public void loadTitled() {
		// titledSkinButton.setSelected(true);
		setTitledSkin();
		graphEditorPersistence.loadTitled(graphEditor);
	}

	@FXML
	public void save() {
		graphEditorPersistence.saveToFile(graphEditor);
	}

	@FXML
	public void clearAll() {
		Commands.clear(graphEditor.getModel());
	}

	@FXML
	public void exit() {
		Platform.exit();
	}

	@FXML
	public void undo() {
		Commands.undo(graphEditor.getModel());
	}

	@FXML
	public void redo() {
		Commands.redo(graphEditor.getModel());
	}

	@FXML
	public void cut() {
		graphEditor.getSelectionManager().cut();
	}

	@FXML
	public void copy() {
		graphEditor.getSelectionManager().copy();
	}

	@FXML
	public void paste() {
//		activeSkinController.get().handlePaste();
	}

	@FXML
	public void selectAll() {
//		activeSkinController.get().handleSelectAll();
	}

	@FXML
	public void deleteSelection() {
		graphEditor.getSelectionManager().deleteSelection();
	}
	
	@FXML
	public void newClassDiagram() {
		FileEditor editor = tabsManager.getActiveEditor();
		if (editor instanceof GraphicEditor) {
			GraphicEditor graphic = (GraphicEditor) editor;
			graphic.addNode(Constants.TITLED_NODE, 1.0);
		}
	}
	
	@FXML
	public void newTransformationFile() {
		NewTransformationDialog newTransformationDialog = new NewTransformationDialog() {
			@Override
			protected void finishCallback(String transformationId) {
				System.out.println("Create new transformation: '" + transformationId);
				activeProject.addProjectElement(new ProjectElementFtl(activeProject, transformationId));
				projectTree.showProject(activeProject);
				tabsManager.clear();
			}
		};
		try {
			newTransformationDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void addNode() {
//		activeSkinController.get().addNode(Constants.CIRCLE_NODE, currentZoomFactor);
	}

	@FXML
	public void addConnector() {
		// activeSkinController.get().addConnector(null, Side.BOTTOM,
		// inputConnectorTypeButton.isSelected());
//		activeSkinController.get().addConnector(null, Side.BOTTOM, true);
	}

	@FXML
	public void clearConnectors() {
//		activeSkinController.get().clearConnectors();
	}

	@FXML
	public void setDefaultSkin() {
//		activeSkinController.set(titledSkinController);
	}

	@FXML
	public void setTitledSkin() {
//		activeSkinController.set(titledSkinController);
	}

	@FXML
	public void setGappedStyle() {

		graphEditor.getProperties().getCustomProperties().remove(SimpleConnectionSkin.SHOW_DETOURS_KEY);
		graphEditor.reload();
	}

	@FXML
	public void setDetouredStyle() {

		final Map<String, String> customProperties = graphEditor.getProperties().getCustomProperties();
		customProperties.put(SimpleConnectionSkin.SHOW_DETOURS_KEY, Boolean.toString(true));
		graphEditor.reload();
	}

	@FXML
	public void toggleMinimap() {
		graphEditorContainer.getMinimap().visibleProperty().bind(minimapButton.selectedProperty());
	}

	/**
	 * Pans the graph editor container to place the window over the center of
	 * the content.
	 *
	 * <p>
	 * Only works after the scene has been drawn, when getWidth() & getHeight()
	 * return non-zero values.
	 * </p>
	 */
	public void panToCenter() {
		graphEditorContainer.panTo(WindowPosition.CENTER);
	}

	/**
	 * Initializes the menu bar.
	 */
	// private void initializeMenuBar() {
	//
	// scaleTransform = new Scale(currentZoomFactor, currentZoomFactor, 0, 0);
	// scaleTransform.yProperty().bind(scaleTransform.xProperty());
	//
	// graphEditor.getView().getTransforms().add(scaleTransform);
	//
	// final ToggleGroup skinGroup = new ToggleGroup();
	// skinGroup.getToggles().addAll(defaultSkinButton, titledSkinButton);
	//
	// final ToggleGroup connectionStyleGroup = new ToggleGroup();
	// connectionStyleGroup.getToggles().addAll(gappedStyleButton,
	// detouredStyleButton);
	//
	// final ToggleGroup connectorTypeGroup = new ToggleGroup();
	// connectorTypeGroup.getToggles().addAll(inputConnectorTypeButton,
	// outputConnectorTypeButton);
	//
	// final ToggleGroup positionGroup = new ToggleGroup();
	// positionGroup.getToggles().addAll(leftConnectorPositionButton,
	// rightConnectorPositionButton);
	// positionGroup.getToggles().addAll(topConnectorPositionButton,
	// bottomConnectorPositionButton);
	//
	// graphEditor.getProperties().gridVisibleProperty().bind(showGridButton.selectedProperty());
	// graphEditor.getProperties().snapToGridProperty().bind(snapToGridButton.selectedProperty());
	//
	// minimapButton.setGraphic(AwesomeIcon.MAP.node());
	//
	// initializeZoomOptions();
	//
	// final ListChangeListener<? super GNode> selectedNodesListener = change ->
	// {
	// //checkConnectorButtonsToDisable();
	// };
	//
	// graphEditor.getSelectionManager().getSelectedNodes().addListener(selectedNodesListener);
	// }

	/**
	 * Initializes the list of zoom options.
	 */
	private void initializeZoomOptions() {

		final ToggleGroup toggleGroup = new ToggleGroup();

		for (int i = 1; i <= 5; i++) {

			final RadioMenuItem zoomOption = new RadioMenuItem();
			final double zoomFactor = i;

			zoomOption.setText(i + "00%");
			zoomOption.setOnAction(event -> setZoomFactor(zoomFactor));

			toggleGroup.getToggles().add(zoomOption);
			// zoomOptions.getItems().add(zoomOption);

			if (i == 1) {
				zoomOption.setSelected(true);
			}
		}
	}

	/**
	 * Sets a new zoom factor.
	 *
	 * <p>
	 * Note that everything will look crap if the zoom factor is non-integer.
	 * </p>
	 *
	 * @param zoomFactor
	 *            the new zoom factor
	 */
	private void setZoomFactor(final double zoomFactor) {

		final double zoomFactorRatio = zoomFactor / currentZoomFactor;

		final double currentCenterX = graphEditorContainer.windowXProperty().get();
		final double currentCenterY = graphEditorContainer.windowYProperty().get();

		if (zoomFactor != 1) {
			// Cache-while-panning is sometimes very sluggish when zoomed in.
			graphEditorContainer.setCacheWhilePanning(false);
		} else {
			graphEditorContainer.setCacheWhilePanning(true);
		}

		scaleTransform.setX(zoomFactor);
		graphEditorContainer.panTo(currentCenterX * zoomFactorRatio, currentCenterY * zoomFactorRatio);
		currentZoomFactor = zoomFactor;
	}

	/**
	 * Adds a listener to make changes to available menu options when the skin
	 * type changes.
	 */
	private void addActiveSkinControllerListener() {

//		activeSkinController.addListener((observable, oldValue, newValue) -> {
//			handleActiveSkinControllerChange();
//		});
	}

	/**
	 * Enables & disables certain menu options and sets CSS classes based on the
	 * new skin type that was set active.
	 */
	private void handleActiveSkinControllerChange() {

		// if (treeSkinController.equals(activeSkinController.get())) {
		//
		// graphEditor.setConnectorValidator(new TreeConnectorValidator());
		// graphEditor.getSelectionManager().setConnectionSelectionPredicate(new
		// TreeConnectionSelectionPredicate());
		// graphEditor.getView().getStyleClass().remove(STYLE_CLASS_TITLED_SKINS);
		// treeSkinButton.setSelected(true);
		//
		// } else
		// if (titledSkinController.equals(activeSkinController.get())) {
		{
			graphEditor.setConnectorValidator(null);
			graphEditor.getSelectionManager().setConnectionSelectionPredicate(null);
			if (!graphEditor.getView().getStyleClass().contains(STYLE_CLASS_TITLED_SKINS)) {
				graphEditor.getView().getStyleClass().add(STYLE_CLASS_TITLED_SKINS);
			}
			// titledSkinButton.setSelected(true);

		}
		// else {
		//
		// graphEditor.setConnectorValidator(null);
		// graphEditor.getSelectionManager().setConnectionSelectionPredicate(null);
		// graphEditor.getView().getStyleClass().remove(STYLE_CLASS_TITLED_SKINS);
		// defaultSkinButton.setSelected(true);
		// }

		// Demo does not currently support mixing of skin types. Skins don't
		// know how to cope with it.
		clearAll();
		flushCommandStack();
		graphEditor.getSelectionManager().clearMemory();
	}

	/**
	 * Flushes the command stack, so that the undo/redo history is cleared.
	 */
	private void flushCommandStack() {

		final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(graphEditor.getModel());
		if (editingDomain != null) {
			editingDomain.getCommandStack().flush();
		}
	}
}
