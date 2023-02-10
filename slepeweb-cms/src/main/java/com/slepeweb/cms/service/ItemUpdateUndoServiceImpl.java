package com.slepeweb.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemUpdateHistory;
import com.slepeweb.cms.bean.ItemUpdateRecord;
import com.slepeweb.cms.bean.ItemUpdateRecord.Action;
import com.slepeweb.cms.bean.MoverItem;
import com.slepeweb.cms.bean.MoverItem.RelativeLocation;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.UndoRedoStatus;

@Service
public class ItemUpdateUndoServiceImpl implements ItemUpdateUndoService {

	@Autowired private ItemService itemService;
	@Autowired private TagService tagService;
	
	@Override
	public RestResponse undo(ItemUpdateHistory h) {
		return update(h, Option.undo);
	}

	@Override
	public RestResponse redo(ItemUpdateHistory h) {
		return update(h, Option.redo);
	}
	
	public enum Option {
		undo, redo
	}
	
	private RestResponse update(ItemUpdateHistory history, Option option) {
		RestResponse resp = new RestResponse();
		ItemUpdateRecord currentUpdateRecord = history.getItemUpdateRecord();
		ItemUpdateRecord nextUpdateRecord = history.getNextItemUpdateRecord();
		
		ItemUpdateRecord updateRecord = null;
		Item i = null;
		
		if (option == Option.undo && currentUpdateRecord != null) {
			updateRecord = currentUpdateRecord;
			i = currentUpdateRecord.getBefore();
		}
		else if (option == Option.redo && nextUpdateRecord != null) {
			updateRecord = nextUpdateRecord;
			i = nextUpdateRecord.getAfter();
		}
		
		if (i == null) {
			return new RestResponse().setError(true).addMessage("No undo/redo available");
		}
		
		Long origId = i.getOrigId();
		Item dbRecord = this.itemService.getItemByOriginalId(origId);
		Object[] moveParams = null;
		
		if (dbRecord != null) {
			try {			
				// core data
				if (updateRecord.getAction() == Action.core) {							
					dbRecord.
						setName(i.getName()).
						setSimpleName(i.getSimpleName()).
						setDateUpdated(i.getDateUpdated()).
						setPublished(i.isPublished()).
						setSearchable(i.isSearchable());
						
					dbRecord.save();
					
					this.tagService.save(dbRecord, i.getTagsAsString());
				}
				// field values
				else if (updateRecord.getAction() == Action.field) {
					dbRecord.setFieldValues(i.getFieldValueSet());
					dbRecord.saveFieldValues();
				}
				// links
				else if (updateRecord.getAction() == Action.links) {
					dbRecord.setLinks(i.getLinks());
					dbRecord.saveLinks();
				}
				// move
				else if (updateRecord.getAction() == Action.move) {
					if (! (i instanceof MoverItem)) {
						resp.setError(true).addMessage("Undo move failure");
					}
					
					MoverItem m = (MoverItem) i;					
					MoverItem mover = new MoverItem(m, 
							new RelativeLocation(m.getFrom().getTargetId(), m.getFrom().getMode()));

					mover.move();
					
					// For ajax caller ... so that left navigation can be updated
					moveParams = new Object[] {
							mover.getTo().getMode(),
							mover.getTo().getTargetId()
					};
				}
								
				if (option == Option.undo) {
					history.undoCompleted();
					resp.addMessage("Undo completed");
				}
				else if (option == Option.redo) {
					history.redoCompleted();
					resp.addMessage("Redo completed");
				}

				resp.setData(new Object[] {
						new UndoRedoStatus(history),
						updateRecord.getAction().name(),
						dbRecord.getOrigId(),
						moveParams
					});
			}
			catch (Exception e) {
				resp.setError(true).addMessage(e.getMessage());
			}			
		}
		
		return resp;
	}
}
