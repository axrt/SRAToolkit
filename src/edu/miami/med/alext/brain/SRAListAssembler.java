package edu.miami.med.alext.brain;

import edu.miami.med.alext.ncbi.xml.jaxb.EXPERIMENTPACKAGESET;
import edu.miami.med.alext.ncbi.xml.jaxb.ExperimentPackageType;
import edu.miami.med.alext.ncbi.xml.jaxb.SRAXMLLoader;
import edu.miami.med.alext.net.DownloadSRA;
import edu.miami.med.alext.process.CallableProcessExecutor;
import edu.miami.med.alext.process.FixThreadCallableProcessExectuor;
import org.xml.sax.SAXException;


import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by alext on 4/19/14.
 */
public class SRAListAssembler {

    public static void main(String[] args) {

        final File driverXML = new File("/home/alext/Documents/Brain/full_process_of_SRP005169/human.xml");
        final File mainFolder = new File("/home/alext/Documents/Brain/full_process_of_SRP005169");
        final File bmTaggerExec = new File("/usr/local/bin/bmtagger.sh");
        final File humanBitmask = new File("/home/alext/NCBI/reference/grch38/grch38.bitmask");
        final File humanSRPrism = new File("/home/alext/NCBI/reference/grch38/grch38.srprism");
        final File tmpDir = new File("/home/alext/Downloads/tmp");
        try (InputStream inputStream = new FileInputStream(driverXML)) {

            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for humans: " + sraNames.size());
            /*for(String s:sraNames){
                DownloadSRA.downloadSRAToANewFolder(s, mainFolder);
                System.out.println(s+" downloaded");
            }*/

            /*final ExecutorService executorService= Executors.newSingleThreadExecutor();
            final File fastqDumpExec=new File("/usr/local/bin/fastq-dump.2.3.4");
            final List<Future<File[]>>futures=new ArrayList<>();
            for(String s:sraNames){
                 futures.add(executorService.submit(FastqDump.newInstance(fastqDumpExec,new File(mainFolder,s+"/"+s+".sra"))));
            }
            for(Future<File[]>f:futures){
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executorService.shutdown();*/

            final CallableProcessExecutor<File, Callable<File>> fileCallableCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(7);
            List<File> folders = new ArrayList<>();
            for (String s : sraNames) {
                final File subFolder = new File(mainFolder, s);
                folders.add(subFolder);
            }
            for (int i = 0; i < folders.size(); i++) {
                final File rLane = new File(folders.get(i), sraNames.get(i) + "_2.fastq");
                final File lLane = new File(folders.get(i), sraNames.get(i) + "_1.fastq");
                if (rLane.exists()) {
                    fileCallableCallableProcessExecutor.addProcess(
                            new BMTagger.BMTaggerBuilder()
                            .bmtaggerExecutale(bmTaggerExec)
                            .lLane(lLane)
                            .rLane(rLane)
                            .referenceBitmask(humanBitmask)
                            .referenceSrprism(humanSRPrism)
                            .tmpDir(tmpDir)
                            .build()
                    );
                } else {
                    fileCallableCallableProcessExecutor.addProcess(new BMTagger.BMTaggerBuilder()
                            .bmtaggerExecutale(bmTaggerExec)
                            .lLane(lLane)
                            .rLane(rLane)
                            .referenceBitmask(humanBitmask)
                            .referenceSrprism(humanSRPrism)
                            .tmpDir(tmpDir)
                            .restrictType(BMTagger.RestrictType.FastQ)
                            .build());
                }
            }
            final List<Future<File>> futures = fileCallableCallableProcessExecutor.getFutures();
            final List<File> blacklists = new ArrayList<>();
            for (Future<File> f : futures) {
                try {
                    blacklists.add(f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < folders.size(); i++) {
                final File rLane = new File(folders.get(i), sraNames.get(i) + "_2.fastq");
                final File lLane = new File(folders.get(i), sraNames.get(i) + "_1.fastq");
                BMTagger.restrict(lLane, new File(lLane.getParent(), lLane.getName().replaceAll("fastq", "rest.fastq")), blacklists.get(i), BMTagger.RestrictType.FastQ);
                if (rLane.exists()) {
                    BMTagger.restrict(lLane, new File(rLane.getParent(), rLane.getName().replaceAll("fastq", "rest.fastq")), blacklists.get(i), BMTagger.RestrictType.FastQ);
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
