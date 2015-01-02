/*
 * 02/01/2015
 */
package org.netbeans.modules.gwtp.util;

import org.openide.filesystems.FileObject;

/**
 *
 * @author faiz
 */
public class SrcPackage {
    private final FileObject file;
    private final String packageName;

    public SrcPackage(FileObject file, String packageName) {
        this.file = file;
        this.packageName = packageName;
    }

    public FileObject getFile() {
        return file;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getImportStatement() {
        return "import " + packageName + ";";
    }
    
    @Override
    public String toString() {
        return packageName ;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.packageName != null ? this.packageName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SrcPackage other = (SrcPackage) obj;
        if ((this.packageName == null) ? (other.packageName != null) : !this.packageName.equals(other.packageName)) {
            return false;
        }
        return true;
    }
       
}
