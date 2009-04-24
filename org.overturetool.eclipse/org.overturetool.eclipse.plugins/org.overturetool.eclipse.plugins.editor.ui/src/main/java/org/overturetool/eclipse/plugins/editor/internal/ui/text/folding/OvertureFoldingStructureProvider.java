/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.folding;

import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OverturePartitionScanner;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureFoldingStructureProvider extends
		AbstractASTFoldingStructureProvider {
	protected String getCommentPartition() {
		return IOverturePartitions.OVERTURE_COMMENT;
	}

	protected ILog getLog() {
		return UIPlugin.getDefault().getLog();
	}

	protected String getPartition() {
		return IOverturePartitions.OVERTURE_PARTITIONING;
	}

	protected IPartitionTokenScanner getPartitionScanner() {
		return new OverturePartitionScanner();
	}

	protected String[] getPartitionTypes() {
		return IOverturePartitions.OVERTURE_PARTITION_TYPES;
	}

	protected String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	
	// TODO removed old code.... 

//	/* preferences */
//	private boolean fInitCollapseComments = true;
//	private boolean fInitCollapseBlocks = true;
//	private boolean fInitCollapseClasses = true;
//	private boolean fFoldNewLines = true;
//
//	protected void initializePreferences(IPreferenceStore store) {
//		super.initializePreferences(store);
//		fFoldNewLines = true;
//		fInitCollapseBlocks = false;
//		fInitCollapseClasses = false;
//
//		fInitCollapseComments = store
//				.getBoolean(PreferenceConstants.EDITOR_COMMENTS_DEFAULT_FOLDED);
//
//	}
//
//	/**
//	 * Installs a partitioner with <code>document</code>.
//	 * 
//	 * @param document
//	 *            the document
//	 */
//	private void installDocumentStuff(Document document) {
//		String[] types = new String[] { IOverturePartitions.OVERTURE_STRING,
//				IOverturePartitions.OVERTURE_COMMENT, IOverturePartitions.OVERTURE_DOC,
//				IDocument.DEFAULT_CONTENT_TYPE };
//		FastPartitioner partitioner = new FastPartitioner(
//				new OverturePartitionScanner(), types);
//		partitioner.connect(document);
//		document.setDocumentPartitioner(IOverturePartitions.OVERTURE_PARTITIONING,
//				partitioner);
//	}
//
//	/**
//	 * Removes partitioner with <code>document</code>.
//	 * 
//	 * @param document
//	 *            the document
//	 */
//	private void removeDocumentStuff(Document document) {
//		document.setDocumentPartitioner(IOverturePartitions.OVERTURE_PARTITIONING,
//				null);
//	}
//
//	private ITypedRegion getRegion(IDocument d, int offset)
//			throws BadLocationException {
//		return TextUtilities.getPartition(d,
//				IOverturePartitions.OVERTURE_PARTITIONING, offset, true);
//	}
//
//	protected final IRegion[] computeCommentsRanges(String contents) {
//		try {
//			if (contents == null)
//				return new IRegion[0];
//			List<IRegion> regions = new ArrayList<IRegion>();
//			Document d = new Document(contents);
//			installDocumentStuff(d);
//			List<ITypedRegion> docRegionList = new ArrayList<ITypedRegion>();
//			ITypedRegion region = null;
//			int offset = 0;
//			while (true) {
//				try {
//					region = getRegion(d, offset);
//					docRegionList.add(region);
//					offset = region.getLength() + region.getOffset() + 1;
//				} catch (BadLocationException e1) {
//					break;
//				}
//			}
//			ITypedRegion docRegions[] = new ITypedRegion[docRegionList.size()];
//			docRegionList.toArray(docRegions);
//			IRegion fullRegion = null;
//			int start = -1;
//			for (int i = 0; i < docRegions.length; i++) {
//				region = docRegions[i];
//				boolean multiline = isMultilineRegion(d, region);
//				boolean badStart = false;
//				if (d.getLineOffset(d.getLineOfOffset(region.getOffset())) != region
//						.getOffset()) {
//					int lineStart = d.getLineOffset(d.getLineOfOffset(region
//							.getOffset()));
//					String lineStartStr = d.get(lineStart, region.getOffset()
//							- lineStart);
//					if (lineStartStr.trim().length() != 0)
//						badStart = true;
//				}
//				if (!badStart
//						&& (region.getType().equals(
//								IOverturePartitions.OVERTURE_DOC)
//								|| (start != -1 && isEmptyRegion(d, region)
//										&& multiline && collapseEmptyLines()) || (start != -1
//								&& isEmptyRegion(d, region) && !multiline))) {
//					if (start == -1)
//						start = i;
//				} else {
//					if (start != -1) {
//						int offset0 = docRegions[start].getOffset();
//						int length0 = docRegions[i - 1].getOffset() - offset0
//								+ docRegions[i - 1].getLength() - 1;
//						fullRegion = new Region(offset0, length0);
//						if (isMultilineRegion(d, fullRegion)) {
//							regions.add(fullRegion);
//						}
//					}
//					start = -1;
//				}
//			}
//			if (start != -1) {
//				int offset0 = docRegions[start].getOffset();
//				int length0 = docRegions[docRegions.length - 1].getOffset()
//						- offset0
//						+ docRegions[docRegions.length - 1].getLength() - 1;
//				fullRegion = new Region(offset0, length0);
//				if (isMultilineRegion(d, fullRegion)) {
//					regions.add(fullRegion);
//				}
//			}
//			removeDocumentStuff(d);
//			IRegion[] result = new IRegion[regions.size()];
//			regions.toArray(result);
//			return result;
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//		}
//		return new IRegion[0];
//	}
//
//	protected CodeBlock[] getCodeBlocks(String code) {
//		return computeBlockRanges(0, code);
//	}
//
//	private CodeBlock[] computeBlockRanges(final int offset, String contents) {
//		OvertureSourceParser pp = new OvertureSourceParser();
//		// TODO: Add support of filename if needed complex paser support.
//		ModuleDeclaration md = pp.parse(null, contents.toCharArray(), null);
//		final List<CodeBlock> result = new ArrayList<CodeBlock>();
//		ASTVisitor visitor = new ASTVisitor() {
//			public boolean visit(MethodDeclaration s) throws Exception {
//				result.add(new CodeBlock(s, new Region(
//						offset + s.sourceStart(), s.sourceEnd()
//								- s.sourceStart())));
//				return super.visit(s);
//			}
//
//			public boolean visit(TypeDeclaration s) throws Exception {
//				result.add(new CodeBlock(s, new Region(
//						offset + s.sourceStart(), s.sourceEnd()
//								- s.sourceStart())));
//				return super.visit(s);
//			}
//
//		};
//		try {
//			md.traverse(visitor);
//		} catch (Exception e) {
//			if (DLTKCore.DEBUG) {
//				e.printStackTrace();
//			}
//		}
//		return (CodeBlock[]) result.toArray(new CodeBlock[result.size()]);
//	}
//
//	protected boolean initiallyCollapse(ASTNode s,
//			FoldingStructureComputationContext ctx) {
//		return false;
//	}
//
//	protected boolean initiallyCollapseComments(
//			FoldingStructureComputationContext ctx) {
//		return ctx.allowCollapsing() && fInitCollapseComments;
//	}
//
//	protected boolean mayCollapse(ASTNode s,
//			FoldingStructureComputationContext ctx) {
//		return true;
//	}
//
//	protected boolean collapseEmptyLines() {
//		return fFoldNewLines;
//	}
//
//	protected String getCommentPartition() {
//		return IOverturePartitions.OVERTURE_DOC;
//	}
//
//	protected ILog getLog() {
//		return UIPlugin.getDefault().getLog();
//	}
//
//	protected String getPartition() {
//		return IOverturePartitions.OVERTURE_PARTITIONING;
//	}
//
//	protected IPartitionTokenScanner getPartitionScanner() {
//		return new OverturePartitionScanner();
//	}
//
//	protected String[] getPartitionTypes() {
//		return IOverturePartitions.OVERTURE_PARTITION_TYPES;
//	}
//
//	protected String getNatureId() {
//		return OvertureNature.NATURE_ID;
//	}
//
//	protected boolean initiallyCollapse(Statement s,
//			FoldingStructureComputationContext ctx) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	protected boolean mayCollapse(Statement s,
//			FoldingStructureComputationContext ctx) {
//		return false;
//	}
}
