package org.overture.ide.plugins.combinatorialtesting.internal;

import java.io.File;

import org.overture.ide.core.resources.IVdmProject;
import org.overture.interpreter.traces.TraceReductionType;

public class TraceExecutionSetup
{
	final IVdmProject project;
	final String container;
	final String traceName;
	final File coverageFolder;
	final boolean customReduction;

	final float subset;
	final TraceReductionType reductionType;
	final long seed;

	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName, final File coverage)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = false;

		this.subset = 1.0F;
		this.reductionType = TraceReductionType.NONE;
		this.seed = 999;
	}
	
	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName, final File coverage,float subset,
			TraceReductionType reductionType, long seed)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = true;

		this.subset = subset;
		this.reductionType = reductionType;
		this.seed = seed;
	}
	
	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName, final File coverage,float subset,
			TraceReductionType reductionType, long seed, boolean customReduction)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = customReduction;

		this.subset = subset;
		this.reductionType = reductionType;
		this.seed = seed;
	}
}
