package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.servlet.http.HttpServletRequest;

public class QandAList {
	
	private List<QandA> list = new ArrayList<QandA>();
	
	public QandAList fillFromRequest(HttpServletRequest req) {
		String q, a;
		
		for (int i = 0; i < 3; i++) {
			q = req.getParameter("q" + i);
			if (q == null) {
				q = "";
			}
			a = req.getParameter("a" + i);
			
			if (StringUtils.isNotBlank(a)) {
				add(q.trim().replaceAll("[\"<>]", "_"), a.trim().replaceAll("[\"<>]", "_"));
			}
		}
		
		return this;
	}


	@JsonIgnore
	public int getSize() {
		return this.list.size();
	}
	
	public List<QandA> getList() {
		return list;
	}

	public void setList(List<QandA> list) {
		this.list = list;
	}
	
	public QandAList add(String q, String a) {
		QandA qa = new QandA(q, a);
		qa.setId(getSize());
		getList().add(qa);
		return this;
	}
	
	public boolean equals(QandAList other) {
		int size = getSize();
		
		if (size == 0) {
			return false;
		}
		
		if (size != other.getSize()) {
			return false;
		}
		
		for (int j = 0; j < size; j++) {
			if (! getList().get(j).equals(other.getList().get(j))) {
				return false;
			}
		}
		
		return true;
	}

	public static class QandA {
		private int id;
		private String question, answer;
		
		public QandA() {}
		
		public QandA(String q, String a) {
			this.question = q;
			this.answer = a;
		}
		
		@Override
		public String toString() {
			return String.format("[%d: \"%s\": \"%s\"]", this.id, this.question, this.answer);
		}
		
		public boolean equals(QandA other) {
			if (! getQuestion().equals(other.getQuestion())) {
				return false;
			}
			
			if (! getAnswer().toLowerCase().equals(other.getAnswer().toLowerCase())) {
				return false;
			}
			
			return true;
		}
		
		@JsonIgnore
		public boolean isSet() {
			return StringUtils.isNotBlank(this.question) && StringUtils.isNotBlank(this.answer);
		}
	
		public String getQuestion() {
			return question;
		}
	
		public QandA setQuestion(String question) {
			this.question = question;
			return this;
		}
	
		public String getAnswer() {
			return answer;
		}
	
		public QandA setAnswer(String answer) {
			this.answer = answer;
			return this;
		}

		public int getId() {
			return id;
		}

		public QandA setId(int id) {
			this.id = id;
			return this;
		}
	}
}
