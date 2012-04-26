package org.overture.typechecker.tests.framework;

import java.util.Vector;

import org.overture.typechecker.tests.framework.TCStruct.Type;
import org.overturetool.vdmjV2.messages.VDMError;
import org.overturetool.vdmjV2.messages.VDMWarning;

@SuppressWarnings("serial")
public class TCStructList extends Vector<TCStruct> {

	
	public boolean markTCStruct(VDMError error) {
		
		TCStruct s = null;
		
		for (TCStruct element : this) {
			if(element.is(error))
			{
				s = element;
				this.remove(s);
				return true;
			}
		}
		return false;
		

	}
	
	public boolean markTCStruct(VDMWarning error) {
		
		TCStruct s = null;
		
		for (TCStruct element : this) {
			if(element.is(error))
			{
				s = element;
				this.remove(s);
				return true;				
			}
		}
		return false;		
	}
	
	@Override
	public synchronized String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		for (TCStruct tcs : this) {
			sb.append(tcs.toString());	
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public int getErrorCount()
	{
		int res = 0;
		
		for (TCStruct e : this) {
			if(e.type == Type.ERROR)
				res++;
		}
		return res;
	}
	
	public int getWarningCount()
	{
		int res = 0;
		
		for (TCStruct e : this) {
			if(e.type == Type.WARNING)
				res++;
		}
		return res;
	}
	
}
