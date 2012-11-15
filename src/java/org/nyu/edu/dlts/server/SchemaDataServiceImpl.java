/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.nyu.edu.dlts.client.SchemaDataService;
import org.nyu.edu.dlts.client.model.SchemaData;
import org.nyu.edu.dlts.client.model.SchemaDataField;

/**
 *
 * @author nathan
 */
public class SchemaDataServiceImpl extends RemoteServiceServlet implements SchemaDataService {
    // Array List for storing field information
    private ArrayList<SchemaData> schemaDataListAT = null;
    
    private ArrayList<SchemaData> schemaDataListAR = null;
    
    private ArrayList<SchemaData> schemaDataListAS = null;
    
    // the base location for the schema code 
    private String schemaCodeBaseUrl = "";
    
    // Hashmap for storing the mapping information for the fields 
    private HashMap<String, String> mappingInfo = new HashMap<String, String>();
    
    // Hashmap for storing the data values that need to be mapped
    private HashMap<String, ArrayList<SchemaDataField>> dataValueMapAT = new HashMap<String, ArrayList<SchemaDataField>>();
    
    // Holds user login information
    private HashMap<String, String> userInfo = new HashMap<String, String>();
    
    /**
     * Setup the url for accessing the schema list
     */
    @Override
    public void init() {
        // set the URI for accessing the schema index
        ServletContext context = getServletConfig().getServletContext();
        
        try {
            // open the database connection and load the user
            DatabaseUtil.setupTestDatabaseInfo();
            DatabaseUtil.getConnection();
            
            userInfo = DatabaseUtil.getUserLoginInfo();
            
            String saveDirectory = context.getRealPath("/schemas");
            FileUtil.saveDirectory = saveDirectory;
            
            String versionDirectory = context.getRealPath("/schemas/versions");
            FileUtil.versionDirectory = versionDirectory;
            
            // set the index uri for AT
            String indexPath = context.getRealPath("/schemas/AT/index.txt");
            ATSchemaUtils.setIndexPath(indexPath);
            
            // set the index url for Archon
            indexPath = "http://archives.library.illinois.edu/tmp/archon_datamodel/index.txt";
            ArchonSchemaUtils.setIndexPath(indexPath);
            
            // set the index uri and url for ASpace docs
            indexPath = context.getRealPath("/schemas/AS/index.txt");
            String docURL = "http://hudmol.github.com/archivesspace/doc";
            ASpaceSchemaUtils.setIndexPath(indexPath, docURL);
            
            // set the url root for ASpace schema code
            schemaCodeBaseUrl = "http://hudmol.github.com/archivesspace/doc";
            
            // load the stored AT and Archon schema data which has edited notes
            //ArrayList<SchemaData> savedSchemaDataList = FileUtil.getSchemaDataList(SchemaData.AT_TYPE);
            ArrayList<SchemaData> savedSchemaDataList = DatabaseUtil.getSchemaDataList(SchemaData.AT_TYPE);
            
            if(savedSchemaDataList != null) {
                schemaDataListAT = savedSchemaDataList;
            }
            
            //savedSchemaDataList = FileUtil.getSchemaDataList(SchemaData.AR_TYPE);
            savedSchemaDataList = DatabaseUtil.getSchemaDataList(SchemaData.AR_TYPE);
            
            if(savedSchemaDataList != null) {
                schemaDataListAR = savedSchemaDataList;
            }
        } catch (Exception ex) {
            Logger.getLogger(SchemaDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load schema fields by calling the utils method
     * 
     * @return HashMap containing FieldList
     */
    public ArrayList<SchemaData> getSchemaDataAT() {
        try {
            if(schemaDataListAT == null) {
                schemaDataListAT = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ATSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(schemaName, fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AT_TYPE);
                    
                    schemaDataListAT.add(schemaData);
                }
                
                // save the list now
                FileUtil.saveSchemaDataList(schemaDataListAT);
            }
            
            return schemaDataListAT;
        } catch (Exception ex) {
            Logger.getLogger(SchemaDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
    
    /**
     * Load schema fields by calling the utils method
     * 
     * @return HashMap containing FieldList
     */
    public ArrayList<SchemaData> getSchemaDataAR() {
        try {
            // 11/12/2012 -- Debug code to force ARChone schema data to reload always
            schemaDataListAR = null;
            
            if(schemaDataListAR == null) {
                schemaDataListAR = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ArchonSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(schemaName, fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AR_TYPE);
                    schemaDataListAR.add(schemaData);
                }
            }
            
            return schemaDataListAR;
        } catch (Exception ex) {
            Logger.getLogger(SchemaDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
    
    /**
     * Load schema fields by calling the utils method
     * 
     * @return HashMap containing FieldList
     */
    public ArrayList<SchemaData> getSchemaDataAS() {
        try {
            if(schemaDataListAS == null) {
                schemaDataListAS = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ASpaceSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(schemaName, fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AS_TYPE);
                    
                    // set the url for the schema code
                    String url = schemaCodeBaseUrl + "/" + schemaName + "_schema.html";
                    schemaData.setUrl(url);
                    
                    schemaDataListAS.add(schemaData);
                }
                
                // save the list now
                FileUtil.saveSchemaDataList(schemaDataListAS);
            }
            
            return schemaDataListAS;
        } catch (Exception ex) {
            Logger.getLogger(SchemaDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
    
    /**
     * Method to generate an array list of schema data field objects  
     * 
     * @param fieldInfo
     * @return 
     */
    private ArrayList<SchemaDataField> getSchemaDataFields(String schemaName, ArrayList<String> fieldInfo) {
        ArrayList<SchemaDataField> schemaDataFields = new ArrayList<SchemaDataField>();
        
        for(String info: fieldInfo) {
            String[] sa = info.split("\\s*,\\s*");
            String name = sa[0];
            String type = sa[1];
            
            SchemaDataField schemaDataField = new SchemaDataField(name, type);
            
            /* set the note and mapped to information, if any
            String note = mappingInfo.get(schemaName + "->" + name + "->NOTE");
            if (note != null) schemaDataField.setNote(note);
            
            String mappedTo = mappingInfo.get(schemaName + "->" + name + "->MAPPED_TO");
            if (mappedTo != null) schemaDataField.setMappedTo(mappedTo);*/
            
            schemaDataFields.add(schemaDataField);
        }
        
        return schemaDataFields;
    }
    
    /**
     * Method to update information about a schemaData including mapping information
     * 
     * @param schemaData
     * @return 
     */
    public synchronized String updateSchemaData(String userId, SchemaData schemaData) {
        try {
            if(schemaData.getType().equals(SchemaData.AT_TYPE)) {
                updateSchemaDataList(userId, schemaData, schemaDataListAT);
            } else { // must be Archon list we updating
                updateSchemaDataList(userId, schemaData, schemaDataListAR);
            }
        
            return "Updated -- " + schemaData.getName();
        } catch(Exception ex) {
            Logger.getLogger(SchemaDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return "Failed Update -- " + schemaData.getName();
        }
    }
    
    /**
     * Method to find and replace the stored schemaData with the one from the client
     * 
     * @param schemaData
     * @param schemaDataListAT 
     */
    private void updateSchemaDataList(String username, SchemaData schemaData, ArrayList<SchemaData> schemaDataList) throws Exception {
        for(int i = 0; i < schemaDataList.size(); i++) {
            SchemaData sd = schemaDataList.get(i);
            
            if(sd.getId() == schemaData.getId()) {
                schemaDataList.set(i, schemaData);
                break;
            }
        }
        
        // now save the schema data to an xml file
        //FileUtil.saveSchemaDataList(schemaDataList);
        DatabaseUtil.saveSchemaDataList(username, schemaDataList);
    }
    
    /**
     * Method to get Hashmap containing a list of values
     * 
     * @param type
     * @return 
     */
    public HashMap<String, ArrayList<SchemaDataField>> getDataValues(String type) {
        return dataValueMapAT;
    }
    
    /**
     * Method to update the HashMap containing a list of values
     * 
     * @param username
     * @param type
     * @param dataValuesMap
     * @return 
     */
    public String updateDataValues(String username, String type, HashMap<String, ArrayList<SchemaDataField>> dataValuesMap) {
        String message = "sucess -- " + username;
        
        return message;
    }
    
    /**
     * Method to authenticate users
     * 
     * @param username
     * @param password
     * @return 
     */
    public String authorize(String username, String password) {
        String message = "Login failed ...";
        
        if(userInfo.containsKey(username)) {
            if(password.equals(userInfo.get(username))) {
                message = "authorized -- " + username;
            }
        }
        
        return message;
    }

}
