package net.belehradek.fumlstudio.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import net.belehradek.AwesomeIcon;

public class ProjectElementGroupDir implements IProjectElementGroup {

	protected File file;
	protected List<IProjectElement> elements = new ArrayList<>();

	public ProjectElementGroupDir(File file) {
		this.file = file;
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
		return "dir";
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public List<IProjectElement> getElements() {
		return elements;
	}

	@Override
	public List<IProjectElement> getAllElements() {
		List<IProjectElement> out = new ArrayList<>();
		getAllElementsRecurse(out, this);
		return out;
	}

	protected void getAllElementsRecurse(List<IProjectElement> out, IProjectElementGroup group) {
		for (IProjectElement e : group.getElements()) {
			out.add(e);
			if (e instanceof IProjectElementGroup) {
				getAllElementsRecurse(out, (IProjectElementGroup) e);
			}
		}
	}

	@Override
	public void addProjectElement(IProjectElement element) {
		elements.add(element);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public IProject getProject() {
		return null;
	}
}
