package org.overture.ast.types.assistants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierTCAssistant;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;
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

    		type.setSetType(set.isEmpty() ? null : new ASetType(location,false, set.getType(location).clone(),false,false));
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

	public static AFunctionType getFunction(AUnionType type) {
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

	public static boolean isOperation(AUnionType type) {
		return getOperation(type) != null;
	}

	public static AOperationType getOperation(AUnionType type) {
		
		if (!type.getOpDone())
		{
    		type.setOpDone(true);
    		type.setOpType(PTypeAssistant.getOperation(new AUnknownType(type.getLocation(), false)));
    		
       		PTypeSet result = new PTypeSet();
       		Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isOperation(t))
    			{
    				if (t.getDefinitions() != null) defs.addAll(t.getDefinitions());
    				AOperationType op = PTypeAssistant.getOperation(t);
    				result.add(op.getResult());

    				for (int p=0; p < op.getParameters().size(); p++)
    				{
    					PType pt = op.getParameters().get(p);
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

    			type.setOpType(new AOperationType(type.getLocation(),false, plist, rtype));
    			type.getOpType().setDefinitions(defs);
    		}
    		else
    		{
    			type.setOpType(null);
    		}
    	}

		return type.getOpType();
	}

	public static boolean isSeq(AUnionType type) {
		return getSeq(type) != null;
	}

	public static boolean isNumeric(AUnionType type) {
		return getNumeric(type) != null;
	}

	public static SNumericBasicType getNumeric(AUnionType type) {
		if (!type.getNumDone())
		{
    		type.setNumDone(true);
			type.setNumType(new ANatNumericBasicType(type.getLocation(),false));		// lightest default
			boolean found = false;

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isNumeric(t))
    			{
    				SNumericBasicType nt = PTypeAssistant.getNumeric(t);

    				if (SNumericBasicTypeAssistant.getWeight(nt) > SNumericBasicTypeAssistant.getWeight(type.getNumType()))
    				{
    					type.setNumType(nt);
    				}

    				found = true;
    			}
    		}
    		
    		if (!found) type.setNumType(null);
		}

		return type.getNumType();
	}

	public static boolean isMap(AUnionType type) {
		return getMap(type) != null;
	}

	public static boolean isSet(AUnionType type) {
		 return getSet(type) != null;
	}

	public static boolean isRecord(AUnionType type) {
		return getRecord(type) != null;
	}

	public static ARecordInvariantType getRecord(AUnionType type) {
		if (!type.getRecDone())
		{
    		type.setRecDone(true);		// Mark early to avoid recursion.
    		type.setRecType(PTypeAssistant.getRecord(new AUnknownType(type.getLocation(), false)));
    		
    		// Build a record type with the common fields of the contained
    		// record types, making the field types the union of the original
    		// fields' types...

    		Map<String, PTypeSet> common = new HashMap<String, PTypeSet>();

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isRecord(t))
    			{
    				for (AFieldField f: PTypeAssistant.getRecord(t).getFields())
    				{
    					PTypeSet current = common.get(f.getTag());

    					if (current == null)
    					{
    						common.put(f.getTag(), new PTypeSet(f.getType()));
    					}
    					else
    					{
    						current.add(f.getType());
    					}
    				}
    			}
    		}

    		List<AFieldField> fields = new Vector<AFieldField>();

    		for (String tag: common.keySet())
    		{
				LexNameToken tagname = new LexNameToken("?", tag, type.getLocation());
				fields.add(new AFieldField(null, tagname, tag, common.get(tag).getType(type.getLocation()), false));
    		}

    		type.setRecType(fields.isEmpty() ? null : new ARecordInvariantType(type.getLocation(), false, new LexNameToken("?", "?", type.getLocation()), fields));
		}

		return type.getRecType();
	}

	public static boolean isClass(AUnionType type) {
		return getClassType(type) != null;
	}

	public static AClassType getClassType(AUnionType type) {
		if (!type.getClassDone())
		{
    		type.setClassDone(true);	// Mark early to avoid recursion.
    		type.setClassType(PTypeAssistant.getClassType(new AUnknownType(type.getLocation(),false)));

    		// Build a class type with the common fields of the contained
    		// class types, making the field types the union of the original
    		// fields' types...

    		Map<LexNameToken, PTypeSet> common = new HashMap<LexNameToken, PTypeSet>();
    		Map<LexNameToken, AAccessSpecifierAccessSpecifier> access = new HashMap<LexNameToken, AAccessSpecifierAccessSpecifier>();
    		LexNameToken classname = null;

    		for (PType t: type.getTypes())
    		{
    			if (PTypeAssistant.isClass(t))
    			{
    				AClassType ct = PTypeAssistant.getClassType(t);

    				if (classname == null)
    				{
    					classname = ct.getClassdef().getName();
    				}

    				for (PDefinition f: SClassDefinitionAssistant.getDefinitions(ct.getClassdef()))
    				{
    					// TypeSet current = common.get(f.name);
    					LexNameToken synthname = f.getName().getModifiedName(classname.name);
    					PTypeSet current = null;

    					for (LexNameToken n: common.keySet())
    					{
    						if (n.name.equals(synthname.name))
    						{
    							current = common.get(n);
    							break;
    						}
    					}

    					PType ftype = f.getType();

    					if (current == null)
    					{
    						common.put(synthname, new PTypeSet(ftype));
    					}
    					else
    					{
    						current.add(ftype);
    					}

    					PAccessSpecifier curracc = access.get(synthname);

    					if (curracc == null)
    					{
    						access.put(synthname, f.getAccess());
    					}
    					else
    					{
    						if (PAccessSpecifierTCAssistant.narrowerThan(curracc, f.getAccess()))
    						{
    							access.put(synthname, f.getAccess());
    						}
    					}
    				}
    			}
    		}

    		List<PDefinition> newdefs = new Vector<PDefinition>();

    		// Note that the pseudo-class is named after one arbitrary
    		// member of the union, even though it has all the distinct
    		// fields of the set of classes within the union.

    		for (LexNameToken synthname: common.keySet())
    		{
    			PDefinition def = new ALocalDefinition(synthname.location,
					synthname, NameScope.GLOBAL, false, null, common.get(synthname).getType(type.getLocation()), null);

    			def.setAccess(access.get(synthname));
				newdefs.add(def);
    		}

    		type.setClassType((classname == null) ? null :
    			new AClassType(type.getLocation(),false,classname,
    				new AClassClassDefinition(classname.getLocation(), classname,
    					null, false, null, type, null, new LexNameList(), newdefs, null, null, null, null, null, null, newdefs, null, null, null)));
		}

		return type.getClassType();
	}

	public static boolean isUnion(AUnionType type) {
		return true;
	}

	public static AUnionType getUnion(AUnionType type) {
		return type;
	}

	public static boolean narrowerThan(AUnionType type,
			PAccessSpecifier accessSpecifier) {
		
		for (PType t: type.getTypes())
		{
			if (PTypeAssistant.narrowerThan(t,accessSpecifier))
			{
				return true;
			}
		}

		return false;
	}

	

}
