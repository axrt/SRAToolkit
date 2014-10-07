package edu.miami.med.alext.module;

import edu.miami.med.alext.process.CallableProcess;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by alext on 4/20/14.
 */
public class FastqDump extends CallableProcess<File[]> {

    public static final String SPLIT_FILES= "--split-files";

    protected final File inputFile;

    protected FastqDump(ProcessBuilder processBuilder, File inputFile) {
        super(processBuilder);
        this.inputFile = inputFile;
        this.processBuilder.directory(this.inputFile.getParentFile());
    }

    @Override
    public File[] call() throws Exception {
        System.out.println("Processing: "+this.inputFile);
        //TEMP TODO REMOVE
        if(!new File(this.inputFile.getParent(),this.inputFile.getName().split("\\.")[0]+"_1.fastq").exists()) {
            final Process p = this.processBuilder.start();
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(p.getErrorStream()))){
                bufferedReader.lines().forEach(line->{

                    System.out.print("Error: "+this.inputFile.toString());
                    System.out.print("Error: "+line);
                });
            }
            p.waitFor();
        }else{
            System.out.println("Has already been processed: "+this.inputFile);
        }
        final File[]outputFiles=new File[]{new File(this.inputFile.getParent(),this.inputFile.getName().split("\\.")[0]+"_1.fastq"),
                new File(this.inputFile.getParent(),this.inputFile.getName().split("\\.")[0]+"_2.fastq")};
        return outputFiles;
    }

    public static CallableProcess<File[]> newInstance(File fastqDumpExec,File inputFile){
        final String[]processCall= {fastqDumpExec.toString(),SPLIT_FILES,inputFile.toString()};
        return new FastqDump(new ProcessBuilder(processCall),inputFile);
    }
}
