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
package org.overture.ide.plugins.combinatorialtesting;

import org.overture.ide.builders.vdmj.IBuilderVdmjConstants;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.debug.core.IDebugConstants;

public interface ITracesConstants
{
	public static final String TRACES_CONSOLE = "TracesConsole";
	
	public final String TRACES_VIEW_ID = "org.overture.ide.plugins.combinatorialtesting.views.TracesView";
	public final String TRACES_TEST_ID = "org.overture.ide.plugins.combinatorialtesting.views.TraceTest";
	public final String PLUGIN_ID = "org.overture.ide.plugins.combinatorialtesting";
	public final int TRACE_FILTERING_DEFAULT_SEED = 999;
	public final String TRACE_REDUCTION_DEFAULT_TYPE = "Random";
	public final int TRACE_SUBSET_LIMITATION_DEFAULT = 100;

	// String REMOTE_DEBUG = "remote_debug";
	public boolean DEBUG = false;
	// public final String ENABLE_DEBUGGING_INFO_PREFERENCE = "ct_enable_debugging_info";
	public final String REMOTE_DEBUG_PREFERENCE = "ct_enable_remote_debug";
	public final String REMOTE_DEBUG_FIXED_PORT = "ct_enable_remote_debug_fixed_port";
	public final String TRACE_SEED = "ct_trace_filter_seed";
	public final String TRACE_REDUCTION_TYPE = "ct_trace_reduction_type";
	public final String TRACE_SUBSET_LIMITATION = "ct_subset_limitation";
	public final String SORT_VIEW = "SORT_VIEW";

	/**
	 * This string gives the plugin id that contains the core ctruntime jar file.
	 */
	public static final String[] TEST_ENGINE_BUNDLE_IDs = { PLUGIN_ID,
			ICoreConstants.PLUGIN_ID, IDebugConstants.PLUGIN_ID,
			IBuilderVdmjConstants.PLUGIN_ID };
	public static final String TEST_ENGINE_CLASS = "org.overture.ct.ctruntime.TraceRunnerMain";
}
