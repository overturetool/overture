package org.overture.ide.vdmpp.ui;


import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.vdmpp.core.VdmPpLanguageToolkit;
import org.overture.ide.vdmpp.ui.internal.editor.VdmppEditorConstants;
//public class VdmPpUILanguageToolkit extends AbstractDLTKUILanguageToolkit
//{
//
//	public IDLTKLanguageToolkit getCoreToolkit()
//	{
//		return VdmPpLanguageToolkit.getDefault();
//	}
//
//	public IPreferenceStore getPreferenceStore()
//	{
//		return UIPlugin.getDefault().getPreferenceStore();
//	}
//
//}
public class VdmPpUILanguageToolkit implements IDLTKUILanguageToolkit {
    
    private static VdmPpUILanguageToolkit sToolkit = null;
    
    public static IDLTKUILanguageToolkit getInstance() {
        if (sToolkit == null)
            sToolkit = new VdmPpUILanguageToolkit();
        return sToolkit;
    }
    
    public IPreferenceStore getPreferenceStore() {
        return UIPlugin.getDefault().getPreferenceStore();
    }
    
    public IDLTKLanguageToolkit getCoreToolkit() {
        return VdmPpLanguageToolkit.getDefault();
    }
    
    public IDialogSettings getDialogSettings() {
        return UIPlugin.getDefault().getDialogSettings();
    }
    
//    public String getPartitioningId() {
//        return HelloPartitions.HELLO_PARTITIONING;
//    }
    
    public String getEditorId(Object inputElement) {
        return VdmppEditorConstants.EDITOR_ID;//"org.overutretool.ide.vdmpp.ui.internal.editor";
    }
    
    public String getInterpreterContainerId() {
        return "com.yoursway.hello.launching.INTERPRETER_CONTAINER";
    }
    
    public ScriptUILabelProvider createScriptUILabelProvider() {
        return null;
    }
    
    public boolean getProvideMembers(ISourceModule element) {
        return true;
    }
    
//    public ScriptTextTools getTextTools() {
//        return UIPlugin.getDefault().getTextTools();
//    }
    
    public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
//        return new SimpleHelloSourceViewerConfiguration(getTextTools().getColorManager(),
//                getPreferenceStore(), null, getPartitioningId(), false);
    	return null;
    }
    
    private static final String INTERPRETERS_PREFERENCE_PAGE_ID = "com.yoursway.hello.preferences.interpreters";
    private static final String DEBUG_PREFERENCE_PAGE_ID = null;//"org.eclipse.dltk.ruby.preferences.debug";
    
    public String getInterpreterPreferencePage() {
        return INTERPRETERS_PREFERENCE_PAGE_ID;
    }
    
    public String getDebugPreferencePage() {
        return DEBUG_PREFERENCE_PAGE_ID;
    }
    
    public String[] getEditorPreferencePages() {
        return new String[0];
    }
    
    public ScriptElementLabels getScriptElementLabels() {
        return null;
    }
    
    public IPreferenceStore getCombinedPreferenceStore() {
        // TODO Auto-generated method stub
        return null;
    }

	public String getPartitioningId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ScriptTextTools getTextTools()
	{
		// TODO Auto-generated method stub
		return null;
	}
    
}
