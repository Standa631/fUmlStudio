package net.belehradek.fumlstudio;

public abstract class IdeWorkerTask {
	
	public String label;
	
	public IdeWorkerTask(String label) {
		this.label = label;
	}

	public abstract void work();
}
