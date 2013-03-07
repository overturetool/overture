/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.traces;

import org.overture.ide.core.ICoreConstants;

public interface ITracesConstants
{
	public final String TRACES_VIEW_ID = "org.overture.ide.plugins.traces.views.TracesView";
	public final String TRACES_TEST_ID = "org.overture.ide.plugins.traces.views.TraceTest";
	public final String PLUGIN_ID = "org.overture.ide.plugins.traces";
	
	//String REMOTE_DEBUG = "remote_debug";
	public boolean DEBUG = false;
//	public final String ENABLE_DEBUGGING_INFO_PREFERENCE = "ct_enable_debugging_info";
	public final String REMOTE_DEBUG_PREFERENCE = "ct_enable_remote_debug";
	public final String REMOTE_DEBUG_FIXED_PORT = "ct_enable_remote_debug_fixed_port";

	/**
	 * This string gives the plugin id that contains the core ctruntime jar file.
	 */
	public static final String[] TEST_ENGINE_BUNDLE_IDs ={PLUGIN_ID,ICoreConstants.PLUGIN_ID};
	public static final String TEST_ENGINE_CLASS = "org.overturetool.ct.ctruntime.TraceRunnerMain";
}
