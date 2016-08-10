/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author zxuuz
 */
public class LabelAnalysis {

    /**
     * @param args the command line arguments
     */
    
    public static ArrayList<Label> LabelList = new ArrayList<>();
    public static ArrayList<File> listOfFiles = new ArrayList<>();
    public static String[] stopWords = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
                                        "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", 
                                        "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
                                        "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", 
                                        "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
                                        "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", 
                                        "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", 
                                        "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", 
                                        "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", 
                                        "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", 
                                        "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", 
                                        "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", 
                                        "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", 
                                        "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", 
                                        "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
    
    public static void main(String[] args) throws IOException {
        
        String pathName = "D:\\Xue\\2016\\Summer\\Policy\\app privacy\\Financial_label_all";
        String type = "ID";  // ID, ID_dual, value, value_dual
        String outName = "";
        fileFilter(pathName);
        
        // read every string.xml files of every app
        for ( File filePath : listOfFiles) {
            FileInputStream fis = new FileInputStream(filePath);
 
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
 
            String line = null;
            int lineIndex = 0;
            while ((line = br.readLine()) != null) {

                // label extracting and processing
                if (lineIndex == 0) {  // not reading first line
                    lineIndex = lineIndex + 1;
                }else {                    
                    //System.out.println(line);
                    freqProcess(labelExtract(line, type));     
                }               
            }
            br.close();
        }
               
        // print into output file
        if ( type.equalsIgnoreCase("ID") ) { // unique word for ID
            outName = "D:\\Xue\\PolidroidProjects\\LabelAnalysis\\output\\output_Financial_ID.txt";
        }
        else if ( type.equalsIgnoreCase("ID_dual") ) { // dual words for ID
            outName = "D:\\Xue\\PolidroidProjects\\LabelAnalysis\\output\\output_Financial_ID_dual.txt";
        }
        else if ( type.equalsIgnoreCase("value") ) { // unique word for value
            outName = "D:\\Xue\\PolidroidProjects\\LabelAnalysis\\output\\output_Financial_value.txt";
        }
        else if ( type.equalsIgnoreCase("value_dual") ) { // dual words for ID
            outName = "D:\\Xue\\PolidroidProjects\\LabelAnalysis\\output\\output_Financial_value_dual.txt";
        }
        File filew = new File(outName);
        FileWriter fileWriter = new FileWriter(filew,true);       
        for ( Label l: LabelList ) { // output every label             
            fileWriter.write(l.getName() + "\t" + l.getFrequency() + "\n");
        }        
        fileWriter.flush();
	fileWriter.close();
    }
    
    public static void fileFilter(String directoryName) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {                
                if (file.getAbsolutePath().endsWith("\\res\\values\\strings.xml")) {
                    //System.out.println(file.getAbsolutePath());
                    listOfFiles.add(file);
                }
                
            } else if (file.isDirectory()) {
                fileFilter(file.getAbsolutePath());
            }
        }
    }

    // extract all the useful label
    protected static ArrayList<String> labelExtract ( String oldLine, String type ) {
        
        ArrayList<String> listOfLabels = new ArrayList<>();
        String line = filterLabels (oldLine);
        
        if ( type.contains("ID") ) {
            
            if (line.indexOf("<string name=\"") != -1) { // find start of id
                String subline = line.substring(line.indexOf("<string name=\"") + 14);               
                if ( subline.indexOf("\"") != -1 ) {
                    String labels = subline.substring(0, subline.indexOf("\""));
                    System.out.println("subline: " + subline);
                    System.out.println("labels: " +labels);
                    listOfLabels = labelSplit(labels, type);
                    System.out.println("listOfLabels: " + listOfLabels);
                }
            }
        }
        else if ( type.contains("value") ) {
            if (line.indexOf("<string name=\"") != -1) { // find start of id
                String startId = line.substring(line.indexOf("<string name=\"") + 14);
                if ( startId.indexOf(">") != -1 && startId.indexOf("<") != -1) { // found value text
                    System.out.println("subline: " + startId);
                    String labels = startId.substring(startId.indexOf(">") + 1, startId.indexOf("<"));
                    //System.out.println("subline: " + startId);
                    System.out.println("labels: " +labels);
                    listOfLabels = labelSplit(labels, type);
                    System.out.println("listOfLabels: " + listOfLabels);
                }
            }
        }    
        return listOfLabels;    
    }
    
    // split one line to labels
    protected static ArrayList<String> labelSplit(String labels, String type) {
        ArrayList<String> listOfLabels = new ArrayList<>();
        
        if ( type.equalsIgnoreCase("ID") ) { // unique word for ID
            String[] speLabels = labels.split("_");
            for (int i = 0; i < speLabels.length; i++) {
                if(filterMeaninglessLabels(speLabels[i]) != null) {
                    listOfLabels.add(speLabels[i]);
                }                    
            }
        }
        else if ( type.equalsIgnoreCase("ID_dual") ) { // dual words for ID
            String[] speLabels = labels.split("_");
            for (int i = 0; i < speLabels.length - 1; i++) {
                if(filterMeaninglessLabels(speLabels[i], speLabels[i+1]) != null) {
                    listOfLabels.add(speLabels[i] + " " + speLabels[i+1]);
                }
            }
        }
        else if ( type.equalsIgnoreCase("value") ) { // unique word for value
            String[] speLabels = labels.replaceAll(",", "").replaceAll("\"", "").replaceAll("\\.", "").split(" ");
            for (int i = 0; i < speLabels.length; i++) {
                if(filterMeaninglessLabels(speLabels[i]) != null) {
                    listOfLabels.add(speLabels[i]);
                }                    
            }
        }
        else if ( type.equalsIgnoreCase("value_dual") ) { // dual words for ID
            String[] speLabels = labels.replaceAll(",", "").replaceAll("\"", "").replaceAll("\\.", "").split(" ");
            for (int i = 0; i < speLabels.length - 1; i++) {
                if(filterMeaninglessLabels(speLabels[i], speLabels[i+1]) != null) {
                    listOfLabels.add(speLabels[i] + " " + speLabels[i+1]);
                }
            }
        }
        else
            return null;
        
        return listOfLabels;
        
    }
    
    protected static String filterLabels (String oldLine) {  
        // filtering google service label
        if (oldLine.contains("common_google_play_services") || oldLine.equalsIgnoreCase("app_name")) {
            return " ";
        }
        else {
            return oldLine;
        }
    }
    
    protected static String filterMeaninglessLabels (String unLabel) { 
        for (int i = 0; i < stopWords.length; i++) {
            if (unLabel.equalsIgnoreCase(stopWords[i])) {  // found stop word
                return null;
            }
        }
        return unLabel;
    }
    
    protected static String filterMeaninglessLabels (String unLabel1, String unLabel2) { 
        for (int i = 0; i < stopWords.length; i++) {
            if (unLabel1.equalsIgnoreCase(stopWords[i])) {  // found stop word1
                for (int j = 0; j < stopWords.length; j++) {
                    if (unLabel2.equalsIgnoreCase(stopWords[j])) {
                        return null;
                    }
                }
            }
        }
        return unLabel1 + " " + unLabel2;
    }
    
    protected static void freqProcess ( ArrayList<String> labelNames ) {
          
        if (labelNames.isEmpty()) {
            // do nothing
        }
        else {
            for (String labelName : labelNames) { // for every extracted label names
                boolean found = false;
            
                if ( LabelList.isEmpty() ) {  // when list is empty
                    Label l = new Label( labelName );
                    LabelList.add(l);
                }
                else {
                    // search whole list
                    for (Label l: LabelList) {
                        if (l.getName().equalsIgnoreCase(labelName)) {
                            l.addFrequency();
                            found = true;
                        } 
                    }
            
                    // not found, add new label to list
                    if (found == false) {
                        Label l = new Label( labelName );
                        LabelList.add(l);
                    }
                }
            }
        }
    }
    
}
