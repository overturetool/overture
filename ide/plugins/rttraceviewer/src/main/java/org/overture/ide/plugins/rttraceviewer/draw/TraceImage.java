package org.overture.ide.plugins.rttraceviewer.draw;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;


public class TraceImage extends ImageFigure implements TraceFigure
{
	public TraceImage(Image image) 
	{
		super(image);
	}

	@Override
	public void dispose()
	{
		Image image = super.getImage();
		if(!image.isDisposed())
		{
			image.dispose();
		}
		
	}

}
