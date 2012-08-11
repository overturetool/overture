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
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileViewerPlugin.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class TracefileViewerPlugin extends AbstractUIPlugin
{

    protected static final String PLUGIN_ID = "org.overture.ide.plugins.showtrace";

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
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.overture.ide.plugins.showtrace", path);
    }

    private static TracefileViewerPlugin plugin;
}