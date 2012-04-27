//import java.util.ArrayList;
//import java.util.List;
//
//import org.overture.ast.definitions.AExplicitFunctionDefinition;
//import org.overture.ast.definitions.PDefinition;
//import org.overture.ast.expressions.ABooleanConstExp;
//import org.overture.ast.expressions.AIfExp;
//import org.overture.ast.expressions.AIntConstExp;
//import org.overture.ast.modules.AModuleModules;
//import org.overture.ast.patterns.AIdentifierPattern;
//import org.overture.ast.patterns.APatternInnerListPatternList;
//import org.overture.ast.patterns.PPattern;
//import org.overture.ast.types.AFunctionType;
//import org.overture.ast.types.AIntNumericBasicType;
//import org.overture.ast.types.PType;
//import org.overture.typecheck.ModuleEnvironment;
//import org.overture.typecheck.TypeCheckInfo;
//import org.overture.typecheck.visitors.TypeCheckVisitor;
//import org.overturetool.vdmj.lex.LexBooleanToken;
//import org.overturetool.vdmj.lex.LexIdentifierToken;
//import org.overturetool.vdmj.lex.LexIntegerToken;
//import org.overturetool.vdmj.lex.LexLocation;
//import org.overturetool.vdmj.lex.LexNameToken;
//import org.overturetool.vdmj.typechecker.NameScope;
//
//
//public class Main {
//
//	/** (a -> b) * (a -> b) * b -> b
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		LexLocation loc = new LexLocation();
//		
//		LexIntegerToken number2 = new LexIntegerToken(2,loc);
//		LexIntegerToken number5 = new LexIntegerToken(5,loc);
//		
//		ABooleanConstExp bool_true = new ABooleanConstExp(null, loc, new LexBooleanToken(true, loc));
//		
//		AIfExp ifExp = new AIfExp(null, loc, bool_true, new AIntConstExp(null,loc, number2), null, new AIntConstExp(null,loc, number5));
//		
//		List<PPattern> innerListPattern = new ArrayList<PPattern>();
//		innerListPattern.add(new AIdentifierPattern(loc, null,
//				new LexNameToken("A",new LexIdentifierToken("a",false,null))));
//		
//		List<APatternInnerListPatternList> paramPatternList = new ArrayList<APatternInnerListPatternList>();
//		paramPatternList.add(new APatternInnerListPatternList( innerListPattern));
//			
//		
//		List<PType> funcParamType = new ArrayList<PType>();
//		funcParamType.add(new AIntNumericBasicType());
//		
//		AExplicitFunctionDefinition foo = new AExplicitFunctionDefinition(
//				loc, //location
//				new LexNameToken("A","foo",new LexLocation()), //lexnametoken 
//				NameScope.GLOBAL, //namescope 
//				new Boolean(false), //used
//				null, //classdefinition
//				null, // type
//				new ArrayList<LexNameToken>(),  //type params
//				new AFunctionType(loc,false, false, funcParamType , new AIntNumericBasicType(loc,true)), //functiontype 
//				paramPatternList, //paramPatternList
//				null, //pre
//				null, //predef
//				null, //postcondition
//				null, //postdef
//				ifExp, //body
//				null, //actual result
//				false,//isTypeInvariant 
//				null, //measure
//				null, //measuredef
//				false, //recursive
//				0, //mesure lexical
//				false); //isCuried
//		
//		List<PDefinition> defs = new ArrayList<PDefinition>();
//		defs.add(foo);
//		
//		AModuleModules module = new AModuleModules(new LexNameToken("A", new LexIdentifierToken("A", false, loc)), null, null, defs );
//		
//		TypeCheckInfo tci = new TypeCheckInfo();
//		tci.scope = NameScope.NAMES;
//		tci.env = new ModuleEnvironment(module);
//		
//		PType tcheckResult = module.apply(TypeCheckVisitor.getInstance(), tci);	
//		System.out.println("Finished");
//	}
//
//}
