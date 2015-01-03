/*
 * 02/01/2015
 */
package org.netbeans.modules.gwtp.util;

/**
 *
 * @author faiz
 */
public enum Constants {
    AbstractAction;
    
    public static String HANDLER_MODULE_CONFIG = "void configureHandlers()";
    public static String APP_MODULE_CONFIG = "void configure()";
    public static String GUICE_IMPORT_STMT = "com.gwtplatform.dispatch.rpc.server.guice";
    public static String GIN_IMPORT_STMT = "com.gwtplatform.mvp.client.gin";
    public static String APP_MODULE = "extends AbstractPresenterModule";
}
