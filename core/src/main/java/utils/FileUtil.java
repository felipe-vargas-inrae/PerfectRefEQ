package utils;


import org.json.JSONObject;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    public static String getFilesize(String path) throws IOException {
        path= path.replace("file://",""); // valid for both cases when is uri or when is path
        Path p = Paths.get(path);

        long bytes = Files.size(p);
        return  String.format("%,f MB", ((double) bytes)/(1024*1024));
    }

    public static void main(String [] args) throws IOException {

//        String path = "/home/mistea/Téléchargements/results/Time012.pdf";
//        String size = getFilesize(path);
//        System.out.println(size);
    }

    public static void writeFile(String path, String text){
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }
    }

    public static void createFolder( String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    public static HashMap<String, String>  fileToHashMap(String filePath) throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null)
        {
            String[] parts = line.split("=", 2);
            if (parts.length >= 2)
            {
                String key = parts[0].trim();
                String value = parts[1].trim().replace("\'","").replace("\"","");
                map.put(key, value);
            } else {
                System.out.println("ignoring line: " + line);
            }
        }
        for (String key : map.keySet())
        {
            System.out.println(key + "=" + map.get(key));
        }
        reader.close();
        return  map;
    }

    public static List<String> strToList(String l){
        if(l != null && l.startsWith("[") && l.endsWith("]") ){

            l = l.replace("[","").replace("]","");
            List<String> myList = new ArrayList<String>(Arrays.asList(l.split(",")));
            return myList;
        }
        return null;
    }
    public static String readFileAsString(String fileName) throws IOException {
        String data = "";
        fileName = flattenURI(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        data = reader.lines().collect(Collectors.joining("\n"));
        reader.close();
        return data;
    }

    public static String flattenURI(String uri) {
        return uri.replace("file:", "");
    }
    public static JSONObject readJsonFile(String fileName) throws IOException {
        String data = readFileAsString(fileName);
        JSONObject obj=  new JSONObject(data);
        return  obj;
    }

    public static void deleteFile(String path){
        try
        {
            File f= new File(path);           //file to be delete
            if(f.delete())                      //returns Boolean value
            {
                System.out.println(f.getName() + " deleted");   //getting and printing the file name
            }
            else
            {
                System.out.println("failed");
            }
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public static void zipModelFile(String pathFile)  {
        String pathCompressed = pathFile.replace(".ttl", "_compressed.zip");
        pathCompressed = pathCompressed.replace(".csv", "_compressed.zip");

        System.out.println(pathCompressed);
        zipFile(pathFile, pathCompressed);
    }
    public static void zipFile(String pathFile, String pathCompressed)  {

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream(pathCompressed);
            zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(pathFile);
            fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  List<String> readFileNamesFolder(String path, String regex){
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(regex);
            }
        });

        System.out.println(listOfFiles);
        return Arrays.stream(listOfFiles).filter(x->x.isFile()).map(x->x.getName()).collect(Collectors.toList());
    }

    public static void createDirectory(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        Files.createDirectory(path);
    }
}
