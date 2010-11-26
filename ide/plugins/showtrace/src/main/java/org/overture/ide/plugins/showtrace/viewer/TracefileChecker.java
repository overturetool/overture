// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileChecker.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

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
//            TracefileMarker
@SuppressWarnings("unchecked")
public class TracefileChecker extends OmlVisitor
{

    public TracefileChecker()
        throws CGException
    {
        line = null;
        errors = null;
        severe = null;
        cputime = new HashMap();
        bustime = new HashMap();
        threads = new HashMap();
        thrcpu = new HashMap();
        curthr = new HashMap();
        opreq = new HashMap();
        opact = new HashMap();
        opfin = new HashMap();
        msgs = new HashMap();
        cpus = new HashMap();
        buses = new HashMap();
        topos = new HashMap();
        objs = new HashMap();
        try
        {
            line = new Long(1L);
            severe = new Boolean(false);
            cputime = new HashMap();
            cputime.put(new Long(0L), new Long(0L));
            bustime = new HashMap();
            bustime.put(new Long(0L), new Long(0L));
            threads = new HashMap();
            thrcpu = new HashMap();
            curthr = new HashMap();
            curthr.put(new Long(0L), null);
            opreq = new HashMap();
            opact = new HashMap();
            opfin = new HashMap();
            msgs = new HashMap();
            cpus = new HashMap();
            cpus.put(new Long(0L), new String("vCPU0"));
            buses = new HashMap();
            buses.put(new Long(0L), new String("vBUS"));
            HashSet tmpVar2_64 = new HashSet();
            tmpVar2_64 = new HashSet();
            tmpVar2_64.add(new Long(0L));
            topos = new HashMap();
            topos.put(new Long(0L), tmpVar2_64);
            objs = new HashMap();
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
    	//TODO rewritten in Java
//        String varRes_2 = null;
//        if((new Boolean(num.intValue() < (new Long(10L)))).booleanValue())
//        {
//            Character e_seq_15 = null;
//            if(1 <= (new Long(num.intValue() + (new Long(1L)))) && (new Long(num.intValue() + (new Long(1L)))) <= (new String("0123456789")).length())
//                e_seq_15 = new Character((new String("0123456789")).charAt((new Long(num.intValue() + (new Long(1L)))) - 1));
//            else
//                UTIL.RunTime("Run-Time Error:Illegal index");
//            varRes_2 = new String();
//            varRes_2 = (new StringBuilder(String.valueOf(varRes_2))).append(e_seq_15).toString();
//        } else
//        {
//            varRes_2 = nat2str(new Long(num.intValue() / (new Long(10L)))).concat(nat2str(new Long((int)(num.doubleValue() - (new Long(10L)).doubleValue() * Math.floor(num.doubleValue() / (new Long(10L)).doubleValue())))));
//        }
//        return varRes_2;
    	return num.toString();
    }

    public TracefileChecker(TracefileMarker tfmark)
        throws CGException
    {
        this();
        errors = (TracefileMarker)UTIL.clone(tfmark);
    }

    public void addError(String pstr)
        throws CGException
    {
        errors.addError(pstr, Integer.valueOf(line.intValue()));
    }

    public void addWarning(String pstr, Long pline)
        throws CGException
    {
        if(pline != null)
            errors.addWarning(pstr, Integer.valueOf(pline.intValue()));
        else
            errors.addWarning(pstr, Integer.valueOf(line.intValue()));
    }

    public Boolean hasErrors()
        throws CGException
    {
        Boolean rexpr_1 = null;
        Long var1_2 = null;
        var1_2 = errors.errorCount();
        rexpr_1 = new Boolean(var1_2 > (new Long(0L)));
        return rexpr_1;
    }

    public Boolean hasWarnings()
        throws CGException
    {
        Boolean rexpr_1 = null;
        Long var1_2 = null;
        var1_2 = errors.warningCount();
        rexpr_1 = new Boolean(var1_2 > (new Long(0L)));
        return rexpr_1;
    }

    @Override
	public void visitTraceFile(IOmlTraceFile pitf)
        throws CGException
    {
        Vector tracefile = null;
        tracefile = pitf.getTrace();
        IOmlTraceEvent event = null;
        for(Iterator enm_16 = tracefile.iterator(); enm_16.hasNext();)
        {
            IOmlTraceEvent elem_5 = (IOmlTraceEvent)enm_16.next();
            event = elem_5;
            if((new Boolean(!severe.booleanValue())).booleanValue())
                visitTraceEvent(event);
            line = UTIL.NumberToLong(UTIL.clone(new Long(line + (new Long(1L)))));
        }

        if((new Boolean(!hasErrors().booleanValue())).booleanValue())
        {
            postMessageCheck();
            postOperCheck();
        }
    }

    private Boolean checkTimeOnCpu(Long cpunm, Long ctime)
        throws CGException
    {
        if((new Boolean(UTIL.NumberToLong(cputime.get(cpunm)).longValue() <= ctime.longValue())).booleanValue())
        {
            cputime.put(cpunm, ctime);
            return new Boolean(true);
        } else
        {
            String tmpVal_9 = null;
            String var1_10 = null;
            String var1_11 = null;
            var1_11 = (new String(" seen ")).concat(nat2str(UTIL.NumberToLong(cputime.get(cpunm))));
            var1_10 = var1_11.concat(new String(" before "));
            tmpVal_9 = var1_10.concat(nat2str(ctime));
            String str = null;
            str = tmpVal_9;
            String tmpArg_v_21 = null;
            String var1_22 = null;
            var1_22 = (new String("inconsistent use of time on cpu ")).concat(nat2str(cpunm));
            tmpArg_v_21 = var1_22.concat(str);
            addError(tmpArg_v_21);
            severe = (Boolean)UTIL.clone(new Boolean(true));
            return new Boolean(false);
        }
    }

    private Boolean checkTimeOnBus(Long busid, Long ctime)
        throws CGException
    {
        if((new Boolean(UTIL.NumberToLong(bustime.get(busid)).longValue() <= ctime.longValue())).booleanValue())
        {
            bustime.put(busid, ctime);
            return new Boolean(true);
        } else
        {
            String tmpVal_9 = null;
            String var1_10 = null;
            String var1_11 = null;
            var1_11 = (new String(" seen ")).concat(nat2str(UTIL.NumberToLong(bustime.get(busid))));
            var1_10 = var1_11.concat(new String(" before "));
            tmpVal_9 = var1_10.concat(nat2str(ctime));
            String str = null;
            str = tmpVal_9;
            String tmpArg_v_21 = null;
            String var1_22 = null;
            var1_22 = (new String("inconsistent use of time on bus ")).concat(nat2str(busid));
            tmpArg_v_21 = var1_22.concat(str);
            addError(tmpArg_v_21);
            severe = (Boolean)UTIL.clone(new Boolean(true));
            return new Boolean(false);
        }
    }

    @Override
	public void visitThreadSwapIn(IOmlThreadSwapIn pthr)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = pthr.getId();
        Long thrid = null;
        thrid = tmpVal_3;
        Long tmpVal_4 = null;
        tmpVal_4 = pthr.getCpunm();
        Long cpunm = null;
        cpunm = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = pthr.getObstime();
        Long ctime = null;
        ctime = tmpVal_5;
        if((new Boolean(!hasCPU(cpunm).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasLiveThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_23 = null;
        cond_23 = pthr.hasObjref();
        if(cond_23.booleanValue())
        {
            Long objref = null;
            objref = pthr.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, cpunm).booleanValue())).booleanValue())
                return;
        }
        if((new Boolean(UTIL.equals(UTIL.NumberToLong(curthr.get(cpunm)), null))).booleanValue())
        {
            curthr.put(cpunm, thrid);
        } else
        {
            String tmpArg_v_39 = null;
            String var1_40 = null;
            String var1_41 = null;
            var1_41 = (new String("thread (id = ")).concat(nat2str(UTIL.NumberToLong(curthr.get(cpunm))));
            var1_40 = var1_41.concat(new String(") is still active on cpu "));
            tmpArg_v_39 = var1_40.concat(nat2str(cpunm));
            addError(tmpArg_v_39);
        }
    }

    private Boolean isCurrentThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        if(!pre_isCurrentThreadOnCpu(thrid, cpunm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in isCurrentThreadOnCpu");
        if((new Boolean(UTIL.equals(UTIL.NumberToLong(curthr.get(cpunm)), null))).booleanValue())
        {
            String str = new String(" (there is no active thread)");
            String tmpArg_v_35 = null;
            String var1_36 = null;
            String var1_37 = null;
            String var1_38 = null;
            var1_38 = (new String("thread ")).concat(nat2str(thrid));
            var1_37 = var1_38.concat(new String(" is not the active thread on cpu "));
            var1_36 = var1_37.concat(nat2str(cpunm));
            tmpArg_v_35 = var1_36.concat(str);
            addError(tmpArg_v_35);
            return new Boolean(false);
        }
        if((new Boolean(UTIL.NumberToLong(curthr.get(cpunm)).longValue() == thrid.longValue())).booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String str = null;
            String var1_13 = null;
            var1_13 = (new String(" (active thread = ")).concat(nat2str(UTIL.NumberToLong(curthr.get(cpunm))));
            str = var1_13.concat(new String(")"));
            String tmpArg_v_21 = null;
            String var1_22 = null;
            String var1_23 = null;
            String var1_24 = null;
            var1_24 = (new String("thread ")).concat(nat2str(thrid));
            var1_23 = var1_24.concat(new String(" is not the active thread on cpu "));
            var1_22 = var1_23.concat(nat2str(cpunm));
            tmpArg_v_21 = var1_22.concat(str);
            addError(tmpArg_v_21);
            return new Boolean(false);
        }
    }

    private Boolean pre_isCurrentThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        Boolean varRes_3 = null;
        Boolean var1_4 = null;
        Boolean var1_5 = null;
        Boolean var1_6 = null;
        var1_6 = new Boolean(threads.containsKey(thrid));
        if((var1_5 = var1_6).booleanValue())
        {
            Boolean var2_9 = null;
            var2_9 = new Boolean(thrcpu.containsKey(thrid));
            var1_5 = var2_9;
        }
        if((var1_4 = var1_5).booleanValue())
        {
            Boolean var2_12 = null;
            var2_12 = new Boolean(cpus.containsKey(cpunm));
            var1_4 = var2_12;
        }
        if((varRes_3 = var1_4).booleanValue())
        {
            Boolean var2_15 = null;
            var2_15 = new Boolean(curthr.containsKey(cpunm));
            varRes_3 = var2_15;
        }
        return varRes_3;
    }

    private Boolean isNotCurrentThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        if(!pre_isNotCurrentThreadOnCpu(thrid, cpunm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in isNotCurrentThreadOnCpu");
        if((new Boolean(UTIL.equals(UTIL.NumberToLong(curthr.get(cpunm)), null))).booleanValue())
            return new Boolean(true);
        if((new Boolean(UTIL.NumberToLong(curthr.get(cpunm)).longValue() == thrid.longValue())).booleanValue())
        {
            String tmpArg_v_15 = null;
            String var1_16 = null;
            String var1_17 = null;
            var1_17 = (new String("thread ")).concat(nat2str(thrid));
            var1_16 = var1_17.concat(new String(" is still active cpu "));
            tmpArg_v_15 = var1_16.concat(nat2str(cpunm));
            addError(tmpArg_v_15);
            return new Boolean(false);
        } else
        {
            return new Boolean(true);
        }
    }

    private Boolean pre_isNotCurrentThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        Boolean varRes_3 = null;
        Boolean var1_4 = null;
        Boolean var1_5 = null;
        Boolean var1_6 = null;
        var1_6 = new Boolean(threads.containsKey(thrid));
        if((var1_5 = var1_6).booleanValue())
        {
            Boolean var2_9 = null;
            var2_9 = new Boolean(thrcpu.containsKey(thrid));
            var1_5 = var2_9;
        }
        if((var1_4 = var1_5).booleanValue())
        {
            Boolean var2_12 = null;
            var2_12 = new Boolean(cpus.containsKey(cpunm));
            var1_4 = var2_12;
        }
        if((varRes_3 = var1_4).booleanValue())
        {
            Boolean var2_15 = null;
            var2_15 = new Boolean(curthr.containsKey(cpunm));
            varRes_3 = var2_15;
        }
        return varRes_3;
    }

    @Override
	public void visitDelayedThreadSwapIn(IOmlDelayedThreadSwapIn pthr)
        throws CGException
    {
        Long thrid = null;
        thrid = pthr.getId();
        Long cpunm = null;
        cpunm = pthr.getCpunm();
        Long tmpVal_5 = null;
        tmpVal_5 = pthr.getObstime();
        Long ctime = null;
        ctime = tmpVal_5;
        if((new Boolean(!hasCPU(cpunm).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasLiveThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_23 = null;
        cond_23 = pthr.hasObjref();
        if(cond_23.booleanValue())
        {
            Long objref = null;
            objref = pthr.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, cpunm).booleanValue())).booleanValue())
                return;
        }
        if((new Boolean(UTIL.equals(UTIL.NumberToLong(curthr.get(cpunm)), null))).booleanValue())
        {
            curthr.put(cpunm, thrid);
        } else
        {
            String tmpArg_v_39 = null;
            String var1_40 = null;
            String var1_41 = null;
            var1_41 = (new String("thread (id = ")).concat(nat2str(UTIL.NumberToLong(curthr.get(cpunm))));
            var1_40 = var1_41.concat(new String(") is still active on cpu "));
            tmpArg_v_39 = var1_40.concat(nat2str(cpunm));
            addError(tmpArg_v_39);
        }
    }

    @Override
	public void visitThreadSwapOut(IOmlThreadSwapOut pthr)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = pthr.getId();
        Long thrid = null;
        thrid = tmpVal_3;
        Long tmpVal_4 = null;
        tmpVal_4 = pthr.getCpunm();
        Long cpunm = null;
        cpunm = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = pthr.getObstime();
        Long ctime = null;
        ctime = tmpVal_5;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_14 = null;
        cond_14 = pthr.hasObjref();
        if(cond_14.booleanValue())
        {
            Long objref = null;
            objref = pthr.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, cpunm).booleanValue())).booleanValue())
                return;
        }
        curthr.put(cpunm, null);
    }

    private Boolean checkThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        if((new Boolean(!hasCPU(cpunm).booleanValue())).booleanValue())
            return new Boolean(false);
        if((new Boolean(!hasThread(thrid).booleanValue())).booleanValue())
            return new Boolean(false);
        if((new Boolean(!hasLiveThread(thrid).booleanValue())).booleanValue())
            return new Boolean(false);
        if((new Boolean(!hasThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return new Boolean(false);
        else
            return isCurrentThreadOnCpu(thrid, cpunm);
    }

    @Override
	public void visitThreadCreate(IOmlThreadCreate pthr)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = pthr.getId();
        Long thrid = null;
        thrid = tmpVal_3;
        Long tmpVal_4 = null;
        tmpVal_4 = pthr.getCpunm();
        Long cpunm = null;
        cpunm = tmpVal_4;
        Long tmpVal_5 = null;
        tmpVal_5 = pthr.getObstime();
        Long ctime = null;
        ctime = tmpVal_5;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        Boolean cond_10 = null;
        cond_10 = new Boolean(threads.containsKey(thrid));
        if(cond_10.booleanValue())
        {
            String tmpArg_v_25 = null;
            String var1_26 = null;
            var1_26 = (new String("thread (id = ")).concat(nat2str(thrid));
            tmpArg_v_25 = var1_26.concat(new String(") is not unique"));
            addError(tmpArg_v_25);
        } else
        {
            HashMap rhs_13 = new HashMap();
            HashMap var2_15 = new HashMap();
            var2_15 = new HashMap();
            var2_15.put(thrid, new Boolean(true));
            HashMap m1_22 = (HashMap)threads.clone();
            HashMap m2_23 = var2_15;
            HashSet com_18 = new HashSet();
            com_18.addAll((Collection)m1_22.keySet());
            com_18.retainAll((Collection)m2_23.keySet());
            boolean all_applies_19 = true;
            Object d_20;
            for(Iterator bb_21 = com_18.iterator(); bb_21.hasNext() && all_applies_19; all_applies_19 = m1_22.get(d_20).equals(m2_23.get(d_20)))
                d_20 = bb_21.next();

            if(!all_applies_19)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_22.putAll(m2_23);
            rhs_13 = m1_22;
            threads = (HashMap)UTIL.clone(rhs_13);
        }
        if(hasCPU(cpunm).booleanValue())
        {
            HashMap rhs_33 = new HashMap();
            HashMap var2_35 = new HashMap();
            var2_35 = new HashMap();
            var2_35.put(thrid, cpunm);
            HashMap m1_42 = (HashMap)thrcpu.clone();
            HashMap m2_43 = var2_35;
            HashSet com_38 = new HashSet();
            com_38.addAll((Collection)m1_42.keySet());
            com_38.retainAll((Collection)m2_43.keySet());
            boolean all_applies_39 = true;
            Object d_40;
            for(Iterator bb_41 = com_38.iterator(); bb_41.hasNext() && all_applies_39; all_applies_39 = m1_42.get(d_40).equals(m2_43.get(d_40)))
                d_40 = bb_41.next();

            if(!all_applies_39)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_42.putAll(m2_43);
            rhs_33 = m1_42;
            thrcpu = (HashMap)UTIL.clone(rhs_33);
        }
    }

    public Boolean hasThread(Long pid)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(threads.containsKey(pid));
        if(cond_2.booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_6 = null;
            String var1_7 = null;
            var1_7 = (new String("thread (id = ")).concat(nat2str(pid));
            tmpArg_v_6 = var1_7.concat(new String(") does not exist"));
            addError(tmpArg_v_6);
            return new Boolean(false);
        }
    }

    public Boolean hasThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        if(!pre_hasThreadOnCpu(thrid, cpunm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in hasThreadOnCpu");
        if((new Boolean(UTIL.NumberToLong(thrcpu.get(thrid)).longValue() == cpunm.longValue())).booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_9 = null;
            String var1_10 = null;
            String var1_11 = null;
            String var1_12 = null;
            String var1_13 = null;
            var1_13 = (new String("thread (id = ")).concat(nat2str(thrid));
            var1_12 = var1_13.concat(new String(") is deployed on cpu "));
            var1_11 = var1_12.concat(nat2str(UTIL.NumberToLong(thrcpu.get(thrid))));
            var1_10 = var1_11.concat(new String(" in stead of cpu "));
            tmpArg_v_9 = var1_10.concat(nat2str(cpunm));
            addError(tmpArg_v_9);
            return new Boolean(false);
        }
    }

    public Boolean pre_hasThreadOnCpu(Long thrid, Long cpunm)
        throws CGException
    {
        Boolean varRes_3 = null;
        Boolean var1_4 = null;
        Boolean var1_5 = null;
        var1_5 = new Boolean(threads.containsKey(thrid));
        if((var1_4 = var1_5).booleanValue())
        {
            Boolean var2_8 = null;
            var2_8 = new Boolean(thrcpu.containsKey(thrid));
            var1_4 = var2_8;
        }
        if((varRes_3 = var1_4).booleanValue())
        {
            Boolean var2_11 = null;
            var2_11 = new Boolean(cpus.containsKey(cpunm));
            varRes_3 = var2_11;
        }
        return varRes_3;
    }

    public Boolean hasLiveThread(Long pid)
        throws CGException
    {
        if(!pre_hasLiveThread(pid).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in hasLiveThread");
        if(((Boolean)threads.get(pid)).booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_6 = null;
            String var1_7 = null;
            var1_7 = (new String("thread (id = ")).concat(nat2str(pid));
            tmpArg_v_6 = var1_7.concat(new String(") is already dead"));
            addError(tmpArg_v_6);
            return new Boolean(false);
        }
    }

    public Boolean pre_hasLiveThread(Long pid)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(threads.containsKey(pid));
        return varRes_2;
    }

    @Override
	public void visitThreadKill(IOmlThreadKill pthr)
        throws CGException
    {
        Long thrid = null;
        thrid = pthr.getId();
        Long cpunm = null;
        cpunm = pthr.getCpunm();
        Long tmpVal_5 = null;
        tmpVal_5 = pthr.getObstime();
        Long ctime = null;
        ctime = tmpVal_5;
        if((new Boolean(!hasCPU(cpunm).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasLiveThread(thrid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        if((new Boolean(!isNotCurrentThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
        {
            return;
        } else
        {
            threads.put(thrid, new Boolean(false));
            return;
        }
    }

    private void postOperCheck()
        throws CGException
    {
        HashSet iset_1 = new HashSet();
        iset_1.clear();
        iset_1.addAll((Collection)opreq.keySet());
        String opname = null;
        for(Iterator enm_31 = iset_1.iterator(); enm_31.hasNext();)
        {
            String elem_2 = UTIL.ConvertToString(enm_31.next());
            opname = elem_2;
            if((new Boolean(hasRequest(opname) > hasActivate(opname))).booleanValue())
            {
                String tmpArg_v_25 = null;
                String var1_26 = null;
                var1_26 = (new String("Operation \"")).concat(opname);
                tmpArg_v_25 = var1_26.concat(new String("\" is still pending"));
                addWarning(tmpArg_v_25, null);
            } else
            if((new Boolean(hasActivate(opname) > hasCompleted(opname))).booleanValue())
            {
                String tmpArg_v_18 = null;
                String var1_19 = null;
                var1_19 = (new String("Operation \"")).concat(opname);
                tmpArg_v_18 = var1_19.concat(new String("\" is still active"));
                addWarning(tmpArg_v_18, null);
            }
        }

    }

    @Override
	public void visitOpRequest(IOmlOpRequest por)
        throws CGException
    {
        Long thrid = null;
        thrid = por.getId();
        String opname = null;
        opname = por.getOpname();
        Long cpunm = null;
        cpunm = por.getCpunm();
        Long tmpVal_6 = null;
        tmpVal_6 = por.getObstime();
        Long ctime = null;
        ctime = tmpVal_6;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_15 = null;
        cond_15 = por.hasObjref();
        if(cond_15.booleanValue())
        {
            Long objref = null;
            objref = por.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
        }
        Boolean cond_21 = null;
        cond_21 = new Boolean(opreq.containsKey(opname));
        if(cond_21.booleanValue())
        {
            Long rcnt = new Long(UTIL.NumberToLong(opreq.get(opname)).longValue() + (new Long(1L)).longValue());
            opreq.put(opname, rcnt);
        } else
        {
            HashMap rhs_24 = new HashMap();
            HashMap var2_26 = new HashMap();
            var2_26 = new HashMap();
            var2_26.put(opname, new Long(1L));
            HashMap m1_33 = (HashMap)opreq.clone();
            HashMap m2_34 = var2_26;
            HashSet com_29 = new HashSet();
            com_29.addAll((Collection)m1_33.keySet());
            com_29.retainAll((Collection)m2_34.keySet());
            boolean all_applies_30 = true;
            Object d_31;
            for(Iterator bb_32 = com_29.iterator(); bb_32.hasNext() && all_applies_30; all_applies_30 = m1_33.get(d_31).equals(m2_34.get(d_31)))
                d_31 = bb_32.next();

            if(!all_applies_30)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_33.putAll(m2_34);
            rhs_24 = m1_33;
            opreq = (HashMap)UTIL.clone(rhs_24);
        }
    }

    private Long hasRequest(String pnm)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(opreq.containsKey(pnm));
        if(cond_2.booleanValue())
            return UTIL.NumberToLong(opreq.get(pnm));
        else
            return new Long(0L);
    }

    @Override
	public void visitOpActivate(IOmlOpActivate poa)
        throws CGException
    {
        Long thrid = null;
        thrid = poa.getId();
        String opname = null;
        opname = poa.getOpname();
        Long cpunm = null;
        cpunm = poa.getCpunm();
        Long tmpVal_6 = null;
        tmpVal_6 = poa.getObstime();
        Long ctime = null;
        ctime = tmpVal_6;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_15 = null;
        cond_15 = poa.hasObjref();
        if(cond_15.booleanValue())
        {
            Long objref = null;
            objref = poa.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, cpunm).booleanValue())).booleanValue())
                return;
        }
        if((new Boolean(hasActivate(opname) >= hasRequest(opname))).booleanValue())
        {
            String tmpArg_v_31 = null;
            String var1_32 = null;
            var1_32 = (new String("Operation \"")).concat(opname);
            tmpArg_v_31 = var1_32.concat(new String("\" is activated more than requested"));
            addError(tmpArg_v_31);
            return;
        }
        Boolean cond_36 = null;
        cond_36 = new Boolean(opact.containsKey(opname));
        if(cond_36.booleanValue())
        {
            Long ract = new Long(UTIL.NumberToLong(opact.get(opname)).longValue() + (new Long(1L)).longValue());
            opact.put(opname, ract);
        } else
        {
            HashMap rhs_39 = new HashMap();
            HashMap var2_41 = new HashMap();
            var2_41 = new HashMap();
            var2_41.put(opname, new Long(1L));
            HashMap m1_48 = (HashMap)opact.clone();
            HashMap m2_49 = var2_41;
            HashSet com_44 = new HashSet();
            com_44.addAll((Collection)m1_48.keySet());
            com_44.retainAll((Collection)m2_49.keySet());
            boolean all_applies_45 = true;
            Object d_46;
            for(Iterator bb_47 = com_44.iterator(); bb_47.hasNext() && all_applies_45; all_applies_45 = m1_48.get(d_46).equals(m2_49.get(d_46)))
                d_46 = bb_47.next();

            if(!all_applies_45)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_48.putAll(m2_49);
            rhs_39 = m1_48;
            opact = (HashMap)UTIL.clone(rhs_39);
        }
    }

    private Long hasActivate(String pnm)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(opact.containsKey(pnm));
        if(cond_2.booleanValue())
            return UTIL.NumberToLong(opact.get(pnm));
        else
            return new Long(0L);
    }

    @Override
	public void visitOpCompleted(IOmlOpCompleted poc)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = poc.getId();
        Long thrid = null;
        thrid = tmpVal_3;
        String opname = null;
        opname = poc.getOpname();
        Long tmpVal_5 = null;
        tmpVal_5 = poc.getCpunm();
        Long cpunm = null;
        cpunm = tmpVal_5;
        Long tmpVal_6 = null;
        tmpVal_6 = poc.getObstime();
        Long ctime = null;
        ctime = tmpVal_6;
        if((new Boolean(!checkTimeOnCpu(cpunm, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkThreadOnCpu(thrid, cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_15 = null;
        cond_15 = poc.hasObjref();
        if(cond_15.booleanValue())
        {
            Long objref = null;
            objref = poc.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, cpunm).booleanValue())).booleanValue())
                return;
        }
        if((new Boolean(hasCompleted(opname) >= hasActivate(opname))).booleanValue())
        {
            String tmpArg_v_31 = null;
            String var1_32 = null;
            var1_32 = (new String("Operation \"")).concat(opname);
            tmpArg_v_31 = var1_32.concat(new String("\" is completed more than activated"));
            addError(tmpArg_v_31);
            return;
        }
        Boolean cond_36 = null;
        cond_36 = new Boolean(opfin.containsKey(opname));
        if(cond_36.booleanValue())
        {
            Long rfin = UTIL.NumberToLong(opfin.get(opname));
            opfin.put(opname, rfin);
        } else
        {
            HashMap rhs_39 = new HashMap();
            HashMap var2_41 = new HashMap();
            var2_41 = new HashMap();
            var2_41.put(opname, new Long(1L));
            HashMap m1_48 = (HashMap)opfin.clone();
            HashMap m2_49 = var2_41;
            HashSet com_44 = new HashSet();
            com_44.addAll((Collection)m1_48.keySet());
            com_44.retainAll((Collection)m2_49.keySet());
            boolean all_applies_45 = true;
            Object d_46;
            for(Iterator bb_47 = com_44.iterator(); bb_47.hasNext() && all_applies_45; all_applies_45 = m1_48.get(d_46).equals(m2_49.get(d_46)))
                d_46 = bb_47.next();

            if(!all_applies_45)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_48.putAll(m2_49);
            rhs_39 = m1_48;
            opfin = (HashMap)UTIL.clone(rhs_39);
        }
    }

    private Long hasCompleted(String pnm)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(opfin.containsKey(pnm));
        if(cond_2.booleanValue())
            return UTIL.NumberToLong(opfin.get(pnm));
        else
            return new Long(0L);
    }

    private void postMessageCheck()
        throws CGException
    {
        HashSet iset_1 = new HashSet();
        iset_1.clear();
        iset_1.addAll((Collection)msgs.keySet());
        Long msg = null;
        for(Iterator enm_33 = iset_1.iterator(); enm_33.hasNext();)
        {
            Long elem_2 = UTIL.NumberToLong(enm_33.next());
            msg = elem_2;
            if((new Boolean(UTIL.NumberToLong(msgs.get(msg)).longValue() == (new Long(1L)).longValue())).booleanValue())
            {
                String tmpArg_v_26 = null;
                String var1_27 = null;
                var1_27 = (new String("Message (id = ")).concat(nat2str(msg));
                tmpArg_v_26 = var1_27.concat(new String(") is still pending"));
                addWarning(tmpArg_v_26, null);
            } else
            if((new Boolean(UTIL.NumberToLong(msgs.get(msg)).longValue() == (new Long(2L)).longValue())).booleanValue())
            {
                String tmpArg_v_18 = null;
                String var1_19 = null;
                var1_19 = (new String("Message (id = ")).concat(nat2str(msg));
                tmpArg_v_18 = var1_19.concat(new String(") is still in transit"));
                addWarning(tmpArg_v_18, null);
            }
        }

    }

    @Override
	public void visitMessageRequest(IOmlMessageRequest pmr)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = pmr.getBusid();
        Long busid = null;
        busid = tmpVal_3;
        Long msgid = null;
        msgid = pmr.getMsgid();
        Long frcpu = null;
        frcpu = pmr.getFromcpu();
        Long tocpu = null;
        tocpu = pmr.getTocpu();
        Long callthr = null;
        callthr = pmr.getCallthr();
        Long tmpVal_8 = null;
        tmpVal_8 = pmr.getObstime();
        Long ctime = null;
        ctime = tmpVal_8;
        if((new Boolean(!hasBUS(busid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkTimeOnBus(busid, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasCPU(frcpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasCPU(tocpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!connectsCpus(busid, frcpu, tocpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(callthr, frcpu).booleanValue())).booleanValue())
            return;
        Boolean cond_31 = null;
        cond_31 = pmr.hasObjref();
        if(cond_31.booleanValue())
        {
            Long objref = null;
            objref = pmr.getObjref();
            if((new Boolean(!hasObject(objref).booleanValue())).booleanValue())
                return;
            if((new Boolean(!hasObjectOnCpu(objref, tocpu).booleanValue())).booleanValue())
                return;
        }
        Boolean cond_41 = null;
        cond_41 = new Boolean(msgs.containsKey(msgid));
        if(cond_41.booleanValue())
        {
            String tmpArg_v_56 = null;
            String var1_57 = null;
            var1_57 = (new String("message (id = ")).concat(nat2str(msgid));
            tmpArg_v_56 = var1_57.concat(new String(") is not unique"));
            addError(tmpArg_v_56);
        } else
        {
            HashMap rhs_44 = new HashMap();
            HashMap var2_46 = new HashMap();
            var2_46 = new HashMap();
            var2_46.put(msgid, new Long(1L));
            HashMap m1_53 = (HashMap)msgs.clone();
            HashMap m2_54 = var2_46;
            HashSet com_49 = new HashSet();
            com_49.addAll((Collection)m1_53.keySet());
            com_49.retainAll((Collection)m2_54.keySet());
            boolean all_applies_50 = true;
            Object d_51;
            for(Iterator bb_52 = com_49.iterator(); bb_52.hasNext() && all_applies_50; all_applies_50 = m1_53.get(d_51).equals(m2_54.get(d_51)))
                d_51 = bb_52.next();

            if(!all_applies_50)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_53.putAll(m2_54);
            rhs_44 = m1_53;
            msgs = (HashMap)UTIL.clone(rhs_44);
        }
    }

    @Override
	public void visitReplyRequest(IOmlReplyRequest prr)
        throws CGException
    {
        Long tmpVal_3 = null;
        tmpVal_3 = prr.getBusid();
        Long busid = null;
        busid = tmpVal_3;
        Long msgid = null;
        msgid = prr.getMsgid();
        Long origmsgid = null;
        origmsgid = prr.getOrigmsgid();
        Long frcpu = null;
        frcpu = prr.getFromcpu();
        Long tocpu = null;
        tocpu = prr.getTocpu();
        Long callthr = null;
        callthr = prr.getCallthr();
        Long calleethr = null;
        calleethr = prr.getCalleethr();
        Long tmpVal_10 = null;
        tmpVal_10 = prr.getObstime();
        Long ctime = null;
        ctime = tmpVal_10;
        if((new Boolean(!hasBUS(busid).booleanValue())).booleanValue())
            return;
        if((new Boolean(!checkTimeOnBus(busid, ctime).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasCPU(frcpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasCPU(tocpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!connectsCpus(busid, frcpu, tocpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(callthr, tocpu).booleanValue())).booleanValue())
            return;
        if((new Boolean(!hasThreadOnCpu(calleethr, frcpu).booleanValue())).booleanValue())
            return;
        Boolean cond_37 = null;
        HashSet var2_39 = new HashSet();
        var2_39.clear();
        var2_39.addAll((Collection)msgs.keySet());
        cond_37 = new Boolean(!var2_39.contains(origmsgid));
        if(cond_37.booleanValue())
        {
            String tmpArg_v_42 = null;
            String var1_43 = null;
            var1_43 = (new String("message (origmsgid = ")).concat(nat2str(origmsgid));
            tmpArg_v_42 = var1_43.concat(new String(") does not exist"));
            addError(tmpArg_v_42);
        }
        Boolean cond_48 = null;
        cond_48 = new Boolean(msgs.containsKey(msgid));
        if(cond_48.booleanValue())
        {
            String tmpArg_v_63 = null;
            String var1_64 = null;
            var1_64 = (new String("message (id = ")).concat(nat2str(msgid));
            tmpArg_v_63 = var1_64.concat(new String(") is not unique"));
            addError(tmpArg_v_63);
        } else
        {
            HashMap rhs_51 = new HashMap();
            HashMap var2_53 = new HashMap();
            var2_53 = new HashMap();
            var2_53.put(msgid, new Long(1L));
            HashMap m1_60 = (HashMap)msgs.clone();
            HashMap m2_61 = var2_53;
            HashSet com_56 = new HashSet();
            com_56.addAll((Collection)m1_60.keySet());
            com_56.retainAll((Collection)m2_61.keySet());
            boolean all_applies_57 = true;
            Object d_58;
            for(Iterator bb_59 = com_56.iterator(); bb_59.hasNext() && all_applies_57; all_applies_57 = m1_60.get(d_58).equals(m2_61.get(d_58)))
                d_58 = bb_59.next();

            if(!all_applies_57)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_60.putAll(m2_61);
            rhs_51 = m1_60;
            msgs = (HashMap)UTIL.clone(rhs_51);
        }
    }

    @Override
	public void visitMessageActivate(IOmlMessageActivate pma)
        throws CGException
    {
        Long msgid = null;
        msgid = pma.getMsgid();
        Boolean cond_4 = null;
        cond_4 = new Boolean(msgs.containsKey(msgid));
        if(cond_4.booleanValue())
        {
            if((new Boolean(UTIL.NumberToLong(msgs.get(msgid)).longValue() == (new Long(1L)).longValue())).booleanValue())
            {
                msgs.put(msgid, new Long(2L));
            } else
            {
                String tmpArg_v_20 = null;
                String var1_21 = null;
                var1_21 = (new String("message (id = ")).concat(nat2str(msgid));
                tmpArg_v_20 = var1_21.concat(new String(") can not be activated"));
                addError(tmpArg_v_20);
            }
        } else
        {
            String tmpArg_v_8 = null;
            String var1_9 = null;
            var1_9 = (new String("message (id = ")).concat(nat2str(msgid));
            tmpArg_v_8 = var1_9.concat(new String(") is unknown"));
            addError(tmpArg_v_8);
        }
    }

    @Override
	public void visitMessageCompleted(IOmlMessageCompleted pmc)
        throws CGException
    {
        Long msgid = null;
        msgid = pmc.getMsgid();
        Boolean cond_4 = null;
        cond_4 = new Boolean(msgs.containsKey(msgid));
        if(cond_4.booleanValue())
        {
            if((new Boolean(UTIL.NumberToLong(msgs.get(msgid)).longValue() == (new Long(2L)).longValue())).booleanValue())
            {
                msgs.put(msgid, new Long(3L));
            } else
            {
                String tmpArg_v_20 = null;
                String var1_21 = null;
                var1_21 = (new String("message (id = ")).concat(nat2str(msgid));
                tmpArg_v_20 = var1_21.concat(new String(") can not be completed"));
                addError(tmpArg_v_20);
            }
        } else
        {
            String tmpArg_v_8 = null;
            String var1_9 = null;
            var1_9 = (new String("message (id = ")).concat(nat2str(msgid));
            tmpArg_v_8 = var1_9.concat(new String(") is unknown"));
            addError(tmpArg_v_8);
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
        Boolean cond_5 = null;
        cond_5 = new Boolean(cpus.containsKey(id));
        if(cond_5.booleanValue())
        {
            String tmpArg_v_9 = null;
            String var1_10 = null;
            var1_10 = (new String("cpu (id = ")).concat(nat2str(id));
            tmpArg_v_9 = var1_10.concat(new String(") is not unique"));
            addError(tmpArg_v_9);
            return;
        }
        Boolean cond_15 = null;
        cond_15 = new Boolean(cpus.containsValue(name));
        if(cond_15.booleanValue())
        {
            String tmpArg_v_19 = null;
            String var1_20 = null;
            var1_20 = (new String("cpu (name = \"")).concat(name);
            tmpArg_v_19 = var1_20.concat(new String("\" is not unique"));
            addError(tmpArg_v_19);
            return;
        }
        HashMap rhs_24 = new HashMap();
        HashMap var2_26 = new HashMap();
        var2_26 = new HashMap();
        var2_26.put(id, name);
        HashMap m1_33 = (HashMap)cpus.clone();
        HashMap m2_34 = var2_26;
        HashSet com_29 = new HashSet();
        com_29.addAll((Collection)m1_33.keySet());
        com_29.retainAll((Collection)m2_34.keySet());
        boolean all_applies_30 = true;
        Object d_31;
        for(Iterator bb_32 = com_29.iterator(); bb_32.hasNext() && all_applies_30; all_applies_30 = m1_33.get(d_31).equals(m2_34.get(d_31)))
            d_31 = bb_32.next();

        if(!all_applies_30)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_33.putAll(m2_34);
        rhs_24 = m1_33;
        cpus = (HashMap)UTIL.clone(rhs_24);
        HashMap rhs_35 = new HashMap();
        HashMap var2_37 = new HashMap();
        var2_37 = new HashMap();
        var2_37.put(id, new Long(0L));
        HashMap m1_44 = (HashMap)cputime.clone();
        HashMap m2_45 = var2_37;
        HashSet com_40 = new HashSet();
        com_40.addAll((Collection)m1_44.keySet());
        com_40.retainAll((Collection)m2_45.keySet());
        boolean all_applies_41 = true;
        Object d_42;
        for(Iterator bb_43 = com_40.iterator(); bb_43.hasNext() && all_applies_41; all_applies_41 = m1_44.get(d_42).equals(m2_45.get(d_42)))
            d_42 = bb_43.next();

        if(!all_applies_41)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_44.putAll(m2_45);
        rhs_35 = m1_44;
        cputime = (HashMap)UTIL.clone(rhs_35);
        HashSet mr_48 = new HashSet();
        HashSet var2_52 = new HashSet();
        var2_52 = new HashSet();
        var2_52.add(id);
        mr_48 = (HashSet)((HashSet)(HashSet)topos.get(new Long(0L))).clone();
        mr_48.addAll(var2_52);
        topos.put(new Long(0L), mr_48);
    }

    private Boolean hasCPU(Long pid)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(cpus.containsKey(pid));
        if(cond_2.booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_6 = null;
            String var1_7 = null;
            var1_7 = (new String("CPU (id = ")).concat(nat2str(pid));
            tmpArg_v_6 = var1_7.concat(new String(") is not known"));
            addError(tmpArg_v_6);
            return new Boolean(false);
        }
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
        Boolean cond_6 = null;
        cond_6 = new Boolean(buses.containsKey(id));
        if(cond_6.booleanValue())
        {
            String tmpArg_v_10 = null;
            String var1_11 = null;
            var1_11 = (new String("bus (id = ")).concat(nat2str(id));
            tmpArg_v_10 = var1_11.concat(new String(") is not unique"));
            addError(tmpArg_v_10);
            return;
        }
        Boolean cond_16 = null;
        cond_16 = new Boolean(buses.containsValue(name));
        if(cond_16.booleanValue())
        {
            String tmpArg_v_20 = null;
            String var1_21 = null;
            var1_21 = (new String("bus (name = \"")).concat(name);
            tmpArg_v_20 = var1_21.concat(new String("\" is not unique"));
            addError(tmpArg_v_20);
            return;
        }
        HashMap rhs_25 = new HashMap();
        HashMap var2_27 = new HashMap();
        var2_27 = new HashMap();
        var2_27.put(id, name);
        HashMap m1_34 = (HashMap)buses.clone();
        HashMap m2_35 = var2_27;
        HashSet com_30 = new HashSet();
        com_30.addAll((Collection)m1_34.keySet());
        com_30.retainAll((Collection)m2_35.keySet());
        boolean all_applies_31 = true;
        Object d_32;
        for(Iterator bb_33 = com_30.iterator(); bb_33.hasNext() && all_applies_31; all_applies_31 = m1_34.get(d_32).equals(m2_35.get(d_32)))
            d_32 = bb_33.next();

        if(!all_applies_31)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_34.putAll(m2_35);
        rhs_25 = m1_34;
        buses = (HashMap)UTIL.clone(rhs_25);
        HashMap rhs_36 = new HashMap();
        HashMap var2_38 = new HashMap();
        var2_38 = new HashMap();
        var2_38.put(id, new Long(0L));
        HashMap m1_45 = (HashMap)bustime.clone();
        HashMap m2_46 = var2_38;
        HashSet com_41 = new HashSet();
        com_41.addAll((Collection)m1_45.keySet());
        com_41.retainAll((Collection)m2_46.keySet());
        boolean all_applies_42 = true;
        Object d_43;
        for(Iterator bb_44 = com_41.iterator(); bb_44.hasNext() && all_applies_42; all_applies_42 = m1_45.get(d_43).equals(m2_46.get(d_43)))
            d_43 = bb_44.next();

        if(!all_applies_42)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_45.putAll(m2_46);
        rhs_36 = m1_45;
        bustime = (HashMap)UTIL.clone(rhs_36);
        HashMap rhs_47 = new HashMap();
        HashMap var2_49 = new HashMap();
        var2_49 = new HashMap();
        var2_49.put(id, topo);
        HashMap m1_56 = (HashMap)topos.clone();
        HashMap m2_57 = var2_49;
        HashSet com_52 = new HashSet();
        com_52.addAll((Collection)m1_56.keySet());
        com_52.retainAll((Collection)m2_57.keySet());
        boolean all_applies_53 = true;
        Object d_54;
        for(Iterator bb_55 = com_52.iterator(); bb_55.hasNext() && all_applies_53; all_applies_53 = m1_56.get(d_54).equals(m2_57.get(d_54)))
            d_54 = bb_55.next();

        if(!all_applies_53)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_56.putAll(m2_57);
        rhs_47 = m1_56;
        topos = (HashMap)UTIL.clone(rhs_47);
    }

    private Boolean hasBUS(Long pid)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(buses.containsKey(pid));
        if(cond_2.booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_6 = null;
            String var1_7 = null;
            var1_7 = (new String("BUS (id = ")).concat(nat2str(pid));
            tmpArg_v_6 = var1_7.concat(new String(") is not known"));
            addError(tmpArg_v_6);
            return new Boolean(false);
        }
    }

    private Boolean connectsCpus(Long pbus, Long pcpu1, Long pcpu2)
        throws CGException
    {
        Boolean res = new Boolean(true);
        Boolean cond_4 = null;
        cond_4 = new Boolean(!((HashSet)(HashSet)topos.get(pbus)).contains(pcpu1));
        if(cond_4.booleanValue())
        {
            String tmpArg_v_10 = null;
            String var1_11 = null;
            String var1_12 = null;
            var1_12 = (new String("CPU (id = ")).concat(nat2str(pcpu1));
            var1_11 = var1_12.concat(new String(") is not connected to BUS "));
            tmpArg_v_10 = var1_11.concat(nat2str(pbus));
            addError(tmpArg_v_10);
            res = (Boolean)UTIL.clone(new Boolean(false));
        }
        Boolean cond_20 = null;
        cond_20 = new Boolean(!((HashSet)(HashSet)topos.get(pbus)).contains(pcpu2));
        if(cond_20.booleanValue())
        {
            String tmpArg_v_26 = null;
            String var1_27 = null;
            String var1_28 = null;
            var1_28 = (new String("CPU (id = ")).concat(nat2str(pcpu2));
            var1_27 = var1_28.concat(new String(") is not connected to BUS "));
            tmpArg_v_26 = var1_27.concat(nat2str(pbus));
            addError(tmpArg_v_26);
            res = (Boolean)UTIL.clone(new Boolean(false));
        }
        return res;
    }

    @Override
	public void visitDeployObj(IOmlDeployObj pido)
        throws CGException
    {
    	//System.out.println("visitDeployObj: DeployObj -> objref: "+pido.getObjref()+" clnm: "+pido.getClnm()+" cpunm: "+pido.getCpunm()+" time: "+pido.getObstime());
        Long objref = null;
        objref = pido.getObjref();
        Long cpunm = null;
        cpunm = pido.getCpunm();
        if((new Boolean(!hasCPU(cpunm).booleanValue())).booleanValue())
            return;
        Boolean cond_8 = null;
        cond_8 = new Boolean(objs.containsKey(objref));
        if(cond_8.booleanValue())
        {
            if((new Boolean(UTIL.NumberToLong(objs.get(objref)).longValue() == (new Long(0L)).longValue())).booleanValue())
            {
                objs.put(objref, cpunm);
            } else
            {
                String tmpArg_v_28 = null;
                String var1_29 = null;
                String var1_30 = null;
                String var1_31 = null;
                String var1_32 = null;
                var1_32 = (new String("Object (id = ")).concat(nat2str(objref));
                var1_31 = var1_32.concat(new String(") can not be moved from cpu "));
                var1_30 = var1_31.concat(nat2str(UTIL.NumberToLong(objs.get(objref))));
                var1_29 = var1_30.concat(new String(" to cpu "));
                tmpArg_v_28 = var1_29.concat(nat2str(objref));
                addError(tmpArg_v_28);
            }
        } else
        {
            HashMap rhs_11 = new HashMap();
            HashMap var2_13 = new HashMap();
            var2_13 = new HashMap();
            var2_13.put(objref, cpunm);
            HashMap m1_20 = (HashMap)objs.clone();
            HashMap m2_21 = var2_13;
            HashSet com_16 = new HashSet();
            com_16.addAll((Collection)m1_20.keySet());
            com_16.retainAll((Collection)m2_21.keySet());
            boolean all_applies_17 = true;
            Object d_18;
            for(Iterator bb_19 = com_16.iterator(); bb_19.hasNext() && all_applies_17; all_applies_17 = m1_20.get(d_18).equals(m2_21.get(d_18)))
                d_18 = bb_19.next();

            if(!all_applies_17)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_20.putAll(m2_21);
            rhs_11 = m1_20;
            objs = (HashMap)UTIL.clone(rhs_11);
        }
    }

    private Boolean hasObject(Long pid)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = new Boolean(objs.containsKey(pid));
        if(cond_2.booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_6 = null;
            String var1_7 = null;
            var1_7 = (new String("Object (id = ")).concat(nat2str(pid));
            tmpArg_v_6 = var1_7.concat(new String(") does not exist"));
            addError(tmpArg_v_6);
            return new Boolean(false);
        }
    }

    private Boolean hasObjectOnCpu(Long pobj, Long pcpu)
        throws CGException
    {
        if(!pre_hasObjectOnCpu(pobj, pcpu).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in hasObjectOnCpu");
        if((new Boolean(UTIL.NumberToLong(objs.get(pobj)).longValue() == pcpu.longValue())).booleanValue())
        {
            return new Boolean(true);
        } else
        {
            String tmpArg_v_9 = null;
            String var1_10 = null;
            String var1_11 = null;
            String var1_12 = null;
            String var1_13 = null;
            var1_13 = (new String("Object (id = ")).concat(nat2str(pobj));
            var1_12 = var1_13.concat(new String(") is deployed on cpu "));
            var1_11 = var1_12.concat(nat2str(UTIL.NumberToLong(objs.get(pobj))));
            var1_10 = var1_11.concat(new String(" in stead of cpu "));
            tmpArg_v_9 = var1_10.concat(nat2str(pcpu));
            addError(tmpArg_v_9);
            return new Boolean(false);
        }
    }

    private Boolean pre_hasObjectOnCpu(Long pobj, Long pcpu)
        throws CGException
    {
        Boolean varRes_3 = null;
        Boolean var1_4 = null;
        var1_4 = new Boolean(objs.containsKey(pobj));
        if((varRes_3 = var1_4).booleanValue())
        {
            Boolean var2_7 = null;
            var2_7 = new Boolean(cpus.containsKey(pcpu));
            varRes_3 = var2_7;
        }
        return varRes_3;
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long line;
    private TracefileMarker errors;
    private Boolean severe;
    private HashMap cputime;
    private HashMap bustime;
    private HashMap threads;
    private HashMap thrcpu;
    private HashMap curthr;
    private HashMap opreq;
    private HashMap opact;
    private HashMap opfin;
    private HashMap msgs;
    private HashMap cpus;
    private HashMap buses;
    private HashMap topos;
    private HashMap objs;

}