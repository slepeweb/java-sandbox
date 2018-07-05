package com.slepeweb.money.bean;

public abstract class DbEntity {
	
	public abstract boolean matches(Object o);
	public abstract void assimilate(Object obj);
	public abstract boolean isDefined4Insert();
}
