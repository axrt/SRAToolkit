package tools;


import org.apache.commons.math3.util.Pair;
import process.CallableProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alext on 4/22/14.
 */
public class Trinity extends CallableProcess<File> {

    public static final String TRINITY = "trinity";
    public static final String JVM_MEMORY = "--JM";
    public static final String LEFT = "--left";
    public static final String RIGHT = "--right";
    public static final String SINGLE = "--single";
    public static final String MIN_CONTIG_LENGTH = "--min_contig_length";
    public static final String PROCESSORS = "--CPU";
    public static final String BFLY_MAX_HEAP = "--bflyHeapSpaceMax";
    public static final String BFLY_CPU = "--bflyCPU";
    public static final String OUTPUT_DIR = "--output";
    public static final String OUTPUT_DIR_DEFAULT = "trinity_out_dir";
    public static final String OUTPUT_FILE = "Trinity.fasta";
    private File rLane;
    private File lLane;

    protected Trinity(ProcessBuilder processBuilder, File lLane, File rLane) {
        super(processBuilder);
        this.rLane = rLane;
        this.lLane = lLane;
        this.processBuilder.directory(lLane.getParentFile());
    }

    protected Trinity(TrinityBuilder builder) {
        super(builder.processBuilder);
        this.processBuilder.directory(builder.workingDir.toFile());
        if (builder.left != null) {
            this.lLane = new File(builder.left.getValue());
        } else {
            this.lLane = new File(builder.single.getValue());
        }
    }

    public static CallableProcess<File> newInstance(final File trinityExec, final File lLane, final File rLane,
                                                    final LIB_TYPE libType, final int numThreads, final int minContigLenghth,
                                                    final SEQ_TYPE seqType, final String jmMemory) {
        final String[] processCall = {
                trinityExec.toString(),
                SEQ_TYPE.SEQTYPE, seqType.getName(),
                LEFT, lLane.toString(),
                RIGHT, rLane.toString(),
                JVM_MEMORY, jmMemory,
                LIB_TYPE.LIBTYPE, libType.getName(),
                PROCESSORS, String.valueOf(numThreads),
                MIN_CONTIG_LENGTH, String.valueOf(minContigLenghth),
                BFLY_MAX_HEAP, jmMemory,
                BFLY_CPU, String.valueOf(numThreads)
        };
        return new Trinity(new ProcessBuilder(processCall), lLane, rLane);

    }

    public static CallableProcess<File> newInstance(final File trinityExec, final File lane,
                                                    LIB_TYPE libType, final int numThreads, final int minContigLength,
                                                    final SEQ_TYPE seqType, final String jmMemory) {

        final String[] processCall = {
                trinityExec.toString(),
                SEQ_TYPE.SEQTYPE, seqType.getName(),
                SINGLE, lane.toString(),
                JVM_MEMORY, jmMemory,
                LIB_TYPE.LIBTYPE, libType.getName(),
                PROCESSORS, String.valueOf(numThreads),
                MIN_CONTIG_LENGTH, String.valueOf(minContigLength),
                BFLY_MAX_HEAP, jmMemory,
                BFLY_CPU, String.valueOf(numThreads)
        };
        return new Trinity(new ProcessBuilder(processCall), lane, null);
    }

    @Override
    public File call() throws Exception {
        for (String s : this.processBuilder.command()) {
            System.out.print(s.concat(" "));
        }
        if (!new File(new File(this.lLane.getParent(), OUTPUT_DIR_DEFAULT), OUTPUT_FILE).exists()) {
            System.out.println();
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

        }
        return new File(new File(this.lLane.getParent(), OUTPUT_DIR_DEFAULT), OUTPUT_FILE);
    }

    public static enum SEQ_TYPE {
        FQ("fq"), FA("fa");
        public static final String SEQTYPE = "--seqType";
        private final String name;

        private SEQ_TYPE(String name) {
            this.name = name;
        }

        public static SEQ_TYPE convertName(String name) {
            switch (name) {
                case "FastQ":
                    return FQ;
                case "FastA":
                    return FA;
            }
            throw new IllegalArgumentException("No such alias \"".concat(name).concat("\""));
        }

        public String getName() {
            return name;
        }
    }

    public static enum LIB_TYPE {
        RF("RF"), FR("FR"), R("R"), F("F");
        public static final String LIBTYPE = "--SS_lib_type";
        private final String name;

        private LIB_TYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class TrinityBuilder {

        protected final List<String> processBullet;
        protected final File trinityExec;
        protected final SEQ_TYPE seqType;
        protected final LIB_TYPE libType;
        protected final String jm;
        protected final List<Pair<String, String>> optionalPairs;
        protected ProcessBuilder processBuilder;
        protected Pair<String, String> left;
        protected Pair<String, String> right;
        protected Pair<String, String> single;
        protected Pair<String, String> minContigLength;
        protected Pair<String, String> numCPU;
        protected Pair<String, String> numBflyCPU;
        protected Pair<String, String> bflyMaxHeap;
        protected Pair<String, String> outputDir;
        protected Pair<String, String> outputFile;
        protected Path workingDir;

        public TrinityBuilder(File trinityExec, SEQ_TYPE seqType, LIB_TYPE libType, String jm) {
            this.processBullet = new ArrayList<>();
            this.processBullet.add(trinityExec.getPath());
            this.trinityExec = trinityExec;
            this.seqType = seqType;
            this.processBullet.add(SEQ_TYPE.SEQTYPE);
            this.processBullet.add(seqType.toString());
            this.libType = libType;
            this.processBullet.add(LIB_TYPE.LIBTYPE);
            this.processBullet.add(libType.toString());
            this.jm = jm;
            this.processBullet.add(JVM_MEMORY);
            this.processBullet.add(jm);
            this.optionalPairs= new ArrayList<>();

        }

        public TrinityBuilder single(String single) {
            if (this.left != null || this.right != null) {
                throw new IllegalArgumentException(SINGLE + " can not be used in conjunction with" + LEFT + " or " + RIGHT);
            }
            this.single = new Pair<>(SINGLE, single);
            this.optionalPairs.add(this.single);
            this.workingDir= Paths.get(single).getParent();
            return this;
        }

        public TrinityBuilder right(String right) {
            if (this.left != null || this.single != null) {
                throw new IllegalArgumentException(RIGHT + " can not be used in conjunction with" + SINGLE + " and without " + LEFT);
            }
            this.right = new Pair<>(RIGHT, right);
            this.optionalPairs.add(this.right);
            return this;
        }

        public TrinityBuilder left(String left) {
            if (this.single != null) {
                throw new IllegalArgumentException(LEFT + " can not be used in conjunction with " + SINGLE);
            }
            this.left = new Pair<>(LEFT, left);
            this.optionalPairs.add(this.left);
            this.workingDir= Paths.get(left).getParent();
            return this;
        }

        public TrinityBuilder minCongtigLength(int minLenght) {
            this.minContigLength = new Pair<>(MIN_CONTIG_LENGTH,String.valueOf(minLenght));
            this.optionalPairs.add(this.minContigLength);
            return this;
        }

        public TrinityBuilder numCPU(int numCPU) {
            this.numCPU = new Pair<>(PROCESSORS, String.valueOf(numCPU));
            this.optionalPairs.add(this.numCPU);
            return this;
        }

        public TrinityBuilder bflyCPU(int numBflyCPU) {
            this.numBflyCPU = new Pair<>(BFLY_CPU, String.valueOf(numBflyCPU));
            this.optionalPairs.add(this.numBflyCPU);
            return this;
        }

        public TrinityBuilder bflyMaxHeap(String bflyMaxHeap) {
            this.bflyMaxHeap = new Pair<>(BFLY_MAX_HEAP, bflyMaxHeap);
            this.optionalPairs.add(this.bflyMaxHeap);
            return this;
        }

        public TrinityBuilder outputDir(Path outputDir) {
            this.outputDir = new Pair<>(OUTPUT_DIR, outputDir.toFile().getPath());
            this.optionalPairs.add(this.outputDir);
            return this;
        }

        public TrinityBuilder outputFile(File outputFile) {
            this.outputFile = new Pair<>(OUTPUT_FILE, outputFile.getPath());
            this.optionalPairs.add(this.outputFile);
            return this;
        }

        public Trinity build() {
            if(this.left==null&&this.single==null){
                throw new IllegalStateException("At least "+LEFT+" or "+ SINGLE+" needed to specify the command!");
            }
            for (Pair<String, String> p : this.optionalPairs) {
                if (p != null) {
                    this.processBullet.add(p.getKey());
                    this.processBullet.add(p.getValue());
                }
            }
            this.processBuilder=new ProcessBuilder(this.processBullet);
            return new Trinity(this);
        }
    }
}
