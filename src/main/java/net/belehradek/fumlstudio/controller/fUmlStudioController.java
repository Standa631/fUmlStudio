package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;

import freemarker.template.TemplateException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.belehradek.AwesomeIcon;
import net.belehradek.Global;
import net.belehradek.Lib;
import net.belehradek.TextAreaLogger;
import net.belehradek.fumlstudio.fUmlStudio;
import net.belehradek.fumlstudio.dialog.NewDiagramDialog;
import net.belehradek.fumlstudio.dialog.NewProjectDialog;
import net.belehradek.fumlstudio.dialog.NewTransformationDialog;
import net.belehradek.fumlstudio.event.EventHandler;
import net.belehradek.fumlstudio.event.EventProjectElementSelected;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.IProjectElement;
import net.belehradek.fumlstudio.project.ProjectElementAlf;
import net.belehradek.fumlstudio.project.ProjectElementFtl;
import net.belehradek.fumlstudio.project.ProjectElementFuml;
import net.belehradek.fumlstudio.project.fUmlProject;
import net.belehradek.umleditor.Constants;

/**
 * Controller for the {@link fUmlStudio} application.
 */
public class fUmlStudioController {

	private static final String APPLICATION_TITLE = "Fuml studio";
//	private static final String APPLICATION_FXML = "fUmlStudioLayout.fxml";
	private static final String APPLICATION_FXML = "fUmlStudioLayoutResizable.fxml";
	
	private static final String DEMO_STYLESHEET = "demo.css";
	private static final String TITLED_SKIN_STYLESHEET = "titledskins.css";
	private static final String FONT_AWESOME = "fontawesome.ttf";
	
	protected Stage stage;

	@FXML private MenuBar menuBar;
	@FXML private Menu connectorTypeMenu;
	@FXML private MenuItem addConnectorButton;
	@FXML private ToggleButton minimapButton;
	@FXML private AnchorPane diagramTestPane;
	
	@FXML protected ComboBox<String> alfFileCombo;
	@FXML protected ComboBox<String> codeGenerationFileCombo;
	
	//conteiners
	@FXML protected AnchorPane logTextAreaConteiner;
	@FXML protected AnchorPane projectTreeConteiner;
	@FXML protected AnchorPane tabsManagerConteiner;
	
	@FXML protected Button imageButton;

	protected IProject activeProject;
	protected ProjectTreeController projectTree;
	protected TabsManager tabsManager;
	protected TextAreaLogger logTextArea;
	

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
			}
		});
		
		logTextArea = new TextAreaLogger();
		Global.setLoggerStream(logTextArea.getPrintStream());
	}

	public void showWindow(Stage stage) throws IOException {
		this.stage = stage;
		
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(APPLICATION_FXML));
		loader.setController(this);
		final Parent root = loader.load();
		final Scene scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource(DEMO_STYLESHEET).toExternalForm());
		scene.getStylesheets().add(getClass().getResource(TITLED_SKIN_STYLESHEET).toExternalForm());
		
		Font.loadFont(getClass().getResource(FONT_AWESOME).toExternalForm(), 12);

		stage.setScene(scene);
		stage.setTitle(APPLICATION_TITLE);
		stage.show();
		
		imageButton.setGraphic(AwesomeIcon.FLOPPY.node(20));
		imageButton.setText("");
	}

	public void initialize() {
		Lib.setZeroAnchor(projectTree);
		projectTreeConteiner.getChildren().add(projectTree);
		
		Lib.setZeroAnchor(tabsManager);
		tabsManagerConteiner.getChildren().add(tabsManager);
		
		Lib.setZeroAnchor(logTextArea);
		logTextAreaConteiner.getChildren().add(logTextArea);
	}

	@FXML
	public void actionNewProject() {
		NewProjectDialog newProjectDialog = new NewProjectDialog() {
			@Override protected void finishCallback(File projectRoot) {
				activeProject = new fUmlProject();
				activeProject.createNewProject(projectRoot); 
				projectTree.showProject(activeProject);
			} 
		};
		try {
			newProjectDialog.showWindow(); 
		} catch (IOException e) {
			e.printStackTrace(); 
		}

		// TEST:
		//activeProject = new fUmlProject();
		//activeProject.createNewProject(new File("C:\\Users\\Bel2\\DIP\\fUmlTest"));
		//projectTree.showProject(activeProject);
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
				//tabsManager.clear();
			}
		};
		try {
			newDiagramDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void newClassDiagram() {
		IProjectElementEditor editor = tabsManager.getActiveEditor();
		if (editor instanceof GraphicEditor) {
			GraphicEditor graphic = (GraphicEditor) editor;
			graphic.addNode(Constants.CLASS_NODE, "id", 1.0);
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
				//tabsManager.clear();
			}
		};
		try {
			newTransformationDialog.showWindow();
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
		
		for (IProjectElement e : activeProject.getAllElements()) {
			if (e instanceof ProjectElementAlf && e.getType().equals("alf"))
				alfFileCombo.getItems().add(e.getName());
			else if (e instanceof ProjectElementFtl)
				codeGenerationFileCombo.getItems().add(e.getName());
		}
		
		alfFileCombo.getSelectionModel().select(0);
		codeGenerationFileCombo.getSelectionModel().select(0);
	}
	
	@FXML
	public void loadSample() {
		
	}
	
	@FXML
	public void runCodeGeneration() throws IOException, TemplateException {
		if (activeProject == null) {
			Global.log("ERROR: project is not open!");
		} else {
			activeProject.generateCode();
		}
	}

	@FXML
	public void exitApp() {
		Platform.exit();
		System.exit(0);
	}
	
	@FXML
	public void buildProject() {
		activeProject.build();
	}
	
	@FXML
	public void debugProject() {
		activeProject.debug();
	}
	
	@FXML
	public void cleanProject() {
		activeProject.clean();
	}

	@FXML
	public void clearAll() {
		//Commands.clear(graphEditor.getModel());
	}

	@FXML
	public void undo() {
		//Commands.undo(graphEditor.getModel());
	}

	@FXML
	public void redo() {
		//Commands.redo(graphEditor.getModel());
	}

	@FXML
	public void cut() {
		//graphEditor.getSelectionManager().cut();
	}

	@FXML
	public void copy() {
		//graphEditor.getSelectionManager().copy();
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
		//graphEditor.getSelectionManager().deleteSelection();
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

		//graphEditor.getProperties().getCustomProperties().remove(SimpleConnectionSkin.SHOW_DETOURS_KEY);
		//graphEditor.reload();
	}

	@FXML
	public void setDetouredStyle() {
		//final Map<String, String> customProperties = graphEditor.getProperties().getCustomProperties();
		//customProperties.put(SimpleConnectionSkin.SHOW_DETOURS_KEY, Boolean.toString(true));
		//graphEditor.reload();
	}

	@FXML
	public void toggleMinimap() {
		//graphEditorContainer.getMinimap().visibleProperty().bind(minimapButton.selectedProperty());
	}
}
