package org.overture.codegen.vdm2jml.predgen.info;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.logging.Logger;
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

	private STypeCG type;
	
	private static Map<Class<? extends STypeCG>, String> utilsCallMap;
	
	static
	{
		utilsCallMap = new HashMap<>();
		utilsCallMap.put(ABoolBasicTypeCG.class, IS_BOOL);
		utilsCallMap.put(ANatNumericBasicTypeCG.class, IS_NAT);
		utilsCallMap.put(ANat1NumericBasicTypeCG.class, IS_NAT1);
		utilsCallMap.put(AIntNumericBasicTypeCG.class, IS_INT);
		utilsCallMap.put(ARatNumericBasicTypeCG.class, IS_RAT);
		utilsCallMap.put(ARealNumericBasicTypeCG.class, IS_REAL);
		utilsCallMap.put(ACharBasicTypeCG.class, IS_CHAR);
		utilsCallMap.put(ATokenBasicTypeCG.class, IS_TOKEN);
		utilsCallMap.put(AQuoteTypeCG.class, IS);
		utilsCallMap.put(ARecordTypeCG.class, IS);
		utilsCallMap.put(AStringTypeCG.class, IS);
	}
	
	public LeafTypeInfo(STypeCG type, boolean optional)
	{
		super(optional);
		this.type = type;
	}
	
	public STypeCG getType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		if(isOptional())
		{
			return "[" + type.toString() + "]";
		}
		else
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
	public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen)
	{
		String methodName = utilsCallMap.get(type.getClass());
		
		if(methodName == null)
		{
			Logger.getLog().printErrorln("Got unhandled case");
			return "true";
		}

		String call;
		if(type instanceof AQuoteTypeCG)
		{
			String qouteValue = ((AQuoteTypeCG) type).getValue();
			String quoteType = JavaQuoteValueCreator.fullyQualifiedQuoteName(javaRootPackage, qouteValue);
			call = consSubjectCheckForType(methodName, arg, quoteType);
		}
		else if(type instanceof ARecordTypeCG)
		{
			ARecordTypeCG rt = (ARecordTypeCG) type;
			String defClass = rt.getName().getDefiningClass();
			String recPackage = JmlGenUtil.consRecPackage(defClass, javaRootPackage);
			String fullyQualifiedRecType = recPackage + "."
					+ rt.getName().getName();
			
			call = consSubjectCheckForType(methodName, arg, fullyQualifiedRecType);
		}
		else if(type instanceof AStringTypeCG)
		{
			call = consSubjectCheckForType(methodName, arg, String.class.getSimpleName());
		}
		else
		{
			call = consSubjectCheck(methodName, arg);
		}
		
		// If the type is optional 'null' is also a legal value
		if(isOptional())
		{
			return "(" + consIsNullCheck(arg) + JmlGenerator.JML_OR + call + ")";
		}
		
		return call;
	}

	private String consSubjectCheck(String methodName, String arg)
	{
		return consSubjectCheck(Utils.class.getSimpleName(), methodName, arg);
	}

	private String consSubjectCheckForType(String methodName, String arg, String type)
	{
		return consSubjectCheckExtraArg(Utils.class.getSimpleName(), methodName, arg, type + CLASS_QUALIFIER);
	}
	
	public static Map<Class<? extends STypeCG>, String> getUtilsCallMap()
	{
		return utilsCallMap;
	}
}
