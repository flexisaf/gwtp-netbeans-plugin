/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.gwtp.presenter;

import javax.swing.event.ChangeListener;
import org.netbeans.modules.gwtp.util.PresenterType;
import org.netbeans.modules.gwtp.util.PropertyKeys;
import org.netbeans.modules.gwtp.util.RevealType;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class GwtpPresenterWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GwtpPresenterVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GwtpPresenterVisualPanel1 getComponent() {
        if (component == null) {
            component = new GwtpPresenterVisualPanel1();
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
        wiz.putProperty(PropertyKeys.PresenterType.name(), getPresenterType());
        wiz.putProperty(PropertyKeys.RevealType.name(), component.getRevealType());
        wiz.putProperty(PropertyKeys.SlotName.name(), component.getSlot());
        wiz.putProperty(PropertyKeys.IsSingleton.name(), component.isSingleton());
        wiz.putProperty(PropertyKeys.IsOverrideDefPopPanel.name(), 
                component.isOverrideDefPopPanel());
        wiz.putProperty(PropertyKeys.IsaPlace.name(), component.isaPlace());        
        wiz.putProperty(PropertyKeys.NameToken.name(), component.getNameToken());
        wiz.putProperty(PropertyKeys.IsCrawlable.name(), component.isCrawlable());
        wiz.putProperty(PropertyKeys.IsCodeSplit.name(), component.isCodeSplit());
        wiz.putProperty(PropertyKeys.IsAddUiHandlers.name(), component.isAddUiHandlers());
        wiz.putProperty(PropertyKeys.IsAddOnBind.name(), component.isAddOnBind());
        wiz.putProperty(PropertyKeys.IsAddOnHide.name(), component.isAddOnHide());
        wiz.putProperty(PropertyKeys.IsAddOnReset.name(), component.isAddOnReset());
        wiz.putProperty(PropertyKeys.IsAddOnUnbind.name(), component.isAddOnUnbind());
        wiz.putProperty(PropertyKeys.IsUseManualReveal.name(), component.isUseManualReveal());
    }
//
//    private boolean isOverrideDefPopupPanel() {
//        return component.isOverrideDefPopPanel();
//    }
//    
//    private boolean isSingleton() {
//        return component.isSingleton();
//    }
//    
//    private RevealType getRevealType() {
//        return component.getRevealType();
//    }
//    
//    private String getSlot() {
//        return component.getSlot();
//    }
//    
    private PresenterType getPresenterType() {
        int selTab = ((GwtpPresenterVisualPanel1) component).getSelectedTab();
        
        switch (selTab) {
            case 0: 
                return PresenterType.Nested;
            case 1:
                return PresenterType.PresenterWidget;
            case 2:
                return PresenterType.Popup;
        }
        
        return PresenterType.Nested;
    }
    
}
