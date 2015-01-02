/*
 * 02/01/2014
 */
package org.netbeans.modules.gwtp.util;

import org.openide.filesystems.FileObject;

/**
 *
 * @author faiz
 */
public class SrcClass {
    private final FileObject file;
    private final String qualifiedName;

    public SrcClass(FileObject file, String qualifiedName) {
        this.file = file;
        this.qualifiedName = qualifiedName;
    }

    public FileObject getFile() {
        return file;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.qualifiedName != null ? this.qualifiedName.hashCode() : 0);
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
        final SrcClass other = (SrcClass) obj;
        if ((this.qualifiedName == null) ? (other.qualifiedName != null) 
                : !this.qualifiedName.equals(other.qualifiedName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return qualifiedName;
    }
}
