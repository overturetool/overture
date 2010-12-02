package org.overture.ide.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;

public class LexicalSortingAction extends Action {

	private VdmOutlineViewer fOutlineViewer = null;
	private OutlineSorter fOutlineSorter = null;
	private boolean isChecked = false;
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
//		int a = getStyle();
//		
//		
//		boolean t = isChecked();
//		
//		isChecked = !isChecked;
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
