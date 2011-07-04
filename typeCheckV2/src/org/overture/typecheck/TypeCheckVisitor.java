package org.overture.typecheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.Node;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.APatternInnerListPatternList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.PPatternList;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.HelperDefinition;

import org.overture.runtime.TypeList;
import org.overturetool.vdmj.lex.LexLocation;







public class TypeCheckVisitor  extends QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	@Override
	public PType defaultNode(Node node, TypeCheckInfo question) {
		
		return super.defaultNode(node, question);
	}
	
	
	@Override
	public PType caseAModuleModules(AModuleModules node, TypeCheckInfo question) {
		System.out.println("Visiting Module: "+ node.getName().getText());
		for (PDefinition def : node.getDefs()) {
			def.apply(this, null);
		}
		
		return null;
	}
	
	
	
	
	@Override
	public PType caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, TypeCheckInfo question) {
		System.out.println("Visiting Explicit Function Def: " + node.getName().getText());
		
		NodeList<PDefinition> defs = new NodeList<PDefinition>(node);

		if (node.getTypeParams() != null)
		{
			//defs.addAll(getTypeParamDefinitions());
		}

		PType expectedResult = checkParams(node.getParamPatternList().listIterator(), node.getType());

		List<List<PDefinition>> paramDefinitionList = getParamDefinitions(node.getType(), node.getParamPatternList(),node.getLocation());

		for (List<PDefinition> pdef: paramDefinitionList)
		{
			defs.addAll(pdef);	// All definitions of all parameter lists
		}

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs,question.env, question.scope);
		
		//TODO: access specifier not defined
//		local.setStatic(accessSpecifier);
		local.setEnclosingDefinition(node);

		//building the new scope for subtypechecks
		TypeCheckInfo info = new TypeCheckInfo();
		info.env = local;
		info.scope = question.scope;
		info.qualifiers = question.qualifiers;
		HelperDefinition.typeCheck(defs,info,this);

		if (question.env.isVDMPP()) //TODO:Access specifier: && !accessSpecifier.isStatic)
		{
			local.add(HelperDefinition.getSelfDefinition(node));
		}
 
		if (node.getPredef() != null)
		{
			PType b = node.getPredef()..body.typeCheck(local, null, NameScope.NAMES);
			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Precondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		if (postdef != null)
		{
			LexNameToken result = new LexNameToken(name.module, "RESULT", location);
			Pattern rp = new IdentifierPattern(result);
			DefinitionList rdefs = rp.getDefinitions(expectedResult, NameScope.NAMES);
			FlatCheckedEnvironment post =
				new FlatCheckedEnvironment(rdefs, local, NameScope.NAMES);

			Type b = postdef.body.typeCheck(post, null, NameScope.NAMES);
			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Postcondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		// This check returns the type of the function body in the case where
		// all of the curried parameter sets are provided.

		actualResult = body.typeCheck(local, null, scope);

		if (!TypeComparator.compatible(expectedResult, actualResult))
		{
			report(3018, "Function returns unexpected type");
			detail2("Actual", actualResult, "Expected", expectedResult);
		}

		if (type.narrowerThan(accessSpecifier))
		{
			report(3019, "Function parameter visibility less than function definition");
		}

		if (measure == null && recursive)
		{
			warning(5012, "Recursive function has no measure");
		}
		else if (measure != null)
		{
			if (base.isVDMPP()) measure.setTypeQualifier(type.parameters);
			measuredef = base.findName(measure, scope);

			if (measuredef == null)
			{
				measure.report(3270, "Measure " + measure + " is not in scope");
			}
			else if (!(measuredef instanceof ExplicitFunctionDefinition))
			{
				measure.report(3271, "Measure " + measure + " is not an explicit function");
			}
			else if (measuredef == this)
			{
				measure.report(3304, "Recursive function cannot be its own measure");
			}
			else
			{
				ExplicitFunctionDefinition efd = (ExplicitFunctionDefinition)measuredef;
				
				if (this.typeParams == null && efd.typeParams != null)
				{
					measure.report(3309, "Measure must not be polymorphic");
				}
				else if (this.typeParams != null && efd.typeParams == null)
				{
					measure.report(3310, "Measure must also be polymorphic");
				}
				
				FunctionType mtype = (FunctionType)measuredef.getType();

				if (!TypeComparator.compatible(mtype.parameters, type.parameters))
				{
					measure.report(3303, "Measure parameters different to function");
					detail2(measure.name, mtype.parameters, name.name, type.parameters);
				}

				if (!(mtype.result instanceof NaturalType))
				{
					if (mtype.result.isProduct())
					{
						ProductType pt = mtype.result.getProduct();

						for (Type t: pt.types)
						{
							if (!(t instanceof NaturalType))
							{
								measure.report(3272,
									"Measure range is not a nat, or a nat tuple");
								measure.detail("Actual", mtype.result);
								break;
							}
						}

						measureLexical = pt.types.size();
					}
					else
					{
						measure.report(3272,
							"Measure range is not a nat, or a nat tuple");
						measure.detail("Actual", mtype.result);
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp) &&
			!(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}
	}
	
	
	private PType checkParams(ListIterator<APatternInnerListPatternList> plists, AFunctionType ftype)
	{
		NodeList<PType> ptypes = ftype.getParameters();
		APatternInnerListPatternList patterns = plists.next();

		if (patterns.getList().size() > ptypes.size())
		{
			report(3020, "Too many parameter patterns");
			detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		}
		else if (patterns.getList().size() < ptypes.size())
		{
			report(3021, "Too few parameter patterns");
			detail2("Pattern(s)", patterns, "Type(s)", ptypes);
			return ftype.getResult();
		}

		if (ftype.getResult() instanceof AFunctionType)
		{
			if (!plists.hasNext())
			{
				// We're returning the function itself
				return ftype.getResult();
			}

			// We're returning what the function returns, assuming we
			// pass the right parameters. Note that this recursion
			// means that we finally return the result of calling the
			// function with *all* of the curried argument sets applied.
			// This is because the type check of the body determines
			// the return type when all of the curried parameters are
			// provided.

			return checkParams(plists, (AFunctionType)ftype.getResult());
		}

		if (plists.hasNext())
		{
			report(3022, "Too many curried parameters");
		}

		return ftype.getResult();
	}
	
	private List<List<PDefinition>> getParamDefinitions(AFunctionType type, NodeList<APatternInnerListPatternList> paramPatternList, LexLocation location)
	{
		List<List<PDefinition>> defList = new ArrayList<List<PDefinition>>(); //new Vector<DefinitionList>();
		AFunctionType ftype = type;	// Start with the overall function
		Iterator<APatternInnerListPatternList> piter = paramPatternList.iterator();

		while (piter.hasNext())
		{
			APatternInnerListPatternList plist = piter.next();
			Set<PDefinition> defs = new HashSet<PDefinition>(); 
			NodeList<PType> ptypes = ftype.getParameters();
			Iterator<PType> titer = ptypes.iterator();

			if (plist.getList().size() != ptypes.size())
			{
				// This is a type/param mismatch, reported elsewhere. But we
				// have to create definitions to avoid a cascade of errors.

				PType unknown = new AUnknownType(location);

				for (PPattern p: plist.getList())
				{
					//TODO: getDefinitions is different
					PatternHelper.getDefinitions(p,unknown,NameScope.LOCAL);
					//defs.addAll(p.getDefinitions(unknown, NameScope.LOCAL));
				}
			}
			else
			{
    			for (PPattern p: plist.getList())
    			{
    				//TODO: getDefinitions is different
					//defs.addAll(p.getDefinitions(titer.next(), NameScope.LOCAL));
    			}
			}

			
			defList.add(new ArrayList<PDefinition>(defs));

			if (ftype.getResult() instanceof AFunctionType)	// else???
			{
				ftype = (AFunctionType)ftype.getResult();
			}
		}

		return defList;
	}
	
	public void report(int number, String msg)
	{
		System.out.println("Error " + number + ": "+  msg);
	}
	
	public void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		System.out.println("Detail2: " + tag1 + obj1 + tag2 + obj2);
	}


}


