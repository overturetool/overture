package org.overture.ide.plugins.showtraceNextGen.draw;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.overture.ide.plugins.showtraceNextGen.data.TraceObject;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;

public abstract class TraceViewer {

	protected enum SWAP_DIRECTION	{ NORTH, SOUTH, EAST, WEST }
	
	protected final Long CPU_X_START = new Long(150L);
	protected final Long CPU_X_OFFSET= new Long(40L);
	protected final Long CPU_Y_POS= new Long(25L);;
	protected final Long CPU_WIDTH = new Long(150L);
	protected final Long CPU_HEIGHT = new Long(40L);
    
	protected final Long BUS_X_POS = new Long(125L);
	protected final Long BUS_Y_START = new Long(90L);
	protected final Long BUS_Y_OFFSET = new Long(30L);
	
	protected final Long MARKER_START_END_HALFLENGTH = new Long(5L);
	protected final Long MARKER_THICKNESS = new Long(3L);

	protected final Long ELEMENT_SIZE = new Long(18L);
	
	protected final Long OBJECT_WIDTH_FACTOR = new Long(10L);
	
	protected void Object2ObjectArrow(GenericTabItem pgti, TraceObject psrc, TraceObject pdest, String pstr)
			throws CGException
			{
		Long psx = null;
		psx = psrc.getX();
		Long psy = null;
		psy = psrc.getY();
		Long pdx = null;
		pdx = pdest.getX();
		Long pdy = null;
		pdy = pdest.getY();
		Line line = new Line(psx, psy, psx, new Long(psy.longValue() + (new Long(20L)).longValue()));
		NormalLabel lbl = null;
		org.eclipse.swt.graphics.Font arg_18 = null;
		arg_18 = pgti.getCurrentFont();
		lbl = new NormalLabel(pstr, arg_18);
		line.setLineWidth(new Long(3L));
		line.setForegroundColor(ColorConstants.blue);
		pgti.addFigure(line);
		line = (Line)UTIL.clone(new Line(pdx, new Long(pdy.longValue() + (new Long(20L)).longValue()), pdx, new Long(pdy.longValue() + (new Long(40L)).longValue())));
		line.setLineWidth(new Long(3L));
		line.setForegroundColor(ColorConstants.blue);
		pgti.addFigure(line);
		line = (Line)UTIL.clone(new Line(psx, new Long(psy.longValue() + (new Long(20L)).longValue()), pdx, new Long(psy.longValue() + (new Long(20L)).longValue())));
		line.setForegroundColor(ColorConstants.blue);
		pgti.addFigure(line);
		
		if((new Boolean(psx.longValue() < pdx.longValue())).booleanValue())
		{
			Point pt = new Point((new Long(psx.longValue() + (new Long(20L)).longValue())).intValue(), (new Long(psy.longValue() + (new Long(2L)).longValue())).intValue());
			lbl.setLocation(pt);
			pgti.addFigure(lbl);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() - (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(16L)).longValue()), new Long(pdx.longValue() - (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() - (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(24L)).longValue()), new Long(pdx.longValue() - (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			pgti.addFigure(line);
		} else
		{
			Point pt = null;
			Long arg_56 = null;
			Long var2_61 = null;
			Dimension tmpRec_62 = null;
			tmpRec_62 = lbl.getSize();
			var2_61 = new Long(tmpRec_62.width);
			arg_56 = new Long((new Long(psx.longValue() - (new Long(20L)).longValue())).longValue() - var2_61.longValue());
			pt = new Point(arg_56.intValue(), (new Long(psy.longValue() + (new Long(2L)).longValue())).intValue());
			lbl.setLocation(pt);
			pgti.addFigure(lbl);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(16L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(24L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			pgti.addFigure(line);
		}
		
//		ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uypos.longValue() + (new Long(40L)).longValue())));
//		psrc.setY(ov_uypos);
//		pdest.setY(ov_uypos);
	}

	protected void drawVerticalArrow(GenericTabItem pgti, Long x, Long y1, Long y2, String str, Color clr)
			throws CGException
			{
		Line line = new Line(x, y1, x, y2);
		NormalLabel lbl = null;
		String arg_11 = null;
		String var1_13 = null;
		var1_13 = (new String(" ")).concat(str);
		arg_11 = var1_13.concat(new String(" "));
		org.eclipse.swt.graphics.Font arg_12 = null;
		arg_12 = pgti.getCurrentFont();
		lbl = new NormalLabel(arg_11, arg_12);
		line.setForegroundColor(clr);
		line.setToolTip(lbl);
		pgti.addFigure(line);
		if((new Boolean(y1.longValue() < y2.longValue())).booleanValue())
		{
			line = (Line)UTIL.clone(new Line(new Long(x.longValue() - (new Long(4L)).longValue()), new Long(y2.longValue() - (new Long(8L)).longValue()), x, y2));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(x.longValue() + (new Long(4L)).longValue()), new Long(y2.longValue() - (new Long(8L)).longValue()), x, y2));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
		} else
		{
			line = (Line)UTIL.clone(new Line(new Long(x.longValue() - (new Long(4L)).longValue()), new Long(y2.longValue() + (new Long(8L)).longValue()), x, y2));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(x.longValue() + (new Long(4L)).longValue()), new Long(y2.longValue() + (new Long(8L)).longValue()), x, y2));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
		}
			}

	protected void drawHorizontalArrow(GenericTabItem pgti, Long x1, Long x2, Long y, String str, Color clr)
			throws CGException
			{
		Line line = new Line(x1, y, x2, y);
		NormalLabel lbl = null;
		String arg_11 = null;
		String var1_13 = null;
		var1_13 = (new String(" ")).concat(str);
		arg_11 = var1_13.concat(new String(" "));
		org.eclipse.swt.graphics.Font arg_12 = null;
		arg_12 = pgti.getCurrentFont();
		lbl = new NormalLabel(arg_11, arg_12);
		line.setForegroundColor(clr);
		line.setToolTip(lbl);
		pgti.addFigure(line);
		if((new Boolean(x1.longValue() < x2.longValue())).booleanValue())
		{
			line = (Line)UTIL.clone(new Line(x1, y, new Long(x1.longValue() + (new Long(8L)).longValue()), new Long(y.longValue() - (new Long(4L)).longValue())));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(x1, y, new Long(x1.longValue() + (new Long(8L)).longValue()), new Long(y.longValue() + (new Long(4L)).longValue())));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
		} else
		{
			line = (Line)UTIL.clone(new Line(new Long(x1.longValue() - (new Long(8L)).longValue()), new Long(y.longValue() - (new Long(4L)).longValue()), x1, y));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(x1.longValue() - (new Long(8L)).longValue()), new Long(y.longValue() + (new Long(4L)).longValue()), x1, y));
			line.setForegroundColor(clr);
			pgti.addFigure(line);
		}
	}
	
    protected void drawMarker(GenericTabItem tab, Long x1, Long y1, Long x2, Long y2, Color clr)
	{
		//Draw main line (Bold and colored)
	    Line line = new Line(x1, y1, x2, y2);
	    line.setLineWidth(MARKER_THICKNESS);
	    line.setForegroundColor(clr);
	    tab.addFigure(line);
	    
	    //Highlight start and end of the main line with small black lines
	    //First determine if line is vertical or horizontal
	    if(y1 == y2)
	    {
	        line = new Line(x1, y1 - MARKER_START_END_HALFLENGTH, x1, y1 + MARKER_START_END_HALFLENGTH);
	        tab.addFigure(line);
	        line = new Line(x2, y1 - MARKER_START_END_HALFLENGTH, x2, y1 + MARKER_START_END_HALFLENGTH);
	        tab.addFigure(line);
	    }
	    else if(x1 == x2)
	    {
	        line = new Line(x1 - MARKER_START_END_HALFLENGTH, y1, x1 + MARKER_START_END_HALFLENGTH, y1);
	        tab.addFigure(line);
	        line = new Line(x1 - MARKER_START_END_HALFLENGTH, y2, x1 + MARKER_START_END_HALFLENGTH, y2);
	        tab.addFigure(line);
	    }
	
	}
    
    protected void drawSwapImage(GenericTabItem tab, Long x, Long y, SWAP_DIRECTION dir)
    {
    	org.eclipse.swt.graphics.Image image = null;
    	String imagePath = "";
    	Dimension dim = null;
    	Point point = null;
    	
    	switch(dir)
    	{
	    	case NORTH: imagePath = tab.composePath("icons", "vswapout.gif");
	    				dim = new Dimension(16, 20);
	    				point = new Point(x + 2L, y - 24L);
	    				break;
	    	case SOUTH: imagePath = tab.composePath("icons", "vswapin.gif");
	    				dim = new Dimension(16, 20);
	    				point = new Point(x + 2L, y - 24L);
	    				break;
	    	case EAST:  imagePath = tab.composePath("icons", "hswapout.gif");
	    				dim = new Dimension(20, 16);
	    				point = new Point(x + 8L, y + 2L);
	    				break;
	    	case WEST:  imagePath = tab.composePath("icons", "hswapin.gif");
	    				dim = new Dimension(20, 16);
	    				point = new Point(x + 2L, y - 24L);
	    				break;
    	}
    	
        image = tab.getImage(imagePath);
        
        
        if(image != null && point != null && dim != null)
        {
            ImageFigure imagefig = new ImageFigure(image);
            imagefig.setLocation(point);
            imagefig.setSize(dim);
            tab.addFigure(imagefig);
        }
    }
    
    protected void drawTimeline(GenericTabItem tab, Long x1, Long y1, Long x2, Long y2)
    {
		Line timeLine = new Line(x1, y1, x2, y2);
        timeLine.setForegroundColor(ColorConstants.lightGray);
        timeLine.setDot();
        tab.addBackgroundFigure(timeLine);
    }
}
