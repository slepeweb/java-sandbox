package com.slepeweb.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemUpdateHistory;
import com.slepeweb.cms.bean.ItemUpdateRecord;
import com.slepeweb.cms.bean.ItemUpdateRecord.Action;
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
		
		ItemUpdateRecord targetUpdateRecord = null;
		Item sourceItem = null;
		
		if (option == Option.undo && currentUpdateRecord != null) {
			targetUpdateRecord = currentUpdateRecord;
			sourceItem = currentUpdateRecord.getBefore();
		}
		else if (option == Option.redo && nextUpdateRecord != null) {
			targetUpdateRecord = nextUpdateRecord;
			sourceItem = nextUpdateRecord.getAfter();
		}
		
		if (sourceItem == null) {
			return new RestResponse().setError(true).addMessage("No undo/redo available");
		}
		
		Long origId = sourceItem.getOrigId();
		Item dbRecord = this.itemService.getItemByOriginalId(origId);
		
		if (dbRecord != null) {
			try {			
				// core data
				if (targetUpdateRecord.getAction() == Action.core) {							
					dbRecord.
						setName(sourceItem.getName()).
						setSimpleName(sourceItem.getSimpleName()).
						setDateUpdated(sourceItem.getDateUpdated()).
						setPublished(sourceItem.isPublished()).
						setSearchable(sourceItem.isSearchable());
						
					dbRecord.save();
					
					this.tagService.save(dbRecord, sourceItem.getTagsAsString());
				}
				// field values
				else if (targetUpdateRecord.getAction() == Action.field) {
					dbRecord.setFieldValues(sourceItem.getFieldValueSet());
					dbRecord.saveFieldValues();
				}
				// links
				else if (targetUpdateRecord.getAction() == Action.links) {
					dbRecord.setLinks(sourceItem.getLinks());
					dbRecord.saveLinks();
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
						targetUpdateRecord.getAction().name(),
						dbRecord.getOrigId()
					});
			}
			catch (Exception e) {
				resp.setError(true).addMessage(e.getMessage());
			}			
		}
		
		return resp;
	}
}
