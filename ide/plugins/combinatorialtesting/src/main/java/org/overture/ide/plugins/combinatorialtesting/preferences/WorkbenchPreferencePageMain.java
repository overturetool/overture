/*
 * #%~
 * Combinatorial Testing
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.combinatorialtesting.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.interpreter.traces.TraceReductionType;

public class WorkbenchPreferencePageMain extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{

	private ComboFieldEditor createTraceReductionTypeCombo()
	{
		TraceReductionType[] values = TraceReductionType.values();
		List<TraceReductionType> toShow = new ArrayList<TraceReductionType>();

		for (int i = 0; i < values.length; i++)
		{
			if (values[i] != TraceReductionType.NONE)
			{
				toShow.add(values[i]);
			}
		}

		final String[][] contents = new String[toShow.size()][1];

		for (int i = 0; i < toShow.size(); i++)
		{

			TraceReductionType current = toShow.get(i);
			contents[i] = new String[] { current.getDisplayName(),
					"" + current.ordinal() };
		}

		final ComboFieldEditor traceReductionType = new ComboFieldEditor(ITracesConstants.TRACE_REDUCTION_TYPE, "Trace reduction type", contents, getFieldEditorParent());

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
		
		addField(new StringFieldEditor(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION, "Additional VM arguments:", getFieldEditorParent()));
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
