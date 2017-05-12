package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementFtl extends ProjectElementText {

	public ProjectElementFtl(IProject project, String name) {
		super(project, "src\\main\\ftl\\" + name);
	}
	
	public ProjectElementFtl(IProject project, File file) {
		super(project, file);
	}

	@Override
	public String getType() {
		return "ftl";
	}
}
