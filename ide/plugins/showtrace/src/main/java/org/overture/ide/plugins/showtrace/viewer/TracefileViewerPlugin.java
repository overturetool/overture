// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileViewerPlugin.java

package org.overture.ide.plugins.showtrace.viewer;

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
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.overturetool.eclipse.plugins.showtrace.viewer", path);
    }

    private static TracefileViewerPlugin plugin;
}