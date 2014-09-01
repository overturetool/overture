/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.model.internal;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IIndexedValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.osgi.util.NLS;
import org.overture.ide.debug.core.VdmDebugManager;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.commands.IDbgpPropertyCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.AtomicVdmType;
import org.overture.ide.debug.core.model.CollectionVdmType;
import org.overture.ide.debug.core.model.ComplexVdmType;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmTypeFactory;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.StringVdmType;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationCommand;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationEngine;
import org.overture.ide.debug.core.model.internal.eval.VdmEvaluationCommand;

public class VdmValue extends VdmDebugElement implements IVdmValue,
		IIndexedValue
{

	static final IVariable[] NO_VARIABLES = new IVariable[0];

	private final IVdmType type;
	final IVariable[] variables;
	private IVdmStackFrame frame;
	private int pageSize;
	private String name;
	private String fullname;
	private String value;
	private String details;
	private boolean hasChildren;
	private String key;
	private String rawValue;
	private String address;

	public static IVdmValue createValue(IVdmStackFrame frame,
			IDbgpProperty property)
	{
		IVdmType type = createType(frame.getDebugTarget(), property);
		return new VdmValue(frame, property, type);
	}

	private static IVdmType createType(IDebugTarget target,
			IDbgpProperty property)
	{
		IVdmType type = null;
		final String rawType = property.getType();

		final IVdmTypeFactory factory = VdmDebugManager.getInstance().getTypeFactory();
		if (factory != null)
		{
			type = factory.buildType(rawType);
		} else
		{
			type = new AtomicVdmType(rawType);
		}
		return type;
	}

	protected VdmValue(IVdmStackFrame frame, IDbgpProperty property,
			IVdmType type)
	{
		this.frame = frame;
		this.type = type;

		this.key = property.getKey();
		this.name = property.getName();
		this.fullname = property.getEvalName();
		this.rawValue = property.getValue();
		this.value = null;
		this.hasChildren = property.hasChildren();
		this.pageSize = property.getPageSize();
		this.address = property.getAddress();

		final int childrenCount = property.getChildrenCount();
		if (childrenCount > 0)
		{
			this.variables = new IVariable[childrenCount];
			fillVariables(property.getPage(), property);
		} else
		{
			this.variables = NO_VARIABLES;
		}
	}

	private void loadPage(int page) throws DbgpException
	{
		IDbgpPropertyCommands commands = frame.getVdmThread().getDbgpSession().getCoreCommands();
		IDbgpProperty pageProperty = null;
		if (key == null || key.length() == 0)
		{
			pageProperty = commands.getProperty(page, fullname, frame.getLevel());
		} else
		{
			pageProperty = commands.getPropertyByKey(page, fullname, frame.getLevel(), key);
		}

		fillVariables(page, pageProperty);
		final int endIndex = Math.min((page + 1) * pageSize, variables.length);
		for (int i = page * pageSize; i < endIndex; ++i)
		{
			if (variables[i] == null)
			{
				variables[i] = new UnknownVariable(frame, this, i);
			}
		}
	}

	private void fillVariables(int page, IDbgpProperty pageProperty)
	{
		int offset = getPageOffset(page);
		IDbgpProperty[] properties = pageProperty.getAvailableChildren();
		// if(pageProperty.getType().equals("map") && !pageProperty.getName().startsWith("Maplet")){
		// properties = filterProperties(properties);
		// }

		final int size = Math.min(properties.length, variables.length - offset);
		if (size != properties.length)
		{
			VdmDebugPlugin.logWarning(NLS.bind("AvailableChildrenExceedsVariableLength", name), null);
		}
		if (size > 0)
		{
			for (int i = 0; i < size; ++i)
			{
				IDbgpProperty p = properties[i];
				variables[offset + i] = new VdmVariable(frame, p.getName(), p);
			}
			
			if(rawValue!=null && !rawValue.startsWith("mk_"))
			{
				Arrays.sort(this.variables, offset, offset + size, VdmDebugManager.getInstance().getVariableNameComparator());
			}
		}
		Assert.isLegal(pageSize > 0 || properties.length == variables.length);
	}

	// private IDbgpProperty[] filterProperties(IDbgpProperty[] properties) {
	//
	// ArrayList<IDbgpProperty> ret = new ArrayList<IDbgpProperty>();
	//
	// for (IDbgpProperty iDbgpProperty : properties) {
	// if(iDbgpProperty.getName().startsWith("Maplet"))
	// ret.add(iDbgpProperty);
	// }
	//
	//
	// return (IDbgpProperty[]) ret.toArray(new IDbgpProperty[ret.size()]);
	//
	// }

	private int getPageOffset(int page)
	{
		if (pageSize <= 0)
		{
			pageSize = frame.getVdmThread().getPropertyPageSize();
		}

		if (pageSize <= 0)
		{
			return 0;
		}
		return page * pageSize;
	}

	private int getPageForOffset(int offset)
	{
		Assert.isLegal(pageSize > 0);
		return offset / pageSize;
	}

	public String getReferenceTypeName()
	{
		return getType().getName();
	}

	public String getValueString()
	{
		if (value == null || value.length() == 0)
		{

			value = getRawValue();
			if (value.equals("seq") || value.equals("map")
					|| value.equals("set"))
			{
				value = type.formatValue(this);
			}

		}

		if (type instanceof CollectionVdmType || type instanceof ComplexVdmType
				|| type instanceof StringVdmType)
		{
			return type.getName();
		}
		return value;
	}

	public String getDetailsString()
	{
		if (details == null || details.length() == 0)
		{
			details = type.formatDetails(this);
		}

		return details;
	}

	public String getRawValue()
	{
		return rawValue;
	}

	public String getEvalName()
	{
		return fullname;
	}

	public boolean hasVariables()
	{
		return hasChildren;
	}

	public boolean isAllocated()
	{
		return true;
	}

	public String toString()
	{
		return getValueString();
	}

	public IDebugTarget getDebugTarget()
	{
		return frame.getDebugTarget();
	}

	public String getInstanceId()
	{
		return key;
	}

	public IVdmType getType()
	{
		return type;
	}

	public IVdmEvaluationCommand createEvaluationCommand(
			String messageTemplate, IVdmThread thread)
	{
		IVdmEvaluationEngine engine = thread.getEvaluationEngine();

		String pattern = "(%variable%)"; //$NON-NLS-1$
		String evalName = getEvalName();
		if (messageTemplate.indexOf(pattern) != -1)
		{
			String snippet = replacePattern(messageTemplate, pattern, evalName);
			return new VdmEvaluationCommand(engine, snippet, frame);
		}
		VdmDebugPlugin.logWarning(NLS.bind("detailFormatterRequiredToContainIdentifier", pattern), null);
		return new VdmEvaluationCommand(engine, evalName, frame);
	}

	private static String replacePattern(String messageTemplate,
			String pattern, String evalName)
	{
		String result = messageTemplate;
		while (result.indexOf(pattern) != -1)
		{
			int pos = result.indexOf(pattern);
			result = result.substring(0, pos) + evalName
					+ result.substring(pos + pattern.length(), result.length());
		}
		return result;
	}

	public int getInitialOffset()
	{
		return 0;
	}

	public int getSize()
	{
		return variables.length;
	}

	public IVariable getVariable(int offset) throws DebugException
	{
		try
		{
			if (variables[offset] == null)
			{
				loadPage(getPageForOffset(offset));
			}
			return variables[offset];
		} catch (DbgpException e)
		{
			throw wrapDbgpException(NLS.bind("unableToLoadChildrenOf", name), e);
		}
	}

	public IVariable[] getVariables() throws DebugException
	{
		return getVariables(0, getSize());
	}

	public IVariable[] getVariables(int offset, int length)
			throws DebugException
	{
		IVariable[] variables = new IVariable[length];
		for (int i = 0; i < length; i++)
		{
			variables[i] = getVariable(offset + i);
		}
		return variables;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if (adapter == IIndexedValue.class && type.isCollection())
		{
			return this;
		}
		return super.getAdapter(adapter);
	}

	public String getName()
	{
		return name;
	}

	/*
	 * @see org.eclipse.dltk.debug.core.model.IVdmValue#getMemoryAddress()
	 */
	public String getMemoryAddress()
	{
		return address;
	}

	/**
	 * Tests that some of the children are already created.
	 * 
	 * @return
	 */
	protected boolean hasChildrenValuesLoaded()
	{
		for (int i = 0; i < variables.length; ++i)
		{
			if (variables[i] != null)
			{
				return true;
			}
		}
		return false;
	}

}
