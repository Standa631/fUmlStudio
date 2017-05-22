package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;
import net.belehradek.Lib;

public class ProjectElementFuml extends ProjectElementText  {
	
	protected final String GRAPH_EXTENSION = ".graph";

	public ProjectElementFuml(IProject project, String name) {
		super(project, "src/main/fuml/" + name);
	}
	
	public ProjectElementFuml(IProject project, File file) {
		super(project, file);
	}
	
	public void createGraphFile(File newFile) {
		try {
			File graphTemplate = new File(getClass().getResource("FumlProjectStructure/diagram.graph").getPath());
			FileUtils.copyFile(graphTemplate, newFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getGraphicFile() {
		if (file != null) {
			File g = new File(getGraphFileName());
			if (!g.exists()) {
				createGraphFile(g);
			}
			return g;
		} else {
			return null;
		}
	}
	
	public String getGraphFileName() {
		String path = file.getAbsolutePath();
		if (!path.endsWith(GRAPH_EXTENSION))
			path += GRAPH_EXTENSION;
		return path;
	}

	@Override
	public Node getIcon() {
		return AwesomeIcon.DIAGRAM_FILE.node();
	}

	@Override
	public String getType() {
		return "fuml";
	}	

//	@Override
//	public void save() {
//		Global.log("Save .graph file: " + projectElementFuml.getGraphicFile().getAbsolutePath());
//		graphEditorPersistence.saveModel(projectElementFuml.getGraphicFile(), graphEditor.getModel());
//		Global.log("Saved");
//		
//		String path = Lib.toFilePath(projectElement.getFile().getParent());
//		Global.log("Save .xmi file: " + path + "/" + projectElement.getFile().getName());
//		XmiModelSaver saver = new XmiModelSaver();
//		try {
//			saver.saveModel(path, projectElement.getFile().getName(), model);
//			Global.log("Saved");
//		} catch (IOException e) {
//			Global.log("Not saved!");
//			e.printStackTrace();
//		}
//	}
}
