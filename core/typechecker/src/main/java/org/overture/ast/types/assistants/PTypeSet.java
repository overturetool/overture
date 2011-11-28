package org.overture.ast.types.assistants;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
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

	
	public PTypeSet(List<PType> types) {
		super(types);
	}

	@Override
	public boolean add(PType t)
	{
		if (t instanceof ASeq1SeqType)
		{
			// If we add a Seq1Type, and there is already a SeqType in the set
			// we ignore the Seq1Type.
			
			ASeq1SeqType s1t = (ASeq1SeqType)t;
			ASeqSeqType st = new ASeqSeqType(s1t.getLocation(),false, s1t.getSeqof(), false);
			
			if (contains(st))
			{
				return false;	// Was already there
			}
		}
		else if (t instanceof ASeqSeqType)
		{
			// If we add a SeqType, and there is already a Seq1Type in the set
			// we replace the Seq1Type.
			
			ASeqSeqType st = (ASeqSeqType)t;
			ASeq1SeqType s1t = new ASeq1SeqType(st.getLocation(),false, st.getSeqof(),null);
			
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
					if ( SNumericBasicTypeAssistant.getWeight(PTypeAssistant.getNumeric(x)) < SNumericBasicTypeAssistant.getWeight(PTypeAssistant.getNumeric(t)) )
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
			
			Vector<PType> types = new Vector<PType>();
			
			for (PType pType : this) {
				types.add(pType);//.clone()
			}
			//TODO
			//AUnionType uType = new AUnionType(location, false, types,false, false);
			AUnionType uType = new AUnionType(location, false, false, false);
			uType.setTypes(types);
			uType.setProdCard(-1);
			result = uType;
		}

		return (optional ? new AOptionalType(location, false,null, result) : result);
	}
	
	@Override
	public String toString()
	{
		return Utils.setToString(this, ", ");
	}
}
