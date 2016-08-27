package org.overture.codegen.vdm2jml.predgen.info;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.runtime.Utils;
import org.overture.codegen.vdm2java.JavaQuoteValueCreator;
import org.overture.codegen.vdm2jml.JmlGenUtil;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

public class LeafTypeInfo extends AbstractTypeInfo
{
	private static final String CLASS_QUALIFIER = ".class";
	private static final String IS_BOOL = "is_bool";
	private static final String IS_NAT = "is_nat";
	private static final String IS_NAT1 = "is_nat1";
	private static final String IS_INT = "is_int";
	private static final String IS_RAT = "is_rat";
	private static final String IS_REAL = "is_real";
	private static final String IS_CHAR = "is_char";
	private static final String IS_TOKEN = "is_token";
	private static final String IS = "is_";

	private STypeIR type;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private static Map<Class<? extends STypeIR>, String> utilsCallMap;

	static
	{
		utilsCallMap = new HashMap<>();
		utilsCallMap.put(ABoolBasicTypeIR.class, IS_BOOL);
		utilsCallMap.put(ANatNumericBasicTypeIR.class, IS_NAT);
		utilsCallMap.put(ANat1NumericBasicTypeIR.class, IS_NAT1);
		utilsCallMap.put(AIntNumericBasicTypeIR.class, IS_INT);
		utilsCallMap.put(ARatNumericBasicTypeIR.class, IS_RAT);
		utilsCallMap.put(ARealNumericBasicTypeIR.class, IS_REAL);
		utilsCallMap.put(ACharBasicTypeIR.class, IS_CHAR);
		utilsCallMap.put(ATokenBasicTypeIR.class, IS_TOKEN);
		utilsCallMap.put(AQuoteTypeIR.class, IS);
		utilsCallMap.put(ARecordTypeIR.class, IS);
		utilsCallMap.put(AStringTypeIR.class, IS);
	}

	public LeafTypeInfo(STypeIR type, boolean optional)
	{
		super(optional);
		this.type = type;
	}

	public STypeIR getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		if (isOptional())
		{
			return "[" + type.toString() + "]";
		} else
		{
			return type.toString();
		}
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> types = new LinkedList<LeafTypeInfo>();
		types.add(this);

		return types;
	}

	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage,
			String arg, NameGen nameGen)
	{
		String methodName = utilsCallMap.get(type.getClass());

		if (methodName == null)
		{
			log.error("Got unhandled case");
			return "true";
		}

		String call;
		if (type instanceof AQuoteTypeIR)
		{
			String qouteValue = ((AQuoteTypeIR) type).getValue();
			String quoteType = JavaQuoteValueCreator.fullyQualifiedQuoteName(javaRootPackage, qouteValue);
			call = consSubjectCheckForType(methodName, arg, quoteType);
		} else if (type instanceof ARecordTypeIR)
		{
			ARecordTypeIR rt = (ARecordTypeIR) type;
			String defClass = rt.getName().getDefiningClass();
			String recPackage = JmlGenUtil.consRecPackage(defClass, javaRootPackage);
			String fullyQualifiedRecType = recPackage + "."
					+ rt.getName().getName();

			call = consSubjectCheckForType(methodName, arg, fullyQualifiedRecType);
		} else if (type instanceof AStringTypeIR)
		{
			call = consSubjectCheckForType(methodName, arg, String.class.getSimpleName());
		} else
		{
			call = consSubjectCheck(methodName, arg);
		}

		// If the type is optional 'null' is also a legal value
		if (isOptional())
		{
			return "(" + consIsNullCheck(arg) + JmlGenerator.JML_OR + call
					+ ")";
		}

		return call;
	}

	private String consSubjectCheck(String methodName, String arg)
	{
		return consSubjectCheck(Utils.class.getSimpleName(), methodName, arg);
	}

	private String consSubjectCheckForType(String methodName, String arg,
			String type)
	{
		return consSubjectCheckExtraArg(Utils.class.getSimpleName(), methodName, arg, type
				+ CLASS_QUALIFIER);
	}

	public static Map<Class<? extends STypeIR>, String> getUtilsCallMap()
	{
		return utilsCallMap;
	}
}
