package edu.miami.med.alext.mothur;

import edu.miami.med.alext.caseclass.Sample;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by alext on 2/18/14.
 */
public class MothurUtilTest {
    //@Test
    public void test(){
       final File scriptFile=new File("/home/alext/Documents/HMPSamples/test/script");
       final String sra="SRR064938";
        try {
            MothurUtil.createMothurScript(sra,scriptFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test2(){
        final File primersFile=new File("/home/alext/Documents/HMPSamples/test/",MothurUtil.primers);
        final Sample sample=Sample.newInstanceFromComponents("SRsSMTHTEST","FEMALE","Test sample","ATTATAATATATAT","GGGGGGG","CCCC","Dont even know","SRsSHMTTEST");
        try {
            MothurUtil.createPrimersFile(sample,primersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
