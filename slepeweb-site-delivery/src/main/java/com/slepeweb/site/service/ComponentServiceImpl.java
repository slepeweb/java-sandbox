package com.slepeweb.site.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.model.Component;
import com.slepeweb.site.util.StringUtil;

@Service("componentService")
public class ComponentServiceImpl implements ComponentService {
	private static Logger LOG = Logger.getLogger(ComponentServiceImpl.class);

	public List<Component> getComponents(Item i) {
		return getComponents(i, null);
	}
	
	public List<Component> getComponents(Item i, String targetLinkName) {

		List<Component> components = new ArrayList<Component>();
		List<Link> componentLinks = i.getComponents();

		if (componentLinks != null && componentLinks.size() > 0) {
			LOG.debug(LogUtil.compose("This item has components", i, componentLinks.size()));
			
			for (Link link : componentLinks) {
				if (targetLinkName == null || link.getName().equals(targetLinkName)) {
					String linkName = replaceChars(link.getName());
					String linkedItemType = link.getChild().getType().getName();
					String componentType = replaceChars(linkedItemType.equals("Component") ?
							link.getChild().getFieldValue("component-type") : linkedItemType);
										
					try {
						Method method = getClass().getMethod(componentType, Item.class);
						Object obj = method.invoke(this, link.getChild());
	
						if (obj != null) {
							Component c = (Component) obj;
							c.setView(linkName).setType(componentType);
							components.add(c);
						}
					} catch (Exception e) {
						LOG.warn(LogUtil.compose("Method not found", linkName), e);
					}
				}
			}
		}
		else {
			LOG.debug(LogUtil.compose("No components found for item", i));
		}

		return components;
	}
	
	private String replaceChars(String s) {
		return StringUtil.toIdentifier(s);
	}

	public Component feature(Item i) {
		LOG.info(LogUtil.compose("Running method", "feature"));
		Component c = new Component().
				setHeading(i.getFieldValue("heading")).
				setBlurb(i.getFieldValue("blurb"));
		
		// Any sub-components?
		c.setComponents(getComponents(i));
		
		return c;
	}
}
