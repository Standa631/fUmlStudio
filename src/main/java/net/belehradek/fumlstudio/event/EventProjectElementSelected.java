package net.belehradek.fumlstudio.event;

import net.belehradek.fumlstudio.project.IProjectElement;

public class EventProjectElementSelected implements Event {
	public IProjectElement element;
	
	public EventProjectElementSelected(IProjectElement element) {
		this.element = element;
	}

	public IProjectElement getElement() {
		return element;
	}
}
