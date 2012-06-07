package org.overture.ast.types.assistants;

import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AParameterTypeAssistantTC extends AParameterTypeAssistant {

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
			tl.add(AstFactory.newAUnknownType(type.getLocation()));
		}

		return AstFactory.newAProductType(type.getLocation(), tl);
	}

	public static AProductType getProduct(AProductType type) {
		return AstFactory.newAProductType(type.getLocation(),new NodeList<PType>(null));
	}

	public static boolean isType(AParameterType b,
			Class<? extends PType> typeclass) {
		return true;
	}

	public static PType isType(AParameterType exptype, String typename) {
		return AstFactory.newAUnknownType(exptype.getLocation());
	}

	public static boolean equals(AParameterType type, Object other) {
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
		return AstFactory.newASeqSeqType(type.getLocation()); //empty
	}

	public static boolean isMap(AParameterType type) {
		return true;
	}

	public static SMapType getMap(AParameterType type) {
		return AstFactory.newAMapMapType(type.getLocation());	// Unknown |-> Unknown	
	}

	public static boolean isSet(AParameterType type) {
		return true;
	}

	public static ASetType getSet(AParameterType type) {
		return AstFactory.newASetType(type.getLocation()); //empty
	}

	public static boolean isRecord(AParameterType type) {
		return true;
	}
	
	public static ARecordInvariantType getRecord(AParameterType type) {
		return AstFactory.newARecordInvariantType(type.getLocation(), new Vector<AFieldField>());				
	}

	public static boolean isClass(AParameterType type) {
		return true;
	}
	
	public static AClassType getClassType(AParameterType type) {
		return AstFactory.newAClassType(type.getLocation(),AstFactory.newAClassClassDefinition());
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
