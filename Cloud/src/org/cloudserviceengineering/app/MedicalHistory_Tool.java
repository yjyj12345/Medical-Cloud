package org.cloudserviceengineering.app;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cloud.paas.medicalhistoryservice.MedicalHistory_implRemote;
import util.ejbUtility;
/**
 * 历史查询Servlet（配合注册登录UserManageService_Tool）
 * 主要功能：校验登录态 -> 调用查询EJB -> 展示看病历史+流程节点
 */
@WebServlet("/MedicalHistory_Tool")
public class MedicalHistory_Tool extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("===== 进入MedicalHistory_Tool doGet方法 =====");

        // 1. 校验登录态
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        String username = (String) session.getAttribute("patientUsername");
        System.out.println("Session中的patientId：" + patientId);
        System.out.println("Session中的patientUsername：" + username);

        if (patientId == null) {
            System.out.println("警告：登录态失效，返回登录页");
            request.setAttribute("alerts", "请先登录再查看历史");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // 2. 调用EJB
        MedicalHistory_implRemote medicalHistoryEJB = null;
        List<Map<String, Object>> medicalHistoryList = null;
        try {
            System.out.println("开始调用EJB查询，patientId：" + patientId);
            medicalHistoryEJB = ejbUtility.getMedicalHistoryService();
            medicalHistoryList = medicalHistoryEJB.getMedicalHistoryByPatientId(patientId);
            
            // 调试：打印EJB返回结果
            System.out.println("EJB返回的患者列表数量：" + (medicalHistoryList == null ? "null" : medicalHistoryList.size()));
            if (medicalHistoryList != null && !medicalHistoryList.isEmpty()) {
                System.out.println("EJB返回的第一条数据：" + medicalHistoryList.get(0));
            }
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("错误：EJB连接异常：" + e.getMessage());
            request.setAttribute("errorMsg", "查询失败：EJB连接异常");
        }

        // 3. 传递数据到页面
        request.setAttribute("medicalHistoryList", medicalHistoryList);
        request.setAttribute("username", username);
        System.out.println("转发到medicalHistory.jsp，已传递medicalHistoryList数据");
        request.getRequestDispatcher("/medicalHistory.jsp").forward(request, response);
    }
}