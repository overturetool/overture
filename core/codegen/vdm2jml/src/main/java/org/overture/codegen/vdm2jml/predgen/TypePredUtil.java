package org.overture.codegen.vdm2jml.predgen;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.logging.Logger;
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
	
	public TypePredUtil(TypePredHandler handler)
	{
		this.handler = handler;
	}
	
	public List<String> consJmlCheck(ADefaultClassDeclCG encClass, String jmlVisibility,
			String annotationType, boolean invChecksGuard, AbstractTypeInfo typeInfo,
			SVarExpCG var)
	{
		List<String> predStrs = new LinkedList<>();
		
		if(handler.getDecorator().buildRecValidChecks())
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
		
		if(invChecksGuard)
		{
			inv.append(consInvChecksGuard(encClass));
			inv.append('(');
		}

		String javaPackage = handler.getJmlGen().getJavaSettings().getJavaRootPackage();
		
		//TODO: Add names of parameters of the enclosing method to 'names-to-avoid' in name generator
		NameGen nameGen = new NameGen();
		String consCheckExp = typeInfo.consCheckExp(encClass.getName(), javaPackage, var.getName(), nameGen);
		
		if (consCheckExp != null)
		{
			inv.append(consCheckExp);
		} else
		{
			Logger.getLog().printErrorln("Expression could not be checked in '" + this.getClass().getSimpleName()
					+ "'");
			// TODO: Consider better handling
			inv.append("true");
		}
		if(invChecksGuard)
		{
			inv.append(')');
		}
		
		inv.append(';');
		
		predStrs.add(inv.toString());
		
		return predStrs;
	}

	private String consInvChecksGuard(ADefaultClassDeclCG encClass)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(handler.getJmlGen().getAnnotator().consInvChecksOnNameEncClass(encClass));
		sb.append(JmlGenerator.JML_IMPLIES);
		
		return sb.toString();
	}

	private void appendRecValidChecks(boolean invChecksGuard,
			AbstractTypeInfo typeInfo, SVarExpCG var,
			List<String> predStrs, ADefaultClassDeclCG encClass)
	{
		List<ARecordTypeCG> recordTypes = getRecTypes(typeInfo);

		if (!recordTypes.isEmpty())
		{
			for (ARecordTypeCG rt : recordTypes)
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

				if (var.getType() instanceof ARecordTypeCG)
				{
					if(handler.getJmlGen().getJmlSettings().genInvariantFor())
					{
						inv.append(JmlGenerator.JML_INVARIANT_FOR);
						inv.append('(');
						inv.append(var.getName());
						inv.append(')');
						// e.g. invariant_for(r1)
					}
					else
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
					
					if(handler.getJmlGen().getJmlSettings().genInvariantFor())
					{
						inv.append(JmlGenerator.JML_INVARIANT_FOR);
						inv.append('(');
						inv.append(consRecVarCast(var, fullyQualifiedRecType));;
						inv.append(')');
						
						// e.g. r1 instanceof project.Entrytypes.R3 ==> \invariant_for((project.Entrytypes.R3) r1);
					}
					else
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

	private String consRecVarCast(SVarExpCG var, String fullyQualifiedRecType)
	{
		return "((" + fullyQualifiedRecType + ") " + var.getName() + ")";
	}

	public String fullyQualifiedRecType(ARecordTypeCG rt)
	{
		String defClass = rt.getName().getDefiningClass();
		String recPackage = JmlGenUtil.consRecPackage(defClass, handler.getJmlGen().getJavaGen().getJavaSettings().getJavaRootPackage());
		String fullyQualifiedRecType = recPackage + "."
				+ rt.getName().getName();
		return fullyQualifiedRecType;
	}
	
	private List<ARecordTypeCG> getRecTypes(AbstractTypeInfo typeInfo)
	{
		List<ARecordTypeCG> recTypes = new LinkedList<>();

		List<LeafTypeInfo> leaves = typeInfo.getLeafTypesRecursively();

		for (LeafTypeInfo leaf : leaves)
		{
			if (leaf.getType() instanceof ARecordTypeCG)
			{
				recTypes.add((ARecordTypeCG) leaf.getType());
			}
		}

		return recTypes;
	}

	public List<AMetaStmCG> consAssertStm(AbstractTypeInfo invTypes,
			ADefaultClassDeclCG encClass, SVarExpCG var, INode node, RecClassInfo recInfo)
	{
		boolean inAccessor = node != null && recInfo != null && recInfo.inAccessor(node);

		List<AMetaStmCG> asserts = new LinkedList<>();
		List<String> assertStrs = consJmlCheck(encClass, null, JmlGenerator.JML_ASSERT_ANNOTATION, inAccessor, invTypes, var);
		
		for(String a : assertStrs)
		{
			AMetaStmCG assertStm = new AMetaStmCG();
			List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData(a);
			handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);
			asserts.add(assertStm);
		}

		return asserts;
	}
	
	public AMetaStmCG consVarNotNullAssert(String varName)
	{
		AMetaStmCG assertStm = new AMetaStmCG();
		List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData("//@ assert " + varName + " != null;");
		handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);

		return assertStm;
	}
	
	public AbstractTypeInfo findTypeInfo(STypeCG type)
	{
		TypeAssistantCG assist = handler.getJmlGen().getJavaGen().getInfo().getTypeAssistant();
		
		if (type.getNamedInvType() != null)
		{
			ANamedTypeDeclCG namedInv = type.getNamedInvType();

			String defModule = namedInv.getName().getDefiningClass();
			String typeName = namedInv.getName().getName();

			NamedTypeInfo info = NamedTypeInvDepCalculator.findTypeInfo(handler.getJmlGen().getTypeInfoList(), defModule, typeName);
			
			if(assist.isOptional(type))
			{
				info = new NamedTypeInfo(info.getTypeName(), info.getDefModule(), info.hasInv(), true, info.getDomainType());
			}

			if (info == null)
			{
				Logger.getLog().printErrorln("Could not find info for named type '"
						+ typeName + "' defined in module '" + defModule
						+ "' in '" + this.getClass().getSimpleName() + "'");
			}
			
			return info;

			// We do not need to collect sub named invariant types
		} else
		{
			if (type instanceof AUnionTypeCG)
			{
				List<AbstractTypeInfo> types = new LinkedList<>();
				
				for (STypeCG t : ((AUnionTypeCG) type).getTypes())
				{
					types.add(findTypeInfo(t));
				}
				
				return new UnionInfo(assist.isOptional(type), types);
			}
			else if(type instanceof ATupleTypeCG)
			{
				List<AbstractTypeInfo> types = new LinkedList<>();
				
				for(STypeCG t : ((ATupleTypeCG) type).getTypes())
				{
					types.add(findTypeInfo(t));
				}
				
				return new TupleInfo(assist.isOptional(type), types);
			}
			else if(type instanceof ASeqSeqTypeCG)
			{
				ASeqSeqTypeCG seqType = ((ASeqSeqTypeCG) type);
				STypeCG elementType = seqType.getSeqOf();
				
				return new SeqInfo(assist.isOptional(seqType), findTypeInfo(elementType), BooleanUtils.isTrue(seqType.getSeq1()));
			}
			else if(type instanceof ASetSetTypeCG)
			{
				ASetSetTypeCG setType = (ASetSetTypeCG) type;
				STypeCG elementType = setType.getSetOf();
				
				return new SetInfo(assist.isOptional(setType), findTypeInfo(elementType));
			}
			else if(type instanceof AMapMapTypeCG)
			{
				AMapMapTypeCG mapType = (AMapMapTypeCG) type;
				
				AbstractTypeInfo domInfo = findTypeInfo(mapType.getFrom());
				AbstractTypeInfo rngInfo = findTypeInfo(mapType.getTo());
				
				boolean injective = BooleanUtils.isTrue(mapType.getInjective());
				
				return new MapInfo(assist.isOptional(mapType), domInfo, rngInfo, injective);
				
			}
			else if(type instanceof AUnknownTypeCG || type instanceof AClassTypeCG || type instanceof AExternalTypeCG)
			{
				// Iterators are class types for instance
				return new UnknownLeaf();
			}
			else
			{
				return new LeafTypeInfo(type, assist.isOptional(type));
			}
		}
	}
}
