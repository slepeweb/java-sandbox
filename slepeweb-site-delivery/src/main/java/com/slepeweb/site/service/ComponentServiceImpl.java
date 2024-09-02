package com.slepeweb.site.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.model.SimpleComponent;

@Service("componentService")
public class ComponentServiceImpl implements ComponentService {
	private static Logger LOG = Logger.getLogger(ComponentServiceImpl.class);
	

	public List<SimpleComponent> getComponents(List<Link> componentLinks) {
		return getComponents(componentLinks, null);
	}
	
	public List<SimpleComponent> getComponents(List<Link> componentLinks, String targetLinkName) {

		List<SimpleComponent> components = new ArrayList<SimpleComponent>();
		SimpleComponent target;
		Object obj;
		Item i;
		String componentType;
		Constructor<?> constructor;
		Class<?> clazz;
		String fullPath;

		if (componentLinks != null && componentLinks.size() > 0) {
			for (final Link link : componentLinks) {
				i = link.getChild();
				
				if (targetLinkName == null || link.getName().equals(targetLinkName)) {
					componentType = SimpleComponent.getComponentType(i.getType().getName(), i.getFieldValue("component-type"));
										
					try {
						fullPath = "com.slepeweb.site.model." + StringUtils.capitalize(componentType) + "Component";
						clazz = Class.forName(fullPath);
						constructor = clazz.getDeclaredConstructor();
						obj = constructor.newInstance();
						target = (SimpleComponent) obj;
						target.setComponentService(this).setup(link);
						components.add(target);
						LOG.info(String.format("Component: %s() [%s]", componentType, i.getPath()));
					} 
					catch (NoSuchMethodException e) {
						LOG.warn(LogUtil.compose("Method not found", componentType));
					}
					catch (Exception e) {
						LOG.warn(LogUtil.compose("Uncaught error", componentType), e);
					}
				}
			}
		}

		return components;
	}
	
}
