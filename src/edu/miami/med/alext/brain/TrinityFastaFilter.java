package edu.miami.med.alext.brain;

import process.CallableProcess;

import edu.miami.med.alext.ncbi.xml.jaxb.EXPERIMENTPACKAGESET;
import edu.miami.med.alext.ncbi.xml.jaxb.ExperimentPackageType;
import edu.miami.med.alext.ncbi.xml.jaxb.SRAXMLLoader;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alext on 26/4/14.
 */
public class TrinityFastaFilter {

    public static void main (String[]ags){

        final File driverXML=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/human.xml");
        final File mainFolder=new File("/home/alext/Documents/Brain/full_process_of_SRP005169");
        try(InputStream inputStream=new FileInputStream(driverXML)) {

            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = new ArrayList<>();
            for (ExperimentPackageType experimentPackageType : experimentpackageset.getEXPERIMENTPACKAGE()) {
                sraNames.add(experimentPackageType.getRUNSET().getRUN().get(0).getAccession());
            }
            System.out.println("SRR archives for humans: " + sraNames.size());


            List<File> folders = new ArrayList<>();
            for (String s : sraNames) {
                File subFolder = new File(mainFolder, s);
                subFolder=new File(subFolder,Trinity.OUTPUT_DIR);
                folders.add(subFolder);
            }

            for(File f:folders){

            }

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
