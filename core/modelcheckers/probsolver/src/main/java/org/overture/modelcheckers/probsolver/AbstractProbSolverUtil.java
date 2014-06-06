package org.overture.modelcheckers.probsolver;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.overture.ast.assistant.AstAssistantFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.IAnimator;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.LoadBProjectFromStringCommand;
import de.prob.animator.command.SetPreferenceCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.animator.domainobjects.AbstractEvalElement;
import de.prob.webconsole.ServletContextListener;

public class AbstractProbSolverUtil
{

	public static class SolverException extends Exception
	{

		/**
		 * Generated serial
		 */
		private static final long serialVersionUID = 1L;

		public SolverException(String message)
		{
			super(message);
		}

		public SolverException(String message, Throwable reason)
		{
			super(message, reason);
		}

	}

	protected static class VdmContext
	{
		/**
		 * Sorted list of return values matching the mk_(...) ret that will be generated
		 */
		public final List<String> resultIds;
		public final AbstractEvalElement solverInput;
		public final List<ILexNameToken> stateIds;
		public final Map<String, PType> types;

		public VdmContext(AbstractEvalElement solverInput,
				Map<String, PType> types, List<ILexNameToken> stateIds,
				List<String> resultIds)
		{
			this.solverInput = solverInput;
			this.types = types;
			this.stateIds = stateIds;
			this.resultIds = resultIds;
		}

		public ILexNameToken getStateId(String id)
		{
			for (ILexNameToken stateId : stateIds)
			{
				if (stateId.getName().equals(id))
				{
					return stateId;
				}
			}
			return null;
		}

		public boolean hasId(String id)
		{
			return resultIds.contains(id) || isState(id);
		}

		public boolean isResult(String id)
		{
			return resultIds.contains(id);
		}

		public boolean isState(String id)
		{
			return getStateId(id) != null;
		}
	}

	protected static class VdmExpContext extends VdmContext
	{

		public VdmExpContext(AbstractEvalElement solverInput,
				Map<String, PType> types, List<ILexNameToken> stateIds,
				List<String> resultIds)
		{
			super(solverInput, types, stateIds, resultIds);
		}

	}

	protected static class VdmPpContext extends VdmContext
	{

		public VdmPpContext(AbstractEvalElement formula,
				List<ILexNameToken> stateIds, Map<String, PType> stateTypes,
				List<String> resultIds, Map<String, PType> resultTypes)
		{
			super(formula, resultTypes, stateIds, resultIds);
			this.types.putAll(stateTypes);
			this.types.putAll(resultTypes);
		}

		public VdmPpContext(AbstractEvalElement solverInput,
				Map<String, PType> types, List<ILexNameToken> stateIds,
				List<String> resultIds)
		{
			super(solverInput, types, stateIds, resultIds);
		}

	}

	protected static class VdmSlContext extends VdmContext
	{

		public VdmSlContext(AbstractEvalElement solverInput,
				ILexNameToken stateId, ARecordInvariantType stateType,
				List<String> resultIds, Map<String, PType> resultTypes)
		{
			super(solverInput, resultTypes, Arrays.asList(new ILexNameToken[] { stateId }), resultIds);
			this.types.put(stateId.getName(), stateType);
			this.types.putAll(resultTypes);
		}

		public ILexNameToken getStateId()
		{
			return this.stateIds.iterator().next();
		}

		public ARecordInvariantType getStateType()
		{
			return (ARecordInvariantType) this.types.get(getStateId().getName());
		}
	}

	protected static IAnimator animator;
	private static String anomatorSets;

	protected final SolverConsole console;
	
	/**
	 * Create the assistant factory for use in the subclasses.
	 */
	public final AstAssistantFactory assistantFactory = new AstAssistantFactory();
	

	public AbstractProbSolverUtil(SolverConsole console)
	{
		this.console = console;
	}

	public String displayFormat(AbstractEvalElement formula)
	{
		return "Solver input:\n\t\t\t"
				+ formula.getCode().replace("&", " & \n\t\t\t");
	}

	protected void initialize(Map<String,Set<String>> sets) throws BException
	{
		String currentSets = generateBMachineSets(sets);
		if (animator == null || !currentSets.equals(anomatorSets))
		{
			anomatorSets = currentSets;
			animator = ServletContextListener.INJECTOR.getInstance(IAnimator.class);
			AbstractCommand[] init = {
					/* We load a machine with the token type installed */
					new LoadBProjectFromStringCommand("MACHINE tmp1 SETS "+currentSets+" END"),
					// new LoadBProjectFromStringCommand("MACHINE empty END"),
					new SetPreferenceCommand("CLPFD", "TRUE"),
					new SetPreferenceCommand("BOOL_AS_PREDICATE", "TRUE"),
					new SetPreferenceCommand("MAXINT", "127"),
					new SetPreferenceCommand("MININT", "-128"),
					new SetPreferenceCommand("TIME_OUT", "500"),
					new StartAnimationCommand() };
			animator.execute(init);
		}
	}
	
	
	private String generateBMachineSets(Map<String,Set<String>> sets)
	{
		StringBuilder sb = new StringBuilder();
		
		for (Iterator<Entry<String, Set<String>>> itr = sets.entrySet().iterator(); itr.hasNext();)
		{
			Entry<String, Set<String>> set = itr.next();
			sb.append(set.getKey());
			
			if(!set.getValue().isEmpty())
			{
				sb.append("= {");
				for (Iterator<String> elemItr = set.getValue().iterator(); elemItr.hasNext();)
				{
					sb.append(elemItr.next());
					if(elemItr.hasNext())
					{
						sb.append(", ");
					}
					
				}
				sb.append("}");
			}
			
			
			if(itr.hasNext())
			{
				sb.append("; ");
			}
			
		}
		
		return sb.toString();
	}

}