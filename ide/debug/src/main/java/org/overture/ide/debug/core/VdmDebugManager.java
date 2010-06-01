package org.overture.ide.debug.core;

import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.debug.core.model.IVdmTypeFactory;
import org.overture.ide.debug.core.model.internal.VariableNameComparator;
import org.overture.ide.debug.core.model.internal.VdmTypeFactory;

public class VdmDebugManager {
	private static final String VDM_DEBUG_MODEL_EXT_POINT = VdmDebugPlugin.PLUGIN_ID
			+ ".VdmDebugModel"; //$NON-NLS-1$
	private static final String NATURE_ID = "natureId"; //$NON-NLS-1$
	private static final String DEBUG_MODEL_ID = "debugModelId"; //$NON-NLS-1$
	private static final String TYPE_FACTORY = "typeFactory"; //$NON-NLS-1$
	private static final String VARIABLE_NAME_COMPARATOR = "variableNameComparator"; //$NON-NLS-1$
	private static final String DEBUG_TOOLKIT = "debugToolkit"; //$NON-NLS-1$

	private static VdmDebugManager instance;
	private static VdmTypeFactory typeFactory = null;
	
	
	public static synchronized VdmDebugManager getInstance() {
		if (instance == null) {
			instance = new VdmDebugManager();
		}

		return instance;
	}

	private final HashMap natureToInfoMap = new HashMap();
	private final HashMap modelToNatureMap = new HashMap();

	private static class Info {
		public final String debugModelId;
		public final IVdmTypeFactory typeFactory;
		public final Comparator comparator;
//		public final IDLTKDebugToolkit debugToolkit;

		public Info(String debugModelId, IVdmTypeFactory typeFactory,
				 Comparator comparator) {
			this.debugModelId = debugModelId;
			this.typeFactory = typeFactory;
			
			this.comparator = comparator;
		}
	}

	public Comparator getVariableNameComparator() {
		return new VdmVariableNameComparator();
	}
	
	public IVdmTypeFactory getTypeFactory() {
		
		if(typeFactory == null){
			typeFactory = new VdmTypeFactory();
		}
		return typeFactory;
	}
	
	private void loadExtenstionPoints() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtension[] extensions = registry.getExtensionPoint(
				VDM_DEBUG_MODEL_EXT_POINT).getExtensions();

		for (int i = 0; i < extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] elements = extension
					.getConfigurationElements();

			if (elements.length > 0) {
				IConfigurationElement element = elements[0];
				final String natureId = element.getAttribute(NATURE_ID);
				final String debugModelId = element
						.getAttribute(DEBUG_MODEL_ID);

				IVdmTypeFactory typeFactory = null;

				try {
					typeFactory = (IVdmTypeFactory) element
							.createExecutableExtension(TYPE_FACTORY);
				} catch (CoreException e) {
					VdmDebugPlugin.log(e);
				}

				Comparator comparator = null;
				String comparatorId = element
						.getAttribute(VARIABLE_NAME_COMPARATOR);
				if (comparatorId != null) {
					try {
						comparator = (Comparator) element
								.createExecutableExtension(VARIABLE_NAME_COMPARATOR);
					} catch (CoreException e) {
						VdmDebugPlugin.log(e);
					}
				}
				if (comparator == null) {
					comparator = new VariableNameComparator();
				}
////				IDLTKDebugToolkit debugToolkit = null;
//				if (element.getAttribute(DEBUG_TOOLKIT) != null) {
//					try {
//						debugToolkit = (IDLTKDebugToolkit) element
//								.createExecutableExtension(DEBUG_TOOLKIT);
//					} catch (Exception e) {
//						DLTKDebugPlugin.log(e);
//					}
//				}
//				if (debugToolkit == null) {
//					debugToolkit = new DefaultDebugToolkit();
//				}

				if (natureId != null && debugModelId != null) {
					natureToInfoMap.put(natureId, new Info(debugModelId,
							typeFactory, comparator));
					modelToNatureMap.put(debugModelId, natureId);
				}
			}
			
		}
		
		
		
	}

//	private static class DefaultDebugToolkit extends AbstractVdmDebugToolkit {
//
//	}
//
//	protected Info getInfo(String natureId) {
//		return (Info) natureToInfoMap.get(natureId);
//	}
//
//	protected VdmDebugManager() {
//		natureToInfoMap = new HashMap();
//		modelToNatureMap = new HashMap();
//
//		loadExtenstionPoints();
//	}
//
//	public String getNatureByDebugModel(String debugModelId) {
//		return (String) modelToNatureMap.get(debugModelId);
//	}
//
//	public String getDebugModelByNature(String natureId) {
//		return getInfo(natureId).debugModelId;
//	}
//
//	public IVdmTypeFactory getTypeFactoryByNature(String natureId) {
//		return getInfo(natureId).typeFactory;
//	}
//
//	public IVdmTypeFactory getTypeFactoryByDebugModel(String debugModelId) {
//		return getTypeFactoryByNature(getNatureByDebugModel(debugModelId));
//	}
//
//	
//
//	public Comparator getVariableNameComparatorByDebugModel(String debugModelId) {
//		return getVariableNameComparatorByNature(getNatureByDebugModel(debugModelId));
//	}
//
//	public IDLTKDebugToolkit getDebugToolkitByNature(String natureId) {
//		return getInfo(natureId).debugToolkit;
//	}
//
//	public IDLTKDebugToolkit getDebugToolkitByDebugModel(String debugModelId) {
//		return getDebugToolkitByNature(getNatureByDebugModel(debugModelId));
//	}
}
