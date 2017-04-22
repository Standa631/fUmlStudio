package net.belehradek.umleditor;

import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.model.GNode;

public abstract class GNodeSkinFix extends GNodeSkin {	
	public GNodeSkinFix(GNode node) {
		super(node);
		this.getRoot().setResizeBorderTolerance(0);
	}
}