package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import net.belehradek.fumlstudio.AwesomeIcon;

public class ProjectElementFtl implements IProjectElement {

	protected File file;
	protected String name;
	protected IProject project;

	public ProjectElementFtl(IProject project, String name) {
		name = name.replace('.', '_');
		this.project = project;
		this.name = name;
		this.file = new File(project.getRootFolder(), "src/main/ftl/" + name + ".ftl");
		createFile();
	}

	public ProjectElementFtl(IProject project, File file) {
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
		return AwesomeIcon.TRANSFORM_FILE.node();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getType() {
		return "ftl";
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
