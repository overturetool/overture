//package org.eclipse.dltk.internal.debug.core.model;
//
//import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
//import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
//import org.eclipse.dltk.debug.core.ISmartStepEvaluator;
//
//public class SmartStepEvaluatorManager {
//	private static final String SMART_STEP_EXTENSION = DLTKDebugPlugin.PLUGIN_ID
//			+ ".smartStepEvaluator"; //$NON-NLS-1$
//	private static PriorityClassDLTKExtensionManager manager = new PriorityClassDLTKExtensionManager(
//			SMART_STEP_EXTENSION, "nature"); //$NON-NLS-1$
//	
//	public static ISmartStepEvaluator getEvaluator(String nature) {
//		return (ISmartStepEvaluator) manager.getObject(nature);
//	}
//} 
