package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ASeq1Type;
import org.overture.ast.types.ASeqType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.util.Utils;


@SuppressWarnings("serial")
public class PTypeSet extends TreeSet<PType>{
	
	public PTypeSet()
	{
		super();
	}

	public PTypeSet(PType t)
	{
		add(t);
	}

	public PTypeSet(PType t1, PType t2)
	{
		add(t1);
		add(t2);
	}

	
	@Override
	public boolean add(PType t)
	{
		if (t instanceof ASeq1Type)
		{
			// If we add a Seq1Type, and there is already a SeqType in the set
			// we ignore the Seq1Type.
			
			ASeq1Type s1t = (ASeq1Type)t;
			ASeqType st = new ASeqType(s1t.getLocation(),false, s1t.getSeqof(), null);
			
			if (contains(st))
			{
				return false;	// Was already there
			}
		}
		else if (t instanceof ASeqType)
		{
			// If we add a SeqType, and there is already a Seq1Type in the set
			// we replace the Seq1Type.
			
			ASeqType st = (ASeqType)t;
			ASeq1Type s1t = new ASeq1Type(st.getLocation(),false, st.getSeqof(),null);
			
			if (contains(s1t))
			{
				remove(s1t);	// Replace seq with seq1
			}
		}
		else if (t instanceof SNumericBasicType)
		{
			for (PType x: this)
			{
				if (x instanceof SNumericBasicType)
				{
					if ( ANumericBasicTypeAssistant.getWeight(PTypeAssistant.getNumeric(x)) < ANumericBasicTypeAssistant.getWeight(PTypeAssistant.getNumeric(t)) )
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
	
	public PType getType(LexLocation location) {
		// If there are any Optional(Unknowns) these are the result of
		// nil values, which set the overall type as optional. Other
		// optional types stay.

		Iterator<PType> tit = this.iterator();
		boolean optional = false;

		while (tit.hasNext()) {
			PType t = tit.next();

			if (t instanceof AOptionalType) {
				AOptionalType ot = (AOptionalType) t;

				if (ot.getType() instanceof AUnknownType) {
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
		PType result = null;

		if (this.size() == 1) {
			result = this.iterator().next();
		} else {
			result = new AUnionType(location, false,null, new ArrayList<PType>(
					this),false, false);
		}

		return (optional ? new AOptionalType(location, false,null, result) : result);
	}
	
	@Override
	public String toString()
	{
		return Utils.setToString(this, ", ");
	}
}
