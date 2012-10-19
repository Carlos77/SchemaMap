/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.server;

import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import org.nyu.edu.dlts.client.model.SchemaData;

/**
 * Simple class for reading a file from the URL
 * @author nathan
 */
public class FileUtil {
    public static String saveDirectory = "";
    
    private static final String FIELD_MAPPING_INFO_FILENAME = "fieldMappingInfo.xml";

    /**
     * Method to read a file from a URI and return the text as string
     * 
     * @param fileURI 
     */
    public static String readFileContent(String filePath) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        try {

            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

        } finally {
            br.close();
        }

        return sb.toString();
    }
    
    /**
     * Method to write the file contents to a file
     * 
     * @param filePath
     * @param contents 
     */
    public static void writeFileContent(String filePath, String contents) throws Exception {
        //use buffering
        Writer output = new BufferedWriter(new FileWriter(filePath));
        try {
            //FileWriter always assumes default encoding is OK!
            output.write(contents);
        } finally {
            output.close();
        }
    }

    /**
     * Method to save the schema data object as xml to some directory
     * 
     * @param schemaData 
     */
    public static void saveSchemaDataList(ArrayList<SchemaData> schemaDataList) throws Exception {
        // get the file name to save to
        String filePath = getFilePathForSchemaData(schemaDataList.get(0).getType());
        
        // convert object to xml then save to file
        XStream xstream = new XStream();
        
        String xmlContent = xstream.toXML(schemaDataList);
        
        writeFileContent(filePath, xmlContent);
    }
    
    /**
     * Method to read the schema data from a file
     * 
     * @param schemaDataType
     * @return
     * @throws Exception 
     */
    public static ArrayList<SchemaData> getSchemaDataList(String schemaDataType) throws Exception {
        // get the file name to save to
        String filePath = getFilePathForSchemaData(schemaDataType);
        
        String xmlContent  = readFileContent(filePath);
        
        // convert object from xml then return the object
        XStream xstream = new XStream();
        
        ArrayList<SchemaData> schemaDataList = (ArrayList<SchemaData>)xstream.fromXML(xmlContent);
        
        return schemaDataList;
    }
    
    /**
     * Method to get the file path based on the schema data type
     * 
     * @param schemaDataType
     * @return 
     */
    public static String getFilePathForSchemaData(String schemaDataType) {
        String filePath;
        
        // based on schema data type set the filepath
        if(schemaDataType.equals(SchemaData.AT_TYPE)) {
            filePath = saveDirectory + File.separator + "AT_SchemaData.xml";
        } else if(schemaDataType.equals(SchemaData.AR_TYPE)) {
            filePath = saveDirectory + File.separator + "AR_SchemaData.xml";
        } else if(schemaDataType.equals(SchemaData.AS_TYPE)) {
            filePath = saveDirectory + File.separator + "AS_SchemaData.xml";
        } else {
            filePath = saveDirectory + File.separator + "Unknown_SchemaData.xml";
        }
        
        return filePath;
    }
    
    /**
     * Method to return the saved mapping information
     * 
     * @return 
     */
    public static HashMap<String, String> getMappingInfo() {
        String filePath = saveDirectory + File.separator + FIELD_MAPPING_INFO_FILENAME;
        
        try {
            String xmlContent = readFileContent(filePath);
        
            // convert object from xml then return the object
            XStream xstream = new XStream();
        
            HashMap<String, String> mappingInfo = (HashMap<String, String>)xstream.fromXML(xmlContent);
        
            return mappingInfo;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Method to save the mapping info object to an xml file
     * 
     * @param mappingInfo 
     */
    public static void saveMappingInfo(HashMap<String, String> mappingInfo) throws Exception {
        String filePath = saveDirectory + File.separator + FIELD_MAPPING_INFO_FILENAME;
        
        // convert object to xml then save to file
        XStream xstream = new XStream();
        
        String xmlContent = xstream.toXML(mappingInfo);
        
        writeFileContent(filePath, xmlContent);
    }
}
