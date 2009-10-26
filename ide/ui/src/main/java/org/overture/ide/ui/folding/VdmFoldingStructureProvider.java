package org.overture.ide.ui.folding;

import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;



public class VdmFoldingStructureProvider extends AbstractASTFoldingStructureProvider{

	@Override
	protected String getCommentPartition() {
		return IVdmPartitions.VDM_COMMENT;
	}

	@Override
	protected ILog getLog() {
		return null;
	}

	@Override
	protected String getNatureId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPartition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IPartitionTokenScanner getPartitionScanner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getPartitionTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
