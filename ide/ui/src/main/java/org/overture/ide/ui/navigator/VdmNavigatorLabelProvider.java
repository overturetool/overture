package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmNavigatorLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	private ResourceManager resourceManager;
	
	public String getDescription(Object anElement) {
		if (anElement instanceof IResource) {
			return ((IResource) anElement).getFullPath().makeRelative().toString();
		}
		return null;
	}

	
	/* (non-Javadoc)
     * Method declared on ILabelProvider
     */
	@Override
    public final Image getImage(Object element) {
		
		if(element instanceof SourceContainer){
			return ((SourceContainer)element).getImage();
		}
		
		if(element instanceof ResourceContainer){
			element = ((ResourceContainer) element).getFolder();
		}
		
		
        //obtain the base image by querying the element
        IWorkbenchAdapter adapter = getAdapter(element);
        if (adapter == null) {
            return null;
        }
        ImageDescriptor descriptor = adapter.getImageDescriptor(element);
        if (descriptor == null) {
            return null;
        }

        //add any annotations to the image descriptor
        descriptor = decorateImage(descriptor, element);

		return (Image) getResourceManager().get(descriptor);
    }

	
	@Override
	public String getText(Object element) {
		
		if(element instanceof SourceContainer){
			return ((SourceContainer)element).getText();
		}
		
		if(element instanceof ResourceContainer){
			element = ((ResourceContainer) element).getFolder();
		}
		//query the element for its label
        IWorkbenchAdapter adapter = getAdapter(element);
        if (adapter == null) {
            return ""; //$NON-NLS-1$
        }
        String label = adapter.getLabel(element);

        //return the decorated label
        return decorateText(label, element);
	}

	protected final IWorkbenchAdapter getAdapter(Object o) {
        return (IWorkbenchAdapter)Util.getAdapter(o, IWorkbenchAdapter.class);
    }
	
	/**
     * Returns a label that is based on the given label,
     * but decorated with additional information relating to the state
     * of the provided object.
     *
     * Subclasses may implement this method to decorate an object's
     * label.
     * @param input The base text to decorate.
     * @param element The element used to look up decorations.
     * @return the resulting text
     */
    protected String decorateText(String input, Object element) {
        return input;
    }

    /**
     * Returns an image descriptor that is based on the given descriptor,
     * but decorated with additional information relating to the state
     * of the provided object.
     *
     * Subclasses may reimplement this method to decorate an object's
     * image.
     * 
     * @param input The base image to decorate.
     * @param element The element used to look up decorations.
     * @return the resuling ImageDescriptor.
     * @see org.eclipse.jface.resource.CompositeImageDescriptor
     */
    protected ImageDescriptor decorateImage(ImageDescriptor input,
            Object element) {
        return input;
    }
    
    /**
	 * Lazy load the resource manager
	 * 
	 * @return The resource manager, create one if necessary
	 */
	private ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(JFaceResources
					.getResources());
		}

		return resourceManager;
	}
}
