package org.overturetool.ast.factory;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.syntax.ParserException;

public class AstFactoryPS extends AstFactory {

	public static SClassDefinition newACpuClassDefinition() throws ParserException, LexException {
		ACpuClassDefinition result = new ACpuClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "CPU", new LexLocation()),
				new LexNameList(),
				ACpuClassDefinitionAssitantPS.operationDefs());
		
		return result;
	}

	public static SClassDefinition newABusClassDefinition() throws ParserException, LexException {
		ABusClassDefinition result = new ABusClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "BUS", new LexLocation()),
				new LexNameList(),
				ABusClassDefinitionAssitantPS.operationDefs());
		
		result.setInstance(result);
		
		return result;
	}
	
}
