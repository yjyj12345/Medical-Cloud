package org.cloudserviceengineering.app;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import org.cloud.paas.registrationservice.RegistrationService_implRemote;
import util.ejbUtility;

/**
 * 预约挂号Servlet
 */
@WebServlet("/Registration_Tool")
public class Registration_Tool extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        String action = request.getParameter("action");
        
        if ("getSpecialties".equals(action)) {
            handleGetSpecialties(request, response);
        } else if ("getDoctors".equals(action)) {
            handleGetDoctors(request, response);
        } else if ("getOrganizations".equals(action)) {
            handleGetOrganizations(request, response);
        } else if ("createAppointment".equals(action)) {
            handleCreateAppointment(request, response);
        } else if ("getAppointments".equals(action)) {
            handleGetAppointments(request, response);
        } else if ("cancelAppointment".equals(action)) {
            handleCancelAppointment(request, response);
        }
    }
    
    /**
     * 获取科室列表
     */
    private void handleGetSpecialties(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        
        try {
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            List<String> specialties = service.getAllSpecialties();
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"data\":[");
            for (int i = 0; i < specialties.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escapeJson(specialties.get(i))).append("\"");
            }
            json.append("]}");
            
            out.print(json.toString());
            
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    /**
     * 根据科室获取医生列表
     */
    private void handleGetDoctors(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String specialty = request.getParameter("specialty");
        
        try {
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            List<Map<String, Object>> doctors = service.getDoctorsBySpecialty(specialty);
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"data\":[");
            for (int i = 0; i < doctors.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> doctor = doctors.get(i);
                json.append("{");
                json.append("\"doctorId\":").append(doctor.get("doctorId")).append(",");
                json.append("\"username\":\"").append(escapeJson(String.valueOf(doctor.get("username")))).append("\",");
                json.append("\"gender\":\"").append(escapeJson(String.valueOf(doctor.get("gender")))).append("\",");
                json.append("\"title\":\"").append(escapeJson(String.valueOf(doctor.get("title")))).append("\",");
                json.append("\"specialty\":\"").append(escapeJson(String.valueOf(doctor.get("specialty")))).append("\"");
                json.append("}");
            }
            json.append("]}");
            
            out.print(json.toString());
            
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    /**
     * 获取机构列表
     */
    private void handleGetOrganizations(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        
        try {
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            List<Map<String, Object>> organizations = service.getAllOrganizations();
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"data\":[");
            for (int i = 0; i < organizations.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> org = organizations.get(i);
                json.append("{");
                json.append("\"organizationId\":").append(org.get("organizationId")).append(",");
                json.append("\"organizationName\":\"").append(escapeJson(String.valueOf(org.get("organizationName")))).append("\",");
                json.append("\"organizationType\":\"").append(escapeJson(String.valueOf(org.get("organizationType")))).append("\"");
                json.append("}");
            }
            json.append("]}");
            
            out.print(json.toString());
            
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    /**
     * 创建预约
     */
    private void handleCreateAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        String doctorIdStr = request.getParameter("doctorId");
        String organizationIdStr = request.getParameter("organizationId");
        String appointmentDate = request.getParameter("appointmentDate");
        
        if (doctorIdStr == null || organizationIdStr == null || appointmentDate == null) {
            out.print("{\"success\":false,\"message\":\"参数不完整\"}");
            return;
        }
        
        try {
            Long doctorId = Long.parseLong(doctorIdStr);
            Long organizationId = Long.parseLong(organizationIdStr);
            
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            Map<String, Object> result = service.createAppointment(patientId, doctorId, organizationId, appointmentDate);
            
            if ((Boolean) result.get("success")) {
                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"processId\":").append(result.get("processId")).append(",");
                json.append("\"message\":\"").append(escapeJson(String.valueOf(result.get("message")))).append("\",");
                json.append("\"doctorName\":\"").append(escapeJson(String.valueOf(result.get("doctorName")))).append("\",");
                json.append("\"specialty\":\"").append(escapeJson(String.valueOf(result.get("specialty")))).append("\",");
                json.append("\"appointmentDate\":\"").append(escapeJson(appointmentDate)).append("\"");
                json.append("}");
                out.print(json.toString());
            } else {
                out.print("{\"success\":false,\"message\":\"" + escapeJson(String.valueOf(result.get("message"))) + "\"}");
            }
            
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"参数格式错误\"}");
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    /**
     * 获取患者预约列表
     */
    private void handleGetAppointments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        try {
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            List<Map<String, Object>> appointments = service.getPatientAppointments(patientId);
            
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,\"data\":[");
            for (int i = 0; i < appointments.size(); i++) {
                if (i > 0) json.append(",");
                Map<String, Object> apt = appointments.get(i);
                json.append("{");
                json.append("\"processId\":").append(apt.get("processId")).append(",");
                json.append("\"status\":\"").append(escapeJson(String.valueOf(apt.get("status")))).append("\",");
                json.append("\"createdAt\":\"").append(apt.get("createdAt")).append("\",");
                json.append("\"doctorName\":\"").append(escapeJson(String.valueOf(apt.get("doctorName")))).append("\",");
                json.append("\"specialty\":\"").append(escapeJson(String.valueOf(apt.get("specialty")))).append("\",");
                json.append("\"title\":\"").append(escapeJson(String.valueOf(apt.get("title")))).append("\",");
                json.append("\"organizationName\":\"").append(escapeJson(String.valueOf(apt.get("organizationName")))).append("\"");
                json.append("}");
            }
            json.append("]}");
            
            out.print(json.toString());
            
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    /**
     * 取消预约
     */
    private void handleCancelAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String processIdStr = request.getParameter("processId");
        
        if (processIdStr == null) {
            out.print("{\"success\":false,\"message\":\"参数不完整\"}");
            return;
        }
        
        try {
            Long processId = Long.parseLong(processIdStr);
            
            RegistrationService_implRemote service = ejbUtility.getRegistrationService();
            boolean success = service.cancelAppointment(processId);
            
            if (success) {
                out.print("{\"success\":true,\"message\":\"取消成功\"}");
            } else {
                out.print("{\"success\":false,\"message\":\"取消失败，可能预约已开始处理\"}");
            }
            
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"参数格式错误\"}");
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务连接失败\"}");
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

