package net.belehradek.fumlstudio;

public interface IdeWorkerTaskChangeEvent {
	void chnageState(IdeWorkerTask task, boolean done);
}
