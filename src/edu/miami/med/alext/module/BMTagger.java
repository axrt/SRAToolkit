package edu.miami.med.alext.module;


import edu.miami.med.alext.process.CallableProcess;

import java.io.*;
import java.util.*;

/**
 * Created by alext on 4/20/14.
 */
public class BMTagger extends CallableProcess<File> {

    public static final String REFERENCE_BITMASK = "-b";
    public static final String REFERENCE_SPRISM = "-x";
    public static final String FASTQ = "-q1";
    public static final String FASTA = "-q0";
    public static final String LEFT_MATE = "-1";
    public static final String RIGHT_MATE = "-2";
    public static final String TEMP_DIR = "-T";
    public static final String OUTPUT = "-o";
    public static final String OUTPUT_EXT = ".blacklist";

    public enum RestrictType {

        FastQ("fastq"),
        FastA("fasta");

        private final String name;

        RestrictType(String name) {
            this.name = name;
        }

        public static String toBMTaggerCommand(RestrictType restrictType) {
            switch (restrictType) {
                case FastA:
                    return FASTA;
                default:
                    return FASTQ;
            }
        }
    }

    protected final File rLane;
    protected final File lLane;
    protected final File referenceBitmask;
    protected final File referenceSrprism;
    protected File blacklist;

    protected BMTagger(ProcessBuilder processBuilder, File lLane, File rLane, File referenceBitmask, File referenceSrprism, File blacklist) {
        super(processBuilder);
        this.rLane = rLane;
        this.lLane = lLane;
        this.referenceBitmask = referenceBitmask;
        this.referenceSrprism = referenceSrprism;
        this.blacklist = blacklist;
    }

    public File getrLane() {
        return rLane;
    }

    public File getlLane() {
        return lLane;
    }

    public File getReferenceBitmask() {
        return referenceBitmask;
    }

    public File getReferenceSrprism() {
        return referenceSrprism;
    }

    public synchronized Optional<File> getBlacklist() {
        final Optional<File> file = Optional.of(new File(this.blacklist.toString()));
        if(this.blacklist.exists()){
            return file;
        }
        else return Optional.empty();
    }

    @Override
    public File call() throws Exception {

        final StringBuilder stringBuilder=new StringBuilder();
        for (String s : this.processBuilder.command()) {
            stringBuilder.append(s.concat(" "));
        }
        stringBuilder.append('\n');
        print(stringBuilder.toString());    //TODO lambdify

        try {
            if (!new File(this.lLane.getParent(), this.lLane.getName().split("\\.")[0] + OUTPUT_EXT).exists()) {

                final Process p = this.processBuilder.start();


                try (InputStream inputStream = p.getErrorStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println("ERR::" + this.lLane.getName() + " >" + line);
                    }
                }
                try (InputStream inputStream = p.getInputStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println("OUT::" + this.lLane.getName() + " >" + line);
                    }
                }
                p.waitFor(); //TODO is this necessary?

            } else {//TODO remove
                print("Reads for " + this.lLane + " have already been filtered against the reference.");
            }
        } catch (Exception e) {
            this.blacklist = null;
            throw e;
        }

        return this.blacklist;
    }

    public synchronized void removePreviousOutput() {
        if (this.blacklist != null && this.blacklist.exists()) {
            this.blacklist.delete();
        }
            this.blacklist = null;
    } //TODO opt to make this check for whether the thread has finished

    public static CallableProcess<File> newInstance(BMTaggerBuilder builder) {

        final List<String> processCall = new ArrayList<>();
        processCall.add(builder.bmtaggerExec.toString());
        processCall.add(REFERENCE_BITMASK);
        processCall.add(builder.referenceBitmask.toString());
        processCall.add(REFERENCE_SPRISM);
        processCall.add(builder.referenceSrprism.toString());
        processCall.add(TEMP_DIR);
        processCall.add(builder.tmpDir.toString());
        processCall.add(builder.restrictType.toString());
        processCall.add(LEFT_MATE);
        processCall.add(builder.lLane.toString());
        if (builder.rLane != null) {
            processCall.add(RIGHT_MATE);
            processCall.add(builder.rLane.toString());
        }
        processCall.add(OUTPUT);
        if (builder.output != null) {
            processCall.add(builder.output.toString());
        } else {
            processCall.add(new File(builder.lLane.getParent(), builder.lLane.getName().split("\\.")[0] + OUTPUT_EXT).toString());
        }

        return new BMTagger(new ProcessBuilder(processCall), builder.lLane, builder.rLane, builder.referenceBitmask, builder.referenceSrprism,builder.output);
    }


    public static File restrict(final File input, final File output, final File blacklist, final RestrictType type) throws IOException {
        if (output.exists()) {
            return output;
        }
        final Set<String> blacklisted = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blacklist))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                blacklisted.add(line.trim());
            }
        }
        switch (type) {
            case FastQ: {
                try (
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output));
                ) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        final String ac = line.split(" ")[0].substring(1);
                        if (!blacklisted.contains(ac)) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                            for (int i = 0; i < 3; i++) {
                                bufferedWriter.write(bufferedReader.readLine());
                                bufferedWriter.newLine();
                            }
                        } else {
                            for (int i = 0; i < 3; i++) {
                                bufferedReader.readLine();
                            }
                        }
                    }
                }
                break;
            }
            case FastA: {
                try (
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output));
                ) {
                    String line;
                    boolean write = false;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith(">")) {
                            final String ac = line.split(" ")[0].substring(1);
                            if (!blacklisted.contains(ac)) {
                                write = true;
                            } else {
                                write = false;
                            }
                        }
                        if (write) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                        }
                    }
                }
                break;
            }
        }
        return output;
    }
    //TODO implement strategy
    public static class BMTaggerBuilder {
        //Required
        private File bmtaggerExec = null;
        private File lLane = null;
        private File referenceBitmask = null;
        private File referenceSrprism = null;
        private File tmpDir = null;
        private String restrictType = null;
        //Optional
        private File rLane = null;
        private File output = null;

        //No explicit constructor

        public BMTaggerBuilder bmtaggerExecutale(File bmtaggerExec) {
            this.bmtaggerExec = bmtaggerExec;
            return this;
        }

        public BMTaggerBuilder rLane(File rLane) {
            this.rLane = rLane;
            return this;
        }

        public BMTaggerBuilder referenceBitmask(File referenceBitmask) {
            this.referenceBitmask = referenceBitmask;
            return this;
        }

        public BMTaggerBuilder referenceSrprism(File referenceSrprism) {
            this.referenceSrprism = referenceSrprism;
            return this;
        }

        public BMTaggerBuilder tmpDir(File tmpDir) {
            this.tmpDir = tmpDir;
            return this;
        }

        public BMTaggerBuilder lLane(File lLane) {
            this.lLane = lLane;
            return this;
        }

        public BMTaggerBuilder restrictType(RestrictType restrictType) {
            this.restrictType = BMTagger.RestrictType.toBMTaggerCommand(restrictType);
            return this;
        }

        public BMTaggerBuilder outputFile(File output) {
            this.output = output;
            return this;
        }

        public BMTagger build() {
            if (this.bmtaggerExec != null
                    && this.lLane != null
                    && this.referenceBitmask != null
                    && this.referenceSrprism != null
                    && this.tmpDir != null
                    && this.restrictType != null) {
                if(this.output==null){
                   this.output=lLane.toPath().resolveSibling(lLane.getName().substring(0,lLane.getName().indexOf("."))+".blacklist").toFile();
                }
                return (BMTagger)BMTagger.newInstance(this);
            }
            throw new IllegalStateException("Please provide path to: bmtagger execuatble, " +
                    "right lane file, reference bitmask path, sprism path, a path to a temporary directory.");
        }
    }
}
