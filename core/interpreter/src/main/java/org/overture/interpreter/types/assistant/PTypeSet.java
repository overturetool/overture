package org.overture.interpreter.types.assistant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import org.overture.interpreter.ast.types.AOptionalTypeInterpreter;
import org.overture.interpreter.ast.types.ASeq1SeqTypeInterpreter;
import org.overture.interpreter.ast.types.ASeqSeqTypeInterpreter;
import org.overture.interpreter.ast.types.AUnionTypeInterpreter;
import org.overture.interpreter.ast.types.AUnknownTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.ast.types.SNumericBasicTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.vdmj.util.Utils;


@SuppressWarnings("serial")
public class PTypeSet extends HashSet<PTypeInterpreter>{
	
	public PTypeSet()
	{
		super();
	}

	public PTypeSet(PTypeInterpreter t)
	{
		add(t);
	}

	public PTypeSet(PTypeInterpreter t1, PTypeInterpreter t2)
	{
		add(t1);
		add(t2);
	}

	
	public PTypeSet(List<PTypeInterpreter> types) {
		super(types);
	}

	@Override
	public boolean add(PTypeInterpreter t)
	{
		if (t instanceof ASeq1SeqTypeInterpreter)
		{
			// If we add a Seq1Type, and there is already a SeqType in the set
			// we ignore the Seq1Type.
			
			ASeq1SeqTypeInterpreter s1t = (ASeq1SeqTypeInterpreter)t;
			ASeqSeqTypeInterpreter st = new ASeqSeqTypeInterpreter(s1t.getLocation(),false, s1t.getSeqof(), false);
			
			if (contains(st))
			{
				return false;	// Was already there
			}
		}
		else if (t instanceof ASeqSeqTypeInterpreter)
		{
			// If we add a SeqType, and there is already a Seq1Type in the set
			// we replace the Seq1Type.
			
			ASeqSeqTypeInterpreter st = (ASeqSeqTypeInterpreter)t;
			ASeq1SeqTypeInterpreter s1t = new ASeq1SeqTypeInterpreter(st.getLocation(),false, st.getSeqof(),null);
			
			if (contains(s1t))
			{
				remove(s1t);	// Replace seq with seq1
			}
		}
		else if (t instanceof SNumericBasicTypeInterpreter)
		{
			for (PTypeInterpreter x: this)
			{
				if (x instanceof SNumericBasicTypeInterpreter)
				{
					if ( ANumericBasicTypeInterpreterAssistant.getWeight(PTypeInterpreterAssistant.getNumeric(x)) < ANumericBasicTypeInterpreterAssistant.getWeight(PTypeInterpreterAssistant.getNumeric(t)) )
					{
						remove(x);
						break;
					}
					else
					{
						return false;	// Was already there
					}
				}
			}
		}
		
		return super.add(t);
	}
	
	public PTypeInterpreter getType(LexLocation location) {
		// If there are any Optional(Unknowns) these are the result of
		// nil values, which set the overall type as optional. Other
		// optional types stay.

		Iterator<PTypeInterpreter> tit = this.iterator();
		boolean optional = false;

		while (tit.hasNext()) {
			PTypeInterpreter t = tit.next();

			if (t instanceof AOptionalTypeInterpreter) {
				AOptionalTypeInterpreter ot = (AOptionalTypeInterpreter) t;

				if (ot.getType() instanceof AUnknownTypeInterpreter) {
					if (this.size() > 1) {
						tit.remove();
						optional = true;
					} else {
						optional = false;
					}
				}
			}
		}

		assert this.size() > 0 : "Getting type of empty TypeSet";
		PTypeInterpreter result = null;

		if (this.size() == 1) {
			result = this.iterator().next();
		} else {
			AUnionTypeInterpreter uType = new AUnionTypeInterpreter(location, false, new ArrayList<PTypeInterpreter>(
					this),false, false);
			uType.setProdCard(-1);
			result = uType;
		}

		return (optional ? new AOptionalTypeInterpreter(location, false,null, result) : result);
	}
	
	@Override
	public String toString()
	{
		return Utils.setToString(this, ", ");
	}
}
