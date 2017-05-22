package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.modeldriven.alf.syntax.units.RootNamespace;
import org.modeldriven.alf.uml.Package;

import freemarker.template.TemplateException;
import javafx.scene.Node;
import net.belehradek.AwesomeIcon;
import net.belehradek.Global;
import net.belehradek.Lib;
import net.belehradek.fuml.core.MyAlfMapping;
import net.belehradek.fuml.core.TemplateEngine;
import net.belehradek.fumlstudio.controller.ProjectElementFactory;

public class fUmlProject implements IProject {

	protected List<IProjectElement> projectElements = new ArrayList<>();

	protected File file;
	protected String name;

	protected GradleConnector gradle;
	protected ProjectConnection gradleConnection;
	
	protected final String modelPath = "src\\main\\alf";

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
	public List<IProjectElement> getAllElements() {
		List<IProjectElement> out = new ArrayList<>();
		for (IProjectElement e : getElements()) {
			out.add(e);
			if (e instanceof IProjectElementGroup) {
				out.addAll(((IProjectElementGroup)e).getAllElements());
			}
		}
		return out;
	}

	@Override
	public void createNewProject(File folder) {
		file = folder;
		name = folder.getName();
		folder.mkdirs();

		// run gradle init
		initGradle();
		runGradleTask("init");

		generateGradleFiles();
		
		runGradleTask("fUmlInstall");
		runGradleTask("fUmlInit");
	}

	private void generateGradleFiles() {
		// prepsani build.gradle a settings.gradle
		File buildTemplate = new File(getClass().getResource("FumlProjectStructure/build.gradle.ftl").getFile());
		Map<String, Object> buildModel = new HashMap<String, Object>();
		buildModel.put("projectName", file.getName());
		File buildOutput = new File(file, "build.gradle");

		File setttingsTemplate = new File(getClass().getResource("FumlProjectStructure/settings.gradle.ftl").getFile());
		Map<String, Object> settingsModel = new HashMap<String, Object>();
		settingsModel.put("projectName", file.getName());
		File settingsOutput = new File(file, "settings.gradle");

		try {
			buildOutput.createNewFile();
			TemplateEngine.transform(buildTemplate, buildModel, buildOutput);

			buildOutput.createNewFile();
			TemplateEngine.transform(setttingsTemplate, settingsModel, settingsOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean loadProject(File folder) {
		file = folder;
		name = folder.getName();
		
		if (!isProjectFolder())
			return false;
		
		initGradle();
		loadElements();
		return true;
	}
	
	protected boolean isProjectFolder() {
		File f = new File(file, "build.gradle");
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	protected void initGradle() {
		gradle = GradleConnector.newConnector().forProjectDirectory(file);
		gradleConnection = gradle.connect();
	}

	protected void runGradleTask(String task) {
		gradleConnection.newBuild().forTasks(task).setStandardOutput(Global.getLoggerStream())
				.setStandardError(Global.getLoggerStream()).run();
	}

	protected void loadElements() {
		projectElements.clear();

		ProjectElementGroupDir alfGroup = loadProjectFilesTree("alf", "src/main/alf", new String[] {".alf"}, new ProjectElementFactory() {
			@Override
			public IProjectElement create(IProject project, File file) {
				return new ProjectElementAlf(project, file);
			}
		}, false);
		ProjectElementGroupDir ftlGroup = loadProjectFilesTree("ftl", "src/main/ftl", new String[] {".ftl"}, new ProjectElementFactory() {
			@Override
			public IProjectElement create(IProject project, File file) {
				return new ProjectElementFtl(project, file);
			}
		}, false);
//		ProjectElementGroupDir buildGroup = loadProjectFilesTree("build", "build", new String[] {".uml", ".fuml"}, new ProjectElementFactory() {
//			@Override
//			public IProjectElement create(IProject project, File file) {
//				return new ProjectElementFuml(project, file);
//			}
//		}, false);
		ProjectElementGroupDir outGroup = loadProjectFilesTree("out", "out", null, new ProjectElementFactory() {
			@Override
			public IProjectElement create(IProject project, File file) {
				return new ProjectElementText(project, file);
			}
		}, false);
		
		File f = new File(file, "src\\main\\alf\\App.alf");
		if (f.exists()) {
			ProjectElementGlobalGraph globalGraph = new ProjectElementGlobalGraph(this);
			ProjectElementActivities activities = new ProjectElementActivities(this);
			addProjectElement(activities);
			addProjectElement(globalGraph);
		}

		addProjectElement(alfGroup);
		addProjectElement(ftlGroup);
//		addProjectElement(buildGroup);
		addProjectElement(outGroup);
	}

	protected ProjectElementGroupDir loadProjectFilesTree(String name, String src, String[] ext, ProjectElementFactory factory, boolean flat) {
		File alf = new File(file, src);
		if (!alf.exists()) return null;
			
		ProjectElementGroupDir alfGroup = new ProjectElementGroupDirName(alf, name);
		
		if (flat) {
			for (Object fileObj : FileUtils.listFiles(alf, null, true)) {
				if (fileObj instanceof File) {
					File file = (File) fileObj;
					if (ext == null || Lib.endsWith(file.getName(), ext)) {
						alfGroup.addProjectElement(factory.create(this, file));
					}
				}
			}
		} else {			
			loadProjectFilesTree(alfGroup, alf, ext, factory);
		}
		
		return alfGroup;
	}
	
	protected void loadProjectFilesTree(ProjectElementGroupDir target, File folder, String[] ext, ProjectElementFactory factory) {
		if (folder == null || !folder.isDirectory())
			return;
		
		for (File f : folder.listFiles()) { 
			if (f.isDirectory()) {
				ProjectElementGroupDir pi = new ProjectElementGroupDirName(f, f.getName());
				target.addProjectElement(pi);
				loadProjectFilesTree(pi, f, ext, factory);
			} else if (ext == null || Lib.endsWith(f.getName(), ext)) {
				target.addProjectElement(factory.create(this, f));
			}
		}
	}
	
	public Package loadModel() {
		MyAlfMapping alf = new MyAlfMapping(file.getAbsolutePath() + "\\" + modelPath, file.getAbsolutePath() + "\\" + getGradleSettingsLibPath());
		return alf.getModel(getGradleSettingsUnitName());
	}

	@Override
	public void addProjectElement(IProjectElement element) {
		projectElements.add(element);
	}

	@Override
	public void build() {
		Global.log("Project build");
		runGradleTask("fUmlCompile");
	}

	@Override
	public void debug() {
		Global.log("Project debug");
		runGradleTask("fUmlRunAlf");
	}

	@Override
	public void run() {
		Global.log("Project run");
		runGradleTask("fUmlRun");
	}
	
	@Override
	public void generateCode() {
		Global.log("Generate code");
		runGradleTask("fUmlCodeGenerate");
	}
	
	public String getGradleSettingsProperty(String name) {
		String out = "";
		try {
			File f = new File(file, "settings.gradle");
			String s = new String(Files.readAllBytes(f.toPath()));
			s = s.substring(s.indexOf(name) + name.length());
			
			Pattern pattern = Pattern.compile("[\\s=]*['\"]([^'\"]*)");
			Matcher matcher = pattern.matcher(s);
			if (matcher.find())
			{
			    out = matcher.group(1);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public void setGradleSettingsProperty(String name, String value) {
		String out = "";
		try {
			File f = new File(file, "settings.gradle");
			String s = new String(Files.readAllBytes(f.toPath()));
			
			Pattern pattern = Pattern.compile("(" + name + "[\\s=]*['\"])([^'\"]*)");
			Matcher matcher = pattern.matcher(s);
			if (matcher.find())
			{
				s = matcher.replaceFirst("$1" + value);
			}
			
			FileUtils.writeStringToFile(f, s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getGradleSettingsUnitName() {
		return getGradleSettingsProperty("gradle.ext.FumlSettingsUnitName");
	}
	
	public void setGradleSettingsUnitName(String unit) {
		setGradleSettingsProperty("gradle.ext.FumlSettingsUnitName", unit);
	}
	
	public String getGradleSettingsTransform() {
		return getGradleSettingsProperty("gradle.ext.FumlSettingsTransformationFile");
	}
	
	public void setGradleSettingsTransform(String trans) {
		setGradleSettingsProperty("gradle.ext.FumlSettingsTransformationFile", trans);
	}
	
	public String getGradleSettingsNamespace() {
		return getGradleSettingsProperty("gradle.ext.FumlSettingsNamespacePrefix");
	}
	
	public void setGradleSettingsNamespace(String trans) {
		setGradleSettingsProperty("gradle.ext.FumlSettingsNamespacePrefix", trans);
	}
	
	public String getGradleSettingsLibPath() {
		return getGradleSettingsProperty("gradle.ext.FumlSettingsLibPath");
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

	@Override
	public void clean() {
		Global.log("Project clean");
		runGradleTask("fUmlClean");
	}

	@Override
	public IProject getProject() {
		return this;
	}
}
