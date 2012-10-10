/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nyu.edu.dlts.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;

/**
 * Simple class for reading a file from the URL
 * @author nathan
 */
public class FileReaderUtil {
    /**
     * Method to read a file from a URI and return the text as string
     * 
     * @param fileURI 
     */
    public static String readFile(String filePath) throws Exception {
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
}
