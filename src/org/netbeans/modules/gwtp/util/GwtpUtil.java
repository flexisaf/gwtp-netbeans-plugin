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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author faiz
 */
public class GwtpUtil {

    public static void bindHandler(SrcClass handlerClass, 
            String actionPackage, String targetName) {  
                
        StringBuilder buf = new StringBuilder("\tbindHandler(").append(targetName)
            .append("Action.class, ").append(targetName).append("Handler.class);");
        
        //bind
        appendLineToFile(buf.toString(), Constants.HANDLER_MODULE_CONFIG, 
                FileUtil.toFile(handlerClass.getFile()));
        
        //import statement
        appendLineToFile(getImportStatement(actionPackage, targetName + "Action"), 
                Constants.GUICE_IMPORT_STMT, 
                FileUtil.toFile(handlerClass.getFile()));
    }
    
    public static void bindPresenterModule(String presenter, 
            String presenterPackage, FileObject appModule) {  
        
        //bind
        appendLineToFile("\tinstall(new " + presenter + "Module());", 
                Constants.APP_MODULE_CONFIG, FileUtil.toFile(appModule));
        
        //import statement
        appendLineToFile(getImportStatement(presenterPackage, presenter + "Module"), 
                Constants.GIN_IMPORT_STMT, FileUtil.toFile(appModule));
    }
    
    private static String getImportStatement(String packageName, String className) {
        return "import " + packageName + "." + className + ";";
    }
    
    private static void appendLineToFile(String lineToAdd, String after, File file) {
        FileInputStream fis = null;
        try {
            // temp file
            File tmpFile = new File("__tmpFile.tmp");
            fis = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            // output
            PrintWriter out = new PrintWriter(new FileOutputStream(tmpFile));
            String curLine = "";
            after = after.replace(" ", "").toLowerCase();
            while ((curLine = in.readLine()) != null) {
                out.println(curLine);
                
                if (curLine.replace(" ", "").toLowerCase().contains(after)) {
                    out.println(lineToAdd);
                }
            }
            out.flush();
            out.close();
            in.close();
            tmpFile.renameTo(file);
            tmpFile.delete();
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
