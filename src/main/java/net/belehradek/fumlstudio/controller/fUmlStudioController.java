package net.belehradek.fumlstudio.controller;

import java.io.File;
import java.io.IOException;

import javax.swing.event.ChangeListener;

import freemarker.template.TemplateException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.belehradek.AwesomeIcon;
import net.belehradek.Global;
import net.belehradek.Lib;
import net.belehradek.TextAreaLogger;
import net.belehradek.fumlstudio.IdeWorker;
import net.belehradek.fumlstudio.IdeWorkerTask;
import net.belehradek.fumlstudio.IdeWorkerTaskChangeEvent;
import net.belehradek.fumlstudio.fUmlStudio;
import net.belehradek.fumlstudio.dialog.NewDiagramDialog;
import net.belehradek.fumlstudio.dialog.NewProjectDialog;
import net.belehradek.fumlstudio.dialog.NewSourceDialog;
import net.belehradek.fumlstudio.dialog.NewTransformationDialog;
import net.belehradek.fumlstudio.event.EventHandler;
import net.belehradek.fumlstudio.event.EventProjectElement;
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

	private static final String APPLICATION_TITLE = "fUml studio";
	private static final String APPLICATION_FXML = "fUmlStudioLayoutResizable.fxml";

	private static final String DEMO_STYLESHEET = "demo.css";
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
	protected ComboBox<String> alfFileCombo;
	@FXML
	protected ComboBox<String> codeGenerationFileCombo;
	@FXML
	protected TextField namespacePrefix;

	// conteiners
	@FXML
	protected AnchorPane logTextAreaConteiner;
	@FXML
	protected AnchorPane projectTreeConteiner;
	@FXML
	protected AnchorPane tabsManagerConteiner;

	// @FXML protected Button saveFileButton;

	@FXML
	protected ProgressBar progressBar;
	@FXML
	protected Label progressLabel;

	protected fUmlProject activeProject;
	protected ProjectTreeController projectTree;
	protected TabsManager tabsManager;
	protected TextAreaLogger logTextArea;

	protected IdeWorker ideWorker;

	public fUmlStudioController() {
		projectTree = new ProjectTreeController();
		EventRouter.registerHandler(projectTree, new EventHandler<EventProjectElement>() {
			@Override
			public void handle(EventProjectElement event) {
				Global.log("Event projectElementTreeSelected: " + event.getElement());
				tabsManager.addElementTab(event.getElement());
			}
		});

		tabsManager = new TabsManager();
		EventRouter.registerHandler(tabsManager, new EventHandler<EventProjectElement>() {
			@Override
			public void handle(EventProjectElement event) {
				Global.log("Event projectElementTabSelected: " + event.getElement());
			}
		});

		logTextArea = new TextAreaLogger();
		Global.setLoggerStream(logTextArea.getPrintStream());

		IdeWorker.getInstance().addOnChangeListener(new IdeWorkerTaskChangeEvent() {
			@Override
			public void chnageState(IdeWorkerTask task, boolean done) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (done) {
							progressBar.setProgress(0.0);
							progressLabel.setText("All done");
						} else {
							progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
							progressLabel.setText(task.label);
						}
					}
				});
			}
		});
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

		// saveFileButton.setGraphic(AwesomeIcon.FLOPPY.node(20));
		// saveFileButton.setText("");

		alfFileCombo.getSelectionModel().selectedItemProperty()
				.addListener(new javafx.beans.value.ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						activeProject.setGradleSettingsUnitName(newValue.substring(0, newValue.indexOf(".")));
					}
				});
		codeGenerationFileCombo.getSelectionModel().selectedItemProperty()
				.addListener(new javafx.beans.value.ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						activeProject.setGradleSettingsTransform(newValue);
					}
				});
		namespacePrefix.textProperty().addListener((observable, oldValue, newValue) -> {
			activeProject.setGradleSettingsNamespace(newValue);
		});
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
			@Override
			protected void finishCallback(File projectRoot) {
				activeProject = new fUmlProject();
				ideWorker.addTask(new IdeWorkerTask("Initializing new project...") {
					@Override
					public void work() {
						activeProject.createNewProject(projectRoot);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								// projectTree.showProject(activeProject);
								openProject(activeProject.getFile());
							}
						});
					}
				});
			}
		};
		try {
			newProjectDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void actionNewElement() {
		NewDiagramDialog newDiagramDialog = new NewDiagramDialog() {
			@Override
			protected void finishCallback(String name, boolean activity, boolean graphic) {
				Global.log("Create new element: '" + name + "' " + (activity ? "activity" : "class") + " "
						+ (graphic ? "graphic" : "textual"));
				if (graphic) {
					activeProject.addProjectElement(new ProjectElementFuml(activeProject, name));
				} else {
					activeProject.addProjectElement(new ProjectElementAlf(activeProject, name));
				}
				projectTree.showProject(activeProject);
				// tabsManager.clear();
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
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		IProjectElementEditor editor = tabsManager.getActiveEditor();
		if (editor instanceof GraphicEditor) {
			GraphicEditor graphic = (GraphicEditor) editor;
			graphic.addNode(Constants.CLASS_NODE, "id", 1.0);
		}
	}

	@FXML
	public void newTransformationFile() {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		NewTransformationDialog newTransformationDialog = new NewTransformationDialog() {
			@Override
			protected void finishCallback(String transformationId) {
				Global.log("Create new transformation: '" + transformationId);
				activeProject.addProjectElement(new ProjectElementFtl(activeProject, transformationId));
				projectTree.showProject(activeProject);
				// tabsManager.clear();
				activeProject.loadProject(activeProject.getFile());
				projectTree.showProject(activeProject);
			}
		};
		try {
			newTransformationDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void newSourceFile() {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		NewSourceDialog newSourceDialog = new NewSourceDialog() {
			@Override
			protected void finishCallback(String transformationId) {
				Global.log("Create new alf source file: '" + transformationId);
				File f = new File(activeProject.getFile(), "src/main/alf/" + transformationId);
				activeProject.addProjectElement(new ProjectElementAlf(activeProject, f));
				// projectTree.showProject(activeProject);
				// tabsManager.clear();
				activeProject.loadProject(activeProject.getFile());
				projectTree.showProject(activeProject);
			}
		};
		try {
			newSourceDialog.showWindow();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void saveFile() {
		tabsManager.saveActive();
	}

	@FXML
	public void deleteFile() {

	}

	@FXML
	public void openProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File location = directoryChooser.showDialog(stage);

		// testovani
		// File location = new File("C:\\Users\\Bel2\\DIP\\fUmlTest");

		openProject(location);
	}

	protected void openProject(File location) {
		if (location != null) {
			activeProject = new fUmlProject();
			boolean ok = activeProject.loadProject(location);
			if (ok) {
				projectTree.showProject(activeProject);
				tabsManager.clear();

				for (IProjectElement e : activeProject.getAllElements()) {
					if (e instanceof ProjectElementAlf && e.getType().equals("alf"))
						alfFileCombo.getItems().add(e.getName());
					else if (e instanceof ProjectElementFtl)
						codeGenerationFileCombo.getItems().add(e.getName());
				}

				String unit = activeProject.getGradleSettingsUnitName() + ".alf";
				String transform = activeProject.getGradleSettingsTransform();

				alfFileCombo.getSelectionModel().select(unit);
				codeGenerationFileCombo.getSelectionModel().select(transform);

				namespacePrefix.setText(activeProject.getGradleSettingsNamespace());
			} else {
				Global.log("ERROR: Opening no project folder!");
			}
		}
	}

	@FXML
	public void loadSample() {

	}

	@FXML
	public void runCodeGeneration() throws IOException, TemplateException {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		ideWorker.addTask(new IdeWorkerTask("Generating code...") {
			@Override
			public void work() {
				activeProject.generateCode();
			}
		});
	}

	@FXML
	public void exitApp() {
		Platform.exit();
		System.exit(0);
	}

	@FXML
	public void buildProject() {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		ideWorker.addTask(new IdeWorkerTask("Building project...") {
			@Override
			public void work() {
				activeProject.build();
			}
		});
	}

	@FXML
	public void debugProject() {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		ideWorker.addTask(new IdeWorkerTask("Debugging project...") {
			@Override
			public void work() {
				activeProject.debug();
			}
		});
	}

	@FXML
	public void cleanProject() {
		if (activeProject == null) {
			Global.log("ERROR: No project is open!");
			return;
		}

		ideWorker.addTask(new IdeWorkerTask("Cleaning project...") {
			@Override
			public void work() {
				activeProject.clean();
			}
		});
	}

	@FXML
	public void clearAll() {
		// Commands.clear(graphEditor.getModel());
	}

	@FXML
	public void undo() {
		// Commands.undo(graphEditor.getModel());
	}

	@FXML
	public void redo() {
		// Commands.redo(graphEditor.getModel());
	}

	@FXML
	public void cut() {
		// graphEditor.getSelectionManager().cut();
	}

	@FXML
	public void copy() {
		// graphEditor.getSelectionManager().copy();
	}

	@FXML
	public void paste() {
		// activeSkinController.get().handlePaste();
	}

	@FXML
	public void selectAll() {
		// activeSkinController.get().handleSelectAll();
	}

	@FXML
	public void deleteSelection() {
		// graphEditor.getSelectionManager().deleteSelection();
	}

	@FXML
	public void addNode() {
		// activeSkinController.get().addNode(Constants.CIRCLE_NODE,
		// currentZoomFactor);
	}

	@FXML
	public void addConnector() {
		// activeSkinController.get().addConnector(null, Side.BOTTOM,
		// inputConnectorTypeButton.isSelected());
		// activeSkinController.get().addConnector(null, Side.BOTTOM, true);
	}

	@FXML
	public void clearConnectors() {
		// activeSkinController.get().clearConnectors();
	}

	@FXML
	public void setDefaultSkin() {
		// activeSkinController.set(titledSkinController);
	}

	@FXML
	public void setTitledSkin() {
		// activeSkinController.set(titledSkinController);
	}

	@FXML
	public void setGappedStyle() {

		// graphEditor.getProperties().getCustomProperties().remove(SimpleConnectionSkin.SHOW_DETOURS_KEY);
		// graphEditor.reload();
	}

	@FXML
	public void setDetouredStyle() {
		// final Map<String, String> customProperties =
		// graphEditor.getProperties().getCustomProperties();
		// customProperties.put(SimpleConnectionSkin.SHOW_DETOURS_KEY,
		// Boolean.toString(true));
		// graphEditor.reload();
	}

	@FXML
	public void toggleMinimap() {
		// graphEditorContainer.getMinimap().visibleProperty().bind(minimapButton.selectedProperty());
	}
}
