package com.slepeweb.site.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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
		String[] componentData;
		Constructor<?> constructor;
		Class<?> clazz;
		int count = 0;

		if (componentLinks != null && componentLinks.size() > 0) {
			for (final Link link : componentLinks) {
				count++;
				i = link.getChild();
				
				if (targetLinkName == null || link.getName().equals(targetLinkName)) {
					componentData = SimpleComponent.getComponentType(i);
										
					try {
						clazz = Class.forName(componentData[0]);
						constructor = clazz.getDeclaredConstructor();
						obj = constructor.newInstance();
						target = (SimpleComponent) obj;
						target.setEnumerator(count);
						target.setComponentService(this).setup(link);
						components.add(target);
						LOG.trace(String.format("Component: %s() [%s]", componentData[0], i.getPath()));
					} 
					catch (NoSuchMethodException e) {
						LOG.warn(LogUtil.compose("Method not found", componentData[0]));
					}
					catch (Exception e) {
						LOG.warn(LogUtil.compose("Uncaught error", componentData[0]));
					}
				}
			}
		}

		return components;
	}
	
}
