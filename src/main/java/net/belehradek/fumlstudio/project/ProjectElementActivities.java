package net.belehradek.fumlstudio.project;

import java.io.File;

public class ProjectElementActivities extends ProjectElementFuml {

	public ProjectElementActivities(IProject project) {
		super(project, new File(project.getFile(), "src\\main\\alf\\App.alf"));
	}
	
	@Override
	public String getName() {
		return "Activities";
	}
	
	@Override
	public String getGraphFileName() {
		return super.getGraphFileName().replace(".graph", "2.graph");
	}
}