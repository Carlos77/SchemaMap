package org.nyu.edu.dlts.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A simple class for loading AT hibernate schema files and extracting
 * relevant information from the files
 * 
 * @author Nathan Stevens
 * @date 10/01/2012
 */
public class ATSchemaUtils {

    private static String indexPath;
    private static String path;

    /**
     * Method used to set the index URU and also the path
     * 
     * @param indexURI 
     */
    public static void setIndexPath(String filepath) {
        indexPath = filepath;
        path = indexPath.replace("/index.txt", "");
    }

    /**
     * Method to load a text file containing a list of AT Schema objects
     * 
     * @return String containing list of hibernate schema 
     */
    public static HashMap<String, ArrayList<String>> processSchemaIndex() throws Exception {
        // stores the field information
        HashMap<String, ArrayList<String>> fieldsMap = new HashMap<String, ArrayList<String>>();

        String fileContent = FileReaderUtil.readFile(indexPath);

        if (fileContent != null) {

            String[] lines = fileContent.split("\\n");

            for (String line : lines) {
                if (line.isEmpty()) {
                    continue;
                }

                String schemaName = line.replace(".hbm.xml", "");
                System.out.println("Schema Name: " + schemaName);

                // get the data fields from the schema files on the server
                String schemaFileURI = path + "/" + line;

                HashMap<String, ArrayList<String>> currentSchemaFieldsMap = getDataFields(schemaFileURI);

                if (currentSchemaFieldsMap != null) {
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
    public static HashMap<String, ArrayList<String>> getDataFields(String schemaFileURI) throws Exception {
        // hashmap that hould the information
        HashMap<String, ArrayList<String>> fieldsMap = new HashMap<String, ArrayList<String>>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(schemaFileURI);
        doc.getDocumentElement().normalize();

        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        // get the class 
        NodeList classList = doc.getElementsByTagName("class");

        for (int i = 0; i < classList.getLength(); i++) {
            Element classElement = (Element) classList.item(i);

            String className = classElement.getAttribute("name");
            className = className.replace("org.archiviststoolkit.model.", "");

            System.out.println("Class Name is: " + className);


            // array list to hold the field informaion
            ArrayList<String> fieldsList = new ArrayList<String>();

            // get the properties, which are the fields now
            NodeList propertyList = classElement.getElementsByTagName("property");

            for (int j = 0; j < propertyList.getLength(); j++) {
                Element propertyElement = (Element) propertyList.item(j);

                String propertyName = propertyElement.getAttribute("name");
                String propertyType = propertyElement.getAttribute("type");
                String propertyLength = propertyElement.getAttribute("length");

                if (!propertyName.equals("auditInfo")) {
                    String propertyInfo = propertyName + ", " + propertyType.toUpperCase() + " " + propertyLength;
                    fieldsList.add(propertyInfo);
                    System.out.println("Property Info: " + propertyInfo);
                }
            }

            // Get the Sets of data now
            NodeList setList = classElement.getElementsByTagName("set");

            for (int j = 0; j < setList.getLength(); j++) {
                Element setElement = (Element) setList.item(j);

                String setName = setElement.getAttribute("name");
                String type = "SET";

                String setInfo = upperCaseFieldName(setName) + ", " + type;
                fieldsList.add(setInfo);
                System.out.println("Set Info: " + setInfo);
            }
            
            // Get any many to one, such as the repository
            NodeList manyToOneList = classElement.getElementsByTagName("many-to-one");
            
            for (int j = 0; j < manyToOneList.getLength(); j++) {
                Element mtoElement = (Element) manyToOneList.item(j);

                String mtoName = mtoElement.getAttribute("name");
                String type = "OBJECT";

                String mtoInfo = upperCaseFieldName(mtoName) + ", " + type;
                fieldsList.add(mtoInfo);
                System.out.println("Many To One Info: " + mtoInfo);
            }

            // add the properties to the main field list
            fieldsMap.put(fixName(className), fieldsList);
        }

        return fieldsMap;
    }

    /**
     * Method to return a more meaningful class or field name
     * 
     * @param name 
     */
    private static String fixName(String name) {
        if (name.equals("BasicNames")) {
            return "Names";
        }

        return name;
    }

    /**
     * Method to upper case the first letter in the field names
     * 
     * @param fieldName
     * @return 
     */
    private static String upperCaseFieldName(String fieldName) {
        final StringBuilder result = new StringBuilder(fieldName.length());
        
        result.append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1));

        return result.toString();
    }

    /**
     * Main method for testing without spinning up servlet container
     * 
     * @param args 
     */
    public static void main(String[] args) {
        String ip = "/Users/nathan/NetBeansProjects/SchemaMap/web/schemas/AT/index.txt";
        ATSchemaUtils.setIndexPath(ip);

        ATSchemaUtils schemaUtils = new ATSchemaUtils();

        try {
            schemaUtils.processSchemaIndex();
        } catch (Exception ex) {
            Logger.getLogger(ATSchemaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
