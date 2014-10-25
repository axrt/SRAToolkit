package edu.miami.med.alext.brain;

import edu.miami.med.alext.process.CallableProcessExecutor;
import edu.miami.med.alext.process.FixThreadCallableProcessExectuor;
import org.xml.sax.SAXException;
import tools.BMTagger;
import tools.Trinity;
import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.ExperimentPackageType;
import xml.jaxb.SRAXMLLoader;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 4/24/14.
 */
public class TrinityRuner {

    public static void main(String[] args) {

        final File driverXML = new File("/home/alext/Documents/Brain/full_process_of_SRP005169/human.xml");
        final File mainFolder = new File("/home/alext/Documents/Brain/full_process_of_SRP005169");
        try (InputStream inputStream = new FileInputStream(driverXML)) {

            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for humans: " + sraNames.size());


            List<File> folders = new ArrayList<>();
            for (String s : sraNames) {
                final File subFolder = new File(mainFolder, s);
                folders.add(subFolder);
            }
            CallableProcessExecutor<File, Callable<File>> fileCallableCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(1);
            final File trinityExec = new File("/opt/trinityrnaseq_r20131110/trinity");
            final int numThreads = 12;
            final int minContigLenghth = 70;
            final Trinity.SEQ_TYPE seqType = Trinity.SEQ_TYPE.FQ;
            final String jmMemory = "50G";
            System.out.println("Starting trinities..");
            for (int i = 0; i < folders.size(); i++) {

                final File lLane = new File(folders.get(i), sraNames.get(i) + "_1.rest.fastq");
                final File rLane = new File(folders.get(i), sraNames.get(i) + "_2.rest.fastq");

                if (rLane.exists()) {
                    fileCallableCallableProcessExecutor.addProcess(Trinity.newInstance(trinityExec, lLane, rLane, Trinity.LIB_TYPE.FR, numThreads, minContigLenghth, seqType, jmMemory));
                } else {
                    fileCallableCallableProcessExecutor.addProcess(Trinity.newInstance(trinityExec, lLane, Trinity.LIB_TYPE.F, numThreads, minContigLenghth, seqType, jmMemory));
                }

            }
            final List<Future<File>> trinityFastas = fileCallableCallableProcessExecutor.getFutures();
            final List<File> trinityFastaFiles = new ArrayList<>();
            for (Future<File> f : trinityFastas) {
                try {
                    trinityFastaFiles.add(f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            fileCallableCallableProcessExecutor.shutdown();
            fileCallableCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(7);
            final File bmTaggerExec = new File("/usr/local/bin/bmtagger.sh");
            final File humanBitmask = new File("/home/alext/NCBI/reference/grch38/grch38.bitmask");
            final File humanSRPrism = new File("/home/alext/NCBI/reference/grch38/grch38.srprism");
            final File tmpDir = new File("/home/alext/Downloads/tmp");
            for (File f : trinityFastaFiles) {
                fileCallableCallableProcessExecutor.addProcess(
                        new BMTagger.BMTaggerBuilder()
                                .bmtaggerExecutale(bmTaggerExec)
                                .lLane(f)
                                .referenceBitmask(humanBitmask)
                                .referenceSrprism(humanSRPrism)
                                .tmpDir(tmpDir)
                                .restrictType(BMTagger.RestrictType.FastA)
                                .build()
                );
            }
            for (Future<File> f : fileCallableCallableProcessExecutor.getFutures()) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            fileCallableCallableProcessExecutor.shutdown();

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
