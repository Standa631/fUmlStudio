package net.belehradek.fumlstudio.project;

import java.io.File;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementGlobalGraph extends ProjectElementFuml {

	public ProjectElementGlobalGraph(IProject project) {
		super(project, new File("C:\\Users\\Bel2\\DIP\\fUmlTest\\src\\main\\alf\\App.alf"));
	}
	
	@Override
	public String getName() {
		return "Classes";
	}
}
