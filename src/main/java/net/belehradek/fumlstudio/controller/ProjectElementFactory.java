package net.belehradek.fumlstudio.controller;

import java.io.File;

import net.belehradek.fumlstudio.project.IProject;
import net.belehradek.fumlstudio.project.IProjectElement;

public interface ProjectElementFactory {
	
	public IProjectElement create(IProject project, File file);

}
