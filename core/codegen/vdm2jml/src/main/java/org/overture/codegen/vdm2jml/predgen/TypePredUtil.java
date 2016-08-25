package org.overture.codegen.vdm2jml.predgen;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.TypeAssistantIR;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.vdm2jml.JmlGenUtil;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.predgen.info.AbstractTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.LeafTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.MapInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInvDepCalculator;
import org.overture.codegen.vdm2jml.predgen.info.SeqInfo;
import org.overture.codegen.vdm2jml.predgen.info.SetInfo;
import org.overture.codegen.vdm2jml.predgen.info.TupleInfo;
import org.overture.codegen.vdm2jml.predgen.info.UnionInfo;
import org.overture.codegen.vdm2jml.predgen.info.UnknownLeaf;
import org.overture.codegen.vdm2jml.util.NameGen;

public class TypePredUtil
{
	private TypePredHandler handler;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public TypePredUtil(TypePredHandler handler)
	{
		this.handler = handler;
	}

	public List<String> consJmlCheck(ADefaultClassDeclIR encClass,
			String jmlVisibility, String annotationType, boolean invChecksGuard,
			AbstractTypeInfo typeInfo, SVarExpIR var)
	{
		List<String> predStrs = new LinkedList<>();

		if (handler.getDecorator().buildRecValidChecks())
		{
			appendRecValidChecks(invChecksGuard, typeInfo, var, predStrs, encClass);
		}

		StringBuilder inv = new StringBuilder();
		inv.append("//@ ");

		if (jmlVisibility != null)
		{
			inv.append(jmlVisibility);
			inv.append(' ');
		}

		inv.append(annotationType);
		inv.append(' ');

		if (invChecksGuard)
		{
			inv.append(consInvChecksGuard(encClass));
			inv.append('(');
		}

		String javaPackage = handler.getJmlGen().getJavaSettings().getJavaRootPackage();

		NameGen nameGen = new NameGen(encClass);
		String consCheckExp = typeInfo.consCheckExp(encClass.getName(), javaPackage, var.getName(), nameGen);

		if (consCheckExp != null)
		{
			inv.append(consCheckExp);
		} else
		{
			log.error("Expression could not be checked");
			// TODO: Consider better handling
			inv.append("true");
		}
		if (invChecksGuard)
		{
			inv.append(')');
		}

		inv.append(';');

		predStrs.add(inv.toString());

		return predStrs;
	}

	private String consInvChecksGuard(ADefaultClassDeclIR encClass)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(handler.getJmlGen().getAnnotator().consInvChecksOnNameEncClass(encClass));
		sb.append(JmlGenerator.JML_IMPLIES);

		return sb.toString();
	}

	private void appendRecValidChecks(boolean invChecksGuard,
			AbstractTypeInfo typeInfo, SVarExpIR var, List<String> predStrs,
			ADefaultClassDeclIR encClass)
	{
		List<ARecordTypeIR> recordTypes = getRecTypes(typeInfo);

		if (!recordTypes.isEmpty())
		{
			for (ARecordTypeIR rt : recordTypes)
			{
				StringBuilder inv = new StringBuilder();
				String fullyQualifiedRecType = fullyQualifiedRecType(rt);

				inv.append("//@ ");
				inv.append(JmlGenerator.JML_ASSERT_ANNOTATION);
				inv.append(' ');

				if (invChecksGuard)
				{
					inv.append(consInvChecksGuard(encClass));
				}

				if (var.getType() instanceof ARecordTypeIR)
				{
					if (handler.getJmlGen().getJmlSettings().genInvariantFor())
					{
						inv.append(JmlGenerator.JML_INVARIANT_FOR);
						inv.append('(');
						inv.append(var.getName());
						inv.append(')');
						// e.g. invariant_for(r1)
					} else
					{
						inv.append(var.getName());
						inv.append('.');
						inv.append(JmlGenerator.REC_VALID_METHOD_CALL);
						// e.g. r1.valid()
					}
				} else
				{
					inv.append(var.getName());
					inv.append(JmlGenerator.JAVA_INSTANCEOF);
					inv.append(fullyQualifiedRecType);
					inv.append(JmlGenerator.JML_IMPLIES);

					// So far we have:
					// e.g. r1 instanceof project.Entrytypes.R3

					if (handler.getJmlGen().getJmlSettings().genInvariantFor())
					{
						inv.append(JmlGenerator.JML_INVARIANT_FOR);
						inv.append('(');
						inv.append(consRecVarCast(var, fullyQualifiedRecType));
						;
						inv.append(')');

						// e.g. r1 instanceof project.Entrytypes.R3 ==> \invariant_for((project.Entrytypes.R3) r1);
					} else
					{
						inv.append(consRecVarCast(var, fullyQualifiedRecType));
						inv.append('.');
						inv.append(JmlGenerator.REC_VALID_METHOD_CALL);
						// e.g. r1 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) r1).valid()
					}
				}

				inv.append(';');
				predStrs.add(inv.toString());
			}
		}
	}

	private String consRecVarCast(SVarExpIR var, String fullyQualifiedRecType)
	{
		return "((" + fullyQualifiedRecType + ") " + var.getName() + ")";
	}

	public String fullyQualifiedRecType(ARecordTypeIR rt)
	{
		String defClass = rt.getName().getDefiningClass();
		String recPackage = JmlGenUtil.consRecPackage(defClass, handler.getJmlGen().getJavaGen().getJavaSettings().getJavaRootPackage());
		String fullyQualifiedRecType = recPackage + "."
				+ rt.getName().getName();
		return fullyQualifiedRecType;
	}

	private List<ARecordTypeIR> getRecTypes(AbstractTypeInfo typeInfo)
	{
		List<ARecordTypeIR> recTypes = new LinkedList<>();

		List<LeafTypeInfo> leaves = typeInfo.getLeafTypesRecursively();

		for (LeafTypeInfo leaf : leaves)
		{
			if (leaf.getType() instanceof ARecordTypeIR)
			{
				recTypes.add((ARecordTypeIR) leaf.getType());
			}
		}

		return recTypes;
	}

	public List<AMetaStmIR> consAssertStm(AbstractTypeInfo invTypes,
			ADefaultClassDeclIR encClass, SVarExpIR var, INode node,
			RecClassInfo recInfo)
	{
		boolean inAccessor = node != null && recInfo != null
				&& recInfo.inAccessor(node);

		List<AMetaStmIR> asserts = new LinkedList<>();
		List<String> assertStrs = consJmlCheck(encClass, null, JmlGenerator.JML_ASSERT_ANNOTATION, inAccessor, invTypes, var);

		for (String a : assertStrs)
		{
			AMetaStmIR assertStm = new AMetaStmIR();
			List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData(a);
			handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);
			asserts.add(assertStm);
		}

		return asserts;
	}

	public AMetaStmIR consVarNotNullAssert(String varName)
	{
		AMetaStmIR assertStm = new AMetaStmIR();
		List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData("//@ assert "
				+ varName + " != null;");
		handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);

		return assertStm;
	}

	public AbstractTypeInfo findTypeInfo(STypeIR type)
	{
		TypeAssistantIR assist = handler.getJmlGen().getJavaGen().getInfo().getTypeAssistant();

		if (type.getNamedInvType() != null)
		{
			ANamedTypeDeclIR namedInv = type.getNamedInvType();

			String defModule = namedInv.getName().getDefiningClass();
			String typeName = namedInv.getName().getName();

			NamedTypeInfo info = NamedTypeInvDepCalculator.findTypeInfo(handler.getJmlGen().getTypeInfoList(), defModule, typeName);

			if (assist.isOptional(type))
			{
				info = new NamedTypeInfo(info.getTypeName(), info.getDefModule(), info.hasInv(), true, info.getDomainType());
			}

			if (info == null)
			{
				log.error("Could not find info for named type '" + typeName
						+ "' defined in module '" + defModule);
			}

			return info;

			// We do not need to collect sub named invariant types
		} else
		{
			if (type instanceof AUnionTypeIR)
			{
				List<AbstractTypeInfo> types = new LinkedList<>();

				for (STypeIR t : ((AUnionTypeIR) type).getTypes())
				{
					types.add(findTypeInfo(t));
				}

				return new UnionInfo(assist.isOptional(type), types);
			} else if (type instanceof ATupleTypeIR)
			{
				List<AbstractTypeInfo> types = new LinkedList<>();

				for (STypeIR t : ((ATupleTypeIR) type).getTypes())
				{
					types.add(findTypeInfo(t));
				}

				return new TupleInfo(assist.isOptional(type), types);
			} else if (type instanceof ASeqSeqTypeIR)
			{
				ASeqSeqTypeIR seqType = (ASeqSeqTypeIR) type;
				STypeIR elementType = seqType.getSeqOf();

				return new SeqInfo(assist.isOptional(seqType), findTypeInfo(elementType), BooleanUtils.isTrue(seqType.getSeq1()));
			} else if (type instanceof ASetSetTypeIR)
			{
				ASetSetTypeIR setType = (ASetSetTypeIR) type;
				STypeIR elementType = setType.getSetOf();

				return new SetInfo(assist.isOptional(setType), findTypeInfo(elementType));
			} else if (type instanceof AMapMapTypeIR)
			{
				AMapMapTypeIR mapType = (AMapMapTypeIR) type;

				AbstractTypeInfo domInfo = findTypeInfo(mapType.getFrom());
				AbstractTypeInfo rngInfo = findTypeInfo(mapType.getTo());

				boolean injective = BooleanUtils.isTrue(mapType.getInjective());

				return new MapInfo(assist.isOptional(mapType), domInfo, rngInfo, injective);

			} else if (type instanceof AUnknownTypeIR
					|| type instanceof AClassTypeIR
					|| type instanceof AExternalTypeIR)
			{
				// Iterators are class types for instance
				return new UnknownLeaf();
			} else
			{
				return new LeafTypeInfo(type, assist.isOptional(type));
			}
		}
	}
}
