package com.slepeweb.site.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
		SimpleComponent simple = new SimpleComponent();

		if (componentLinks != null && componentLinks.size() > 0) {
			for (final Link link : componentLinks) {
				if (targetLinkName == null || link.getName().equals(targetLinkName)) {
					simple.setup(link);
										
					try {
						String fullPath = "com.slepeweb.site.model." + StringUtils.capitalize(simple.getType()) + "Component";
						Class<?> clazz = Class.forName(fullPath);
						Constructor<?> constructor = clazz.getDeclaredConstructor();
						Object obj = constructor.newInstance();
						SimpleComponent target = (SimpleComponent) obj;
						target.setup(link);
	
						if (obj != null) {
							components.add(target);
							LOG.debug(String.format("Component: %s() [%s]", simple.getType(), link.getChild().getPath()));
						}
					} 
					catch (NoSuchMethodException e) {
						LOG.warn(LogUtil.compose("Method not found", simple.getType()));
					}
					catch (Exception e) {
						LOG.warn(LogUtil.compose("Uncaught error", simple.getType()), e);
					}
				}
			}
		}

		return components;
	}
	
}
