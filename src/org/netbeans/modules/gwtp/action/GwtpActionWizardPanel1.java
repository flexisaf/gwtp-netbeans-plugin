/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.gwtp.action;

import javax.swing.event.ChangeListener;
import org.netbeans.modules.gwtp.util.SrcPackage;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import java.util.List;
import org.netbeans.modules.gwtp.util.PropertyKeys;
import org.netbeans.modules.gwtp.util.SrcClass;

public class GwtpActionWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    private final List<SrcPackage> packages;
    private final List<SrcClass> handlerModule;
    private final SrcPackage selPackage;
    
    public GwtpActionWizardPanel1(List<SrcPackage> packages, 
            List<SrcClass> handlerModules, SrcPackage abstractActionPackage) {
        this.packages = packages;
        this.handlerModule = handlerModules;
        this.selPackage = abstractActionPackage;
    }

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GwtpActionVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GwtpActionVisualPanel1 getComponent() {
        if (component == null) {
            component = new GwtpActionVisualPanel1(packages, handlerModule, 
                    selPackage);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(PropertyKeys.HandlerPackage.name(), 
                component.getSelActionHandler());
        wiz.putProperty(PropertyKeys.HandlerModule.name(), 
                component.getSelHandlerModule());
    }
}
