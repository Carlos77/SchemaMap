/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;
import java.util.HashMap;
import org.nyu.edu.dlts.client.model.SchemaData;

/**
 *
 * @author nathan
 */
@RemoteServiceRelativePath("schemadataservice")
public interface SchemaDataService extends RemoteService {

    public String myMethod(String s);
    
    public ArrayList<SchemaData> getSchemaDataAT();
    
    public ArrayList<SchemaData> getSchemaDataAR();
    
    public ArrayList<SchemaData> getSchemaDataAS();
    
    public String authorize(String username, String password);
}
