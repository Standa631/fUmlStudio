package net.belehradek.fumlstudio;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class IdeWorker {

	protected Queue<IdeWorkerTask> tasks = new LinkedList<>();

	protected AtomicBoolean running = new AtomicBoolean(false);

	public IdeWorker() {
		running.set(false);
	}

	public abstract void beforeStart(IdeWorkerTask task);

	public abstract void afterDone(IdeWorkerTask task);

	public void addTask(IdeWorkerTask task) {
		tasks.add(task);
		if (running.compareAndSet(false, true)) {
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					work();
				}
			});
		}
	}

	public void work() {
		try {
			IdeWorkerTask t = tasks.poll();
			while (t != null) {
				beforeStart(t);
				t.work();
				afterDone(t);
				t = tasks.poll();
			}
		} catch (Exception e) {

		} finally {
			running.set(false);
			afterDone(null);
		}
	}
}
