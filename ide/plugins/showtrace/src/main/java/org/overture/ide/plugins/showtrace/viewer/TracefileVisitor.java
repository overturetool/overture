// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:15
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileVisitor.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.Record;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.overturetool.traceviewer.ast.itf.IOmlBUSdecl;
import org.overturetool.traceviewer.ast.itf.IOmlCPUdecl;
import org.overturetool.traceviewer.ast.itf.IOmlDelayedThreadSwapIn;
import org.overturetool.traceviewer.ast.itf.IOmlDeployObj;
import org.overturetool.traceviewer.ast.itf.IOmlMessageActivate;
import org.overturetool.traceviewer.ast.itf.IOmlMessageCompleted;
import org.overturetool.traceviewer.ast.itf.IOmlMessageRequest;
import org.overturetool.traceviewer.ast.itf.IOmlOpActivate;
import org.overturetool.traceviewer.ast.itf.IOmlOpCompleted;
import org.overturetool.traceviewer.ast.itf.IOmlOpRequest;
import org.overturetool.traceviewer.ast.itf.IOmlReplyRequest;
import org.overturetool.traceviewer.ast.itf.IOmlThreadCreate;
import org.overturetool.traceviewer.ast.itf.IOmlThreadKill;
import org.overturetool.traceviewer.ast.itf.IOmlThreadSwapIn;
import org.overturetool.traceviewer.ast.itf.IOmlThreadSwapOut;
import org.overturetool.traceviewer.ast.itf.IOmlTraceEvent;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;
import org.overturetool.traceviewer.visitor.OmlVisitor;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            TraceData, tdCPU, GenericTabItem, NormalLabel, 
//            RectangleLabelFigure, tdBUS, Line, tdThread, 
//            tdObject, RotatedLabel, tdResource, tdMessage
@SuppressWarnings("unchecked")
public class TracefileVisitor extends OmlVisitor
{
    private static class ConjectureLimit
        implements Record
    {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object clone()
        {
            return new ConjectureLimit(obstime, thrid, name);
        }

        @Override
		public String toString()
        {
            return (new StringBuilder("mk_TracefileVisitor`ConjectureLimit(")).append(UTIL.toString(obstime)).append(",").append(UTIL.toString(thrid)).append(",").append(UTIL.toString(name)).append(")").toString();
        }

        @Override
		public boolean equals(Object obj)
        {
            if(!(obj instanceof ConjectureLimit))
                return false;
            ConjectureLimit temp = (ConjectureLimit)obj;
            return UTIL.equals(obstime, temp.obstime) && UTIL.equals(thrid, temp.thrid) && UTIL.equals(name, temp.name);
        }

        @Override
		public int hashCode()
        {
            return (obstime != null ? obstime.hashCode() : 0) + (thrid != null ? thrid.hashCode() : 0) + (name != null ? name.hashCode() : 0);
        }

        public Long obstime;
        public Long thrid;
        public String name;

        @SuppressWarnings("unused")
		public ConjectureLimit()
        {
        }

        public ConjectureLimit(Long p1, Long p2, String p3)
        {
            obstime = p1;
            thrid = p2;
            name = p3;
        }
    }


    public TracefileVisitor()
        throws CGException
    {
        theTabItem = null;
        theCpu = null;
        data = null;
        ov_uxpos = null;
        ov_uypos = null;
        ov_ustarttime = null;
        ov_ucurrenttime = null;
        ov_utimepos = null;
        failedLower = null;
        lastLower = new HashMap();
        failedUpper = null;
        lastUpper = new HashMap();
        try
        {
            data = new TraceData();
            ov_uxpos = CPU_uXPOS;
            ov_uypos = new Long(0L);
            ov_ustarttime = new Long(0L);
            ov_ucurrenttime = new Long(0L);
            ov_utimepos = new Vector();
            failedLower = new Vector();
            lastLower = new HashMap();
            failedUpper = new Vector();
            lastUpper = new HashMap();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    private String nat2str(Long num)
        throws CGException
    {
//        String varRes_2 = null;
//        if((new Boolean(num.longValue() < (new Long(10L)).longValue())).booleanValue())
//        {
//            Character e_seq_15 = null;
//            if(1 <= (new Long(num.longValue() + (new Long(1L)).longValue())).longValue() && (new Long(num.longValue() + (new Long(1L)).longValue())).longValue() <= (new String("0123456789")).length())
//                e_seq_15 = new Character((new String("0123456789")).charAt((new Long(num.longValue() + (new Long(1L)).longValue())).longValue() - 1));
//            else
//                UTIL.RunTime("Run-Time Error:Illegal index");
//            varRes_2 = new String();
//            varRes_2 = (new StringBuilder(String.valueOf(varRes_2))).append(e_seq_15).toString();
//        } else
//        {
//            varRes_2 = nat2str(new Long(num.longValue() / (new Long(10L)).longValue())).concat(nat2str(new Long((int)(num.doubleValue() - (new Long(10L)).doubleValue() * Math.floor(num.doubleValue() / (new Long(10L)).doubleValue())))));
//        }
        return num.toString();
    }

    public Vector getAllTimes()
        throws CGException
    {
        Vector rexpr_1 = null;
        rexpr_1 = data.getTimes();
        return rexpr_1;
    }

    public Vector getCpus()
        throws CGException
    {
        Vector res = new Vector();
        Vector sq_1 = null;
        sq_1 = data.getOrderedCpus();
        Long cpuid = null;
        tdCPU e_6;
        for(Iterator enm_9 = sq_1.iterator(); enm_9.hasNext(); res.add(e_6))
        {
            Long elem_2 = UTIL.NumberToLong(enm_9.next());
            cpuid = elem_2;
            e_6 = null;
            e_6 = data.getCPU(cpuid);
        }

        return res;
    }

    public void drawArchitecture(GenericTabItem pgti)
        throws CGException
    {
        Long curx = CPU_uXPOS;
        Long cury = BUS_uYPOS;
        data.reset();
        Vector sq_3 = null;
        sq_3 = data.getOrderedCpus();
        Long cpuid = null;
        for(Iterator enm_44 = sq_3.iterator(); enm_44.hasNext();)
        {
            Long elem_4 = UTIL.NumberToLong(enm_44.next());
            cpuid = elem_4;
            tdCPU tmpVal_8 = null;
            tmpVal_8 = data.getCPU(cpuid);
            tdCPU cpu = null;
            cpu = tmpVal_8;
            Long width = null;
            Long var2_15 = null;
            Long var2_17 = null;
            HashSet unArg_18 = new HashSet();
            unArg_18 = cpu.connects();
            var2_17 = new Long(unArg_18.size());
            var2_15 = new Long((new Long(9L)).longValue() * var2_17.longValue());
            width = new Long((new Long((new Long(2L)).longValue() * CPU_uHALFWIDTH.longValue())).longValue() + var2_15.longValue());
            NormalLabel nlb = null;
            String arg_19 = null;
            arg_19 = cpu.getName();
            org.eclipse.swt.graphics.Font arg_20 = null;
            arg_20 = pgti.getCurrentFont();
            nlb = new NormalLabel(arg_19, arg_20);
            RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);
            Point np = new Point(curx.longValue(), CPU_uYPOS.longValue());
            Boolean cond_24 = null;
            Boolean unArg_25 = null;
            unArg_25 = cpu.isExplicit();
            cond_24 = new Boolean(!unArg_25.booleanValue());
            if(cond_24.booleanValue())
            {
                nrr.setDash();
                nrr.setForegroundColor(ColorConstants.darkGray);
            }
            nrr.setLocation(np);
            nrr.setSize(width, CPU_uHEIGHT);
            pgti.addFigure(nrr);
            cpu.setX(curx);
            curx = UTIL.NumberToLong(UTIL.clone(new Long((new Long(curx.longValue() + width.longValue())).longValue() + CPU_uHINTERVAL.longValue())));
        }

        Vector sq_45 = null;
        sq_45 = data.getOrderedBuses();
        Long busid = null;
        for(Iterator enm_73 = sq_45.iterator(); enm_73.hasNext();)
        {
            Long elem_46 = UTIL.NumberToLong(enm_73.next());
            busid = elem_46;
            tdBUS bus = null;
            bus = data.getBUS(busid);
            NormalLabel nlb = null;
            String arg_52 = null;
            tdBUS obj_54 = null;
            obj_54 = data.getBUS(busid);
            arg_52 = obj_54.getName();
            org.eclipse.swt.graphics.Font arg_53 = null;
            arg_53 = pgti.getCurrentFont();
            nlb = new NormalLabel(arg_52, arg_53);
            Point np = null;
            Long arg_56 = null;
            Long var2_61 = null;
            Dimension tmpRec_62 = null;
            tmpRec_62 = nlb.getSize();
            var2_61 = new Long(tmpRec_62.width);
            arg_56 = new Long((new Long(BUS_uXPOS.longValue() + (new Long(100L)).longValue())).longValue() - var2_61.longValue());
            np = new Point(arg_56.longValue(), cury.longValue());
            nlb.setLocation(np);
            pgti.addFigure(nlb);
            bus.setY(cury);
            cury = UTIL.NumberToLong(UTIL.clone(new Long(cury.longValue() + BUS_uVINTERVAL.longValue())));
        }

        drawArchDetail(pgti);
    }

    public void drawOverview(GenericTabItem pgti, Long starttime)
        throws CGException
    {
        if(!pre_drawOverview(pgti, starttime).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in drawOverview");
        Long cury = new Long(RESOURCE_uVINTERVAL.longValue() / (new Long(2L)).longValue());
        data.reset();
        resetLastDrawn();
        ov_uxpos = UTIL.NumberToLong(UTIL.clone(CPU_uXPOS));
        ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        ov_ustarttime = UTIL.NumberToLong(UTIL.clone(starttime));
        ov_ucurrenttime = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        Vector revcpus = null;
        revcpus = data.getOrderedCpus();
        Long cpuid = null;
        for(int i_43 = revcpus.size(); i_43 > 0; i_43--)
        {
            Long elem_14 = UTIL.NumberToLong(revcpus.get(i_43 - 1));
            cpuid = elem_14;
            tdCPU tmpVal_18 = null;
            tmpVal_18 = data.getCPU(cpuid);
            tdCPU cpu = null;
            cpu = tmpVal_18;
            NormalLabel nlb = null;
            String arg_20 = null;
            arg_20 = cpu.getName();
            org.eclipse.swt.graphics.Font arg_21 = null;
            arg_21 = pgti.getCurrentFont();
            nlb = new NormalLabel(arg_20, arg_21);
            Point np = null;
            Long arg_22 = null;
            Long var2_27 = null;
            Dimension tmpRec_28 = null;
            tmpRec_28 = nlb.getSize();
            var2_27 = new Long(tmpRec_28.width);
            arg_22 = new Long((new Long(BUS_uXPOS.longValue() + (new Long(100L)).longValue())).longValue() - var2_27.longValue());
            np = new Point(arg_22.longValue(), cury.longValue());
            nlb.setLocation(np);
            pgti.addFigure(nlb);
            cpu.setX(CPU_uXPOS);
            cpu.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = UTIL.NumberToLong(UTIL.clone(new Long(cury.longValue() + RESOURCE_uVINTERVAL.longValue())));
        }

        Vector sq_44 = null;
        sq_44 = data.getOrderedBuses();
        Long busid = null;
        for(Iterator enm_74 = sq_44.iterator(); enm_74.hasNext();)
        {
            Long elem_45 = UTIL.NumberToLong(enm_74.next());
            busid = elem_45;
            tdBUS bus = null;
            bus = data.getBUS(busid);
            NormalLabel nlb = null;
            String arg_51 = null;
            arg_51 = bus.getName();
            org.eclipse.swt.graphics.Font arg_52 = null;
            arg_52 = pgti.getCurrentFont();
            nlb = new NormalLabel(arg_51, arg_52);
            Point np = null;
            Long arg_53 = null;
            Long var2_58 = null;
            Dimension tmpRec_59 = null;
            tmpRec_59 = nlb.getSize();
            var2_58 = new Long(tmpRec_59.width);
            arg_53 = new Long((new Long(BUS_uXPOS.longValue() + (new Long(100L)).longValue())).longValue() - var2_58.longValue());
            np = new Point(arg_53.longValue(), cury.longValue());
            nlb.setLocation(np);
            pgti.addFigure(nlb);
            bus.setX(CPU_uXPOS);
            bus.setY(new Long(cury.longValue() + (new Long(10L)).longValue()));
            cury = UTIL.NumberToLong(UTIL.clone(new Long(cury.longValue() + RESOURCE_uVINTERVAL.longValue())));
        }

        ov_uypos = UTIL.NumberToLong(UTIL.clone(cury));
        drawOverviewDetail(pgti);
    }

    public Boolean pre_drawOverview(GenericTabItem pgti, Long starttime)
        throws CGException
    {
        Boolean varRes_3 = null;
        HashSet var2_5 = new HashSet();
        Vector unArg_6 = null;
        unArg_6 = data.getTimes();
        HashSet set_7 = new HashSet();
        for(Enumeration enm_8 = unArg_6.elements(); enm_8.hasMoreElements(); set_7.add(enm_8.nextElement()));
        var2_5 = set_7;
        varRes_3 = new Boolean(var2_5.contains(starttime));
        return varRes_3;
    }

    public void drawCpu(GenericTabItem pgti, Long starttime)
        throws CGException
    {
        Long curx = new Long(100L);
        tdCPU cpu = null;
        cpu = pgti.getCPU();
        data.reset();
        ov_ustarttime = UTIL.NumberToLong(UTIL.clone(starttime));
        ov_ucurrenttime = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(0L)));
        ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long((new Long(CPU_uYPOS.longValue() + CPU_uHEIGHT.longValue())).longValue() + ELEMENT_uSIZE.longValue())));
        ov_utimepos = (Vector)UTIL.ConvertToList(UTIL.clone(new Vector()));
        Vector sq_13 = null;
        sq_13 = data.getOrderedBuses();
        Long busid = null;
        for(Iterator enm_60 = sq_13.iterator(); enm_60.hasNext();)
        {
            Long elem_14 = UTIL.NumberToLong(enm_60.next());
            busid = elem_14;
            Boolean cond_17 = null;
            HashSet var2_19 = new HashSet();
            var2_19 = cpu.connects();
            cond_17 = new Boolean(var2_19.contains(busid));
            if(cond_17.booleanValue())
            {
                tdBUS bus = null;
                bus = data.getBUS(busid);
                Long width = new Long((new Long((new Long(2L)).longValue() * CPU_uHALFWIDTH.longValue())).longValue() + (new Long(23L)).longValue());
                NormalLabel nlb = null;
                String arg_29 = null;
                arg_29 = bus.getName();
                org.eclipse.swt.graphics.Font arg_30 = null;
                arg_30 = pgti.getCurrentFont();
                nlb = new NormalLabel(arg_29, arg_30);
                RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);
                Point np = new Point(curx.longValue(), CPU_uYPOS.longValue());
                Boolean cond_34 = null;
                Boolean unArg_35 = null;
                unArg_35 = bus.isExplicit();
                cond_34 = new Boolean(!unArg_35.booleanValue());
                if(cond_34.booleanValue())
                {
                    nrr.setDash();
                    nrr.setForegroundColor(ColorConstants.darkGray);
                }
                nrr.setLocation(np);
                nrr.setSize(width, CPU_uHEIGHT);
                pgti.addFigure(nrr);
                bus.setX(new Long((new Long(curx.longValue() + CPU_uHALFWIDTH.longValue())).longValue() + (new Long(12L)).longValue()));
                bus.setY(ov_uypos);
                curx = UTIL.NumberToLong(UTIL.clone(new Long((new Long(curx.longValue() + width.longValue())).longValue() + CPU_uHINTERVAL.longValue())));
            }
        }

        ov_uxpos = UTIL.NumberToLong(UTIL.clone(curx));
        HashSet iset_62 = new HashSet();
        iset_62 = cpu.getObjects();
        Long objid = null;
        tdObject obj;
        for(Iterator enm_73 = iset_62.iterator(); enm_73.hasNext(); updateCpuObject(pgti, cpu, obj))
        {
            Long elem_63 = UTIL.NumberToLong(enm_73.next());
            objid = elem_63;
            obj = null;
            obj = data.getObject(objid);
        }

        drawCpuDetail(pgti, cpu);
    }

    private void drawArchDetail(GenericTabItem pgti)
        throws CGException
    {
        HashMap max = new HashMap();
        HashMap min = new HashMap();
        Vector cpus = null;
        cpus = data.getOrderedCpus();
        Long lastcpu = null;
        if(1 <= (new Long(cpus.size())).longValue() && (new Long(cpus.size())).longValue() <= cpus.size())
            lastcpu = UTIL.NumberToLong(cpus.get((new Long(cpus.size())).intValue() - 1));
        else
            UTIL.RunTime("Run-Time Error:Illegal index");
        Long x1 = null;
        Long var1_11 = null;
        Long var1_12 = null;
        tdCPU obj_13 = null;
        obj_13 = data.getCPU(new Long(0L));
        var1_12 = obj_13.getX();
        var1_11 = new Long(var1_12.longValue() + CPU_uHALFWIDTH.longValue());
        x1 = new Long(var1_11.longValue() + (new Long(1L)).longValue());
        Long x2 = null;
        Long var1_18 = null;
        Long var1_19 = null;
        tdCPU obj_20 = null;
        obj_20 = data.getCPU(lastcpu);
        var1_19 = obj_20.getX();
        var1_18 = new Long(var1_19.longValue() + CPU_uHALFWIDTH.longValue());
        x2 = new Long(var1_18.longValue() + (new Long(9L)).longValue());
        Long tmpVal_24 = null;
        Long var1_25 = null;
        tdBUS obj_26 = null;
        obj_26 = data.getBUS(new Long(0L));
        var1_25 = obj_26.getY();
        tmpVal_24 = new Long(var1_25.longValue() + (new Long(1L)).longValue());
        Long y = null;
        y = tmpVal_24;
        Line line = new Line(x1, y, x2, y);
        line.setLineWidth(new Long(2L));
        line.setForegroundColor(ColorConstants.gray);
        pgti.addFigure(line);
        HashSet iset_39 = new HashSet();
        iset_39 = data.getCPUs();
        Long cpuid = null;
        for(Iterator enm_129 = iset_39.iterator(); enm_129.hasNext();)
        {
            Long elem_40 = UTIL.NumberToLong(enm_129.next());
            cpuid = elem_40;
            tdCPU tmpVal_44 = null;
            tmpVal_44 = data.getCPU(cpuid);
            tdCPU cpu = null;
            cpu = tmpVal_44;
            Long xbase = null;
            Long var1_48 = null;
            Long var1_49 = null;
            tdCPU obj_50 = null;
            obj_50 = data.getCPU(cpuid);
            var1_49 = obj_50.getX();
            var1_48 = new Long(var1_49.longValue() + CPU_uHALFWIDTH.longValue());
            xbase = new Long(var1_48.longValue() + (new Long(5L)).longValue());
            HashSet iset_54 = new HashSet();
            iset_54 = cpu.connects();
            Long busid = null;
            for(Iterator enm_128 = iset_54.iterator(); enm_128.hasNext();)
            {
                Long elem_55 = UTIL.NumberToLong(enm_128.next());
                busid = elem_55;
                tdBUS bus = null;
                bus = data.getBUS(busid);
                Long nxp = new Long(xbase.longValue() + (new Long((new Long(9L)).longValue() * busid.longValue())).longValue());
                Long nyp1 = new Long(CPU_uYPOS.longValue() + CPU_uHEIGHT.longValue());
                Long nyp2 = null;
                tdBUS obj_71 = null;
                obj_71 = data.getBUS(busid);
                nyp2 = obj_71.getY();
                 line = new Line(nxp, nyp1, nxp, nyp2);
                Boolean cond_77 = null;
                Boolean unArg_78 = null;
                unArg_78 = bus.isExplicit();
                cond_77 = new Boolean(!unArg_78.booleanValue());
                if(cond_77.booleanValue())
                {
                    line.setDot();
                    line.setForegroundColor(ColorConstants.gray);
                }
                pgti.addFigure(line);
                Boolean cond_84 = null;
                cond_84 = new Boolean(max.containsKey(busid));
                if(cond_84.booleanValue())
                {
                    if((new Boolean(nxp.longValue() > UTIL.NumberToLong(max.get(busid)).longValue())).booleanValue())
                        max.put(busid, nxp);
                } else
                {
                    HashMap rhs_87 = new HashMap();
                    HashMap var2_89 = new HashMap();
                    var2_89 = new HashMap();
                    var2_89.put(busid, nxp);
                    HashMap m1_96 = (HashMap)max.clone();
                    HashMap m2_97 = var2_89;
                    HashSet com_92 = new HashSet();
                    com_92.addAll(m1_96.keySet());
                    com_92.retainAll(m2_97.keySet());
                    boolean all_applies_93 = true;
                    Object d_94;
                    for(Iterator bb_95 = com_92.iterator(); bb_95.hasNext() && all_applies_93; all_applies_93 = m1_96.get(d_94).equals(m2_97.get(d_94)))
                        d_94 = bb_95.next();

                    if(!all_applies_93)
                        UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
                    m1_96.putAll(m2_97);
                    rhs_87 = m1_96;
                    max = (HashMap)UTIL.clone(rhs_87);
                }
                Boolean cond_106 = null;
                cond_106 = new Boolean(min.containsKey(busid));
                if(cond_106.booleanValue())
                {
                    if((new Boolean(nxp.longValue() < UTIL.NumberToLong(min.get(busid)).longValue())).booleanValue())
                        min.put(busid, nxp);
                } else
                {
                    HashMap rhs_109 = new HashMap();
                    HashMap var2_111 = new HashMap();
                    var2_111 = new HashMap();
                    var2_111.put(busid, nxp);
                    HashMap m1_118 = (HashMap)min.clone();
                    HashMap m2_119 = var2_111;
                    HashSet com_114 = new HashSet();
                    com_114.addAll(m1_118.keySet());
                    com_114.retainAll(m2_119.keySet());
                    boolean all_applies_115 = true;
                    Object d_116;
                    for(Iterator bb_117 = com_114.iterator(); bb_117.hasNext() && all_applies_115; all_applies_115 = m1_118.get(d_116).equals(m2_119.get(d_116)))
                        d_116 = bb_117.next();

                    if(!all_applies_115)
                        UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
                    m1_118.putAll(m2_119);
                    rhs_109 = m1_118;
                    min = (HashMap)UTIL.clone(rhs_109);
                }
            }

        }

        HashSet iset_130 = new HashSet();
        HashSet var1_137 = new HashSet();
        var1_137 = data.getBUSes();
        HashSet var2_138 = new HashSet();
        var2_138 = new HashSet();
        var2_138.add(new Long(0L));
        iset_130 = (HashSet)var1_137.clone();
        iset_130.removeAll(var2_138);
        Long busid = null;
        
        for(Iterator enm_164 = iset_130.iterator(); enm_164.hasNext(); pgti.addFigure(line))
        {
            Long elem_131 = UTIL.NumberToLong(enm_164.next());
            busid = elem_131;
             x1 = new Long(UTIL.NumberToLong(min.get(busid)).longValue() - (new Long(4L)).longValue());
             x2 = new Long(UTIL.NumberToLong(max.get(busid)).longValue() + (new Long(4L)).longValue());
            Long tmpVal_151 = null;
            Long var1_152 = null;
            tdBUS obj_153 = null;
            obj_153 = data.getBUS(busid);
            var1_152 = obj_153.getY();
            tmpVal_151 = new Long(var1_152.longValue() + (new Long(1L)).longValue());
             y = null;
            y = tmpVal_151;
            line = new Line(x1, y, x2, y);
            line.setLineWidth(new Long(2L));
        }

    }

    private void drawOverviewDetail(GenericTabItem pgti)
        throws CGException
    {
        Boolean cond_2 = null;
        Long var1_3 = null;
        Vector unArg_4 = null;
        unArg_4 = data.getTimes();
        var1_3 = new Long(unArg_4.size());
        cond_2 = new Boolean(var1_3.longValue() > (new Long(0L)).longValue());
        if(cond_2.booleanValue())
        {
            Long event_utime = null;
            Vector unArg_6 = null;
            unArg_6 = data.getTimes();
            event_utime = UTIL.NumberToLong(unArg_6.get(0));
            Vector rest_uhist = null;
            Vector unArg_7 = null;
            unArg_7 = data.getTimes();
            rest_uhist = new Vector(unArg_7.subList(1, unArg_7.size()));
            for(Boolean cont = new Boolean(true); cont.booleanValue();)
            {
                ov_ucurrenttime = UTIL.NumberToLong(UTIL.clone(event_utime));
                if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
                    drawOvTimeMarker(pgti, ov_uxpos, ov_uypos, event_utime);
                Vector sq_18 = null;
                sq_18 = data.getHistory(event_utime);
                IOmlTraceEvent event = null;
                for(Iterator enm_88 = sq_18.iterator(); enm_88.hasNext();)
                {
                    IOmlTraceEvent elem_19 = (IOmlTraceEvent)enm_88.next();
                    event = elem_19;
                    Boolean cond_24 = null;
                    Long var2_26 = null;
                    var2_26 = pgti.getHorizontalSize();
                    cond_24 = new Boolean(ov_uxpos.longValue() < var2_26.longValue());
                    if(cond_24.booleanValue())
                        if((new Boolean(event instanceof IOmlThreadCreate)).booleanValue())
                            drawOvThreadCreate(pgti, (IOmlThreadCreate)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadSwapIn)).booleanValue())
                            drawOvThreadSwapIn(pgti, (IOmlThreadSwapIn)event);
                        else
                        if((new Boolean(event instanceof IOmlDelayedThreadSwapIn)).booleanValue())
                            drawOvDelayedThreadSwapIn(pgti, (IOmlDelayedThreadSwapIn)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadSwapOut)).booleanValue())
                            drawOvThreadSwapOut(pgti, (IOmlThreadSwapOut)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadKill)).booleanValue())
                            drawOvThreadKill(pgti, (IOmlThreadKill)event);
                        else
                        if((new Boolean(event instanceof IOmlOpRequest)).booleanValue())
                            drawOvOpRequest(pgti, (IOmlOpRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlOpActivate)).booleanValue())
                            drawOvOpActivate(pgti, (IOmlOpActivate)event);
                        else
                        if((new Boolean(event instanceof IOmlOpCompleted)).booleanValue())
                            drawOvOpCompleted(pgti, (IOmlOpCompleted)event);
                        else
                        if((new Boolean(event instanceof IOmlMessageRequest)).booleanValue())
                            drawOvMessageRequest(pgti, (IOmlMessageRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlReplyRequest)).booleanValue())
                            drawOvReplyRequest(pgti, (IOmlReplyRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlMessageActivate)).booleanValue())
                            drawOvMessageActivate(pgti, (IOmlMessageActivate)event);
                        else
                        if((new Boolean(event instanceof IOmlMessageCompleted)).booleanValue())
                            drawOvMessageCompleted(pgti, (IOmlMessageCompleted)event);
                }

                Boolean cond_89 = null;
                if(!(cond_89 = new Boolean(UTIL.equals(rest_uhist, new Vector()))).booleanValue())
                {
                    Boolean var2_93 = null;
                    Long var2_95 = null;
                    var2_95 = pgti.getHorizontalSize();
                    var2_93 = new Boolean(ov_uxpos.longValue() >= var2_95.longValue());
                    cond_89 = var2_93;
                }
                if(cond_89.booleanValue())
                {
                    cont = (Boolean)UTIL.clone(new Boolean(false));
                } else
                {
                    event_utime = UTIL.NumberToLong(UTIL.clone(UTIL.NumberToLong(rest_uhist.get(0))));
                    rest_uhist = (Vector)UTIL.ConvertToList(UTIL.clone(new Vector(rest_uhist.subList(1, rest_uhist.size()))));
                }
            }

            HashSet iset_101 = new HashSet();
            iset_101 = data.getCPUs();
            Long cpuid = null;
            tdCPU tmpArg_v_107;
            for(Iterator enm_109 = iset_101.iterator(); enm_109.hasNext(); updateOvCpu(pgti, tmpArg_v_107))
            {
                Long elem_102 = UTIL.NumberToLong(enm_109.next());
                cpuid = elem_102;
                tmpArg_v_107 = null;
                tmpArg_v_107 = data.getCPU(cpuid);
            }

            HashSet iset_110 = new HashSet();
            iset_110 = data.getBUSes();
            Long busid = null;
            tdBUS tmpArg_v_116;
            for(Iterator enm_118 = iset_110.iterator(); enm_118.hasNext(); updateOvBus(pgti, tmpArg_v_116))
            {
                Long elem_111 = UTIL.NumberToLong(enm_118.next());
                busid = elem_111;
                tmpArg_v_116 = null;
                tmpArg_v_116 = data.getBUS(busid);
            }

        }
    }

    private void drawCpuDetail(GenericTabItem pgti, tdCPU cpu)
        throws CGException
    {
        Boolean cond_3 = null;
        Long var1_4 = null;
        Vector unArg_5 = null;
        unArg_5 = cpu.getTimes();
        var1_4 = new Long(unArg_5.size());
        cond_3 = new Boolean(var1_4.longValue() > (new Long(0L)).longValue());
        if(cond_3.booleanValue())
        {
            Long event_utime = null;
            Vector unArg_7 = null;
            unArg_7 = cpu.getTimes();
            event_utime = UTIL.NumberToLong(unArg_7.get(0));
            Vector rest_uhist = null;
            Vector unArg_8 = null;
            unArg_8 = cpu.getTimes();
            rest_uhist = new Vector(unArg_8.subList(1, unArg_8.size()));
            for(Boolean cont = new Boolean(true); cont.booleanValue();)
            {
                ov_ucurrenttime = UTIL.NumberToLong(UTIL.clone(event_utime));
                if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
                    drawCpuTimeMarker(pgti, new Long(120L), ov_uypos, event_utime);//TODO size of the time label
                Vector sq_19 = null;
                sq_19 = cpu.getHistory(event_utime);
                IOmlTraceEvent event = null;
                for(Iterator enm_84 = sq_19.iterator(); enm_84.hasNext();)
                {
                    IOmlTraceEvent elem_20 = (IOmlTraceEvent)enm_84.next();
                    event = elem_20;
                    Boolean cond_25 = null;
                    Long var2_27 = null;
                    var2_27 = pgti.getVerticalSize();
                    cond_25 = new Boolean(ov_uypos.longValue() < var2_27.longValue());
                    if(cond_25.booleanValue())
                        if((new Boolean(event instanceof IOmlThreadCreate)).booleanValue())
                            drawCpuThreadCreate(pgti, (IOmlThreadCreate)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadSwapIn)).booleanValue())
                            drawCpuThreadSwapIn(pgti, (IOmlThreadSwapIn)event);
                        else
                        if((new Boolean(event instanceof IOmlDelayedThreadSwapIn)).booleanValue())
                            drawCpuDelayedThreadSwapIn(pgti, (IOmlDelayedThreadSwapIn)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadSwapOut)).booleanValue())
                            drawCpuThreadSwapOut(pgti, (IOmlThreadSwapOut)event);
                        else
                        if((new Boolean(event instanceof IOmlThreadKill)).booleanValue())
                            drawCpuThreadKill(pgti, (IOmlThreadKill)event);
                        else
                        if((new Boolean(event instanceof IOmlOpRequest)).booleanValue())
                            drawCpuOpRequest(pgti, (IOmlOpRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlOpActivate)).booleanValue())
                            drawCpuOpActivate(pgti, (IOmlOpActivate)event);
                        else
                        if((new Boolean(event instanceof IOmlOpCompleted)).booleanValue())
                            drawCpuOpCompleted(pgti, (IOmlOpCompleted)event);
                        else
                        if((new Boolean(event instanceof IOmlMessageRequest)).booleanValue())
                            drawCpuMessageRequest(pgti, (IOmlMessageRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlReplyRequest)).booleanValue())
                            drawCpuReplyRequest(pgti, (IOmlReplyRequest)event);
                        else
                        if((new Boolean(event instanceof IOmlMessageCompleted)).booleanValue())
                            drawCpuMessageCompleted(pgti, (IOmlMessageCompleted)event);
                }

                Boolean cond_85 = null;
                if(!(cond_85 = new Boolean(UTIL.equals(rest_uhist, new Vector()))).booleanValue())
                {
                    Boolean var2_89 = null;
                    Long var2_91 = null;
                    var2_91 = pgti.getVerticalSize();
                    var2_89 = new Boolean(ov_uypos.longValue() >= var2_91.longValue());
                    cond_85 = var2_89;
                }
                if(cond_85.booleanValue())
                {
                    cont = (Boolean)UTIL.clone(new Boolean(false));
                } else
                {
                    event_utime = UTIL.NumberToLong(UTIL.clone(UTIL.NumberToLong(rest_uhist.get(0))));
                    rest_uhist = (Vector)UTIL.ConvertToList(UTIL.clone(new Vector(rest_uhist.subList(1, rest_uhist.size()))));
                }
            }

            HashSet iset_97 = new HashSet();
            iset_97 = cpu.connects();
            Long busid = null;
            tdBUS bus;
            for(Iterator enm_111 = iset_97.iterator(); enm_111.hasNext(); drawCpuTimeMarkerHelper(pgti, bus))
            {
                Long elem_98 = UTIL.NumberToLong(enm_111.next());
                busid = elem_98;
                bus = null;
                bus = data.getBUS(busid);
                tdBUS tmpArg_v_106 = null;
                tmpArg_v_106 = data.getBUS(busid);
                updateCpuBus(pgti, tmpArg_v_106);
            }

            HashSet iset_112 = new HashSet();
            iset_112 = cpu.getObjects();
            Long objid = null;
            tdObject obj;
            for(Iterator enm_126 = iset_112.iterator(); enm_126.hasNext(); drawCpuTimeMarkerHelper(pgti, obj))
            {
                Long elem_113 = UTIL.NumberToLong(enm_126.next());
                objid = elem_113;
                obj = null;
                obj = cpu.getObject(objid);
                updateCpuObject(pgti, cpu, obj);
            }

        }
    }

    private void updateOvBus(GenericTabItem pgti, tdBUS ptdr)
        throws CGException
    {
        Long tmpVal_4 = null;
        tmpVal_4 = ptdr.getX();
        Long xpos = null;
        xpos = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = ptdr.getY();
        Long ypos = null;
        ypos = tmpVal_5;
        if((new Boolean(ov_uxpos.longValue() > xpos.longValue())).booleanValue())
        {
            Line line = new Line(new Long(xpos.longValue() + (new Long(1L)).longValue()), ypos, new Long(ov_uxpos.longValue() + (new Long(1L)).longValue()), ypos);
            Boolean cond_17 = null;
            cond_17 = ptdr.isIdle();
            if(cond_17.booleanValue())
            {
                line.setForegroundColor(ColorConstants.lightGray);
                line.setDot();
            } else
            {
                line.setForegroundColor(ColorConstants.blue);
                line.setLineWidth(new Long(3L));
            }
            pgti.addFigure(line);
            ptdr.setX(ov_uxpos);
        }
    }

    private void updateOvCpu(GenericTabItem pgti, tdCPU ptdr)
        throws CGException
    {
        Long tmpVal_4 = null;
        tmpVal_4 = ptdr.getX();
        Long xpos = null;
        xpos = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = ptdr.getY();
        Long ypos = null;
        ypos = tmpVal_5;
        if((new Boolean(ov_uxpos.longValue() > xpos.longValue())).booleanValue())
        {
            Line line = new Line(new Long(xpos.longValue() + (new Long(1L)).longValue()), ypos, new Long(ov_uxpos.longValue() + (new Long(1L)).longValue()), ypos);
            Boolean cond_17 = null;
            cond_17 = ptdr.isIdle();
            if(cond_17.booleanValue())
            {
                line.setForegroundColor(ColorConstants.lightGray);
                line.setDot();
            } else
            {
                tdThread thr = null;
                thr = ptdr.getCurrentThread();
                line.setForegroundColor(ColorConstants.blue);
                Boolean cond_22 = null;
                cond_22 = thr.getStatus();
                if(cond_22.booleanValue())
                    line.setDot();
                line.setLineWidth(new Long(3L));
            }
            pgti.addFigure(line);
            ptdr.setX(ov_uxpos);
            Boolean cond_33 = null;
            cond_33 = ptdr.hasCurrentThread();
            if(cond_33.booleanValue())
            {
                tdThread thr = null;
                thr = ptdr.getCurrentThread();
                Long thrid = null;
                thrid = thr.getId();
                checkConjectureLimits(pgti, new Long(ov_uxpos.longValue() - ELEMENT_uSIZE.longValue()), ypos, ov_ucurrenttime, thrid);
            }
        }
    }

    private void updateCpuObject(GenericTabItem pgti, tdCPU pcpu, tdObject pobj)
        throws CGException
    {
       // Long width = new Long((new Long((new Long(2L)).longValue() * CPU_uHALFWIDTH.longValue())).longValue() + (new Long(123L)).longValue());
    	Long width = Long.valueOf(10);
        Long xpos = null;
        xpos = pobj.getX();
        Long ypos = null;
        ypos = pobj.getY();
        Boolean cond_10 = null;
        if((cond_10 = new Boolean(xpos.longValue() == (new Long(0L)).longValue())).booleanValue())
            cond_10 = new Boolean(ypos.longValue() == (new Long(0L)).longValue());
        if(cond_10.booleanValue())
        {
            String str = null;
            String var1_17 = null;
            String var1_18 = null;
            String var1_19 = null;
            var1_19 = pobj.getName();
            var1_18 = var1_19.concat(new String(" ("));
            String var2_21 = null;
            Long par_22 = null;
            par_22 = pobj.getId();
            var2_21 = nat2str(par_22);
            var1_17 = var1_18.concat(var2_21);
            str = var1_17.concat(new String(")"));
            NormalLabel nlb = null;
            org.eclipse.swt.graphics.Font arg_25 = null;
            arg_25 = pgti.getCurrentFont();
            nlb = new NormalLabel(str, arg_25);
            width = Long.valueOf(10*str.length());//TODO fix for CPU Box size
            RectangleLabelFigure nrr = new RectangleLabelFigure(nlb);
            Point np = new Point(ov_uxpos.longValue(), CPU_uYPOS.longValue());
            pcpu.addObject(pobj);
            nrr.setLocation(np);
            nrr.setSize(width, CPU_uHEIGHT);
            pgti.addFigure(nrr);
            xpos = UTIL.NumberToLong(UTIL.clone(new Long((new Long(ov_uxpos.longValue() + CPU_uHALFWIDTH.longValue())).longValue() + (new Long(12L)).longValue())));
            pobj.setX(xpos);
            ypos = UTIL.NumberToLong(UTIL.clone(new Long((new Long(CPU_uYPOS.longValue() + CPU_uHEIGHT.longValue())).longValue() + ELEMENT_uSIZE.longValue())));
            pobj.setY(ypos);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long((new Long(ov_uxpos.longValue() + width.longValue())).longValue() + CPU_uHINTERVAL.longValue())));
        }
        if((new Boolean(ov_uypos.longValue() > ypos.longValue())).booleanValue())
        {
            Line line = new Line(xpos, new Long(ypos.longValue() + (new Long(1L)).longValue()), xpos, new Long(ov_uypos.longValue() - (new Long(1L)).longValue()));
            line.setForegroundColor(ColorConstants.lightGray);
            line.setDot();
            pgti.addFigure(line);
            pobj.setY(ov_uypos);
        }
    }

    private void updateCpuBus(GenericTabItem pgti, tdBUS ptdr)
        throws CGException
    {
        Long tmpVal_4 = null;
        tmpVal_4 = ptdr.getX();
        Long xpos = null;
        xpos = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = ptdr.getY();
        Long ypos = null;
        ypos = tmpVal_5;
        if((new Boolean(ov_uypos.longValue() > ypos.longValue())).booleanValue())
        {
            Line line = new Line(xpos, new Long(ypos.longValue() + (new Long(1L)).longValue()), xpos, new Long(ov_uypos.longValue() - (new Long(1L)).longValue()));
            line.setForegroundColor(ColorConstants.lightGray);
            line.setDot();
            pgti.addFigure(line);
            ptdr.setY(ov_uypos);
        }
    }

    private void drawOvMarker(GenericTabItem pgti, Long x1, Long y1, Long x2, Long y2, Color clr)
        throws CGException
    {
        if(!pre_drawOvMarker(pgti, x1, y1, x2, y2, clr).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in drawOvMarker");
        Line line = new Line(x1, y1, x2, y2);
        line.setLineWidth(new Long(3L));
        line.setForegroundColor(clr);
        pgti.addFigure(line);
        line = (Line)UTIL.clone(new Line(x1, new Long(y1.longValue() - (new Long(5L)).longValue()), x1, new Long(y1.longValue() + (new Long(5L)).longValue())));
        pgti.addFigure(line);
        line = (Line)UTIL.clone(new Line(x2, new Long(y2.longValue() - (new Long(5L)).longValue()), x2, new Long(y2.longValue() + (new Long(5L)).longValue())));
        pgti.addFigure(line);
    }

    private Boolean pre_drawOvMarker(GenericTabItem pgti, Long x1, Long y1, Long x2, Long y2, Color clr)
        throws CGException
    {
        return new Boolean(x1.longValue() < x2.longValue());
    }

    private void drawCpuMarker(GenericTabItem pgti, Long x1, Long y1, Long x2, Long y2, Color clr)
        throws CGException
    {
        if(!pre_drawCpuMarker(pgti, x1, y1, x2, y2, clr).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in drawCpuMarker");
        Line line = new Line(x1, y1, x2, y2);
        line.setLineWidth(new Long(3L));
        line.setForegroundColor(clr);
        pgti.addFigure(line);
        line = (Line)UTIL.clone(new Line(new Long(x1.longValue() - (new Long(5L)).longValue()), y1, new Long(x1.longValue() + (new Long(5L)).longValue()), y1));
        pgti.addFigure(line);
        line = (Line)UTIL.clone(new Line(new Long(x2.longValue() - (new Long(5L)).longValue()), y2, new Long(x2.longValue() + (new Long(5L)).longValue()), y2));
        pgti.addFigure(line);
    }

    private Boolean pre_drawCpuMarker(GenericTabItem pgti, Long x1, Long y1, Long x2, Long y2, Color clr)
        throws CGException
    {
        return new Boolean(y1.longValue() < y2.longValue());
    }

    private void drawOvTimeMarker(GenericTabItem pgti, Long x, Long y, Long marktime)
        throws CGException
    {
        Long dy = new Long(RESOURCE_uVINTERVAL.longValue() / (new Long(2L)).longValue());
        Line line1 = new Line(x, new Long(dy.longValue() - (new Long(10L)).longValue()), x, new Long(y.longValue() - dy.longValue()));
        Line line2 = new Line(x, y, x, new Long(y.longValue() + (new Long(5L)).longValue()));
        RotatedLabel label = null;
        org.eclipse.swt.graphics.Font arg_24 = null;
        arg_24 = pgti.getCurrentFont();
        label = new RotatedLabel(nat2str(marktime), arg_24);
        Long xoffset = null;
        Long var1_26 = null;
        Dimension tmpRec_27 = null;
        tmpRec_27 = label.getSize();
        var1_26 = new Long(tmpRec_27.width);
        xoffset = new Long(var1_26.longValue() / (new Long(2L)).longValue());
        Point pt = new Point((new Long((new Long(x.longValue() - xoffset.longValue())).longValue() - (new Long(1L)).longValue())).longValue(), (new Long(y.longValue() + (new Long(10L)).longValue())).longValue());
        line1.setForegroundColor(ColorConstants.lightGray);
        line1.setDot();
        pgti.addFigure(line1);
        pgti.addFigure(line2);
        label.setLocation(pt);
        pgti.addFigure(label);
    }

    private void drawCpuTimeMarker(GenericTabItem pgti, Long x, Long y, Long marktime)
        throws CGException
    {
        Line line1 = new Line(new Long(x.longValue() - (new Long(5L)).longValue()), y, x, y);
        Line line2 = new Line(new Long(100L), y, new Long((new Long(101L)).longValue() + CPU_uHALFWIDTH.longValue()), y);
        NormalLabel label = null;
        org.eclipse.swt.graphics.Font arg_18 = null;
        arg_18 = pgti.getCurrentFont();
        label = new NormalLabel(nat2str(marktime), arg_18);
        Long xoffset = null;
        Long var2_21 = null;
        Dimension tmpRec_22 = null;
        tmpRec_22 = label.getSize();
        var2_21 = new Long(tmpRec_22.width);
        xoffset = new Long((new Long(10L)).longValue() + var2_21.longValue());
        Long yoffset = null;
        Long var2_24 = null;
        Long var1_25 = null;
        Dimension tmpRec_26 = null;
        tmpRec_26 = label.getSize();
        var1_25 = new Long(tmpRec_26.height);
        var2_24 = new Long(var1_25.longValue() / (new Long(2L)).longValue());
        yoffset = new Long((new Long(1L)).longValue() + var2_24.longValue());
        Point pt = new Point((new Long(x.longValue() - xoffset.longValue())).longValue(), (new Long(y.longValue() - yoffset.longValue())).longValue());
        ov_utimepos.add(y);
        pgti.addFigure(line1);
        line2.setForegroundColor(ColorConstants.lightGray);
        line2.setDot();
        pgti.addFigure(line2);
        label.setLocation(pt);
        pgti.addFigure(label);
    }

    private void drawCpuTimeMarkerHelper(GenericTabItem pgti, tdResource res)
        throws CGException
    {
        Long width = new Long((new Long((new Long((new Long(2L)).longValue() * CPU_uHALFWIDTH.longValue())).longValue() + (new Long(23L)).longValue())).longValue() + CPU_uHINTERVAL.longValue());
        Long xmax = new Long(ov_uxpos.longValue() - CPU_uHINTERVAL.longValue());
        Long tmpVal_14 = null;
        Long var1_15 = null;
        var1_15 = res.getX();
        tmpVal_14 = new Long(var1_15.longValue() + (new Long(10L)).longValue());
        Long x1 = null;
        x1 = tmpVal_14;
        Long tmpVal_17 = null;
        Boolean cond_19 = null;
        Long var1_20 = null;
        Long var1_21 = null;
        Long var1_22 = null;
        var1_22 = res.getX();
        var1_21 = new Long(var1_22.longValue() + width.longValue());
        var1_20 = new Long(var1_21.longValue() - (new Long(10L)).longValue());
        cond_19 = new Boolean(var1_20.longValue() > xmax.longValue());
        if(cond_19.booleanValue())
        {
            tmpVal_17 = xmax;
        } else
        {
            Long var1_26 = null;
            Long var1_27 = null;
            var1_27 = res.getX();
            var1_26 = new Long(var1_27.longValue() + width.longValue());
            tmpVal_17 = new Long(var1_26.longValue() - (new Long(10L)).longValue());
        }
        Long x2 = null;
        x2 = tmpVal_17;
        Long ypos = null;
        Line line;
        for(Iterator enm_44 = ov_utimepos.iterator(); enm_44.hasNext(); pgti.addFigure(line))
        {
            Long elem_31 = UTIL.NumberToLong(enm_44.next());
            ypos = elem_31;
            line = new Line(x1, ypos, x2, ypos);
            line.setForegroundColor(ColorConstants.lightGray);
            line.setDot();
        }

    }

    private void drawVerticalArrow(GenericTabItem pgti, Long x, Long y1, Long y2, String str, Color clr)
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

    private void drawHorizontalArrow(GenericTabItem pgti, Long x1, Long x2, Long y, String str, Color clr)
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

    @Override
	public void visitTraceFile(IOmlTraceFile pitf)
        throws CGException
    {
        tdCPU cpu = null;
        cpu = data.createCPU(new Long(0L), new String("vCPU0"), new Boolean(false));
        tdBUS bus = null;
        bus = data.createBUS(new Long(0L), new String("vBUS"), new Boolean(false));
        tdObject obj = null;
        obj = data.createObject(new Long(0L), new String("VdmTools"));
        cpu.connect(new Long(0L));
        bus.connect(new Long(0L));
        obj.deploy(new Long(0L), new Long(0L));
        Vector lines = null;
        lines = pitf.getTrace();
        IOmlTraceEvent line = null;
        for(Iterator enm_26 = lines.iterator(); enm_26.hasNext(); visitTraceEvent(line))
        {
            IOmlTraceEvent elem_20 = (IOmlTraceEvent)enm_26.next();
            line = elem_20;
        }

    }

    @Override
	public void visitThreadCreate(IOmlThreadCreate pitc)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitc.getCpunm();
        Long thrid = null;
        thrid = pitc.getId();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_6;
        tdThread thr = null;
        thr = cpu.createThread(thrid);
        Long par_13 = null;
        par_13 = pitc.getObstime();
        data.addHistory(pitc, par_13);
        Long par_16 = null;
        par_16 = pitc.getObstime();
        cpu.addHistory(pitc, par_16);
        Long par_19 = null;
        par_19 = pitc.getObstime();
        thr.addHistory(pitc, par_19);
    }

    private void drawOvThreadCreate(GenericTabItem pgti, IOmlThreadCreate pitc)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitc.getCpunm();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_6;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvCpu(pgti, cpu);
            Long x1 = null;
            x1 = cpu.getX();
            Long x2 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
            Long tmpVal_19 = null;
            tmpVal_19 = cpu.getY();
            Long y1 = null;
            y1 = tmpVal_19;
            Long tmpVal_20 = null;
            tmpVal_20 = y1;
            Long y2 = null;
            y2 = tmpVal_20;
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.green);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
    }

    private void drawCpuThreadCreate(GenericTabItem pgti, IOmlThreadCreate pitc)
        throws CGException
    {
        tdThread thr = null;
        Long par_5 = null;
        par_5 = pitc.getId();
        thr = data.getThread(par_5);
        Long objref = null;
        Boolean cond_8 = null;
        cond_8 = pitc.hasObjref();
        if(cond_8.booleanValue())
            objref = pitc.getObjref();
        else
            objref = new Long(0L);
        Long cpunm = null;
        cpunm = pitc.getCpunm();
        tdCPU tmpVal_10 = null;
        tmpVal_10 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_10;
        tdObject obj = null;
        obj = data.getObject(objref);
        thr.pushCurrentObject(objref);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateCpuObject(pgti, cpu, obj);
            Long x1 = null;
            x1 = obj.getX();
            Long x2 = x1;
            Long tmpVal_26 = null;
            tmpVal_26 = obj.getY();
            Long y1 = null;
            y1 = tmpVal_26;
            Long tmpVal_27 = null;
            tmpVal_27 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
            Long y2 = null;
            y2 = tmpVal_27;
            drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.green);
            ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
            obj.setY(y2);
        }
    }

    @Override
	public void visitThreadSwapIn(IOmlThreadSwapIn pitsw)
        throws CGException
    {
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pitsw.getObstime();
        data.addHistory(pitsw, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pitsw.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pitsw.getObstime();
        obj_11.addHistory(pitsw, par_14);
        Long par_17 = null;
        par_17 = pitsw.getObstime();
        thr.addHistory(pitsw, par_17);
    }

    private void drawOvThreadSwapIn(GenericTabItem pgti, IOmlThreadSwapIn pitsw)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
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
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
        Long par_38 = null;
        par_38 = pitsw.getId();
        cpu.setCurrentThread(par_38);
    }

    private void drawOvSwapInImage(GenericTabItem pgti, Long x, Long y)
        throws CGException
    {
        org.eclipse.swt.graphics.Image image = null;
        String par_4 = null;
        par_4 = pgti.composePath(new String("icons"), new String("vswapin.gif"));
        image = pgti.getImage(par_4);
        if((new Boolean(!UTIL.equals(image, null))).booleanValue())
        {
            ImageFigure imagefig = new ImageFigure(image);
            Point point = new Point((new Long(x.longValue() + (new Long(2L)).longValue())).longValue(), (new Long(y.longValue() - (new Long(24L)).longValue())).longValue());
            imagefig.setLocation(point);
            imagefig.setSize(16, 20);
            pgti.addFigure(imagefig);
        }
    }

    private void drawCpuThreadSwapIn(GenericTabItem pgti, IOmlThreadSwapIn pitsw)
        throws CGException
    {
        Long objref = null;
        Boolean cond_6 = null;
        cond_6 = pitsw.hasObjref();
        if(cond_6.booleanValue())
            objref = pitsw.getObjref();
        else
            objref = new Long(0L);
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdObject obj = null;
        obj = data.getObject(objref);
        tdCPU tmpVal_13 = null;
        tmpVal_13 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_13;
        cpu.setCurrentThread(thrid);
        thr.pushCurrentObject(objref);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateCpuObject(pgti, cpu, obj);
            Long x1 = null;
            x1 = obj.getX();
            Long x2 = x1;
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
        }
    }

    private void drawCpuSwapInImage(GenericTabItem pgti, Long x, Long y)
        throws CGException
    {
        org.eclipse.swt.graphics.Image image = null;
        String par_4 = null;
        par_4 = pgti.composePath(new String("icons"), new String("hswapin.gif"));
        image = pgti.getImage(par_4);
        if((new Boolean(!UTIL.equals(image, null))).booleanValue())
        {
            ImageFigure imagefig = new ImageFigure(image);
            Point point = new Point((new Long(x.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y.longValue() + (new Long(2L)).longValue())).longValue());
            imagefig.setLocation(point);
            imagefig.setSize(20, 16);
            pgti.addFigure(imagefig);
        }
    }

    @Override
	public void visitDelayedThreadSwapIn(IOmlDelayedThreadSwapIn pitsw)
        throws CGException
    {
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pitsw.getObstime();
        data.addHistory(pitsw, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pitsw.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pitsw.getObstime();
        obj_11.addHistory(pitsw, par_14);
        Long par_17 = null;
        par_17 = pitsw.getObstime();
        thr.addHistory(pitsw, par_17);
    }

    private void drawOvDelayedThreadSwapIn(GenericTabItem pgti, IOmlDelayedThreadSwapIn pitsw)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_6;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvCpu(pgti, cpu);
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
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
        Long par_38 = null;
        par_38 = pitsw.getId();
        cpu.setCurrentThread(par_38);
    }

    private void drawCpuDelayedThreadSwapIn(GenericTabItem pgti, IOmlDelayedThreadSwapIn pitsw)
        throws CGException
    {
        Long objref = null;
        Boolean cond_6 = null;
        cond_6 = pitsw.hasObjref();
        if(cond_6.booleanValue())
            objref = pitsw.getObjref();
        else
            objref = new Long(0L);
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdObject obj = null;
        obj = data.getObject(objref);
        tdCPU tmpVal_13 = null;
        tmpVal_13 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_13;
        cpu.setCurrentThread(thrid);
        thr.pushCurrentObject(objref);
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
        }
    }

    @Override
	public void visitThreadSwapOut(IOmlThreadSwapOut pitsw)
        throws CGException
    {
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pitsw.getObstime();
        data.addHistory(pitsw, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pitsw.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pitsw.getObstime();
        obj_11.addHistory(pitsw, par_14);
        Long par_17 = null;
        par_17 = pitsw.getObstime();
        thr.addHistory(pitsw, par_17);
    }

    private void drawOvThreadSwapOut(GenericTabItem pgti, IOmlThreadSwapOut pitsw)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_6;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateOvCpu(pgti, cpu);
            Long tmpVal_15 = null;
            tmpVal_15 = cpu.getX();
            Long x1 = null;
            x1 = tmpVal_15;
            Long tmpVal_16 = null;
            tmpVal_16 = new Long(x1.longValue() + ELEMENT_uSIZE.longValue());
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
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.gray);
            drawOvSwapOutImage(pgti, x1, y1);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
        cpu.setCurrentThread(null);
    }

    private void drawOvSwapOutImage(GenericTabItem pgti, Long x, Long y)
        throws CGException
    {
        org.eclipse.swt.graphics.Image image = null;
        String par_4 = null;
        par_4 = pgti.composePath(new String("icons"), new String("vswapout.gif"));
        image = pgti.getImage(par_4);
        if((new Boolean(!UTIL.equals(image, null))).booleanValue())
        {
            ImageFigure imagefig = new ImageFigure(image);
            Point point = new Point((new Long(x.longValue() + (new Long(2L)).longValue())).longValue(), (new Long(y.longValue() - (new Long(24L)).longValue())).longValue());
            imagefig.setLocation(point);
            imagefig.setSize(16, 20);
            pgti.addFigure(imagefig);
        }
    }

    private void drawCpuThreadSwapOut(GenericTabItem pgti, IOmlThreadSwapOut pitsw)
        throws CGException
    {
        Long objref = null;
        Boolean cond_6 = null;
        cond_6 = pitsw.hasObjref();
        if(cond_6.booleanValue())
            objref = pitsw.getObjref();
        else
            objref = new Long(0L);
        Long thrid = null;
        thrid = pitsw.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdObject obj = null;
        obj = data.getObject(objref);
        tdCPU tmpVal_13 = null;
        tmpVal_13 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_13;
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateCpuObject(pgti, cpu, obj);
            Long tmpVal_23 = null;
            tmpVal_23 = obj.getX();
            Long x1 = null;
            x1 = tmpVal_23;
            Long tmpVal_24 = null;
            tmpVal_24 = x1;
            Long x2 = null;
            x2 = tmpVal_24;
            Long tmpVal_25 = null;
            tmpVal_25 = obj.getY();
            Long y1 = null;
            y1 = tmpVal_25;
            Long tmpVal_26 = null;
            tmpVal_26 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
            Long y2 = null;
            y2 = tmpVal_26;
            drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.gray);
            drawCpuSwapOutImage(pgti, x1, y1);
            ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
            obj.setY(y2);
        }
        cpu.setCurrentThread(null);
        thr.popCurrentObject();
    }

    private void drawCpuSwapOutImage(GenericTabItem pgti, Long x, Long y)
        throws CGException
    {
        org.eclipse.swt.graphics.Image image = null;
        String par_4 = null;
        par_4 = pgti.composePath(new String("icons"), new String("hswapout.gif"));
        image = pgti.getImage(par_4);
        if((new Boolean(!UTIL.equals(image, null))).booleanValue())
        {
            ImageFigure imagefig = new ImageFigure(image);
            Point point = new Point((new Long(x.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y.longValue() + (new Long(2L)).longValue())).longValue());
            imagefig.setLocation(point);
            imagefig.setSize(20, 16);
            pgti.addFigure(imagefig);
        }
    }

    @Override
	public void visitThreadKill(IOmlThreadKill pitk)
        throws CGException
    {
        Long thrid = null;
        thrid = pitk.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pitk.getObstime();
        data.addHistory(pitk, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pitk.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pitk.getObstime();
        obj_11.addHistory(pitk, par_14);
        Long par_17 = null;
        par_17 = pitk.getObstime();
        thr.addHistory(pitk, par_17);
    }

    private void drawOvThreadKill(GenericTabItem pgti, IOmlThreadKill pitsw)
        throws CGException
    {
        Long cpunm = null;
        cpunm = pitsw.getCpunm();
        tdCPU tmpVal_6 = null;
        tmpVal_6 = data.getCPU(cpunm);
        tdCPU cpu = null;
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
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            cpu.setX(x2);
        }
    }

    private void drawCpuThreadKill(GenericTabItem pgti, IOmlThreadKill pitk)
        throws CGException
    {
        Long thrid = null;
        thrid = pitk.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long cpunm = null;
        cpunm = pitk.getCpunm();
        tdCPU tmpVal_8 = null;
        tmpVal_8 = data.getCPU(cpunm);
        tdCPU cpu = null;
        cpu = tmpVal_8;
        tdObject obj = null;
        obj = thr.getCurrentObject();
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            updateCpuObject(pgti, cpu, obj);
            Long x1 = null;
            x1 = obj.getX();
            Long x2 = x1;
            Long y1 = null;
            y1 = obj.getY();
            Long y2 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
            drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.red);
            ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
            obj.setY(y2);
        }
        thr.popCurrentObject();
    }

    @Override
	public void visitOpRequest(IOmlOpRequest pior)
        throws CGException
    {
        Long thrid = null;
        thrid = pior.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pior.getObstime();
        data.addHistory(pior, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pior.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pior.getObstime();
        obj_11.addHistory(pior, par_14);
        Long par_17 = null;
        par_17 = pior.getObstime();
        thr.addHistory(pior, par_17);
    }

    public void drawOvOpRequest(GenericTabItem pgti, IOmlOpRequest pior)
        throws CGException
    {
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            Long cpunm = null;
            cpunm = pior.getCpunm();
            tdCPU tmpVal_9 = null;
            tmpVal_9 = data.getCPU(cpunm);
            tdCPU cpu = null;
            cpu = tmpVal_9;
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uxpos.longValue() + ELEMENT_uSIZE.longValue())));
            updateOvCpu(pgti, cpu);
        }
        Boolean cond_17 = null;
        Boolean unArg_18 = null;
        unArg_18 = pior.getAsynchronous();
        cond_17 = new Boolean(!unArg_18.booleanValue());
        if(cond_17.booleanValue())
        {
            Boolean cond_19 = null;
            cond_19 = pior.hasObjref();
            if(cond_19.booleanValue())
            {
                Long thrid = null;
                thrid = pior.getId();
                Long cpunm = null;
                cpunm = pior.getCpunm();
                Long objref = null;
                objref = pior.getObjref();
                tdCPU tmpVal_25 = null;
                tmpVal_25 = data.getCPU(cpunm);
                tdCPU cpu = null;
                cpu = tmpVal_25;
                Boolean cond_27 = null;
                Boolean unArg_28 = null;
                Long par_30 = null;
                par_30 = pior.getObstime();
                unArg_28 = cpu.hasObjectAt(objref, par_30);
                cond_27 = new Boolean(!unArg_28.booleanValue());
                if(cond_27.booleanValue())
                {
                    tdThread obj_32 = null;
                    obj_32 = cpu.getThread(thrid);
                    obj_32.setStatus(new Boolean(true));
                }
            }
        }
    }

    public void drawCpuOpRequest(GenericTabItem pgti, IOmlOpRequest pior)
        throws CGException
    {
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            Long thrid = null;
            thrid = pior.getId();
            Long objid = null;
            objid = pior.getObjref();
            Long cpunm = null;
            cpunm = pior.getCpunm();
            tdCPU tmpVal_10 = null;
            tmpVal_10 = data.getCPU(cpunm);
            tdCPU cpu = null;
            cpu = tmpVal_10;
            tdThread thr = null;
            thr = data.getThread(thrid);
            Boolean cond_14 = null;
            cond_14 = thr.hasCurrentObject();
            if(cond_14.booleanValue())
            {
                tdObject obj = null;
                obj = thr.getCurrentObject();
                updateCpuObject(pgti, cpu, obj);
                Long x1 = null;
                x1 = obj.getX();
                Long x2 = x1;
                Long y1 = null;
                y1 = obj.getY();
                Long y2 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
                NormalLabel lbl = null;
                org.eclipse.swt.graphics.Font arg_29 = null;
                arg_29 = pgti.getCurrentFont();
                lbl = new NormalLabel(new String("R"), arg_29);
                String str = null;
                Boolean cond_31 = null;
                cond_31 = pior.hasArgs();
                if(cond_31.booleanValue())
                {
                    String var2_33 = null;
                    var2_33 = pior.getArgs();
                    str = (new String(" with arguments ")).concat(var2_33);
                } else
                {
                    str = UTIL.ConvertToString(new String());
                }
                NormalLabel ttl = null;
                String arg_34 = null;
                String var1_36 = null;
                String var1_37 = null;
                String var1_38 = null;
                String var1_39 = null;
                String var2_41 = null;
                var2_41 = pior.getOpname();
                var1_39 = (new String(" Requested ")).concat(var2_41);
                var1_38 = var1_39.concat(new String(" on object "));
                var1_37 = var1_38.concat(nat2str(objid));
                var1_36 = var1_37.concat(str);
                arg_34 = var1_36.concat(new String(" "));
                org.eclipse.swt.graphics.Font arg_35 = null;
                arg_35 = pgti.getCurrentFont();
                ttl = new NormalLabel(arg_34, arg_35);
                Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
                drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.blue);
                lbl.setToolTip(ttl);
                lbl.setLocation(pt);
                pgti.addFigure(lbl);
                ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
                obj.setY(y2);
            }
        }
    }

    @Override
	public void visitOpActivate(IOmlOpActivate pioa)
        throws CGException
    {
        Long thrid = null;
        thrid = pioa.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pioa.getObstime();
        data.addHistory(pioa, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pioa.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pioa.getObstime();
        obj_11.addHistory(pioa, par_14);
        Long par_17 = null;
        par_17 = pioa.getObstime();
        thr.addHistory(pioa, par_17);
    }

    public void drawOvOpActivate(GenericTabItem pgti, IOmlOpActivate pioa)
        throws CGException
    {
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            Long cpunm = null;
            cpunm = pioa.getCpunm();
            tdCPU tmpVal_9 = null;
            tmpVal_9 = data.getCPU(cpunm);
            tdCPU cpu = null;
            cpu = tmpVal_9;
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uxpos.longValue() + ELEMENT_uSIZE.longValue())));
            updateOvCpu(pgti, cpu);
        }
    }

    public void drawCpuOpActivate(GenericTabItem pgti, IOmlOpActivate pioa)
        throws CGException
    {
        Long thrid = null;
        thrid = pioa.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        tdObject srcobj = null;
        srcobj = thr.getCurrentObject();
        Boolean cond_9 = null;
        Boolean unArg_10 = null;
        unArg_10 = pioa.hasObjref();
        cond_9 = new Boolean(!unArg_10.booleanValue());
        if(!cond_9.booleanValue())
        {
            Long destobjref = null;
            destobjref = pioa.getObjref();
            tdObject destobj = null;
            destobj = data.getObject(destobjref);
            if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
            {
                Long cpunm = null;
                cpunm = pioa.getCpunm();
                tdCPU tmpVal_20 = null;
                tmpVal_20 = data.getCPU(cpunm);
                tdCPU cpu = null;
                cpu = tmpVal_20;
                Boolean cond_22 = null;
                Long var1_23 = null;
                var1_23 = srcobj.getId();
                Long var2_24 = null;
                var2_24 = destobj.getId();
                cond_22 = new Boolean(var1_23.longValue() == var2_24.longValue());
                if(cond_22.booleanValue())
                {
                    updateCpuObject(pgti, cpu, destobj);
                    Long x1 = null;
                    x1 = destobj.getX();
                    Long x2 = x1;
                    Long y1 = null;
                    y1 = destobj.getY();
                    Long y2 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
                    NormalLabel lbl = null;
                    String arg_49 = null;
                    String var2_52 = null;
                    var2_52 = pioa.getOpname();
                    arg_49 = (new String("A ")).concat(var2_52);
                    org.eclipse.swt.graphics.Font arg_50 = null;
                    arg_50 = pgti.getCurrentFont();
                    lbl = new NormalLabel(arg_49, arg_50);
                    Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
                    drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.blue);
                    lbl.setLocation(pt);
                    pgti.addFigure(lbl);
                    ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
                    destobj.setY(y2);
                } else
                {
                    updateCpuObject(pgti, cpu, srcobj);
                    updateCpuObject(pgti, cpu, destobj);
                    String tmpArg_v_37 = null;
                    tmpArg_v_37 = pioa.getOpname();
                    Object2ObjectArrow(pgti, srcobj, destobj, tmpArg_v_37);
                }
            }
            thr.pushCurrentObject(destobjref);
        }
    }

    private void Object2ObjectArrow(GenericTabItem pgti, tdObject psrc, tdObject pdest, String pstr)
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
            Point pt = new Point((new Long(psx.longValue() + (new Long(20L)).longValue())).longValue(), (new Long(psy.longValue() + (new Long(2L)).longValue())).longValue());
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
            pt = new Point(arg_56.longValue(), (new Long(psy.longValue() + (new Long(2L)).longValue())).longValue());
            lbl.setLocation(pt);
            pgti.addFigure(lbl);
            line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(16L)).longValue())));
            line.setForegroundColor(ColorConstants.blue);
            pgti.addFigure(line);
            line = (Line)UTIL.clone(new Line(new Long(pdx.longValue() + (new Long(2L)).longValue()), new Long(pdy.longValue() + (new Long(20L)).longValue()), new Long(pdx.longValue() + (new Long(10L)).longValue()), new Long(pdy.longValue() + (new Long(24L)).longValue())));
            line.setForegroundColor(ColorConstants.blue);
            pgti.addFigure(line);
        }
        ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uypos.longValue() + (new Long(40L)).longValue())));
        psrc.setY(ov_uypos);
        pdest.setY(ov_uypos);
    }

    @Override
	public void visitOpCompleted(IOmlOpCompleted pioc)
        throws CGException
    {
        Long thrid = null;
        thrid = pioc.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        Long par_9 = null;
        par_9 = pioc.getObstime();
        data.addHistory(pioc, par_9);
        tdCPU obj_11 = null;
        Long par_12 = null;
        par_12 = pioc.getCpunm();
        obj_11 = data.getCPU(par_12);
        Long par_14 = null;
        par_14 = pioc.getObstime();
        obj_11.addHistory(pioc, par_14);
        Long par_17 = null;
        par_17 = pioc.getObstime();
        thr.addHistory(pioc, par_17);
    }

    public void drawOvOpCompleted(GenericTabItem pgti, IOmlOpCompleted pioc)
        throws CGException
    {
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            Long cpunm = null;
            cpunm = pioc.getCpunm();
            tdCPU tmpVal_9 = null;
            tmpVal_9 = data.getCPU(cpunm);
            tdCPU cpu = null;
            cpu = tmpVal_9;
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uxpos.longValue() + ELEMENT_uSIZE.longValue())));
            updateOvCpu(pgti, cpu);
        }
    }

    public void drawCpuOpCompleted(GenericTabItem pgti, IOmlOpCompleted pioc)
        throws CGException
    {
        Long thrid = null;
        thrid = pioc.getId();
        tdThread thr = null;
        thr = data.getThread(thrid);
        tdObject srcobj = null;
        srcobj = thr.getCurrentObject();
        Boolean cond_9 = null;
        Boolean unArg_10 = null;
        unArg_10 = pioc.hasObjref();
        cond_9 = new Boolean(!unArg_10.booleanValue());
        if(!cond_9.booleanValue())
        {
            thr.popCurrentObject();
            tdObject destobj = null;
            destobj = thr.getCurrentObject();
            if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
            {
                Long cpunm = null;
                cpunm = pioc.getCpunm();
                tdCPU tmpVal_19 = null;
                tmpVal_19 = data.getCPU(cpunm);
                tdCPU cpu = null;
                cpu = tmpVal_19;
                Boolean cond_21 = null;
                Long var1_22 = null;
                var1_22 = srcobj.getId();
                Long var2_23 = null;
                var2_23 = destobj.getId();
                cond_21 = new Boolean(var1_22.longValue() == var2_23.longValue());
                if(cond_21.booleanValue())
                {
                    updateCpuObject(pgti, cpu, destobj);
                    Long x1 = null;
                    x1 = destobj.getX();
                    Long x2 = x1;
                    Long tmpVal_44 = null;
                    tmpVal_44 = destobj.getY();
                    Long y1 = null;
                    y1 = tmpVal_44;
                    Long tmpVal_45 = null;
                    tmpVal_45 = new Long(y1.longValue() + ELEMENT_uSIZE.longValue());
                    Long y2 = null;
                    y2 = tmpVal_45;
                    Long objid = null;
                    objid = pioc.getObjref();
                    NormalLabel lbl = null;
                    org.eclipse.swt.graphics.Font arg_50 = null;
                    arg_50 = pgti.getCurrentFont();
                    lbl = new NormalLabel(new String("C"), arg_50);
                    String str = null;
                    Boolean cond_52 = null;
                    cond_52 = pioc.hasRes();
                    if(cond_52.booleanValue())
                    {
                        String var2_54 = null;
                        var2_54 = pioc.getRes();
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
                    String var2_62 = null;
                    var2_62 = pioc.getOpname();
                    var1_60 = (new String(" Completed ")).concat(var2_62);
                    var1_59 = var1_60.concat(new String(" on object "));
                    var1_58 = var1_59.concat(nat2str(objid));
                    var1_57 = var1_58.concat(str);
                    arg_55 = var1_57.concat(new String(" "));
                    org.eclipse.swt.graphics.Font arg_56 = null;
                    arg_56 = pgti.getCurrentFont();
                    ttl = new NormalLabel(arg_55, arg_56);
                    Point pt = new Point((new Long(x1.longValue() + (new Long(8L)).longValue())).longValue(), (new Long(y1.longValue() + (new Long(2L)).longValue())).longValue());
                    drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.blue);
                    lbl.setToolTip(ttl);
                    lbl.setLocation(pt);
                    pgti.addFigure(lbl);
                    ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
                    destobj.setY(y2);
                } else
                {
                    updateCpuObject(pgti, cpu, srcobj);
                    updateCpuObject(pgti, cpu, destobj);
                    Object2ObjectArrow(pgti, srcobj, destobj, new String(""));
                }
            }
        }
    }

    @Override
	public void visitMessageRequest(IOmlMessageRequest pimr)
        throws CGException
    {
        Long busid = null;
        busid = pimr.getBusid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        Long msgid = null;
        msgid = pimr.getMsgid();
        Long fromcpu = null;
        fromcpu = pimr.getFromcpu();
        Long fromthr = null;
        fromthr = pimr.getCallthr();
        Long tocpu = null;
        tocpu = pimr.getTocpu();
        Long toobj = null;
        toobj = pimr.getObjref();
        String tmpVal_12 = null;
        tmpVal_12 = pimr.getOpname();
        String str = null;
        str = tmpVal_12;
        tdMessage msg = null;
        msg = bus.createMessage(msgid, fromcpu, fromthr, tocpu, null, toobj, str);
        Long par_24 = null;
        par_24 = pimr.getObstime();
        data.addHistory(pimr, par_24);
        Long par_27 = null;
        par_27 = pimr.getObstime();
        bus.addHistory(pimr, par_27);
        Long par_30 = null;
        par_30 = pimr.getObstime();
        msg.addHistory(pimr, par_30);
        tdCPU obj_32 = null;
        obj_32 = data.getCPU(fromcpu);
        Long par_35 = null;
        par_35 = pimr.getObstime();
        obj_32.addHistory(pimr, par_35);
    }

    private void drawOvMessageRequest(GenericTabItem pgti, IOmlMessageRequest pitmr)
        throws CGException
    {
        Long busid = null;
        busid = pitmr.getBusid();
        Long msgid = null;
        msgid = pitmr.getMsgid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        tdMessage msg = null;
        msg = data.getMessage(msgid);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uxpos.longValue() + (new Long(6L)).longValue())));
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
            tdCPU obj_30 = null;
            Long par_31 = null;
            par_31 = msg.getFromCpu();
            obj_30 = data.getCPU(par_31);
            var1_29 = obj_30.getY();
            ycpu = new Long(var1_29.longValue() + (new Long(8L)).longValue());
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_47 = null;
            String var1_48 = null;
            String var2_50 = null;
            var2_50 = msg.getDescr();
            var1_48 = (new String(" call ")).concat(var2_50);
            tmpArg_v_47 = var1_48.concat(new String(" "));
            drawVerticalArrow(pgti, x1, ycpu, new Long(y1.longValue() - (new Long(8L)).longValue()), tmpArg_v_47, ColorConstants.darkBlue);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }
    }

    private void drawCpuMessageRequest(GenericTabItem pgti, IOmlMessageRequest pitmr)
        throws CGException
    {
        Long busid = null;
        busid = pitmr.getBusid();
        Long msgid = null;
        msgid = pitmr.getMsgid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        tdMessage msg = null;
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
            tdThread thr = null;
            Long par_29 = null;
            par_29 = msg.getFromThread();
            thr = data.getThread(par_29);
            tdObject obj = null;
            obj = thr.getCurrentObject();
            Long xobj = null;
            Long var1_34 = null;
            var1_34 = obj.getX();
            xobj = new Long(var1_34.longValue() - (new Long(10L)).longValue());
            drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_50 = null;
            String var1_51 = null;
            String var2_53 = null;
            var2_53 = msg.getDescr();
            var1_51 = (new String(" call ")).concat(var2_53);
            tmpArg_v_50 = var1_51.concat(new String(" "));
            drawHorizontalArrow(pgti, new Long(x1.longValue() + (new Long(10L)).longValue()), xobj, y1, tmpArg_v_50, ColorConstants.darkGreen);
            ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
            bus.setY(y2);
        }
    }

    @Override
	public void visitReplyRequest(IOmlReplyRequest pirr)
        throws CGException
    {
        Long busid = null;
        busid = pirr.getBusid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        Long msgid = null;
        msgid = pirr.getMsgid();
        Long fromcpu = null;
        fromcpu = pirr.getFromcpu();
        Long fromthr = null;
        fromthr = pirr.getCalleethr();
        Long tocpu = null;
        tocpu = pirr.getTocpu();
        Long tothr = null;
        tothr = pirr.getCallthr();
        String tmpVal_12 = null;
        tdMessage obj_13 = null;
        Long par_14 = null;
        par_14 = pirr.getOrigmsgid();
        obj_13 = data.getMessage(par_14);
        tmpVal_12 = obj_13.getDescr();
        String str = null;
        str = tmpVal_12;
        tdMessage msg = null;
        msg = bus.createMessage(msgid, fromcpu, fromthr, tocpu, tothr, null, str);
        Long par_26 = null;
        par_26 = pirr.getObstime();
        data.addHistory(pirr, par_26);
        Long par_29 = null;
        par_29 = pirr.getObstime();
        bus.addHistory(pirr, par_29);
        Long par_32 = null;
        par_32 = pirr.getObstime();
        msg.addHistory(pirr, par_32);
        tdCPU obj_34 = null;
        obj_34 = data.getCPU(fromcpu);
        Long par_37 = null;
        par_37 = pirr.getObstime();
        obj_34.addHistory(pirr, par_37);
    }

    private void drawOvReplyRequest(GenericTabItem pgti, IOmlReplyRequest pitrr)
        throws CGException
    {
        Long busid = null;
        busid = pitrr.getBusid();
        Long msgid = null;
        msgid = pitrr.getMsgid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        tdMessage msg = null;
        msg = data.getMessage(msgid);
        if((new Boolean(ov_ucurrenttime.longValue() >= ov_ustarttime.longValue())).booleanValue())
        {
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(ov_uxpos.longValue() + (new Long(6L)).longValue())));
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
            tdCPU obj_30 = null;
            Long par_31 = null;
            par_31 = msg.getFromCpu();
            obj_30 = data.getCPU(par_31);
            var1_29 = obj_30.getY();
            ycpu = new Long(var1_29.longValue() + (new Long(8L)).longValue());
            drawOvMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_47 = null;
            String var1_48 = null;
            String var2_50 = null;
            var2_50 = msg.getDescr();
            var1_48 = (new String(" return from ")).concat(var2_50);
            tmpArg_v_47 = var1_48.concat(new String(" "));
            drawVerticalArrow(pgti, x1, ycpu, new Long(y1.longValue() - (new Long(8L)).longValue()), tmpArg_v_47, ColorConstants.darkBlue);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }
    }

    private void drawCpuReplyRequest(GenericTabItem pgti, IOmlReplyRequest pitrr)
        throws CGException
    {
        Long busid = null;
        busid = pitrr.getBusid();
        Long msgid = null;
        msgid = pitrr.getMsgid();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        tdMessage msg = null;
        msg = data.getMessage(msgid);
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
            tdThread thr = null;
            Long par_29 = null;
            par_29 = msg.getFromThread();
            thr = data.getThread(par_29);
            tdObject obj = null;
            obj = thr.getCurrentObject();
            Long xobj = null;
            Long var1_34 = null;
            var1_34 = obj.getX();
            xobj = new Long(var1_34.longValue() - (new Long(10L)).longValue());
            drawCpuMarker(pgti, x1, y1, x2, y2, ColorConstants.lightGray);
            String tmpArg_v_50 = null;
            String var1_51 = null;
            String var2_53 = null;
            var2_53 = msg.getDescr();
            var1_51 = (new String(" return from ")).concat(var2_53);
            tmpArg_v_50 = var1_51.concat(new String(" "));
            drawHorizontalArrow(pgti, new Long(x1.longValue() + (new Long(10L)).longValue()), xobj, y1, tmpArg_v_50, ColorConstants.darkGreen);
            ov_uypos = UTIL.NumberToLong(UTIL.clone(y2));
            bus.setY(y2);
        }
    }

    @Override
	public void visitMessageActivate(IOmlMessageActivate pima)
        throws CGException
    {
        tdMessage msg = null;
        Long par_4 = null;
        par_4 = pima.getMsgid();
        msg = data.getMessage(par_4);
        tdBUS bus = null;
        Long par_7 = null;
        par_7 = msg.getBusId();
        bus = data.getBUS(par_7);
        Long par_10 = null;
        par_10 = pima.getObstime();
        data.addHistory(pima, par_10);
        Long par_13 = null;
        par_13 = pima.getObstime();
        bus.addHistory(pima, par_13);
        Long par_16 = null;
        par_16 = pima.getObstime();
        msg.addHistory(pima, par_16);
    }

    private void drawOvMessageActivate(GenericTabItem pgti, IOmlMessageActivate pitma)
        throws CGException
    {
        Long msgid = null;
        msgid = pitma.getMsgid();
        Long busid = null;
        tdMessage obj_7 = null;
        obj_7 = data.getMessage(msgid);
        busid = obj_7.getBusId();
        tdBUS bus = null;
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
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(x2));
            bus.setX(x2);
        }
    }

    @Override
	public void visitMessageCompleted(IOmlMessageCompleted pimc)
        throws CGException
    {
        tdMessage msg = null;
        Long par_4 = null;
        par_4 = pimc.getMsgid();
        msg = data.getMessage(par_4);
        tdBUS bus = null;
        Long par_7 = null;
        par_7 = msg.getBusId();
        bus = data.getBUS(par_7);
        Long par_10 = null;
        par_10 = pimc.getObstime();
        data.addHistory(pimc, par_10);
        Long par_13 = null;
        par_13 = pimc.getObstime();
        bus.addHistory(pimc, par_13);
        Long par_16 = null;
        par_16 = pimc.getObstime();
        msg.addHistory(pimc, par_16);
        tdCPU obj_18 = null;
        Long par_19 = null;
        par_19 = msg.getToCpu();
        obj_18 = data.getCPU(par_19);
        Long par_21 = null;
        par_21 = pimc.getObstime();
        obj_18.addHistory(pimc, par_21);
    }

    private void drawOvMessageCompleted(GenericTabItem pgti, IOmlMessageCompleted pitmc)
        throws CGException
    {
        Long msgid = null;
        msgid = pitmc.getMsgid();
        tdMessage msg = null;
        msg = data.getMessage(msgid);
        Long busid = null;
        busid = msg.getBusId();
        tdBUS bus = null;
        bus = data.getBUS(busid);
        tdCPU tmpVal_13 = null;
        Long par_14 = null;
        par_14 = msg.getToCpu();
        tmpVal_13 = data.getCPU(par_14);
        tdCPU cpu = null;
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
            String var2_49 = null;
            var2_49 = msg.getDescr();
            var1_47 = (new String(" ")).concat(var2_49);
            tmpArg_v_46 = var1_47.concat(new String(" "));
            drawVerticalArrow(pgti, x2, new Long(y1.longValue() - (new Long(8L)).longValue()), ycpu, tmpArg_v_46, ColorConstants.darkBlue);
            ov_uxpos = UTIL.NumberToLong(UTIL.clone(new Long(x2.longValue() + (new Long(6L)).longValue())));
            updateOvCpu(pgti, cpu);
            bus.setX(x2);
        }
        Boolean cond_60 = null;
        cond_60 = msg.hasToThread();
        if(cond_60.booleanValue())
        {
            tdThread obj_62 = null;
            Long par_63 = null;
            par_63 = msg.getToThread();
            obj_62 = cpu.getThread(par_63);
            obj_62.setStatus(new Boolean(false));
        }
    }

    private void drawCpuMessageCompleted(GenericTabItem pgti, IOmlMessageCompleted pitmc)
        throws CGException
    {
        Long msgid = null;
        msgid = pitmc.getMsgid();
        tdMessage msg = null;
        msg = data.getMessage(msgid);
        Long busid = null;
        busid = msg.getBusId();
        tdBUS bus = null;
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
            cond_32 = msg.hasToThread();
            if(cond_32.booleanValue())
            {
                tdThread thr = null;
                Long par_60 = null;
                par_60 = msg.getToThread();
                thr = data.getThread(par_60);
                tdObject obj = null;
                obj = thr.getCurrentObject();
                Long xobj = null;
                Long var1_65 = null;
                var1_65 = obj.getX();
                xobj = new Long(var1_65.longValue() - (new Long(10L)).longValue());
                String tmpArg_v_74 = null;
                String var1_75 = null;
                String var2_77 = null;
                var2_77 = msg.getDescr();
                var1_75 = (new String(" ")).concat(var2_77);
                tmpArg_v_74 = var1_75.concat(new String(" "));
                drawHorizontalArrow(pgti, xobj, new Long(x1.longValue() + (new Long(10L)).longValue()), y2, tmpArg_v_74, ColorConstants.darkGreen);
            } else
            {
                Long objid = null;
                objid = msg.getToObj();
                Long cpuid = null;
                cpuid = msg.getToCpu();
                tdObject obj = null;
                obj = data.getObject(objid);
                tdCPU tmpVal_41 = null;
                tmpVal_41 = data.getCPU(cpuid);
                tdCPU cpu = null;
                cpu = tmpVal_41;
                updateCpuObject(pgti, cpu, obj);
                Long tmpArg_v_49 = null;
                Long var1_50 = null;
                var1_50 = obj.getX();
                tmpArg_v_49 = new Long(var1_50.longValue() - (new Long(10L)).longValue());
                String tmpArg_v_56 = null;
                tmpArg_v_56 = msg.getDescr();
                drawHorizontalArrow(pgti, tmpArg_v_49, new Long(x1.longValue() + (new Long(10L)).longValue()), y2, tmpArg_v_56, ColorConstants.darkGreen);
            }
            ov_uypos = UTIL.NumberToLong(UTIL.clone(new Long(y2.longValue() + (new Long(10L)).longValue())));
            bus.setY(y2);
        }
    }

    @Override
	public void visitCPUdecl(IOmlCPUdecl picd)
        throws CGException
    {
        Long id = null;
        id = picd.getId();
        String name = null;
        name = picd.getName();
        Boolean expl = null;
        expl = picd.getExpl();
        tdCPU cpu = null;
        cpu = data.createCPU(id, name, expl);
        tdBUS bus = null;
        bus = data.getBUS(new Long(0L));
        bus.connect(id);
        Long par_13 = null;
        par_13 = bus.getId();
        cpu.connect(par_13);
    }

    @Override
	public void visitBUSdecl(IOmlBUSdecl pibd)
        throws CGException
    {
        Long id = null;
        id = pibd.getId();
        String name = null;
        name = pibd.getName();
        HashSet topo = new HashSet();
        topo = pibd.getTopo();
        tdBUS bus = null;
        bus = data.createBUS(id, name, new Boolean(true));
        Long cpuid = null;
        tdCPU cpu;
        for(Iterator enm_18 = topo.iterator(); enm_18.hasNext(); cpu.connect(id))
        {
            Long elem_10 = UTIL.NumberToLong(enm_18.next());
            cpuid = elem_10;
            cpu = null;
            cpu = data.getCPU(cpuid);
            bus.connect(cpuid);
        }

    }

    @Override
	public void visitDeployObj(IOmlDeployObj pido)
        throws CGException
    {
        Long objref = null;
        objref = pido.getObjref();
        Long cpunm = null;
        cpunm = pido.getCpunm();
        String clnm = null;
        clnm = pido.getClnm();
        Long etime = null;
        etime = pido.getObstime();
        tdObject obj = null;
        Boolean cond_8 = null;
        cond_8 = data.hasObject(objref);
        if(cond_8.booleanValue())
            obj = data.getObject(objref);
        else
            obj = data.createObject(objref, clnm);
        obj.deploy(cpunm, etime);
    }

    public void resetLastDrawn()
        throws CGException
    {
        lastLower = (HashMap)UTIL.clone(new HashMap());
        lastUpper = (HashMap)UTIL.clone(new HashMap());
    }

    public Long lastLowerTime(Long pthr)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(lastLower.containsKey(pthr));
        if(cond_2.booleanValue())
            return UTIL.NumberToLong(lastLower.get(pthr));
        else
            return new Long(0L);
    }

    public void addFailedLower(Long ptime, Long pthr, String pname)
        throws CGException
    {
        ConjectureLimit e_5 = null;
        e_5 = new ConjectureLimit(ptime, pthr, pname);
        failedLower.add(e_5);
    }

    public Boolean inFailedLower(Long ptime, Long pthr)
        throws CGException
    {
        Boolean rexpr_3 = null;
        boolean tmpQuant_4 = false;
        boolean succ_23 = true;
        HashSet e_set_24 = new HashSet();
        HashSet riseq_26 = new HashSet();
        int max_27 = failedLower.size();
        for(int i_28 = 1; i_28 <= max_27; i_28++)
            riseq_26.add(new Long(i_28));

        e_set_24 = riseq_26;
        Long i = null;
        for(Iterator enm_30 = e_set_24.iterator(); enm_30.hasNext() && !tmpQuant_4;)
        {
            Long elem_29 = UTIL.NumberToLong(enm_30.next());
            succ_23 = true;
            i = elem_29;
            if(succ_23)
            {
                Boolean pred_5 = null;
                ConjectureLimit tmpVal_7 = null;
                if(1 <= i.longValue() && i.longValue() <= failedLower.size())
                    tmpVal_7 = (ConjectureLimit)failedLower.get(i.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                Long clthr = null;
                Long cltime = null;
                boolean succ_6 = true;
                if(tmpVal_7 instanceof ConjectureLimit)
                {
                    Vector e_l_10 = new Vector();
                    e_l_10.add(tmpVal_7.obstime);
                    e_l_10.add(tmpVal_7.thrid);
                    e_l_10.add(tmpVal_7.name);
                    if(succ_6 = 3 == e_l_10.size())
                    {
                        cltime = UTIL.NumberToLong(e_l_10.get(0));
                        clthr = UTIL.NumberToLong(e_l_10.get(1));
                    }
                } else
                {
                    succ_6 = false;
                }
                if(!succ_6)
                    UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
                Boolean var1_12 = null;
                if((var1_12 = new Boolean(cltime.longValue() == ptime.longValue())).booleanValue())
                    var1_12 = new Boolean(clthr.longValue() == pthr.longValue());
                if((pred_5 = var1_12).booleanValue())
                    pred_5 = new Boolean(lastLowerTime(clthr).longValue() < cltime.longValue());
                if(pred_5.booleanValue())
                    tmpQuant_4 = true;
            }
        }

        rexpr_3 = new Boolean(tmpQuant_4);
        return rexpr_3;
    }

    public String getLowerLimitName(Long ptime, Long pthr)
        throws CGException
    {
        String res = UTIL.ConvertToString(new String());
        ConjectureLimit cl = null;
        for(Iterator enm_27 = failedLower.iterator(); enm_27.hasNext();)
        {
            ConjectureLimit elem_4 = (ConjectureLimit)enm_27.next();
            cl = elem_4;
            ConjectureLimit tmpVal_8 = null;
            tmpVal_8 = cl;
            Long clthr = null;
            String clname = null;
            Long cltime = null;
            boolean succ_7 = true;
            if(tmpVal_8 instanceof ConjectureLimit)
            {
                Vector e_l_9 = new Vector();
                e_l_9.add(tmpVal_8.obstime);
                e_l_9.add(tmpVal_8.thrid);
                e_l_9.add(tmpVal_8.name);
                if(succ_7 = 3 == e_l_9.size())
                {
                    cltime = UTIL.NumberToLong(e_l_9.get(0));
                    clthr = UTIL.NumberToLong(e_l_9.get(1));
                    clname = UTIL.ConvertToString(e_l_9.get(2));
                }
            } else
            {
                succ_7 = false;
            }
            if(!succ_7)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            Boolean cond_11 = null;
            if((cond_11 = new Boolean(cltime.longValue() == ptime.longValue())).booleanValue())
                cond_11 = new Boolean(clthr.longValue() == pthr.longValue());
            if(cond_11.booleanValue())
            {
                String rhs_18 = null;
                String var1_19 = null;
                var1_19 = res.concat(clname);
                rhs_18 = var1_19.concat(new String(" "));
                res = UTIL.ConvertToString(UTIL.clone(rhs_18));
                lastLower.put(clthr, cltime);
            }
        }

        return res;
    }

    public Long lastUpperTime(Long pthr)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(lastUpper.containsKey(pthr));
        if(cond_2.booleanValue())
            return UTIL.NumberToLong(lastUpper.get(pthr));
        else
            return new Long(0L);
    }

    public void addFailedUpper(Long ptime, Long pthr, String pname)
        throws CGException
    {
        ConjectureLimit e_5 = null;
        e_5 = new ConjectureLimit(ptime, pthr, pname);
        failedUpper.add(e_5);
    }

    public Boolean inFailedUpper(Long ptime, Long pthr)
        throws CGException
    {
        Boolean rexpr_3 = null;
        boolean tmpQuant_4 = false;
        boolean succ_23 = true;
        HashSet e_set_24 = new HashSet();
        HashSet riseq_26 = new HashSet();
        int max_27 = failedUpper.size();
        for(int i_28 = 1; i_28 <= max_27; i_28++)
            riseq_26.add(new Long(i_28));

        e_set_24 = riseq_26;
        Long i = null;
        for(Iterator enm_30 = e_set_24.iterator(); enm_30.hasNext() && !tmpQuant_4;)
        {
            Long elem_29 = UTIL.NumberToLong(enm_30.next());
            succ_23 = true;
            i = elem_29;
            if(succ_23)
            {
                Boolean pred_5 = null;
                ConjectureLimit tmpVal_7 = null;
                if(1 <= i.longValue() && i.longValue() <= failedUpper.size())
                    tmpVal_7 = (ConjectureLimit)failedUpper.get(i.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                Long clthr = null;
                Long cltime = null;
                boolean succ_6 = true;
                if(tmpVal_7 instanceof ConjectureLimit)
                {
                    Vector e_l_10 = new Vector();
                    e_l_10.add(tmpVal_7.obstime);
                    e_l_10.add(tmpVal_7.thrid);
                    e_l_10.add(tmpVal_7.name);
                    if(succ_6 = 3 == e_l_10.size())
                    {
                        cltime = UTIL.NumberToLong(e_l_10.get(0));
                        clthr = UTIL.NumberToLong(e_l_10.get(1));
                    }
                } else
                {
                    succ_6 = false;
                }
                if(!succ_6)
                    UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
                Boolean var1_12 = null;
                if((var1_12 = new Boolean(cltime.longValue() == ptime.longValue())).booleanValue())
                    var1_12 = new Boolean(clthr.longValue() == pthr.longValue());
                if((pred_5 = var1_12).booleanValue())
                    pred_5 = new Boolean(lastUpperTime(clthr).longValue() < cltime.longValue());
                if(pred_5.booleanValue())
                    tmpQuant_4 = true;
            }
        }

        rexpr_3 = new Boolean(tmpQuant_4);
        return rexpr_3;
    }

    public String getUpperLimitName(Long ptime, Long pthr)
        throws CGException
    {
        String res = UTIL.ConvertToString(new String());
        ConjectureLimit cl = null;
        for(Iterator enm_27 = failedUpper.iterator(); enm_27.hasNext();)
        {
            ConjectureLimit elem_4 = (ConjectureLimit)enm_27.next();
            cl = elem_4;
            ConjectureLimit tmpVal_8 = null;
            tmpVal_8 = cl;
            Long clthr = null;
            String clname = null;
            Long cltime = null;
            boolean succ_7 = true;
            if(tmpVal_8 instanceof ConjectureLimit)
            {
                Vector e_l_9 = new Vector();
                e_l_9.add(tmpVal_8.obstime);
                e_l_9.add(tmpVal_8.thrid);
                e_l_9.add(tmpVal_8.name);
                if(succ_7 = 3 == e_l_9.size())
                {
                    cltime = UTIL.NumberToLong(e_l_9.get(0));
                    clthr = UTIL.NumberToLong(e_l_9.get(1));
                    clname = UTIL.ConvertToString(e_l_9.get(2));
                }
            } else
            {
                succ_7 = false;
            }
            if(!succ_7)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            Boolean cond_11 = null;
            if((cond_11 = new Boolean(cltime.longValue() == ptime.longValue())).booleanValue())
                cond_11 = new Boolean(clthr.longValue() == pthr.longValue());
            if(cond_11.booleanValue())
            {
                String rhs_18 = null;
                String var1_19 = null;
                var1_19 = res.concat(clname);
                rhs_18 = var1_19.concat(new String(" "));
                res = UTIL.ConvertToString(UTIL.clone(rhs_18));
                lastUpper.put(clthr, cltime);
            }
        }

        return res;
    }

    public void checkConjectureLimits(GenericTabItem pgti, Long xpos, Long ypos, Long ptime, Long pthr)
        throws CGException
    {
        if(inFailedLower(ptime, pthr).booleanValue())
            drawFailedLower(pgti, xpos, ypos, getLowerLimitName(ptime, pthr));
        if(inFailedUpper(ptime, pthr).booleanValue())
            drawFailedUpper(pgti, xpos, ypos, getUpperLimitName(ptime, pthr));
    }

    public void drawFailedLower(GenericTabItem pgti, Long xpos, Long ypos, String pname)
        throws CGException
    {
        Ellipse ellipse = new Ellipse();
        NormalLabel nlb = null;
        org.eclipse.swt.graphics.Font arg_6 = null;
        arg_6 = pgti.getCurrentFont();
        nlb = new NormalLabel(pname, arg_6);
        Point p1 = new Point((new Long(xpos.longValue() + (new Long(1L)).longValue())).longValue(), (new Long(ypos.longValue() - (new Long(8L)).longValue())).longValue());
        Point p2 = new Point((new Long(xpos.longValue() + (new Long(2L)).longValue())).longValue(), (new Long(ypos.longValue() + (new Long(12L)).longValue())).longValue());
        ellipse.setLocation(p1);
        ellipse.setSize(16, 16);
        ellipse.setFill((new Boolean(false)).booleanValue());
        ellipse.setForegroundColor(ColorConstants.red);
        pgti.addFigure(ellipse);
        nlb.setLocation(p2);
        nlb.setForegroundColor(ColorConstants.red);
        pgti.addFigure(nlb);
    }

    public void drawFailedUpper(GenericTabItem pgti, Long xpos, Long ypos, String pname)
        throws CGException
    {
        Ellipse ellipse = new Ellipse();
        NormalLabel nlb = null;
        org.eclipse.swt.graphics.Font arg_6 = null;
        arg_6 = pgti.getCurrentFont();
        nlb = new NormalLabel(pname, arg_6);
        Point p1 = new Point((new Long(xpos.longValue() + (new Long(1L)).longValue())).longValue(), (new Long(ypos.longValue() - (new Long(8L)).longValue())).longValue());
        Point p2 = new Point((new Long(xpos.longValue() + (new Long(2L)).longValue())).longValue(), (new Long((new Long(ypos.longValue() - (new Long(20L)).longValue())).longValue() - (new Long(20L)).longValue())).longValue());
        ellipse.setLocation(p1);
        ellipse.setSize(16, 16);
        ellipse.setFill((new Boolean(false)).booleanValue());
        ellipse.setForegroundColor(ColorConstants.red);
        pgti.addFigure(ellipse);
        nlb.setLocation(p2);
        nlb.setForegroundColor(ColorConstants.red);
        pgti.addFigure(nlb);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    @SuppressWarnings("unused")
	private GenericTabItem theTabItem;
    @SuppressWarnings("unused")
	private tdCPU theCpu;
    private TraceData data;
    private Long ov_uxpos;
    private Long ov_uypos;
    private Long ov_ustarttime;
    private Long ov_ucurrenttime;
    private Vector ov_utimepos;
    private Vector failedLower;
    private HashMap lastLower;
    private Vector failedUpper;
    private HashMap lastUpper;
    private static final Long CPU_uXPOS = new Long(150L);
    private static final Long CPU_uYPOS;
    private static final Long CPU_uHEIGHT;
    private static final Long CPU_uHALFWIDTH = new Long(65L);
    private static final Long CPU_uHINTERVAL = new Long(40L);
    private static final Long BUS_uXPOS = new Long(25L);
    private static final Long BUS_uYPOS;
    private static final Long BUS_uVINTERVAL = new Long(30L);
    private static final Long RESOURCE_uVINTERVAL = new Long(50L);
    private static final Long ELEMENT_uSIZE = new Long(18L);

    static 
    {
        CPU_uYPOS = new Long(25L);
        CPU_uHEIGHT = new Long(40L);
        Long BUS_uYPOStemp = null;
        try
        {
            Long tmpVal_1 = null;
            tmpVal_1 = new Long((new Long(CPU_uYPOS.longValue() * (new Long(2L)).longValue())).longValue() + CPU_uHEIGHT.longValue());
            BUS_uYPOStemp = tmpVal_1;
        }
        catch(Throwable e)
        {
            System.out.println(e.getMessage());
        }
        BUS_uYPOS = BUS_uYPOStemp;
    }
}