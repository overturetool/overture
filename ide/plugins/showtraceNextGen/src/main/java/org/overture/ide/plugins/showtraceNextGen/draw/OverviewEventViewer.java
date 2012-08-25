package org.overture.ide.plugins.showtraceNextGen.draw;

import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;
import jp.co.csk.vdm.toolbox.VDM.VDMRunTimeException;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.*;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;

public class OverviewEventViewer extends TraceEventViewer {

	private final Long ELEMENT_SIZE = 18L;
	
	public OverviewEventViewer()
	{

	}

	public void drawOverview(GenericTabItem tab, Vector<TraceCPU> cpus, Vector<TraceBus> buses)
	{
        Long cury = RESOURCE_VINTERVAL / 2L;
        
        Long ov_uxpos = CPU_X_START;
        Long ov_uypos = 0L;
        Long ov_ustarttime = 0L;
        Long ov_ucurrenttime = 0L;
        
        //Vector<Long> revcpus = data.getOrderedCpus();
        Long cpuid = null;
//        for(int i_43 = cpus.size(); i_43 > 0; i_43--)
//        {
        for(TraceCPU cpu : cpus)
        {
            //Long elem_14 = cpus.get(i_43 - 1);
            //cpuid = elem_14;
            //TraceCPU tmpVal_18 = null;
            //tmpVal_18 = data.getCPU(cpuid);
            //TraceCPU cpu = null;
            //cpu = tmpVal_18;
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
            arg_22 = new Long((new Long(BUS_X_POS.longValue() + (new Long(100L)).longValue())).longValue() - var2_27.longValue());
            np = new Point(arg_22.longValue(), cury.longValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            cpu.setX(CPU_X_START);
            cpu.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = new Long(cury.longValue() + RESOURCE_VINTERVAL.longValue());
        }

//        Vector sq_44 = null;
//        sq_44 = data.getOrderedBuses();
//        Long busid = null;
//        for(Iterator enm_74 = sq_44.iterator(); enm_74.hasNext();)
//        {
        for(TraceBus bus : buses)
        {
//            Long elem_45 = UTIL.NumberToLong(enm_74.next());
//            busid = elem_45;
//            TraceBus bus = null;
//            bus = data.getBUS(busid);
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
            arg_53 = new Long((new Long(BUS_X_POS.longValue() + (new Long(100L)).longValue())).longValue() - var2_58.longValue());
            np = new Point(arg_53.longValue(), cury.longValue());
            nlb.setLocation(np);
            tab.addFigure(nlb);
            bus.setX(CPU_X_START);
            bus.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = new Long(cury.longValue() + RESOURCE_VINTERVAL.longValue());
        }
	}
	
	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu)
	{
		//Update the resource

		//Draw the activity


		/*
		Long cpunm = null;
		//cpunm = pitsw.getCpunm();
		//cpunm = new Long(((NextGenThreadEvent)pitsw).thread.cpu.id);

		//         TraceCPU tmpVal_6 = null;
		//         tmpVal_6 = data.getCPU(cpunm);
		//         TraceCPU cpu = null;
		//         cpu = tmpVal_6;
		//         if((new boolean(ov_ucurrenttime.longvalue() >= ov_ustarttime.longvalue())).booleanvalue())
		//         {
		try {
			updateOvCpu(tab, cpu);
		} catch (CGException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Long tmpVal_15 = null;
		tmpVal_15 = cpu.getX();
		Long x1 = null;
		x1 = tmpVal_15;
		Long tmpVal_16 = null;
		tmpVal_16 = new Long(x1.longValue() + ELEMENT_SIZE.longValue());
		Long x2 = null;
		x2 = tmpVal_16;
		Long tmpVal_19 = null;
		tmpVal_19 = cpu.getY();
		Long y1 = null;
		y1 = tmpVal_19;
		Long tmpVal_20 = null;
		tmpVal_20 = y1;
		Long y2 = null;
		y2 = tmpVal_20;
		try {
			drawOvMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		drawOvSwapOutImage(tab, x1, y1);
		currentXPosition = x2;
		cpu.setX(x2);
		//         }
		cpu.setCurrentThread(null);*/
	}

	public void drawDelayedThreadSwapIn(GenericTabItem pgti, TraceCPU cpu)
	{
		/*
        Long cpunm = null;
        //cpunm = pitsw.getCpunm();
        cpunm = new Long(((NextGenThreadEvent)pitsw).thread.cpu.id);

        TraceCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        TraceCPU cpu = null;
        cpu = tmpVal_6;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvCpu(pgti, tmpVal_6);
            Long tmpVal_15 = null;
            tmpVal_15 = cpu.getX();
            Long x1 = null;
            x1 = tmpVal_15;
            Long tmpVal_16 = null;
            tmpVal_16 = new Long((new Long(x1.longValue() + ELEMENT_uSIZE.longValue())).longValue() - (new Long(1L)).longValue());
            Long x2 = null;
            x2 = tmpVal_16;
            Long tmpVal_21 = null;
            tmpVal_21 = cpu.getY();
            Long y1 = null;
            y1 = tmpVal_21;
            Long tmpVal_22 = null;
            tmpVal_22 = y1;
            Long y2 = null;
            y2 = tmpVal_22;
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.orange);
            drawOvSwapInImage(pgti, x1, y1);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
        //TODO MAA: Should it be used? MVQ: Yes, Martin you dumbass
        Long par_38 = null;
        par_38 = ((NextGenThreadEvent)pitsw).thread.id;
        cpu.setCurrentThread(par_38);*/
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

	public void drawThreadSwapIn(GenericTabItem pgti, TraceCPU cpu)
	{
		/*
            Long cpunm = null;

            //cpunm = pitsw.getCpunm();
            cpunm = new Long(((NextGenThreadEvent)pitsw).thread.cpu.id);

            TraceCPU tmpVal_6 = null;
            tmpVal_6 = data.getCPU(cpunm);
            TraceCPU cpu = null;
            cpu = tmpVal_6;
            if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
            {
                updateOvCpu(pgti, cpu);
                Long x1 = null;
                x1 = cpu.getX();
                Long x2 = new Long((new Long(x1.longValue() + ELEMENT_uSIZE.longValue())).longValue() - (new Long(1L)).longValue());
                Long tmpVal_21 = null;
                tmpVal_21 = cpu.getY();
                Long y1 = null;
                y1 = tmpVal_21;
                Long tmpVal_22 = null;
                tmpVal_22 = y1;
                Long y2 = null;
                y2 = tmpVal_22;
                drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.gray);
                drawOvSwapInImage(pgti, x1, y1);
                currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
                cpu.setX(x2);
            }

            //TODO MAA: Is it needed?? MVQ: Yes, Martin you dumbass
            Long par_38 = null;
            par_38 = ((NextGenThreadEvent)pitsw).thread.id;
            cpu.setCurrentThread(par_38);*/
	}

	public void drawThreadKill(GenericTabItem pgti, TraceCPU cpu)
			{
		/*	
        Long cpunm = null;
        //cpunm = pitsw.getCpunm();
        cpunm = new Long(((NextGenThreadEvent)pitsw).thread.cpu.id);

        TraceCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        TraceCPU cpu = null;
        cpu = tmpVal_6;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvCpu(pgti, cpu);
            Long x1 = null;
            x1 = cpu.getX();
            Long x2 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long y1 = null;
            y1 = cpu.getY();
            Long y2 = y1;
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.red);
            currentXPosition = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }*/
			}

	public void drawThreadCreate(GenericTabItem pgti, TraceCPU cpu)
	{
        Long cpunm = null;
        //NextGenCpu ncpu = ((NextGenThreadEvent)pitc).thread.cpu;


        //Integer cpuId = ncpu.id;
        //cpunm = new Long(cpuId); 
        //TraceCPU cpu = data.getCPU(cpunm);
        //cpu.addThreadId(((NextGenThreadEvent)pitc).thread.id);

//        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
//        {
            //updateCpu(pgti, cpu);
            Long x1 = null;
            x1 = cpu.getX();
            Long x2 = new Long(x1.longValue() + ELEMENT_SIZE.longValue());
            Long tmpVal_19 = null;
            tmpVal_19 = cpu.getY();
            Long y1 = null;
            y1 = tmpVal_19;
            Long tmpVal_20 = null;
            tmpVal_20 = y1;
            Long y2 = null;
            y2 = tmpVal_20;
            //drawMarker(pgti, x1, y1, x2, y2, ColorConstants.green, horizontalImages);
            //currentXPosition = x2;//UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
//        }
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

	public void drawOpCompleted(GenericTabItem pgti, INextGenEvent pioc)
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

	public void drawOpActivate(GenericTabItem pgti, INextGenEvent pioa)
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

	public void drawOpRequest(GenericTabItem pgti, INextGenEvent pior)
			{/*

        	NextGenOperationEvent opEvent = (NextGenOperationEvent) pior;

            if( ov_ucurrenttime >= ov_ustarttime )
            {
    			  currentXPosition = currentXPosition + ELEMENT_uSIZE;
                  updateOvCpu(pgti, data.getCPU(new Long(opEvent.thread.cpu.id)));
            }

            //Check for remote synchronous calls and update thread status to blocked
            if(!opEvent.operation.isAsync)
            {
            	//FIXME MVQ: opEvent doesn't ever seem to have a reference to an object?!
                if(opEvent.object != null)
                {
                    boolean cpuHasObject = opEvent.object.cpu.id == opEvent.thread.cpu.id;
                    if(!cpuHasObject)
                    {
                    	data.getThread(opEvent.thread.id).setStatus(true);
                    }
                }
            }*/
			}

	
	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime) {
		
		System.out.println("Drawing time marker: " + markerTime + " Y position: " + markerYPosition + " X Position: " + markerXPosition);
		
		markerXPosition += 150;
		
	    //Long dy = new Long(RESOURCE_VINTERVAL.longValue() / (new Long(2L)).longValue());
	    //Line line1 = new Line(markerXPosition, new Long(dy.longValue() - (new Long(10L)).longValue()), currentXPosition, new Long(markerYPosition.longValue() - dy.longValue()));
	    Line line2 = new Line(markerXPosition, 0L, markerXPosition, 0L);
	    
	    RotatedLabel label = new RotatedLabel(markerTime.toString(), tab.getCurrentFont());

	    Dimension labelSize = label.getSize();;
	    Long labelWidth = new Long(labelSize.width);
	    Long xoffset = new Long(labelWidth / 2L);
	    
	    Point labelPoint = new Point(new Long(markerXPosition - xoffset).intValue(), new Long(markerYPosition).intValue());
	    
	    //line1.setForegroundColor(ColorConstants.lightGray);
	    //line1.setDot();
	    
	    //tab.addFigure(line1);
	    tab.addFigure(line2);
	    
	    label.setLocation(labelPoint);
	    tab.addFigure(label);
	}

}
