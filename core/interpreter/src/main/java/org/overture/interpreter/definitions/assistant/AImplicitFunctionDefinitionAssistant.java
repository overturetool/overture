package org.overture.interpreter.definitions.assistant;


import java.util.Iterator;
import java.util.List;

import org.overture.interpreter.ast.definitions.AImplicitFunctionDefinitionInterpreter;
import org.overture.interpreter.ast.types.AFunctionTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overture.interpreter.types.assistant.PTypeInterpreterAssistant;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;


public class AImplicitFunctionDefinitionAssistant {

	public static AFunctionTypeInterpreter getType(AImplicitFunctionDefinitionInterpreter impdef, List<PTypeInterpreter> actualTypes)
	{		
		Iterator<PTypeInterpreter> ti = actualTypes.iterator();
		AFunctionTypeInterpreter ftype = (AFunctionTypeInterpreter)impdef.getType();

		for (LexNameToken pname: impdef.getTypeParams())
		{
			PTypeInterpreter ptype = ti.next();
			//AFunctionTypeAssistent.
			ftype = (AFunctionTypeInterpreter)PTypeInterpreterAssistant.polymorph(ftype,pname, ptype);
		}

		return ftype;
	}

//	public static List<PDefinition> getTypeParamDefinitions(
//			AImplicitFunctionDefinition node) {
//		
//		List<PDefinition> defs = new ArrayList<PDefinition>();
//
//		for (LexNameToken pname: node.getTypeParams())
//		{
//			PDefinition p = new ALocalDefinition(
//				pname.location, pname, NameScope.NAMES,false,null, null, new AParameterType(null,false,null,pname),false);
//
//			PDefinitionAssistant.markUsed(p);
//			defs.add(p);
//		}
//
//		return defs;
//	}
//
//	public static PDefinition findName(AImplicitFunctionDefinition d,
//			LexNameToken sought, NameScope scope) {
//		
//		if (PDefinitionAssistant.findNameBaseCase(d, sought, scope) != null)
//		{
//			return d;
//		}
//
//		PDefinition predef = d.getPredef();
//		if (predef != null && PDefinitionAssistant.findName(predef, sought, scope) != null)
//		{
//			return predef;
//		}
//
//		PDefinition postdef = d.getPostdef();
//		if (postdef != null && PDefinitionAssistant.findName(postdef,sought, scope) != null)
//		{
//			return postdef;
//		}
//
//		return null;
//	}
//
//	public static List<PDefinition> getDefinitions(AImplicitFunctionDefinition d) {
//		List<PDefinition> defs = new Vector<PDefinition>();
//		defs.add(d);
//
//		if (d.getPredef() != null)
//		{
//			defs.add(d.getPredef());
//		}
//
//		if (d.getPostdef() != null)
//		{
//			defs.add(d.getPostdef());
//		}
//
//		return defs;
//	}
//
//	public static LexNameList getVariableNames(AImplicitFunctionDefinition d) {
//		return new LexNameList(d.getName());
//	}
//
//	public static void typeResolve(AImplicitFunctionDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) {
//		
//		if (d.getTypeParams() != null)
//		{
//			FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
//				AImplicitFunctionDefinitionAssistant.getTypeParamDefinitions(d), question.env, NameScope.NAMES);
//
//			TypeCheckInfo newQuestion = new TypeCheckInfo();
//			newQuestion.env = params;
//			
//			
//			d.setType(d.getType().apply(rootVisitor, question));;
//		}
//		else
//		{
//			question.qualifiers = null;
//			d.setType(d.getType().apply(rootVisitor, question));
//		}
//
//		if (d.getResult() != null)
//		{
//			APatternTypePairAssistant.typeResolve(d.getResult(),rootVisitor,question);
//		}
//
//		if (question.env.isVDMPP())
//		{
//			d.getName().setTypeQualifier(d.getType().getParameters());
//
//			if (d.getBody() instanceof ASubclassResponsibilityExp)
//			{
//				d.getClassDefinition().setIsAbstract(true);
//			}
//		}
//
//		if (d.getBody() instanceof ASubclassResponsibilityExp ||
//				d.getBody() instanceof ANotYetSpecifiedExp)
//		{
//			d.getClassDefinition().setIsUndefined(true);
//		}
//
//		if (d.getPrecondition() != null)
//		{
//			PDefinitionAssistant.typeResolve(d.getPredef(), rootVisitor, question);
//		}
//
//		if (d.getPostcondition() != null)
//		{
//			PDefinitionAssistant.typeResolve(d.getPostdef(), rootVisitor, question);
//		}
//
//		for (APatternListTypePair pltp: d.getParamPatterns())
//		{
//			APatternListTypePairAssistant.typeResolve(pltp,rootVisitor,question);
//		}
//		
//	}
//
//	public static void implicitDefinitions(AImplicitFunctionDefinition d,
//			Environment env) {
//		// TODO Auto-generated method stub
//		
//	}
}
