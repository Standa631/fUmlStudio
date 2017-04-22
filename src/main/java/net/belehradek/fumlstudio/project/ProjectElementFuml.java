package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;
import net.belehradek.Lib;
import net.belehradek.fuml.core.XmiModelSaver;

public class ProjectElementFuml implements IProjectElement  {
	
	protected File file;
	protected String name;
	protected IProject project;

	public ProjectElementFuml(IProject project, String name) {
		this.project = project;
		this.name = name;
		this.file = new File(project.getFile(), "src/main/fuml/" + name + ".fuml");
		createFile();
	}
	
	public ProjectElementFuml(IProject project, File file) {
		this.project = project;
		this.name = file.getName();
		this.file = file;
		createFile();
	}
	
	protected void createFile() {
		try {
			if (!file.exists()) {
				System.out.println("Create element: " + file.getAbsolutePath());
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			File g = null;
			String path = file.getAbsolutePath();
			if (path.endsWith(".fuml")) {
				g = new File(file.getAbsolutePath().replace(".fuml", ".graph"));
			} else if (path.endsWith(".uml")) {
				g = new File(file.getAbsolutePath().replace(".uml", ".graph"));
			} else {
				return null;
			}
			if (!g.exists()) {
				createGraphFile(g);
			}
			return g;
		} else {
			return null;
		}
	}

	@Override
	public Node getIcon() {
		return AwesomeIcon.DIAGRAM_FILE.node();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getType() {
		return "fuml";
	}	
	
	@Override
	public String toString() {
		return file.getName();
	}
	
	@Override
	public String getName() {
		return file.getName();
	}

//	@Override
//	public void save() {
//		System.out.println("Save .graph file: " + projectElementFuml.getGraphicFile().getAbsolutePath());
//		graphEditorPersistence.saveModel(projectElementFuml.getGraphicFile(), graphEditor.getModel());
//		System.out.println("Saved");
//		
//		String path = Lib.toFilePath(projectElement.getFile().getParent());
//		System.out.println("Save .xmi file: " + path + "/" + projectElement.getFile().getName());
//		XmiModelSaver saver = new XmiModelSaver();
//		try {
//			saver.saveModel(path, projectElement.getFile().getName(), model);
//			System.out.println("Saved");
//		} catch (IOException e) {
//			System.out.println("Not saved!");
//			e.printStackTrace();
//		}
//	}
}
