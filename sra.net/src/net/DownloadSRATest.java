package net;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by alext on 2/18/14.
 */
public class DownloadSRATest {
    @Test

    public void test(){

        final String sra="SRR328958";
        try {
            //DownloadSRA.downloadSra(sra,new File("/home/alext/Documents/HMPSamples/test/".concat(sra)));

                DownloadSRA.downloadSRAToANewFolder(sra,new File("/home/alext/Documents/HMPSamples/test"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
