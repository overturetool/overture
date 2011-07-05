import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.NodeList;
import org.overture.ast.node.tokens.TBoolLiteral;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckVisitor;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIdentifierTokenImpl;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexNameTokenImpl;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class Main {

	/** (a -> b) * (a -> b) * b -> b
	 * @param args
	 */
	public static void main(String[] args) {
		LexLocation loc = new LexLocation();
		
		TNumbersLiteral number2 = new TNumbersLiteral("2");
		TNumbersLiteral number5 = new TNumbersLiteral("5");
		
		ABooleanConstExp bool_true = new ABooleanConstExp(null, loc, new TBoolLiteral("true"));
		
		AIfExp ifExp = new AIfExp(null, loc, bool_true, new AIntConstExp(null,loc, number2), null, new AIntConstExp(null,loc, number5));
		
		List<PPattern> paramPatternList = new ArrayList<PPattern>();
		paramPatternList.add(new AIdentifierPattern(loc, null,
				new LexNameTokenImpl("A",new LexIdentifierTokenImpl("a",false,null))));
			
		
		List<PType> funcParamType = new ArrayList<PType>();
		funcParamType.add(new AIntNumericBasicType());
		
		AExplicitFunctionDefinition foo = new AExplicitFunctionDefinition(
				loc, //location
				new LexNameTokenImpl("A","foo",new LexLocation()), //lexnametoken 
				NameScope.GLOBAL, //namescope 
				new Boolean(false), //used
				null, //classdefinition
				null, // type
				null, //type params
				new AFunctionType(loc, new ABooleanConstExp(null, loc, new TBoolLiteral("false")), new AIntNumericBasicType(loc,new TInt("int")) , funcParamType), //functiontype 
				null,//paramPatternList, //paramPatternList
				null, //pre
				null, //predef
				null, //postcondition
				null, //postdef
				ifExp, //body
				null, //actual result
				false,//isTypeInvariant 
				null, //measure
				null, //measuredef
				false, //recursive
				0, //mesure lexical
				false); //isCuried
		
		List<PDefinition> defs = new ArrayList<PDefinition>();
		defs.add(foo);
		
		AModuleModules module = new AModuleModules(new LexNameTokenImpl("A", new LexIdentifierTokenImpl("A", false, loc)), defs );
		
		
		module.apply(new TypeCheckVisitor(), null);

	}

}
