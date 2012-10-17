/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
    // hashmap for storing field information
    private ArrayList<SchemaData> schemaDataMapAT = null;
    
    private ArrayList<SchemaData> schemaDataMapAR = null;
    
    private ArrayList<SchemaData> schemaDataMapAS = null;
    
    public String myMethod(String s) {
        // Do something interesting with 's' here on the server.
        return "Server says: " + s;
    }
    
    /**
     * Setup the url for accessing the schema list
     */
    @Override
    public void init() {
        // set the URI for accessing the schema index
        ServletContext context = getServletConfig().getServletContext();
        
        try {
            String saveDirectory = context.getRealPath("/schemas");
            FileUtil.saveDirectory = saveDirectory;
            
            String indexPath = context.getRealPath("/schemas/AT/index.txt");
            
            // set the index uri for AT
            ATSchemaUtils.setIndexPath(indexPath);
            
            // set the index url for Archon
            indexPath = "http://hudmol.github.com/archivesspace/doc/index.txt";
            ArchonSchemaUtils.setIndexPath(indexPath);
            
            // set the index uri and url for ASpace docs
            indexPath = context.getRealPath("/schemas/AS/index.txt");
            String docURL = "http://hudmol.github.com/archivesspace/doc";
            ASpaceSchemaUtils.setIndexPath(indexPath, docURL);
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
            if(schemaDataMapAT == null) {
                schemaDataMapAT = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ATSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AT_TYPE);
                    schemaDataMapAT.add(schemaData);
                }
                
                // save the list now
                FileUtil.saveSchemaDataList(schemaDataMapAT);
            }
            
            return schemaDataMapAT;
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
            // debug code to force ARChone schema data to reload always
            schemaDataMapAR = null;
            
            /*if(schemaDataMapAR == null) {
                schemaDataMapAR = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ArchonSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AR_TYPE);
                    schemaDataMapAR.add(schemaData);
                }
            }*/
            
            return schemaDataMapAR;
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
            if(schemaDataMapAS == null) {
                schemaDataMapAS = new ArrayList<SchemaData>();
                
                HashMap<String, ArrayList<String>> fieldsMap = ASpaceSchemaUtils.processSchemaIndex();
                
                // process all the the entries
                for(String schemaName: fieldsMap.keySet()) {
                    ArrayList<String> fieldInfo = fieldsMap.get(schemaName);
                    Collections.sort(fieldInfo);
                    
                    ArrayList<SchemaDataField> schemaDataFields = getSchemaDataFields(fieldInfo);
                    SchemaData schemaData = new SchemaData(schemaName, schemaDataFields);
                    schemaData.setType(SchemaData.AS_TYPE);
                    schemaDataMapAS.add(schemaData);
                }
                
                // save the list now
                FileUtil.saveSchemaDataList(schemaDataMapAS);
            }
            
            return schemaDataMapAS;
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
    private ArrayList<SchemaDataField> getSchemaDataFields(ArrayList<String> fieldInfo) {
        ArrayList<SchemaDataField> schemaDataFields = new ArrayList<SchemaDataField>();
        
        for(String info: fieldInfo) {
            String[] sa = info.split("\\s*,\\s*");
            String name = sa[0];
            String type = sa[1];
            
            SchemaDataField schemaDataField = new SchemaDataField(name, type);
            schemaDataFields.add(schemaDataField);
        }
        
        return schemaDataFields;
    }
    
    /**
     * Method to authenticate users
     * 
     * @param username
     * @param password
     * @return 
     */
    public String authorize(String username, String password) {
        return "authorized -- " + username;
    }
}
