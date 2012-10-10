
package org.nyu.edu.dlts.client.model;

import java.io.Serializable;

/**
 * Class that holds information about a particular in a schema
 * @author nathan
 */
public class SchemaDataField implements Serializable {
    private int id;
    private String name;
    private String type;
    private String mappedTo;
    private String note;
    private static int COUNTER = 0;
    
    /**
     * Default constructor which does nothing
     */
    public SchemaDataField() {
        this.id = Integer.valueOf(COUNTER++);
        this.name = "Test Schema Data Field";
        this.type = "Test Type";
        this.note = "none";
        this.mappedTo = "";
    }
    
    /**
     * Constructor that is typically used
     * 
     * @param name
     * @param type 
     */
    public SchemaDataField(String name, String type) {
        this.id = Integer.valueOf(COUNTER++);
        this.name = name;
        this.type = type;
        //this.note = "Test Note";
        //this.mappedTo = "Test Map";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getMappedTo() {
        return mappedTo;
    }

    public void setMappedTo(String mappedTo) {
        this.mappedTo = mappedTo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
