package edu.miami.med.alext.brain;

import edu.miami.med.alext.ncbi.xml.jaxb.EXPERIMENTPACKAGESET;
import edu.miami.med.alext.ncbi.xml.jaxb.ExperimentPackageType;
import edu.miami.med.alext.ncbi.xml.jaxb.SRAXMLLoader;
import edu.miami.med.alext.net.DownloadSRA;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by alext on 4/19/14.
 */
public class SRAListAssembler {

    public static void main (String[]args){

        final File driverXML=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/human.xml");
        final File mainFolder=new File("/home/alext/Documents/Brain/full_process_of_SRP005169");
        try(InputStream inputStream=new FileInputStream(driverXML)){

            final EXPERIMENTPACKAGESET experimentpackageset= SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames=new ArrayList<>();
            for(ExperimentPackageType experimentPackageType:experimentpackageset.getEXPERIMENTPACKAGE()){
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for humans: "+sraNames.size());
            sraNames.remove("SRR112675");
            /*for(String s:sraNames){
                DownloadSRA.downloadSRAToANewFolder(s, mainFolder);
                System.out.println(s+" downloaded");
            }*/
            final ExecutorService executorService= Executors.newSingleThreadExecutor();
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
            executorService.shutdown();

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
