package org.overture.ide.plugins.combinatorialtesting.preferences;


import java.util.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.interpreter.traces.TraceReductionType;

public class WorkbenchPreferencePageMain  extends FieldEditorPreferencePage implements
IWorkbenchPreferencePage {

	private ComboFieldEditor createTraceReductionTypeCombo()
	{
		TraceReductionType[] values = TraceReductionType.values();
		List<TraceReductionType> toShow = new ArrayList<TraceReductionType>();
		
		for(int i = 0; i < values.length; i++)
		{
			if(values[i] != TraceReductionType.NONE)
			{
				toShow.add(values[i]);
			}
		}
		
		final String[][] contents = new String[toShow.size()][1];
			
		for ( int i = 0; i < toShow.size(); i++) {
			
			TraceReductionType current = toShow.get(i);
			contents[i] = new String[]{current.getDisplayName(), "" + current.ordinal()};
		}
		
		final ComboFieldEditor traceReductionType = new ComboFieldEditor(ITracesConstants.TRACE_REDUCTION_TYPE , "Trace reduction type" , contents, getFieldEditorParent());

		return traceReductionType;
	}
	
	@Override
	protected void createFieldEditors()
	{
		addField(new BooleanFieldEditor(ITracesConstants.REMOTE_DEBUG_PREFERENCE, "Enable remote debug", getFieldEditorParent()));
		addField(new BooleanFieldEditor(ITracesConstants.REMOTE_DEBUG_FIXED_PORT, "Use fixed port for remote debug", getFieldEditorParent()));
				
		addField(createTraceReductionTypeCombo());
		
		IntegerFieldEditor traceFilteringSeed = new IntegerFieldEditor(ITracesConstants.TRACE_SEED, "Trace filtering seed", getFieldEditorParent());
		traceFilteringSeed.setValidRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
		addField(traceFilteringSeed);
		
		IntegerFieldEditor subsetLimitation = new IntegerFieldEditor(ITracesConstants.TRACE_SUBSET_LIMITATION, "Subset limitation (%)", getFieldEditorParent());
		subsetLimitation.setValidRange(1, 100);
		addField(subsetLimitation);
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return OvertureTracesPlugin.getDefault().getPreferenceStore();
	}
	
	@Override
	protected void performDefaults()
	{
		super.performDefaults();
		IPreferenceStore store = getPreferenceStore();
		OvertureTracesPlugin.initializeDefaultMainPreferences(store);
	}

	public void init(IWorkbench workbench)
	{
		IPreferenceStore store = getPreferenceStore();
		OvertureTracesPlugin.initializeDefaultMainPreferences(store);
	}

}
