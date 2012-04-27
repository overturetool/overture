package org.overture.ast.types.assistants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;


public class AUnionTypeAssistat {

	public static PType typeResolve(AUnionType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved())
		{
			return type;
		}
		else
		{
			type.setResolved(true);
			type.setInfinite(true);
		}

		try
		{
			Set<PType> fixed = new HashSet<PType>();

			for (PType t: type.getTypes())
			{
				if (root != null)
					root.setInfinite(false);

				fixed.add(PTypeAssistant.typeResolve(t, root, rootVisitor, question));

				if (root != null)
					type.setInfinite(type.getInfinite() && root.getInfinite());
			}

			type.setTypes(new Vector<PType>(fixed));
			if (root != null) root.setInfinite(type.getInfinite());

			// Resolved types may be unions, so force a re-expand
			type.setExpanded(false);
			expand(type);

			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static void unResolve(AUnionType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType t: type.getTypes())
		{
			PTypeAssistant.unResolve(t);
		}
		
	}

	private static void expand(AUnionType type) {
		
		if (type.getExpanded()) return;
		Set<PType> exptypes = new HashSet<PType>();

		for (PType t: type.getTypes())
		{
    		if (t instanceof AUnionType)
    		{
    			AUnionType ut = (AUnionType)t;
  				expand(ut);
   				exptypes.addAll(ut.getTypes());
    		}
    		else
    		{
    			exptypes.add(t);
    		}
		}

		type.setTypes(new Vector<PType>(exptypes));
		type.setExpanded(true);
		List<PDefinition> definitions = type.getDefinitions();
 
		for (PType t: type.getTypes())
		{
			if (t.getDefinitions() != null)
			{
				definitions.addAll(t.getDefinitions());
			}
		}
		
	}

	public static SSeqType getSeq(AUnionType type) {
		if (!type.getSeqDone())
		{
	   		type.setSeqDone(true);		// Mark early to avoid recursion.
	   		type.setSeqType(PTypeAssistant.getSeq(new AUnknownType(type.getLocation(),false)));

	   		PTypeSet set = new PTypeSet();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isSeq(t))
    			{
    				set.add(PTypeAssistant.getSeq(t).getSeqof());
    			}
    		}

    		ASeqSeqType tt = new ASeqSeqType(type.getLocation(),false, false);
    		tt.setSeqof(set.getType(type.getLocation()));
    		type.setSeqType(set.isEmpty() ? null :
    			tt );
 		}

		return type.getSeqType();
	}

	public static ASetType getSet(AUnionType type) {
		
		LexLocation location = type.getLocation();
		
		if (!type.getSetDone())
		{
    		type.setSetDone(true);	// Mark early to avoid recursion.
    		type.setSetType(PTypeAssistant.getSet(new AUnknownType(location,false)));

    		PTypeSet set = new PTypeSet();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isSet(t))
    			{
    				set.add(PTypeAssistant.getSet(t).getSetof());
    			}
    		}

    		type.setSetType(set.isEmpty() ? null : new ASetType(location,false,null, set.getType(location),false,false));
		}

		return type.getSetType();
	}

	public static SMapType getMap(AUnionType type) {
		LexLocation location = type.getLocation();
		
		if (!type.getMapDone())
		{
    		type.setMapDone(true);		// Mark early to avoid recursion.
    		type.setMapType( PTypeAssistant.getMap(new AUnknownType(location,false)));

    		PTypeSet from = new PTypeSet();
    		PTypeSet to = new PTypeSet();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isMap(t))
    			{
    				from.add(PTypeAssistant.getMap(t).getFrom());
    				to.add(PTypeAssistant.getMap(t).getTo());
    			}
    		}
    		
    		AMapMapType mm = new AMapMapType(location, false, false);
    		mm.setFrom(from.getType(location));
    		mm.setTo( to.getType(location));

    		type.setMapType(from.isEmpty() ? null : mm );
		}

		return type.getMapType();
	}
	

}
