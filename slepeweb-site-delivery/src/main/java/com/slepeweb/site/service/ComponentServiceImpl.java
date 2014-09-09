package com.slepeweb.site.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.model.Component;
import com.slepeweb.site.model.Image;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.SimpleBlockComponent;
import com.slepeweb.site.util.StringUtil;

@Service("componentService")
public class ComponentServiceImpl implements ComponentService {
	private static Logger LOG = Logger.getLogger(ComponentServiceImpl.class);
	
	@Autowired private RomeService romeService;

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
						LOG.warn(LogUtil.compose("Method not found", componentType), e);
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

	public SimpleBlockComponent simple_block(Item i) {
		LOG.info(LogUtil.compose("Running method", "simple_block"));
		SimpleBlockComponent c = new SimpleBlockComponent();
		c.setHeading(i.getFieldValue("heading"));
		c.setBlurb(i.getFieldValue("blurb"));
		c.setCssClass(i.getFieldValue("css"));
		
		// Images
		c.setImages(new ArrayList<Image>());
		for (Link l : i.getInlines()) {
			if (! l.getName().equals(Image.BACKGROUND)) {
				c.getImages().add(new Image(l));
			}
		}
		
		// Link targets
		c.setTargets(new ArrayList<LinkTarget>());
		for (Link l : i.getRelations()) {
			c.getTargets().add(new LinkTarget(l.getChild()));
		}
		
		return c;
	}
	
	public SimpleBlockComponent rss_feed(Item i) {
		LOG.info(LogUtil.compose("Running method", "rss_feed"));
		
		SimpleBlockComponent c = new SimpleBlockComponent();
		c.setHeading(i.getFieldValue("title"));
		c.setBlurb(i.getFieldValue("intro"));
		c.setCssClass(i.getFieldValue("css"));
		
		String url = i.getFieldValue("url");		
		if (StringUtils.isNotBlank(url)) {
			c.setTargets(this.romeService.getFeed(url));
		}
		
		return c;
	}

}
