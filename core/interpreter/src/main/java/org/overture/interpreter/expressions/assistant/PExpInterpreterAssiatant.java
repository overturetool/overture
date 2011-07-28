package org.overture.interpreter.expressions.assistant;

import java.util.List;

import org.overture.interpreter.ast.expressions.PExpInterpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;

public class PExpInterpreterAssiatant
{

	public static List<Value> getValues(PExpInterpreter exp, ObjectContext ctxt)
	{
		switch(exp.kindPExpInterpreter())
		{
			case APPLY:
				break;
			case BINARY:
				break;
			case BOOLEANCONST:
				break;
			case BREAKPOINT:
				break;
			case CASES:
				break;
			case CHARLITERAL:
				break;
			case DEF:
				break;
			case ELSEIF:
				break;
			case EXISTS:
				break;
			case EXISTS1:
				break;
			case FIELD:
				break;
			case FIELDNUMBER:
				break;
			case FORALL:
				break;
			case FUNCINSTATIATION:
				break;
			case HISTORY:
				break;
			case IF:
				break;
			case INTLITERAL:
				break;
			case IOTA:
				break;
			case IS:
				break;
			case ISOFBASECLASS:
				break;
			case ISOFCLASS:
				break;
			case LAMBDA:
				break;
			case LETBEST:
				break;
			case LETDEF:
				break;
			case MAP:
				break;
			case MAPLET:
				break;
			case MKBASIC:
				break;
			case MKTYPE:
				break;
			case MU:
				break;
			case NEW:
				break;
			case NIL:
				break;
			case NOTYETSPECIFIED:
				break;
			case POSTOP:
				break;
			case PRE:
				break;
			case PREOP:
				break;
			case QUOTELITERAL:
				break;
			case REALLITERAL:
				break;
			case SAMEBASECLASS:
				break;
			case SAMECLASS:
				break;
			case SELF:
				break;
			case SEQ:
				break;
			case SET:
				break;
			case STATEINIT:
				break;
			case STRINGLITERAL:
				break;
			case SUBCLASSRESPONSIBILITY:
				break;
			case SUBSEQ:
				break;
			case THREADID:
				break;
			case TIME:
				break;
			case TUPLE:
				break;
			case UNARY:
				break;
			case UNDEFINED:
				break;
			case VARIABLE:
				break;
			
		}
	}

}
