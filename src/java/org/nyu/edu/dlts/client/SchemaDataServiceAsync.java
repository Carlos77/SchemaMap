/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.HashMap;
import org.nyu.edu.dlts.client.model.SchemaData;

/**
 *
 * @author nathan
 */
public interface SchemaDataServiceAsync {

    public void myMethod(String s, AsyncCallback<String> callback);
    
    public void getSchemaDataAT(AsyncCallback<ArrayList<SchemaData>> callback);
    
    public void getSchemaDataAS(AsyncCallback<ArrayList<SchemaData>> callback);

    public void authorize(String username, String password, AsyncCallback<String> asyncCallback);
}
