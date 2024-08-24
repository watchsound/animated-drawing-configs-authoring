package r9.bvh.simple.ui.fbfig;

import r9.bvh.simpe.utils.ContextProviderI;
import r9.bvh.simpe.utils.UndoManager.UndoableAction; 
import r9.bvh.simple.model.NodeBase;  

public class FigNodeLocChange  implements UndoableAction{

	NodeBase<NodeBase>  node; double oldx,   oldy,   oldz,  newx,   newy,   newz;
	ContextProviderI context;
	public FigNodeLocChange(ContextProviderI context, NodeBase<NodeBase> node, double oldx, double oldy, double oldz,
			double newx, double newy, double newz) {
        this.context = context;
        this.node = node;
		this.oldx = oldx;
		this.oldy = oldy;
		this.oldz = oldz;
		this.newx = newx;
		this.newy = newy;
		this.newz = newz;
	}
	@Override
	public void executeUndo() {
		node.getPosition().setX(oldx);
		node.getPosition().setY( oldy );
		node.getPosition().setZ( oldz  );
		context.refreshWorkspace();
	}

	@Override
	public void executeRedo() {
		node.getPosition().setX( newx );
		node.getPosition().setY( newy );
		node.getPosition().setZ( newz  );
		context.refreshWorkspace();
	}

	@Override
	public CharSequence getName() { 
		return "";
	}

	@Override
	public boolean isSubOperation() {
	 	return false;
	}

	@Override
	public boolean isTopOpOnly() {
		 return false;
	}
	
}