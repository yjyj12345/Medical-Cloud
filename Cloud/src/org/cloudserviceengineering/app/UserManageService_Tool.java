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
import org.cloud.paas.medicalhistoryservice.MedicalHistory_implRemote;
import org.cloud.paas.usermanagementservice.MultipleTenantManageService_implRemote;
import util.ejbUtility;

/**
 * 用户管理Servlet - 处理患者和医生的注册/登录
 * 患者登录 → 跳转到看病历史页面
 * 医生登录 → 跳转到医生流程管理页面
 */
@WebServlet("/UserManageService_Tool")
public class UserManageService_Tool extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("register".equals(action)) {
            handleRegister(request, response);
        } else if ("getMedicalHistory".equals(action)) {
            handleGetMedicalHistory(request, response);
        } else if ("logout".equals(action)) {
            handleLogout(request, response);
        } else if ("doctorLogout".equals(action)) {
            handleDoctorLogout(request, response);
        } else if ("getProfile".equals(action)) {
            handleGetProfile(request, response);
        } else if ("updateProfile".equals(action)) {
            handleUpdateProfile(request, response);
        } else if ("updatePassword".equals(action)) {
            handleUpdatePassword(request, response);
        } else if ("getPatientStats".equals(action)) {
            handleGetPatientStats(request, response);
        }
    }

    /**
     * 处理登录 - 区分患者和医生
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // patient 或 doctor
        
        if (role == null || role.isEmpty()) {
            role = "patient"; // 默认患者
        }

        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            
            if ("doctor".equals(role)) {
                // 医生登录
                boolean loginSuccess = userService.doctorLogin(username, password);
                if (loginSuccess) {
                    Long doctorId = userService.getDoctorIdByUsername(username);
                    String doctorInfo = userService.getDoctorInfo(doctorId);
                    
                    session.setAttribute("successMsg", "登录成功，欢迎回来！");
                    session.setAttribute("doctorId", doctorId);
                    session.setAttribute("doctorName", username);
                    session.setAttribute("userRole", "doctor");
                    
                    // 解析医生信息获取科室
                    if (doctorInfo != null) {
                        try {
                            // 简单解析JSON获取specialty
                            if (doctorInfo.contains("\"specialty\"")) {
                                int start = doctorInfo.indexOf("\"specialty\"") + 13;
                                int end = doctorInfo.indexOf("\"", start);
                                if (end > start) {
                                    String specialty = doctorInfo.substring(start, end);
                                    session.setAttribute("specialty", specialty);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("解析医生信息失败: " + e.getMessage());
                        }
                    }
                    
                    // 医生登录后跳转到医生工作站
                    response.sendRedirect(request.getContextPath() + "/doctor_main.jsp");
                    return;
                } else {
                    request.setAttribute("error", "医生用户名或密码错误");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    return;
                }
            } else {
                // 患者登录
                boolean loginSuccess = userService.patientLogin(username, password);
                if (loginSuccess) {
                    Long patientId = userService.getPatientIdByUsername(username);
                    session.setAttribute("successMsg", "登录成功，欢迎回来！");
                    session.setAttribute("patientId", patientId);
                    session.setAttribute("patientUsername", username);
                    session.setAttribute("userRole", "patient");
                    
                    response.sendRedirect(request.getContextPath() + "/main.jsp");
                    return;
                } else {
                    request.setAttribute("error", "患者用户名或密码错误");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    return;
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("error", "登录失败：服务器连接异常");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    /**
     * 处理注册 - 区分患者和医生
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phoneNum = request.getParameter("phoneNum");
        String gender = request.getParameter("gender");
        String role = request.getParameter("role"); // patient 或 doctor
        
        if (role == null || role.isEmpty()) {
            role = "patient";
        }

        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            boolean registerSuccess = false;
            
            if ("doctor".equals(role)) {
                // 医生注册 - 获取额外字段
                String title = request.getParameter("title");
                String specialty = request.getParameter("specialty");
                
                registerSuccess = userService.doctorRegister(username, password, phoneNum, gender, 
                                                             title, specialty, 1L); // organizationId 默认为1
            } else {
                // 患者注册
                registerSuccess = userService.patientRegister(username, password, phoneNum, gender);
            }
            
            if (registerSuccess) {
                // 注册成功后直接登录并跳转
                HttpSession session = request.getSession();
                session.setAttribute("successMsg", "注册成功，欢迎使用！");

                if ("doctor".equals(role)) {
                    Long doctorId = userService.getDoctorIdByUsername(username);
                    session.setAttribute("doctorId", doctorId);
                    session.setAttribute("doctorName", username);
                    session.setAttribute("userRole", "doctor");
                    
                    // 获取科室信息
                    String specialty = request.getParameter("specialty");
                    if (specialty != null && !specialty.isEmpty()) {
                        session.setAttribute("specialty", specialty);
                    }
                    
                    response.sendRedirect(request.getContextPath() + "/doctor_main.jsp");
                } else {
                    Long patientId = userService.getPatientIdByUsername(username);
                    session.setAttribute("patientId", patientId);
                    session.setAttribute("patientUsername", username);
                    session.setAttribute("userRole", "patient");
                    response.sendRedirect(request.getContextPath() + "/main.jsp");
                }
            } else {
                request.setAttribute("error", "注册失败：用户名已存在或信息不完整");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("alerts", "注册失败：服务器连接异常");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    /**
     * 查询患者看病历史
     */
    private void handleGetMedicalHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            request.setAttribute("error", "请先登录");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            MedicalHistory_implRemote medicalHistoryEJB = ejbUtility.getMedicalHistoryService();
            List historyList = medicalHistoryEJB.getMedicalHistoryByPatientId(patientId);
            request.setAttribute("medicalHistoryList", historyList);
            request.getRequestDispatcher("/medicalHistory.jsp").forward(request, response);
        } catch (NamingException e) {
            e.printStackTrace();
            request.setAttribute("errorMsg", "查询历史失败");
            request.getRequestDispatcher("/medicalHistory.jsp").forward(request, response);
        }
    }
    
    /**
     * 处理退出登录（患者）
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
    
    /**
     * 处理医生退出登录
     */
    private void handleDoctorLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
    
    /**
     * 获取患者个人信息
     */
    private void handleGetProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            String patientInfo = userService.getPatientInfo(patientId);
            
            if (patientInfo != null) {
                out.print("{\"success\":true,\"data\":" + patientInfo + "}");
            } else {
                out.print("{\"success\":false,\"message\":\"获取信息失败\"}");
            }
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务器连接异常\"}");
        }
    }
    
    /**
     * 更新患者基本信息
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        String username = request.getParameter("username");
        String phoneNum = request.getParameter("phoneNum");
        String gender = request.getParameter("gender");
        
        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            boolean success = userService.updatePatientInfo(patientId, username, phoneNum, gender);
            
            if (success) {
                // 更新session中的用户名
                session.setAttribute("patientUsername", username);
                out.print("{\"success\":true,\"message\":\"信息更新成功\"}");
            } else {
                out.print("{\"success\":false,\"message\":\"更新失败，用户名可能已被使用\"}");
            }
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务器连接异常\"}");
        }
    }
    
    /**
     * 修改患者密码
     */
    private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        
        if (newPassword == null || newPassword.length() < 6) {
            out.print("{\"success\":false,\"message\":\"新密码长度至少6位\"}");
            return;
        }
        
        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            boolean success = userService.updatePatientPassword(patientId, oldPassword, newPassword);
            
            if (success) {
                out.print("{\"success\":true,\"message\":\"密码修改成功\"}");
            } else {
                out.print("{\"success\":false,\"message\":\"原密码错误或修改失败\"}");
            }
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"服务器连接异常\"}");
        }
    }

    /**
     * 获取患者统计数据
     */
    private void handleGetPatientStats(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        
        if (patientId == null) {
            out.print("{\"success\":false,\"message\":\"请先登录\"}");
            return;
        }
        
        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            String stats = userService.getPatientStats(patientId);
            
            if (stats != null) {
                out.print("{\"success\":true,\"data\":" + stats + "}");
            } else {
                // 返回默认值
                out.print("{\"success\":true,\"data\":{\"visitCount\":0,\"pendingFee\":0}}");
            }
        } catch (NamingException e) {
            e.printStackTrace();
            out.print("{\"success\":true,\"data\":{\"visitCount\":0,\"pendingFee\":0}}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
