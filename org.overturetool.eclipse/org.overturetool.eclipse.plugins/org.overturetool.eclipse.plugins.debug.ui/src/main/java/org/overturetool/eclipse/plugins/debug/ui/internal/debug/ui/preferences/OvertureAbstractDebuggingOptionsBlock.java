package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.debug.ui.preferences.ScriptDebugPreferencesMessages;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;


public abstract class OvertureAbstractDebuggingOptionsBlock extends 
AbstractOptionsBlock {

	private static String DLTK_DEBUG_PREF_PAGE_ID = "org.eclipse.dltk.preferences.debug"; //$NON-NLS-1$

	public OvertureAbstractDebuggingOptionsBlock(IStatusChangeListener context,
			IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	/*
	 * @see org.eclipse.dltk.ui.preferences.AbstractOptionsBlock#createOptionsBlock(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createOptionsBlock(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent
				.getFont(), 1, 1, GridData.FILL_HORIZONTAL);

		createSettingsGroup(composite);
		//createVariablesGroup(composite);

		if (isProjectPreferencePage()) {
			createPropToPrefLink(composite,
					ScriptDebugPreferencesMessages.LinkToGlobalDebugOptions,
					DLTK_DEBUG_PREF_PAGE_ID, null);
		} else {
			createPrefLink(composite,
					ScriptDebugPreferencesMessages.LinkToGlobalDebugOptions,
					DLTK_DEBUG_PREF_PAGE_ID, null);
		}

		return composite;
	}

	/**
	 * Returns the 'break on first line' preference key
	 */
	//protected abstract PreferenceKey getBreakOnFirstLineKey();

	/**
	 * Returns the 'dbgp logging enabled' preference key
	 */
	protected abstract PreferenceKey getDbgpLoggingEnabledKey();
	
	protected abstract PreferenceKey getDebugFromConsoleKey();

//	/**
//	 * Returns the 'show global variables' preference key
//	 */
//	protected abstract PreferenceKey getShowGlobalVarsKey();
//
//	/**
//	 * Returns the 'show class variables' preference key
//	 */
//	protected abstract PreferenceKey getShowClassVarsKey();
//
//	/**
//	 * Returns the 'show local variables' preference key
//	 */
//	protected abstract PreferenceKey getShowLocalVarsKey();

	private void createSettingsGroup(Composite parent) {
		final Group group = SWTFactory.createGroup(parent,
				ScriptDebugPreferencesMessages.EngineSettingsLabel, 1, 1,
				GridData.FILL_HORIZONTAL);

		// Break on first line
		//Button b = SWTFactory.createCheckButton(group,
			//	ScriptDebugPreferencesMessages.BreakOnFirstLineLabel, null,
				//false, 1);

		//bindControl(b, getBreakOnFirstLineKey(), null);

		// Enable dbgp logging
		Button b = SWTFactory.createCheckButton(group,
				ScriptDebugPreferencesMessages.EnableDbgpLoggingLabel, null,
				false, 1);
		bindControl(b, getDbgpLoggingEnabledKey(), null);
		
		//b = SWTFactory.createCheckButton(group,
			//	OvertureDebugConstants.DEBUG_FROM_CONSOLE, null,
				//false, 1);
		//bindControl(b, getDebugFromConsoleKey(), null);
		
		
	}

//	private void createVariablesGroup(Composite parent) {
//		final Group group = SWTFactory.createGroup(parent,
//				ScriptDebugPreferencesMessages.VariableSettingsLabel, 1, 1,
//				GridData.FILL_HORIZONTAL);
//
//		// global variables
//		Button b = SWTFactory.createCheckButton(group,
//				ScriptDebugPreferencesMessages.ShowGlobalVarsLabel, null,
//				false, 1);
//		bindControl(b, getShowGlobalVarsKey(), null);
//
//		// class variables
//		b = SWTFactory.createCheckButton(group,
//				ScriptDebugPreferencesMessages.ShowClassVarsLabel, null, false,
//				1);
//		bindControl(b, getShowClassVarsKey(), null);
//
//		// local variables
//		b = SWTFactory.createCheckButton(group,
//				ScriptDebugPreferencesMessages.ShowLocalVarsLabel, null, false,
//				1);
//		bindControl(b, getShowLocalVarsKey(), null);
//	}
	
	
	

}
