/*
 * 02/01/2015
 */
package org.netbeans.modules.gwtp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author faiz
 */
public class GwtpUtil {

    public static void bindHandler(SrcClass handlerClass, 
            String actionPackage, String targetName) {  
                
        StringBuilder buf = new StringBuilder("bindHandler(").append(targetName)
            .append("Action.class, ").append(targetName).append("Handler.class");
        
        //bind
        appendLineToFile(buf.toString(), Constants.HANDLER_MODULE, 
                FileUtil.toFile(handlerClass.getFile()));
        
        //import statement
        appendLineToFile(actionPackage, Constants.GUICE_IMPORT_STMT, 
                FileUtil.toFile(handlerClass.getFile()));
    }
    
    private static String getImportStatement(String qualifiedName) {
        return "import " + qualifiedName + ";";
    }
    
    private static void appendLineToFile(String lineToAdd, String after, File file) {
        FileInputStream fis = null;
        try {
            // temp file
            File outFile = new File("__tmpFile.tmp");
            fis = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            // output
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter out = new PrintWriter(fos);
            String curLine = "";
            after = after.replace(" ", "");
            while ((curLine = in.readLine()) != null) {
                if (curLine.replace(" ", "").toLowerCase()
                        .contains(after)) {
                    out.println(lineToAdd);
                }
                out.println(curLine);
            }
            out.flush();
            out.close();
            in.close();
            outFile.renameTo(file);
            outFile.delete();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    public static boolean containsInFile(String line, File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String curLine = "";
            line = line.replace(" ", "");
            while ((curLine = in.readLine()) != null) {
                if (curLine.replace(" ", "").contains(line)) {
                    return true;
                }
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return false;
    }
}
