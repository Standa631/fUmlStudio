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
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.GradleTask;

import net.belehradek.fumlstudio.TemplateEngineHelper;

public class fUmlProject implements IProject {
	
	protected List<ProjectElementAlf> projectElements;
	
	protected File rootFolder;
	protected String name;
	
	public fUmlProject() {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public File getRootFolder() {
		return rootFolder;
	}

	@Override
	public List<IProjectElement> getElements() {
		List<IProjectElement> elems = new ArrayList<>();
		File src = new File(rootFolder, "src");
		
		for (Object fileObj : FileUtils.listFiles(src, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".alf")) {
					elems.add(new ProjectElementAlf(this, file));
				} else if (file.getName().endsWith(".fuml")) {
					elems.add(new ProjectElementFuml(this, file));
				} else if (file.getName().endsWith(".ftl")) {
					elems.add(new ProjectElementFtl(this, file));
				} else {
					System.out.println("Neznamy typ souboru pri parsovani projektu: " + file.getAbsolutePath());
				}
			}
		}
		return elems;
	}

	@Override
	public void createNewProject(File folder) {
		rootFolder = folder;
		name = folder.getName();
		
		//create project directory
		folder.mkdirs();
		
		//run gradle init
    	GradleConnector gradle = GradleConnector.newConnector().forProjectDirectory(folder);
    	ProjectConnection gradleconnection = gradle.connect();
        gradleconnection.newBuild().forTasks("init").setStandardOutput(System.out).run();
        
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
        File emptySrcDir = new File(getClass().getResource("FumlProjectStructure/EmptySrc").getPath());
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
		rootFolder = folder;
		name = folder.getName();
	}

	@Override
	public void addProjectElement(IProjectElement element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
