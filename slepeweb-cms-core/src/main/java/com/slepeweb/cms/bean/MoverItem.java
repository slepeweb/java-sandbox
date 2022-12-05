package com.slepeweb.cms.bean;

import java.util.List;

import com.slepeweb.cms.except.ResourceException;

public class MoverItem extends Item {
	private static final long serialVersionUID = 1L;

	public static final String MOVE_BEFORE = "before";
	public static final String MOVE_AFTER = "after";
	public static final String MOVE_OVER = "over";
	
	private Item currentParent, targetParent, target, undoTarget;
	private String mode, undoMode;
	
	public MoverItem(Item i, Item target, String mode) {
		super.assimilate(i);
		setId(i.getId());
		setOrigId(i.getOrigId());
		setCmsService(i.getCmsService());
		
		this.currentParent = i.getParent();
		this.targetParent = target.getParent();
		this.target = target;
		this.mode = mode != null ? mode : MOVE_OVER;
		
		// Now work out how to undo the move
		if (this.currentParent != null) {
			List<Item> currentChildren = this.currentParent.getBoundItems();
			if (currentChildren.size() > 1) {
				if (currentChildren.get(0).getId() == getId()) {
					this.undoTarget = currentChildren.get(1);
					this.undoMode = MOVE_BEFORE;
				}
				else {
					for (int j = 0; j < currentChildren.size() - 1; j++) {
						if (currentChildren.get(j + 1).getId() == getId()) {
							this.undoTarget = currentChildren.get(j);
							this.undoMode = MOVE_AFTER;
							break;
						}
					}
				}
			}
			
			if (this.undoTarget == null) {
				this.undoTarget = this.currentParent;
				this.undoMode = MOVE_OVER;
			}
		}
	}
	
	public MoverItem move() throws ResourceException {
		return getCmsService().getItemWorkerService().move(this);
	}
	
	public boolean isValid() {
		return this.target != null && this.currentParent != null && this.mode != null;
	}

	public Item getCurrentParent() {
		return currentParent;
	}

	public Item getTargetParent() {
		return targetParent;
	}

	public Item getTarget() {
		return target;
	}

	public String getMode() {
		return mode;
	}

	public Item getUndoTarget() {
		return undoTarget;
	}

	public String getUndoMode() {
		return undoMode;
	}

	public void setCurrentParent(Item currentParent) {
		this.currentParent = currentParent;
	}

	public void setTargetParent(Item targetParent) {
		this.targetParent = targetParent;
	}

	public void setTarget(Item target) {
		this.target = target;
	}

	public void setUndoTarget(Item undoTarget) {
		this.undoTarget = undoTarget;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setUndoMode(String undoMode) {
		this.undoMode = undoMode;
	}
}
