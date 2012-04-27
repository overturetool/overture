package org.overture.interpreter.expressions.assistant;

import java.util.List;

import org.overture.interpreter.ast.expressions.AApplyExpInterpreter;
import org.overture.interpreter.ast.expressions.ACaseAlternativeInterpreter;
import org.overture.interpreter.ast.expressions.ACasesExpInterpreter;
import org.overture.interpreter.ast.expressions.AElseIfExpInterpreter;
import org.overture.interpreter.ast.expressions.AExists1ExpInterpreter;
import org.overture.interpreter.ast.expressions.AExistsExpInterpreter;
import org.overture.interpreter.ast.expressions.AFieldExpInterpreter;
import org.overture.interpreter.ast.expressions.AFieldNumberExpInterpreter;
import org.overture.interpreter.ast.expressions.AForAllExpInterpreter;
import org.overture.interpreter.ast.expressions.AFuncInstatiationExpInterpreter;
import org.overture.interpreter.ast.expressions.AIfExpInterpreter;
import org.overture.interpreter.ast.expressions.AIotaExpInterpreter;
import org.overture.interpreter.ast.expressions.AIsExpInterpreter;
import org.overture.interpreter.ast.expressions.AIsOfClassExpInterpreter;
import org.overture.interpreter.ast.expressions.ALambdaExpInterpreter;
import org.overture.interpreter.ast.expressions.ALetBeStExpInterpreter;
import org.overture.interpreter.ast.expressions.ALetDefExpInterpreter;
import org.overture.interpreter.ast.expressions.AMapletExpInterpreter;
import org.overture.interpreter.ast.expressions.AMkBasicExpInterpreter;
import org.overture.interpreter.ast.expressions.AMkTypeExpInterpreter;
import org.overture.interpreter.ast.expressions.AMuExpInterpreter;
import org.overture.interpreter.ast.expressions.ANewExpInterpreter;
import org.overture.interpreter.ast.expressions.ARecordModifierInterpreter;
import org.overture.interpreter.ast.expressions.ASameBaseClassExpInterpreter;
import org.overture.interpreter.ast.expressions.ASameClassExpInterpreter;
import org.overture.interpreter.ast.expressions.ASubseqExpInterpreter;
import org.overture.interpreter.ast.expressions.ATupleExpInterpreter;
import org.overture.interpreter.ast.expressions.PExpInterpreter;
import org.overture.interpreter.ast.expressions.SBinaryExpInterpreter;
import org.overture.interpreter.ast.expressions.SUnaryExpInterpreter;
import org.overture.interpreter.ast.patterns.PMultipleBindInterpreter;
import org.overture.interpreter.definitions.assistant.PDefinitionInterpreterAssistant;
import org.overture.interpreter.patterns.assistant.PBindInterpreterAssistant;
import org.overture.interpreter.patterns.assistant.PMultipleBindInterpreterAssistant;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class PExpInterpreterAssiatant
{
	
	public static List<Value> getValues(PExpInterpreter exp, ObjectContext ctxt)
	{
		switch(exp.kindPExpInterpreter())
		{
			case APPLY:
				{
				AApplyExpInterpreter e = (AApplyExpInterpreter) exp;
				
				ValueList list = PExpInterpreterAssiatant.getValues(e.getArgs(), ctxt);
				list.addAll(PExpInterpreterAssiatant.getValues(e.getRoot(), ctxt));
				return list;	
				}	
			case BINARY:
				{
					SBinaryExpInterpreter e = (SBinaryExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getLeft(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getRight(), ctxt));
					return list;					
				}
			case BOOLEANCONST:
				
				break;
				
			case BREAKPOINT:
				
				break;
			case CASES:
				{
					ACasesExpInterpreter e = (ACasesExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getExpression(), ctxt);

					for (ACaseAlternativeInterpreter c: e.getCases())
					{
						list.addAll(ACaseAlternativeInterpreterAssistant.getValues(c, ctxt));
					} 

					if (e.getOthers() != null)
					{
						list.addAll(PExpInterpreterAssiatant.getValues(e.getOthers(), ctxt));
					}

					return list;
				}
			case CHARLITERAL:
				
				break;
			case DEF:
				
				break;
			case ELSEIF:
				{
					AElseIfExpInterpreter e = (AElseIfExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getElseIf(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getThen(), ctxt));
					return list;
				}
			case EXISTS:
				{
					AExistsExpInterpreter e = (AExistsExpInterpreter) exp;
					ValueList list = new ValueList();

					for (PMultipleBindInterpreter mb: e.getBindList())
					{
						list.addAll(PMultipleBindInterpreterAssistant.getValues(mb, ctxt));
					}

					list.addAll(PExpInterpreterAssiatant.getValues(e.getPredicate(), ctxt));
					return list;
				}
			case EXISTS1:
				{
					AExists1ExpInterpreter e = (AExists1ExpInterpreter) exp;
					List<Value> list = PBindInterpreterAssistant.getValues(e.getBind(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getPredicate(), ctxt));
					return list;
				}
			case FIELD:
				{
					AFieldExpInterpreter e = (AFieldExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getObject(), ctxt);
				}
			case FIELDNUMBER:
				{
					AFieldNumberExpInterpreter e = (AFieldNumberExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getTuple(), ctxt);
				}
			case FORALL:
				{
					AForAllExpInterpreter e = (AForAllExpInterpreter) exp;
					ValueList list = new ValueList();

					for (PMultipleBindInterpreter mb: e.getBindList())
					{
						list.addAll(PMultipleBindInterpreterAssistant.getValues(mb, ctxt));
					}

					list.addAll(PExpInterpreterAssiatant.getValues(e.getPredicate(), ctxt));
					return list;
				}
			case FUNCINSTATIATION:
				{
					AFuncInstatiationExpInterpreter e = (AFuncInstatiationExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getFunction(), ctxt);
				}
			case HISTORY:
				
				break;
			case IF:
				{
					AIfExpInterpreter e = (AIfExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getTest(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getTest(), ctxt));

					for (AElseIfExpInterpreter elif: e.getElseList())
					{
						list.addAll(PExpInterpreterAssiatant.getValues(elif, ctxt));
					}

					if (e.getElse()!= null)
					{
						list.addAll(PExpInterpreterAssiatant.getValues(e.getElse(),ctxt));
					}

					return list;
				}
			case INTLITERAL:
				
				break;
			case IOTA:
				{	
					AIotaExpInterpreter e = (AIotaExpInterpreter) exp;
					List<Value> list = PBindInterpreterAssistant.getValues(e.getBind(),ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getPredicate(), ctxt));
					return list;
				}
			case IS:
				{
					AIsExpInterpreter e = (AIsExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getTest(), ctxt);
				}
			case ISOFBASECLASS:
				
				break;
			case ISOFCLASS:
				{
					AIsOfClassExpInterpreter e = (AIsOfClassExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getExp(), ctxt);
				}
			case LAMBDA:
				{
					ALambdaExpInterpreter e = (ALambdaExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getExpression(), ctxt);
				}
			case LETBEST:
				{
					ALetBeStExpInterpreter e = (ALetBeStExpInterpreter) exp;
					List<Value> list = PMultipleBindInterpreterAssistant.getValues(e.getBind(),ctxt);

					if (e.getSuchThat()!= null)
					{
						list.addAll(PExpInterpreterAssiatant.getValues(e.getSuchThat(), ctxt));
					}

					list.addAll(PExpInterpreterAssiatant.getValues(e.getValue(),ctxt));
					return list;
				}
			case LETDEF:
				{
					ALetDefExpInterpreter e = (ALetDefExpInterpreter) exp;
					ValueList list = PDefinitionInterpreterAssistant.getValues(e.getLocalDefs(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getExpression(), ctxt));
					return list;
				}
			case MAP:
				{
						
				}
				break;
			case MAPLET:
				{
					AMapletExpInterpreter e = (AMapletExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getLeft(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getRight(), ctxt));
					return list;
					
				}
			case MKBASIC:
				{
					AMkBasicExpInterpreter e = (AMkBasicExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getArg(), ctxt);
				}
			case MKTYPE:
				{
					AMkTypeExpInterpreter e = (AMkTypeExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getArgs(), ctxt);
				}
			case MU:
				{
					AMuExpInterpreter e = (AMuExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getRecord(), ctxt);

					for (ARecordModifierInterpreter rm: e.getModifiers())
					{
						list.addAll(ARecordModifierInterpreterAssistant.getValues(rm ,ctxt));
					}

					return list;
				}
			case NEW:
				{
					ANewExpInterpreter e = (ANewExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getArgs(), ctxt);
				}
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
				{
					ASameBaseClassExpInterpreter e = (ASameBaseClassExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getLeft(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getRight(), ctxt));
					return list;
				}
			case SAMECLASS:
				{
					ASameClassExpInterpreter e = (ASameClassExpInterpreter) exp;
					List<Value> list = PExpInterpreterAssiatant.getValues(e.getLeft(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getRight(), ctxt));
					return list;
				}
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
				{
					ASubseqExpInterpreter e = (ASubseqExpInterpreter) exp;

					List<Value> list = PExpInterpreterAssiatant.getValues(e.getSeq(), ctxt);
					list.addAll(PExpInterpreterAssiatant.getValues(e.getFrom(), ctxt));
					list.addAll(PExpInterpreterAssiatant.getValues(e.getTo(), ctxt));
					return list;
				}
			case THREADID:
				
				break;
			case TIME:
				
				break;
			case TUPLE:
				{
					ATupleExpInterpreter e = (ATupleExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getArgs(), ctxt);
				}
			case UNARY:
				{
					SUnaryExpInterpreter e = (SUnaryExpInterpreter) exp;
					return PExpInterpreterAssiatant.getValues(e.getExp(), ctxt);
				}
			case UNDEFINED:
				
				break;
			case VARIABLE:
				
				break;
			
		}
		return new ValueList();  // Default, for expressions with no variables
	}


	public static ValueList getValues(List<PExpInterpreter> args,
			ObjectContext ctxt) {
		
		  ValueList list = new ValueList();

		  for (PExpInterpreter exp: args)
		  {
		    list.addAll(PExpInterpreterAssiatant.getValues(exp, ctxt));
		  }

		return list;
	}
}
