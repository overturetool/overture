package org.overture.ast.types.assistants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AProductType;
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
import org.overturetool.vdmj.util.Utils;


public class AUnionTypeAssistant {

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
			PTypeSet fixed = new PTypeSet();

			for (PType t: type.getTypes())
			{
				if (root != null)
					root.setInfinite(false);

				fixed.add(PTypeAssistant.typeResolve(t, root, rootVisitor, question).clone());

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

    		type.setSeqType(set.isEmpty() ? null :
    			new ASeqSeqType(type.getLocation(),false, set.getType(type.getLocation()),false));
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

    		type.setSetType(set.isEmpty() ? null : new ASetType(location,false, set.getType(location),false,false));
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

    		type.setMapType(from.isEmpty() ? null : new AMapMapType(location, false, from.getType(location), to.getType(location), false));
		}

		return type.getMapType();
	}

	public static String toDisplay(AUnionType exptype) {
		List<PType> types = exptype.getTypes();		
		
		if (types.size() == 1)
		{
			return types.iterator().next().toString();
		}
		else
		{
			return Utils.setToString(new PTypeSet(types), " | ");
		}
	}

	public static boolean isProduct(AUnionType type, int size) {
		return getProduct(type,size) != null;
	}

	public static AProductType getProduct(AUnionType type, int n) {
		
		if (type.getProdCard() != n)
		{
    		type.setProdCard(n);
    		type.setProdType(PTypeAssistant.getProduct(new AUnknownType(type.getLocation(),false),n));

    		// Build a N-ary product type, making the types the union of the
    		// original N-ary products' types...

    		Map<Integer, PTypeSet> result = new HashMap<Integer, PTypeSet>();

    		for (PType t: type.getTypes())
    		{
    			if ((n == 0 && PTypeAssistant.isProduct(t)) || PTypeAssistant.isProduct(t, n))
    			{
    				AProductType pt = PTypeAssistant.getProduct(t,n);
    				int i=0;

    				for (PType member: pt.getTypes())
    				{
    					PTypeSet ts = result.get(i);

    					if (ts == null)
    					{
    						ts = new PTypeSet();
    						result.put(i, ts);
    					}

    					ts.add(member);
    					i++;
    				}
    			}
    		}

    		PTypeList list = new PTypeList();

    		for (int i=0; i<result.size(); i++)
    		{
    			list.add(result.get(i).getType(type.getLocation()));
    		}

    		type.setProdType(list.isEmpty() ? null : new AProductType(type.getLocation(), false, list));
		}

		return type.getProdType();
	}

	public static boolean isProduct(AUnionType type) {
		return getProduct(type) != null;
	}

	public static AProductType getProduct(AUnionType type) {
		return getProduct(type, 0);
	}

	public static boolean isType(AUnionType b, Class<? extends PType> typeclass) {
		for (PType t: b.getTypes())
		{
			if (PTypeAssistant.isType(t, typeclass))
			{
				return true;
			}
		}

		return false;
	}

	public static PType isType(AUnionType exptype, String typename) {
		for (PType t: exptype.getTypes())
		{
			PType rt = PTypeAssistant.isType(t, typename);

			if (rt != null)
			{
				return rt;
			}
		}

		return null;
	}

	public static boolean equals(AUnionType type, PType other) {
		other = PTypeAssistant.deBracket(other);
		PTypeSet types = new PTypeSet(type.getTypes());
		
		if (other instanceof AUnionType)
		{
			AUnionType uother = (AUnionType)other;
			
			for (PType t: uother.getTypes())
			{
				if (!types.contains(t))
				{
					return false;
				}
			}

			return true;
		}

		return types.contains(other);
	}
	

	public static boolean isFunction(AUnionType type) {
		return getFunction(type) != null;
	}

	private static AFunctionType getFunction(AUnionType type) {
		if (!type.getFuncDone())
		{
    		type.setFuncDone(true);
    		type.setFuncType(PTypeAssistant.getFunction(new AUnknownType(type.getLocation(),false)));

       		PTypeSet result = new PTypeSet();
       		Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isFunction(t))
    			{
    				if (t.getDefinitions() != null) defs.addAll(t.getDefinitions());
    				AFunctionType f = PTypeAssistant.getFunction(t);
    				result.add(f.getResult());

    				for (int p=0; p < f.getParameters().size(); p++)
    				{
    					PType pt = f.getParameters().get(p);
    					PTypeSet pset = params.get(p);

    					if (pset == null)
    					{
    						pset = new PTypeSet(pt);
    						params.put(p, pset);
    					}
    					else
    					{
    						pset.add(pt);
    					}
    				}
    			}
    		}

    		if (!result.isEmpty())
    		{
    			PType rtype = result.getType(type.getLocation());
    			PTypeList plist = new PTypeList();

    			for (int i=0; i<params.size(); i++)
    			{
    				PType pt = params.get(i).getType(type.getLocation());
    				plist.add(pt);
    			}

    			type.setFuncType(new AFunctionType(type.getLocation(), false, true, plist, rtype));
    			type.getFuncType().setDefinitions(defs);
    		}
    		else
    		{
    			type.setFuncType(null);
    		}
    	}

		return type.getFuncType();
	}
	

}
