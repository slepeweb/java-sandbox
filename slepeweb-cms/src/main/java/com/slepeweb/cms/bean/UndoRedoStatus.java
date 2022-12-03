package com.slepeweb.cms.bean;

public class UndoRedoStatus {
	private static final String HELP_TEMPLATE = "%sdo \"%s\" update on item \"%s\"";
	
	private Status undo, redo;	
	
	public UndoRedoStatus(ItemUpdateHistory h) {
		ItemUpdateRecord current = h.getItemUpdateRecord();
		ItemUpdateRecord next = h.getNextItemUpdateRecord();
		
		this.undo = new Status("undo");
		if (current != null) {
			this.undo.
				setAvailable(true).
				setAction(current.getAction().name()).
				setName(current.getBefore().getName());
		}
		
		this.redo = new Status("redo");
		if (next != null) {
			this.redo.
				setAvailable(true).
				setAction(next.getAction().name()).
				setName(next.getAfter().getName());
		}		
	}

	public Status getUndo() {
		return undo;
	}

	public Status getRedo() {
		return redo;
	}

	public static class Status {
		private boolean available;
		private String type, action, name;
		
		public Status(String type) {
			this.type = type;
		}
		
		public boolean isAvailable() {
			return available;
		}
	
		public Status setAvailable(boolean available) {
			this.available = available;
			return this;
		}

		public String getAction() {
			return action;
		}
	
		public Status setAction(String action) {
			this.action = action;
			return this;
		}

		public String getName() {
			return name;
		}
	
		public Status setName(String name) {
			this.name = name;
			return this;
		}

		public String getType() {
			return type;
		}

		public String getHelp() {
			return String.format(HELP_TEMPLATE, 
					this.type.equals("undo") ? "Un" : "Re", this.action, this.name);
		}		
	}
}
