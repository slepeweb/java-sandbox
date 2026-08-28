package com.slepeweb.money.service;

import java.util.List;

import com.slepeweb.money.Note;

public interface NoteService {
	List<Note> getNotes(long chartid, int begin, int end);
}
