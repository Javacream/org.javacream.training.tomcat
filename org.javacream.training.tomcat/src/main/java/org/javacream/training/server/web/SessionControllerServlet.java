package org.javacream.training.server.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.javacream.training.jvm.util.simulation.memory.MegaByte;


public class SessionControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = ((HttpServletRequest) request).getSession(true);
		if (request.getParameter("invalidate") != null){
			session.invalidate();
			out.println("Session invalidated");
			return;
		}

		@SuppressWarnings("unchecked")
		ArrayList<MegaByte> sessionMemory = (ArrayList<MegaByte>) session.getAttribute("sessionMemory");
		if (sessionMemory == null){
			sessionMemory = new ArrayList<MegaByte>();
			session.setAttribute("sessionMemory", sessionMemory);
		}
		
		sessionMemory.add(new MegaByte(5));
		// print session info

		Date created = new Date(session.getCreationTime());
		Date accessed = new Date(session.getLastAccessedTime());
		
		out.println(getFormattedInfo("Id", session.getId()));
		out.println(getFormattedInfo("Created", created));
		out.println(getFormattedInfo("Last Accessed", accessed));
		out.println(getFormattedInfo("Session Size [MB]", sessionMemory.size()*5));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	private String getFormattedInfo(String category, Object value){
		return new StringBuilder("<p>").append(category).append(": ").append(value).append("<p>").toString();
	}
}
