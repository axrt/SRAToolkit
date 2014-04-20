package scripts;

import edu.miami.med.alext.caseclass.Sample;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;
import util.FastaUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by alext on 2/25/14.
 */
public class SelectSamples {

    @Test
    public void selectThoseWithMoreSequences() {

        final File productionFolder = new File("/home/alext/Documents/HMPSamples/production");
        final String desiredExt = ".shhh.trim.fasta";
        final String compromiseExt = ".shhh.trim.pick.fasta";
        final List<File> filteredFiles = selectFilesFromProduction(productionFolder, desiredExt, compromiseExt);
        final int readsCutoff=250;
        final File driverTableFile=new File("/home/alext/Documents/HMPSamples/fullannotation.txt");
        final File selectedFilesOuputDir=new File("/home/alext/Documents/HMPSamples/selected");
        final File selectedFilesLegendFile=new File(selectedFilesOuputDir,"selected.leg");
        System.out.println("Filtered files found: " + filteredFiles.size());

        final DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        final Map<File, Long> fileReadMap=new HashMap<>(filteredFiles.size());
        try {
            for (File f : filteredFiles) {
                final long countFasta= FastaUtil.countFasta(f);
                descriptiveStatistics.addValue((double) countFasta);
                fileReadMap.put(f,countFasta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Min sequences: "+descriptiveStatistics.getMin());
        System.out.println("Mean sequences: "+descriptiveStatistics.getMean());
        System.out.println("Max sequences: "+descriptiveStatistics.getMax());

        final List<File> withReadsOverCutoff=new ArrayList<>();
        for(File f:fileReadMap.keySet()){
            if(fileReadMap.get(f)>=readsCutoff){
                withReadsOverCutoff.add(f);
            }
        }
        System.out.println("Number of files with reads over cutoff "+readsCutoff+" : "+withReadsOverCutoff.size());

        try {

            final List<Sample> samples=Processor.loadDriverTable(driverTableFile);
            final Map<String, Sample> srrSampleMap=new HashMap<>(samples.size());
            for(Sample s:samples){
                srrSampleMap.put(s.getSeqAccession(),s);
            }

            final Map<String, Integer> countMap=new TreeMap<>();
            for(File f:withReadsOverCutoff){
                final String srrName=f.getName().split("\\.")[0];
                final Sample sample=srrSampleMap.get(srrName);
                if(countMap.keySet().contains(sample.getSampleDefinintion())){
                    countMap.put(sample.getSampleDefinintion(),countMap.get(sample.getSampleDefinintion())+1);
                }else{
                    countMap.put(sample.getSampleDefinintion(),1);
                }
                final String outputFileName=sample.getSampleDefinintion()+'.'+sample.getGender()+"."+srrName+'.'+"fasta";
                //System.out.println(sample.getSampleDefinintion());
                FileUtils.copyFile(f,new File(selectedFilesOuputDir,outputFileName));
            }
            final StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("Group\tNumber of Samples\n");
            for(String s:countMap.keySet()){
                stringBuilder.append(s);
                stringBuilder.append('\t');
                stringBuilder.append(countMap.get(s));
                stringBuilder.append('\n');
            }
            FileUtils.writeStringToFile(selectedFilesLegendFile,stringBuilder.toString().trim());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static List<File> selectFilesFromProduction(final File productionFolder, final String desiredExt, final String compromiseExt) {

        final File[] productionFolderSubdirs = productionFolder.listFiles();
        final List<File> subDirs = new ArrayList<>(productionFolderSubdirs.length);
        for (File f : productionFolderSubdirs) {
            if (f.isDirectory()) {
                subDirs.add(f);
            }
        }

        final List<File> requiredFiles = new ArrayList<>();
        for (File f : subDirs) {
            final File[] internalFiles = f.listFiles();
            for (File fi : internalFiles) {
                if (fi.getName().endsWith(desiredExt)) {
                    requiredFiles.add(fi);
                    break;
                }
            }
            for (File fi : internalFiles) {
                if (fi.getName().endsWith(compromiseExt)) {
                    requiredFiles.add(fi);
                    break;
                }
            }
        }

        return requiredFiles;
    }
}
