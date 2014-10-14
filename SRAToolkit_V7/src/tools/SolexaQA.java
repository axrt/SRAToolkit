package tools;

import process.CallableProcess;

import java.io.File;
import java.util.*;

/**
 * Created by alext on 10/10/14.
 * TODO document class
 */
public abstract class SolexaQA<T extends SolexaQA.SolexaQAResult> extends CallableProcess<List<T>> {

    protected final File exec;
    protected boolean hasRun;
    protected final UUID uuid;
    protected final SolexaQABuilder solexaBuilder;

    protected SolexaQA(SolexaQABuilder builder) {
        super(builder.processBuilder);
        this.exec = builder.exec;
        this.hasRun = false;
        this.uuid = UUID.randomUUID();
        this.solexaBuilder = builder;
    }


    @Override
    public List<T> call() throws Exception {
        if (this.hasRun) throw new IllegalStateException("SolexaQA " + this.uuid.toString() + " has already run!");
        final StringBuilder stringBuilder = new StringBuilder();
        for (String s : this.solexaBuilder.command) {
            stringBuilder.append(s);
            stringBuilder.append(' ');
        }
        synchronized (System.out) {
            System.out.println(stringBuilder);
        }
        this.hasRun=true;
        return new ArrayList<>();
    }

    protected static abstract class SolexaQABuilder {

        protected ProcessBuilder processBuilder;
        protected List<String> command;
        protected final Map<String, String> params;
        protected final File exec;

        protected SolexaQABuilder(File exec) {
            this.exec = exec;
            this.params = new HashMap<>();
        }

        protected List<String> assembleCommand() {
            final List<String> command = new ArrayList<>();
            command.add(this.exec.getPath());
            command.addAll(Arrays.asList(this.params.get("").split(" ")));
            for (Map.Entry<String, String> e : this.params.entrySet()) {
                if (e.getKey().equals("")) {
                    continue;
                }
                command.add(e.getKey());
                command.add(e.getValue());
            }
            return command;
        }

        public abstract SolexaQA build();
    }

    public abstract static class SolexaQAResult {

    }

}
