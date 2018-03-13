package org.javacream.training.server.tomcat;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.lang3.SerializationUtils;

public class SimpleValve extends ValveBase {
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		HttpSession session = request.getSession();
		getNext().invoke(request, response);
		if (active) {
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
				System.out.println("SimpleValve: Session " + session.getId() + " has size "
						+ size);
			}
		}
	}

}
