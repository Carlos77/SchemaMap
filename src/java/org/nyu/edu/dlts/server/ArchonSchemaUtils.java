package org.nyu.edu.dlts.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * A simple class for loading AT hibernate schema files and extracting
 * relevant information from the files
 * 
 * @author Nathan Stevens
 * @date 10/01/2012
 */
public class ArchonSchemaUtils {
    private static String indexPath;
    private static String docUrl;
    
    
    /**
     * Method used to set the index URU and also the path
     * 
     * @param indexURI 
     */
    public static void setIndexPath(String path) {
        indexPath = path;
        docUrl = indexPath.replace("/index.txt", "");
    }
    
    /**
     * Method to load a text file containing a list of AT Schema objects
     * 
     * @return String containing list of hibernate schema 
     */
    public static HashMap<String, ArrayList<String>> processSchemaIndex() throws Exception {
        // stores the field information
        HashMap<String, ArrayList<String>> fieldsMap = new HashMap<String, ArrayList<String>>();
        
        // initialize the http client for reading the schema file
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(indexPath);
        HttpResponse response = httpClient.execute(httpget);

        HttpEntity entity = response.getEntity();

        
        if (entity != null) {
            String fileContent = EntityUtils.toString(entity);
            
            String[] lines = fileContent.split("\\n");

            for (String line : lines) {
                if(line.isEmpty()) continue;
                
                String schemaName = line.replace(".rb", "");
                System.out.println("Schema Name: " + schemaName);
                
                // get the data fields from the schema files on the server
                String schemaFileURI = docUrl + "/" + schemaName + ".txt";
                
                HashMap<String, ArrayList<String>> currentSchemaFieldsMap = getDataFields(httpClient, schemaName, schemaFileURI);
                
                if(currentSchemaFieldsMap != null) {
                    fieldsMap.putAll(currentSchemaFieldsMap);
                } 
            }
        }

        return fieldsMap;
    }

    /**
     * Method to get the data fields by reading content from a file 
     * 
     * @param httpClient
     * @param schemaFileName
     * @return 
     */
    public static HashMap<String, ArrayList<String>> getDataFields(HttpClient httpClient, String schemaName, String schemaFileURI) throws Exception {
        // hashmap that hould the information
        HashMap<String, ArrayList<String>> fieldsMap = new HashMap<String, ArrayList<String>>();
        
        
        HttpGet httpget = new HttpGet(schemaFileURI);
        HttpResponse response = httpClient.execute(httpget);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            ArrayList<String> fieldsList = new ArrayList<String>();
            
            String fileContent = EntityUtils.toString(entity);

            String[] lines = fileContent.split("\\n");

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if(line.isEmpty()) continue;
                
                String[] sa = line.split("\\s*,\\s*");
                
                String propertyName = sa[1];
                String propertyType = sa[0];
                
                String propertyInfo = propertyName + ", " + propertyType.toUpperCase();
                fieldsList.add(propertyInfo);
                
                System.out.println("Property Info: " + propertyInfo);
            }
            
            fieldsMap.put(schemaName, fieldsList);
        }

        return fieldsMap;
    }

    /**
     * Main method for testing without spinning up servlet container
     * 
     * @param args 
     */
    public static void main(String[] args) {
        ArchonSchemaUtils schemaUtils = new ArchonSchemaUtils();
        
        try {
            schemaUtils.processSchemaIndex();
        } catch (Exception ex) {
            Logger.getLogger(ArchonSchemaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
