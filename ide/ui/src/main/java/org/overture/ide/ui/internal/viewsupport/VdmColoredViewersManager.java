/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.internal.viewsupport;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

public class VdmColoredViewersManager implements IPropertyChangeListener {

	public static final String INHERITED_COLOR_NAME= "org.eclipse.jdt.ui.ColoredLabels.inherited"; //$NON-NLS-1$

	public static final String HIGHLIGHT_BG_COLOR_NAME= "org.eclipse.jdt.ui.ColoredLabels.match_highlight"; //$NON-NLS-1$
	public static final String HIGHLIGHT_WRITE_BG_COLOR_NAME= "org.eclipse.jdt.ui.ColoredLabels.writeaccess_highlight"; //$NON-NLS-1$

	private static VdmColoredViewersManager fgInstance= new VdmColoredViewersManager();

	private Set<VdmColoringLabelProvider> fManagedLabelProviders;

	public VdmColoredViewersManager() {
		fManagedLabelProviders= new HashSet<VdmColoringLabelProvider>();
	}

	public void installColoredLabels(VdmColoringLabelProvider labelProvider) {
		if (fManagedLabelProviders.contains(labelProvider))
			return;

		if (fManagedLabelProviders.isEmpty()) {
			// first lp installed
			PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
			JFaceResources.getColorRegistry().addListener(this);
		}
		fManagedLabelProviders.add(labelProvider);
	}

	public void uninstallColoredLabels(VdmColoringLabelProvider labelProvider) {
		if (!fManagedLabelProviders.remove(labelProvider))
			return; // not installed

		if (fManagedLabelProviders.isEmpty()) {
			PlatformUI.getPreferenceStore().removePropertyChangeListener(this);
			JFaceResources.getColorRegistry().removeListener(this);
			// last viewer uninstalled
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property= event.getProperty();
		if (property.equals(JFacePreferences.QUALIFIER_COLOR)
				|| property.equals(JFacePreferences.COUNTER_COLOR)
				|| property.equals(JFacePreferences.DECORATIONS_COLOR)
				|| property.equals(HIGHLIGHT_BG_COLOR_NAME)
				|| property.equals(HIGHLIGHT_WRITE_BG_COLOR_NAME)
				|| property.equals(INHERITED_COLOR_NAME)
				|| property.equals(IWorkbenchPreferenceConstants.USE_COLORED_LABELS)
		) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					updateAllViewers();
				}
			});
		}
	}

	protected final void updateAllViewers() {
		for (Iterator<VdmColoringLabelProvider> iterator= fManagedLabelProviders.iterator(); iterator.hasNext();) {
			VdmColoringLabelProvider lp= (VdmColoringLabelProvider) iterator.next();
			lp.update();
		}
	}

	public static boolean showColoredLabels() {
		return PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
	}

	public static void install(VdmColoringLabelProvider labelProvider) {
		fgInstance.installColoredLabels(labelProvider);
	}

	public static void uninstall(VdmColoringLabelProvider labelProvider) {
		fgInstance.uninstallColoredLabels(labelProvider);
	}

}
