package edu.miami.med.alext.module;

import xml.jaxb.EXPERIMENTPACKAGESET;
import xml.jaxb.SRAXMLLoader;
import net.DownloadSRA;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by alext on 10/7/14.
 * TODO document class
 */
public class SRADownloader implements Callable<List<File>> {


    protected final File driverXML;
    protected final File mainFolder;

    protected SRADownloader(File driverXML, File mainFolder) {
        this.driverXML = driverXML;
        this.mainFolder = mainFolder;
    }


    @Override
    public List<File> call() throws JAXBException, SAXException, IOException {
        try (InputStream inputStream = new FileInputStream(driverXML)) {

            //Download all the SRA files
            final EXPERIMENTPACKAGESET experimentpackageset = SRAXMLLoader.catchXMLOutput(inputStream);
            final List<String> sraNames = experimentpackageset.getEXPERIMENTPACKAGE().stream()
                    .map(ex -> {
                        return ex.getRUNSET().getRUN().get(0).getAccession();
                    }).collect(Collectors.toList());

            System.out.println("SRR archives number: " + sraNames.size());

            //Create a list of subfolders
            final List<File> subFolders = sraNames.stream().map(sraName -> new File(mainFolder, sraName)).collect(Collectors.toList());

            //For each folder - download the file and retain
            final List<File> sraFiles = sraNames.stream().map(sraName -> {
                try {
                    System.out.println(sraName + " is being downloaded..");
                    return DownloadSRA.downloadSRAToANewFolder(sraName, mainFolder);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).collect(Collectors.toList());
            return sraFiles;
        }catch (UncheckedIOException e){
            throw e.getCause();
        }
    }

    public static SRADownloader get(File driverXML, File mainFolder){
        return new SRADownloader(driverXML,mainFolder);
    }

    public static SRADownloader get(Path driverXML, Path mainFolder){
        return new SRADownloader(driverXML.toFile(),mainFolder.toFile());
    }
}
