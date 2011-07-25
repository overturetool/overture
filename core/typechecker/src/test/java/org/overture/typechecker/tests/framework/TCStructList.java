package org.overture.typechecker.tests.framework;

import java.util.Vector;

import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

@SuppressWarnings("serial")
public class TCStructList extends Vector<TCStruct> {

	
	public void markTCStruct(VDMError error) {
		
		TCStruct s = null;
		
		for (TCStruct element : this) {
			if(element.is(error))
			{
				s = element;
				break;
			}
		}
		this.remove(s);

	}
	
	public void markTCStruct(VDMWarning error) {
		
		TCStruct s = null;
		
		for (TCStruct element : this) {
			if(element.is(error))
			{
				s = element;
				break;
			}
		}
		this.remove(s);
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
	
}
