package net.belehradek.fumlstudio.project;

import java.io.File;

public class ProjectElementGroupDirName extends ProjectElementGroupDir {

	protected String name;
	
	public ProjectElementGroupDirName(File file, String name) {
		super(file);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
