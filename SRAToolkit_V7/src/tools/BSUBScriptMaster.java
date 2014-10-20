package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alext on 10/16/14.
 * TODO document class
 */
public class BSUBScriptMaster {

    public final static String HEADER = "#!/bin/bash";
    public final static String EXT = ".drv";
    public final static String END = "end";
    public final static String MASTER_EXT = ".master";
    public final static String BSUB_C = "bsub <";

    protected final String name;
    protected final String task;
    protected final int hours;
    protected final String queue;
    protected final int cores;
    protected final boolean emailOnStart;
    protected final boolean emailOnFinish;
    protected final String email;
    protected final String command;
    protected final int fullLength;
    protected final int stepping;
    protected final List<BSUBScript> scripts;
    protected final Path dir;

    public BSUBScriptMaster(String name, String task, int hours,
                            String queue, int cores,
                            boolean emailOnStart, boolean emailOnFinish,
                            String email, String command,
                            int fullLength, int stepping, Path dir) {
        this.name = name;
        this.task = task;
        this.hours = hours;
        this.queue = queue;
        this.cores = cores;
        this.emailOnStart = emailOnStart;
        this.emailOnFinish = emailOnFinish;
        this.email = email;
        this.command = command;
        this.fullLength = fullLength;
        this.stepping = stepping;
        this.dir = dir;
        this.scripts = new ArrayList<>();

        for (int i = 0; i < this.fullLength; i += this.stepping) {
            int to = i + this.stepping - 1;
            if (to >= fullLength) {
                to = -1;
            }
            this.scripts.add(new BSUBScript(this.name, this.task, this.hours, this.queue, this.cores, this.emailOnStart, this.emailOnFinish, this.email, this.command, i, to));
        }
    }

    public void generate() throws IOException {

        int i = 0;
        try (BufferedWriter masterWriter = new BufferedWriter(new FileWriter(this.dir.resolve(this.name + '_' + this.task + MASTER_EXT).toFile()))) {
            masterWriter.write(HEADER);
            masterWriter.newLine();
            masterWriter.newLine();

            for (BSUBScript bs : this.scripts) {
                int to = i + this.stepping - 1;
                final File outFile;
                if (to < fullLength) {
                    outFile = this.dir.resolve(this.name + '_' + this.task + '_' + i + '_' + (i + stepping) + EXT).toFile();
                } else {
                    outFile = this.dir.resolve(this.name + '_' + this.task + '_' + i + '_' + END + EXT).toFile();
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile))) {
                    bufferedWriter.write(bs.toString());
                }
                i += this.stepping;
                masterWriter.write(BSUB_C);
                masterWriter.write(' ');
                masterWriter.write(outFile.getPath());
                masterWriter.newLine();
            }
        }
    }

    public static class BSUBScript {

        public final static String BSUB = "#BSUB";
        public final static String NAME = "-J";
        public final static String OUT = "-o";
        public final static String OUT_EXT = ".out";
        public final static String ERR = "-e";
        public final static String ERR_EXT = ".err";
        public final static String WALLCLOCK = "-W";
        public final static String QUEUE = "-q";
        public final static String CORES = "-o";
        public final static String EMAIL_ON_START = "-B";
        public final static String EMAIL_ON_FINISH = "-N";
        public final static String EMAIL = "-u";

        public final static String CORES_C = "-c";
        public final static String FROM = "-from";
        public final static String TO = "-to";

        protected final String name;
        protected final int hours;
        protected final String queue;
        protected final int cores;
        protected final boolean emailOnStart;
        protected final boolean emailOnFinish;
        protected final String email;
        protected final String command;
        protected final int from;
        protected final int to;

        public BSUBScript(String name, String task, int hours, String queue, int cores, boolean emailOnStart, boolean emailOnFinish, String email, String command, int from, int to) {

            this.hours = hours;
            this.queue = queue;
            this.cores = cores;
            this.emailOnStart = emailOnStart;
            this.emailOnFinish = emailOnFinish;
            this.email = email;
            this.command = command;
            this.from = from;
            this.to = to;

            final StringBuilder stringBuilder = new StringBuilder(name);
            stringBuilder.append('_');
            stringBuilder.append(task);
            stringBuilder.append('_');
            stringBuilder.append(this.from);
            stringBuilder.append('_');
            if (this.to > -1) {
                stringBuilder.append(this.to);
            } else {
                stringBuilder.append(END);
            }
            this.name = stringBuilder.toString();
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(HEADER);
            stringBuilder.append('\n');
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(NAME);
            stringBuilder.append(' ');
            stringBuilder.append(this.name);
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(OUT);
            stringBuilder.append(' ');
            stringBuilder.append(this.name);
            stringBuilder.append(OUT_EXT);
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(ERR);
            stringBuilder.append(' ');
            stringBuilder.append(this.name);
            stringBuilder.append(ERR_EXT);
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(WALLCLOCK);
            stringBuilder.append(' ');
            stringBuilder.append(this.hours);
            stringBuilder.append(":00");
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(QUEUE);
            stringBuilder.append(' ');
            stringBuilder.append(this.queue);
            stringBuilder.append('\n');

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(CORES);
            stringBuilder.append(' ');
            stringBuilder.append(this.cores);
            stringBuilder.append('\n');

            if (this.emailOnStart) {
                stringBuilder.append(BSUB);
                stringBuilder.append(' ');
                stringBuilder.append(EMAIL_ON_START);
                stringBuilder.append('\n');
            }

            if (this.emailOnFinish) {
                stringBuilder.append(BSUB);
                stringBuilder.append(' ');
                stringBuilder.append(EMAIL_ON_FINISH);
                stringBuilder.append('\n');
            }

            stringBuilder.append(BSUB);
            stringBuilder.append(' ');
            stringBuilder.append(EMAIL);
            stringBuilder.append(' ');
            stringBuilder.append(this.email);
            stringBuilder.append("\n#\n");

            stringBuilder.append(this.command);
            stringBuilder.append(' ');
            stringBuilder.append(CORES_C);
            stringBuilder.append(' ');
            stringBuilder.append(String.valueOf(this.cores));
            stringBuilder.append(' ');
            stringBuilder.append(FROM);
            stringBuilder.append(' ');
            stringBuilder.append(String.valueOf(this.from));
            stringBuilder.append(' ');
            stringBuilder.append(TO);
            stringBuilder.append(' ');
            if (this.to > -1) {
                stringBuilder.append(String.valueOf(this.to));
            }
            stringBuilder.append('\n');
            return stringBuilder.toString();
        }
    }
}
