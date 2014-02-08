package org.overture.test.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ConditionalIgnoreMethodRule implements MethodRule
{
	/**
	 * Condition that decides if the current test should be ignored
	 * 
	 * @author kel
	 */
	public interface IgnoreCondition
	{
		/**
		 * Gets the truth value of the condition
		 * 
		 * @return true if the condition decides that the test should be skipped, otherwise false.
		 */
		boolean isIgnored();
	}

	/**
	 * Junit annotation used to annotate the test method
	 * 
	 * @author kel
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface ConditionalIgnore
	{
		Class<? extends IgnoreCondition> condition();
	}

	/**
	 * Check the rule
	 */
	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object target)
	{
		Statement result = base;
		if (hasAnnotation(method))
		{
			IgnoreCondition condition = getIgnoreContition(method);
			if (condition.isIgnored())
			{
				result = new IgnoreStatement(condition);
			}
		}
		return result;
	}

	/**
	 * Gets the ignore condition. This should only be called in
	 * {@link ConditionalIgnoreMethodRule#hasAnnotation(FrameworkMethod)} return true
	 * 
	 * @param method
	 * @return
	 */
	private IgnoreCondition getIgnoreContition(FrameworkMethod method)
	{
		ConditionalIgnore annotation = method.getAnnotation(ConditionalIgnore.class);
		return newCondition(annotation);
	}

	/**
	 * Constructs a new instance of the condition such that is can be validated
	 * 
	 * @param annotation
	 * @return
	 */
	private IgnoreCondition newCondition(ConditionalIgnore annotation)
	{
		try
		{
			return annotation.condition().newInstance();
		} catch (RuntimeException re)
		{
			throw re;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks if the annotation exists
	 * 
	 * @param method
	 * @return
	 */
	private boolean hasAnnotation(FrameworkMethod method)
	{
		return method.getAnnotation(ConditionalIgnore.class) != null;
	}

	/**
	 * Ignore statement returned if the test should be ignored.
	 * 
	 * @author kel
	 */
	private static class IgnoreStatement extends Statement
	{
		private IgnoreCondition condition;

		IgnoreStatement(IgnoreCondition condition)
		{
			this.condition = condition;
		}

		@Override
		public void evaluate()
		{
			Assume.assumeTrue("Ignored test by "
					+ condition.getClass().getSimpleName(), false);
		}
	}

}
