/*
 * 02/01/2015
 */
package org.netbeans.modules.gwtp.util;

import org.netbeans.api.project.Project;
import java.util.List;
import java.util.ArrayList;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.Enumeration;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author faiz
 */
public class ProjectInfo {
    
    public static SrcPackage getAbstractActionPackage(Project project) {
        FileObject srcDir = getSourcesDir(project);
                
        Enumeration<? extends FileObject> files = srcDir.getData(true);
        
        while (files.hasMoreElements()) {
            FileObject f = files.nextElement();
                            
            if (f.getName().equals(Constants.AbstractAction.name())) {
                
                String packageName = f.getPath().replace(srcDir.getPath(), "")
                    .replace(File.separator, ".").replaceFirst(".", "")
                        .replace("." + Constants.AbstractAction.name() + ".java", "");
                                
                return new SrcPackage(f.getParent(), packageName);
            }          
        }
        
        return null;
    }
    
    public static FileObject getAbstractAction(Project project) {
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> files = srcDir.getData(true);
        
        while (files.hasMoreElements()) {
            FileObject f = files.nextElement();
                            
            if (f.getName().equals(Constants.AbstractAction.name())) {
                return f;
            }          
        }
        
        return null;
    }
    
    public static FileObject getApplicationModule(Project project) {
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> files = srcDir.getData(true);
        
        while (files.hasMoreElements()) {
            FileObject f = files.nextElement();
                 
            if (GwtpUtil.containsInFile(Constants.APP_MODULE, FileUtil.toFile(f))) {
                return f;
            }
        }
        
        return null;
    }
    public static FileObject getPresenter(Project project, String s) {
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> files = srcDir.getData(true);
        
        while (files.hasMoreElements()) {
            FileObject f = files.nextElement();
                 
            if (GwtpUtil.containsInFile(s, FileUtil.toFile(f))) {
                return f;
            }
        }
        
        return null;
    }
    public static FileObject getNameTokenFile(Project project) {
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> files = srcDir.getData(true);
        
        while (files.hasMoreElements()) {
            FileObject f = files.nextElement();
                 
            if (GwtpUtil.containsInFile(Constants.NAME_TOKEN_CONFIG, FileUtil.toFile(f))) {
                return f;
            }
        }
        
        return null;
    }
    /**
     * 
     * This method returns the package name of  fo in  project.
     *
     * @param  project the project the file is in.
     * @param  fo the file whose package name is needed
     *         
     *
     * @return  The fully-qualified package name.
     *
     */
    public static String getPackage(Project project, FileObject fo) {
        FileObject srcDir = getSourcesDir(project);
        String packageName = fo.getPath().replace(srcDir.getPath(), "")
                    .replace(File.separator, ".").replaceFirst(".", "");
        return packageName;
    }
    
    public static List<SrcPackage> getPackages(Project project) {
        List<SrcPackage> packages = new ArrayList<SrcPackage>();
        
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> folEnum = srcDir.getFolders(true);
        
        while (folEnum.hasMoreElements()) {
            FileObject fo = folEnum.nextElement();
            String packageName = fo.getPath().replace(srcDir.getPath(), "")
                    .replace(File.separator, ".").replaceFirst(".", "");
            
            packages.add(new SrcPackage(fo, packageName));            
        }
        
        return packages;
    }
    
    public static List<SrcClass> getHandlerModules(Project project) {
        List<SrcClass> handlerModules = new ArrayList<SrcClass>();
        
        FileObject srcDir = getSourcesDir(project);
        
        Enumeration<? extends FileObject> folEnum = srcDir.getData(true);
        
        while (folEnum.hasMoreElements()) {
            FileObject fo = folEnum.nextElement();
            
            String qualifiedName = fo.getPath().replace(srcDir.getPath(), "")
                    .replace(File.separator, ".").replaceFirst(".", "");
            
            if (GwtpUtil.containsInFile(Constants.HANDLER_MODULE_CONFIG, FileUtil.toFile(fo)))
                handlerModules.add(new SrcClass(fo, qualifiedName));            
        }
        
        return handlerModules;
    }

    /**
     * @return first directory for source files
     */
    private static FileObject getSourcesDir(Project project) {
        final Sources sources = ProjectUtils.getSources(project);
        final SourceGroup[] javaSources = sources.getSourceGroups(
                JavaProjectConstants.SOURCES_TYPE_JAVA);

        final String referenceName = getSourcesDirReference(project);

        for(final SourceGroup sg : javaSources) {
            final String name = sg.getName();

            if(name.equals(referenceName) ||
                    // ${referenceName}
                    name.regionMatches(2, referenceName, 0,
                    referenceName.length())) {
                return sg.getRootFolder();
            }
        }

        // SourceRoot is used by Maven projects
        for(final SourceGroup sg : javaSources) {
            final String name = sg.getName();

            if(name.equals("SourceRoot")) { // NOI18N
                return sg.getRootFolder();
            }
        }

        if (javaSources.length > 0)
            return javaSources[0].getRootFolder();
        else
            return project.getProjectDirectory().
                    getFileObject("src/java"); // NOI18N
    }

    
    /**
     * Finds the first name of a registered "source-root" in a web project.
     * This method uses the {@code AuxiliaryConfiguration} and reads in the
     * web-apps data section to find the &lt;source-roots&gt; element and
     * the &lt;root&gt; elements it contains.
     *
     * @param project the {@code Project} to find the source-root of
     * @return the reference to the source-root as described in the
     *      project.xml file
     */
    private static String getSourcesDirReference(final Project project) {
        final AuxiliaryConfiguration configuration =
                ProjectUtils.getAuxiliaryConfiguration(project);

        final Element webappElement = configuration.getConfigurationFragment(
                "data", "http://www.netbeans.org/ns/web-project/3", true); // NOI18N

        if(webappElement != null) {
            final Element sourceRootsElement = (Element)webappElement.
                    getElementsByTagName("source-roots").item(0); // NOI18N

            if(sourceRootsElement != null) {
                final NodeList sourceRoots =
                        sourceRootsElement.getElementsByTagName("root"); // NOI18N

                for(int i = 0; i < sourceRoots.getLength(); i++) {
                    final Element root = (Element)sourceRoots.item(i);
                    final String id = root.getAttribute("id"); // NOI18N

                    if(id.length() > 0) {
                        return id;
                    }
                }
            }
        }

        return "src.dir"; // NOI18N
    }

}
