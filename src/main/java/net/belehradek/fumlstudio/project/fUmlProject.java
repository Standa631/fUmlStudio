package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.dialogs.ProjectLocationSelectionDialog;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;
import net.belehradek.Global;
import net.belehradek.fuml.codegeneration.TemplateEngineHelper;

public class fUmlProject implements IProject {
	
	protected List<IProjectElement> projectElements = new ArrayList<>();
	
	protected File file;
	protected String name;
	
	protected GradleConnector gradle;
	protected ProjectConnection gradleConnection;
	
	protected String unitName = "App";
	
	public fUmlProject() {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IProjectElement> getElements() {
		return projectElements;
	}

	@Override
	public void createNewProject(File folder) {
		file = folder;
		name = folder.getName();
		
		//create project directory
		folder.mkdirs();
		
		//run gradle init
    	gradle = GradleConnector.newConnector().forProjectDirectory(folder);
    	gradleConnection = gradle.connect();
        gradleConnection.newBuild().forTasks("init").setStandardOutput(System.out).run();
        
        //prepsani mym build.gradle a settings.gradle
        File buildTemplate = new File(getClass().getResource("FumlProjectStructure/build.gradle.ftl").getFile());
        Map<String, Object> buildModel = new HashMap<String, Object>();
        buildModel.put("projectName", folder.getName());
        File buildOutput = new File(folder, "build.gradle");
        
        File setttingsTemplate = new File(getClass().getResource("FumlProjectStructure/settings.gradle.ftl").getFile());
        Map<String, Object> settingsModel = new HashMap<String, Object>();
        settingsModel.put("projectName", folder.getName());
        File settingsOutput = new File(folder, "settings.gradle");
        
        try {
			buildOutput.createNewFile();
			TemplateEngineHelper.transform(buildTemplate, buildModel, buildOutput);
			
			buildOutput.createNewFile();
			TemplateEngineHelper.transform(setttingsTemplate, settingsModel, settingsOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//vytvoreni dalsi struktury
        //File emptySrcDir = new File(getClass().getResource("FumlProjectStructure/EmptySrc").getPath());
        File sampleSrcDir = new File(getClass().getResource("FumlProjectStructure/SampleSrc").getPath());
        try {
			FileUtils.copyDirectory(sampleSrcDir, folder);
			System.out.println(sampleSrcDir.getAbsolutePath() + " -> " + folder.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadProject(File folder) {
		file = folder;
		name = folder.getName();
		
		gradle = GradleConnector.newConnector().forProjectDirectory(folder);
    	gradleConnection = gradle.connect();
    	
    	loadElements();
	}
	
	protected void loadElements() {
		projectElements.clear();
		
		File src = new File(file, "src");
		
		ProjectElementGroupDir srcGroup = new ProjectElementGroupDir(src);
		for (Object fileObj : FileUtils.listFiles(src, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".alf")) {
					srcGroup.addProjectElement(new ProjectElementAlf(this, file));
				} else if (file.getName().endsWith(".fuml") || file.getName().endsWith(".uml")) {
					srcGroup.addProjectElement(new ProjectElementFuml(this, file));
				} else if (file.getName().endsWith(".ftl")) {
					srcGroup.addProjectElement(new ProjectElementFtl(this, file));
				} else {
					System.out.println("Neznamy typ souboru pri parsovani projektu: " + file.getAbsolutePath());
				}
			}
		}
		
		//TEST: build folder
		File build = new File(file, "build");
		ProjectElementGroupDir buildGroup = new ProjectElementGroupDir(build);
		for (Object fileObj : FileUtils.listFiles(build, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".uml") || file.getName().endsWith(".fuml")) {
					buildGroup.addProjectElement(new ProjectElementFuml(this, file));
				} else {
					System.out.println("Neznamy typ souboru pri parsovani projektu: " + file.getAbsolutePath());
				}
			}
		}
		
		addProjectElement(srcGroup);
		addProjectElement(buildGroup);
	}

	@Override
	public void addProjectElement(IProjectElement element) {
		projectElements.add(element);
	}

	@Override
	public void build() {
		System.out.println("Project build");
		//ze vsech ALF souboru vytvorit fuml soubory
		//gradleConnection.newBuild().forTasks("hello").setStandardOutput(Global.getLoggerStream()).run();
		
		gradleConnection.newBuild().forTasks("compileFuml").setStandardOutput(Global.getLoggerStream()).setStandardError(Global.getLoggerStream()).run();
		
		gradleConnection.newBuild().forTasks("compileAlf").withArguments("-PunitName=" + unitName).setStandardOutput(Global.getLoggerStream()).setStandardError(Global.getLoggerStream()).run();
	}
	
	@Override
	public void debug() {
		System.out.println("Project debug");
		gradleConnection.newBuild().forTasks("runFuml").withArguments("-PunitName=" + unitName).setStandardOutput(Global.getLoggerStream()).setStandardError(Global.getLoggerStream()).run();
	}
	
	@Override
	public void run() {
		System.out.println("Project run");
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Node getIcon() {
		return AwesomeIcon.PROJECT_FILE.node();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public String getType() {
		return "FumlProject";
	}
}
