package net.belehradek.fumlstudio.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.modeldriven.alf.syntax.units.RootNamespace;
import org.modeldriven.alf.uml.Package;

import freemarker.template.TemplateException;
import javafx.scene.Node;
import net.belehradek.AwesomeIcon;
import net.belehradek.Global;
import net.belehradek.fuml.codegeneration.TemplateEngine;
import net.belehradek.fuml.codegeneration.TemplateEngineUml;
import net.belehradek.fuml.core.MyAlfMyMapping;

public class fUmlProject implements IProject {

	protected List<IProjectElement> projectElements = new ArrayList<>();

	protected File file;
	protected String name;

	protected GradleConnector gradle;
	protected ProjectConnection gradleConnection;
	
	protected String mainUnitName = "App";
	protected String rootNamespace = "net.belehradek.fumltest.fumltest";
	protected String outPath = "out\\";
	protected String activeTemplatePath = "src\\main\\ftl\\androidRoot.ftl";

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

		// vytvoreni dalsi struktury
		// File(getClass().getResource("FumlProjectStructure/EmptySrc").getPath());
		File sampleSrcDir = new File(getClass().getResource("FumlProjectStructure/SampleSrc").getPath());
		try {
			FileUtils.copyDirectory(sampleSrcDir, folder);
			Global.log(sampleSrcDir.getAbsolutePath() + " -> " + folder.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void loadProject(File folder) {
		file = folder;
		name = folder.getName();
		initGradle();
		loadElements();
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

		ProjectElementGroupDir alfGroup = loadElementsAlf();
		ProjectElementGroupDir ftlGroup = loadElementsFtl();
		ProjectElementGroupDir buildGroup = loadElementsBuild();
		ProjectElementGroupDir outGroup = loadElementsOut();
		ProjectElementGlobalGraph globalGraph = new ProjectElementGlobalGraph(this);
		ProjectElementActivities activities = new ProjectElementActivities(this);

		addProjectElement(activities);
		addProjectElement(globalGraph);
		addProjectElement(alfGroup);
		addProjectElement(ftlGroup);
		addProjectElement(buildGroup);
		addProjectElement(outGroup);
	}

	protected ProjectElementGroupDir loadElementsOut() {
		// out folder
		File out = new File(file, "out");
		ProjectElementGroupDir outGroup = new ProjectElementGroupDir(out);
		for (Object fileObj : FileUtils.listFiles(out, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".java")) {
					outGroup.addProjectElement(new ProjectElementAlf(this, file));
				} else {
//					Global.log("Neznamy typ souboru pri parsovani projektu: " + file.getAbsolutePath());
				}
			}
		}
		return outGroup;
	}

	protected ProjectElementGroupDir loadElementsBuild() {
		// build folder
		File build = new File(file, "build");
		ProjectElementGroupDir buildGroup = new ProjectElementGroupDir(build);
		for (Object fileObj : FileUtils.listFiles(build, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".uml") || file.getName().endsWith(".fuml")) {
					buildGroup.addProjectElement(new ProjectElementFuml(this, file));
				} else {
//					Global.log("Neznamy typ souboru pri parsovani projektu: " + file.getAbsolutePath());
				}
			}
		}
		return buildGroup;
	}

	protected ProjectElementGroupDir loadElementsFtl() {
		// ftl folder
		File ftl = new File(file, "src");
		ProjectElementGroupDir ftlGroup = new ProjectElementGroupDirName(ftl, "ftl");
		for (Object fileObj : FileUtils.listFiles(ftl, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".ftl")) {
					ftlGroup.addProjectElement(new ProjectElementFtl(this, file));
				}
			}
		}
		return ftlGroup;
	}

	protected ProjectElementGroupDir loadElementsAlf() {
		// alf folder
		File alf = new File(file, "src");
		ProjectElementGroupDir alfGroup = new ProjectElementGroupDirName(alf, "alf");
		for (Object fileObj : FileUtils.listFiles(alf, null, true)) {
			if (fileObj instanceof File) {
				File file = (File) fileObj;
				if (file.getName().endsWith(".alf")) {
					alfGroup.addProjectElement(new ProjectElementAlf(this, file));
				}
			}
		}
		return alfGroup;
	}

	@Override
	public void addProjectElement(IProjectElement element) {
		projectElements.add(element);
	}

	@Override
	public void build() {
		Global.log("Project build");
		runGradleTask("compileAlf");
	}

	@Override
	public void debug() {
		Global.log("Project debug");
		runGradleTask("runFuml");
	}

	@Override
	public void run() {
		Global.log("Project run");
		runGradleTask("runFuml");
	}
	
	@Override
	public void generateCode() {
		MyAlfMyMapping alf = new MyAlfMyMapping();
		Package pack = alf.getModel(mainUnitName);
		
		if (pack != null) {
			Global.log(pack);
			
			TemplateEngineUml engine = new TemplateEngineUml(new File(file, outPath));
			try {
				engine.setTemplate(new File(file, activeTemplatePath));
				engine.processPackage(pack, rootNamespace);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TemplateException e) {
				e.printStackTrace();
			}
		}
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
		runGradleTask("cleanAll");
	}
}
