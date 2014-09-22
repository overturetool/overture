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
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.overture.ide.ui.internal.util.SWTUtil;

public class ImageDescriptorRegistry {
	private HashMap<ImageDescriptor, Image> fRegistry= new HashMap<ImageDescriptor, Image>(10);
	private Display fDisplay;

	/**
	 * Creates a new image descriptor registry for the current or default display,
	 * respectively.
	 */
	public ImageDescriptorRegistry() {
		this(SWTUtil.getStandardDisplay());
	}

	/**
	 * Creates a new image descriptor registry for the given display. All images
	 * managed by this registry will be disposed when the display gets disposed.
	 *
	 * @param display the display the images managed by this registry are allocated for
	 */
	public ImageDescriptorRegistry(Display display) {
		fDisplay= display;
		Assert.isNotNull(fDisplay);
		hookDisplay();
	}

	/**
	 * Returns the image associated with the given image descriptor.
	 *
	 * @param descriptor the image descriptor for which the registry manages an image,
	 *  or <code>null</code> for a missing image descriptor
	 * @return the image associated with the image descriptor or <code>null</code>
	 *  if the image descriptor can't create the requested image.
	 */
	public Image get(ImageDescriptor descriptor) {
		if (descriptor == null)
			descriptor= ImageDescriptor.getMissingImageDescriptor();

		Image result= (Image)fRegistry.get(descriptor);
		if (result != null)
			return result;

		Assert.isTrue(fDisplay == SWTUtil.getStandardDisplay(), "Allocating image for wrong display."); //$NON-NLS-1$
		result= descriptor.createImage();
		if (result != null)
			fRegistry.put(descriptor, result);
		return result;
	}

	/**
	 * Disposes all images managed by this registry.
	 */
	public void dispose() {
		for (Iterator<Image> iter= fRegistry.values().iterator(); iter.hasNext(); ) {
			Image image= (Image)iter.next();
			image.dispose();
		}
		fRegistry.clear();
	}

	private void hookDisplay() {
		fDisplay.disposeExec(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}
}
