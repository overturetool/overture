/*
 * #%~
 * RT Trace Viewer Plugin
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
package org.overture.ide.plugins.rttraceviewer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class TracefileViewerPlugin extends AbstractUIPlugin
{

    public static final String PLUGIN_ID = IRealTimeTaceViewer.PLUGIN_ID;;

	public TracefileViewerPlugin()
    {
        plugin = this;
    }

    @Override
	public void start(BundleContext context)
        throws Exception
    {
        super.start(context);
    }

    @Override
	public void stop(BundleContext context)
        throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    public static TracefileViewerPlugin getDefault()
    {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    private static TracefileViewerPlugin plugin;
    
    
	public static void log(Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, IRealTimeTaceViewer.PLUGIN_ID, "RealTimeTraceViewerPlugin", exception));
	}

	public static void log(String message, Exception exception)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, IRealTimeTaceViewer.PLUGIN_ID, message, exception));
	}
}
