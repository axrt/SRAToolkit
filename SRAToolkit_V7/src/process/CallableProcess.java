package process;

import java.util.concurrent.Callable;

/**
 * Created by alext on 4/20/14.
 */
public abstract class CallableProcess<T> implements Callable<T>{

    protected final ProcessBuilder processBuilder;

    protected CallableProcess(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }
    public static void print(String toPrint){
        synchronized (CallableProcess.class){
            System.out.println(toPrint);
        }
    }
}