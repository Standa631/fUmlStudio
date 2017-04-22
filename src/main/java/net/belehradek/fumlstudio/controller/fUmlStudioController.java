/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import net.belehradek.Global;
import net.belehradek.Lib;
import net.belehradek.TextAreaLogger;
import net.belehradek.fuml.codegeneration.CreateFileDirective;
import net.belehradek.fuml.codegeneration.ExtendedMethodWrapper;
import net.belehradek.fuml.codegeneration.MethodWrapper;
import net.belehradek.fuml.codegeneration.TemplateEngineHelper;
import net.belehradek.fuml.core.XmiModelLoader;
import net.belehradek.fumlstudio.fUmlStudio;
import net.belehradek.fumlstudio.dialog.NewDiagramDialog;
import net.belehradek.fumlstudio.dialog.NewProjectDialog;
import net.belehradek.fumlstudio.dialog.NewTransformationDialog;
import net.belehradek.fumlstudio.event.EventHandler;
import net.belehradek.fumlstudio.event.EventProjectElementSelected;
import net.belehradek.fumlstudio.event.EventRouter;
import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.ProjectElementAlf;
import net.belehradek.fumlstudio.project.ProjectElementFtl;
import net.belehradek.fumlstudio.project.ProjectElementFuml;
import net.belehradek.fumlstudio.project.fUmlProject;
import net.belehradek.umleditor.Constants;

import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.modeldriven.alf.eclipse.papyrus.execution.Fuml.ElementResolutionError;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.core.skins.defaults.connection.SimpleConnectionSkin;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import de.tesis.dynaware.grapheditor.window.WindowPosition;
import freemarker.template.TemplateException;

import org.modeldriven.alf.uml.Package;

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
	
	//conteiners
	@FXML protected AnchorPane logTextAreaConteiner;
	@FXML protected AnchorPane projectTreeConteiner;
	@FXML protected AnchorPane tabsManagerConteiner;

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
		
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("fUmlStudioLayout.fxml"));
		loader.setController(this);
		final Parent root = loader.load();
		final Scene scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource(DEMO_STYLESHEET).toExternalForm());
		scene.getStylesheets().add(getClass().getResource(TITLED_SKIN_STYLESHEET).toExternalForm());
		Font.loadFont(getClass().getResource(FONT_AWESOME).toExternalForm(), 12);

		stage.setScene(scene);
		stage.setTitle(APPLICATION_TITLE);
		stage.show();
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
		ProjectElementEditor editor = tabsManager.getActiveEditor();
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
	}
	
	@FXML
	public void loadSample() {
		
	}
	
	@FXML
	public void runCodeGeneration() throws IOException, TemplateException, ElementResolutionError {
		XmiModelLoader m = new XmiModelLoader(new File("C:\\Users\\Bel2\\DIP\\fUmlGradlePlugin\\Libraries"), new File("C:\\Users\\Bel2\\DIP\\fUmlTest\\build\\fuml"));
		Package p = m.loadModel("App");
		
		Map<String, Object> model = new HashMap<>();
		model.put("model", new ExtendedMethodWrapper(p, p));
		model.put("createFile", new CreateFileDirective(new File("c:\\Users\\Bel2\\DIP\\fUmlTest\\out\\src\\main\\java\\")));
		model.put("test", "cau");
		
		TemplateEngineHelper.transform(new File("c:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\ftl\\javaClass.ftl"), model);
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
