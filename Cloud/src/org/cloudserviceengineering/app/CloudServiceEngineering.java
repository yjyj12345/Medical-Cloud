package org.cloudserviceengineering.app;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cloudserviceengineering.cloudfactory.ICloud;
import org.cloudserviceengineering.cloudfactory.CloudServiceFactory;
/**
 * Servlet implementation class CloudServiceEngineering
 */
@WebServlet("/CloudServiceEngineering")
public class CloudServiceEngineering extends HttpServlet {
	private String domain_str = "Healthcare";
    private ICloud domaincloud;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String domain_str = request.getParameter("select");
        System.out.println(domain_str);
        CloudServiceFactory factory = new CloudServiceFactory();
        domaincloud = factory.produce_domaincloud(domain_str);
        String cloudNameString = domaincloud.getCloudName();
        request.getSession().setAttribute("Cloud", domain_str);
        request.getSession().setAttribute("CloudName", cloudNameString);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
}