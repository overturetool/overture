package org.overture.typechecker.assistant.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AUnionTypeAssistantTC extends AUnionTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUnionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public SSeqType getSeq(AUnionType type)
	{
		if (!type.getSeqDone())
		{
			type.setSeqDone(true); // Mark early to avoid recursion.
			type.setSeqType(PTypeAssistantTC.getSeq(AstFactory.newAUnknownType(type.getLocation())));

			PTypeSet set = new PTypeSet();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isSeq(t))
				{
					set.add(PTypeAssistantTC.getSeq(t).getSeqof());
				}
			}

			type.setSeqType(set.isEmpty() ? null
					: AstFactory.newASeqSeqType(type.getLocation(), set.getType(type.getLocation())));
		}

		return type.getSeqType();
	}

	public ASetType getSet(AUnionType type)
	{

		ILexLocation location = type.getLocation();

		if (!type.getSetDone())
		{
			type.setSetDone(true); // Mark early to avoid recursion.
			type.setSetType(PTypeAssistantTC.getSet(AstFactory.newAUnknownType(location)));

			PTypeSet set = new PTypeSet();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isSet(t))
				{
					set.add(PTypeAssistantTC.getSet(t).getSetof());
				}
			}

			type.setSetType(set.isEmpty() ? null
					: AstFactory.newASetType(location, set.getType(location)));
		}

		return type.getSetType();
	}

	public SMapType getMap(AUnionType type)
	{
		ILexLocation location = type.getLocation();

		if (!type.getMapDone())
		{
			type.setMapDone(true); // Mark early to avoid recursion.
			type.setMapType(af.createPTypeAssistant().getMap(AstFactory.newAUnknownType(location)));

			PTypeSet from = new PTypeSet();
			PTypeSet to = new PTypeSet();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isMap(t))
				{
					from.add(af.createPTypeAssistant().getMap(t).getFrom());
					to.add(af.createPTypeAssistant().getMap(t).getTo());
				}
			}

			type.setMapType(from.isEmpty() ? null
					: AstFactory.newAMapMapType(location, from.getType(location), to.getType(location)));
		}

		return type.getMapType();
	}

	public static boolean isProduct(AUnionType type, int size)
	{
		return getProduct(type, size) != null;
	}

	public static AProductType getProduct(AUnionType type, int n)
	{

		if (type.getProdCard() != n)
		{
			type.setProdCard(n);
			type.setProdType(PTypeAssistantTC.getProduct(AstFactory.newAUnknownType(type.getLocation()), n));

			// Build a N-ary product type, making the types the union of the
			// original N-ary products' types...

			Map<Integer, PTypeSet> result = new HashMap<Integer, PTypeSet>();

			for (PType t : type.getTypes())
			{
				if (n == 0 && PTypeAssistantTC.isProduct(t)
						|| PTypeAssistantTC.isProduct(t, n))
				{
					AProductType pt = PTypeAssistantTC.getProduct(t, n);
					int i = 0;

					for (PType member : pt.getTypes())
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

			for (int i = 0; i < result.size(); i++)
			{
				list.add(result.get(i).getType(type.getLocation()));
			}

			type.setProdType(list.isEmpty() ? null
					: AstFactory.newAProductType(type.getLocation(), list));
		}

		return type.getProdType();
	}

	public static boolean isProduct(AUnionType type)
	{
		return getProduct(type) != null;
	}

	public static AProductType getProduct(AUnionType type)
	{
		return getProduct(type, 0);
	}

	public static boolean isType(AUnionType b, Class<? extends PType> typeclass)
	{
		for (PType t : b.getTypes())
		{
			if (PTypeAssistantTC.isType(t, typeclass))
			{
				return true;
			}
		}

		return false;
	}

	public static PType isType(AUnionType exptype, String typename)
	{
		for (PType t : exptype.getTypes())
		{
			PType rt = PTypeAssistantTC.isType(t, typename);

			if (rt != null)
			{
				return rt;
			}
		}

		return null;
	}

	public AFunctionType getFunction(AUnionType type)
	{
		if (!type.getFuncDone())
		{
			type.setFuncDone(true);
			type.setFuncType(PTypeAssistantTC.getFunction(AstFactory.newAUnknownType(type.getLocation())));

			PTypeSet result = new PTypeSet();
			Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isFunction(t))
				{
					if (t.getDefinitions() != null)
					{
						defs.addAll(t.getDefinitions());
					}
					AFunctionType f = PTypeAssistantTC.getFunction(t);
					result.add(f.getResult());

					for (int p = 0; p < f.getParameters().size(); p++)
					{
						PType pt = f.getParameters().get(p);
						PTypeSet pset = params.get(p);

						if (pset == null)
						{
							pset = new PTypeSet(pt);
							params.put(p, pset);
						} else
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

				for (int i = 0; i < params.size(); i++)
				{
					PType pt = params.get(i).getType(type.getLocation());
					plist.add(pt);
				}

				type.setFuncType(AstFactory.newAFunctionType(type.getLocation(), true, plist, rtype));
				type.getFuncType().setDefinitions(defs);
			} else
			{
				type.setFuncType(null);
			}
		}

		return (AFunctionType) type.getFuncType();
	}

	public static boolean isOperation(AUnionType type)
	{
		return af.createAUnionTypeAssistant().getOperation(type) != null;
	}

	public AOperationType getOperation(AUnionType type)
	{

		if (!type.getOpDone())
		{
			type.setOpDone(true);
			type.setOpType(PTypeAssistantTC.getOperation(AstFactory.newAUnknownType(type.getLocation())));

			PTypeSet result = new PTypeSet();
			Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isOperation(t))
				{
					if (t.getDefinitions() != null)
					{
						defs.addAll(t.getDefinitions());
					}
					AOperationType op = PTypeAssistantTC.getOperation(t);
					result.add(op.getResult());

					for (int p = 0; p < op.getParameters().size(); p++)
					{
						PType pt = op.getParameters().get(p);
						PTypeSet pset = params.get(p);

						if (pset == null)
						{
							pset = new PTypeSet(pt);
							params.put(p, pset);
						} else
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

				for (int i = 0; i < params.size(); i++)
				{
					PType pt = params.get(i).getType(type.getLocation());
					plist.add(pt);
				}

				type.setOpType(AstFactory.newAOperationType(type.getLocation(), plist, rtype));
				type.getOpType().setDefinitions(defs);
			} else
			{
				type.setOpType(null);
			}
		}

		return (AOperationType) type.getOpType();
	}

	public boolean isSeq(AUnionType type)
	{
		return af.createAUnionTypeAssistant().getSeq(type) != null;
	}

	public static boolean isUnknown(AUnionType type)
	{
		for (PType t : type.getTypes())
		{
			if (PTypeAssistantTC.isUnknown(t))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isMap(AUnionType type)
	{
		return af.createAUnionTypeAssistant().getMap(type) != null;
	}

	public static boolean isSet(AUnionType type)
	{
		return af.createAUnionTypeAssistant().getSet(type) != null;
	}

	public static boolean isRecord(AUnionType type)
	{
		return af.createAUnionTypeAssistant().getRecord(type) != null;
	}

	public ARecordInvariantType getRecord(AUnionType type)
	{
		if (!type.getRecDone())
		{
			type.setRecDone(true); // Mark early to avoid recursion.
			type.setRecType(PTypeAssistantTC.getRecord(AstFactory.newAUnknownType(type.getLocation())));

			// Build a record type with the common fields of the contained
			// record types, making the field types the union of the original
			// fields' types...

			Map<String, PTypeSet> common = new HashMap<String, PTypeSet>();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isRecord(t))
				{
					for (AFieldField f : PTypeAssistantTC.getRecord(t).getFields())
					{
						PTypeSet current = common.get(f.getTag());

						if (current == null)
						{
							common.put(f.getTag(), new PTypeSet(f.getType()));
						} else
						{
							current.add(f.getType());
						}
					}
				}
			}

			List<AFieldField> fields = new Vector<AFieldField>();

			for (String tag : common.keySet())
			{
				LexNameToken tagname = new LexNameToken("?", tag, type.getLocation());
				fields.add(AstFactory.newAFieldField(tagname, tag, common.get(tag).getType(type.getLocation()), false));
			}

			type.setRecType(fields.isEmpty() ? null
					: AstFactory.newARecordInvariantType(type.getLocation(), fields));
		}

		return type.getRecType();
	}

}
