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
//package org.overture.ide.debug.internal.ui.views.launch;
//
//
//
//import org.eclipse.debug.internal.core.IInternalDebugCoreConstants;
//import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
//import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
//import org.eclipse.jface.action.Action;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.PlatformUI;
//
///**
// * Action that controls the view mode for Debug view (auto vs. breadcrumb, vs. 
// * tree view). 
// * 
// * @since 3.5
// */
//class DebugViewModeAction extends Action {
//
//	private final VdmLaunchView fLaunchView;
//	private final Composite fParent;
//	private final String fMode;
//
//	/**
//	 * Creates a new action to set the debug view mode.
//	 * 
//	 * @param view Reference to the debug view.
//	 * @param mode The mode to be set by this action.
//	 * @param parent The view's parent control used to calculate view size
//     * in auto mode.
//	 */
//	public DebugViewModeAction(VdmLaunchView view, String mode, Composite parent) {
//		super(IInternalDebugCoreConstants.EMPTY_STRING, AS_RADIO_BUTTON);
//		fLaunchView = view;
//		fParent = parent;
//		fMode = mode;
//				
//		if (mode == IDebugPreferenceConstants.DEBUG_VIEW_MODE_AUTO) {
//			setText(LaunchViewMessages.DebugViewModeAction_Auto_label);
//			setToolTipText(LaunchViewMessages.DebugViewModeAction_Auto_tooltip);  
//			setDescription(LaunchViewMessages.DebugViewModeAction_Auto_description);  
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.DEBUG_VIEW_MODE_AUTO_ACTION);
//		} else if (mode == IDebugPreferenceConstants.DEBUG_VIEW_MODE_FULL) {
//			setText(LaunchViewMessages.DebugViewModeAction_Full_label);  
//			setToolTipText(LaunchViewMessages.DebugViewModeAction_Full_tooltip);  
//			setDescription(LaunchViewMessages.DebugViewModeAction_Full_description);  
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.DEBUG_VIEW_MODE_FULL_ACTION);
//		} else {
//			setText(LaunchViewMessages.DebugViewModeAction_Compact_label);
//			setToolTipText(LaunchViewMessages.DebugViewModeAction_Compact_tooltip);  
//			setDescription(LaunchViewMessages.DebugViewModeAction_Compact_description);  
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.DEBUG_VIEW_MODE_COMPACT_ACTION);
//		} 		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.action.IAction#run()
//	 */
//	public void run() {
//		fLaunchView.setViewMode(fMode, fParent); 
//	}	
//	
//	/**
//	 * Returns the view mode set by this action.
//	 */
//	public String getMode() {
//	    return fMode;
//	}
//}

