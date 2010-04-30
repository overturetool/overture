package org.overture.ide.ui.internal.viewsupport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.UntypedDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.modules.ImportFromModule;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleImports;
import org.overturetool.vdmj.types.Field;

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
	static Map<String,Module> activeModule = new HashMap<String,Module>();

	private ImageDescriptor computeDescriptor(Object element, int flags) {
		int adornmentFlags = 0;

		adornmentFlags = computeVdmAdornmentFlags(element);

		if (element instanceof ClassDefinition) {
//			activeModule = null;
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_CLASS);
		} else if (element instanceof Module) {
			activeModule.put(((Module)element).getName(), ((Module)element));
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_MODULE);
		} else if (element instanceof TypeDefinition) {
			return getTypeDefinitionImage((TypeDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof InstanceVariableDefinition) {
			return getInstanceVariableDefinitionImage(
					(InstanceVariableDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if (element instanceof ExplicitOperationDefinition) {
			return getExplicitOperationDefinitionImage(
					(ExplicitOperationDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if (element instanceof ExplicitFunctionDefinition) {
			return getExplicitFunctionDefinitionImage(
					(ExplicitFunctionDefinition) element, SMALL_ICONS,
					adornmentFlags);
		} else if(element instanceof ImplicitFunctionDefinition){
			return getImplicitFunctionDefinitionImage(
					(ImplicitFunctionDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}else if(element instanceof ImplicitOperationDefinition){
			return getImplicitOperationDefinitionImage(
					(ImplicitOperationDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}else if(element instanceof PerSyncDefinition){
			return getPerSyncDefinitionImage(
					(PerSyncDefinition) element, SMALL_ICONS,
					adornmentFlags);
		}
		
		
		
		
		else if (element instanceof LocalDefinition) {
			return getLocalDefinitionImage((LocalDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof ValueDefinition) {
			return getValueDefinitionImage((ValueDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof NamedTraceDefinition) {
			return getNamedTraceDefinitionImage((NamedTraceDefinition) element,
					SMALL_ICONS, adornmentFlags);
		} else if (element instanceof UntypedDefinition) {
			DefinitionList definitions = null;
			if (((UntypedDefinition) element).classDefinition != null) {
				ClassDefinition classDef = ((UntypedDefinition) element).classDefinition;
				definitions = classDef.definitions;
			} else {
				if (activeModule != null){
					UntypedDefinition untypedDef = (UntypedDefinition) element;
					
					definitions = activeModule.get(untypedDef.name.module).defs;
				}
			}
			if (definitions != null) {
				for (Definition def : definitions) {
					if (def instanceof ValueDefinition) {
						for (int i = 0; i < def.getDefinitions().size(); i++) {
							if (def.getDefinitions().get(i).getLocation()
									.equals(
											((UntypedDefinition) element)
													.getLocation())) {
								return getValueDefinitionImage(
										(ValueDefinition) def, SMALL_ICONS,
										adornmentFlags);
							}
						}
					}
				}
			}
		}

		else if (element instanceof ModuleImports) {
			return VdmPluginImages.DESC_OBJS_IMPCONT;
		}

		else if (element instanceof ImportFromModule) {
			return VdmPluginImages.DESC_OBJS_IMPDECL;
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
		} else if (element instanceof Field){
			return getFieldImage((Field) element,
					SMALL_ICONS, adornmentFlags);
		}
		
		return null;
	}

	private ImageDescriptor getFieldImage(Field element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessibility;
		if(as == null) 
			return result;
		
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;
		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
		
	}

	private ImageDescriptor getImplicitOperationDefinitionImage(
			ImplicitOperationDefinition element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getImplicitFunctionDefinitionImage(
			ImplicitFunctionDefinition element, int smallIcons,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(smallIcons) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getExplicitFunctionDefinitionImage(
			ExplicitFunctionDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		adornmentFlags = adornmentFlags | VdmElementImageDescriptor.FINAL;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getNamedTraceDefinitionImage(
			NamedTraceDefinition element, int renderFlags, int adornmentFlags) {
		//ImageDescriptor result = null;

		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		return new VdmElementImageDescriptor(VdmPluginImages
				.getDescriptor(VdmPluginImages.IMG_TRACE_DEFAULT),
				adornmentFlags, size);
	}
	
	private ImageDescriptor getPerSyncDefinitionImage(
			PerSyncDefinition element, int renderFlags, int adornmentFlags) {
		//ImageDescriptor result = null;

		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		return new VdmElementImageDescriptor(VdmPluginImages
				.getDescriptor(VdmPluginImages.IMG_OBJS_VDM_PER_SYNC),
				adornmentFlags, size);
	}

	private ImageDescriptor getTypeDefinitionImage(TypeDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_TYPE_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getValueDefinitionImage(ValueDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getLocalDefinitionImage(LocalDefinition element,
			int renderFlags, int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;
	}

	private ImageDescriptor getExplicitOperationDefinitionImage(
			ExplicitOperationDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		// result =
		// VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
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
			InstanceVariableDefinition element, int renderFlags,
			int adornmentFlags) {
		ImageDescriptor result = null;
		AccessSpecifier as = element.accessSpecifier;
		Point size = useSmallSize(renderFlags) ? SMALL_SIZE : BIG_SIZE;

		if (as.access.toString().equals("private")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE),
					adornmentFlags, size);
		} else if (as.access.toString().equals("public")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC),
					adornmentFlags, size);
		} else if (as.access.toString().equals("protected")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED),
					adornmentFlags, size);
		} else if (as.access.toString().equals("default")) {
			return new VdmElementImageDescriptor(VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT),
					adornmentFlags, size);
		}

		return result;

	}

	private static boolean showOverlayIcons(int flags) {
		return (flags & OVERLAY_ICONS) != 0;
	}

	private static boolean useSmallSize(int flags) {
		return (flags & SMALL_ICONS) != 0;
	}

	private static boolean useLightIcons(int flags) {
		return (flags & LIGHT_TYPE_ICONS) != 0;
	}

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

		if (element instanceof ClassDefinition) {
			flags = getClassDefinitionFlags((ClassDefinition) element);
		} else if (element instanceof InstanceVariableDefinition) {
			flags = getInstanceVariableDefinitionFlags((InstanceVariableDefinition) element);
		} else if (element instanceof ExplicitOperationDefinition) {
			flags = getExplicitOperationDefinitionFlags((ExplicitOperationDefinition) element);

		} else if (element instanceof LocalDefinition) {
			flags = getLocalDefinitionFlags((LocalDefinition) element);
		}

		return flags;
	}

	private int getLocalDefinitionFlags(LocalDefinition element) {
		int flags = 0;

		flags |= VdmElementImageDescriptor.STATIC;
		flags |= VdmElementImageDescriptor.FINAL;

		return flags;
	}

	private int getInstanceVariableDefinitionFlags(
			InstanceVariableDefinition element) {
		int flags = 0;

		if (element.isStatic()) {
			flags |= VdmElementImageDescriptor.STATIC;
		}

		return flags;
	}

	private int getExplicitOperationDefinitionFlags(
			ExplicitOperationDefinition element) {

		int flags = 0;

		if (element.isConstructor) {
			flags |= VdmElementImageDescriptor.CONSTRUCTOR;
		}
		if (element.isStatic()) {
			flags |= VdmElementImageDescriptor.STATIC;
		}

		return flags;
	}

	private int getClassDefinitionFlags(ClassDefinition element) {
		int flags = 0;

		if (element.isAbstract) {
			flags |= VdmElementImageDescriptor.ABSTRACT;
		}

		return flags;
	}

	public void dispose() {
	}

}
