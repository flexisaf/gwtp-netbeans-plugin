/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.gwtp.presenter;

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
import org.netbeans.modules.gwtp.util.GwtpUtil;
import org.netbeans.modules.gwtp.util.PresenterType;
import org.netbeans.modules.gwtp.util.ProjectInfo;
import org.netbeans.modules.gwtp.util.PropertyKeys;
import org.netbeans.modules.gwtp.util.RevealType;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * 
 * @author faiz
 */
public final class GwtpPresenterWizardIterator 
    implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;

    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private WizardDescriptor.Panel<WizardDescriptor> presenterPanel;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        Project project = Templates.getProject(wizard);
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        presenterPanel = JavaTemplates.createPackageChooser(
                project, groups, new GwtpPresenterWizardPanel1());
        
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(presenterPanel);
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
        
        //presenter type
        PresenterType preType = (PresenterType) wizard.getProperty(
                PropertyKeys.PresenterType.name());
        args.put(PropertyKeys.PresenterType.name(), preType.name());
        
        //reveal type (for Nested Presenter types)
        RevealType revealType = (RevealType) wizard.getProperty(
                PropertyKeys.RevealType.name());
        args.put(PropertyKeys.RevealType.name(), revealType.name());
        
        //slot name (for Slot reveal type)
        String slotName = (String) wizard.getProperty(PropertyKeys.SlotName.name());
        args.put(PropertyKeys.SlotName.name(), slotName);

        //Is Singleton Presenter (for PresenterWidget and PopupPresenter)
        boolean isSingleton = (Boolean) wizard.getProperty(PropertyKeys.IsSingleton.name());
        args.put(PropertyKeys.IsSingleton.name(), isSingleton);
        
        //Is Override default Popup Panel (for Popup Presenter)
        boolean isOverrideDefPopupPanel = (Boolean) wizard.getProperty(
                PropertyKeys.IsOverrideDefPopPanel.name());
        args.put(PropertyKeys.IsOverrideDefPopPanel.name(), isOverrideDefPopupPanel);
        
        //
        args.put(PropertyKeys.IsaPlace.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsaPlace.name()));
        args.put(PropertyKeys.NameToken.name(), 
                (String) wizard.getProperty(PropertyKeys.NameToken.name()));        
        args.put(PropertyKeys.IsCrawlable.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsCrawlable.name()));
        args.put(PropertyKeys.IsCodeSplit.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsCodeSplit.name()));
        args.put(PropertyKeys.IsAddUiHandlers.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsAddUiHandlers.name()));
        args.put(PropertyKeys.IsAddOnBind.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsAddOnBind.name()));
        args.put(PropertyKeys.IsAddOnHide.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsAddOnHide.name()));
        args.put(PropertyKeys.IsAddOnReset.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsAddOnReset.name()));
        args.put(PropertyKeys.IsAddOnUnbind.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsAddOnUnbind.name()));
        args.put(PropertyKeys.IsUseManualReveal.name(), 
                (Boolean) wizard.getProperty(PropertyKeys.IsUseManualReveal.name()));
        
        //Get the presenter name
        String targetName = Templates.getTargetName(wizard);
        targetName = targetName.replace("Presenter", "");
        
        String presenterName = targetName + "Presenter";       
        String viewName = targetName + "View";    //the view name for the presenter
        String viewUiXmlName = targetName + "View.ui"; //the UI binder for the view
        String moduleName = targetName + "Module"; //the GIN module for this presenter
        String uiHandlerName = targetName + "UiHandler"; //the GIN module for this presenter
        
        //set the target name (Presenter)
        args.put("targetName", targetName);
        
        //Get the package:
        FileObject dir = Templates.getTargetFolder(wizard);
        FileObject newDir = dir.createFolder(targetName.toLowerCase());
        DataFolder df = DataFolder.findFolder(newDir);
        
        final Set<FileObject> files = new LinkedHashSet<FileObject>(3);
        
        //presenter template
        files.add(processTemplate(
                "Templates/GWTP/GwtpPresenter.java", 
                df,
                presenterName,
                args));
        
        //bind presenter
        Project project = Templates.getProject(wizard);        
        GwtpUtil.bindPresenterModule(targetName, 
                ProjectInfo.getPackage(project, newDir), 
                ProjectInfo.getApplicationModule(project));
        
        //view template
        files.add(processTemplate(
                "Templates/GWTP/GwtpView.java", 
                df,
                viewName,
                args));
        
        //UI Binder template
        files.add(processTemplate(
                "Templates/xml/GwtpView.ui.xml", 
                df,
                viewUiXmlName,
                args));
        
        //GIN Module
        files.add(processTemplate(
                "Templates/GWTP/GwtpModule.java", 
                df,
                moduleName,
                args));
        
        //Add UiHandler class if UiHandlers is selected
        if ((Boolean) wizard.getProperty(PropertyKeys.IsAddUiHandlers.name())) {
            files.add(processTemplate(
                "Templates/GWTP/GwtpUiHandler.java", 
                df,
                uiHandlerName,
                args));
        }
        
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
