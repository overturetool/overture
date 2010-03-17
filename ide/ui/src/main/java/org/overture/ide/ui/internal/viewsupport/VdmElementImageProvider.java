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
		if( element instanceof ClassDefinition)
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

	
	


	public void dispose() {
	}


}
