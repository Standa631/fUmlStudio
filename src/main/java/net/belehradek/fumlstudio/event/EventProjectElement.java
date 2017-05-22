package net.belehradek.fumlstudio.event;

import net.belehradek.fumlstudio.project.IProjectElement;

public class EventProjectElement implements Event {
	public IProjectElement element;
	
	public EventProjectElement(IProjectElement element) {
		this.element = element;
	}

	public IProjectElement getElement() {
		return element;
	}
}
