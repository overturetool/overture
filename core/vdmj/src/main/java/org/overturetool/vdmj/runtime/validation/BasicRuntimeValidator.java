package org.overturetool.vdmj.runtime.validation;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;

public class BasicRuntimeValidator implements IRuntimeValidatior {

	final List<ConjectureDefinition> conjectures = new ArrayList<ConjectureDefinition>();
	final List<String[]> variables = new ArrayList<String[]>();
	
	public void init(ClassInterpreter classInterpreter) {
		// TODO Auto-generated method stub
		
	}

	public void validate(OperationValue operationValue, MessageType type) {
		
		if(conjectures.size() > 0)
		{
			ISchedulableThread ct = BasicSchedulableThread.getThread(Thread.currentThread());
			
			for (ConjectureDefinition conj : conjectures) {
				conj.process(operationValue.name.name,operationValue.classdef.getName(),type, SystemClock.getWallTime(),ct.getId(),operationValue.getSelf().objectReference);
			}
		}	
		
	}

	public void bindSystemVariables(SystemDefinition systemDefinition) {
	 
		
		List<String[]> variablesTemp = filterVariablesInSystem(systemDefinition.name.name,variables);
		Context ctxt = systemDefinition.getStatics();
		
		
		for (String[] strings : variablesTemp) {
			Value v = digInCtxt(strings,ctxt);
			for (ConjectureDefinition definition : conjectures) {
				definition.associateVariable(strings,v);
			}
			
		}
		
		
	}

	private Value digInCtxt(String[] strings, Context ctxt) {
		
		List<String> rest = new ArrayList<String>();
		for(int i = 1; i< strings.length;i ++)
		{
			rest.add(strings[i]);
		}
		
		for (LexNameToken name : ctxt.keySet()) {
			if(name.name.equals(rest.get(0)))
			{		
				Value v = ctxt.get(name);
				if(rest.size() > 1)
				{
					return digInVariable(v,rest.subList(1, rest.size()),ctxt);
				}
				else
				{
					return v; 
				}
			}
		}
		
		return null;
		
	}

	private Value digInVariable(Value value, List<String> rest, Context ctxt) {
		
		try {
			ObjectValue ov = value.objectValue(ctxt);			
			NameValuePairMap nvpm = ov.members;
			
			for (LexNameToken name : nvpm.keySet()) {
				if(name.name.equals(rest.get(0)))
				{
					Value v = nvpm.get(name);
					
					if(rest.size() > 1)
					{
						return digInVariable(v,rest.subList(1, rest.size()) , ctxt);
					}
					else
					{
						return v;
					}
				}
			}
			
			
			
		} catch (ValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
				
		return null;
	}

	private List<String[]> filterVariablesInSystem(String name,
			List<String[]> variables) {
		for (int i = 0; i < variables.size(); i++) {

			if(!variables.get(i)[0].equals(name))
			{
				variables.remove(i);
				i--;
			}
			
		}
		return variables;
	}

	
	
}
