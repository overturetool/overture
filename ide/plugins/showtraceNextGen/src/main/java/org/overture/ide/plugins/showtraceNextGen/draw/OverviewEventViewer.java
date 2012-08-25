package org.overture.ide.plugins.showtraceNextGen.draw;

import java.util.Collections;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.*;

public class OverviewEventViewer extends TraceEventViewer {

	private final Long ELEMENT_SIZE = 18L;
	private final Long BUSMSG_ARROW_OFFSET = 8L;
	
	public OverviewEventViewer()
	{

	}

	public void drawOverview(GenericTabItem tab, Vector<TraceCPU> cpus, Vector<TraceBus> buses)
	{
        Long cury = RESOURCE_VINTERVAL / 2L;
        
        //Draw CPU labels in reverse order
        Collections.reverse(cpus);

        for(TraceCPU cpu : cpus)
        {   //FIXME: Partly code generated
            NormalLabel nlb = null;
            String arg_20 = null;
            arg_20 = cpu.getName();
            org.eclipse.swt.graphics.Font arg_21 = null;
            arg_21 = tab.getCurrentFont();
            nlb = new NormalLabel(arg_20, arg_21);
            Point np = null;
            Long arg_22 = null;
            Long var2_27 = null;
            Dimension tmpRec_28 = null;
            tmpRec_28 = nlb.getSize();
            var2_27 = new Long(tmpRec_28.width);
            arg_22 = BUS_LABEL_X_POS.longValue() - var2_27.longValue();
            np = new Point(arg_22.longValue(), cury.longValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            cpu.setX(CPU_X_START);
            cpu.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = new Long(cury.longValue() + RESOURCE_VINTERVAL.longValue());
            
            //Draw CPU line
            drawTimeline(tab, cpu.getX(), cpu.getY(), tab.getHorizontalSize(), cpu.getY());
        }

        //Draw Bus labels
        for(TraceBus bus : buses)
        {   //FIXME: Partly code generated
            NormalLabel nlb = null;
            String arg_51 = null;
            arg_51 = bus.getName();
            org.eclipse.swt.graphics.Font arg_52 = null;
            arg_52 = tab.getCurrentFont();
            nlb = new NormalLabel(arg_51, arg_52);
            Point np = null;
            Long arg_53 = null;
            Long var2_58 = null;
            Dimension tmpRec_59 = null;
            tmpRec_59 = nlb.getSize();
            var2_58 = new Long(tmpRec_59.width);
            arg_53 = BUS_LABEL_X_POS.longValue() - var2_58.longValue();
            np = new Point(arg_53.longValue(), cury.longValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            bus.setX(CPU_X_START);
            bus.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = new Long(cury.longValue() + RESOURCE_VINTERVAL.longValue());
            
            //Draw Bus line
            drawTimeline(tab, bus.getX(), bus.getY(), tab.getHorizontalSize(), bus.getY());
        }
	}
	
	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		drawSwapImage(tab, x1, y1, SWAP_DIRECTION.NORTH);
		cpu.setX(x2);

		cpu.setCurrentThread(null);
	}

	public void drawDelayedThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.orange);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.SOUTH);
        cpu.setX(x2);

        //TODO MAA: Should it be used? MVQ: Yes, Martin you dumbass
        cpu.setCurrentThread(thread.getId());
	}
	
	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.SOUTH);
        cpu.setX(x2);

        cpu.setCurrentThread(thread.getId());
	}
	
	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax() < cpu.getX() ? cpu.getX() : tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.red);
        cpu.setX(x2);
	}

	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax() < cpu.getX() ? cpu.getX() : tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
        
        drawMarker(tab, x1, y1, x2, y2, ColorConstants.green);
        cpu.setX(x2);
	}
	
	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destionationObject)
	{
		updateCpu(tab, cpu, thread);
		
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = cpu.getY();
		Long y2 = y1;
		
	    Line line = new Line(x1, y1, x2, y2);
        line.setForegroundColor(ColorConstants.blue);
        line.setLineWidth(3L);
        tab.addFigure(line);
        cpu.setX(tab.getXMax());
	}
	
	public void drawMessageRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op)
	{
		updateCpu(tab, cpu, thread);
		
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.lightGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from CPU to bus
		y1 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		y2 = bus.getY() - BUSMSG_ARROW_OFFSET;
		String label = " call "+op.getName()+" ";
		
		drawVerticalArrow(tab, x1, y1, y2, label, ColorConstants.darkBlue);
	}

	public void drawReplyRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op)
	{
		updateCpu(tab, cpu, thread);
		
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.lightGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from CPU to bus
		y1 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		y2 = bus.getY() - BUSMSG_ARROW_OFFSET;
		String label = " return from "+op.getName()+" ";
		
		drawVerticalArrow(tab, x1, y1, y2, label, ColorConstants.darkBlue);
	}
	
	public void drawMessageActivated(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op)
	{
		updateCpu(tab, cpu, thread);
		
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
	}

	public void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceBus bus, TraceOperation op)
	{
		updateCpu(tab, cpu, thread);
		
		//Draw marker on bus
		Long x1 = tab.getXMax();
		Long x2 = x1 + ELEMENT_SIZE;
		Long y1 = bus.getY();
		Long y2 = y1;
		
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.darkGray);
		bus.setX(x2); //TODO: MVQ: This info is not used?
		
		//Draw arrow from bus to CPU
		y1 = bus.getY() - BUSMSG_ARROW_OFFSET;
		y2 = cpu.getY() + BUSMSG_ARROW_OFFSET;
		String label = " "+op.getName()+" ";
		
		drawVerticalArrow(tab, x2, y1, y2, label, ColorConstants.darkBlue);
	}

	
	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime)
	{	//FIXME: Magic numbers
		Long markerStartX = tab.getXMax() < CPU_X_START ? CPU_X_START : tab.getXMax();
		Long markerStartY = 10L;
		Long markerEndX = markerStartX;
		Long markerEndY = tab.getYMax() + 5L;
		
		//Draw vertical marker line
		Line markerLine = new Line(markerStartX, markerStartY, markerEndX, markerEndY);
		markerLine.setForegroundColor(ColorConstants.lightGray);
		markerLine.setDot();
		tab.addBackgroundFigure(markerLine);
		
		//Draw time label
		Line labelLine = new Line(markerEndX, markerEndY + 5L, markerEndX, markerEndY + 10L);
		tab.addBackgroundFigure(labelLine);
		String labelText = markerTime.toString();
		RotatedLabel timeLabel = new RotatedLabel(labelText, tab.getCurrentFont());	
		int labelStartX = markerEndX.intValue() - timeLabel.getSize().width/2;
		int labelStartY = markerEndY.intValue() + 15;
		Point labelLocation = new Point(labelStartX, labelStartY);
		timeLabel.setLocation(labelLocation);
		tab.addBackgroundFigure(timeLabel);
	}
	
    private void updateCpu(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
    {
    	if(cpu.getX() < tab.getXMax())
    	{
		    if(!cpu.isIdle() && thread != null)
		    {
			    Line line = new Line(cpu.getX(), cpu.getY(), tab.getXMax(), cpu.getY());
		        line.setForegroundColor(ColorConstants.blue);
		        line.setLineWidth(3L);
		        if(thread.getStatus())
		            line.setDot();
		        
		        tab.addFigure(line);
		        cpu.setX(tab.getXMax());
		        
		    }
    	}
	    
	    
//TODO: MVQ: Check conjectures?
//	    if(cpu.hasCurrentThread())
//	    {
//	    	Long thrid = data.getThread(cpu.getCurrentThread()).getId();
//	        checkConjectureLimits(pgti, ov_uxpos - ELEMENT_uSIZE, cpu.getY(), ov_ucurrenttime, thrid);
//	    }
    }
    
    //Helper
	private void drawVerticalArrow(GenericTabItem pgti, Long x, Long y1, Long y2, String str, Color clr)
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

}
