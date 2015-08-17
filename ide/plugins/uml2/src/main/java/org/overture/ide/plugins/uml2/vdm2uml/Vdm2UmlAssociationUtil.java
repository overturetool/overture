/*
 * #%~
 * UML2 Translator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;

public class Vdm2UmlAssociationUtil
{
	public static class UnknownTypeDetector
			extends
			DepthFirstAnalysisAdaptorAnswer<UnknownTypeDetector.UnknownDetectorResult>
	{
		public static class UnknownDetectorResult
		{
			public boolean hasUnknown = false;

			public UnknownDetectorResult()
			{
			}

			public UnknownDetectorResult(boolean found)
			{
				this.hasUnknown = found;
			}
		}

		@Override
		public UnknownDetectorResult createNewReturnValue(INode node)
		{
			return new UnknownDetectorResult();
		}

		@Override
		public UnknownDetectorResult createNewReturnValue(Object node)
		{
			return new UnknownDetectorResult();
		}

		@Override
		public UnknownDetectorResult caseAUnknownType(AUnknownType node)
				throws AnalysisException
		{
			return new UnknownDetectorResult(true);
		}

		@Override
		public UnknownDetectorResult mergeReturns(
				UnknownDetectorResult original, UnknownDetectorResult new_)
		{
			if (new_ != null && new_.hasUnknown)
			{
				original.hasUnknown = true;
			}
			return original;
		}

	}

	public static final UnknownTypeDetector unknownDetector = new UnknownTypeDetector();

	public static boolean isSimpleType(PType type)
	{
		if (type instanceof ANamedInvariantType)
		{
			return true;
		}
		try
		{
			return Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(type, null)
					&& !type.apply(unknownDetector).hasUnknown;
		} catch (AnalysisException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}// || type.kindPType()==EType.BASIC;
	}

	public static boolean validType(PType type)
	{
		if (type instanceof SBasicType)
		{
			return false;
		} else if (type instanceof ABracketType)
		{
		} else if (type instanceof AClassType)
		{
		} else if (type instanceof AFunctionType)
		{
			return false;
		} else if (type instanceof SInvariantType)
		{
			return type instanceof ANamedInvariantType;
		} else if (type instanceof SMapType)
		{
			SMapType mType = (SMapType) type;
			// return isSimpleType(mType.getFrom())
			// && isSimpleType(mType.getTo());
			return validMapFromType(mType.getFrom())
					&& validMapType(mType.getTo());
		} else if (type instanceof AOperationType)
		{
			return false;
		} else if (type instanceof AOptionalType)
		{
			AOptionalType optionalType = (AOptionalType) type;
			return isSimpleType(optionalType.getType());
		} else if (type instanceof AParameterType)
		{
			return false;
		} else if (type instanceof AProductType)
		{
			return false;
		} else if (type instanceof AQuoteType)
		{
		} else if (type instanceof SSeqType)
		{
			SSeqType seqType = (SSeqType) type;
			return isSimpleType(seqType.getSeqof());
		} else if (type instanceof ASetType)
		{
			ASetType setType = (ASetType) type;
			return isSimpleType(setType.getSetof());
		} else if (type instanceof AUndefinedType || type instanceof AUnionType
				|| type instanceof AUnknownType
				|| type instanceof AUnresolvedType || type instanceof AVoidType
				|| type instanceof AVoidReturnType)
		{
			return false;
		}

		if (Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(type, null))
		{
			return true;
		}

		return false;
	}

	private static boolean validMapType(PType type)
	{
		if (isSimpleType(type)
				|| Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(type, null))
		{
			return true;
		}

		if (type instanceof SSeqType)
		{
			SSeqType seqType = (SSeqType) type;
			return isSimpleType(seqType.getSeqof());
		} else if (type instanceof ASetType)
		{
			ASetType setType = (ASetType) type;
			return isSimpleType(setType.getSetof());
		} else
		{
			return false;
		}
	}

	private static boolean validMapFromType(PType type)
	{
		if (type instanceof SSeqType)
		{
			SSeqType seqType = (SSeqType) type;
			if (seqType.getSeqof() instanceof SBasicType)
			{
				return true;
			}
		} else if (type instanceof ASetType)
		{
			ASetType setType = (ASetType) type;
			if (setType.getSetof() instanceof SBasicType)
			{
				return true;
			}
		} else if (type instanceof SBasicType)
		{
			return true;
		}
		return validMapType(type);
	}

	public static Type getReferenceClass(PType type, Map<String, Class> classes)
	{
		if (type instanceof AOptionalType)
		{
			type = ((AOptionalType) type).getType();
		}
		if (Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(type, null))
		{
			return getType(classes, type);
		}

		if (type instanceof SBasicType)
		{
			return getType(classes, type);
		} else if (type instanceof ABracketType)
		{
		} else if (type instanceof AClassType)
		{
		} else if (type instanceof AFunctionType)
		{
		} else if (type instanceof SInvariantType)
		{
			if (type instanceof ANamedInvariantType)
			{
				ANamedInvariantType nInvType = (ANamedInvariantType) type;
				return getType(classes, nInvType);
			}
		} else if (type instanceof SMapType)
		{
			SMapType mType = (SMapType) type;
			return getTypeForMap(classes, mType.getTo());
		} else if (type instanceof AOperationType)
		{
		} else if (type instanceof AOptionalType)
		{
		} else if (type instanceof AParameterType)
		{
		} else if (type instanceof AProductType)
		{
		} else if (type instanceof AQuoteType)
		{
		} else if (type instanceof SSeqType)
		{
			SSeqType seqType = (SSeqType) type;
			return getType(classes, seqType.getSeqof());
		} else if (type instanceof ASetType)
		{
			ASetType setType = (ASetType) type;
			return getType(classes, setType.getSetof());
		} else if (type instanceof AUndefinedType)
		{
		} else if (type instanceof AUnionType)
		{
		} else if (type instanceof AUnknownType)
		{
		} else if (type instanceof AUnresolvedType)
		{
		} else if (type instanceof AVoidType)
		{
		} else if (type instanceof AVoidReturnType)
		{
		}

		return null;
	}

	private static Type getType(Map<String, Class> classes, String name)
	{
		if (classes.containsKey(name))
		{
			return classes.get(name);
		}
		for (Class c : classes.values())
		{
			if (name.contains(UmlTypeCreatorBase.NAME_SEPERATOR))
			{
				int index = name.indexOf(UmlTypeCreatorBase.NAME_SEPERATOR);
				if (!c.getName().equals(name.subSequence(0, index)))
				{
					continue;
				} else
				{
					name = name.substring(index
							+ UmlTypeCreatorBase.NAME_SEPERATOR.length());
				}
			}
			Classifier ncl = c.getNestedClassifier(name);
			if (ncl != null)
			{
				return ncl;
			}
		}

		return null;
	}

	static Type getType(Map<String, Class> classes, PType type)
	{
		return getType(classes, UmlTypeCreatorBase.getName(type));
	}

	static Type getTypeForMap(Map<String, Class> classes, PType type)
	{
		if (type instanceof SSeqType)
		{
			type = ((SSeqType) type).getSeqof();
		} else if (type instanceof ASetType)
		{
			type = ((ASetType) type).getSetof();
		}else if(type instanceof AOptionalType)
		{
			type = ((AOptionalType) type).getType();
		}
		return getType(classes, UmlTypeCreatorBase.getName(type));
	}

	public static void createAssociation(String name, PType defType,
			AAccessSpecifierAccessSpecifier access, PExp defaultExp,
			Map<String, Class> classes, Class class_, boolean readOnly,
			UmlTypeCreator utc)
	{
		Type referencedClass = Vdm2UmlAssociationUtil.getReferenceClass(defType, classes);

		Assert.isNotNull(referencedClass, "association end with: "+defType+" cannot be found");
		
		int lower = Vdm2UmlUtil.extractLower(defType);

		Association association = class_.createAssociation(true, AggregationKind.NONE_LITERAL, name, lower, Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
		association.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(access));

		Property prop = association.getMemberEnd(name, null);
		prop.setVisibility(association.getVisibility());
		prop.setIsReadOnly(readOnly);

		// set default
		if (defaultExp != null)
		{
			prop.setDefault(defaultExp.toString());
		}
		// set static
		prop.setIsStatic(access.getStatic() != null);

		// set ordered
		prop.setIsOrdered(defType instanceof SSeqType);
		prop.setIsUnique(!(defType instanceof SSeqType || defType instanceof SMapType));

		// set qualifier if map
		if (defType instanceof SMapType)
		{
			SMapType mType = (SMapType) defType;
			PType fromType = mType.getFrom();
			PType toType = mType.getTo();

			Property qualifier = prop.createQualifier(null, Vdm2UmlAssociationUtil.getQualifierReferenceClass(class_, fromType, classes, utc));
			qualifier.setLower(Vdm2UmlUtil.extractLower(fromType));
			qualifier.setUpper(Vdm2UmlUtil.extractUpper(fromType));
			// set ordered
			qualifier.setIsOrdered(fromType instanceof SSeqType);
			qualifier.setIsUnique(!(fromType instanceof SSeqType || fromType instanceof SMapType));

			prop.setLower(Vdm2UmlUtil.extractLower(toType));
			prop.setUpper(Vdm2UmlUtil.extractUpper(toType));
			// set ordered
			prop.setIsOrdered(toType instanceof SSeqType);
			prop.setIsUnique(!(toType instanceof SSeqType || toType instanceof SMapType));

			// Map unique
			prop.setIsUnique(mType instanceof AInMapMapType);
			Property targetProp = association.getMemberEnd("", null);
			targetProp.setIsUnique(true);

		}
	}

	private static Type getQualifierReferenceClass(Class class_, PType type,
			Map<String, Class> classes, UmlTypeCreator utc)
	{
		PType qualifierType = unfoldSetSeqTypes(type);
		if (qualifierType instanceof SBasicType)
		{
			utc.create(class_, qualifierType);
			return utc.getUmlType(qualifierType);
		}
		return getReferenceClass(qualifierType, classes);
	}

	private static PType unfoldSetSeqTypes(PType type)
	{
		if (type instanceof SSeqType)
		{
			return ((SSeqType) type).getSeqof();
		} else if (type instanceof ASetType)
		{
			return ((ASetType) type).getSetof();
		}
		return type;
	}

}
