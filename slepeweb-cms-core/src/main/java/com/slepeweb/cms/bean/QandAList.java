package com.slepeweb.cms.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class QandAList {
	
	private List<QandA> list = new ArrayList<QandA>();

	public List<QandA> getList() {
		return list;
	}

	public void setList(List<QandA> list) {
		this.list = list;
	}

	public static class QandA {
		private String question, answer;
		
		public QandA() {}
		
		public QandA(String q, String a) {
			this.question = q;
			this.answer = a;
		}
		
		@Override
		public String toString() {
			return String.format("[\"%s\": \"%s\"]", this.question, this.answer);
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
	}
}
