package com.slepeweb.money.service;

import com.slepeweb.money.bean.User;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

public interface UserService {
	User save(User u) throws MissingDataException, DuplicateItemException, DataInconsistencyException;
	User getUser(String alias);
}
