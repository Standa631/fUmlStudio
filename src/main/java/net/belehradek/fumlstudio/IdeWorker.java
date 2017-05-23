package net.belehradek.fumlstudio;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class IdeWorker {

	protected Queue<IdeWorkerTask> tasks = new LinkedList<>();

	protected AtomicBoolean running = new AtomicBoolean(false);

	private List<IdeWorkerTaskChangeEvent> onChange = new ArrayList<>();
    public void addOnChangeListener(IdeWorkerTaskChangeEvent l) {
    	onChange.add(l);
    }
    public void change(IdeWorkerTask task, boolean done) {
        for (IdeWorkerTaskChangeEvent l : onChange)
            l.chnageState(task, done);
    }
    
	
	private static IdeWorker instance = null;
	private IdeWorker() {
	}
	public static IdeWorker getInstance() {
	      if(instance == null) {
	         instance = new IdeWorker();
	      }
	      return instance;
	   }

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
				change(t, false);
				t.work();
				change(t, true);
				t = tasks.poll();
			}
		} catch (Exception e) {

		} finally {
			running.set(false);
			change(null, true);
		}
	}
}
