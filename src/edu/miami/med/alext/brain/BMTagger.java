package edu.miami.med.alext.brain;

import process.CallableProcess;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alext on 4/20/14.
 */
public class BMTagger extends CallableProcess<File> {

    public static final String REFERENCE_BITMASK = "-b";
    public static final String REFERENCE_SPRISM = "-x";
    public static final String FASTQ_PAIRED_END_IND = "-q1";
    public static final String LEFT_MATE = "-1";
    public static final String RIGHT_MATE = "-2";
    public static final String TEMP_DIR = "-T";
    public static final String OUTPUT = "-o";
    public static final String OUTPUT_EXT = ".blacklist";

    private final File rLane;
    private final File lLane;
    private final File referenceBitmask;
    private final File referenceSrprism;

    public BMTagger(ProcessBuilder processBuilder, File rLane, File lLane, File referenceBitmask, File referenceSrprism) {
        super(processBuilder);
        this.rLane = rLane;
        this.lLane = lLane;
        this.referenceBitmask = referenceBitmask;
        this.referenceSrprism = referenceSrprism;
    }

    @Override
    public File call() throws Exception {
        for (String s : this.processBuilder.command()) {
            System.out.print(s.concat(" "));
        }
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
        p.waitFor();
        return new File(this.lLane.getParent(), this.lLane.getName().split("\\.")[0] + OUTPUT_EXT);
    }

    public static CallableProcess<File> newInstance(File bmtaggerExec, File lLane, File rLane, File referenceBitmask, File referenceSrprism, File tmpDir) {

        final String[] processCall = {
                bmtaggerExec.toString(),
                REFERENCE_BITMASK, referenceBitmask.toString(),
                REFERENCE_SPRISM, referenceSrprism.toString(),
                TEMP_DIR, tmpDir.toString(),
                FASTQ_PAIRED_END_IND,
                LEFT_MATE, lLane.toString(),
                RIGHT_MATE, rLane.toString(),
                OUTPUT, new File(rLane.getParent(), rLane.getName().split("\\.")[0] + OUTPUT_EXT).toString()
        };


        return new BMTagger(new ProcessBuilder(processCall), rLane, lLane, referenceBitmask, referenceSrprism);
    }

    public static CallableProcess<File> newInstance(File bmtaggerExec, File lane, File referenceBitmask, File referenceSrprism, File tmpDir) {

        final String[] processCall = {
                bmtaggerExec.toString(),
                REFERENCE_BITMASK, referenceBitmask.toString(),
                REFERENCE_SPRISM, referenceSrprism.toString(),
                TEMP_DIR, tmpDir.toString(),
                FASTQ_PAIRED_END_IND,
                LEFT_MATE, lane.toString(),
                OUTPUT, new File(lane.getParent(), lane.getName().split("\\.")[0] + OUTPUT_EXT).toString()
        };


        return new BMTagger(new ProcessBuilder(processCall), null, lane, referenceBitmask, referenceSrprism);
    }

    public enum RestrictType {
        FastQ("fastq"),
        FastA("fasta");
        private final String name;

        RestrictType(String name) {
            this.name = name;
        }
    }

    public static File restrict(final File input, final File output, final File blacklist, final RestrictType type) throws IOException {
        switch (type) {
            case FastQ: {
                final Set<String> blacklisted = new HashSet<>();
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blacklist))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        blacklisted.add(line.trim());
                    }
                }
                try (
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output));
                ) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        final String ac=line.split(" ")[0].substring(1);
                        if (!blacklisted.contains(ac)) {
                            bufferedWriter.write(line);
                            bufferedWriter.newLine();
                            for (int i = 0; i < 3; i++) {
                                bufferedWriter.write(bufferedReader.readLine());
                                bufferedWriter.newLine();
                            }
                        }else{
                            for(int i=0;i<3;i++){
                                bufferedReader.readLine();
                            }
                        }
                    }
                }
                break;
            }
            case FastA: {
                throw new NotImplementedException();
            }
        }
        return output;
    }
}
