<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 登录校验：未登录则跳转到登录页
    if (session.getAttribute("patientId") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    Long patientId = (Long) session.getAttribute("patientId");
    String username = (String) session.getAttribute("patientUsername");
    // 如果用户名为空，显示默认值
    if (username == null || username.trim().isEmpty()) {
        username = "用户";
    }
    
    // 获取并清除成功消息
    String successMsg = (String) session.getAttribute("successMsg");
    if (successMsg != null) {
        session.removeAttribute("successMsg");
    }
    
    // 获取当前 Tab（默认显示看病历史）
    String currentTab = request.getParameter("tab");
    if (currentTab == null || currentTab.isEmpty()) {
        currentTab = "history";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.bootcdn.net/ajax/libs/normalize/8.0.1/normalize.min.css" rel="stylesheet">
    <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/3.4.0/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title>医疗云系统 - 患者中心</title>
    <style>
        /* ========== 公共样式 ========== */
        html {
            scrollbar-width: none; 
            -ms-overflow-style: none; 
        }
        
        html::-webkit-scrollbar {
            display: none;
        }
        
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background-color: #f8f9fa;
            line-height: 1.8;
            font-size: 15px;
        }
        
        .header {
            background: #FAF8F6;
            color: #333;
            padding: 20px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            border-bottom: 1px solid #ebe6e3;
        }
        
        .header h1 {
            margin: 0 0 5px 0;
            font-size: 28px;
            font-weight: 800;
            color: #e85a4f;
        }
        
        .header p {
            margin: 0;
            color: #666;
            font-size: 14px;
        }
        
        .container {
            max-width: 1400px;
            width: 95%;
        }
        
        .navbar {
            background-color: #e85a4f !important;
            border: none;
            border-radius: 0;
            margin-bottom: 25px;
        }
        
        .navbar-default .navbar-brand,
        .navbar-default .navbar-nav > li > a {
            color: white !important;
            padding: 15px 20px;
            transition: all 0.3s ease;
        }
        
        .navbar-default .navbar-nav > li > a:hover,
        .navbar-default .navbar-nav > li > a:focus {
            background-color: #d14b41 !important;
        }
        
        .navbar-default .navbar-nav > li.active > a {
            background-color: #d14b41 !important;
            box-shadow: inset 0 -3px 0 #fff;
        }
        
        .footer {
            background-color: #2d3748;
            color: white;
            padding: 20px 0;
            text-align: center;
            margin-top: 60px;
            font-size: 14px;
        }
        
        .fa { margin-right: 5px; }
        
        /* Tab 内容区域 */
        .tab-content-area {
            display: none;
            min-height: calc(100vh - 300px); 
        }
        
        .tab-content-area.active {
            display: block;
        }
        
        /* ========== 看病历史样式 ========== */
        .history-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            padding: 25px;
            margin-bottom: 30px;
            border-left: 4px solid #e85a4f;
        }
        
        .history-header {
            border-bottom: 1px solid #e9ecef;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        
        .history-header h3 {
            color: #e85a4f;
            margin: 0 0 10px 0;
            font-weight: 600;
        }
        
        .history-meta {
            color: #6c757d;
            font-size: 14px;
        }
        
        .history-meta span {
            margin-right: 20px;
        }
        
        .timeline {
            position: relative;
            margin: 30px 0;
            padding: 0;
            display: flex;
            width: 100%;
            overflow-x: auto;
            padding-bottom: 10px;
        }
        
        .timeline::before {
            content: '';
            position: absolute;
            top: 30px;
            left: 0;
            width: 100%;
            height: 4px;
            background: #fcd5cf;
            border-radius: 2px;
        }
        
        .timeline-dot {
            position: absolute;
            top: 30px;
            left: 50%;
            transform: translateX(-50%);
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: #d0d0d0;
            border: 3px solid #e8e8e8;
            z-index: 1;
            transition: all 0.3s ease;
        }
        
        /* 已完成 - 绿色 */
        .timeline-dot.completed { 
            background: #10b981; 
            border-color: #10b981;
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
        }
        
        /* 正在进行 - 主题色脉冲 */
        .timeline-dot.active { 
            background: #e85a4f; 
            border-color: #e85a4f;
            box-shadow: 0 0 0 3px rgba(232, 90, 79, 0.3);
            animation: timelinePulse 2s infinite;
        }
        
        /* 待完成 - 灰色 */
        .timeline-dot.pending { 
            background: #d0d0d0; 
            border-color: #e8e8e8;
        }
        
        @keyframes timelinePulse {
            0% { box-shadow: 0 0 0 3px rgba(232, 90, 79, 0.3); }
            50% { box-shadow: 0 0 0 6px rgba(232, 90, 79, 0.1); }
            100% { box-shadow: 0 0 0 3px rgba(232, 90, 79, 0.3); }
        }
        
        .timeline-label.completed { color: #10b981; font-weight: 600; }
        .timeline-label.active { color: #e85a4f; font-weight: bold; }
        .timeline-label.pending { color: #999; }
        
        .timeline-label {
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 5px;
            display: block;
        }
        
        .timeline-time {
            font-size: 13px;
            color: #6c757d;
            display: block;
            margin-bottom: 8px;
        }
        
        .timeline-content {
            margin-top: 8px;
            font-size: 13px;
            color: #495057;
            background: #f8f9fa;
            padding: 8px 10px;
            border-radius: 4px;
            display: block;
            text-align: left;
            word-wrap: break-word;
        }
        
        .timeline-item {
            position: relative;
            flex: 1;
            text-align: center;
            padding-top: 60px;
            padding-left: 25px;
            padding-right: 5px;
            box-sizing: border-box;
            min-width: 140px;
        }
        
        /* ========== 就医进程样式 ========== */
        .process-section {
            background-color: white;
            border-radius: 8px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .process-title {
            color: #e85a4f;
            margin-bottom: 20px;
            margin-top: 0;
            font-size: 1.5em;
            font-weight: 600;
        }
        
        .progress-container {
            position: relative;
            padding: 20px 0;
            margin: 20px 0;
        }
        
        .progress-line {
            position: absolute;
            top: 50px;
            left: 0;
            width: 100%;
            height: 5px;
            background-color: #e8e8e8;
            z-index: 1;
            border-radius: 3px;
        }
        
        .progress-line-active {
            position: absolute;
            top: 50px;
            left: 0;
            height: 5px;
            background-color: #10b981;
            z-index: 2;
            transition: width 0.8s ease;
            width: 8.33%;
            border-radius: 3px;
        }
        
        .progress-steps {
            display: flex;
            justify-content: space-between;
            position: relative;
            z-index: 3;
            margin: 0;
            padding: 0;
            list-style: none;
        }
        
        .progress-step {
            display: flex;
            flex-direction: column;
            align-items: center;
            width: 16.67%;
            text-align: center;
            cursor: not-allowed;
            transition: all 0.3s ease;
            padding-left: 25px;
            opacity: 0.5;
        }
        
        .progress-step.clickable {
            cursor: pointer;
            opacity: 1;
        }
        
        .progress-step.clickable:hover .step-circle {
            transform: scale(1.1);
        }
        
        .progress-step.pending {
            opacity: 0.5;
        }
        
        .step-circle {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background-color: #e8e8e8;
            color: #999;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 10px;
            transition: all 0.3s ease;
            box-shadow: 0 2px 5px rgba(0,0,0,0.08);
            border: 3px solid #d0d0d0;
        }
        
        .step-circle i {
            margin: 0;
        }
        
        .step-text {
            font-size: 14px;
            color: #999;
            transition: all 0.3s ease;
            font-weight: 500;
        }
        
        /* 已完成 - 绿色 */
        .progress-step.completed .step-circle {
            background-color: #10b981;
            color: white;
            border-color: #10b981;
            box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.2);
        }
        
        .progress-step.completed .step-text {
            color: #10b981;
            font-weight: 600;
        }
        
        /* 正在进行 - 主题色动画 */
        .progress-step.active .step-circle {
            background-color: #e85a4f;
            color: white;
            border-color: #e85a4f;
            box-shadow: 0 0 0 4px rgba(232, 90, 79, 0.3);
            transform: scale(1.15);
            animation: pulse 2s infinite;
        }
        
        .progress-step.active .step-text {
            color: #e85a4f;
            font-weight: bold;
        }
        
        @keyframes pulse {
            0% { box-shadow: 0 0 0 4px rgba(232, 90, 79, 0.3); }
            50% { box-shadow: 0 0 0 8px rgba(232, 90, 79, 0.1); }
            100% { box-shadow: 0 0 0 4px rgba(232, 90, 79, 0.3); }
        }
        
        .step-details {
            margin-top: 40px;
            padding: 25px;
            background-color: #FEF9F8;
            border-left: 4px solid #e85a4f;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            min-height: 200px;
        }
        
        .step-details h3 {
            color: #e85a4f;
            margin-top: 0;
            margin-bottom: 20px;
            border-bottom: 1px solid #f0e0dc;
            padding-bottom: 10px;
        }
        
        .step-info {
            margin: 15px 0;
        }
        
        .step-info strong {
            color: #555;
            min-width: 80px;
            display: inline-block;
        }
        
        .status-label {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
            margin-left: 5px;
        }
        
        .status-pending { background-color: #6b7280; color: white; }
        .status-in-progress { background-color: #e85a4f; color: white; }
        .status-completed { background-color: #10b981; color: white; }
        
        .btn-action {
            min-width: 100px;
            margin: 0 5px;
            font-weight: 500;
        }
        
        /* ========== 门诊缴费样式 ========== */
        .payment-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            padding: 25px;
            margin-bottom: 30px;
            min-height: 400px;
        }
        
        .payment-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        
        .payment-table th, .payment-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }
        
        .payment-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }
        
        .payment-table tr:hover {
            background-color: #f8f9fa;
        }
        
        .status-paid {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            background-color: #d4edda;
            color: #155724;
        }
        
        .status-unpaid {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            background-color: #fff3cd;
            color: #856404;
        }
        
        .btn-pay {
            background-color: #e85a4f;
            color: white;
            border: none;
            padding: 10px 24px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
            margin-left: 10px;
        }
        
        .btn-pay:hover {
            background-color: #d14b41;
        }
        
        .btn-pay:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        
        .payment-actions {
            text-align: right;
            padding-top: 20px;
            border-top: 1px solid #e9ecef;
        }
        
        .row-check {
            width: 18px;
            height: 18px;
            cursor: pointer;
            position: relative;
            z-index: 10;
            -webkit-appearance: checkbox;
            -moz-appearance: checkbox;
            appearance: checkbox;
        }
        
        .payment-table td:first-child,
        .payment-table th:first-child {
            text-align: center;
            vertical-align: middle;
        }
        
        .payment-table input[type="checkbox"] {
            margin: 0;
            padding: 0;
            pointer-events: auto;
        }
        
        /* ========== 个人信息样式 ========== */
        .profile-layout {
            display: flex;
            gap: 30px;
            min-height: 500px;
        }
        
        .profile-sidebar {
            width: 280px;
            flex-shrink: 0;
        }
        
        .profile-avatar-box {
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            border-radius: 16px;
            padding: 30px 20px;
            text-align: center;
            color: white;
            margin-bottom: 20px;
        }
        
        .profile-avatar-circle {
            width: 90px;
            height: 90px;
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            margin: 0 auto 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 40px;
            backdrop-filter: blur(10px);
        }
        
        .profile-username {
            margin: 0 0 5px 0;
            font-size: 20px;
            font-weight: 600;
        }
        
        .profile-patient-id {
            margin: 0 0 15px 0;
            opacity: 0.8;
            font-size: 13px;
        }
        
        .profile-status {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: rgba(255,255,255,0.2);
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 13px;
        }
        
        .status-dot {
            width: 8px;
            height: 8px;
            background: #4ade80;
            border-radius: 50%;
        }
        
        .profile-stats {
            background: white;
            border-radius: 12px;
            padding: 20px;
            display: flex;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }
        
        .stat-item {
            flex: 1;
            text-align: center;
        }
        
        .stat-item + .stat-item {
            border-left: 1px solid #eee;
        }
        
        .stat-num {
            font-size: 24px;
            font-weight: 700;
            color: #e85a4f;
        }
        
        .stat-label {
            font-size: 12px;
            color: #888;
            margin-top: 4px;
        }
        
        .profile-main {
            flex: 1;
        }
        
        .profile-section {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
        }
        
        .profile-section-header {
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .profile-section-header h4 {
            margin: 0;
            color: #333;
            font-size: 16px;
            font-weight: 600;
        }
        
        .profile-section-header h4 i {
            color: #e85a4f;
            margin-right: 8px;
        }
        
        .profile-form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }
        
        .profile-form-item label {
            display: block;
            font-size: 13px;
            color: #666;
            margin-bottom: 8px;
            font-weight: 500;
        }
        
        .profile-input {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.2s;
            background: #fafafa;
        }
        
        .profile-input:focus {
            outline: none;
            border-color: #e85a4f;
            background: white;
            box-shadow: 0 0 0 3px rgba(232, 90, 79, 0.1);
        }
        
        .profile-input-wrap {
            position: relative;
        }
        
        .profile-input-wrap .profile-input {
            padding-right: 40px;
        }
        
        .profile-eye-btn {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #999;
            cursor: pointer;
            padding: 5px;
        }
        
        .profile-eye-btn:hover {
            color: #e85a4f;
        }
        
        .profile-radio-group {
            display: flex;
            gap: 25px;
            padding-top: 8px;
        }
        
        .profile-radio {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
            font-size: 14px;
            color: #555;
        }
        
        .profile-radio input[type="radio"] {
            width: 18px;
            height: 18px;
            accent-color: #e85a4f;
        }
        
        .profile-form-actions {
            display: flex;
            gap: 12px;
            align-items: flex-end;
        }
        
        .profile-btn {
            padding: 12px 24px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        
        .profile-btn-save {
            background: #e85a4f;
            color: white;
            border: none;
        }
        
        .profile-btn-save:hover {
            background: #d14b41;
        }
        
        .profile-btn-reset {
            background: white;
            color: #666;
            border: 1px solid #ddd;
        }
        
        .profile-btn-reset:hover {
            background: #f5f5f5;
        }
        
        .profile-password-bar {
            height: 4px;
            background: #eee;
            border-radius: 2px;
            margin-top: 8px;
            overflow: hidden;
        }
        
        .profile-password-fill {
            height: 100%;
            width: 0;
            border-radius: 2px;
            transition: all 0.3s;
        }
        
        .profile-password-fill.strength-weak { background: #dc2626; width: 33% !important; }
        .profile-password-fill.strength-medium { background: #f59e0b; width: 66% !important; }
        .profile-password-fill.strength-strong { background: #10b981; width: 100% !important; }
        
        .profile-hint {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
            display: block;
        }
        
        .profile-alert {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .profile-alert-success {
            background: #ecfdf5;
            color: #059669;
            border: 1px solid #a7f3d0;
        }
        
        .profile-alert-error {
            background: #fef2f2;
            color: #dc2626;
            border: 1px solid #fecaca;
        }
        
        @media (max-width: 992px) {
            .profile-layout {
                flex-direction: column;
            }
            .profile-sidebar {
                width: 100%;
            }
            .profile-form-grid {
                grid-template-columns: 1fr;
            }
        }
        
        /* ========== 预约挂号样式 ========== */
        .reg-wrapper {
            display: flex;
            gap: 25px;
        }
        
        .reg-form-panel {
            flex: 2;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 15px rgba(0,0,0,0.06);
            overflow: hidden;
        }
        
        .reg-form-header {
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            color: white;
            padding: 25px;
        }
        
        .reg-form-header h3 {
            margin: 0 0 5px 0;
            font-size: 20px;
        }
        
        .reg-form-header p {
            margin: 0;
            opacity: 0.85;
            font-size: 14px;
        }
        
        .reg-steps {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 25px;
            background: #f8fafc;
            border-bottom: 1px solid #eee;
        }
        
        .reg-step {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #999;
        }
        
        .reg-step.active {
            color: #e85a4f;
        }
        
        .reg-step.active .step-num {
            background: #e85a4f;
            color: white;
        }
        
        .step-num {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: #e0e0e0;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: 600;
        }
        
        .step-text {
            font-size: 14px;
        }
        
        .reg-step-line {
            width: 50px;
            height: 2px;
            background: #e0e0e0;
            margin: 0 15px;
        }
        
        #appointmentForm {
            padding: 25px;
        }
        
        .reg-field {
            margin-bottom: 20px;
        }
        
        .reg-field label {
            display: block;
            font-size: 14px;
            color: #555;
            margin-bottom: 8px;
            font-weight: 500;
        }
        
        .reg-field label i {
            color: #e85a4f;
            margin-right: 6px;
        }
        
        .reg-select, .reg-input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            background: white;
            transition: border-color 0.2s;
        }
        
        .reg-select:focus, .reg-input:focus {
            outline: none;
            border-color: #e85a4f;
        }
        
        .reg-field-row {
            display: flex;
            gap: 20px;
        }
        
        .reg-field.half {
            flex: 1;
        }
        
        .reg-doctor-list {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            max-height: 200px;
            overflow-y: auto;
            padding: 5px;
        }
        
        .reg-doctor-card {
            border: 2px solid #eee;
            border-radius: 10px;
            padding: 15px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .reg-doctor-card:hover {
            border-color: #e85a4f;
            background: #FEF9F8;
        }
        
        .reg-doctor-card.selected {
            border-color: #e85a4f;
            background: #FEF3F0;
        }
        
        .reg-doctor-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
        }
        
        .reg-doctor-info {
            font-size: 12px;
            color: #888;
        }
        
        .reg-empty-hint {
            grid-column: 1 / -1;
            text-align: center;
            padding: 30px;
            color: #999;
        }
        
        .reg-submit-area {
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        
        .reg-submit-btn {
            width: 100%;
            padding: 14px;
            background: #e85a4f;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .reg-submit-btn:hover:not(:disabled) {
            background: #d14b41;
        }
        
        .reg-submit-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .reg-list-panel {
            flex: 1;
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 15px rgba(0,0,0,0.06);
            display: flex;
            flex-direction: column;
            max-height: 600px;
        }
        
        .reg-list-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            border-bottom: 1px solid #eee;
        }
        
        .reg-list-header h4 {
            margin: 0;
            font-size: 16px;
            color: #333;
        }
        
        .reg-refresh-btn {
            background: none;
            border: 1px solid #ddd;
            border-radius: 6px;
            padding: 6px 12px;
            cursor: pointer;
            color: #666;
        }
        
        .reg-refresh-btn:hover {
            background: #f5f5f5;
        }
        
        .reg-list-body {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
        }
        
        .reg-apt-item {
            border: 1px solid #eee;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 12px;
        }
        
        .reg-apt-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .reg-apt-doctor {
            font-weight: 600;
            color: #333;
        }
        
        .reg-apt-status {
            font-size: 12px;
            padding: 3px 10px;
            border-radius: 12px;
        }
        
        .reg-apt-status.status-booked {
            background: #FEF3F0;
            color: #e85a4f;
        }
        
        .reg-apt-status.status-done {
            background: #d4edda;
            color: #155724;
        }
        
        .reg-apt-status.status-cancel {
            background: #f8d7da;
            color: #721c24;
        }
        
        .reg-apt-info {
            font-size: 13px;
            color: #666;
            margin-bottom: 10px;
        }
        
        .reg-apt-info span {
            display: block;
            margin-bottom: 3px;
        }
        
        .reg-apt-actions {
            text-align: right;
        }
        
        .reg-cancel-btn {
            font-size: 12px;
            color: #dc3545;
            background: none;
            border: 1px solid #dc3545;
            padding: 4px 12px;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .reg-cancel-btn:hover {
            background: #dc3545;
            color: white;
        }
        
        .reg-loading {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        
        .reg-empty {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        
        .reg-alert {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .reg-alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .reg-alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        @media (max-width: 992px) {
            .reg-wrapper {
                flex-direction: column;
            }
            .reg-doctor-list {
                grid-template-columns: 1fr;
            }
        }
        
        .password-strength-bar {
            height: 100%;
            width: 0%;
            transition: all 0.3s ease;
            border-radius: 2px;
        }
        
        .strength-weak { background: #dc3545; width: 33%; }
        .strength-medium { background: #ffc107; width: 66%; }
        .strength-strong { background: #28a745; width: 100%; }
        
        .password-hint {
            font-size: 12px;
            color: #6c757d;
            margin-top: 5px;
        }
        
        /* ========== 通用样式 ========== */
        .no-data {
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .no-data i {
            font-size: 60px;
            color: #fcd5cf;
            margin-bottom: 20px;
            display: block;
        }
        
        .no-data p {
            color: #6c757d;
            font-size: 16px;
            margin: 0;
        }
        
        /* 无就医进程提示样式 */
        .no-process-hint {
            text-align: center;
            padding: 80px 40px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }
        
        .no-process-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #FEF3F0, #fcd5cf);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 25px;
        }
        
        .no-process-icon i {
            font-size: 45px;
            color: #e85a4f;
        }
        
        .no-process-hint h3 {
            color: #333;
            font-size: 22px;
            margin: 0 0 15px 0;
            font-weight: 600;
        }
        
        .no-process-hint p {
            color: #888;
            font-size: 15px;
            margin: 0 0 30px 0;
            line-height: 1.6;
        }
        
        .btn-go-registration {
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            color: white;
            border: none;
            padding: 14px 32px;
            border-radius: 30px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(232, 90, 79, 0.3);
        }
        
        .btn-go-registration:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(232, 90, 79, 0.4);
        }
        
        .btn-go-registration i {
            margin-right: 8px;
        }
        
        /* 无看病历史提示样式 */
        .no-history-hint {
            text-align: center;
            padding: 80px 40px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }
        
        .no-history-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #FEF3F0, #fcd5cf);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 25px;
        }
        
        .no-history-icon i {
            font-size: 45px;
            color: #e85a4f;
        }
        
        .no-history-hint h3 {
            color: #333;
            font-size: 22px;
            margin: 0 0 15px 0;
            font-weight: 600;
        }
        
        .no-history-hint p {
            color: #888;
            font-size: 15px;
            margin: 0 0 30px 0;
            line-height: 1.6;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }
        
        /* 看病历史刷新按钮 */
        .history-refresh-bar {
            margin-bottom: 20px;
            text-align: right;
        }
        
        .btn-refresh-history {
            background: #e85a4f;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-refresh-history:hover {
            background: #d14b41;
            transform: translateY(-1px);
        }
        
        .btn-refresh-history i {
            margin-right: 6px;
        }
        
        .error-alert {
            background-color: #fef2f2;
            color: #dc2626;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
            border-left: 4px solid #dc2626;
        }
        
        .label {
            padding: 5px 10px;
            font-size: 12px;
            border-radius: 4px;
            font-weight: 500;
        }
        
        .label-success {
            background-color: #10b981 !important;
            color: white !important;
            border-radius: 12px !important;
            padding: 4px 12px !important;
        }
        
        .label-warning, .label.label-warning {
            background-color: #e85a4f !important;
            color: white !important;
            border-radius: 12px !important;
            padding: 4px 12px !important;
        }
        
        .label-default {
            background-color: #6b7280 !important;
            color: white !important;
            border-radius: 12px !important;
            padding: 4px 12px !important;
        }
        
        /* 加载动画 */
        .loading-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.95);
            display: none;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }
        
        .loading-spinner {
            width: 60px;
            height: 60px;
            border: 5px solid #f3f3f3;
            border-top: 5px solid #e85a4f;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-bottom: 20px;
        }
        
        .loading-text {
            font-size: 18px;
            color: #e85a4f;
            font-weight: bold;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        /* Tab 切换动画 */
        .tab-content-area {
            animation: fadeIn 0.3s ease;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        /* 患者端检查图片预览样式 */
        .examination-images {
            margin-top: 10px;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .examination-images > i {
            color: #059669;
            margin-right: 5px;
        }
        
        .patient-image-preview {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 10px;
        }
        
        .patient-image-item {
            width: 100px;
            height: 100px;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .patient-image-item:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(0,0,0,0.25);
        }
        
        .patient-image-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        /* 图片全屏查看模态框 */
        .image-modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.9);
            z-index: 10000;
            display: flex;
            justify-content: center;
            align-items: center;
            opacity: 0;
            visibility: hidden;
            transition: all 0.3s ease;
        }
        
        .image-modal-overlay.active {
            opacity: 1;
            visibility: visible;
        }
        
        .image-modal-content {
            max-width: 90%;
            max-height: 90%;
        }
        
        .image-modal-content img {
            max-width: 100%;
            max-height: 90vh;
            object-fit: contain;
            border-radius: 8px;
        }
        
        .image-modal-close {
            position: absolute;
            top: 20px;
            right: 30px;
            color: white;
            font-size: 40px;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        .image-modal-close:hover {
            transform: scale(1.2);
        }

        /* ========== AI 助手聊天组件样式 ========== */
        .ai-chat-btn {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: #e85a4f;
            color: white;
            border: none;
            cursor: move;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            z-index: 9999;
            display: flex;
            align-items: center;
            justify-content: center;
            user-select: none;
        }

        .ai-chat-btn:hover {
            background: #d14b41;
        }

        .ai-chat-btn.dragging {
            opacity: 0.8;
        }

        .ai-chat-btn i {
            font-size: 24px;
            pointer-events: none;
            margin: 0 !important;
            padding: 0;
            width: auto;
            text-align: center;
        }

        .ai-chat-btn .badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background: #28a745;
            color: white;
            font-size: 10px;
            padding: 3px 6px;
            border-radius: 10px;
        }

        .ai-chat-window {
            position: fixed;
            bottom: 100px;
            right: 30px;
            width: 380px;
            height: 500px;
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 20px;
            box-shadow: 0 12px 48px rgba(0,0,0,0.25), 0 0 0 1px rgba(255,255,255,0.3);
            z-index: 9998;
            display: none;
            overflow: hidden;
        }

        .ai-chat-window.show {
            display: block;
        }

        .ai-chat-window-inner {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .ai-chat-header {
            background: rgba(232, 90, 79, 0.9);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            color: white;
            padding: 16px 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            cursor: move;
            user-select: none;
            flex-shrink: 0;
        }

        .ai-chat-header-left {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .ai-avatar {
            width: 40px;
            height: 40px;
            background: rgba(255,255,255,0.2);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .ai-chat-title {
            font-size: 16px;
            font-weight: 600;
        }

        .ai-chat-subtitle {
            font-size: 12px;
            opacity: 0.8;
        }

        .ai-chat-close {
            background: none;
            border: none;
            color: white;
            font-size: 20px;
            cursor: pointer;
            padding: 5px;
            opacity: 0.8;
            transition: opacity 0.2s;
        }

        .ai-chat-close:hover {
            opacity: 1;
        }

        .ai-chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 15px 15px 8px;
            background: transparent;
        }

        .ai-chat-bottom {
            background: rgba(255, 255, 255, 0.6);
            border-top: 1px solid rgba(200, 200, 200, 0.5);
            padding-top: 6px;
        }

        .ai-message {
            margin-bottom: 16px;
            display: flex;
            gap: 10px;
        }

        .ai-message.user {
            flex-direction: row-reverse;
        }

        .ai-message-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            flex-shrink: 0;
        }

        .ai-message-avatar i {
            margin: 0;
            padding: 0;
            line-height: 1;
        }

        .ai-message.ai .ai-message-avatar {
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            color: white;
        }

        .ai-message.user .ai-message-avatar {
            background: #e0e0e0;
            color: #666;
        }

        .ai-message-content {
            max-width: 75%;
            padding: 12px 16px;
            border-radius: 16px;
            font-size: 14px;
            line-height: 1.5;
        }

        .ai-message.ai .ai-message-content {
            background: white;
            color: #333;
            border-bottom-left-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .ai-message.user .ai-message-content {
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            color: white;
            border-bottom-right-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            word-break: break-word;
        }

        .ai-message-time {
            font-size: 11px;
            color: #999;
            margin-top: 4px;
        }

        .ai-message.user .ai-message-time {
            text-align: right;
        }

        .ai-message-body {
            flex: 1;
            min-width: 0;
            max-width: calc(100% - 42px);
        }

        .ai-message.user .ai-message-body {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }

        .ai-typing {
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 8px 0;
        }

        .ai-typing-dot {
            width: 8px;
            height: 8px;
            background: #ccc;
            border-radius: 50%;
            animation: typing 1.4s infinite;
        }

        .ai-typing-dot:nth-child(2) { animation-delay: 0.2s; }
        .ai-typing-dot:nth-child(3) { animation-delay: 0.4s; }

        @keyframes typing {
            0%, 60%, 100% { transform: translateY(0); background: #ccc; }
            30% { transform: translateY(-8px); background: #e85a4f; }
        }

        .ai-chat-input-area {
            padding: 8px 12px 12px;
            display: flex;
            gap: 10px;
        }

        .ai-chat-input {
            flex: 1;
            padding: 12px 16px;
            border: 1px solid #ddd;
            border-radius: 24px;
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s;
        }

        .ai-chat-input:focus {
            border-color: #e85a4f;
        }

        .ai-chat-send {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: linear-gradient(135deg, #e85a4f, #d14b41);
            color: white;
            border: none;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.2s;
        }

        .ai-chat-send:hover:not(:disabled) {
            transform: scale(1.05);
        }

        .ai-chat-send:disabled {
            background: #ccc;
            cursor: not-allowed;
        }

        .ai-quick-actions {
            padding: 4px 12px;
            display: flex;
            gap: 6px;
            flex-wrap: wrap;
        }

        .ai-quick-btn {
            padding: 6px 12px;
            background: #f5f5f5;
            border: 1px solid #e0e0e0;
            border-radius: 16px;
            font-size: 12px;
            color: #666;
            cursor: pointer;
            transition: all 0.2s;
        }

        .ai-quick-btn:hover {
            background: #FEF3F0;
            border-color: #e85a4f;
            color: #e85a4f;
        }

        @media (max-width: 480px) {
            .ai-chat-window {
                width: calc(100% - 20px);
                right: 10px;
                bottom: 80px;
                height: 60vh;
            }
            .ai-chat-btn {
                bottom: 20px;
                right: 20px;
                width: 50px;
                height: 50px;
            }
        }
    </style>
    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>
<body>
    <!-- 加载动画 -->
    <div class="loading-overlay" id="loadingOverlay">
        <div class="loading-spinner"></div>
        <div class="loading-text">正在加载...</div>
    </div>

    <!-- 成功提示 -->
    <% if (successMsg != null) { %>
    <div id="successToast" style="position:fixed;top:20px;left:50%;transform:translateX(-50%);background:#10b981;color:#fff;padding:12px 24px;border-radius:8px;font-size:15px;z-index:9999;box-shadow:0 4px 12px rgba(0,0,0,0.15);">
        <i class="fa fa-check"></i> <%= successMsg %>
    </div>
    <script>setTimeout(function(){document.getElementById('successToast').style.display='none';},3000);</script>
    <% } %>

    <!-- Header -->
    <div class="header">
        <div class="container">
            <h1><i class="fa fa-user-circle-o"></i> 患者中心</h1>
            <p>智慧医疗，便捷服务</p>
        </div>
    </div>

    <!-- 导航栏 -->
    <div class="navbar navbar-default">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#main-nav">
                    <span class="sr-only">切换导航</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="<%=request.getContextPath()%>/index.jsp" class="navbar-brand">医疗云</a>
            </div>
            <div class="collapse navbar-collapse" id="main-nav">
                <ul class="nav navbar-nav">
                    <li id="tab-history" class="<%= "history".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('history')"><i class="fa fa-history"></i> 看病历史</a>
                    </li>
                    <li id="tab-process" class="<%= "process".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('process')"><i class="fa fa-heartbeat"></i> 就医进程</a>
                    </li>
                    <li id="tab-payment" class="<%= "payment".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('payment')"><i class="fa fa-credit-card"></i> 门诊缴费</a>
                    </li>
                    <li id="tab-registration" class="<%= "registration".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('registration')"><i class="fa fa-calendar-check-o"></i> 预约挂号</a>
                    </li>
                    <li id="tab-profile" class="<%= "profile".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('profile')"><i class="fa fa-user-circle"></i> 个人信息</a>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#" style="color: #fff59d !important;">
                        <i class="fa fa-user"></i> <%=username%>
                    </a></li>
                    <li><a href="<%=request.getContextPath()%>/UserManageService_Tool?action=logout"><i class="fa fa-sign-out"></i> 退出</a></li>
                </ul>
            </div>
        </div>
    </div>

    <!-- ========== 内容区域 ========== -->
    <div class="container">
        
        <!-- Tab 1: 看病历史 -->
        <div id="content-history" class="tab-content-area <%= "history".equals(currentTab) ? "active" : "" %>">
            <div id="historyContainer">
                <div class="text-center" style="padding: 50px 0;">
                    <i class="fa fa-spinner fa-spin fa-2x" style="color: #0d5fc9;"></i>
                    <p>正在加载看病历史...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 2: 就医进程 -->
        <div id="content-process" class="tab-content-area <%= "process".equals(currentTab) ? "active" : "" %>">
            <div id="processContainer">
                <div class="text-center" style="padding: 50px 0;">
                    <i class="fa fa-spinner fa-spin fa-2x" style="color: #0d5fc9;"></i>
                    <p>正在加载就医进程...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 3: 门诊缴费 -->
        <div id="content-payment" class="tab-content-area <%= "payment".equals(currentTab) ? "active" : "" %>">
            <div id="paymentContainer">
                <div class="text-center" style="padding: 50px 0;">
                    <i class="fa fa-spinner fa-spin fa-2x" style="color: #0d5fc9;"></i>
                    <p>正在加载缴费信息...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 4: 预约挂号 -->
        <div id="content-registration" class="tab-content-area <%= "registration".equals(currentTab) ? "active" : "" %>">
            <div id="registrationContainer">
                <div class="text-center" style="padding: 50px 0;">
                    <i class="fa fa-spinner fa-spin fa-2x" style="color: #0d5fc9;"></i>
                    <p>正在加载预约挂号...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 5: 个人信息 -->
        <div id="content-profile" class="tab-content-area <%= "profile".equals(currentTab) ? "active" : "" %>">
            <div id="profileContainer">
                <div class="text-center" style="padding: 50px 0;">
                    <i class="fa fa-spinner fa-spin fa-2x" style="color: #0d5fc9;"></i>
                    <p>正在加载个人信息...</p>
                </div>
            </div>
        </div>
        
    </div>

    <!-- Footer -->
    <div class="footer">
        &copy; 2025 医疗云系统. 版权所有. | 智慧医疗服务平台
    </div>


    <script>
        // ========== 全局变量 ==========
        let currentTab = '<%= currentTab %>';
        let dataLoaded = { history: false, process: false, payment: false, registration: false, profile: false };
        
        // 就医进程相关
        let processId = null;
        let patientId = null;
        let patientName = null;
        let currentStep = 1;
        let stepNames = ["挂号", "问诊", "检查", "治疗", "取药", "完成"];
        let stepIcons = ["<i class='fa fa-clipboard'></i>", "<i class='fa fa-stethoscope'></i>", "<i class='fa fa-flask'></i>", "<i class='fa fa-medkit'></i>", "<i class='fa fa-plus-square'></i>", "<i class='fa fa-check-circle'></i>"];
        
        // ========== 页面初始化 ==========
        $(document).ready(function() {
            console.log('主页面加载完成，当前Tab:', currentTab);
            
            // 加载当前 Tab 的数据
            loadTabData(currentTab);
        });
        
        // ========== Tab 切换 ==========
        function switchTab(tab) {
            if (tab === currentTab) return;
            
            console.log('切换到:', tab);
            currentTab = tab;
            
            // 更新导航栏 active 状态
            $('.navbar-nav li').removeClass('active');
            $('#tab-' + tab).addClass('active');
            
            // 隐藏所有内容区域，显示当前的
            $('.tab-content-area').removeClass('active');
            $('#content-' + tab).addClass('active');
            
            // 加载数据（如果未加载过）
            loadTabData(tab);
            
            // 更新 URL（不刷新页面）
            history.pushState({tab: tab}, '', '?tab=' + tab);
        }
        
        // ========== 数据加载 ==========
        function loadTabData(tab) {
            switch(tab) {
                case 'history':
                    if (!dataLoaded.history) loadHistoryData();
                    break;
                case 'process':
                    if (!dataLoaded.process) loadProcessData();
                    break;
                case 'payment':
                    if (!dataLoaded.payment) loadPaymentData();
                    break;
                case 'registration':
                    if (!dataLoaded.registration) loadRegistrationData();
                    break;
                case 'profile':
                    if (!dataLoaded.profile) {
                        loadProfileData();
                    } else {
                        // 每次切换到个人信息都刷新统计数据（待缴费用等）
                        loadProfileStats();
                    }
                    break;
            }
        }
        
        // 加载看病历史
        function loadHistoryData() {
            console.log('加载看病历史...');
            $.ajax({
                url: 'MedicalHistory_Tool',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    // 添加刷新按钮容器
                    var refreshBtn = '<div class="history-refresh-bar"><button class="btn-refresh-history" onclick="refreshHistoryData()"><i class="fa fa-refresh"></i> 刷新看病历史</button></div>';
                    
                    // 直接使用响应内容
                    if (response && response.trim()) {
                        $('#historyContainer').html(refreshBtn + response);
                        // 初始化检查图片预览
                        initExaminationImages();
                    } else {
                        showNoHistoryHint();
                    }
                    dataLoaded.history = true;
                },
                error: function() {
                    $('#historyContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载看病历史失败</div>');
                }
            });
        }
        
        // 刷新看病历史
        function refreshHistoryData() {
            console.log('刷新看病历史...');
            $('#historyContainer').html('<div class="text-center" style="padding: 50px 0;"><i class="fa fa-spinner fa-spin fa-2x" style="color: #e85a4f;"></i><p>正在刷新...</p></div>');
            dataLoaded.history = false;
            loadHistoryData();
        }
        
        // ========== 检查图片预览功能 ==========
        function initExaminationImages() {
            $('.patient-image-preview').each(function() {
                var $container = $(this);
                var picturesData = $container.data('pictures');
                
                $container.empty();
                
                if (picturesData && picturesData.toString().trim() !== '') {
                    // 图片数据使用 ||| 分隔
                    var images = picturesData.toString().split('|||');
                    
                    images.forEach(function(imgData, index) {
                        if (imgData && imgData.trim() !== '') {
                            var $imgItem = $('<div class="patient-image-item"></div>');
                            $imgItem.html('<img src="' + imgData.trim() + '" alt="检查图片' + (index + 1) + '" onclick="openPatientImageModal(this.src)">');
                            $container.append($imgItem);
                        }
                    });
                }
                
                // 如果没有有效图片，显示提示
                if ($container.children().length === 0) {
                    $container.html('<span style="color:#999;">暂无检查图片</span>');
                }
            });
        }
        
        function openPatientImageModal(imgSrc) {
            var $modal = $('#patientImageModal');
            if ($modal.length === 0) {
                // 创建模态框
                $('body').append(
                    '<div class="image-modal-overlay" id="patientImageModal" onclick="closePatientImageModal()">' +
                    '<span class="image-modal-close">&times;</span>' +
                    '<div class="image-modal-content" onclick="event.stopPropagation()">' +
                    '<img id="patientModalImage" src="" alt="检查图片">' +
                    '</div>' +
                    '</div>'
                );
                $modal = $('#patientImageModal');
            }
            
            $('#patientModalImage').attr('src', imgSrc);
            $modal.addClass('active');
            
            // 禁止页面滚动
            $('body').css('overflow', 'hidden');
        }
        
        function closePatientImageModal() {
            $('#patientImageModal').removeClass('active');
            $('body').css('overflow', '');
        }
        
        // ESC键关闭模态框
        $(document).on('keydown', function(e) {
            if (e.key === 'Escape') {
                closePatientImageModal();
            }
        });
        
        // 显示无看病历史提示
        function showNoHistoryHint() {
            var html = '<div class="no-history-hint">' +
                '<div class="no-history-icon"><i class="fa fa-file-text-o"></i></div>' +
                '<h3>暂无看病历史</h3>' +
                '<p>您目前还没有任何就诊记录。完成就诊后，您的看病历史将会显示在这里。</p>' +
                '<button class="btn-go-registration" onclick="switchTab(\'registration\')">' +
                '<i class="fa fa-plus-circle"></i> 去预约挂号' +
                '</button>' +
                '</div>';
            $('#historyContainer').html(html);
        }
        
        // 加载就医进程
        function loadProcessData() {
            console.log('加载就医进程...');
            $.ajax({
                url: 'patient_process.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    if (response && response.trim()) {
                        $('#processContainer').html(response);
                        // 初始化进度步骤并加载数据
                        initProgressSteps();
                        getLatestMedicalProcessInfo();
                    } else {
                        $('#processContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载就医进程页面失败</div>');
                    }
                    dataLoaded.process = true;
                },
                error: function() {
                    $('#processContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载就医进程失败</div>');
                }
            });
        }
        
        
        // 加载门诊缴费
        function loadPaymentData() {
            console.log('加载门诊缴费...');
            $.ajax({
                url: 'Payment_Tool',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    // 直接使用响应内容
                    if (response && response.trim()) {
                        $('#paymentContainer').html(response);
                    } else {
                        $('#paymentContainer').html('<div class="no-data"><i class="fa fa-credit-card"></i><p>暂无缴费记录</p></div>');
                    }
                    dataLoaded.payment = true;
                },
                error: function() {
                    $('#paymentContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载缴费信息失败</div>');
                }
            });
        }

        // 刷新门诊缴费数据
        function refreshPaymentData() {
            console.log('刷新门诊缴费...');
            $('#paymentContainer').html('<div class="text-center" style="padding: 50px 0;"><i class="fa fa-spinner fa-spin fa-2x" style="color: #e85a4f;"></i><p>正在刷新...</p></div>');
            dataLoaded.payment = false;
            loadPaymentData();
        }

        // ========== 就医进程相关函数 ==========
        function initProgressSteps() {
            let html = '';
            for (let i = 0; i < stepNames.length; i++) {
                html += '<li class="progress-step" data-step="' + (i + 1) + '" onclick="onStepClick(' + (i + 1) + ')">' +
                        '<div class="step-circle">' + stepIcons[i] + '</div>' +
                        '<div class="step-text">' + stepNames[i] + '</div>' +
                        '</li>';
            }
            $('#progressSteps').html(html);
        }
        
        // 点击步骤结点
        function onStepClick(step) {
            // 只能点击已完成的或当前进行中的结点
            if (step > currentStep) {
                // 未来的结点不可点击
                return;
            }
            // 加载该步骤的详细信息
            loadStepData(step);
        }
        
        function getLatestMedicalProcessInfo() {
            $.ajax({
                url: 'PatientProcess_Tool',
                type: 'POST',
                data: { action: 'getLatestMedicalProcessInfo' },
                dataType: 'json',
                timeout: 10000,
                success: function(data) {
                    // 隐藏加载提示
                    $('#processLoadingContainer').hide();
                    
                    // 检查返回数据：外层 success 和内层 data.success 都必须为 true
                    if (data && data.success && data.data) {
                        let info = data.data;

                        let ejbSuccess = info.success;
                        let isReallySuccess = (ejbSuccess === true || ejbSuccess === "true");
                        
                        if (isReallySuccess && info.processId) {
                            processId = info.processId;
                            patientId = info.patientId || "1";
                            patientName = info.patientName || "患者";
                            
                            $('#processId').val(processId);
                            $('#patientId').val(patientId);
                            $('#processInfo').text('进程ID: ' + processId + ' | 患者ID: ' + patientId);
                            $('#patientName').text(patientName + ' 的就医进程');
                            
                            // 使用后端返回的当前节点ID
                            currentStep = parseInt(info.currentNodeId) || 1;
                            updateProgressBar();
                            updateStepStatus();
                            updateStepDetailsWithInfo(info, currentStep);
                            
                            // 显示进程内容，隐藏无进程提示
                            $('#processContentContainer').show();
                            $('#noProcessHintContainer').hide();
                        } else {
                            // EJB 返回 success=false，显示无进程提示
                            console.log('EJB返回无进程: ', info.error || '未找到就医进程');
                            $('#processContentContainer').hide();
                            $('#noProcessHintContainer').show();
                        }
                    } else {
                        // 没有进行中的就医进程，显示友好提示
                        $('#processContentContainer').hide();
                        $('#noProcessHintContainer').show();
                    }
                },
                error: function(xhr, status, error) {
                    // 隐藏加载提示，显示无进程提示
                    $('#processLoadingContainer').hide();
                    $('#processContentContainer').hide();
                    $('#noProcessHintContainer').show();
                }
            });
        }
        
        function updateProgressBar() {
            let progressWidth = 8.33;
            if (currentStep >= 6) progressWidth = 100.0;
            else if (currentStep > 1) progressWidth = ((currentStep - 1) + 0.5) / 6.0 * 100.0;
            $('#progressBar').css('width', progressWidth + '%');
        }
        
        function updateStepStatus() {
            $('.progress-step').each(function(index) {
                let step = index + 1;
                $(this).removeClass('completed active pending clickable');
                if (step < currentStep) {
                    $(this).addClass('completed clickable');
                } else if (step === currentStep) {
                    $(this).addClass('active clickable');
                } else {
                    $(this).addClass('pending');
                }
            });
        }
        
        function updateStepDetailsWithInfo(info, viewingStep) {
            let nodeStatus = info.nodeStatus || '待完成';
            let diagnosisText = info.diagnosisText || '暂无信息';
            let reminder = info.reminder || '';
            let createdAt = info.createdAt || '未记录';
            let location = info.location || '暂未设置';
            let doctorName = info.doctorName || '暂未分配';
            let doctorTitle = info.doctorTitle || '';
            let medicineName = info.medicineName || '暂无';
            let useMethod = info.useMethod || '请遵医嘱';
            
            let displayStep = viewingStep || currentStep;
            let nodeName = info.nodeName || stepNames[displayStep - 1];
            
            // 医生显示格式：名称 + 职称
            let doctorDisplay = doctorName;
            if (doctorTitle && doctorName !== '暂未分配') {
                doctorDisplay = doctorName + '（' + doctorTitle + '）';
            }
            
            let statusClass = 'status-pending';
            if (nodeStatus === '进行中' || nodeStatus === 'inprogress' || nodeStatus === 'in_progress') statusClass = 'status-in-progress';
            if (nodeStatus === '已完成' || nodeStatus === 'completed') statusClass = 'status-completed';
            
            let html = '<h3><i class="fa fa-info-circle"></i> ' + nodeName + '</h3>' +
                       '<div class="step-info"><strong>状态：</strong><span class="status-label ' + statusClass + '">' + nodeStatus + '</span></div>';
            
            // 根据不同步骤显示不同信息
            if (displayStep === 1) {
                // 挂号：挂号时间、预约医生、就诊地点
                html += '<div class="step-info"><strong>挂号时间：</strong>' + createdAt + '</div>';
                html += '<div class="step-info"><strong>预约医生：</strong>' + doctorDisplay + '</div>';
                html += '<div class="step-info"><strong>就诊地点：</strong><i class="fa fa-map-marker"></i> ' + location + '</div>';
            } else if (displayStep === 2) {
                // 问诊：接诊医生、诊室地点、诊断内容
                html += '<div class="step-info"><strong>接诊医生：</strong>' + doctorDisplay + '</div>';
                html += '<div class="step-info"><strong>诊室地点：</strong><i class="fa fa-map-marker"></i> ' + location + '</div>';
                html += '<div class="step-info"><strong>诊断内容：</strong>' + diagnosisText + '</div>';
            } else if (displayStep === 3) {
                // 检查：检查地点、检查内容、检查图片
                html += '<div class="step-info"><strong>检查地点：</strong><i class="fa fa-map-marker"></i> ' + location + '</div>';
                html += '<div class="step-info"><strong>检查内容：</strong>' + diagnosisText + '</div>';
                // 检查图片 
                html += '<div class="examination-images">';
                html += '<i class="fa fa-picture-o"></i><span>检查影像：</span>';
                html += '<div class="patient-image-preview" id="processExaminationImages"></div>';
                html += '</div>';
            } else if (displayStep === 4) {
                // 治疗：治疗地点、治疗方案
                html += '<div class="step-info"><strong>治疗地点：</strong><i class="fa fa-map-marker"></i> ' + location + '</div>';
                html += '<div class="step-info"><strong>治疗方案：</strong>' + diagnosisText + '</div>';
            } else if (displayStep === 5) {
                // 取药：取药地点、药品名称、用药说明
                html += '<div class="step-info"><strong>取药地点：</strong><i class="fa fa-map-marker"></i> ' + location + '</div>';
                html += '<div class="step-info"><strong>药品名称：</strong>' + medicineName + '</div>';
                html += '<div class="step-info"><strong>用药说明：</strong>' + useMethod + '</div>';
            } else if (displayStep === 6) {
                // 完成：完成提示
                html += '<div class="alert alert-success" style="margin-top: 20px;"><h4><i class="fa fa-check-circle"></i> 就医流程已完成！</h4><p>感谢您使用医疗云系统服务。</p></div>';
            }
            
            // 通用提醒信息
            if (reminder && reminder !== '请完成此步骤' && reminder.trim() !== '') {
                html += '<div class="step-info"><strong>医嘱提醒：</strong><i class="fa fa-bell-o"></i> ' + reminder + '</div>';
            }
            
            $('#currentStepDetails').html(html);
            
            // 如果是检查阶段，加载图片预览
            if (displayStep === 3) {
                let pictures = info.pictures || '';
                console.log('[调试] 检查阶段图片数据:', pictures ? ('有数据，长度:' + pictures.length) : '无数据');
                loadProcessExaminationImages(pictures);
            }
        }
        
        // 加载就医进程中的检查图片
        function loadProcessExaminationImages(picturesData) {
            var $container = $('#processExaminationImages');
            if ($container.length === 0) return;
            
            $container.empty();
            
            if (picturesData && picturesData.trim() !== '') {
                var images = picturesData.split('|||');
                
                images.forEach(function(imgData, index) {
                    if (imgData && imgData.trim() !== '') {
                        var $imgItem = $('<div class="patient-image-item"></div>');
                        $imgItem.html('<img src="' + imgData.trim() + '" alt="检查图片' + (index + 1) + '" onclick="openPatientImageModal(this.src)">');
                        $container.append($imgItem);
                    }
                });
                
                if ($container.children().length === 0) {
                    $container.html('<span style="color:#999;">暂无检查图片</span>');
                }
            } else {
                $container.html('<span style="color:#999;">暂无检查图片</span>');
            }
        }
        
        function loadStepData(step) {
            if (!processId) return;
            let viewingStep = parseInt(step);
            
            // 显示加载中
            $('#currentStepDetails').html('<div class="text-center" style="padding: 30px;"><i class="fa fa-spinner fa-spin" style="color: #e85a4f;"></i> 加载中...</div>');
            
            $.ajax({
                url: 'PatientProcess_Tool',
                type: 'POST',
                data: { action: 'getStepData', processId: processId, step: viewingStep },
                dataType: 'json',
                success: function(data) {
                    if (data && data.success) {
                        updateStepDetailsWithInfo(data.data || {}, viewingStep);
                    } else {
                        $('#currentStepDetails').html('<div class="step-info">暂无该步骤的详细信息</div>');
                    }
                },
                error: function() {
                    $('#currentStepDetails').html('<div class="step-info">加载失败，请稍后重试</div>');
                }
            });
        }
        
        
        function refreshProcess() {
            dataLoaded.process = false;
            $('#processContainer').html('<div class="text-center" style="padding: 50px 0;"><i class="fa fa-spinner fa-spin fa-2x" style="color: #e85a4f;"></i><p>正在刷新...</p></div>');
            loadProcessData();
        }
        
        function showProcessError(msg) {
            $('#currentStepDetails').html('<div class="alert alert-danger"><i class="fa fa-exclamation-triangle"></i> ' + msg + '</div>');
        }
        
        // ========== 缴费相关函数 ==========
        function toggleSelectAll() {
            var isChecked = $('#selectAll').prop('checked');
            $('.item-check').prop('checked', isChecked);
            updateSelectedInfo();
        }
        
        function updateSelectedInfo() {
            var count = $('.item-check:checked').length;
            $('#selectedInfo').text('已选择 ' + count + ' 项');
            console.log('updateSelectedInfo被调用，选中数量: ' + count);
        }
        
        function getSelectedIds() {
            var ids = [];
            $('.item-check:checked').each(function() {
                ids.push($(this).val());
            });
            return ids;
        }
        
        function submitPayment(method) {
            var ids = getSelectedIds();
            if (ids.length === 0) {
                alert('请至少选择一条待支付的记录！');
                return;
            }
            if (!confirm('确认使用' + (method === 'wechat' ? '微信' : '支付宝') + '支付选中的 ' + ids.length + ' 项费用？')) {
                return;
            }
            
            $('#loadingOverlay').css('display', 'flex');
            
            $.ajax({
                url: 'Payment_Tool',
                type: 'POST',
                data: {
                    action: 'orderPay',
                    ids: ids.join(','),
                    method: method
                },
                dataType: 'text',  
                success: function(responseText) {
                    $('#loadingOverlay').hide();
                    try {
                        var res = JSON.parse(responseText);
                        if (res.success) {
                            alert('支付成功！');
                            dataLoaded.payment = false;
                            loadPaymentData();
                            if (dataLoaded.profile) {
                                loadProfileStats();
                            }
                        } else {
                            alert('支付失败：' + (res.message || '未知错误'));
                        }
                    } catch (e) {
                        console.log('响应解析失败，刷新列表检查状态');
                        dataLoaded.payment = false;
                        loadPaymentData();
                    }
                },
                error: function(xhr, status, error) {
                    $('#loadingOverlay').hide();
                    alert('请求异常，正在刷新列表检查支付状态...');
                    dataLoaded.payment = false;
                    loadPaymentData();
                }
            });
        }
        
        $(document).on('click', '.item-check', function() {
            updateSelectedInfo();
        });
        
        $(document).on('click', '#selectAll', function() {
            toggleSelectAll();
        });
        
        // ========== 浏览器后退处理 ==========
        window.onpopstate = function(event) {
            if (event.state && event.state.tab) {
                switchTab(event.state.tab);
            }
        };
        
        // ========== 个人信息相关函数 ==========
        // 加载个人信息页面
        function loadProfileData() {
            console.log('加载个人信息...');
            $.ajax({
                url: 'profile.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    if (response && response.trim()) {
                        $('#profileContainer').html(response);
                        // 加载表单数据
                        loadProfileFormData();
                    } else {
                        $('#profileContainer').html('<div class="no-data"><i class="fa fa-user-circle"></i><p>加载个人信息失败</p></div>');
                    }
                    dataLoaded.profile = true;
                },
                error: function() {
                    $('#profileContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载个人信息失败</div>');
                    dataLoaded.profile = true;
                }
            });
        }
        
        // 加载表单数据
        function loadProfileFormData() {
            $.ajax({
                url: 'UserManageService_Tool',
                type: 'POST',
                data: { action: 'getProfile' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        var data = res.data;
                        $('#profileUsername').val(data.username || '');
                        $('#profilePhoneNum').val(data.phoneNum || '');
                        
                        // 设置性别
                        if (data.gender) {
                            $('input[name="profileGender"][value="' + data.gender + '"]').prop('checked', true);
                        }
                        
                        // 更新显示名称
                        $('#displayName').text(data.username || '用户');
                    } else {
                        showProfileError(res.message || '加载信息失败');
                    }
                },
                error: function() {
                    showProfileError('网络请求失败');
                }
            });
            
            // 加载统计数据
            loadProfileStats();
        }
        
        // 加载统计数据（就诊次数、待缴费用）
        function loadProfileStats() {
            $.ajax({
                url: 'UserManageService_Tool',
                type: 'POST',
                data: { action: 'getPatientStats' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        var data = res.data;
                        $('#visitCount').text(data.visitCount || 0);
                        var fee = parseFloat(data.pendingFee) || 0;
                        if (fee > 0) {
                            $('#pendingFee').text('¥' + fee.toFixed(2));
                        } else {
                            $('#pendingFee').text('¥0.00');
                        }
                    } else {
                        $('#visitCount').text('0');
                        $('#pendingFee').text('¥0.00');
                    }
                },
                error: function() {
                    $('#visitCount').text('0');
                    $('#pendingFee').text('¥0.00');
                }
            });
        }
        
        // 保存个人信息
        function saveProfile() {
            var username = $('#profileUsername').val().trim();
            var phoneNum = $('#profilePhoneNum').val().trim();
            var gender = $('input[name="profileGender"]:checked').val();
            
            if (!username) {
                showProfileError('用户名不能为空');
                return;
            }
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'UserManageService_Tool',
                type: 'POST',
                data: {
                    action: 'updateProfile',
                    username: username,
                    phoneNum: phoneNum,
                    gender: gender || ''
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showProfileSuccess('信息保存成功！');
                        // 更新显示
                        $('#displayName').text(username);
                        // 更新导航栏用户名
                        $('.navbar-right li:first a').html('<i class="fa fa-user"></i> ' + username);
                    } else {
                        showProfileError(res.message || '保存失败');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showProfileError('网络请求失败');
                }
            });
        }
        
        // 修改密码
        function changePassword() {
            var oldPassword = $('#oldPassword').val();
            var newPassword = $('#newPassword').val();
            var confirmPassword = $('#confirmPassword').val();
            
            if (!oldPassword) {
                showProfileError('请输入原密码');
                return;
            }
            
            if (!newPassword || newPassword.length < 6) {
                showProfileError('新密码长度至少6位');
                return;
            }
            
            if (newPassword !== confirmPassword) {
                showProfileError('两次输入的密码不一致');
                return;
            }
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'UserManageService_Tool',
                type: 'POST',
                data: {
                    action: 'updatePassword',
                    oldPassword: oldPassword,
                    newPassword: newPassword
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showProfileSuccess('密码修改成功！');
                        // 清空密码输入框
                        $('#oldPassword').val('');
                        $('#newPassword').val('');
                        $('#confirmPassword').val('');
                        // 重置密码强度
                        $('#strengthBar').removeClass('strength-weak strength-medium strength-strong');
                        $('#strengthHint').text('密码长度至少6位');
                    } else {
                        showProfileError(res.message || '密码修改失败');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showProfileError('网络请求失败');
                }
            });
        }
        
        // 检查密码强度
        function checkPasswordStrength() {
            var password = $('#newPassword').val();
            var bar = $('#strengthBar');
            var hint = $('#strengthHint');
            
            bar.removeClass('strength-weak strength-medium strength-strong');
            
            if (password.length === 0) {
                hint.text('密码强度');
                return;
            }
            
            if (password.length < 6) {
                bar.addClass('strength-weak');
                hint.text('弱 - 至少6位');
                return;
            }
            
            var strength = 0;
            if (password.length >= 8) strength++;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
            if (/\d/.test(password)) strength++;
            if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++;
            
            if (strength <= 1) {
                bar.addClass('strength-weak');
                hint.text('弱');
            } else if (strength <= 2) {
                bar.addClass('strength-medium');
                hint.text('中等');
            } else {
                bar.addClass('strength-strong');
                hint.text('强');
            }
        }
        
        // 显示/隐藏密码
        function togglePassword(inputId, btn) {
            var input = document.getElementById(inputId);
            var icon = btn.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }
        
        // 显示个人信息成功消息
        function showProfileSuccess(msg) {
            $('#profileSuccessAlert').html('<i class="fa fa-check-circle"></i> ' + msg).fadeIn();
            $('#profileErrorAlert').hide();
            setTimeout(function() {
                $('#profileSuccessAlert').fadeOut();
            }, 3000);
        }
        
        // 显示个人信息错误消息
        function showProfileError(msg) {
            $('#profileErrorAlert').html('<i class="fa fa-exclamation-circle"></i> ' + msg).fadeIn();
            $('#profileSuccessAlert').hide();
            setTimeout(function() {
                $('#profileErrorAlert').fadeOut();
            }, 3000);
        }
        
        // 绑定个人信息表单提交
        $(document).on('submit', '#profileForm', function(e) {
            e.preventDefault();
            saveProfile();
        });
        
        $(document).on('submit', '#passwordForm', function(e) {
            e.preventDefault();
            changePassword();
        });
        
        // ========== 预约挂号相关函数 ==========
        let selectedDoctorId = null;
        let selectedOrgId = 1; // 默认机构ID
        
        function loadRegistrationData() {
            console.log('加载预约挂号...');
            $.ajax({
                url: 'registration.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    if (response && response.trim()) {
                        $('#registrationContainer').html(response);
                        initRegistration();
                    } else {
                        $('#registrationContainer').html('<div class="no-data"><i class="fa fa-calendar"></i><p>加载失败</p></div>');
                    }
                    dataLoaded.registration = true;
                },
                error: function() {
                    $('#registrationContainer').html('<div class="error-alert"><i class="fa fa-exclamation-circle"></i> 加载预约挂号失败</div>');
                    dataLoaded.registration = true;
                }
            });
        }
        
        function initRegistration() {
            // 设置最小日期为今天
            var today = new Date().toISOString().split('T')[0];
            $('#appointmentDate').attr('min', today).val(today);
            
            // 加载科室
            loadSpecialties();
            
            // 加载预约列表
            loadAppointments();
            
            // 绑定表单提交
            $(document).off('submit', '#appointmentForm').on('submit', '#appointmentForm', function(e) {
                e.preventDefault();
                submitAppointment();
            });
        }
        
        function loadSpecialties() {
            $.ajax({
                url: 'Registration_Tool',
                type: 'POST',
                data: { action: 'getSpecialties' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        var html = '<option value="">-- 请选择科室 --</option>';
                        res.data.forEach(function(specialty) {
                            html += '<option value="' + specialty + '">' + specialty + '</option>';
                        });
                        $('#specialtySelect').html(html);
                    }
                },
                error: function() {
                    showRegError('加载科室失败');
                }
            });
        }
        
        function onSpecialtyChange() {
            var specialty = $('#specialtySelect').val();
            updateRegStep(1);
            
            if (!specialty) {
                $('#doctorList').html('<div class="reg-empty-hint">请先选择科室</div>');
                selectedDoctorId = null;
                checkSubmitEnabled();
                return;
            }
            
            updateRegStep(2);
            loadDoctors(specialty);
        }
        
        function loadDoctors(specialty) {
            $('#doctorList').html('<div class="reg-loading"><i class="fa fa-spinner fa-spin"></i> 加载医生...</div>');
            
            $.ajax({
                url: 'Registration_Tool',
                type: 'POST',
                data: { action: 'getDoctors', specialty: specialty },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data && res.data.length > 0) {
                        var html = '';
                        res.data.forEach(function(doctor) {
                            html += '<div class="reg-doctor-card" data-doctor-id="' + doctor.doctorId + '" onclick="selectDoctor(this, ' + doctor.doctorId + ')">';
                            html += '<div class="reg-doctor-name">' + doctor.username + '</div>';
                            html += '<div class="reg-doctor-info">' + doctor.title + ' | ' + doctor.gender + '</div>';
                            html += '</div>';
                        });
                        $('#doctorList').html(html);
                    } else {
                        $('#doctorList').html('<div class="reg-empty-hint">该科室暂无医生</div>');
                    }
                },
                error: function() {
                    $('#doctorList').html('<div class="reg-empty-hint">加载医生失败</div>');
                }
            });
        }
        
        function selectDoctor(element, doctorId) {
            $('.reg-doctor-card').removeClass('selected');
            $(element).addClass('selected');
            selectedDoctorId = doctorId;
            $('#selectedDoctorId').val(doctorId);
            updateRegStep(3);
            checkSubmitEnabled();
        }
        
        function updateRegStep(step) {
            $('.reg-step').each(function(index) {
                if (index + 1 <= step) {
                    $(this).addClass('active');
                } else {
                    $(this).removeClass('active');
                }
            });
        }
        
        function checkSubmitEnabled() {
            var specialty = $('#specialtySelect').val();
            var date = $('#appointmentDate').val();
            
            if (specialty && selectedDoctorId && date) {
                $('#submitBtn').prop('disabled', false);
            } else {
                $('#submitBtn').prop('disabled', true);
            }
        }
        
        function submitAppointment() {
            var date = $('#appointmentDate').val();
            var timeSlot = $('#timeSlot').val();
            var appointmentDate = date + ' ' + timeSlot;
            
            if (!selectedDoctorId) {
                showRegError('请选择医生');
                return;
            }
            
            $('#loadingOverlay').css('display', 'flex');
            
            $.ajax({
                url: 'Registration_Tool',
                type: 'POST',
                data: {
                    action: 'createAppointment',
                    doctorId: selectedDoctorId,
                    organizationId: selectedOrgId,
                    appointmentDate: appointmentDate
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showRegSuccess('预约成功！医生：' + res.doctorName + '，时间：' + res.appointmentDate);
                        // 重置表单
                        $('#specialtySelect').val('');
                        $('#doctorList').html('<div class="reg-empty-hint">请先选择科室</div>');
                        selectedDoctorId = null;
                        updateRegStep(1);
                        checkSubmitEnabled();
                        // 刷新预约列表
                        loadAppointments();
                    } else {
                        showRegError(res.message || '预约失败');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showRegError('网络请求失败');
                }
            });
        }
        
        function loadAppointments() {
            $('#appointmentList').html('<div class="reg-loading"><i class="fa fa-spinner fa-spin"></i> 加载中...</div>');
            
            $.ajax({
                url: 'Registration_Tool',
                type: 'POST',
                data: { action: 'getAppointments' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data && res.data.length > 0) {
                        var html = '';
                        res.data.forEach(function(apt) {
                            // 状态转换为中文
                            var displayStatus = apt.status;
                            if (apt.status === 'completed' || apt.status === '已完成') displayStatus = '已完成';
                            else if (apt.status === 'inprogress' || apt.status === 'in_progress' || apt.status === '进行中' || apt.status === '已预约') displayStatus = '已预约';
                            else if (apt.status === 'cancelled' || apt.status === 'canceled' || apt.status === '已取消') displayStatus = '已取消';
                            
                            var statusClass = 'status-booked';
                            if (displayStatus === '已完成') statusClass = 'status-done';
                            else if (displayStatus === '已取消') statusClass = 'status-cancel';
                            
                            html += '<div class="reg-apt-item">';
                            html += '<div class="reg-apt-header">';
                            html += '<span class="reg-apt-doctor">' + apt.doctorName + ' ' + apt.title + '</span>';
                            html += '<span class="reg-apt-status ' + statusClass + '">' + displayStatus + '</span>';
                            html += '</div>';
                            html += '<div class="reg-apt-info">';
                            html += '<span><i class="fa fa-stethoscope"></i> ' + apt.specialty + '</span>';
                            html += '<span><i class="fa fa-hospital-o"></i> ' + apt.organizationName + '</span>';
                            html += '<span><i class="fa fa-clock-o"></i> ' + apt.createdAt + '</span>';
                            html += '</div>';
                            if (displayStatus === '已预约') {
                                html += '<div class="reg-apt-actions">';
                                html += '<button class="reg-cancel-btn" onclick="cancelAppointment(' + apt.processId + ')">取消预约</button>';
                                html += '</div>';
                            }
                            html += '</div>';
                        });
                        $('#appointmentList').html(html);
                    } else {
                        $('#appointmentList').html('<div class="reg-empty"><i class="fa fa-calendar-o"></i><br>暂无预约记录</div>');
                    }
                },
                error: function() {
                    $('#appointmentList').html('<div class="reg-empty">加载失败</div>');
                }
            });
        }
        
        function cancelAppointment(processId) {
            if (!confirm('确定要取消这个预约吗？')) return;
            
            $.ajax({
                url: 'Registration_Tool',
                type: 'POST',
                data: { action: 'cancelAppointment', processId: processId },
                dataType: 'json',
                success: function(res) {
                    if (res.success) {
                        showRegSuccess('已取消预约');
                        loadAppointments();
                    } else {
                        showRegError(res.message || '取消失败');
                    }
                },
                error: function() {
                    showRegError('网络请求失败');
                }
            });
        }
        
        function showRegSuccess(msg) {
            $('#regSuccessAlert').html('<i class="fa fa-check-circle"></i> ' + msg).fadeIn();
            $('#regErrorAlert').hide();
            setTimeout(function() { $('#regSuccessAlert').fadeOut(); }, 4000);
        }
        
        function showRegError(msg) {
            $('#regErrorAlert').html('<i class="fa fa-exclamation-circle"></i> ' + msg).fadeIn();
            $('#regSuccessAlert').hide();
            setTimeout(function() { $('#regErrorAlert').fadeOut(); }, 4000);
        }
        
        // 监听日期变化
        $(document).on('change', '#appointmentDate', function() {
            checkSubmitEnabled();
        });
    </script>

    <!-- AI 助手悬浮按钮 -->
    <button class="ai-chat-btn" id="aiChatBtn" title="AI 健康助手">
        <i class="fa fa-commenting"></i>
    </button>

    <!-- AI 聊天窗口 -->
    <div class="ai-chat-window" id="aiChatWindow">
        <div class="ai-chat-window-inner">
            <div class="ai-chat-header">
                <div class="ai-chat-header-left">
                    <div class="ai-avatar">
                        <i class="fa fa-user-md"></i>
                    </div>
                    <div>
                        <div class="ai-chat-title">小医 AI 助手</div>
                        <div class="ai-chat-subtitle">基于 DeepSeek 大模型</div>
                    </div>
                </div>
                <button class="ai-chat-close" id="aiChatClose">
                    <i class="fa fa-times"></i>
                </button>
            </div>

            <div class="ai-chat-messages" id="aiChatMessages">
                <!-- 欢迎消息 -->
                <div class="ai-message ai">
                    <div class="ai-message-avatar">
                        <i class="fa fa-user-md"></i>
                    </div>
                    <div class="ai-message-body">
                        <div class="ai-message-content">
                            您好，<%= username %>！我是您的 AI 健康助手"小医"。我可以为您提供健康咨询、就医建议等服务。请问有什么可以帮助您的吗？
                        </div>
                        <div class="ai-message-time" id="aiWelcomeTime"></div>
                    </div>
                </div>
            </div>

            <div class="ai-chat-bottom">
                <div class="ai-quick-actions">
                    <button class="ai-quick-btn" onclick="sendQuickMessage('我最近的就诊情况怎么样？')">我的就诊情况</button>
                    <button class="ai-quick-btn" onclick="sendQuickMessage('有什么健康建议吗？')">健康建议</button>
                    <button class="ai-quick-btn" onclick="sendQuickMessage('如何预约挂号？')">预约挂号</button>
                </div>
                <div class="ai-chat-input-area">
                    <input type="text" class="ai-chat-input" id="aiChatInput" placeholder="输入您的问题..." maxlength="500">
                    <button class="ai-chat-send" id="aiChatSend">
                        <i class="fa fa-paper-plane"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script>
    // AI 助手功能
    (function() {
        var chatBtn = document.getElementById('aiChatBtn');
        var chatWindow = document.getElementById('aiChatWindow');
        var chatHeader = document.querySelector('.ai-chat-header');
        var chatClose = document.getElementById('aiChatClose');
        var chatInput = document.getElementById('aiChatInput');
        var chatSend = document.getElementById('aiChatSend');
        var chatMessages = document.getElementById('aiChatMessages');
        var isLoading = false;

        // ========== 按钮拖动功能 ==========
        var btnDragging = false;
        var btnHasMoved = false;
        var btnStartX, btnStartY, btnInitX, btnInitY;

        chatBtn.addEventListener('mousedown', function(e) {
            btnDragging = true;
            btnHasMoved = false;
            btnStartX = e.clientX;
            btnStartY = e.clientY;
            // 使用 getBoundingClientRect 获取实际位置
            var rect = chatBtn.getBoundingClientRect();
            btnInitX = rect.left;
            btnInitY = rect.top;
            chatBtn.classList.add('dragging');
            e.preventDefault();
        });

        document.addEventListener('mousemove', function(e) {
            if (!btnDragging) return;
            var dx = e.clientX - btnStartX;
            var dy = e.clientY - btnStartY;
            if (Math.abs(dx) > 3 || Math.abs(dy) > 3) {
                btnHasMoved = true;
                var newX = btnInitX + dx;
                var newY = btnInitY + dy;
                // 边界限制
                newX = Math.max(0, Math.min(window.innerWidth - 60, newX));
                newY = Math.max(0, Math.min(window.innerHeight - 60, newY));
                chatBtn.style.left = newX + 'px';
                chatBtn.style.top = newY + 'px';
                chatBtn.style.right = 'auto';
                chatBtn.style.bottom = 'auto';
            }
        });

        document.addEventListener('mouseup', function(e) {
            if (btnDragging) {
                btnDragging = false;
                chatBtn.classList.remove('dragging');
                if (!btnHasMoved) {
                    chatWindow.classList.add('show');
                    chatBtn.style.display = 'none'; // 隐藏按钮
                    chatInput.focus();
                }
            }
        });

        // ========== 聊天窗口拖动功能 ==========
        var winDragging = false;
        var winStartX, winStartY, winInitX, winInitY;

        chatHeader.addEventListener('mousedown', function(e) {
            if (e.target === chatClose || e.target.closest('.ai-chat-close')) return;
            winDragging = true;
            winStartX = e.clientX;
            winStartY = e.clientY;
            var rect = chatWindow.getBoundingClientRect();
            winInitX = rect.left;
            winInitY = rect.top;
            e.preventDefault();
        });

        document.addEventListener('mousemove', function(e) {
            if (!winDragging) return;
            var dx = e.clientX - winStartX;
            var dy = e.clientY - winStartY;
            var newX = winInitX + dx;
            var newY = winInitY + dy;
            // 边界限制
            newX = Math.max(0, Math.min(window.innerWidth - 380, newX));
            newY = Math.max(0, Math.min(window.innerHeight - 500, newY));
            chatWindow.style.left = newX + 'px';
            chatWindow.style.top = newY + 'px';
            chatWindow.style.right = 'auto';
            chatWindow.style.bottom = 'auto';
        });

        document.addEventListener('mouseup', function() {
            winDragging = false;
        });

        // 关闭聊天窗口
        chatClose.addEventListener('click', function(e) {
            e.stopPropagation();
            chatWindow.classList.remove('show');
            chatBtn.style.display = 'flex'; // 重新显示按钮
        });

        // 发送消息
        chatSend.addEventListener('click', sendMessage);
        chatInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });

        function sendMessage() {
            var message = chatInput.value.trim();
            if (!message || isLoading) return;

            // 显示用户消息
            appendMessage(message, 'user');
            chatInput.value = '';

            // 显示加载状态
            showTyping();
            isLoading = true;
            chatSend.disabled = true;

            // 发送请求到后端
            $.ajax({
                url: 'DeepSeekAI_Tool',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ message: message }),
                dataType: 'json',
                timeout: 60000,
                success: function(res) {
                    hideTyping();
                    if (res.success) {
                        appendMessage(res.reply, 'ai');
                    } else {
                        appendMessage(res.reply || '抱歉，出现了一些问题，请稍后再试。', 'ai');
                    }
                },
                error: function(xhr, status, error) {
                    hideTyping();
                    var errorMsg = '网络连接失败，请检查网络后重试。';
                    if (status === 'timeout') {
                        errorMsg = '响应超时，请稍后再试。';
                    }
                    appendMessage(errorMsg, 'ai');
                    console.log('AJAX Error:', status, error, xhr.responseText);
                },
                complete: function() {
                    isLoading = false;
                    chatSend.disabled = false;
                }
            });
        }

        function appendMessage(content, type) {
            var time = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
            var avatarIcon = type === 'ai' ? 'fa-user-md' : 'fa-user';

            var messageHtml =
                '<div class="ai-message ' + type + '">' +
                    '<div class="ai-message-avatar">' +
                        '<i class="fa ' + avatarIcon + '"></i>' +
                    '</div>' +
                    '<div class="ai-message-body">' +
                        '<div class="ai-message-content">' + escapeHtml(content) + '</div>' +
                        '<div class="ai-message-time">' + time + '</div>' +
                    '</div>' +
                '</div>';

            chatMessages.insertAdjacentHTML('beforeend', messageHtml);
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }

        function showTyping() {
            var typingHtml =
                '<div class="ai-message ai" id="aiTyping">' +
                    '<div class="ai-message-avatar">' +
                        '<i class="fa fa-user-md"></i>' +
                    '</div>' +
                    '<div class="ai-message-content">' +
                        '<div class="ai-typing">' +
                            '<div class="ai-typing-dot"></div>' +
                            '<div class="ai-typing-dot"></div>' +
                            '<div class="ai-typing-dot"></div>' +
                        '</div>' +
                    '</div>' +
                '</div>';
            chatMessages.insertAdjacentHTML('beforeend', typingHtml);
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }

        function hideTyping() {
            var typing = document.getElementById('aiTyping');
            if (typing) typing.remove();
        }

        function escapeHtml(text) {
            var div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // 快捷消息
        window.sendQuickMessage = function(msg) {
            chatInput.value = msg;
            sendMessage();
        };

        // 设置欢迎消息时间
        var welcomeTime = document.getElementById('aiWelcomeTime');
        if (welcomeTime) {
            welcomeTime.textContent = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
        }
    })();
    </script>
</body>
</html>

