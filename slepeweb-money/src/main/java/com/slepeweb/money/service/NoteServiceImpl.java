package com.slepeweb.money.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.money.Note;

/*
 * The creation and updation of tables 'note' and 'chartnote' are handled manually,
 * ie there is no web interface for these functions.
 */

@Service("noteService")
public class NoteServiceImpl extends BaseServiceImpl implements NoteService {
	
	public List<Note> getNotes(long chartId, int begin, int end) {
		String sql = String.format("""
			select n.id, n.year, n.when, n.detail
			from chartnote cn
			join chart c on c.id = cn.chartid
			join note n on n.id = cn.noteid 
			where cn.chartid = ? and c.id %s
			order by n.year asc
			""", 
			chartId == -1 ? "= -1" : "> -1");
		
		List<Note> list = this.jdbcTemplate.query(sql, new RowMapperUtil.NoteMapper(), new Object[] {chartId});
		
		Iterator<Note> iter = list.iterator();
		Note n;
		
		while (iter.hasNext()) {
			n = iter.next();
			if (n.getYear() < begin || n.getYear() > end) {
				iter.remove();
			}
		}
		
		return list;
	}

}
