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
import org.cloud.paas.paymentservice.Payment_implRemote;
import util.ejbUtility;

/**
 * 缴费查询Servlet
 * 核心功能：校验登录态 → 调用缴费EJB → 展示缴费
 */
@WebServlet("/Payment_Tool")
public class Payment_Tool extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if ("orderLst".equals(action)) {
            handleOrderLst(request, response);
        } else if ("orderPay".equals(action)) {
            handleOrderPay(request, response);
        } else {
            handleOrderLst(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 处理缴费列表查询
     */
    protected void handleOrderLst(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("===== 进入Payment_Tool handleOrderLst方法 =====");

        // 1. 校验登录态
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        String username = (String) session.getAttribute("patientUsername");
        System.out.println("Session中的patientId：" + patientId);
        System.out.println("Session中的patientUsername：" + username);

        if (patientId == null) {
            System.out.println("【错误】登录态失效，返回登录页");
            request.setAttribute("alerts", "请先登录再查看缴费");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        // 2. 调用EJB
        Payment_implRemote paymentEJB = null;
        List<Map<String, Object>> paymentList = null;
        try {
            System.out.println("开始调用EJB，传入patientId：" + patientId);
            paymentEJB = ejbUtility.getPaymentService();
            paymentList = paymentEJB.getPaymentByPatientId(patientId);
            
            // 调试：打印EJB返回结果
            System.out.println("EJB返回的缴费列表数量：" + (paymentList == null ? "null" : paymentList.size()));
            if (paymentList != null && !paymentList.isEmpty()) {
                System.out.println("EJB返回的第一条数据：" + paymentList.get(0));
            }
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("【错误】EJB调用异常：" + e.getMessage());
            request.setAttribute("errorMsg", "查询失败：EJB连接异常");
        }

        // 3. 传递数据到页面
        request.setAttribute("paymentList", paymentList);
        request.setAttribute("username", username);
        System.out.println("跳转到paymentList.jsp，已传递paymentList参数");
        request.getRequestDispatcher("/paymentList.jsp").forward(request, response);
    }
    
    /**
     * 处理支付操作 
     */
    protected void handleOrderPay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 设置为 JSON 响应
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        PrintWriter out = response.getWriter();
        
        try {
            // 1. 校验登录态
            HttpSession session = request.getSession();
            Long patientId = (Long) session.getAttribute("patientId");
            String username = (String) session.getAttribute("patientUsername");
            System.out.println("Session中的patientId：" + patientId);
            System.out.println("Session中的patientUsername：" + username);

            if (patientId == null) {
                System.out.println("【错误】登录态失效");
                out.print("{\"success\":false,\"message\":\"请先登录\"}");
                out.flush();
                return;
            }
         
            // 2. 获取ids参数
            String orderIdLst = request.getParameter("ids");
            
            if (orderIdLst == null || orderIdLst.trim().isEmpty()) {
                System.out.println("【错误】ids参数为空");
                out.print("{\"success\":false,\"message\":\"请选择要支付的订单\"}");
                out.flush();
                return;
            }
            
            System.out.println("-------收到订单ID列表: " + orderIdLst);
            
            // 3. 调用EJB执行支付
            Payment_implRemote paymentEJB = ejbUtility.getPaymentService();
            paymentEJB.PayByOrderIDs(orderIdLst);
            System.out.println("支付完成");
            
            // 4. 返回成功 JSON
            out.print("{\"success\":true,\"message\":\"支付成功\"}");
            out.flush();
            
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("【错误】EJB调用异常：" + e.getMessage());
            out.print("{\"success\":false,\"message\":\"支付失败：服务连接异常\"}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("【错误】支付异常：" + e.getMessage());
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'").replace("\n", " ") : "未知错误";
            out.print("{\"success\":false,\"message\":\"支付失败：" + errorMsg + "\"}");
            out.flush();
        }
    }
}
