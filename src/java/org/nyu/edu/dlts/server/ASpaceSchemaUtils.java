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
public class ASpaceSchemaUtils {
    private static String indexPath;
    private static String docUrl;
    
    
    // use to store fields for abstract classes
    static private HashMap<String, ArrayList<String>> abstractFieldsMap = new HashMap<String, ArrayList<String>>();
    
    /**
     * Method used to set the index URU and also the path
     * 
     * @param indexURI 
     */
    public static void setIndexPath(String path, String url) {
        indexPath = path;
        docUrl = url;
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

        /*HttpGet httpget = new HttpGet(indexURI);
        HttpResponse response = httpClient.execute(httpget);

        HttpEntity entity = response.getEntity();*/

        String fileContent = FileUtil.readFileContent(indexPath);
        
        if (fileContent != null) {

            String[] lines = fileContent.split("\\n");

            for (String line : lines) {
                if(line.isEmpty()) continue;
                
                String schemaName = line.replace(".rb", "");
                System.out.println("Schema Name: " + schemaName);
                
                // get the data fields from the schema files on the server
                String schemaFileURI = docUrl + "/" + schemaName + "_schema.txt";
                
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
        
        // boolean that specifies that this is an abstract schema
        boolean isAbstract = false;
        
        // if this schema is an obstract schemma the store it in the abstract field list
        if(schemaName.contains("abstract_")) {
            fieldsMap = abstractFieldsMap;
            isAbstract = true;
        }
        
        HttpGet httpget = new HttpGet(schemaFileURI);
        HttpResponse response = httpClient.execute(httpget);

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            ArrayList<String> fieldsList = new ArrayList<String>();
            
            String fileContent = EntityUtils.toString(entity);

            String[] lines = fileContent.split("\\n");

            for (int i = 3; i < lines.length; i++) {
                String line = lines[i];
                if(line.isEmpty()) continue;
                
                String[] sa = line.split("\\t");
                
                String propertyName = sa[1];
                String propertyType = sa[0];
                
                if(!propertyName.equals("uri")) {
                    String propertyInfo = propertyName + ", " + propertyType.toUpperCase();
                    fieldsList.add(propertyInfo);
                    System.out.println("Property Info: " + propertyInfo);
                }
            }
            
            fieldsMap.put(schemaName, fieldsList);
            
            // if we are not an abstract schema see if there is an abstract schema to add the fields
            if(!isAbstract) {
                // check to see if it as an abstract schema
                String[] sa = schemaName.split("_");
                String abstractSchemaName = "abstract_" + sa[0];
                
                ArrayList<String> alist = abstractFieldsMap.get(abstractSchemaName);
                
                if(alist != null && !schemaName.equals("agent_contact")) {
                    fieldsList.addAll(alist);
                    System.out.println("Added abstract fields to " + schemaName);
                }        
            } else {
                return null; // we don't want to add abstract schemas
            }
        }

        return fieldsMap;
    }

    /**
     * Main method for testing without spinning up servlet container
     * 
     * @param args 
     */
    public static void main(String[] args) {
        // set the index uri and url for ASpace docs
        String path = "/Users/nathan/NetBeansProjects/SchemaMap/web/schemas/AS/index.txt";
        String docURL = "http://hudmol.github.com/archivesspace/doc";
        ASpaceSchemaUtils.setIndexPath(path, docURL);
        
        try {
            ASpaceSchemaUtils.processSchemaIndex();
        } catch (Exception ex) {
            Logger.getLogger(ASpaceSchemaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
