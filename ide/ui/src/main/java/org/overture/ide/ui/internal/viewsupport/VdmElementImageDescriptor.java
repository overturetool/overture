package org.overture.ide.ui.internal.viewsupport;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;

/**
 * A {@link JavaElementImageDescriptor} consists of a base image and several adornments. The adornments
 * are computed according to the flags either passed during creation or set via the method
 *{@link #setAdornments(int)}.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class VdmElementImageDescriptor extends CompositeImageDescriptor {
	
	/** Flag to render the abstract adornment. */
	public final static int ABSTRACT= 		0x001;

	/** Flag to render the final adornment. */
	public final static int FINAL=			0x002;

	/** Flag to render the synchronized adornment. */
	public final static int SYNCHRONIZED=	0x004;

	/** Flag to render the static adornment. */
	public final static int STATIC=			0x008;

	/** Flag to render the runnable adornment. */
	public final static int RUNNABLE= 		0x010;

	/** Flag to render the warning adornment. */
	public final static int WARNING=			0x020;

	/** Flag to render the error adornment. */
	public final static int ERROR=			0x040;

	/** Flag to render the 'override' adornment. */
	public final static int OVERRIDES= 		0x080;

	/** Flag to render the 'implements' adornment. */
	public final static int IMPLEMENTS= 		0x100;

	/** Flag to render the 'constructor' adornment. */
	public final static int CONSTRUCTOR= 	0x200;

	/**
	 * Flag to render the 'deprecated' adornment.
	 * @since 3.0
	 */
	public final static int DEPRECATED= 	0x400;

	/**
	 * Flag to render the 'volatile' adornment.
	 * @since 3.3
	 */
	public final static int VOLATILE= 	0x800;

	/**
	 * Flag to render the 'transient' adornment.
	 * @since 3.3
	 */
	public final static int TRANSIENT= 	0x1000;

	private ImageDescriptor fBaseImage;
	private int fFlags;
	private Point fSize;

	/**
	 * Creates a new JavaElementImageDescriptor.
	 *
	 * @param baseImage an image descriptor used as the base image
	 * @param flags flags indicating which adornments are to be rendered. See {@link #setAdornments(int)}
	 * 	for valid values.
	 * @param size the size of the resulting image
	 */
	public VdmElementImageDescriptor(ImageDescriptor baseImage, int flags, Point size) {
		fBaseImage= baseImage;
		Assert.isNotNull(fBaseImage);
		fFlags = flags;
		//Assert.isNotNull(element);		
		fSize= size;
		Assert.isNotNull(fSize);
	}

	


	/**
	 * Sets the size of the image created by calling {@link #createImage()}.
	 *
	 * @param size the size of the image returned from calling {@link #createImage()}
	 */
	public void setImageSize(Point size) {
		Assert.isNotNull(size);
		Assert.isTrue(size.x >= 0 && size.y >= 0);
		fSize= size;
	}

	/**
	 * Returns the size of the image created by calling {@link #createImage()}.
	 *
	 * @return the size of the image created by calling {@link #createImage()}
	 */
	public Point getImageSize() {
		return new Point(fSize.x, fSize.y);
	}

	/* (non-Javadoc)
	 * Method declared in CompositeImageDescriptor
	 */
	protected Point getSize() {
		return fSize;
	}

//	/* (non-Javadoc)
//	 * Method declared on Object.
//	 */
//	public boolean equals(Object object) {
//		if (object == null || !VdmElementImageDescriptor.class.equals(object.getClass()))
//			return false;
//
//		VdmElementImageDescriptor other= (VdmElementImageDescriptor)object;
//		return (fBaseImage.equals(other.fBaseImage) && fFlags == other.fFlags && fSize.equals(other.fSize));
//	}

//	/* (non-Javadoc)
//	 * Method declared on Object.
//	 */
//	public int hashCode() {
//		return fBaseImage.hashCode() | fFlags | fSize.hashCode();
//	}

	/* (non-Javadoc)
	 * Method declared in CompositeImageDescriptor
	 */
	protected void drawCompositeImage(int width, int height) {
		ImageData bg= getImageData(fBaseImage);
						
		drawImage(bg, 0, 0);

		drawTopRight();
		drawBottomRight();
		drawBottomLeft();


	}

	private ImageData getImageData(ImageDescriptor descriptor) {
		ImageData data= descriptor.getImageData(); // see bug 51965: getImageData can return null
		if (data == null) {
			data= DEFAULT_IMAGE_DATA;
			VdmUIPlugin.logErrorMessage("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
		}
		return data;
	}

	private void addTopRightImage(ImageDescriptor desc, Point pos) {
		ImageData data= getImageData(desc);
		int x= pos.x - data.width;
		if (x >= 0) {
			drawImage(data, x, pos.y);
			pos.x= x;
		}
	}

	private void addBottomRightImage(ImageDescriptor desc, Point pos) {
		ImageData data= getImageData(desc);
		int x= pos.x - data.width;
		int y= pos.y - data.height;
		if (x >= 0 && y >= 0) {
			drawImage(data, x, y);
			pos.x= x;
		}
	}

	private void addBottomLeftImage(ImageDescriptor desc, Point pos) {
		ImageData data= getImageData(desc);
		int x= pos.x;
		int y= pos.y - data.height;
		if (x + data.width < getSize().x && y >= 0) {
			drawImage(data, x, y);
			pos.x= x + data.width;
		}
	}


	private void drawTopRight() {
		Point pos= new Point(getSize().x, 0);

		if ((fFlags & ABSTRACT) != 0) {
			addTopRightImage(VdmPluginImages.DESC_OVR_ABSTRACT, pos);
		}
		if ((fFlags & CONSTRUCTOR) != 0) {
			addTopRightImage(VdmPluginImages.DESC_OVR_CONSTRUCTOR, pos);
		}
		if ((fFlags & FINAL) != 0) {
			addTopRightImage(VdmPluginImages.DESC_OVR_FINAL, pos);
		}
		if ((fFlags & VOLATILE) != 0) {
			addTopRightImage(VdmPluginImages.DESC_OVR_VOLATILE, pos);
		}
		if ((fFlags & STATIC) != 0) {
			addTopRightImage(VdmPluginImages.DESC_OVR_STATIC, pos);
		}

	}

	private void drawBottomRight() {
//		Point size= getSize();
//		Point pos= new Point(size.x, size.y);

		
	}

	private void drawBottomLeft() {
//		Point pos= new Point(0, getSize().y);
		

	}
}