package org.overture.ast.util;

import java.util.Comparator;

import org.overture.ast.types.PType;

public class PTypeComparator implements Comparator<PType> {

	public int compare(PType o1, PType o2) {
		
		return o1.toString().compareTo(o2.toString());
		
	}

}
