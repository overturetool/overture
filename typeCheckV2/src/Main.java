import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.tokens.TBoolLiteral;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.node.tokens.TLexnametoken;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overture.ast.patterns.AIdentifierPatternPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckVisitor;
import org.overturetool.vdmj.lex.LexLocation;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LexLocation loc = new LexLocation();
		
		TNumbersLiteral number2 = new TNumbersLiteral("2");
		TNumbersLiteral number5 = new TNumbersLiteral("5");
		
		ABooleanConstExp bool_true = new ABooleanConstExp(null, new TBoolLiteral("true"));
		
		AIfExp ifExp = new AIfExp(null, bool_true, new AIntConstExp(null,number2), null, new AIntConstExp(null,number5));
		
		List<PPattern> paramPatternList = new ArrayList<PPattern>();
		paramPatternList.add(new AIdentifierPatternPattern(loc, new TLexnametoken("a")));
		List<PType> funcParamType = new ArrayList<PType>();
		funcParamType.add(new AIntNumericBasicType());
		
		AExplicitFunctionDefDefinition foo = new AExplicitFunctionDefDefinition(loc, 
				new TLexnametoken("foo"), 
				null, 
				new AFunctionType(loc, new ABooleanConstExp(null, new TBoolLiteral("false")), new AIntNumericBasicType(loc,new TInt("int")) , funcParamType), //type 
				paramPatternList, 
				null, 
				null, //postcondition 
				ifExp, 
				new ABooleanConstExp(null,new TBoolLiteral("false")), //isTypeInvariant
				null, 
				new ABooleanConstExp(null,new TBoolLiteral("false"))); //isCuried
		
		List<PDefinition> defs = new ArrayList<PDefinition>();
		defs.add(foo);
		
		AModuleModules module = new AModuleModules(new TLexnametoken("A"), defs );
		
		
		module.apply(new TypeCheckVisitor(), null);

	}

}
