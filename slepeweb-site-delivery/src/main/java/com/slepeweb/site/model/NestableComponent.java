package com.slepeweb.site.model;

import java.util.List;

public interface NestableComponent {
	List<SimpleComponent> getComponents();
	void setComponents(List<SimpleComponent> list);
}
