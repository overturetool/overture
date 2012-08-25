package org.overture.ide.plugins.showtraceNextGen.draw;

import java.util.Collections;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.*;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public class OverviewEventViewer extends TraceEventViewer {

	private final Long ELEMENT_SIZE = 18L;
	
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
            arg_22 = BUS_X_POS.longValue() - var2_27.longValue();
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
            arg_53 = BUS_X_POS.longValue() - var2_58.longValue();
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

        //TODO MAA: Should it be used? MVQ: Yes, Martin you dumbass
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
	
	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		updateCpu(tab, cpu, thread);
		
//		NextGenOperationEvent opEvent = (NextGenOperationEvent) pior;
//	
//	    if( ov_ucurrenttime >= ov_ustarttime )
//	    {
//			  currentXPosition = currentXPosition + ELEMENT_uSIZE;
//	          updateOvCpu(pgti, data.getCPU(new Long(opEvent.thread.cpu.id)));
//	    }
	
	    //Check for remote synchronous calls and update thread status to blocked
//	    if(!opEvent.operation.isAsync)
//	    {
//	    	//FIXME MVQ: opEvent doesn't ever seem to have a reference to an object?!
//	        if(opEvent.object != null)
//	        {
//	            boolean cpuHasObject = opEvent.object.cpu.id == opEvent.thread.cpu.id;
//	            if(!cpuHasObject)
//	            {
//	            	data.getThread(opEvent.thread.id).setStatus(true);
//	            }
//	        }
//	    }
	}
	
	public void drawOpActivate(GenericTabItem pgti, TraceCPU cpu, TraceThread thread)
	{/*
    if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
    {
        Long cpunm = null;
        //cpunm = pioa.getCpunm();
        cpunm = new Long(((NextGenOperationEvent)pioa).thread.cpu.id);
        TraceCPU tmpVal_9 = null;
        tmpVal_9 = data.getCPU(cpunm);
        TraceCPU cpu = null;
        cpu = tmpVal_9;
        currentXPosition = UTIL.NumberToLong(UTIL.clone(new Long(currentXPosition.longValue() + ELEMENT_uSIZE.longValue())));
        updateOvCpu(pgti, cpu);
    }*/
	}
	
	public void drawOpCompleted(GenericTabItem pgti, TraceCPU cpu, TraceThread thread)
	{/*
            if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
            {
                Long cpunm = null;
                //cpunm = pioc.getCpunm();
                cpunm = new Long(((NextGenOperationEvent)pioc).thread.cpu.id);
                TraceCPU tmpVal_9 = null;
                tmpVal_9 = data.getCPU(cpunm);
                TraceCPU cpu = null;
                cpu = tmpVal_9;
                currentXPosition = UTIL.NumberToLong(UTIL.clone(new Long(currentXPosition.longValue() + ELEMENT_uSIZE.longValue())));
                updateOvCpu(pgti, cpu);
            }*/
			}

	public void drawReplyRequest(GenericTabItem pgti, INextGenEvent pitrr)
	{	/*
    	NextGenBusMessageReplyRequestEvent replyEvent = (NextGenBusMessageReplyRequestEvent) pitrr;

        Long busid = null;
        //busid = pitrr.getBusid();
        busid = new Long(replyEvent.replyMessage.bus.id);

        Long msgid = null;
        //msgid = pitrr.getMsgid();
        msgid = replyEvent.replyMessage.id;

        TraceBus bus = null;
        bus = data.getBUS(busid);
        TraceMessage msg = null;
        msg = data.getMessage(msgid);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            currentXPosition = UTIL.NumberToLong(UTIL.clone(new Long(currentXPosition.longValue() + (new Long(6L)).longValue())));
            updateOvBus(pgti, bus);
            Long x1 = null;
            x1 = bus.getX();
            Long x2 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long tmpVal_25 = null;
            tmpVal_25 = bus.getY();
            Long y1 = null;
            y1 = tmpVal_25;
            Long tmpVal_26 = null;
            tmpVal_26 = y1;
            Long y2 = null;
            y2 = tmpVal_26;
            Long ycpu = null;
            Long var1_29 = null;
            TraceCPU obj_30 = null;
            Long par_31 = null;
            par_31 = msg.getFromCpu();
            obj_30 = data.getCPU(par_31);
            var1_29 = obj_30.getY();
            ycpu = new Long(var1_29.longValue() + (new Long(8L)).longValue());
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_47 = null;
            String var1_48 = null;
            String var2_50 = "";
            //var2_50 = msg.getDescr(); //TODO: msg description
            var2_50 = replyEvent.message.operation.name;
            var1_48 = (new String(" return from ")).concat(var2_50);
            tmpArg_v_47 = var1_48.concat(new String(" "));
            drawVerticalArrow(pgti, x1, ycpu, new Long(y1.longValue() - (new Long(8L)).longValue()), tmpArg_v_47, ColorConstants.darkBlue);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }*/
	}

	public void drawMessageCompleted(GenericTabItem pgti, INextGenEvent pitmc)
	{
		/*
    	NextGenBusMessageEvent busMessageEvent = (NextGenBusMessageEvent) pitmc;

        Long msgid = busMessageEvent.message.id;

        TraceMessage msg = null;
        msg = data.getMessage(msgid);
        Long busid = null;
        busid = msg.getBusId();
        TraceBus bus = null;
        bus = data.getBUS(busid);
        TraceCPU tmpVal_13 = null;
        Long par_14 = null;
        par_14 = msg.getToCpu();
        tmpVal_13 = data.getCPU(par_14);
        TraceCPU cpu = null;
        cpu = tmpVal_13;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvBus(pgti, bus);
            Long tmpVal_22 = null;
            tmpVal_22 = bus.getX();
            Long x1 = null;
            x1 = tmpVal_22;
            Long tmpVal_23 = null;
            tmpVal_23 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long x2 = null;
            x2 = tmpVal_23;
            Long tmpVal_26 = null;
            tmpVal_26 = bus.getY();
            Long y1 = null;
            y1 = tmpVal_26;
            Long tmpVal_27 = null;
            tmpVal_27 = y1;
            Long y2 = null;
            y2 = tmpVal_27;
            Long ycpu = null;
            Long var1_30 = null;
            var1_30 = cpu.getY();
            ycpu = new Long(var1_30.longValue() + (new Long(8L)).longValue());
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.darkGray);
            String tmpArg_v_46 = null;
            String var1_47 = null;
            String var2_49 = "";
            //var2_49 = msg.getDescr(); //TODO: Message description?
            var2_49 = busMessageEvent.message.operation.name;
            var1_47 = (new String(" ")).concat(var2_49);
            tmpArg_v_46 = var1_47.concat(new String(" "));
            drawVerticalArrow(pgti, x2, new Long(y1.longValue() - (new Long(8L)).longValue()), ycpu, tmpArg_v_46, ColorConstants.darkBlue);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(new Long(x2.longValue() + (new Long(6L)).longValue())));
            updateOvCpu(pgti, cpu);
            bus.setX(x2);
        }

        //If this is a reply to an earlier request then unblock the thread which did the request
        if(busMessageEvent.message.receiverThread != null)
        {
			TraceThread thr = data.getThread(busMessageEvent.message.receiverThread.id);
			thr.setStatus(false);
        }*/
	}

	public void drawMessageActivated(GenericTabItem pgti, INextGenEvent pitma)
	{
		/*
    	NextGenBusMessageEvent busMessageEvent = (NextGenBusMessageEvent) pitma;
        Long msgid = busMessageEvent.message.id;
        msgid = ((NextGenBusMessageEvent)pitma).message.id;

        Long busid = null;
        TraceMessage obj_7 = null;
        obj_7 = data.getMessage(msgid);
        busid = obj_7.getBusId();
        TraceBus bus = null;
        bus = data.getBUS(busid);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvBus(pgti, bus);
            Long tmpVal_18 = null;
            tmpVal_18 = bus.getX();
            Long x1 = null;
            x1 = tmpVal_18;
            Long tmpVal_19 = null;
            tmpVal_19 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long x2 = null;
            x2 = tmpVal_19;
            Long tmpVal_22 = null;
            tmpVal_22 = bus.getY();
            Long y1 = null;
            y1 = tmpVal_22;
            Long tmpVal_23 = null;
            tmpVal_23 = y1;
            Long y2 = null;
            y2 = tmpVal_23;
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.gray);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }*/
	}

	public void drawMessageRequest(GenericTabItem pgti, INextGenEvent pitmr)
			{
		/*
    	NextGenBusMessageEvent busMessageEvent = (NextGenBusMessageEvent) pitmr;

        Long busid = null;
        //busid = pitmr.getBusid();
        busid = new Long(busMessageEvent.message.bus.id);

        Long msgid = null;
        //msgid = pitmr.getMsgid();
        msgid = busMessageEvent.message.id;

        TraceBus bus = null;
        bus = data.getBUS(busid);
        TraceMessage msg = null;
        msg = data.getMessage(msgid);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            currentXPosition = UTIL.NumberToLong(UTIL.clone(new Long(currentXPosition.longValue() + (new Long(6L)).longValue())));
            updateOvBus(pgti, bus);
            Long tmpVal_21 = null;
            tmpVal_21 = bus.getX();
            Long x1 = null;
            x1 = tmpVal_21;
            Long tmpVal_22 = null;
            tmpVal_22 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long x2 = null;
            x2 = tmpVal_22;
            Long tmpVal_25 = null;
            tmpVal_25 = bus.getY();
            Long y1 = null;
            y1 = tmpVal_25;
            Long tmpVal_26 = null;
            tmpVal_26 = y1;
            Long y2 = null;
            y2 = tmpVal_26;
            Long ycpu = null;
            Long var1_29 = null;
            TraceCPU obj_30 = null;
            Long par_31 = null;
            par_31 = msg.getFromCpu();
            obj_30 = data.getCPU(par_31);
            var1_29 = obj_30.getY();
            ycpu = new Long(var1_29.longValue() + (new Long(8L)).longValue());
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_47 = null;
            String var1_48 = null;
            String var2_50 = "";
            //var2_50 = msg.getDescr(); //TODO
            var2_50 = busMessageEvent.message.operation.name;
            var1_48 = (new String(" call ")).concat(var2_50);
            tmpArg_v_47 = var1_48.concat(new String(" "));
            drawVerticalArrow(pgti, x1, ycpu, new Long(y1.longValue() - (new Long(8L)).longValue()), tmpArg_v_47, ColorConstants.darkBlue);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }*/
			}

	
	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime) {
		

	}
	
    private void updateCpu(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
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
	    
	    
//TODO: MVQ: Check conjectures?
//	    if(cpu.hasCurrentThread())
//	    {
//	    	Long thrid = data.getThread(cpu.getCurrentThread()).getId();
//	        checkConjectureLimits(pgti, ov_uxpos - ELEMENT_uSIZE, cpu.getY(), ov_ucurrenttime, thrid);
//	    }
    }

}
