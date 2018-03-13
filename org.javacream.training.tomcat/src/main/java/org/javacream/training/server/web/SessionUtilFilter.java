package org.javacream.training.server.web;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Enumeration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.SerializationUtils;

public class SessionUtilFilter implements Filter, SessionUtilFilterMBean {
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private ObjectName objectName;
	private MBeanServer mbeanServer;

	@Override
	public void destroy() {
		try {
			mbeanServer.unregisterMBean(objectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		filterChain.doFilter(httpServletRequest, response);
		if (active) {
			HttpSession session = httpServletRequest.getSession();
			if (session != null) {
				Enumeration<String> attributeNames = session
						.getAttributeNames();
				long size = 0;
				while(attributeNames.hasMoreElements()){
					String attributeName = attributeNames.nextElement();
					byte[] attributeAsBytes = SerializationUtils
							.serialize((Serializable) session.getAttribute(attributeName));
					size += attributeAsBytes.length;
				}
				System.out.println("Session " + session.getId() + " has size "
						+ size);
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			objectName = new ObjectName(
					"javacream:service=Management,name=Session,type=ServletFilter");
			mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.registerMBean(this, objectName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
