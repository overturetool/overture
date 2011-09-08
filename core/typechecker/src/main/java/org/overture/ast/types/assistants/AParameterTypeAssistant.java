package org.overture.ast.types.assistants;

import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AParameterTypeAssistant {

	public static PType typeResolve(AParameterType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else type.setResolved(true);

		PDefinition p = question.env.findName(type.getName(), NameScope.NAMES);

		if (p == null || !(PDefinitionAssistantTC.getType(p) instanceof AParameterType))
		{
			TypeCheckerErrors.report(3433, "Parameter type @" + type.getName() + " not defined",type.getLocation(),type);
		}

		return type;
	}

	public static String toDisplay(AParameterType exptype) {
		
		return "@" + exptype.getName();
	}

	public static AProductType getProduct(AProductType type, int n) {
		
		NodeList<PType> tl = new NodeList<PType>(null);

		for (int i=0; i<n; i++)
		{
			tl.add(new AUnknownType(type.getLocation(),false));
		}

		return new AProductType(type.getLocation(),false, tl);
	}

	public static AProductType getProduct(AProductType type) {
		return new AProductType(type.getLocation(),false, new NodeList<PType>(null));
	}

	public static boolean isType(AParameterType b,
			Class<? extends PType> typeclass) {
		return true;
	}

	public static PType isType(AParameterType exptype, String typename) {
		return new AUnknownType(exptype.getLocation(),false);
	}

	public static boolean equals(AParameterType type, PType other) {
		return true;	// Runtime dependent - assume OK
	}

	public static boolean isFunction(AFunctionType type) {
		return true;
	}

	public static boolean isOperation(AParameterType type) {
		return true;
	}

	public static boolean isSeq(AParameterType type) {
		return true;
	}

	public static SSeqType getSeq(AParameterType type) {
		return new ASeqSeqType(type.getLocation(),false,null,true);
	}

	public static boolean isNumeric(AParameterType type) {
		return true;
	}

	public static ARealNumericBasicType getNumeric(AParameterType type) {
		return new ARealNumericBasicType(type.getLocation(), false);
	}

	public static boolean isMap(AParameterType type) {
		return true;
	}

	public static SMapType getMap(AParameterType type) {
		return new AMapMapType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), new AUnknownType(type.getLocation(), false), true);
	}

	public static boolean isSet(AParameterType type) {
		return true;
	}

	public static ASetType getSet(AParameterType type) {
		return new ASetType(type.getLocation(), false, new AUnknownType(type.getLocation(), false), true, false);
	}

	public static boolean isRecord(AParameterType type) {
		return true;
	}
	
	public static ARecordInvariantType getRecord(AParameterType type) {
		return new ARecordInvariantType(type.getLocation(),false, new LexNameToken("?", "?", type.getLocation()), new Vector<AFieldField>());
	}

	public static boolean isClass(AParameterType type) {
		return true;
	}
	
	public static AClassType getClassType(AParameterType type) {
		return new AClassType(type.getLocation(),false, null, 
				new AClassClassDefinition(
						new LexLocation(),
						new LexNameToken("CLASS", "DEFAULT", new LexLocation()),
				null, 
				false, 
				null, 
				null, 
				null, 
				null, new LexNameList(), new Vector<PDefinition>(), null, null, null, null, null, null, null, null, null, null, null, null, null));
	}

	public static boolean isProduct(AParameterType type) {
		return true;
	}

	public static boolean narrowerThan(AParameterType type,
			PAccessSpecifier accessSpecifier) {		
		return false;
	}

	public static PType polymorph(AParameterType type, LexNameToken pname,
			PType actualType) {
		return (type.getName().equals(pname)) ? actualType : type;
	}

}
