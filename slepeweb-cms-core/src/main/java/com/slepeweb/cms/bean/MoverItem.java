package com.slepeweb.cms.bean;

import java.util.List;

import com.slepeweb.cms.except.ResourceException;

public class MoverItem extends Item {
	private static final long serialVersionUID = 1L;

	public static final String MOVE_BEFORE = "before";
	public static final String MOVE_AFTER = "after";
	public static final String MOVE_OVER = "over";
	
	private Long parentId;
	private RelativeLocation fromLocation, toLocation;
	
	public static class RelativeLocation {
		private Long targetId;
		private String mode;
		
		public RelativeLocation() {}
		
		public RelativeLocation(Long id, String m) {
			this.targetId = id;
			this.mode = m;
		}

		public Long getTargetId() {
			return this.targetId;
		}

		public String getMode() {
			return mode;
		}

		public RelativeLocation setTargetId(Long targetId) {
			this.targetId = targetId;
			return this;
		}

		public RelativeLocation setMode(String mode) {
			this.mode = mode;
			return this;
		}
	}
	
	public MoverItem(Item i, RelativeLocation moveTo) {
		super.assimilate(i);
		setId(i.getId());
		setOrigId(i.getOrigId());
		setCmsService(i.getCmsService());
		
		Item parent = i.getOrthogonalParent();
		if (parent == null) {
			return;
		}
		
		this.parentId = parent.getOrigId();
		this.fromLocation = new RelativeLocation().setTargetId(i.getOrigId());
		this.toLocation = moveTo;
		
		// Work out the Context for the current item
		List<Item> allSiblings = parent.getBoundItems();
		int numSiblings = allSiblings.size();			
		
		if (numSiblings == 1) {
			// The mover item has no siblings
			this.fromLocation.setTargetId(this.parentId);
			this.fromLocation.setMode(MOVE_OVER);
		}
		else if (numSiblings > 1) {
			for (int j = 0; j < allSiblings.size(); j++) {
				if (allSiblings.get(j).getId().equals(getId())) {
					// The jth sibling IS the mover item
					if (j > 0) {
						// The mover item IS NOT the first sibling
						this.fromLocation.setTargetId(allSiblings.get(j - 1).getOrigId());
						this.fromLocation.setMode(MOVE_AFTER);
					}
					else {
						// The mover item IS the first sibling
						this.fromLocation.setTargetId(allSiblings.get(1).getOrigId());
						this.fromLocation.setMode(MOVE_BEFORE);
					}
					
					break;
				}
			}
		}
	}
	
	public MoverItem move() throws ResourceException {
		return getCmsService().getItemWorkerService().move(this);
	}
	
	public boolean isValid() {
		return this.fromLocation != null && this.parentId != null;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public RelativeLocation getFrom() {
		return this.fromLocation;
	}

	public RelativeLocation getTo() {
		return this.toLocation;
	}

}
