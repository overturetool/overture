package org.overture.ide.ui.internal.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;

public class VdmElementImageProvider {
	/**
	 * Flags for the JavaImageLabelProvider:
	 * Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS= 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS= 0x2;

	/**
	 * Use the 'light' style for rendering types.
	 */
	public final static int LIGHT_TYPE_ICONS= 0x4;


	public static final Point SMALL_SIZE= new Point(16, 16);
	public static final Point BIG_SIZE= new Point(22, 16);

	private static ImageDescriptor DESC_OBJ_PROJECT_CLOSED;
	private static ImageDescriptor DESC_OBJ_PROJECT;
	{
		ISharedImages images= VdmUIPlugin.getDefault().getWorkbench().getSharedImages();
		DESC_OBJ_PROJECT_CLOSED= images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		DESC_OBJ_PROJECT= 		 images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}

	private ImageDescriptorRegistry fRegistry;

	public VdmElementImageProvider() {
		fRegistry= null; // lazy initialization
	}

	/**
	 * Returns the icon for a given element. The icon depends on the element type
	 * and element properties. If configured, overlay icons are constructed for
	 * <code>ISourceReference</code>s.
	 * @param element the element
	 * @param flags Flags as defined by the JavaImageLabelProvider
	 * @return return the image or <code>null</code>
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}

	private Image getImageLabel(ImageDescriptor descriptor){
		if (descriptor == null)
			return null;
		return getRegistry().get(descriptor);
	}

	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry= VdmUIPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}


	private ImageDescriptor computeDescriptor(Object element, int flags){
		if (element instanceof IVdmElement) {
			return getJavaImageDescriptor((IVdmElement) element, flags);
		} else if( element instanceof ClassDefinition)
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_OBJS_CLASS);
		} else if( element instanceof InstanceVariableDefinition)
		{
			return getInstanceVariableDefinitionImage((InstanceVariableDefinition) element);
		} else if( element instanceof ExplicitOperationDefinition){
			return getExplicitOperationDefinitionImage((ExplicitOperationDefinition) element);
		} else if( element instanceof LocalDefinition){
			return getLocalDefinitionImage((LocalDefinition) element);
		}
		
		else if (element instanceof IFile) {
			IFile file= (IFile) element;
//			if (JavaCore.isJavaLikeFileName(file.getName())) {
//				return getCUResourceImageDescriptor(file, flags); // image for a CU not on the build path
//			}
			return getWorkbenchImageDescriptor(file, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		}
		return null;
	}

	private ImageDescriptor getLocalDefinitionImage(LocalDefinition element) {
		ImageDescriptor result = null;		
		AccessSpecifier as = element.accessSpecifier;
						
		if(as.access.toString().equals("private"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE);
		} else if(as.access.toString().equals("public"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC);
		} else if (as.access.toString().equals("protected"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED);
		} else if(as.access.toString().equals("default"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT);
		}
		
		
		return result;
	}

	private ImageDescriptor getExplicitOperationDefinitionImage(
			ExplicitOperationDefinition element) {
		ImageDescriptor result = null;		
		AccessSpecifier as = element.accessSpecifier;
						
		if(as.access.toString().equals("private"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PRIVATE);
		} else if(as.access.toString().equals("public"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PUBLIC);
		} else if (as.access.toString().equals("protected"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_PROTECTED);
		}else if(as.access.toString().equals("default"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_METHOD_DEFAULT);
		}
		
		
		return result;
		
	}

	private ImageDescriptor getInstanceVariableDefinitionImage(InstanceVariableDefinition element) {
		ImageDescriptor result = null;		
		AccessSpecifier as = element.accessSpecifier;
						
		if(as.access.toString().equals("private"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PRIVATE);
		} else if(as.access.toString().equals("public"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PUBLIC);
		} else if (as.access.toString().equals("protected"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_PROTECTED);
		} else if(as.access.toString().equals("default"))
		{
			return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_FIELD_DEFAULT);
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
	 * @param file the cu resource file
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getCUResourceImageDescriptor(IFile file, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new VdmElementImageDescriptor(VdmPluginImages.DESC_OBJS_CUNIT_RESOURCE, 0, size);
	}

	/**
	 * Returns an image descriptor for a java element. The descriptor includes overlays, if specified.
	 * @param element the Java element
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getJavaImageDescriptor(IVdmElement element, int flags) {
		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;

//		ImageDescriptor baseDesc= getBaseImageDescriptor(element, flags);
//		if (baseDesc != null) {
//			int adornmentFlags= computeJavaAdornmentFlags(element, flags);
//			return new VdmElementImageDescriptor(baseDesc, adornmentFlags, size);
//		}
		return new VdmElementImageDescriptor(VdmPluginImages.DESC_OBJS_GHOST, 0, size);
	}

	/**
	 * Returns an image descriptor for a IAdaptable. The descriptor includes overlays, if specified (only error ticks apply).
	 * Returns <code>null</code> if no image could be found.
	 * @param adaptable the adaptable
	 * @param flags the image flags
	 * @return returns the image descriptor
	 */
	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable, int flags) {
		IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) adaptable.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor= wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}

		Point size= useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new VdmElementImageDescriptor(descriptor, 0, size);
	}

	// ---- Computation of base image key -------------------------------------------------

	/**
	 * Returns an image descriptor for a java element. This is the base image, no overlays.
	 * @param element the element
	 * @param renderFlags the image flags
	 * @return returns the image descriptor
	 */
//	public ImageDescriptor getBaseImageDescriptor(IVdmElement element, int renderFlags) {

//		try {
//			switch (element.getElementType()) {
//				case IVdmElement.INITIALIZER:
//					return VdmPluginImages.DESC_MISC_PRIVATE; // 23479
//				case IVdmElement.METHOD: {
//					IMethod method= (IMethod) element;
//					IType declType= method.getDeclaringType();
//					int flags= method.getFlags();
//					if (declType.isEnum() && isDefaultFlag(flags) && method.isConstructor())
//						return VdmPluginImages.DESC_MISC_PRIVATE;
//					return getMethodImageDescriptor(JavaModelUtil.isInterfaceOrAnnotation(declType), flags);
//				}
//				case IVdmElement.FIELD: {
//					IMember member= (IMember) element;
//					IType declType= member.getDeclaringType();
//					return getFieldImageDescriptor(JavaModelUtil.isInterfaceOrAnnotation(declType), member.getFlags());
//				}
//				case IVdmElement.LOCAL_VARIABLE:
//					return VdmPluginImages.DESC_OBJS_LOCAL_VARIABLE;
//
//				case IVdmElement.PACKAGE_DECLARATION:
//					return VdmPluginImages.DESC_OBJS_PACKDECL;
//
//				case IVdmElement.IMPORT_DECLARATION:
//					return VdmPluginImages.DESC_OBJS_IMPDECL;
//
//				case IVdmElement.IMPORT_CONTAINER:
//					return VdmPluginImages.DESC_OBJS_IMPCONT;
//
//				case IVdmElement.TYPE: {
//					IType type= (IType) element;
//
//					IType declType= type.getDeclaringType();
//					boolean isInner= declType != null;
//					boolean isInInterfaceOrAnnotation= isInner && JavaModelUtil.isInterfaceOrAnnotation(declType);
//					return getTypeImageDescriptor(isInner, isInInterfaceOrAnnotation, type.getFlags(), useLightIcons(renderFlags));
//				}
//
//				case IVdmElement.PACKAGE_FRAGMENT_ROOT: {
//					IPackageFragmentRoot root= (IPackageFragmentRoot) element;
//					IPath attach= root.getSourceAttachmentPath();
//					if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
//						if (root.isArchive()) {
//							if (root.isExternal()) {
//								if (attach == null) {
//									return VdmPluginImages.DESC_OBJS_EXTJAR;
//								} else {
//									return VdmPluginImages.DESC_OBJS_EXTJAR_WSRC;
//								}
//							} else {
//								if (attach == null) {
//									return VdmPluginImages.DESC_OBJS_JAR;
//								} else {
//									return VdmPluginImages.DESC_OBJS_JAR_WSRC;
//								}
//							}
//						} else {
//							if (attach == null) {
//								return VdmPluginImages.DESC_OBJS_CLASSFOLDER;
//							} else {
//								return VdmPluginImages.DESC_OBJS_CLASSFOLDER_WSRC;
//							}
//						}
//					} else {
//						return VdmPluginImages.DESC_OBJS_PACKFRAG_ROOT;
//					}
//				}
//
//				case IVdmElement.PACKAGE_FRAGMENT:
//					return getPackageFragmentIcon(element);
//
//
//				case IVdmElement.COMPILATION_UNIT:
//					return JavaPluginImages.DESC_OBJS_CUNIT;
//
//				case IVdmElement.CLASS_FILE:
//					/* this is too expensive for large packages
//					try {
//						IClassFile cfile= (IClassFile)element;
//						if (cfile.isClass())
//							return JavaPluginImages.IMG_OBJS_CFILECLASS;
//						return JavaPluginImages.IMG_OBJS_CFILEINT;
//					} catch(JavaModelException e) {
//						// fall through;
//					}*/
//					return VdmPluginImages.DESC_OBJS_CFILE;
//
//				case IVdmElement.VDM_PROJECT:
//					IVdmProject jp= (IVdmProject)element;
//					if (jp.getProject().isOpen()) {
//						IProject project= jp.getProject();
//						IWorkbenchAdapter adapter= (IWorkbenchAdapter)project.getAdapter(IWorkbenchAdapter.class);
//						if (adapter != null) {
//							ImageDescriptor result= adapter.getImageDescriptor(project);
//							if (result != null)
//								return result;
//						}
//						return DESC_OBJ_PROJECT;
//					}
//					return DESC_OBJ_PROJECT_CLOSED;
//
//				case IVdmElement.VDM_MODEL:
//					return VdmPluginImages.DESC_OBJS_JAVA_MODEL;
//
//				case IVdmElement.TYPE_PARAMETER:
//					return VdmPluginImages.DESC_OBJS_TYPEVARIABLE;
//
//				case IVdmElement.ANNOTATION:
//					return VdmPluginImages.DESC_OBJS_ANNOTATION;
//
//				default:
//					// ignore. Must be a new, yet unknown Java element
//					// give an advanced IWorkbenchAdapter the chance
//					IWorkbenchAdapter wbAdapter= (IWorkbenchAdapter) element.getAdapter(IWorkbenchAdapter.class);
//					if (wbAdapter != null && !(wbAdapter instanceof JavaWorkbenchAdapter)) { // avoid recursion
//						ImageDescriptor imageDescriptor= wbAdapter.getImageDescriptor(element);
//						if (imageDescriptor != null) {
//							return imageDescriptor;
//						}
//					}
//					return VdmPluginImages.DESC_OBJS_GHOST;
//			}
//
//		} catch (JavaModelException e) {
//			if (e.isDoesNotExist())
//				return VdmPluginImages.DESC_OBJS_UNKNOWN;
//			JavaPlugin.log(e);
//			return VdmPluginImages.DESC_OBJS_GHOST;
//		}
//	}

	private static boolean isDefaultFlag(int flags) {
//		return !Flags.isPublic(flags) && !Flags.isProtected(flags) && !Flags.isPrivate(flags);
	return false;
	}

//	private ImageDescriptor getPackageFragmentIcon(IVdmElement element) throws VdmModelException {
//		IPackageFragment fragment= (IPackageFragment)element;
//		boolean containsJavaElements= false;
//		try {
//			containsJavaElements= fragment.hasChildren();
//		} catch(JavaModelException e) {
//			// assuming no children;
//		}
//		if(!containsJavaElements && (fragment.getNonJavaResources().length > 0))
//			return VdmPluginImages.DESC_OBJS_EMPTY_PACKAGE_RESOURCES;
//		else if (!containsJavaElements)
//			return VdmPluginImages.DESC_OBJS_EMPTY_PACKAGE;
//		return VdmPluginImages.DESC_OBJS_PACKAGE;
//	}

	public void dispose() {
	}

	// ---- Methods to compute the adornments flags ---------------------------------

//	private int computeJavaAdornmentFlags(IVdmElement element, int renderFlags) {
//		int flags= 0;
//		if (showOverlayIcons(renderFlags) && element instanceof IMember) {
//			try {
//				IMember member= (IMember) element;
//
//				if (element.getElementType() == IVdmElement.METHOD && ((IMethod)element).isConstructor())
//					flags |= VdmElementImageDescriptor.CONSTRUCTOR;
//
//				int modifiers= member.getFlags();
//				if (Flags.isAbstract(modifiers) && confirmAbstract(member))
////					flags |= VdmElementImageDescriptor.ABSTRACT;
//				if (Flags.isFinal(modifiers) || isInterfaceOrAnnotationField(member) || isEnumConstant(member, modifiers))
//					flags |= VdmElementImageDescriptor.FINAL;
//				if (Flags.isSynchronized(modifiers) && confirmSynchronized(member))
//					flags |= VdmElementImageDescriptor.SYNCHRONIZED;
//				if (Flags.isStatic(modifiers) || isInterfaceOrAnnotationFieldOrType(member) || isEnumConstant(member, modifiers))
//					flags |= VdmElementImageDescriptor.STATIC;
//
////				if (Flags.isDeprecated(modifiers))
////					flags |= VdmElementImageDescriptor.DEPRECATED;
////				if (member.getElementType() == IVdmElement.TYPE) {
////					if (JavaModelUtil.hasMainMethod((IType) member)) {
////						flags |= VdmElementImageDescriptor.RUNNABLE;
////					}
////				}
//				if (member.getElementType() == IVdmElement.FIELD) {
//					if (Flags.isVolatile(modifiers))
//						flags |= VdmElementImageDescriptor.VOLATILE;
//					if (Flags.isTransient(modifiers))
//						flags |= VdmElementImageDescriptor.TRANSIENT;
//				}
//
//
//			} catch (VdmModelException e) {
//				// do nothing. Can't compute runnable adornment or get flags
//			}
//		}
//		return flags;
//	}

//	private static boolean confirmAbstract(IMember element) throws JavaModelException {
//		// never show the abstract symbol on interfaces or members in interfaces
//		if (element.getElementType() == IVdmElement.TYPE) {
//			return ! JavaModelUtil.isInterfaceOrAnnotation((IType) element);
//		}
//		return ! JavaModelUtil.isInterfaceOrAnnotation(element.getDeclaringType());
//	}
//
//	private static boolean isInterfaceOrAnnotationField(IMember element) throws JavaModelException {
//		// always show the final symbol on interface fields
//		if (element.getElementType() == IVdmElement.FIELD) {
//			return JavaModelUtil.isInterfaceOrAnnotation(element.getDeclaringType());
//		}
//		return false;
//	}
//
//	private static boolean isInterfaceOrAnnotationFieldOrType(IMember element) throws JavaModelException {
//		// always show the static symbol on interface fields and types
//		if (element.getElementType() == IVdmElement.FIELD) {
//			return JavaModelUtil.isInterfaceOrAnnotation(element.getDeclaringType());
//		} else if (element.getElementType() == IVdmElement.TYPE && element.getDeclaringType() != null) {
//			return JavaModelUtil.isInterfaceOrAnnotation(element.getDeclaringType());
//		}
//		return false;
//	}
//
//	private static boolean isEnumConstant(IMember element, int modifiers) {
//		if (element.getElementType() == IVdmElement.FIELD) {
//			return Flags.isEnum(modifiers);
//		}
//		return false;
//	}

	private static boolean confirmSynchronized(IVdmElement member) {
		// Synchronized types are allowed but meaningless.
//		return member.getElementType() != IVdmElement.TYPE;
		return false;
	}


	public static ImageDescriptor getMethodImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
//			return VdmPluginImages.DESC_MISC_PUBLIC;
//		if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_MISC_PROTECTED;
//		if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_MISC_PRIVATE;

		return VdmPluginImages.DESC_MISC_DEFAULT;
	}

	public static ImageDescriptor getFieldImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation || Flags.isEnum(flags))
//			return VdmPluginImages.DESC_FIELD_PUBLIC;
//		if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_FIELD_PROTECTED;
//		if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_FIELD_PRIVATE;

		return VdmPluginImages.DESC_FIELD_DEFAULT;
	}

	public static ImageDescriptor getTypeImageDescriptor(boolean isInner, boolean isInInterfaceOrAnnotation, int flags, boolean useLightIcons) {
//		if (Flags.isEnum(flags)) {
//			if (useLightIcons) {
//				return VdmPluginImages.DESC_OBJS_ENUM_ALT;
//			}
//			if (isInner) {
//				return getInnerEnumImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
//			return getEnumImageDescriptor(flags);
//		} else if (Flags.isAnnotation(flags)) {
//			if (useLightIcons) {
//				return VdmPluginImages.DESC_OBJS_ANNOTATION_ALT;
//			}
//			if (isInner) {
//				return getInnerAnnotationImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
//			return getAnnotationImageDescriptor(flags);
//		}  else if (Flags.isInterface(flags)) {
//			if (useLightIcons) {
//				return VdmPluginImages.DESC_OBJS_INTERFACEALT;
//			}
//			if (isInner) {
//				return getInnerInterfaceImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
//			return getInterfaceImageDescriptor(flags);
//		} else {
//			if (useLightIcons) {
//				return VdmPluginImages.DESC_OBJS_CLASSALT;
//			}
//			if (isInner) {
//				return getInnerClassImageDescriptor(isInInterfaceOrAnnotation, flags);
//			}
			return getClassImageDescriptor(flags);
//		}
	}


	public static Image getDecoratedImage(ImageDescriptor baseImage, int adornments, Point size) {
		return VdmUIPlugin.getImageDescriptorRegistry().get(new VdmElementImageDescriptor(baseImage, adornments, size));
	}


	private static ImageDescriptor getClassImageDescriptor(int flags) {
//		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_CLASS;
//		else
			return VdmPluginImages.DESC_OBJS_CLASS_DEFAULT;
	}

	private static ImageDescriptor getInnerClassImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
//			return VdmPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC;
//		else if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE;
//		else if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED;
//		else
			return VdmPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT;
	}

	private static ImageDescriptor getEnumImageDescriptor(int flags) {
//		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_ENUM;
//		else
			return VdmPluginImages.DESC_OBJS_ENUM_DEFAULT;
	}

	private static ImageDescriptor getInnerEnumImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
//			return VdmPluginImages.DESC_OBJS_ENUM;
//		else if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_ENUM_PRIVATE;
//		else if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_OBJS_ENUM_PROTECTED;
//		else
			return VdmPluginImages.DESC_OBJS_ENUM_DEFAULT;
	}

	private static ImageDescriptor getAnnotationImageDescriptor(int flags) {
//		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_ANNOTATION;
//		else
			return VdmPluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}

	private static ImageDescriptor getInnerAnnotationImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
//			return VdmPluginImages.DESC_OBJS_ANNOTATION;
//		else if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_ANNOTATION_PRIVATE;
//		else if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_OBJS_ANNOTATION_PROTECTED;
//		else
			return VdmPluginImages.DESC_OBJS_ANNOTATION_DEFAULT;
	}

	private static ImageDescriptor getInterfaceImageDescriptor(int flags) {
//		if (Flags.isPublic(flags) || Flags.isProtected(flags) || Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_INTERFACE;
//		else
			return VdmPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
	}

	private static ImageDescriptor getInnerInterfaceImageDescriptor(boolean isInInterfaceOrAnnotation, int flags) {
//		if (Flags.isPublic(flags) || isInInterfaceOrAnnotation)
//			return VdmPluginImages.DESC_OBJS_INNER_INTERFACE_PUBLIC;
//		else if (Flags.isPrivate(flags))
//			return VdmPluginImages.DESC_OBJS_INNER_INTERFACE_PRIVATE;
//		else if (Flags.isProtected(flags))
//			return VdmPluginImages.DESC_OBJS_INNER_INTERFACE_PROTECTED;
//		else
			return VdmPluginImages.DESC_OBJS_INTERFACE_DEFAULT;
	}
}
