/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core;

import java.util.Comparator;

import org.overture.ide.debug.core.model.IVdmTypeFactory;
import org.overture.ide.debug.core.model.internal.VdmTypeFactory;

public class VdmDebugManager
{
	// private static final String VDM_DEBUG_MODEL_EXT_POINT = VdmDebugPlugin.PLUGIN_ID
	//			+ ".VdmDebugModel"; //$NON-NLS-1$
	//	private static final String NATURE_ID = "natureId"; //$NON-NLS-1$
	//	private static final String DEBUG_MODEL_ID = "debugModelId"; //$NON-NLS-1$
	//	private static final String TYPE_FACTORY = "typeFactory"; //$NON-NLS-1$
	//	private static final String VARIABLE_NAME_COMPARATOR = "variableNameComparator"; //$NON-NLS-1$
	//	private static final String DEBUG_TOOLKIT = "debugToolkit"; //$NON-NLS-1$

	private static VdmDebugManager instance;
	private static VdmTypeFactory typeFactory = null;

	public static synchronized VdmDebugManager getInstance()
	{
		if (instance == null)
		{
			instance = new VdmDebugManager();
		}

		return instance;
	}

	// private final Map<String,Info> natureToInfoMap = new HashMap<String,Info>();
	// private final Map<String,String> modelToNatureMap = new HashMap<String,String>();

	// private static class Info {
	// public final String debugModelId;
	// public final IVdmTypeFactory typeFactory;
	// public final Comparator comparator;
	// // public final IDLTKDebugToolkit debugToolkit;
	//
	// public Info(String debugModelId, IVdmTypeFactory typeFactory,
	// Comparator comparator) {
	// this.debugModelId = debugModelId;
	// this.typeFactory = typeFactory;
	//
	// this.comparator = comparator;
	// }
	// }

	public Comparator<Object> getVariableNameComparator()
	{
		return new VdmVariableNameComparator();
	}

	public IVdmTypeFactory getTypeFactory()
	{

		if (typeFactory == null)
		{
			typeFactory = new VdmTypeFactory();
		}
		return typeFactory;
	}

	// private void loadExtenstionPoints() {
	// IExtensionRegistry registry = Platform.getExtensionRegistry();
	// IExtension[] extensions = registry.getExtensionPoint(
	// VDM_DEBUG_MODEL_EXT_POINT).getExtensions();
	//
	// for (int i = 0; i < extensions.length; i++) {
	// IExtension extension = extensions[i];
	// IConfigurationElement[] elements = extension
	// .getConfigurationElements();
	//
	// if (elements.length > 0) {
	// IConfigurationElement element = elements[0];
	// final String natureId = element.getAttribute(NATURE_ID);
	// final String debugModelId = element
	// .getAttribute(DEBUG_MODEL_ID);
	//
	// IVdmTypeFactory typeFactory = null;
	//
	// try {
	// typeFactory = (IVdmTypeFactory) element
	// .createExecutableExtension(TYPE_FACTORY);
	// } catch (CoreException e) {
	// VdmDebugPlugin.log(e);
	// }
	//
	// Comparator comparator = null;
	// String comparatorId = element
	// .getAttribute(VARIABLE_NAME_COMPARATOR);
	// if (comparatorId != null) {
	// try {
	// comparator = (Comparator) element
	// .createExecutableExtension(VARIABLE_NAME_COMPARATOR);
	// } catch (CoreException e) {
	// VdmDebugPlugin.log(e);
	// }
	// }
	// if (comparator == null) {
	// comparator = new VariableNameComparator();
	// }
	// //// IDLTKDebugToolkit debugToolkit = null;
	// // if (element.getAttribute(DEBUG_TOOLKIT) != null) {
	// // try {
	// // debugToolkit = (IDLTKDebugToolkit) element
	// // .createExecutableExtension(DEBUG_TOOLKIT);
	// // } catch (Exception e) {
	// // DLTKDebugPlugin.log(e);
	// // }
	// // }
	// // if (debugToolkit == null) {
	// // debugToolkit = new DefaultDebugToolkit();
	// // }
	//
	// if (natureId != null && debugModelId != null) {
	// natureToInfoMap.put(natureId, new Info(debugModelId,
	// typeFactory, comparator));
	// modelToNatureMap.put(debugModelId, natureId);
	// }
	// }
	//
	// }
	//
	//
	//
	// }

	// private static class DefaultDebugToolkit extends AbstractVdmDebugToolkit {
	//
	// }
	//
	// protected Info getInfo(String natureId) {
	// return (Info) natureToInfoMap.get(natureId);
	// }
	//
	// protected VdmDebugManager() {
	// natureToInfoMap = new HashMap();
	// modelToNatureMap = new HashMap();
	//
	// loadExtenstionPoints();
	// }
	//
	// public String getNatureByDebugModel(String debugModelId) {
	// return (String) modelToNatureMap.get(debugModelId);
	// }
	//
	// public String getDebugModelByNature(String natureId) {
	// return getInfo(natureId).debugModelId;
	// }
	//
	// public IVdmTypeFactory getTypeFactoryByNature(String natureId) {
	// return getInfo(natureId).typeFactory;
	// }
	//
	// public IVdmTypeFactory getTypeFactoryByDebugModel(String debugModelId) {
	// return getTypeFactoryByNature(getNatureByDebugModel(debugModelId));
	// }
	//
	//
	//
	// public Comparator getVariableNameComparatorByDebugModel(String debugModelId) {
	// return getVariableNameComparatorByNature(getNatureByDebugModel(debugModelId));
	// }
	//
	// public IDLTKDebugToolkit getDebugToolkitByNature(String natureId) {
	// return getInfo(natureId).debugToolkit;
	// }
	//
	// public IDLTKDebugToolkit getDebugToolkitByDebugModel(String debugModelId) {
	// return getDebugToolkitByNature(getNatureByDebugModel(debugModelId));
	// }
}
