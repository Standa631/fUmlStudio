package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementText implements IProjectElement {

	protected File file;
	protected String name;
	protected IProject project;
	
	protected static String ext = "ftl";

	public ProjectElementText(IProject project, String name) {
		this(project, new File(project.getFile(), addTypeToName(name)));
	}
	
	public ProjectElementText(IProject project, File file) {
		this.project = project;
		this.name = file.getName();
		this.file = file;
		createFile();
	}
	
	protected static String addTypeToName(String name) {
		if (!name.contains("."))
			name += "." + ext;
		return name;
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

	@Override
	public Node getIcon() {
		return AwesomeIcon.CODE_FILE.node();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getType() {
		return ext;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return file.getName();
	}
}