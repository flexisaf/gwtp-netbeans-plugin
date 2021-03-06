/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.gwtp.action;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.gwtp.util.Constants;
import org.netbeans.modules.gwtp.util.GwtpUtil;
import org.netbeans.modules.gwtp.util.ProjectInfo;
import org.netbeans.modules.gwtp.util.PropertyKeys;
import org.netbeans.modules.gwtp.util.SrcClass;
import org.netbeans.modules.gwtp.util.SrcPackage;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

public final class GwtpActionWizardIterator 
        implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private WizardDescriptor.Panel<WizardDescriptor> actionPanel;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {        
        Project project = Templates.getProject(wizard);
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(
                JavaProjectConstants.SOURCES_TYPE_JAVA);
        
        actionPanel = JavaTemplates.createPackageChooser(
                project, groups, new GwtpActionWizardPanel1(
                ProjectInfo.getPackages(project), 
                ProjectInfo.getHandlerModules(project), 
                ProjectInfo.getAbstractActionPackage(project)));
        
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(actionPanel);
            String[] steps = createSteps();
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
        return panels;
    }

    @Override
    public Set<?> instantiate() throws IOException {
        //Prepare the arguments for passing to the FreeMarker template:
        Map<String, Object> args = new HashMap<String, Object>();
        
        //Get the presenter name
        String targetName = Templates.getTargetName(wizard);
        targetName = targetName.replace("Action", "");
        
        //set the target name (Presenter)
        args.put("targetName", targetName);
        
        //Get the package:
        FileObject dir = Templates.getTargetFolder(wizard);
        //FileObject newDir = dir.createFolder(targetName.toLowerCase());
        DataFolder df = DataFolder.findFolder(dir);
        
        final Set<FileObject> files = new LinkedHashSet<FileObject>();
        
        String actionName = targetName + "Action";
        String resultName = targetName + "Result";
        String handlerName = targetName + "Handler";
        
        //set action package      
        Project project = Templates.getProject(wizard);
        args.put("actionPackage", ProjectInfo.getPackage(project, dir));
        
        //action template
        files.add(processTemplate(
                "Templates/GWTP/GwtpAction.java", 
                df,
                actionName,
                args));
        
        //result template
        files.add(processTemplate(
                "Templates/GWTP/GwtpResult.java", 
                df,
                resultName,
                args));
        
        //handler template
        SrcPackage handlerPackage = (SrcPackage)wizard.getProperty(
                PropertyKeys.HandlerPackage.name());
                
        files.add(processTemplate(
                "Templates/GWTP/GwtpActionHandler.java", 
                DataFolder.findFolder(handlerPackage.getFile()),
                handlerName,
                args));
        
        //add AbstractAction if not existing in handler package 
        if (ProjectInfo.getAbstractAction(project) == null) {
            files.add(processTemplate(
                "Templates/GWTP/GwtpAbstractAction.java", 
                DataFolder.findFolder(handlerPackage.getFile()),
                Constants.AbstractAction.name(),
                args));
        }
        
        //bind action in handler module
        SrcClass handlerModule = (SrcClass)wizard.getProperty(
                PropertyKeys.HandlerModule.name());
        GwtpUtil.bindHandler(handlerModule, 
                ProjectInfo.getPackage(project, dir), targetName);
        
        return files;
    }

    private FileObject processTemplate(
            final String templateName,
            final DataFolder outputDir,
            final String className,
            final Map<String, ? extends Object> templateParameters)
            throws DataObjectNotFoundException, IOException {

        final DataObject template = DataObject.find(
                FileUtil.getConfigFile(templateName));

        return template.createFromTemplate(
                outputDir,
                className,
                templateParameters).getPrimaryFile();
    }
    
    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = (String[]) wizard.getProperty("WizardPanel_contentData");
        assert beforeSteps != null : "This wizard may only be used embedded in the template wizard";
        String[] res = new String[(beforeSteps.length - 1) + panels.size()];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels.get(i - beforeSteps.length + 1).getComponent().getName();
            }
        }
        return res;
    }
}
