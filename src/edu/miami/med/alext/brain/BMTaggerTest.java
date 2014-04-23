package edu.miami.med.alext.brain;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by alext on 4/23/14.
 */
public class BMTaggerTest {
    @Test
    public void testFastqBlacklist(){

        final File input =new File("/home/alext/Documents/Brain/full_process_of_SRP005169/SRR090440/SRR090440_1.fastq");
        final File output=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/SRR090440/SRR090440_1.restrict.fastq");
        final File blacklist=new File("/home/alext/Documents/Brain/full_process_of_SRP005169/SRR090440/SRR090440_1.blacklist");
        final BMTagger.RestrictType type= BMTagger.RestrictType.FastQ;

        try {
            BMTagger.restrict(input,output,blacklist,type);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
