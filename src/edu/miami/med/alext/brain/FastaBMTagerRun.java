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
 * Created by alext on 4/26/14.
 */
public class FastaBMTagerRun {

    public static void main(String[] ags) {

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
                File subFolder = new File(mainFolder, s);
                subFolder = new File(subFolder, Trinity.OUTPUT_DIR_DEFAULT);
                folders.add(subFolder);
            }

            CallableProcessExecutor<File, Callable<File>> fileCallableCallableProcessExecutor = FixThreadCallableProcessExectuor.newInstance(7);
            final File bmTaggerExec = new File("/usr/local/bin/bmtagger.sh");
            final File humanBitmask = new File("/home/alext/NCBI/reference/grch38/grch38.bitmask");
            final File humanSRPrism = new File("/home/alext/NCBI/reference/grch38/grch38.srprism");
            final File tmpDir = new File("/home/alext/Downloads/tmp");
            for (File f : folders) {
                fileCallableCallableProcessExecutor.addProcess(
                        new BMTagger.BMTaggerBuilder()
                                .bmtaggerExecutale(bmTaggerExec)
                                .lLane(new File(f, Trinity.OUTPUT_FILE))
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
