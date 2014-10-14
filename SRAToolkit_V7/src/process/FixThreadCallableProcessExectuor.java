package process;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by alext on 4/20/14.
 */
public class FixThreadCallableProcessExectuor<T,C extends Callable<T>> extends CallableProcessExecutor<T,C> {

    protected FixThreadCallableProcessExectuor(List<Future<T>> futures, ExecutorService executorService) {
        super(futures, executorService);
    }

    public static <T,C extends Callable<T>> CallableProcessExecutor<T,C> newInstance(final int numThreads) {
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        final List<Future<T>> futureList = new ArrayList<Future<T>>();
        return new FixThreadCallableProcessExectuor<T,C>(futureList, executorService);
    }
}
