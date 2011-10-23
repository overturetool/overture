package org.overture.ide.debug.core;

import java.util.Comparator;

import org.overture.ide.debug.core.model.internal.VdmVariable;

public class VdmVariableNameComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		if(o1 instanceof VdmVariable && o2 instanceof VdmVariable ){
			return ((VdmVariable) o1).getName().compareTo(((VdmVariable) o2).getName());
		}
		return 0;
	}

}
