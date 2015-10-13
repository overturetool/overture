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
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.runtime.Utils;
import org.overture.codegen.vdm2java.JavaQuoteValueCreator;
import org.overture.codegen.vdm2jml.JmlGenUtil;
import org.overture.codegen.vdm2jml.JmlGenerator;

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
		//TODO: String types need treatment
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
	public boolean allowsNull()
	{
		return optional;
	}
	
	@Override
	public String toString()
	{
		if(optional)
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
	
	public String concCheckCall(String s)
	{
		return concCheckCall(s, null);
	}
	
	public String concCheckCall(String s, String arg)
	{
		String call =  Utils.class.getSimpleName() + "." + s + "(" + ARG_PLACEHOLDER;
		
		if(arg != null)
		{
			call += "," + arg;
		}
		
		call += ")";
		
		return call;
	}

	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage)
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
			call = concCheckCall(methodName, quoteType + CLASS_QUALIFIER);
		}
		else if(type instanceof ARecordTypeCG)
		{
			ARecordTypeCG rt = (ARecordTypeCG) type;
			String defClass = rt.getName().getDefiningClass();
			String recPackage = JmlGenUtil.consRecPackage(defClass, javaRootPackage);
			String fullyQualifiedRecType = recPackage + "."
					+ rt.getName().getName();
			
			call = concCheckCall(methodName, fullyQualifiedRecType + CLASS_QUALIFIER);
		}
		else
		{
			call = concCheckCall(methodName);
		}
		
		// If the type is optional 'null' is also a legal value
		if(allowsNull())
		{
			return "(" + consIsNullCheck() + JmlGenerator.JML_OR + call + ")";
		}
		
		return call;
	}
	
	public static Map<Class<? extends STypeCG>, String> getUtilsCallMap()
	{
		return utilsCallMap;
	}
}
