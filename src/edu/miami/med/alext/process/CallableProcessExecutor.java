package edu.miami.med.alext.process;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by alext on 4/20/14.
 */
public abstract class CallableProcessExecutor<T,C extends Callable<T>> {

    protected int numcallables=0;
    protected final List<Future<T>>futures;
    protected final ExecutorService executorService;

    protected CallableProcessExecutor(List<Future<T>> futures, ExecutorService executorService) {
        this.futures = futures;
        this.executorService = executorService;
    }

    public int addProcess(final C callable){
        this.futures.add(this.executorService.submit(callable));
        return ++numcallables;
    }

    public List<Future<T>> getFutures() {
        return futures;
    }
    public void shutdown(){
        this.executorService.shutdown();
    }
}