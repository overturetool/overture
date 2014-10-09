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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFieldField;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmElementImageProvider {
	/**
	 * Flags for the JavaImageLabelProvider: Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS = 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS = 0x2;

	/**
	 * Use the 'light' style for rendering types.
	 */
	public final static int LIGHT_TYPE_ICONS = 0x4;

	public static final Point SMALL_SIZE = new Point(16, 16);
	public static final Point BIG_SIZE = new Point(22, 16);



	private ImageDescriptorRegistry fRegistry;

	public VdmElementImageProvider() {
		fRegistry = null; // lazy initialization
	}

	/**
	 * Returns the icon for a given element. The icon depends on the element
	 * type and element properties. If configured, overlay icons are constructed
	 * for <code>ISourceReference</code>s.
	 * 
	 * @param element
	 *            the element
	 * @param flags
	 *            Flags as defined by the JavaImageLabelProvider
	 * @return return the image or <code>null</code>
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}

	private Image getImageLabel(ImageDescriptor descriptor) {
		if (descriptor == null)
			return null;
		return getRegistry().get(descriptor);
	}

	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry = VdmUIPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}

	
	//TODO: this map should be deleted when the AST fix is made 
	//so that definitions contain a reference to the module they belong
	static Map<String,AModuleModules> activeModule = new HashMap<String,AModuleModules>();

	protected ImageDescriptor computeDescriptor(Object element, int flags) {
		int adornmentFlags = 0;

		adornmentFlags = computeVdmAdornmentFlags(element);

		if (element instanceof ASystemClassDefinition) {
//			activeModule = null;
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_SYSTEM);
		} else if (element instanceof AClassClassDefinition) {
//			activeModule = null;
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_CLASS);
		} else if (element instanceof AModuleModules) {
			activeModule.put(((AModuleModules)element).getName().getName(), ((AModuleModules)element));
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_MODULE);
		} else if (element instanceof ATypeDefinition) {
			return getTypeDefinitionImage((ATypeDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof AInstanceVariableDefinition) {
			return getInstanceVariableDefinitionImage(
					(AInstanceVariableDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if (element instanceof AExplicitOperationDefinition) {
			return getExplicitOperationDefinitionImage(
					(AExplicitOperationDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if (element instanceof AExplicitFunctionDefinition) {
			return getExplicitFunctionDefinitionImage(
					(AExplicitFunctionDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if(element instanceof AImplicitFunctionDefinition){
			return getImplicitFunctionDefinitionImage(
					(AImplicitFunctionDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}else if(element instanceof AImplicitOperationDefinition){
			return getImplicitOperationDefinitionImage(
					(AImplicitOperationDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}else if(element instanceof APerSyncDefinition){
			return getPerSyncDefinitionImage(
					(APerSyncDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}
		
		
		
		
		else if (element instanceof ALocalDefinition) {
			return getLocalDefinitionImage((ALocalDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof AValueDefinition) {
			return getValueDefinitionImage((AValueDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof ANamedTraceDefinition) {
			return getNamedTraceDefinitionImage((ANamedTraceDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof AUntypedDefinition) {
			List<PDefinition> definitions = null;
			if (((AUntypedDefinition) element).getClassDefinition() != null) {
				SClassDefinition classDef = ((AUntypedDefinition) element).getClassDefinition();
				definitions = classDef.getDefinitions();
			} else {
				if (activeModule != null){
					AUntypedDefinition untypedDef = (AUntypedDefinition) element;
					
					definitions = activeModule.get(untypedDef.getName().getModule()).getDefs();
				}
			}
			if (definitions != null) {
				for (PDefinition def : definitions) {
					if (def instanceof AValueDefinition) {
						for (int i = 0; i < ((AValueDefinition)def).getDefs().size(); i++) {
							if (((AValueDefinition)def).getDefs().get(i).getLocation()
									.equals(
											((AUntypedDefinition) element)
													.getLocation())) {
								return getValueDefinitionImage(
										(AValueDefinition) def, SMALL_ICONS,
										adornmentFlags);
							}
						}
					}
				}
			}
		}

		else if (element instanceof AModuleImports) {
			return VdmPluginImages.DESC_OBJS_IMPCONT;
		}

		else if (element instanceof AFromModuleImports) {
			return VdmPluginImages.DESC_OBJS_IMPDECL;
		}
		
		else if(element instanceof ImportsContainer){
			return computeDescriptor(((ImportsContainer) element).getImports(), adornmentFlags);
		}
		
		else if (element instanceof IFile) {
			IFile file = (IFile) element;
			// if (JavaCore.isJavaLikeFileName(file.getName())) {
			// return getCUResourceImageDescriptor(file, flags); // image for a
			// CU not on the build path
			// }
			return getWorkbenchImageDescriptor(file, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		} else if (element instanceof AFieldField){
			return getFieldImage((AFieldField) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof AInheritedDefinition){
			return getInheritedDefinitionImage((AInheritedDefinition)element,SMALL_ICONS,adornmentFlags);
		} else if (element instanceof ARenamedDefinition){
			return VdmPluginImages.DESC_OBJS_IMPDECL;
		} else if(element instanceof AOperationValueImport){
			return VdmPluginImages.DESC_METHOD_DEFAULT;					
		}
		
		
		return null;
	}

	

	private ImageDescriptor getFieldImage(AFieldField element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		if(as == null) 
			return result;
		
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;
		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
		
	}

	private ImageDescriptor getInheritedDefinitionImage(
			AInheritedDefinition element, int smallIcons, int adornmentFlags) {
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;
		ImageDescriptor desc = computeDescriptor(element.getSuperdef(),adornmentFlags);
		
		
		return new VdmElementImageDescriptor(desc, adornmentFlags, size);
	}
	
	private ImageDescriptor getImplicitOperationDefinitionImage(
			AImplicitOperationDefinition element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getImplicitFunctionDefinitionImage(
			AImplicitFunctionDefinition element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getExplicitFunctionDefinitionImage(
			AExplicitFunctionDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		//adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FUNC_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getNamedTraceDefinitionImage(
			ANamedTraceDefinition element, int renderFlags, int adornmentFlags) {
		//ImageDescriptor result = null;

		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		return new VdmElementImageDescriptor(VdmPluginImages
				.getDescriptor(VdmPluginImages.IMG_TRACE_DEFAULT),
				adornmentFlags, size);
	}
	
	private ImageDescriptor getPerSyncDefinitionImage(
			APerSyncDefinition element, int renderFlags, int adornmentFlags) {
		//ImageDescriptor result = null;

		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		return new VdmElementImageDescriptor(VdmPluginImages
				.getDescriptor(VdmPluginImages.IMG_OBJS_LOCK),
				adornmentFlags, size);
	}

	private ImageDescriptor getTypeDefinitionImage(ATypeDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getValueDefinitionImage(AValueDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		adornmentFlags |= VdmElementImageDescriptor.FINAL;
		
		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getLocalDefinitionImage(ALocalDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getExplicitOperationDefinitionImage(
			AExplicitOperationDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
		//		

		// return new
		// VdmElementImageDescriptor(VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
		// 0,
		// size);
	}

	private ImageDescriptor getInstanceVariableDefinitionImage(
			AInstanceVariableDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AAccessSpecifierAccessSpecifier as = element.getAccess();
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.getAccess().toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.getAccess().toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;

	}

//	private static boolean showOverlayIcons(int flags) {
//		return (flags & OVERLAY_ICONS) != 0;
//	}

	protected static boolean useSmallSize(int flags) {
		return (flags & SMALL_ICONS) != 0;
	}

//	private static boolean useLightIcons(int flags) {
//		return (flags & LIGHT_TYPE_ICONS) != 0;
//	}

	/**
	 * Returns an image descriptor for a compilation unit not on the class path.
	 * The descriptor includes overlays, if specified.
	 * 
	 * @param file
	 *            the cu resource file
	 * @param flags
	 *            the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new VdmElementImageDescriptor(
				VdmPluginImages.DESC_OBJS_CUNIT_RESOURCE, 0, size);
	}

	/**
	 * Returns an image descriptor for a IAdaptable. The descriptor includes
	 * overlays, if specified (only error ticks apply). Returns
	 * <code>null</code> if no image could be found.
	 * 
	 * @param adaptable
	 *            the adaptable
	 * @param flags
	 *            the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable,
			int flags) {
		IWorkbenchAdapter wbAdapter = (IWorkbenchAdapter) adaptable
				.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor = wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}

		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new VdmElementImageDescriptor(descriptor, 0, size);
	}

	public int computeVdmAdornmentFlags(Object element) {
		int flags = 0;

		if (element instanceof AClassClassDefinition) {
			flags = getClassDefinitionFlags((AClassClassDefinition) element);
		} else if (element instanceof AInstanceVariableDefinition) {
			flags = getInstanceVariableDefinitionFlags((AInstanceVariableDefinition) element);
		} else if (element instanceof AExplicitOperationDefinition) {
			flags = getExplicitOperationDefinitionFlags((AExplicitOperationDefinition) element);

		} else if (element instanceof ALocalDefinition) {
			flags = getLocalDefinitionFlags((ALocalDefinition) element);
		} else if (element instanceof AInheritedDefinition){
			flags = getInheritedDefinitionFlags((AInheritedDefinition)element);
		}
		
		
		return flags;
	}

	private int getInheritedDefinitionFlags(AInheritedDefinition element) {
		int flags = 0;
		flags |= VdmElementImageDescriptor.OVERRIDES;
		
		return flags;
	}

	private int getLocalDefinitionFlags(ALocalDefinition element) {
		int flags = 0;

		//flags |= VdmElementImageDescriptor.STATIC;
		flags |= VdmElementImageDescriptor.FINAL;

		return flags;
	}

	private int getInstanceVariableDefinitionFlags(
			AInstanceVariableDefinition element) {
		int flags = 0;

		if (element.getAccess().getStatic()!=null) {
			flags |= VdmElementImageDescriptor.STATIC;
		}

		return flags;
	}

	private int getExplicitOperationDefinitionFlags(
			AExplicitOperationDefinition element) {

		int flags = 0;
		if (element.getIsConstructor()) {
			flags |= VdmElementImageDescriptor.CONSTRUCTOR;
		}
		if (element.getAccess().getStatic()!=null) {
			flags |= VdmElementImageDescriptor.STATIC;
		}
		if(element.getName().getName() != null){
			String s = element.getName().getName();
			if(s.equals("thread")){
				flags |= VdmElementImageDescriptor.RUNNABLE;
			}
		}
		
		

		return flags;
	}

	private int getClassDefinitionFlags(AClassClassDefinition element) {
		int flags = 0;

		if (element.getIsAbstract()) {
			flags |= VdmElementImageDescriptor.ABSTRACT;
		}
		return flags;
	}
	
	

	public void dispose() {
	}

}
