package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.typechecker.NameScope;


public class AParameterTypeAssistant {

	public static PType typeResolve(AParameterType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else type.setResolved(true);

		PDefinition p = question.env.findName(type.getName(), NameScope.NAMES);

		if (p == null || !(p.getType() instanceof AParameterType))
		{
			TypeCheckerErrors.report(3433, "Parameter type @" + type.getName() + " not defined",p.getLocation(),p);
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

}
