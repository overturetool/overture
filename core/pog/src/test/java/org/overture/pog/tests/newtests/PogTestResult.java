package org.overture.pog.tests.newtests;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

public class PogTestResult extends Vector<String> implements Serializable,
		List<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static PogTestResult convert(IProofObligationList ipol) {
		PogTestResult r = new PogTestResult();
		for (IProofObligation ipo : ipol) {
			r.add(ipo.getKindString() + "obligation:" + ipo.getFullPredString());
		}
		return r;
	}
}
