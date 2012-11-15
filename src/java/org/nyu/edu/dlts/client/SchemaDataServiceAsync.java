/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.HashMap;
import org.nyu.edu.dlts.client.model.SchemaData;
import org.nyu.edu.dlts.client.model.SchemaDataField;

/**
 *
 * @author nathan
 */
public interface SchemaDataServiceAsync {

    public void getSchemaDataAT(AsyncCallback<ArrayList<SchemaData>> callback);
    
    public void getSchemaDataAR(AsyncCallback<ArrayList<SchemaData>> callback);
    
    public void getSchemaDataAS(AsyncCallback<ArrayList<SchemaData>> callback);
    
    public void getDataValues(String type, AsyncCallback<HashMap<String, ArrayList<SchemaDataField>>> callback);
    
    public void updateDataValues(String username, String type, HashMap<String, ArrayList<SchemaDataField>> dataValuesMap, AsyncCallback<String> callback);
    
    public void updateSchemaData(String username, SchemaData schemaData, AsyncCallback<String> callback);

    public void authorize(String username, String password, AsyncCallback<String> callback);
}
