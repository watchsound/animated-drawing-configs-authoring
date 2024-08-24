package r9.bvh.simpe.utils;
 

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import r9.bvh.simple.Workspace;
import r9.bvh.simple.model.Node; 
 

public class UndoManager {
	@FunctionalInterface
	public static interface Func {
		void exec();
	}
	
	public static final long Undo_Unit_TimeGap = 200;
    private LinkedList<UndoableAction> actionStack;
    private int index;
  //  private DocContext context;
    private int maxSize = 50;

    private long lastPushTime;
    ContextProviderI  context;
    long lastEnvHash;
    boolean enabled;
    public UndoManager(ContextProviderI context) {
        this.context = context;
        actionStack = new LinkedList<UndoableAction>();
        index = -1;
        enabled = true;
    }
    
    public boolean isEnabled() {
    	return this.enabled;
    }
    
    public void setEnabled(boolean enable) {
    	if( this.enabled != enable )  clear();
    	this.enabled = enable;
    }
    
    public void clear(){
    	actionStack.clear();
    	index = -1;
    }

    public UndoableAction getLastAction() {
        if(actionStack.isEmpty()) return null;
        return actionStack.getLast();
    }
    private void pushSubAction(final UndoableAction act) {
    	pushActionDirectly(new UndoableAction() { 
			public void executeUndo() {
				 act.executeUndo();
			} 
			public void executeRedo() {
				 act.executeRedo();
			} 
			public CharSequence getName() {
				return act.getName();
			} 
			public boolean isSubOperation() { 
				return true;
			}
			@Override
			public boolean isTopOpOnly() { 
				return act.isTopOpOnly();
			} 
    	});
    }
    public void pushAction(final String name, final boolean subOp, final Func undo, final Func redo) {
    	pushAction(name, subOp, undo, redo, false);
    }
    public void pushAction(final String name, final boolean subOp, final Func undo, final Func redo,
    		final boolean  topOpOnly) {
    	if( !this.enabled ) return;
    	pushAction(new UndoableAction() { 
			public void executeUndo() {
				undo.exec ();
			} 
			public void executeRedo() {
				redo.exec ();
			} 
			public CharSequence getName() {
				return name;
			} 
			public boolean isSubOperation() { 
				return subOp;
			}
			@Override
			public boolean isTopOpOnly() { 
				return topOpOnly;
			} 
			
    	});
    }
  
    
    public void pushAction(UndoableAction act) {
    	if( !this.enabled ) return; 
    	if( this.peekUndo() != null && !this.peekUndo().isSubOperation() &&
    			this.peekUndo().isTopOpOnly() ) {
    		this.clear();
    	}
    	long time = new Date().getTime();
    	if( act.isSubOperation() ) {
    		pushActionDirectly(act); //no need to wrap
    	}
    	else if( time - this.lastPushTime <  Undo_Unit_TimeGap  ) {
    		pushSubAction(act);  //we need to wrap and merge as a undo unit
    	} else {
    		pushActionDirectly(act);
    	}
    	this.lastPushTime = time;
    }
    
    private void pushActionDirectly(UndoableAction act) {
    	if( index > maxSize){
    		//remove  10% or 10 undoable actions.
    		final int steps = maxSize / 10;
    		for(int i = 0; i < steps; i++)
    			actionStack.remove(0);
    		index -= steps;
    	}
      
//        u.p("adding at index: " + index);
        if( act.isSubOperation() ) {
        	if( actionStack.isEmpty() ) {
        		actionStack.add(act);
        	} else {
        		UndoableAction prev = actionStack.get(index );
        		if( prev instanceof UndoableActionSet) {
        			UndoableActionSet uas = (UndoableActionSet)prev;
        			uas.actions.add(act);
        		} else {
        			UndoableActionSet uas = new UndoableActionSet(prev);
        			actionStack.set(index, uas); 
        			uas.actions.add(act);
        		}
        	}
        } else {
        	  index++;
        	 actionStack.add(index,act);
        }
        
       
        int start = (index+1);
        int end = actionStack.size();
//        u.p("removing "+start+" to "+end);
        if(start <= end) {
            actionStack.subList(start,end).clear();
        }
        index = actionStack.size()-1;
        //dump();
    }

//    public void pushAddSNode(ContextProvider provider, SNode newNode) {
//    	this.pushAction(new UndoableAddNodeAction(provider.getContext(),newNode,"node"));
//    }
    
    
    
    public void dump() {
        u.p("---- dump -----");
        for(UndoableAction a : actionStack) {
            u.p("   undoable: " + a.getName());
        }
    }
    
    public void pushNodeRotationChange(Node node, double oldx, double oldy, double oldz,
			double newx, double newy, double newz) {
    	this.pushAction(new NodeRotationChange(node, newz, newz, newz, newz, newz, newz));
    }
    public void pushNodeRotationChange(Node node, double changex, double changey, double changez ) {
    	this.pushAction(new NodeRotationChange2(node, changex, changey, changez));
    }

    public static interface UndoableAction {
        public void executeUndo();
        public void executeRedo();
        public CharSequence getName();
        public boolean isSubOperation(); 
        public boolean isTopOpOnly();
    }
    
    public static class UndoableActionSet implements UndoableAction{
    	List<UndoableAction> actions = new ArrayList<>();
    	UndoableActionSet(UndoableAction a){
    		if( a != null )
    			actions.add(a);
    	}
		@Override
		public void executeUndo() {
			for(int i = actions.size()-1; i >=0; i--) {
				actions.get(i).executeUndo();
			}
		}
		@Override
		public void executeRedo() {
			for(int i = 0; i < actions.size(); i++) {
				actions.get(i).executeRedo();
			}
		}
		@Override
		public CharSequence getName() { 
			return actions.get(0).getName();
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
    
    private boolean isCurSuboperation(){
    	if( index < 0 || index > actionStack.size() -1 )
    		return false;
    	UndoableAction action = actionStack.get(index);
    	return action.isSubOperation();
    }

    // op, [op , subop, subop,] [op, subop]
    public void stepBackwards() { 
    	if( !this.enabled ) return;
       boolean subOp = false; 
       do {
    	   subOp = isCurSuboperation();
    	   stepBackwards2();
       } while ( this.canUndo() && subOp );
       
       this.context. refreshWorkspace();
    }
    
    private void stepBackwards2() {  
        u.p("backwards: getting index: " + index  +  " stack:  " + actionStack.size());
        UndoableAction action = actionStack.get(index);
        action.executeUndo();
        index--; 
     //   context.addNotification("Undoing: " + action.getName()); 
    }
    
    // op, [op , subop, subop,] [op, subop]
    public void stepForwards() { 
    	if( !this.enabled ) return;
    	 boolean subOp = false;
         do { 
        	 stepForwards2();
      	    subOp = isCurSuboperation();
         } while ( this.canRedo() && subOp );
         this.context. refreshWorkspace();
     }
     

    private void stepForwards2() {
        index++;
        u.p("forwards: getting index:  " + index);
        UndoableAction action = actionStack.get(index);
        action.executeRedo();
        dump();
    //    context.addNotification("Redoing: " + action.getName());
    }

    public boolean canUndo() {
    	if( !this.enabled ) return false;
        return (index >= 0);
    }
    public UndoableAction peekUndo(){
    	if( !this.enabled ) return null;
    	if( canUndo() )
    		return actionStack.get(index);
    	return null;
    }

    public boolean canRedo() {
    	if( !this.enabled ) return false;
        return (index < actionStack.size()-1);
    }

    public   class NodeRotationChange implements UndoableAction{

    	Node node; double oldx,   oldy,   oldz,  newx,   newy,   newz;
    	public NodeRotationChange(Node node, double oldx, double oldy, double oldz,
    			double newx, double newy, double newz) {
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
			node.setXrotation( oldx  );
			node.setYrotation( oldy );
			node.setZrotation( oldz  );
			context.refreshWorkspace();
		}

		@Override
		public void executeRedo() {
			node.setXrotation( newx  );
			node.setYrotation( newy );
			node.setZrotation( newz  );
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
    
    
    public   class NodeRotationChange2 implements UndoableAction{

    	Node node; double changex,   changey,   changez;
    	public NodeRotationChange2(Node node, double changex, double changey, double changez ) {
    		this.node = node;
    		this.changex = changex;
    		this.changey = changey;
    		this.changez = changez; 
    	}
		@Override
		public void executeUndo() {
			node.setXrotation( node.getXrotation() - changex  );
			node.setYrotation( node.getYrotation() - changey  );
			node.setZrotation( node.getZrotation() - changez   );
			context.refreshWorkspace();
		}

		@Override
		public void executeRedo() {
			node.setXrotation( node.getXrotation() + changex  );
			node.setYrotation( node.getYrotation() + changey  );
			node.setZrotation( node.getZrotation() + changez   );
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
}
