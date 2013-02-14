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
package org.overture.ide.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;

public class LexicalSortingAction extends Action {

	private VdmOutlineViewer fOutlineViewer = null;
	private OutlineSorter fOutlineSorter = null;
//	private boolean isChecked = false;
	private String fPreferenceKey;
	
	public LexicalSortingAction(VdmOutlineViewer fOutlineViewer) {
		this.fPreferenceKey = "LexicalSortingAction.isChecked";
		this.fOutlineViewer = fOutlineViewer;
		this.fOutlineSorter = new OutlineSorter();
		boolean checked = VdmUIPlugin.getDefault().getPreferenceStore().getBoolean(fPreferenceKey);
		valueChecked(checked, false);		
	}

	@Override
	public ImageDescriptor getImageDescriptor() {	
		return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_ALPHA_SORTING);
	}
	
	@Override
	public void run() {
		valueChecked(isChecked(),true);
	}

	private void valueChecked(boolean on, boolean store) {
		
		setChecked(on);
		
		if(on){
			fOutlineViewer.setComparator(fOutlineSorter);
		}
		else{
			fOutlineViewer.setComparator(null);
		}
		
		if(store)
		{
			VdmUIPlugin.getDefault().getPreferenceStore().setValue(fPreferenceKey, on);
		}
	}
	

}
