package com.slepeweb.cms.service;

import com.slepeweb.cms.bean.ItemUpdateHistory;
import com.slepeweb.cms.bean.RestResponse;

public interface ItemUpdateUndoService {
	RestResponse undo(ItemUpdateHistory h);
	RestResponse redo(ItemUpdateHistory h);
}
