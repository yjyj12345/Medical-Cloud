<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 刷新按钮栏 -->
<div class="payment-refresh-bar" style="margin-bottom: 20px; text-align: right;">
    <button class="btn-refresh-payment" onclick="refreshPaymentData()" style="background: #e85a4f; color: white; border: none; padding: 10px 20px; border-radius: 6px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.3s ease;">
        <i class="fa fa-refresh"></i> 刷新缴费信息
    </button>
</div>

<!-- 错误提示 -->
<c:if test="${not empty errorMsg}">
    <div class="error-alert">
        <i class="fa fa-exclamation-circle"></i>${errorMsg}
    </div>
</c:if>

<div class="payment-card">
    <c:choose>
        <c:when test="${paymentList != null and paymentList.size() > 0}">
            <table class="payment-table">
                <thead>
                    <tr>
                        <th style="width:50px; text-align:center;">
                            <input type="checkbox" id="selectAll" class="row-check" title="全选">
                        </th>
                        <th>项目名称</th>
                        <th>费用（元）</th>
                        <th>创建时间</th>
                        <th>支付时间</th>
                        <th>支付状态</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${paymentList}" var="payment">
                        <tr>
                            <td style="text-align:center; vertical-align:middle;">
                                <c:set var="isPaid" value="${payment['orderStatus'] eq 'paid' or payment['orderStatus'] eq '已支付'}" />
                                <c:choose>
                                    <c:when test="${not isPaid}">
                                        <input type="checkbox" class="row-check item-check" value="${payment['orderId']}" style="width:18px;height:18px;cursor:pointer;">
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color:#ccc;">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${payment['orderContent']}</td>
                            <td><strong>¥${payment['amount']}</strong></td>
                            <td>${payment['createAt']}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty payment['paidAt']}">${payment['paidAt']}</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${isPaid}">
                                        <span class="status-paid"><i class="fa fa-check"></i> 已支付</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-unpaid"><i class="fa fa-clock-o"></i> 未支付</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
            <div class="payment-actions">
                <span id="selectedInfo" style="color:#6c757d; margin-right:20px;">已选择 0 项</span>
                <button class="btn-pay" onclick="submitPayment('wechat')">
                    <i class="fa fa-wechat"></i> 微信支付
                </button>
                <button class="btn-pay" onclick="submitPayment('alipay')">
                    <i class="fa fa-money"></i> 支付宝支付
                </button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="no-data">
                <i class="fa fa-credit-card"></i>
                <p>暂无缴费记录</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>
