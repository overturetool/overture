package org.overture.ide.plugins.showtraceNextGen.draw;

import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.overture.ide.plugins.showtraceNextGen.data.*;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public class CpuEventViewer  extends TraceEventViewer {

	public void drawView(GenericTabItem tab, Vector<TraceBus> buses)
	{
		Long currentXPos = BUS_X_START; 
		Long yPos = CPU_Y_POS + CPU_HEIGHT + ELEMENT_SIZE;

		for(TraceBus bus : buses)
		{
			NormalLabel nlb = new NormalLabel(bus.getName(), tab.getCurrentFont());

			RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);
			Point np = new Point(currentXPos.intValue(), CPU_Y_POS.intValue());

			if(bus.isVirtual())
			{
				nrr.setDash();
				nrr.setForegroundColor(ColorConstants.darkGray);
			}

			nrr.setLocation(np);
			nrr.setSize(CPU_WIDTH, CPU_HEIGHT);
			tab.addFigure(nrr);
			
			
			//Draw Object timeline
            Long lineXPos = currentXPos + new Long(CPU_WIDTH/2);
            Long lineYStartPos = yPos;
            Long lineYEndPos = tab.getVerticalSize();
            drawTimeline(tab, lineXPos, lineYStartPos, lineXPos, lineYEndPos);
	
			bus.setX(currentXPos + new Long((CPU_WIDTH/2))); 
			bus.setY(yPos);
			
			currentXPos = currentXPos + CPU_WIDTH + CPU_X_OFFSET;         
		}
		
		Line spacer = new Line(0L, CPU_Y_POS, 0L, yPos);
		spacer.setForegroundColor(ColorConstants.white);
		tab.addFigure(spacer);
	}

	public void drawReplyRequest(GenericTabItem pgti, INextGenEvent pitrr)
	{
		/*
		NextGenBusMessageReplyRequestEvent rEvent = (NextGenBusMessageReplyRequestEvent)pitrr;
		Long busid = new Long(rEvent.message.bus.id);     
		Long msgid = rEvent.message.id;
		Long thrid = new Long(rEvent.replyMessage.callerThread.id);

		TraceBus bus = data.getBUS(busid);
		TraceMessage msg = data.getMessage(msgid);

		if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
		{
			ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uypos.longValue() + (new Long(10L)).longValue())));
			updateCpuBus(pgti, bus);
			Long x1 = null;
			x1 = bus.getX();
			Long x2 = x1;
			Long tmpVal_23 = null;
			tmpVal_23 = bus.getY();
			Long y1 = null;
			y1 = tmpVal_23;
			Long tmpVal_24 = null;
			tmpVal_24 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
			Long y2 = null;
			y2 = tmpVal_24;
			TraceThread thr = null;
			//Long par_29 = null;
			//par_29 = msg.getFromThread();
			//thr = data.getThread(par_29);
			thr = data.getThread(thrid);
			TraceObject obj = thr.getCurrentObject();
			Long xobj = null;
			Long var1_34 = null;
			var1_34 = obj.getX();
			xobj = new Long(var1_34.longValue() - (new Long(10L)).longValue());
			drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
			String tmpArg_v_50 = null;
			String var1_51 = null;
			String var2_53 = null;
			//var2_53 = msg.getDescr();
			var2_53 = rEvent.message.operation.name;
			var1_51 = (new String(" return from ")).concat(var2_53);
			tmpArg_v_50 = var1_51.concat(new String(" "));
			drawHorizontalArrow(pgti, new Long(x1.longValue() + (new Long(10L)).longValue()), xobj, y1, tmpArg_v_50, ColorConstants.darkGreen);
			ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
			bus.setY(y2);
		}*/
	}

	public void drawThreadSwapOut(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		TraceObject obj = thread.getCurrentObject();
		updateObject(tab, obj);
		

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

        drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.EAST);
        
		obj.setY(y2);
	}

	public void drawThreadSwapIn(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		TraceObject obj = thread.getCurrentObject();
		updateObject(tab, obj);
		

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

        drawMarker(tab, x1, y1, x2, y2, ColorConstants.gray);
        drawSwapImage(tab, x1, y1, SWAP_DIRECTION.WEST);
        
		obj.setY(y2);
	}
	
	public void drawDelayedThreadSwapIn(GenericTabItem pgti, TraceCPU cpu, TraceThread thread)
	{
		/*
		NextGenThreadEvent threadEvent = (NextGenThreadEvent) pitsw;


		Long objref = null;
		Boolean cond_6 = null;

		//cond_6 = pitsw.hasObjref();
		cond_6 = threadEvent.thread.object != null;

		if(cond_6.booleanValue())
		{
			//objref = pitsw.getObjref();
			objref = new Long(threadEvent.thread.object.id);
		}
		else
			objref = new Long(0L);
		Long thrid = null;
		//thrid = pitsw.getId();
		thrid = new Long(threadEvent.thread.id);
		TraceThread thr = null;
		thr = data.getThread(thrid);
		Long cpunm = null;

		//cpunm = pitsw.getCpunm();
		cpunm = new Long(threadEvent.thread.cpu.id);

		TraceObject obj = null;
		obj = data.getObject(objref);
		TraceCPU tmpVal_13 = null;
		tmpVal_13 = data.getCPU(cpunm);
		TraceCPU cpu = null;
		cpu = tmpVal_13;
		cpu.setCurrentThread(thrid);
		thr.pushCurrentObject(obj);
		if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
		{
			updateCpuObject(pgti, cpu, obj);
			Long tmpVal_27 = null;
			tmpVal_27 = obj.getX();
			Long x1 = null;
			x1 = tmpVal_27;
			Long tmpVal_28 = null;
			tmpVal_28 = x1;
			Long x2 = null;
			x2 = tmpVal_28;
			Long tmpVal_29 = null;
			tmpVal_29 = obj.getY();
			Long y1 = null;
			y1 = tmpVal_29;
			Long tmpVal_30 = null;
			tmpVal_30 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
			Long y2 = null;
			y2 = tmpVal_30;
			drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.gray);
			drawCpuSwapInImage(pgti, x1, y1);
			ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
			obj.setY(y2);
		}*/
	}

	public void drawThreadKill(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		TraceObject obj = thread.getCurrentObject();
		
		updateObject(tab, obj);

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.red);
		
		obj.setY(y2);
	}

	public void drawThreadCreate(GenericTabItem tab, TraceCPU cpu, TraceThread thread)
	{
		TraceObject obj = thread.getCurrentObject();
		
		updateObject(tab, obj);

		Long x1 = obj.getX();
		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1 + ELEMENT_SIZE;

		drawMarker(tab, x1, y1, x2, y2, ColorConstants.green);
		
		obj.setY(y2);
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
		if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
		{
			updateCpuBus(pgti, bus);
			Long tmpVal_19 = null;
			tmpVal_19 = bus.getX();
			Long x1 = null;
			x1 = tmpVal_19;
			Long tmpVal_20 = null;
			tmpVal_20 = x1;
			Long x2 = null;
			x2 = tmpVal_20;
			Long tmpVal_21 = null;
			tmpVal_21 = bus.getY();
			Long y1 = null;
			y1 = tmpVal_21;
			Long tmpVal_22 = null;
			tmpVal_22 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
			Long y2 = null;
			y2 = tmpVal_22;
			drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.darkGray);
			Boolean cond_32 = null;
			//cond_32 = msg.hasToThread();
			cond_32 = busMessageEvent.message.receiverThread != null;
			if(cond_32.booleanValue())
			{
				TraceThread thr = null;
				Long par_60 = null;
				par_60 = busMessageEvent.message.receiverThread.id;//msg.getToThread();
				thr = data.getThread(par_60);
				TraceObject obj = null;
				obj = data.getObject(new Long(busMessageEvent.message.callerThread.object.id));
				Long xobj = null;
				Long var1_65 = null;
				var1_65 = obj.getX();
				xobj = new Long(var1_65.longValue() - (new Long(10L)).longValue());
				String tmpArg_v_74 = null;
				String var1_75 = null;
				String var2_77 = null;
				//var2_77 = msg.getDescr();
				var2_77 = busMessageEvent.message.operation.name;
				var1_75 = (new String(" ")).concat(var2_77);
				tmpArg_v_74 = var1_75.concat(new String(" "));
				drawHorizontalArrow(pgti, xobj, new Long(x1.longValue() + (new Long(10L)).longValue()), y2, tmpArg_v_74, ColorConstants.darkGreen);
			} else
			{
				Long objid = null;
				if(busMessageEvent.message.object != null)
				{
					objid = new Long(busMessageEvent.message.object.id);
				}
				else if(busMessageEvent.message.callerThread != null)
				{
					objid = new Long(busMessageEvent.message.callerThread.object.id);
				}
				Long cpuid = null;
				cpuid = msg.getToCpu();
				TraceObject obj = null;
				obj = data.getObject(objid);
				TraceCPU tmpVal_41 = null;
				tmpVal_41 = data.getCPU(cpuid);
				TraceCPU cpu = null;
				cpu = tmpVal_41;
				updateCpuObject(pgti, cpu, obj);
				Long tmpArg_v_49 = null;
				Long var1_50 = null;
				var1_50 = obj.getX();
				tmpArg_v_49 = new Long(var1_50.longValue() - (new Long(10L)).longValue());
				String tmpArg_v_56 = "Message Completed:";
				//tmpArg_v_56 = msg.getDescr();
				tmpArg_v_56 = busMessageEvent.message.operation.name;
				drawHorizontalArrow(pgti, tmpArg_v_49, new Long(x1.longValue() + (new Long(10L)).longValue()), y2, tmpArg_v_56, ColorConstants.darkGreen);
			}
			ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(y2.longValue() + (new Long(10L)).longValue())));
			bus.setY(y2);
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
			ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uypos.longValue() + (new Long(10L)).longValue())));
			updateCpuBus(pgti, bus);
			Long tmpVal_21 = null;
			tmpVal_21 = bus.getX();
			Long x1 = null;
			x1 = tmpVal_21;
			Long tmpVal_22 = null;
			tmpVal_22 = x1;
			Long x2 = null;
			x2 = tmpVal_22;
			Long tmpVal_23 = null;
			tmpVal_23 = bus.getY();
			Long y1 = null;
			y1 = tmpVal_23;
			Long tmpVal_24 = null;
			tmpVal_24 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
			Long y2 = null;
			y2 = tmpVal_24;
			TraceThread thr = null;
			Long par_29 = null;
			par_29 = msg.getFromThread();
			thr = data.getThread(par_29);
			TraceObject obj = null;
			//obj = data.getObject(thr.getCurrentObjectId());
			obj = data.getObject(new Long(busMessageEvent.message.callerThread.object.id));
			Long xobj = null;
			Long var1_34 = null;
			var1_34 = obj.getX();
			xobj = new Long(var1_34.longValue() - (new Long(10L)).longValue());
			drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
			String tmpArg_v_50 = null;
			String var1_51 = null;
			String var2_53 = null;
			//var2_53 = msg.getDescr();
			var2_53 = busMessageEvent.message.operation.name;
			var1_51 = (new String(" call ")).concat(var2_53);
			tmpArg_v_50 = var1_51.concat(new String(" "));
			drawHorizontalArrow(pgti, new Long(x1.longValue() + (new Long(10L)).longValue()), xobj, y1, tmpArg_v_50, ColorConstants.darkGreen);
			ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
			bus.setY(y2);
		}*/
	}

	public void drawOpCompleted(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj )
	{

		TraceObject currentObj = thread.getCurrentObject();
		boolean internalOperation = currentObj.getId() == destinationObj.getId();
		updateObject(tab, currentObj);
		if(internalOperation)
		{
			updateObject(tab, currentObj);
			Long x1 = currentObj.getX();
			Long x2 = x1;
			Long y1 = tab.getYMax();
			Long y2 = y1 + ELEMENT_SIZE;

			//					Long tmpVal_44 = null;
			//					tmpVal_44 = destobj.getY();
			//					y1 = tmpVal_44;
			//					Long tmpVal_45 = null;
			//					tmpVal_45 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
			//					Long y2 = null;
			//					y2 = tmpVal_45;
			//					Long objid = null;
			//					//objid = pioc.getObjref();
			//					objid = new Long(opEvent.object.id);
			NormalLabel lbl = new NormalLabel("C", tab.getCurrentFont());;
			//					org.eclipse.swt.graphics.Font arg_50 = null;
			//					arg_50 = pgti.getCurrentFont();
			//					lbl = new NormalLabel(new String("C"), arg_50);
			String str = null;
			Boolean cond_52 = null;
			//cond_52 = pioc.hasRes(); //TODO MAA: Add return value to data structure?
			cond_52 = false;
			if(cond_52.booleanValue())
			{
				String var2_54 = null;
				//var2_54 = pioc.getRes(); //TODO MAA
				str = (new String(" returns ")).concat(var2_54);
			} else
			{
				str = UTIL.ConvertToString(new String());
			}
			NormalLabel ttl = null;
			String arg_55 = null;
			String var1_57 = null;
			String var1_58 = null;
			String var1_59 = null;
			String var1_60 = null;
			String var2_62 = "TODO"; //TODO
			//var2_62 = pioc.getOpname();
			//					var2_62 = opEvent.operation.name;
			var1_60 = (new String(" Completed ")).concat(var2_62);
			var1_59 = var1_60.concat(new String(" on object "));
			var1_58 = var1_59.concat(currentObj.getId().toString());
			var1_57 = var1_58.concat(str);
			arg_55 = var1_57.concat(new String(" "));
			org.eclipse.swt.graphics.Font arg_56 = null;
			arg_56 = tab.getCurrentFont();
			ttl = new NormalLabel(arg_55, arg_56);
			Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
			drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
			lbl.setToolTip(ttl);
			lbl.setLocation(pt);
			tab.addFigure(lbl);
			//ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
			currentObj.setY(y2);
		} else
		{
			updateObject(tab,destinationObj);
			drawObjectArrow(tab, destinationObj, currentObj, new String(""));
		}

	}

	public void drawOpActivate(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{

		TraceObject currentObj = thread.getCurrentObject();
		boolean internalOperation = currentObj.getId() == destinationObj.getId();
		updateObject(tab, currentObj);
		
		if(internalOperation)
		{
			
			Long x1 = currentObj.getX();
			Long x2 = x1;
			Long y1 = tab.getYMax();
			Long y2 = y1.longValue() + ELEMENT_SIZE;
			
			String operationLabel = "A " + operation.getName();
			NormalLabel lbl = new NormalLabel(operationLabel, tab.getCurrentFont());

			Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
			drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
			lbl.setLocation(pt);
			tab.addFigure(lbl);
			//ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
			currentObj.setY(y2);
		} 
		else
		{
			updateObject(tab, destinationObj);
			String operationName = operation.getName();
		
				drawObjectArrow(tab, currentObj, destinationObj, operationName);

		}
	} 

	public void drawOpRequest(GenericTabItem tab, TraceCPU cpu, TraceThread thread, TraceObject destinationObj, TraceOperation operation)
	{

		TraceObject obj = thread.getCurrentObject();
		updateObject(tab, obj);
		
		Long x1 = obj.getX();

		Long x2 = x1;
		Long y1 = tab.getYMax();
		Long y2 = y1.longValue() + ELEMENT_SIZE;
		
		NormalLabel lbl = new NormalLabel("R", tab.getCurrentFont());
		
		//TODO: Cleanup auto generated code below:
		
		String str = "";
		Boolean hasArguments = false; //TODO MAA
		if(hasArguments)
		{
			String var2_33 = "Operation Request";;
			//var2_33 = pior.getArgs(); //TODO MAA
			str = (new String(" with arguments ")).concat(var2_33);
		} else
		{
			str = UTIL.ConvertToString(new String());
		}

		String arg_34 = null;
		String var1_36 = null;
		String var1_37 = null;
		String var1_38 = null;
		String var1_39 = null;
		String var2_41 = null;
		//var2_41 = pior.getOpname();
		//var2_41 = event.operation.name;
		var2_41 = operation.getName(); //TODO
		var1_39 = (new String(" Requested ")).concat(var2_41);
		var1_38 = var1_39.concat(new String(" on object "));
		var1_37 = var1_38.concat(obj.getId().toString());
		var1_36 = var1_37.concat(str);
		arg_34 = var1_36.concat(new String(" "));
		org.eclipse.swt.graphics.Font arg_35 = null;
		arg_35 = tab.getCurrentFont();
		NormalLabel ttl = new NormalLabel(arg_34, arg_35);
		Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
		drawMarker(tab, x1, y1, x2, y2, ColorConstants.blue);
		lbl.setToolTip(ttl);
		lbl.setLocation(pt);
		tab.addFigure(lbl);
		//ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
		obj.setY(y2);


	}

	@Override
	public void drawTimeMarker(GenericTabItem tab, Long markerTime) {
		
		Long markerStartX = BUS_X_START;
		Long markerStartY = tab.getYMax();
		Long markerEndX = tab.getXMax();
		Long markerEndY = markerStartY;
		
		//Draw horizontal marker line
		Line markerLine = new Line(markerStartX, markerStartY, markerEndX, markerEndY);
		markerLine.setForegroundColor(ColorConstants.lightGray);
		markerLine.setDot();
		tab.addBackgroundFigure(markerLine);
		
		//Draw time label
		String labelText = markerTime.toString();
		NormalLabel timeLabel = new NormalLabel(labelText, tab.getCurrentFont());	
		int labelStartX = markerStartX.intValue() - timeLabel.getSize().width;
		int labelStartY = markerStartY.intValue() - (int)(timeLabel.getSize().height/2);
		Point labelLocation = new Point(labelStartX, labelStartY);
		timeLabel.setLocation(labelLocation);
		tab.addBackgroundFigure(timeLabel);
		
	}

	private void updateObject(GenericTabItem tab, TraceObject pobj)
	{
		if(!pobj.isVisible())
		{	
			//Draw Object
			String name = pobj.getName() + " (" + pobj.getId().toString() + ")";
			NormalLabel nlb = new NormalLabel(name, tab.getCurrentFont());;
			RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);	
			Long objectXPos = tab.getXMax() + CPU_X_OFFSET;
			Long objectYPos = CPU_Y_POS + CPU_HEIGHT + ELEMENT_SIZE;
			Long objWidth = new Long(name.length())*OBJECT_WIDTH_FACTOR;
			
			Point np = new Point(objectXPos.intValue(), CPU_Y_POS.intValue());
			nrr.setLocation(np);
			nrr.setSize(objWidth, CPU_HEIGHT);
			tab.addFigure(nrr);
				
			//Draw Object timeline
            Long lineXPos = objectXPos + new Long(objWidth/2);
            Long lineYStartPos = objectYPos;
            Long lineYEndPos = tab.getVerticalSize();
			drawTimeline(tab, lineXPos, lineYStartPos, lineXPos, lineYEndPos);
			
			//Update Object
			pobj.setY(objectYPos);			
			pobj.setVisible(true);
            pobj.setX(lineXPos);
		}
	}

	@Override
	public void drawReplyRequest(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageCompleted(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageRequest(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMessageActivated(GenericTabItem tab, TraceCPU cpu,
			TraceThread thread, TraceBus bus, TraceOperation op) {
		// TODO Auto-generated method stub
		
	}

	
	protected void drawObjectArrow(GenericTabItem tab, TraceObject psrc, TraceObject pdest, String pstr)
	{
		Long psx = psrc.getX();
		Long psy = tab.getYMax(); //psrc.getY();
		Long pdx = pdest.getX();
		Long pdy = tab.getYMax(); //pdest.getY();
		
		Line line = new Line(psx, psy, psx, new Long(psy.longValue() + (new Long(20L)).longValue()));
		
		NormalLabel lbl = null;
		org.eclipse.swt.graphics.Font arg_18 = null;
		arg_18 = tab.getCurrentFont();
		lbl = new NormalLabel(pstr, arg_18);
		line.setLineWidth(new Long(3L));
		line.setForegroundColor(ColorConstants.blue);
		tab.addFigure(line);
		line = (Line)UTIL.clone(new Line(pdx, new Long(pdy.longValue() + (new Long(20L)).longValue()), pdx, new Long(pdy.longValue() + (new Long(40L)).longValue())));
		line.setLineWidth(new Long(3L));
		line.setForegroundColor(ColorConstants.blue);
		tab.addFigure(line);
		line = (Line)UTIL.clone(new Line(psx, new Long(psy.longValue() + (new Long(20L)).longValue()), pdx, new Long(psy.longValue() + (new Long(20L)).longValue())));
		line.setForegroundColor(ColorConstants.blue);
		tab.addFigure(line);
		
		if((new Boolean(psx.longValue() < pdx.longValue())).booleanValue())
		{
			Point pt = new Point((new Long(psx.longValue() + (new Long(20L)).longValue())).intValue(), (new Long(psy.longValue() + (new Long(2L)).longValue())).intValue());
			lbl.setLocation(pt);
			tab.addFigure(lbl);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() - (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(16L)).longValue()), new Long(pdx.longValue() - (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			tab.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() - (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(24L)).longValue()), new Long(pdx.longValue() - (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			tab.addFigure(line);
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
			tab.addFigure(lbl);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(16L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			tab.addFigure(line);
			line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(24L)).longValue())));
			line.setForegroundColor(ColorConstants.blue);
			tab.addFigure(line);
		}
		
//		ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uypos.longValue() + (new Long(40L)).longValue())));
//		psrc.setY(ov_uypos);
//		pdest.setY(ov_uypos);
	}
}
