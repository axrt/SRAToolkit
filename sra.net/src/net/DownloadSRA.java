package net;

import java.io.*;
import java.net.URL;

/**
 * Created by alext on 2/18/14.
 */
public class DownloadSRA {

    public static String address = "http://trace.ncbi.nlm.nih.gov/Traces/sra/?run=";

    public static void downloadSra(String sra, File out) throws IOException {

        final URL driverPage = new URL(address.concat(sra));
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(driverPage.openConnection().getInputStream()))) {
            String line;
            String[]split;
            String downloadUrl=null;
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if (line.contains("<a href=\"http://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/")) {

                    split=line.split("\"");
                    downloadUrl=split[1];
                    System.out.println(downloadUrl);
                    break;
                }
            }
            try(
                    final BufferedInputStream bufferedInputStream=new BufferedInputStream(new URL(downloadUrl).openConnection().getInputStream());
                    final BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(new FileOutputStream(out));
            ){
                int n=-1;
                byte [] b=new byte[4096];
                while ((n=bufferedInputStream.read(b))!=-1){
                    if(n>0){
                        bufferedOutputStream.write(b,0,n);
                    }
                }
            }
        }

    }
    public static void systemDownloadSra(String sra, File out) throws IOException, InterruptedException {

        final URL driverPage = new URL(address.concat(sra));
        final ProcessBuilder processBuilder=new ProcessBuilder("wget",driverPage.toString(),"-P",out.getPath());
        final Process p=processBuilder.start();
        try (InputStream inputStream = p.getErrorStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                synchronized (System.out.getClass()) {
                    System.out.println("ERR:: >" + line);
                }
            }
        }
        try (InputStream inputStream = p.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                synchronized (System.out.getClass()) {
                    System.out.println("OUT:: >" + line);
                }
            }
        }
    }

    public static File downloadSRAToANewFolder(String sra,File dir) throws IOException {
        dir=new File(dir,sra);
        if(!dir.exists()){
            System.out.println("Creating ".concat(dir.toString()));
            dir.mkdir();
        }
        final File file=new File(dir,sra.concat(".sra"));
        if(!file.exists()){
            DownloadSRA.downloadSra(sra, file);
        }
        return file;
    }

    public static File systemDownloadSRAToANewFolder(String sra,File dir) throws IOException, InterruptedException {
        dir=new File(dir,sra);
        if(!dir.exists()){
            System.out.println("Creating ".concat(dir.toString()));
            dir.mkdir();
        }
        final File file=new File(dir,sra.concat(".sra"));
        if(!file.exists()){
            DownloadSRA.systemDownloadSra(sra, file);
        }
        return file;
    }
}
