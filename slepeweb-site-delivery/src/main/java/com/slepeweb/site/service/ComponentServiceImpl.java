package com.slepeweb.site.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.constant.FieldName;
import com.slepeweb.site.model.ImageComponent;
import com.slepeweb.site.model.SimpleComponent;
import com.slepeweb.site.model.StandardComponent;
import com.slepeweb.site.model.TwitterComponent;

@Service("componentService")
public class ComponentServiceImpl implements ComponentService {
	private static Logger LOG = Logger.getLogger(ComponentServiceImpl.class);
	
	@Autowired private RomeService romeService;
	@Autowired private TwitterService twitterService;

	public List<SimpleComponent> getComponents(List<Link> componentLinks) {
		return getComponents(componentLinks, null);
	}
	
	public List<SimpleComponent> getComponents(List<Link> componentLinks, String targetLinkName) {

		List<SimpleComponent> components = new ArrayList<SimpleComponent>();
		SimpleComponent dummy = new SimpleComponent();

		if (componentLinks != null && componentLinks.size() > 0) {
			for (final Link link : componentLinks) {
				if (targetLinkName == null || link.getName().equals(targetLinkName)) {
					dummy.setup(link);
										
					try {
						Method method = getClass().getMethod(dummy.getType(), Link.class);
						Object obj = method.invoke(this, link);
	
						if (obj != null) {
							components.add((SimpleComponent) obj);
							LOG.debug(String.format("Component: %s() [%s]", dummy.getType(), link.getChild().getPath()));
						}
					} 
					catch (NoSuchMethodException e) {
						LOG.warn(LogUtil.compose("Method not found", dummy.getType()));
					}
					catch (Exception e) {
						LOG.warn(LogUtil.compose("Uncaught error", dummy.getType()), e);
					}
				}
			}
		}

		return components;
	}
	
	public SimpleComponent simple(Link l) {
		SimpleComponent c = new SimpleComponent().setup(l);
		c.setComponents(getComponents(l.getChild().getBindings()));		
		return c;	
	}

	public StandardComponent standard(Link l) {
		return new StandardComponent().setup(l);				
	}
	
	public StandardComponent rss_feed(Link l) {
		
		StandardComponent c = standard(l);
		
		String url = l.getChild().getFieldValue("url");		
		if (StringUtils.isNotBlank(url)) {
			c.setTargets(this.romeService.getFeed(url));
		}
		
		return c;
	}

	public TwitterComponent twitter(Link l) {		
		TwitterComponent c = new TwitterComponent().setup(l);
		c.setTweets(this.twitterService.getSyndicatedTweets(c.getAccounts(), c.getMax()));		
		return c;
	}

	public SimpleComponent tabbed(Link l) {
		SimpleComponent c = simple(l);		
		return c;
	}
	
	public SimpleComponent weather_report(Link l) {
		SimpleComponent c = simple(l);
		return c;
	}
	
	public ImageComponent image_gif(Link l) {
		return image(l);
	}
	
	public ImageComponent image_jpg(Link l) {
		return image(l);
	}
	
	public ImageComponent image_png(Link l) {
		return image(l);
	}
	
	public ImageComponent image(Link l) {
		ImageComponent c = new ImageComponent().setup(l);
		c.setType("image");
		c.setSrc(l.getChild().getPath());
		
		String s = l.getChild().getFieldValue(FieldName.MAX_WIDTH);
		if (StringUtils.isNumeric(s)) {
			c.setMaxWidth(Integer.valueOf(s));
		}
		
		return c;
	}
	
//	private boolean isComponent(Item i) {
//		return i.getType().getName().equals("Component");
//	}
//
//	private boolean isType(Item i, String typeName) {
//		return isComponent(i) && i.getFieldValue("component-type", "").equals(typeName);
//	}
}
