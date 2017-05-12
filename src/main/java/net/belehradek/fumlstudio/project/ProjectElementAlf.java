package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementAlf extends ProjectElementText {

	public ProjectElementAlf(IProject project, String name) {
		super(project, name);
	}
	
	public ProjectElementAlf(IProject project, File file) {
		super(project, file);
	}

	@Override
	public String getType() {
		return "alf";
	}
}
