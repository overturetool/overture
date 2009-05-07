package org.overturetool.vdmtools.dbgp;

import java.util.ArrayList;
import java.util.Hashtable;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.exceptions.VDMToolsException;

public class BreakpointManager {
	private Hashtable<Integer, Breakpoint> breakpoints = new Hashtable<Integer, Breakpoint>();
	private VDMToolsProject vdmToolsProject;
	
	private static volatile BreakpointManager INSTANCE;
	
	public static BreakpointManager getInstance() {
		if (INSTANCE == null) {
			synchronized (VDMToolsProject.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = new BreakpointManager();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return INSTANCE;
	}
	
	private BreakpointManager() {
		vdmToolsProject =  VDMToolsProject.getInstance();
	}

	public int addBreakpoint(
			String filename, int linenumber, boolean enabled, 
			DBGPBreakpointType breakpointType, String hitCondition, 
			String expression, int hitValue, boolean isTemp) throws DBGPException {
		try {
			int id = vdmToolsProject.GetInterpreter().SetBreakPointByPos(filename, linenumber, 10);
			if (breakpoints.contains(id)){
				
			}
			else
			{
				Breakpoint bp = new Breakpoint(filename,linenumber, enabled, breakpointType, hitCondition, expression, hitValue, isTemp);
				breakpoints.put(id, bp);
			}
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void removeBreakpooint(int id){
		if (breakpoints.containsKey(id)){
			breakpoints.remove(id);
		}
	}

	public ArrayList<Breakpoint> getBreakPoints(){
		ArrayList<Breakpoint> bpList = new ArrayList<Breakpoint>();
		for (Breakpoint breakpoint : breakpoints.values()) {
			bpList.add(breakpoint);
		}
		return bpList;
	}
	
	public Breakpoint getBreakpoint(int id) throws Exception{
		if (breakpoints.containsKey(id)){
			return breakpoints.get(id);
		}
		else {
			throw new Exception("the breakpoint does not exist");
		}
	}

	public void updateBreakpoint(int id, String newState, int newLine,
			int hitValue, String hitCondition, String condEString) {
		if (breakpoints.containsKey(id)){
			Breakpoint bp = breakpoints.get(id);

			// new state
			if (!newState.equals( bp.getState() )) {
				bp.setEnabled(!bp.isEnabled());
			}
			
			// newLine
			if (bp.getLineNumber() != newLine){
				bp.setLineNumber(newLine);
			}

			// hitValue
			if (bp.getHitValue() != hitValue){
				bp.setHitValue(hitValue);
			}

			// hitCondition
			if (!bp.getHitCondition().equals(hitCondition)) {
				bp.setHitCondition(hitCondition);
			}
			
			// condEString
			if (!bp.getExpression().equals(condEString)){
				bp.setExpression(condEString);
			}
		}
	}
	
	
}
