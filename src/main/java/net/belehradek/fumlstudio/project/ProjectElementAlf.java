package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementAlf implements IProjectElement {

	protected File file;
	protected String name;
	protected IProject project;

	public ProjectElementAlf(IProject project, String name) {
		this.project = project;
		this.name = name;
		this.file = new File(project.getFile(), "src/main/alf/" + name + ".alf");
		createFile();
	}

	public ProjectElementAlf(IProject project, File file) {
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
		return "alf";
	}
	
	@Override
	public String toString() {
		return file.getName();
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
