package edu.miami.med.alext.eye;

import edu.miami.med.alext.module.BMTagger;
import edu.miami.med.alext.module.FastqDump;
import edu.miami.med.alext.brain.Trinity;
import edu.miami.med.alext.ncbi.xml.jaxb.EXPERIMENTPACKAGESET;
import edu.miami.med.alext.ncbi.xml.jaxb.ExperimentPackageType;
import edu.miami.med.alext.ncbi.xml.jaxb.SRAXMLLoader;
import edu.miami.med.alext.net.DownloadSRA;
import edu.miami.med.alext.process.CallableProcessExecutor;
import edu.miami.med.alext.process.FixThreadCallableProcessExectuor;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by alext on 8/18/14.
 * TODO document class
 */
public class EyePipeline {

    public static void main(String[]args){


        final File driverXML = new File("/home/alext/Documents/ocular_rnaseq/sequences/eye.xml");
        final File mainFolder = new File("/home/alext/Documents/ocular_rnaseq/sequences/eye/");

        try (InputStream inputStream = new FileInputStream(driverXML)) {
            //1. Download all the SRA files
            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for eye: " + sraNames.size());
            //Create a list of subfolders
            final List<File> subFolders = sraNames.stream().map(sraName -> new File(mainFolder, sraName)).collect(Collectors.toList());

            //For each folder - download the file and retain
            final List<File> sraFiles = sraNames.stream().map(sraName -> {
                try {
                    System.out.println(sraName + " is being downloaded");
                    return DownloadSRA.downloadSRAToANewFolder(sraName, mainFolder);
                } catch (IOException e) {
                    return null;
                }
            }).collect(Collectors.toList());

            //2.Decompress all the SRAs
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            final File fastqDumpExec = new File("/usr/local/bin/fastq-dump.2.3.4");
            final List<Future<File[]>> fastqFutures = sraFiles.stream().map(
                    sraFileToDecompress -> executorService.submit(
                            FastqDump.newInstance(
                                    fastqDumpExec, sraFileToDecompress
                            )
                    )
            ).collect(Collectors.toList());
            final List<File[]> fastqFiles = fastqFutures
                    .stream().map(fastqFuture -> {
                        try {
                            return fastqFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());
            executorService.shutdown();

            //3.Filter all the files with BMTagger in 7 threads (max that system allows due to RAM)
            final File bmTaggerExec = new File("/usr/local/bin/bmtagger.sh");
            final File bitmask = new File("/home/alext/NCBI/reference/grch38/grch38.bitmask");
            final File sRPrism = new File("/home/alext/NCBI/reference/grch38/grch38.srprism");
            final File tmpDir = new File("/home/alext/Downloads/tmp");
            CallableProcessExecutor<File, Callable<File>> fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(6);
            for (int i = 0; i < fastqFiles.size(); i++) {
                //If the right file exists do paiwise
                if (fastqFiles.get(i)[1].exists()) {
                    fileCallableProcessExecutor.addProcess(
                            new BMTagger.BMTaggerBuilder()
                                    .bmtaggerExecutale(bmTaggerExec)
                                    .lLane(fastqFiles.get(i)[0])
                                    .rLane(fastqFiles.get(i)[1])
                                    .referenceBitmask(bitmask)
                                    .referenceSrprism(sRPrism)
                                    .tmpDir(tmpDir)
                                    .restrictType(BMTagger.RestrictType.FastQ)
                                    .build());
                } else {
                    fileCallableProcessExecutor.addProcess(
                            new BMTagger.BMTaggerBuilder()
                                    .bmtaggerExecutale(bmTaggerExec)
                                    .lLane(fastqFiles.get(i)[0])
                                    .referenceBitmask(bitmask)
                                    .referenceSrprism(sRPrism)
                                    .restrictType(BMTagger.RestrictType.FastQ)
                                    .tmpDir(tmpDir).build()
                    );
                }

            }
            final List<File> blacklistFiles = fileCallableProcessExecutor.getFutures().stream().map(
                    future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            ).collect(Collectors.toList());
            fileCallableProcessExecutor.shutdown();

            //4. Restrict all files to their corresponding blacklists
            final List<File[]> restrictedFastqFiles = new ArrayList<>();
            for (int i = 0; i < fastqFiles.size(); i++) {
                final File[] fastqPair = fastqFiles.get(i);

                final File left = BMTagger.restrict(fastqPair[0], new File(fastqPair[0].getParent(), fastqPair[0].getName().replaceAll("\\.fastq", ".rest.fastq")), blacklistFiles.get(i), BMTagger.RestrictType.FastQ);

                if (fastqPair[1].exists()) {
                    restrictedFastqFiles.add(
                            new File[]{
                                    left,
                                    BMTagger.restrict(fastqPair[1], new File(fastqPair[1].getParent(), fastqPair[1].getName().replaceAll("\\.fastq", ".rest.fastq")), blacklistFiles.get(i), BMTagger.RestrictType.FastQ)
                            }
                    );
                } else {
                    restrictedFastqFiles.add(new File[]{left});
                }
            }

            //5. Run Trinities
            fileCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(1);
            final File trinityExec = new File("/opt/trinityrnaseq_r20131110/trinity");
            final boolean forward = true;
            final int numThreads = 6;
            final int minContigLenghth = 70;
            final String seqType = Trinity.FASTQ;
            final String jmMemory = "50G";
            System.out.println("Starting trinities..");

            for (int i = 0; i < restrictedFastqFiles.size(); i++) {
                if (restrictedFastqFiles.get(i).length > 1) {
                    fileCallableProcessExecutor.addProcess(Trinity.newInstance(trinityExec, restrictedFastqFiles.get(i)[0], restrictedFastqFiles.get(i)[1], forward, numThreads, minContigLenghth, seqType, jmMemory));
                } else {
                    fileCallableProcessExecutor.addProcess(Trinity.newInstance(trinityExec, restrictedFastqFiles.get(i)[0], forward, numThreads, minContigLenghth, seqType, jmMemory));
                }
            }
            final List<File> trinityOutputs = fileCallableProcessExecutor.getFutures().stream().map(
                    future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
            ).collect(Collectors.toList());
            fileCallableProcessExecutor.shutdown();

            //6. Search contigs against the human reference
            final CallableProcessExecutor<File, Callable<File>> bmtaggerCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(6);
            trinityOutputs.stream().map(output ->
                            bmtaggerCallableProcessExecutor.addProcess(
                                    new BMTagger.BMTaggerBuilder()
                                            .bmtaggerExecutale(bmTaggerExec)
                                            .lLane(output)
                                            .referenceBitmask(bitmask)
                                            .referenceSrprism(sRPrism)
                                            .tmpDir(tmpDir)
                                            .restrictType(BMTagger.RestrictType.FastA)
                                            .build())

            ).count();
            final List<File> contigBlacklists = bmtaggerCallableProcessExecutor.getFutures().stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            //7. Restrict the contigs
            final List<File> outFiles = new ArrayList<>();
            for (int i = 0; i < trinityOutputs.size(); i++) {

                outFiles.add(BMTagger.restrict(trinityOutputs.get(i), new File(trinityOutputs.get(i).getParent(),
                                trinityOutputs.get(i).getName().replaceAll("\\.fasta", ".rest.fasta")),
                        contigBlacklists.get(i), BMTagger.RestrictType.FastA
                ));
            }


            bmtaggerCallableProcessExecutor.shutdown();
            final File outDir = new File("/home/alext/Documents/ocular_rnaseq/sequences/eye/for_tuit");
            outFiles.stream().forEach(file -> {

                        try {
                            FileUtils.copyFile(file, new File(outDir, file.getParentFile().getParentFile().getName() + "." + file.getName()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
            );



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }


    }

}
