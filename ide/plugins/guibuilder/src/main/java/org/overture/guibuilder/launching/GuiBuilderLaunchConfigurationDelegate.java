package org.overture.guibuilder.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.overture.ide.debug.core.launching.VdmLaunchConfigurationDelegate;

/**
 * Launch Delegate for Gui Builder. Inherits from {@link VdmLaunchConfigurationDelegate}
 * and adds the GuiBuilder class to the classpath before launching.
 * @author ldc
 *
 */
public class GuiBuilderLaunchConfigurationDelegate extends
		VdmLaunchConfigurationDelegate {

	/**
	 * Add Gui Builder to the list of Bundles to be used
	 * when computing the classpath.
	 */
	@Override
	protected String[] getDebugEngineBundleIds() {
		List<String> r = new ArrayList<String>(Arrays.asList(super
				.getDebugEngineBundleIds()));

		r.add(IGuiBuilderConstants.GUI_BUILDER_BUNDLE_ID);

		return r.toArray((new String[] {}));
	}

}
