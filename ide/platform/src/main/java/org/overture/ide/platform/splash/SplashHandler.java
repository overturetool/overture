package org.overture.ide.platform.splash;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;
import org.overture.ide.platform.Activator;

public class SplashHandler extends BasicSplashHandler
{

	@Override
	public void init(Shell splash)
	{
		super.init(splash);

		int foregroundColorInteger;
		// String foregroundColorString = null;

		// try
		// {
		// foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		//
		// } catch (Exception ex)
		// {

		foregroundColorInteger = 0xD2D7FF; // off white

		// }
		setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16,

		(foregroundColorInteger & 0xFF00) >> 8,

		foregroundColorInteger & 0xFF));

		final Point buildIdPoint = new Point(350, 200);
		final String bundleVersion = "v"
				+ Activator.getDefault().getBundle().getHeaders().get("Bundle-Version");

		getContent().addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.setForeground(getForeground());
				e.gc.drawText(bundleVersion, buildIdPoint.x, buildIdPoint.y, true);
			}

		});
	}

	@Override
	public void dispose()
	{

		super.dispose();
	}

}
