<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    // 登录校验：未登录则跳转到登录页
    if (session.getAttribute("doctorId") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    Long doctorId = (Long) session.getAttribute("doctorId");
    String doctorName = (String) session.getAttribute("doctorName");
    String specialty = (String) session.getAttribute("specialty");
    
    // 获取并清除成功消息
    String successMsg = (String) session.getAttribute("successMsg");
    if (successMsg != null) {
        session.removeAttribute("successMsg");
    }
    
    // 获取当前 Tab（默认显示待诊患者）
    String currentTab = request.getParameter("tab");
    if (currentTab == null || currentTab.isEmpty()) {
        currentTab = "waiting";
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
    <title>医疗云系统 - 医生工作站</title>
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
            background-color: #f0f4f8;
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
            color: #059669;
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
            background-color: #059669 !important;
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
            background-color: #047857 !important;
        }
        
        .navbar-default .navbar-nav > li.active > a {
            background-color: #047857 !important;
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
            animation: fadeIn 0.3s ease;
        }
        
        .tab-content-area.active {
            display: block;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        /* ========== 待诊患者列表样式 ========== */
        .waiting-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            overflow: hidden;
            margin-bottom: 30px;
        }
        
        .waiting-header {
            background: #059669;
            color: white;
            padding: 25px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .waiting-header-left h3 {
            margin: 0 0 5px 0;
            font-size: 20px;
        }
        
        .waiting-subtitle {
            margin: 0;
            opacity: 0.85;
            font-size: 14px;
        }
        
        .waiting-stats {
            display: flex;
            gap: 35px;
        }
        
        .waiting-stat {
            text-align: center;
        }
        
        .waiting-stat .stat-num {
            font-size: 32px;
            font-weight: 700;
            display: block;
            line-height: 1.2;
        }
        
        .waiting-stat .stat-label {
            font-size: 14px;
            opacity: 0.9;
        }
        
        .waiting-toolbar {
            background: #f8f9fa;
            padding: 15px 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #e9ecef;
        }
        
        .toolbar-left {
            display: flex;
            gap: 15px;
            align-items: center;
        }
        
        .filter-select {
            padding: 8px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            background: white;
            font-size: 14px;
            min-width: 120px;
        }
        
        .btn-refresh {
            background: #059669;
            color: white;
            border: none;
            padding: 8px 20px;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-refresh:hover {
            background: #047857;
        }
        
        .patient-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .patient-table th {
            background: #f8f9fa;
            padding: 16px 20px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid #e9ecef;
            font-size: 14px;
        }
        
        .patient-table td {
            padding: 16px 20px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
        }
        
        .patient-table tr:hover {
            background: #f0fdf4;
        }
        
        .patient-name-cell {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .patient-avatar {
            width: 45px;
            height: 45px;
            background: #059669;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            font-size: 18px;
        }
        
        .patient-info-main .patient-name {
            font-weight: 600;
            color: #333;
            display: block;
            margin-bottom: 2px;
        }
        
        .patient-info-main .patient-id {
            font-size: 14px;
            color: #999;
        }
        
        .stage-badge {
            display: inline-block;
            padding: 5px 14px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 500;
        }
        
        .stage-register { background: #e3f2fd; color: #1976d2; }
        .stage-consult { background: #fff3e0; color: #f57c00; }
        .stage-examine { background: #fce4ec; color: #c2185b; }
        .stage-treat { background: #f3e5f5; color: #7b1fa2; }
        .stage-medicine { background: #e8f5e9; color: #388e3c; }
        .stage-complete { background: #e0f2f1; color: #00796b; }
        
        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 14px;
        }
        
        .status-waiting { background: #fff3cd; color: #856404; }
        .status-inprogress { background: #cce5ff; color: #004085; }
        .status-completed { background: #d4edda; color: #155724; }
        
        .btn-action {
            padding: 8px 18px;
            border-radius: 6px;
            font-size: 14px;
            border: none;
            cursor: pointer;
            transition: all 0.2s;
            margin-right: 8px;
        }
        
        .btn-action.btn-refresh {
            background: #17a2b8;
            color: white;
        }
        
        .btn-action.btn-refresh:hover {
            background: #138496;
        }
        
        .btn-start {
            background: #059669;
            color: white;
        }
        
        .btn-start:hover {
            background: #047857;
            color: white;
        }
        
        .btn-view {
            background: #e9ecef;
            color: #495057;
        }
        
        .btn-view:hover {
            background: #dee2e6;
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
        }
        
        .btn-back:hover {
            background: #5a6268;
            color: white;
        }
        
        /* ========== 就诊进程样式 ========== */
        .process-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            padding: 30px;
            margin-bottom: 30px;
        }
        
        .process-empty-state {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            padding: 80px 30px;
            text-align: center;
        }
        
        .empty-icon {
            font-size: 80px;
            color: #d1fae5;
            margin-bottom: 20px;
        }
        
        .empty-icon i {
            margin: 0;
        }
        
        .process-empty-state h3 {
            color: #333;
            margin: 0 0 10px 0;
        }
        
        .process-empty-state p {
            color: #666;
            margin: 0 0 25px 0;
        }
        
        .btn-back-to-list {
            background: #059669;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 8px;
            font-size: 15px;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-back-to-list:hover {
            background: #047857;
        }
        
        .process-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 25px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .current-patient-info {
            display: flex;
            align-items: center;
            gap: 18px;
        }
        
        .current-patient-avatar {
            width: 65px;
            height: 65px;
            background: #059669;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 28px;
        }
        
        .current-patient-avatar i {
            margin: 0;
        }
        
        .current-patient-details h3 {
            margin: 0 0 5px 0;
            color: #333;
            font-size: 22px;
        }
        
        .current-patient-details p {
            margin: 0;
            color: #666;
            font-size: 14px;
        }
        
        .process-actions {
            display: flex;
            gap: 12px;
        }
        
        /* 进度条 */
        .progress-timeline {
            display: flex;
            justify-content: space-between;
            position: relative;
            margin: 45px 0;
            padding: 0 20px;
        }
        
        .progress-timeline::before {
            content: '';
            position: absolute;
            top: 25px;
            left: 40px;
            right: 40px;
            height: 4px;
            background: #e0e0e0;
            z-index: 1;
        }
        
        .progress-line-fill {
            position: absolute;
            top: 25px;
            left: 40px;
            height: 4px;
            background: #059669;
            z-index: 2;
            transition: width 0.5s ease;
        }
        
        .progress-node {
            position: relative;
            z-index: 3;
            text-align: center;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        .progress-node:hover {
            transform: translateY(-3px);
        }
        
        .node-circle {
            width: 55px;
            height: 55px;
            border-radius: 50%;
            background: #e0e0e0;
            margin: 0 auto 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .node-circle i {
            margin: 0;
        }
        
        .progress-node.completed .node-circle {
            background: #059669;
            color: white;
        }
        
        .progress-node.active .node-circle {
            background: #059669;
            color: white;
            box-shadow: 0 0 0 6px rgba(5, 150, 105, 0.25);
            transform: scale(1.1);
        }
        
        .node-label {
            font-size: 14px;
            color: #666;
            font-weight: 500;
        }
        
        .progress-node.active .node-label,
        .progress-node.completed .node-label {
            color: #059669;
            font-weight: 600;
        }
        
        /* 表单区域 */
        .form-section {
            background: #f0fdf4;
            border-radius: 12px;
            padding: 30px;
            border-left: 4px solid #059669;
        }
        
        .form-section h4 {
            color: #059669;
            margin: 0 0 25px 0;
            font-size: 18px;
        }
        
        .form-row {
            display: flex;
            gap: 25px;
            margin-bottom: 20px;
        }
        
        .form-group {
            flex: 1;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 10px;
            font-weight: 500;
            color: #555;
            font-size: 14px;
        }
        
        .form-control {
            width: 100%;
            padding: 14px 18px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        
        .form-control:focus {
            outline: none;
            border-color: #059669;
            box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
        }
        
        textarea.form-control {
            min-height: 130px;
            resize: vertical;
        }
        
        .btn-group-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
            padding-top: 25px;
            border-top: 1px solid #e9ecef;
        }
        
        .btn-save {
            background: #059669;
            color: white;
            padding: 14px 35px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-save:hover {
            background: #047857;
        }
        
        .btn-next-stage {
            background: #17a2b8;
            color: white;
            padding: 14px 35px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-next-stage:hover {
            background: #138496;
        }
        
        /* ========== 病历管理样式 ========== */
        .record-wrapper {
            display: flex;
            gap: 25px;
            min-height: 600px;
        }
        
        .record-list-panel {
            width: 350px;
            flex-shrink: 0;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }
        
        .record-list-header {
            background: #059669;
            color: white;
            padding: 20px;
        }
        
        .record-list-header h4 {
            margin: 0 0 15px 0;
            font-size: 16px;
        }
        
        .search-box-mini {
            display: flex;
            gap: 8px;
        }
        
        .search-input-mini {
            flex: 1;
            padding: 8px 12px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
        }
        
        .btn-search-mini {
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            padding: 8px 12px;
            border-radius: 6px;
            cursor: pointer;
        }
        
        .record-list-body {
            flex: 1;
            overflow-y: auto;
            padding: 15px;
        }
        
        .record-list-item {
            padding: 15px;
            border: 1px solid #eee;
            border-radius: 8px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .record-list-item:hover {
            border-color: #059669;
            background: #f0fdf4;
        }
        
        .record-list-item.active {
            border-color: #059669;
            background: #ecfdf5;
        }
        
        .record-list-item .item-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        
        .record-list-item .item-info {
            font-size: 14px;
            color: #888;
        }
        
        .record-detail-panel {
            flex: 1;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        
        .record-empty-state {
            padding: 100px 30px;
            text-align: center;
        }
        
        .record-empty-state .empty-icon {
            font-size: 70px;
            color: #d1fae5;
        }
        
        .record-empty-state h3 {
            color: #333;
            margin: 20px 0 10px 0;
        }
        
        .record-empty-state p {
            color: #888;
        }
        
        .record-detail-content {
            padding: 0;
        }
        
        .record-detail-header {
            background: #059669;
            color: white;
            padding: 20px 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .record-patient-name {
            font-size: 18px;
            font-weight: 600;
            margin-right: 15px;
        }
        
        .record-id {
            opacity: 0.8;
            font-size: 14px;
        }
        
        .record-detail-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn-edit, .btn-print {
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            padding: 8px 16px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-edit:hover, .btn-print:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .record-section {
            padding: 20px 25px;
            border-bottom: 1px solid #eee;
        }
        
        .record-section h5 {
            color: #059669;
            margin: 0 0 18px 0;
            font-size: 16px;
        }
        
        .record-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }
        
        .record-item label {
            display: block;
            font-size: 14px;
            color: #888;
            margin-bottom: 5px;
        }
        
        .record-input, .record-textarea {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.2s;
            background: #fafafa;
        }
        
        .record-input:focus, .record-textarea:focus {
            outline: none;
            border-color: #059669;
            background: white;
        }
        
        .record-input[readonly], .record-textarea[readonly] {
            background: #f5f5f5;
            cursor: default;
        }
        
        .record-full-width {
            margin-bottom: 15px;
        }
        
        .record-full-width label {
            display: block;
            font-size: 14px;
            color: #888;
            margin-bottom: 5px;
        }
        
        .record-form-actions {
            padding: 20px 25px;
            background: #f8f9fa;
            display: flex;
            gap: 15px;
            justify-content: flex-end;
        }
        
        .btn-save-record {
            background: #059669;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 8px;
            cursor: pointer;
        }
        
        .btn-cancel-edit {
            background: #e9ecef;
            color: #666;
            border: none;
            padding: 12px 25px;
            border-radius: 8px;
            cursor: pointer;
        }
        
        /* ========== 弹窗样式 ========== */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9998;
        }
        
        .modal-content {
            background: white;
            border-radius: 12px;
            max-width: 600px;
            width: 90%;
            max-height: 80vh;
            overflow: hidden;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        .modal-header {
            background: linear-gradient(135deg, #00b894, #00cec9);
            color: white;
            padding: 18px 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .modal-header h4 {
            margin: 0;
        }
        
        .modal-close {
            background: none;
            border: none;
            color: white;
            font-size: 20px;
            cursor: pointer;
        }
        
        .modal-body {
            padding: 25px;
            max-height: 400px;
            overflow-y: auto;
        }
        
        .modal-footer {
            padding: 15px 25px;
            background: #f8f9fa;
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }
        
        .btn-view-history {
            background: #00b894;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
        }
        
        .btn-close-modal {
            background: #e9ecef;
            color: #666;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
        }
        
        /* ========== 提示框样式 ========== */
        .doctor-alert {
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .doctor-alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .doctor-alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        /* ========== 加载动画 ========== */
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
            border-top: 5px solid #059669;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-bottom: 20px;
        }
        
        .loading-text {
            font-size: 18px;
            color: #059669;
            font-weight: bold;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .loading-placeholder {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .loading-placeholder i {
            color: #059669;
            display: block;
            margin-bottom: 15px;
        }
        
        .no-data {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .no-data i {
            font-size: 60px;
            color: #d1fae5;
            display: block;
            margin-bottom: 20px;
        }
        
        /* ========== 响应式 ========== */
        @media (max-width: 992px) {
            .record-wrapper {
                flex-direction: column;
            }
            .record-list-panel {
                width: 100%;
                max-height: 300px;
            }
            .record-grid {
                grid-template-columns: 1fr;
            }
            .form-row {
                flex-direction: column;
            }
            .waiting-header {
                flex-direction: column;
                text-align: center;
                gap: 20px;
            }
            .waiting-stats {
                justify-content: center;
            }
        }
        
        /* ========== 就诊进程阶段表单 ========== */
        .stage-forms {
            margin-top: 20px;
        }
        
        .stage-form {
            animation: fadeIn 0.3s ease;
        }
        
        .form-row {
            display: flex;
            gap: 20px;
            margin-bottom: 15px;
        }
        
        .form-row .form-group {
            flex: 1;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }
        
        .form-group label i {
            margin-right: 8px;
            color: #059669;
        }
        
        .form-control {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            border-color: #27a94b;
            box-shadow: 0 0 0 3px rgba(39, 169, 75, 0.1);
            outline: none;
        }
        
        textarea.form-control {
            min-height: 100px;
            resize: vertical;
        }
        
        select.form-control {
            height: auto;
            line-height: 1.5;
            padding: 12px 15px;
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='%23333' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M2 5l6 6 6-6'/%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right 12px center;
            background-size: 12px;
            padding-right: 35px;
            cursor: pointer;
        }
        
        .location-info {
            margin-top: 10px;
            padding: 12px;
            background: #ecfdf5;
            border-radius: 8px;
            border-left: 3px solid #059669;
        }
        
        .reminder-display {
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            color: #666;
        }
        
        .patient-basic-info {
            padding: 15px;
            background: #ecfdf5;
            border-radius: 8px;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 10px;
        }
        
        .patient-basic-info .info-item {
            display: flex;
            gap: 8px;
        }
        
        .patient-basic-info .info-label {
            color: #888;
            min-width: 60px;
        }
        
        .patient-basic-info .info-value {
            color: #333;
            font-weight: 500;
        }
        
        /* ========== 支付状态栏 ========== */
        .payment-status-bar {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 12px 15px;
            background: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
        }
        
        .status-badge.status-paid {
            background: #ecfdf5;
            color: #059669;
        }
        
        .status-badge.status-unpaid {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .status-badge.status-inprogress {
            background: #e3f2fd;
            color: #1976d2;
        }
        
        /* ========== 按钮组 ========== */
        .btn-group-actions {
            display: flex;
            gap: 15px;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #f0f0f0;
        }
        
        .btn-prev-stage,
        .btn-next-stage,
        .btn-save {
            padding: 12px 25px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            border: none;
        }
        
        .btn-prev-stage {
            background: #f0f0f0;
            color: #666;
        }
        
        .btn-prev-stage:hover {
            background: #e0e0e0;
        }
        
        .btn-save {
            background: #ecfdf5;
            color: #059669;
            border: 1px solid #059669;
        }
        
        .btn-save:hover {
            background: #059669;
            color: white;
        }
        
        .btn-next-stage {
            background: #059669;
            color: white;
            margin-left: auto;
        }
        
        .btn-next-stage:hover {
            background: #047857;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
        }
        
        .btn-next-stage:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            transform: none !important;
        }
        
        .btn-add-payment {
            padding: 10px 20px;
            background: #059669;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s ease;
            margin-bottom: 15px;
        }
        
        .btn-add-payment:hover {
            background: #047857;
            transform: translateY(-2px);
        }
        
        /* ========== 收费项目列表 ========== */
        .payment-items-list {
            border: 1px solid #e8e8e8;
            border-radius: 8px;
            max-height: 300px;
            overflow-y: auto;
        }
        
        .payment-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 15px;
            border-bottom: 1px solid #f0f0f0;
            transition: background 0.2s ease;
        }
        
        .payment-item:hover {
            background: #f0fdf4;
        }
        
        .payment-item:last-child {
            border-bottom: none;
        }
        
        .payment-item-info {
            flex: 1;
        }
        
        .payment-item-name {
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .payment-item-status {
            font-size: 14px;
            padding: 2px 8px;
            border-radius: 10px;
        }
        
        .payment-item-status.status-paid {
            background: #ecfdf5;
            color: #059669;
        }
        
        .payment-item-status.status-unpaid {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .payment-item-amount {
            font-size: 16px;
            font-weight: 700;
            color: #059669;
            min-width: 100px;
            text-align: right;
        }
        
        .payment-item-actions {
            margin-left: 15px;
        }
        
        .payment-item-actions .btn-small {
            padding: 5px 12px;
            font-size: 14px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
        }
        
        .payment-item-actions .btn-cancel {
            background: #fff5f5;
            color: #f44336;
        }
        
        .payment-item-actions .btn-cancel:hover {
            background: #f44336;
            color: white;
        }
        
        .payment-total {
            padding: 15px;
            background: #f0fdf4;
            border-top: 2px solid #059669;
            text-align: right;
            font-size: 16px;
            font-weight: 700;
            color: #059669;
        }
        
        /* ========== 模态框 ========== */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            animation: fadeIn 0.2s ease;
        }
        
        .modal-content {
            background: white;
            border-radius: 15px;
            max-width: 500px;
            width: 90%;
            max-height: 90vh;
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            animation: slideUp 0.3s ease;
            position: relative;
        }
        
        .medicine-modal-content {
            max-width: 600px;
            max-height: 85vh;
            display: flex;
            flex-direction: column;
        }
        
        .medicine-modal-content .modal-body {
            flex: 1;
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }
        
        @keyframes slideUp {
            from { transform: translateY(30px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        
        .modal-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 18px 20px;
            background: #059669;
            color: white;
            position: relative;
        }
        
        .modal-header h4 {
            margin: 0;
            font-size: 18px;
            flex: 1;
        }
        
        .modal-close {
            position: absolute;
            top: 12px;
            right: 12px;
            background: rgba(255,255,255,0.2);
            border: none;
            color: white;
            width: 32px;
            height: 32px;
            border-radius: 50%;
            font-size: 16px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s ease;
            z-index: 10;
            padding: 0;
            line-height: 1;
        }
        
        .modal-close i {
            display: block;
            width: 100%;
            text-align: center;
            line-height: 32px;
            margin-left: 5px;
        }
        
        .modal-close:hover {
            background: rgba(255,255,255,0.3);
        }
        
        .modal-body {
            padding: 20px;
            max-height: 60vh;
            overflow-y: auto;
        }

        .medicine-modal-content .modal-body {
            max-height: none;
            overflow-y: visible;
        }
        
        .modal-footer {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            padding: 15px 20px;
            background: #f8f9fa;
            border-top: 1px solid #e8e8e8;
        }
        
        .btn-cancel-modal {
            padding: 10px 25px;
            background: #f0f0f0;
            color: #666;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .btn-cancel-modal:hover {
            background: #e0e0e0;
        }
        
        .btn-save-modal {
            padding: 10px 25px;
            background: #059669;
            color: white;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .btn-save-modal:hover {
            background: #047857;
        }
        
        /* ========== 药品选择相关 ========== */
        .medicine-search-box {
            margin-bottom: 15px;
        }
        
        .medicine-selection-list {
            max-height: 350px;
            overflow-y: auto;
            border: 1px solid #e8e8e8;
            border-radius: 8px;
            flex: 1;
            min-height: 200px;
        }
        
        .medicine-selection-item {
            display: flex;
            align-items: center;
            padding: 12px 15px;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .medicine-selection-item:hover {
            background: #f0fdf4;
        }
        
        .medicine-selection-item.selected {
            background: #ecfdf5;
            border-left: 3px solid #059669;
        }
        
        .medicine-selection-item:last-child {
            border-bottom: none;
        }
        
        .quantity-btn {
            width: 28px;
            height: 28px;
            border: 1px solid #ddd;
            background: white;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .quantity-btn:hover {
            background: #059669;
            color: white;
            border-color: #059669;
        }
        
        .medicine-quantity-input {
            width: 50px !important;
            text-align: center;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        
        .selected-medicines-summary {
            padding: 15px;
            background: #ecfdf5;
            border-radius: 8px;
            margin-top: 15px;
            color: #059669;
            font-weight: 600;
            text-align: center;
        }
        
        /* ========== 药品悬停提示 ========== */
        .medicine-tooltip {
            position: fixed;
            background: white;
            border-radius: 10px;
            padding: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            z-index: 10000;
            display: none;
            min-width: 280px;
            max-width: 350px;
        }
        
        .medicine-tooltip h4 {
            margin: 0 0 12px 0;
            color: #059669;
            font-size: 16px;
            border-bottom: 2px solid #ecfdf5;
            padding-bottom: 8px;
        }
        
        .medicine-tooltip table {
            width: 100%;
        }
        
        .medicine-tooltip td {
            padding: 5px 0;
        }
        
        .medicine-tooltip td.label {
            color: #888;
            width: 80px;
        }
        
        .medicine-tooltip td.value {
            color: #333;
        }
        
        .medicine-tooltip td.price {
            color: #27a94b;
            font-weight: 700;
        }
        
        /* ========== 完成阶段 ========== */
        .completion-section {
            text-align: center;
            padding: 40px;
        }
        
        .completion-icon {
            font-size: 80px;
            color: #059669;
            margin-bottom: 20px;
        }
        
        .completion-section h3 {
            color: #059669;
            font-size: 24px;
            margin-bottom: 10px;
        }
        
        .completion-section p {
            color: #666;
            margin-bottom: 30px;
        }
        
        .completion-summary {
            background: #f0fdf4;
            border-radius: 10px;
            padding: 20px;
            text-align: left;
            margin-bottom: 20px;
        }
        
        .completion-summary h5 {
            color: #059669;
            margin-bottom: 15px;
        }
        
        /* 图片上传区域样式 */
        .image-upload-area {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .btn-upload-image {
            background: linear-gradient(135deg, #059669, #047857);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
        }
        
        .btn-upload-image:hover {
            background: linear-gradient(135deg, #047857, #065f46);
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(5, 150, 105, 0.4);
        }
        
        .btn-upload-image:active {
            transform: translateY(0);
        }
        
        .btn-upload-image i {
            font-size: 16px;
        }
        
        .upload-hint {
            font-size: 12px;
            color: #888;
        }
        
        .image-preview-container {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 10px;
        }
        
        .image-preview-item {
            position: relative;
            width: 120px;
            height: 120px;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            transition: transform 0.2s;
        }
        
        .image-preview-item:hover {
            transform: scale(1.05);
        }
        
        .image-preview-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        
        .image-preview-item .remove-image {
            position: absolute;
            top: 5px;
            right: 5px;
            width: 24px;
            height: 24px;
            background: rgba(220, 53, 69, 0.9);
            color: white;
            border: none;
            border-radius: 50%;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            opacity: 0;
            transition: opacity 0.2s;
        }
        
        .image-preview-item:hover .remove-image {
            opacity: 1;
        }
        
        .image-preview-item .remove-image:hover {
            background: rgba(200, 35, 51, 1);
        }
        
        /* 病历页面图片上传区域 */
        .record-image-upload-area {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 15px;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 8px;
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
    <div id="successToast" style="position:fixed;top:20px;left:50%;transform:translateX(-50%);background:#059669;color:#fff;padding:12px 24px;border-radius:8px;font-size:15px;z-index:9999;box-shadow:0 4px 12px rgba(0,0,0,0.15);">
        <i class="fa fa-check"></i> <%= successMsg %>
    </div>
    <script>setTimeout(function(){document.getElementById('successToast').style.display='none';},3000);</script>
    <% } %>

    <!-- Header -->
    <div class="header">
        <div class="container">
            <h1><i class="fa fa-stethoscope"></i> 医生工作站</h1>
            <p>智慧诊疗，高效服务</p>
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
                    <li id="tab-waiting" class="<%= "waiting".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('waiting')"><i class="fa fa-users"></i> 待诊患者</a>
                    </li>
                    <li id="tab-process" class="<%= "process".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('process')"><i class="fa fa-heartbeat"></i> 就诊进程</a>
                    </li>
                    <li id="tab-record" class="<%= "record".equals(currentTab) ? "active" : "" %>">
                        <a href="javascript:void(0)" onclick="switchTab('record')"><i class="fa fa-file-text"></i> 病历管理</a>
                    </li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="#" style="color: #fff59d !important;">
                        <i class="fa fa-user-md"></i> <%=doctorName%> (<%= specialty != null ? specialty : "全科" %>)
                    </a></li>
                    <li><a href="<%=request.getContextPath()%>/UserManageService_Tool?action=doctorLogout"><i class="fa fa-sign-out"></i> 退出</a></li>
                </ul>
            </div>
        </div>
    </div>

    <!-- 内容区域 -->
    <div class="container">
        
        <!-- Tab 1: 待诊患者 -->
        <div id="content-waiting" class="tab-content-area <%= "waiting".equals(currentTab) ? "active" : "" %>">
            <div id="waitingContainer">
                <div class="loading-placeholder">
                    <i class="fa fa-spinner fa-spin fa-2x"></i>
                    <p>正在加载待诊患者...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 2: 就诊进程 -->
        <div id="content-process" class="tab-content-area <%= "process".equals(currentTab) ? "active" : "" %>">
            <div id="processContainer">
                <div class="loading-placeholder">
                    <i class="fa fa-spinner fa-spin fa-2x"></i>
                    <p>正在加载就诊进程...</p>
                </div>
            </div>
        </div>
        
        <!-- Tab 3: 病历管理 -->
        <div id="content-record" class="tab-content-area <%= "record".equals(currentTab) ? "active" : "" %>">
            <div id="recordContainer">
                <div class="loading-placeholder">
                    <i class="fa fa-spinner fa-spin fa-2x"></i>
                    <p>正在加载病历信息...</p>
                </div>
            </div>
        </div>
        
    </div>

    <!-- Footer -->
    <div class="footer">
        &copy; 2025 医疗云系统. 版权所有. | 医生工作站
    </div>

    <script>
        // ========== 全局变量 ==========
        let currentTab = '<%= currentTab %>';
        let dataLoaded = { waiting: false, process: false, record: false };
        
        // 就诊进程相关
        let currentProcessId = null;
        let currentPatientId = null;
        let currentPatientName = null;
        let currentStage = 1;
        
        // 病历相关
        let currentRecordProcessId = null;
        let isRecordEditMode = false;
        
        const stageNames = ["挂号", "问诊", "检查", "治疗", "取药", "完成"];
        const stageIcons = ["<i class='fa fa-clipboard'></i>", "<i class='fa fa-stethoscope'></i>", "<i class='fa fa-flask'></i>", "<i class='fa fa-medkit'></i>", "<i class='fa fa-plus-square'></i>", "<i class='fa fa-check-circle'></i>"];
        
        // ========== 页面初始化 ==========
        $(document).ready(function() {
            console.log('医生工作站加载完成，当前Tab:', currentTab);
            loadTabData(currentTab);
        });
        
        // ========== Tab 切换 ==========
        function switchTab(tab) {
            if (tab === currentTab) return;
            
            console.log('切换到:', tab);
            currentTab = tab;
            
            $('.navbar-nav li').removeClass('active');
            $('#tab-' + tab).addClass('active');
            
            $('.tab-content-area').removeClass('active');
            $('#content-' + tab).addClass('active');
            
            loadTabData(tab);
            
            history.pushState({tab: tab}, '', '?tab=' + tab);
        }
        
        // ========== 数据加载 ==========
        function loadTabData(tab) {
            switch(tab) {
                case 'waiting':
                    loadWaitingPage();
                    break;
                case 'process':
                    loadProcessPage();
                    break;
                case 'record':
                    loadRecordPage();
                    break;
            }
        }
        
        // ========== 待诊患者 ==========
        function loadWaitingPage() {
            console.log('加载待诊患者页面...');
            $.ajax({
                url: 'doctor_waiting.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    $('#waitingContainer').html(response);
                    loadWaitingPatients();
                    dataLoaded.waiting = true;
                },
                error: function() {
                    $('#waitingContainer').html('<div class="no-data"><i class="fa fa-exclamation-circle"></i><p>加载失败，请刷新重试</p></div>');
                }
            });
        }
        
        function loadWaitingPatients() {
            console.log('加载待诊患者数据...');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getAllPatientProcesses', format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        renderWaitingTable(res.data);
                        updateStats(res.data);
                    } else {
                        $('#waitingTableContainer').html('<div class="no-data"><i class="fa fa-inbox"></i><p>暂无待诊患者</p></div>');
                    }
                },
                error: function() {
                    $('#waitingTableContainer').html('<div class="no-data"><i class="fa fa-exclamation-circle"></i><p>加载失败，请刷新重试</p></div>');
                }
            });
        }
        
        function renderWaitingTable(patients) {
            if (!patients || patients.length === 0) {
                $('#waitingTableContainer').html('<div class="no-data"><i class="fa fa-inbox"></i><p>暂无待诊患者</p></div>');
                return;
            }
            
            let html = '<table class="patient-table">';
            html += '<thead><tr><th>患者信息</th><th>当前阶段</th><th>状态</th><th>挂号时间</th><th>操作</th></tr></thead>';
            html += '<tbody>';
            
            patients.forEach(function(p) {
                let stageName = p.currentNodeName || '挂号';
                let stageClass = getStageClass(stageName);
                let statusClass = getStatusClass(p.processStatus || '进行中');
                let patientInitial = (p.patientName || '患').substring(0, 1);
                
                html += '<tr>';
                html += '<td><div class="patient-name-cell">';
                html += '<div class="patient-avatar">' + patientInitial + '</div>';
                html += '<div class="patient-info-main">';
                html += '<span class="patient-name">' + (p.patientName || '未知') + '</span>';
                html += '<span class="patient-id">流程ID: ' + p.processId + '</span>';
                html += '</div></div></td>';
                html += '<td><span class="stage-badge ' + stageClass + '">' + stageName + '</span></td>';
                html += '<td><span class="status-badge ' + statusClass + '">' + (p.processStatus || '进行中') + '</span></td>';
                html += '<td>' + (p.createdAt || '-') + '</td>';
                html += '<td>';
                html += '<button class="btn-action btn-start" onclick="startProcess(\'' + p.processId + '\', \'' + (p.patientName || '') + '\', \'' + (p.patientId || '') + '\')"><i class="fa fa-play"></i> 就诊</button>';
                html += '<button class="btn-action btn-view" onclick="viewRecord(\'' + p.processId + '\', \'' + (p.patientName || '') + '\')"><i class="fa fa-file-text-o"></i> 病历</button>';
                html += '</td>';
                html += '</tr>';
            });
            
            html += '</tbody></table>';
            $('#waitingTableContainer').html(html);
        }
        
        function getStageClass(stageName) {
            let classes = {
                '挂号': 'stage-register',
                '问诊': 'stage-consult',
                '检查': 'stage-examine',
                '治疗': 'stage-treat',
                '取药': 'stage-medicine',
                '完成': 'stage-complete'
            };
            return classes[stageName] || 'stage-register';
        }
        
        function getStatusClass(status) {
            if (status.indexOf('已完成') >= 0) return 'status-completed';
            if (status.indexOf('进行中') >= 0) return 'status-inprogress';
            return 'status-waiting';
        }
        
        function updateStats(patients) {
            let waiting = 0, inprogress = 0, completed = 0;
            patients.forEach(function(p) {
                let status = p.processStatus || '';
                if (status.indexOf('已完成') >= 0) completed++;
                else if (status.indexOf('进行中') >= 0 || status.indexOf('已预约') >= 0) inprogress++;
                else waiting++;
            });
            $('#waitingCount').text(waiting);
            $('#inprogressCount').text(inprogress);
            $('#completedCount').text(completed);
        }
        
        function refreshWaitingList() {
            loadWaitingPatients();
            showToast('列表已刷新', 'success');
        }
        
        function filterPatients() {
            let stageFilter = $('#stageFilter').val();
            let statusFilter = $('#statusFilter').val();
            
            $('.patient-table tbody tr').each(function() {
                let stageBadge = $(this).find('.stage-badge').text().trim();
                let statusBadge = $(this).find('.status-badge').text().trim();
                
                let stageMatch = !stageFilter || stageBadge === stageFilter;
                let statusMatch = !statusFilter || statusBadge.indexOf(statusFilter) >= 0;
                
                if (stageMatch && statusMatch) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
            
            let visibleRows = $('.patient-table tbody tr:visible').length;
            if (visibleRows === 0 && $('.patient-table').length > 0) {
                if ($('#noFilterResultHint').length === 0) {
                    $('.patient-table').after('<div id="noFilterResultHint" class="no-data"><i class="fa fa-filter"></i><p>没有符合筛选条件的患者</p></div>');
                }
            } else {
                $('#noFilterResultHint').remove();
            }
        }
        
        // ========== 就诊进程 ==========
        // 地点列表缓存
        let locationList = [];
        // 药品列表缓存
        let medicineList = [];
        // 已选药品
        let selectedMedicines = [];
        // 支付管理器
        let paymentManagers = {
            3: { nodeId: 3, nodeName: '检查', items: [], total: 0, paid: false },
            4: { nodeId: 4, nodeName: '治疗', items: [], total: 0, paid: false },
            5: { nodeId: 5, nodeName: '取药', items: [], total: 0, paid: false }
        };
        
        function loadProcessPage() {
            console.log('加载就诊进程页面...');
            $.ajax({
                url: 'doctor_process_inner.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    $('#processContainer').html(response);
                    // 加载地点和药品数据
                    loadLocationsAndMedicines();
                    if (currentProcessId) {
                        loadProcessData(currentProcessId);
                    }
                    dataLoaded.process = true;
                },
                error: function() {
                    $('#processContainer').html('<div class="no-data"><i class="fa fa-exclamation-circle"></i><p>加载失败</p></div>');
                }
            });
        }
        
        function loadLocationsAndMedicines() {
            // 加载地点列表
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getAllLocations', format: 'json' },
                dataType: 'json',
                success: function(res) {
                    console.log('地点数据返回:', res);
                    if (res.success && res.data) {
                        locationList = res.data;
                        fillLocationDropdowns();
                    }
                },
                error: function(xhr, status, error) {
                    console.error('加载地点失败:', error);
                }
            });
            
            // 加载药品列表
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getAllMedicines', format: 'json' },
                dataType: 'json',
                success: function(res) {
                    console.log('药品数据返回:', res);
                    if (res.success && res.data) {
                        medicineList = res.data;
                    }
                },
                error: function(xhr, status, error) {
                    console.error('加载药品失败:', error);
                }
            });
        }
        
        function fillLocationDropdowns() {
            let selectors = ['#consultationLocation', '#examinationLocation', '#treatmentLocation', '#medicationLocation'];
            selectors.forEach(function(sel) {
                let $select = $(sel);
                if ($select.length) {
                    $select.html('<option value="">请选择地点</option>');
                    locationList.forEach(function(loc) {
                        $select.append('<option value="' + loc.locationId + '">' + (loc.locationName || loc.locationDescription || '地点' + loc.locationId) + '</option>');
                    });
                }
            });
        }
        
        function startProcess(processId, patientName, patientId) {
            currentProcessId = processId;
            currentPatientName = patientName;
            currentPatientId = patientId;
            
            switchTab('process');
        }
        
        function loadProcessData(processId) {
            console.log('加载就诊进程数据:', processId);
            
            // 显示进程内容，隐藏空状态
            $('#processEmptyState').hide();
            $('#processContent').show();
            
            // 更新患者信息
            $('#currentPatientName').text(currentPatientName || '患者');
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getLatestNode', processId: processId, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        currentStage = parseInt(res.data.nodeId) || 1;
                        $('#currentPatientMeta').text('流程ID: ' + processId + ' | 当前阶段: ' + stageNames[currentStage - 1]);
                        updateProgressNodes();
                        showStageForm(currentStage);
                        loadStageData(currentStage, res.data);
                    } else {
                        currentStage = 1;
                        $('#currentPatientMeta').text('流程ID: ' + processId + ' | 当前阶段: 挂号');
                        updateProgressNodes();
                        showStageForm(1);
                        loadStageData(1, {});
                    }
                },
                error: function() {
                    showToast('加载失败', 'error');
                }
            });
        }
        
        function updateProgressNodes() {
            $('.progress-node').each(function() {
                let stage = parseInt($(this).data('stage'));
                $(this).removeClass('completed active');
                if (stage < currentStage) $(this).addClass('completed');
                else if (stage === currentStage) $(this).addClass('active');
            });
            
            let progressWidth = ((currentStage - 1) / 5) * 100;
            if (progressWidth > 100) progressWidth = 100;
            $('#progressLineFill').css('width', 'calc(' + progressWidth + '% - 40px)');
        }
        
        function showStageForm(stage) {
            // 隐藏所有阶段表单
            $('.stage-form').hide();
            // 显示当前阶段表单
            $('#stageForm' + stage).show();
        }
        
        function loadNodeForm(stage) {
            currentStage = stage;
            updateProgressNodes();
            showStageForm(stage);
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getNodeInfo', processId: currentProcessId, nodeId: stage, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    loadStageData(stage, res.data || {});
                }
            });
        }
        
        function loadStageData(stage, data) {
            switch(stage) {
                case 1: // 挂号
                    if (data.reminder) {
                        $('#registerReminder').text(data.reminder);
                    }
                    loadPatientBasicInfo();
                    break;
                case 2: // 问诊
                    if (data.locationId) $('#consultationLocation').val(data.locationId);
                    if (data.diagnosisText) $('#consultationDiagnosis').val(data.diagnosisText);
                    if (data.reminder) $('#consultationReminder').val(data.reminder);
                    break;
                case 3: // 检查
                    if (data.locationId) $('#examinationLocation').val(data.locationId);
                    if (data.diagnosisText) $('#examinationAnalysis').val(data.diagnosisText);
                    // 加载图片预览
                    loadExistingImages(data.pictures || '');
                    if (data.reminder) $('#examinationReminder').val(data.reminder);
                    loadPaymentItems(3);
                    break;
                case 4: // 治疗
                    if (data.locationId) $('#treatmentLocation').val(data.locationId);
                    if (data.diagnosisText) $('#treatmentPlan').val(data.diagnosisText);
                    if (data.reminder) $('#treatmentReminder').val(data.reminder);
                    loadPaymentItems(4);
                    break;
                case 5: // 取药
                    if (data.locationId) $('#medicationLocation').val(data.locationId);
                    if (data.reminder) $('#medicationReminder').val(data.reminder);
                    loadPaymentItems(5);
                    break;
                case 6: // 完成
                    loadCompletionSummary();
                    break;
            }
        }
        
        function loadPatientBasicInfo() {
            if (!currentPatientId) return;
            $.ajax({
                url: 'UserManageService',
                type: 'GET',
                data: { action: 'getPatientInfo', patientId: currentPatientId, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data) {
                        let p = res.data;
                        let html = '';
                        html += '<div class="info-item"><span class="info-label">姓名：</span><span class="info-value">' + (p.patientName || '-') + '</span></div>';
                        html += '<div class="info-item"><span class="info-label">性别：</span><span class="info-value">' + (p.gender || '-') + '</span></div>';
                        html += '<div class="info-item"><span class="info-label">年龄：</span><span class="info-value">' + (p.age || '-') + '</span></div>';
                        html += '<div class="info-item"><span class="info-label">联系电话：</span><span class="info-value">' + (p.phone || '-') + '</span></div>';
                        $('#patientBasicInfo').html(html);
                    }
                }
            });
        }
        
        function loadCompletionSummary() {
            let html = '<h5>流程总结</h5>';
            html += '<div class="summary-item"><strong>患者：</strong>' + currentPatientName + '</div>';
            html += '<div class="summary-item"><strong>流程ID：</strong>' + currentProcessId + '</div>';
            html += '<div class="summary-item"><strong>状态：</strong>已完成全部治疗流程</div>';
            $('#completionSummary').html(html);
        }
        
        // ========== 保存函数 ==========
        function saveConsultation() {
            let locationId = $('#consultationLocation').val();
            let diagnosisText = $('#consultationDiagnosis').val();
            let reminder = $('#consultationReminder').val();
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateConsultation',
                    processId: currentProcessId,
                    locationId: locationId,
                    diagnosisText: diagnosisText,
                    reminder: reminder,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('问诊信息保存成功', 'success');
                    } else {
                        showToast(res.message || '保存失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('保存失败', 'error');
                }
            });
        }
        
        // ========== 图片上传处理 ==========
        let uploadedImages = []; // 存储已上传的图片Base64数据
        const MAX_IMAGE_WIDTH = 800;  // 最大宽度
        const MAX_IMAGE_HEIGHT = 800; // 最大高度
        const IMAGE_QUALITY = 0.7;    // 压缩质量 (0-1)
        
        // 压缩图片函数
        function compressImage(file, callback) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = new Image();
                img.onload = function() {
                    const canvas = document.createElement('canvas');
                    let width = img.width;
                    let height = img.height;
                    
                    // 计算缩放比例
                    if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
                        const ratio = Math.min(MAX_IMAGE_WIDTH / width, MAX_IMAGE_HEIGHT / height);
                        width = Math.round(width * ratio);
                        height = Math.round(height * ratio);
                    }
                    
                    canvas.width = width;
                    canvas.height = height;
                    
                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, width, height);
                    
                    // 转换为压缩后的Base64
                    const compressedData = canvas.toDataURL('image/jpeg', IMAGE_QUALITY);
                    callback(compressedData);
                };
                img.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
        
        function handleImageUpload(input) {
            const files = input.files;
            if (!files || files.length === 0) return;
            
            const maxSize = 10 * 1024 * 1024; // 10MB限制
            
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                
                // 检查文件类型
                if (!file.type.startsWith('image/')) {
                    showToast('请选择图片文件', 'error');
                    continue;
                }
                
                // 检查文件大小
                if (file.size > maxSize) {
                    showToast('图片 ' + file.name + ' 超过10MB限制', 'error');
                    continue;
                }
                
                // 压缩图片并添加
                (function(currentFile) {
                    compressImage(currentFile, function(compressedData) {
                        uploadedImages.push({
                            name: currentFile.name,
                            data: compressedData,
                            id: Date.now() + '_' + Math.random().toString(36).substr(2, 9)
                        });
                        updateImagePreview();
                        updateHiddenField();
                        showToast('图片 ' + currentFile.name + ' 已压缩上传', 'success');
                    });
                })(file);
            }
            
            input.value = '';
        }
        
        function updateImagePreview() {
            const container = $('#examinationImagePreview');
            container.empty();
            
            uploadedImages.forEach(function(img, index) {
                const previewItem = $('<div class="image-preview-item"></div>');
                previewItem.html(
                    '<img src="' + img.data + '" alt="' + img.name + '" onclick="openImageFullscreen(\'' + index + '\')">' +
                    '<button class="remove-image" onclick="removeImage(\'' + img.id + '\')" title="删除图片"><i class="fa fa-times"></i></button>'
                );
                container.append(previewItem);
            });
        }
        
        function removeImage(imageId) {
            uploadedImages = uploadedImages.filter(function(img) {
                return img.id !== imageId;
            });
            updateImagePreview();
            updateHiddenField();
        }
        
        function updateHiddenField() {
            // 将图片数据存储到隐藏字段（这里存储Base64数据，用分隔符分开）
            const dataArray = uploadedImages.map(function(img) {
                return img.data;
            });
            $('#examinationPictures').val(dataArray.join('|||'));
        }
        
        function openImageFullscreen(index) {
            const img = uploadedImages[index];
            if (img) {
                const win = window.open('', '_blank');
                win.document.write('<html><head><title>' + img.name + '</title><style>body{margin:0;display:flex;justify-content:center;align-items:center;min-height:100vh;background:#1a1a1a;}img{max-width:100%;max-height:100vh;object-fit:contain;}</style></head><body><img src="' + img.data + '" alt="' + img.name + '"></body></html>');
            }
        }
        
        function loadExistingImages(picturesData) {
            uploadedImages = [];
            if (picturesData && picturesData.trim() !== '') {
                const dataArray = picturesData.split('|||');
                dataArray.forEach(function(data, index) {
                    if (data.trim() !== '') {
                        uploadedImages.push({
                            name: '图片' + (index + 1),
                            data: data,
                            id: 'existing_' + index + '_' + Date.now()
                        });
                    }
                });
            }
            updateImagePreview();
            updateHiddenField(); 
        }
        
        function saveExamination() {
            let locationId = $('#examinationLocation').val();
            let diagnosisText = $('#examinationAnalysis').val();
            let pictures = $('#examinationPictures').val();
            let reminder = $('#examinationReminder').val();
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateExamination',
                    processId: currentProcessId,
                    locationId: locationId,
                    diagnosisText: diagnosisText,
                    pictures: pictures,
                    reminder: reminder,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('检查信息保存成功', 'success');
                    } else {
                        showToast(res.message || '保存失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('保存失败', 'error');
                }
            });
        }
        
        function saveTreatment() {
            let locationId = $('#treatmentLocation').val();
            let diagnosisText = $('#treatmentPlan').val();
            let reminder = $('#treatmentReminder').val();
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateTreatment',
                    processId: currentProcessId,
                    locationId: locationId,
                    diagnosisText: diagnosisText,
                    reminder: reminder,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('治疗信息保存成功', 'success');
                    } else {
                        showToast(res.message || '保存失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('保存失败', 'error');
                }
            });
        }
        
        function saveMedication() {
            let locationId = $('#medicationLocation').val();
            let reminder = $('#medicationReminder').val();
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateMedication',
                    processId: currentProcessId,
                    locationId: locationId,
                    reminder: reminder,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('取药信息保存成功', 'success');
                    } else {
                        showToast(res.message || '保存失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('保存失败', 'error');
                }
            });
        }
        
        function completeProcess() {
            if (!confirm('确定要完成该患者的就诊流程吗？')) return;
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'completeProcess',
                    processId: currentProcessId,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('就诊流程已完成！', 'success');
                        // 返回待诊列表
                        setTimeout(function() {
                            currentProcessId = null;
                            switchTab('waiting');
                        }, 1500);
                    } else {
                        showToast(res.message || '操作失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('操作失败', 'error');
                }
            });
        }
        
        // ========== 阶段导航 ==========
        function goToNextStage() {
            if (currentStage >= 6) return;
            
            // 检查支付状态（阶段3、4、5需要先完成支付）
            if (currentStage === 3 || currentStage === 4 || currentStage === 5) {
                checkPaymentBeforeNext();
                return;
            }
            
            doGoToNextStage();
        }
        
        function checkPaymentBeforeNext() {
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'checkNodePayment', processId: currentProcessId, nodeId: currentStage, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success) {
                        if (res.paymentCompleted) {
                            doGoToNextStage();
                        } else {
                            showToast('请先确保患者完成缴费后再进入下一阶段', 'error');
                        }
                    } else {
                        doGoToNextStage();
                    }
                },
                error: function() {
                    doGoToNextStage();
                }
            });
        }
        
        function doGoToNextStage() {
            $('#loadingOverlay').css('display', 'flex');
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'goToNextStage',
                    processId: currentProcessId,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        currentStage++;
                        updateProgressNodes();
                        showStageForm(currentStage);
                        loadStageData(currentStage, {});
                        showToast('已进入' + stageNames[currentStage - 1] + '阶段', 'success');
                    } else {
                        showToast(res.message || '流转失败', 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('流转失败', 'error');
                }
            });
        }
        
        function goToPrevStage() {
            if (currentStage <= 1) return;
            currentStage--;
            updateProgressNodes();
            showStageForm(currentStage);
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getNodeInfo', processId: currentProcessId, nodeId: currentStage, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    loadStageData(currentStage, res.data || {});
                }
            });
        }
        
        function refreshCurrentStage() {
            if (!currentProcessId) {
                showToast('请先选择一个患者', 'error');
                return;
            }
            
            showToast('正在刷新数据...', 'info');
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getNodeInfo', processId: currentProcessId, nodeId: currentStage, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    loadStageData(currentStage, res.data || {});
                    showToast('数据已刷新', 'success');
                },
                error: function() {
                    showToast('刷新失败', 'error');
                }
            });
        }
        
        // ========== 收费项目管理 ==========
        function loadPaymentItems(nodeId) {
            if (!currentProcessId) return;
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getNodePayments', processId: currentProcessId, nodeId: nodeId, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success) {
                        renderPaymentItems(nodeId, res.data || []);
                    }
                }
            });
        }
        
        function renderPaymentItems(nodeId, items) {
            let containerId = getPaymentContainerId(nodeId);
            let totalId = getTotalAmountId(nodeId);
            let statusId = getPaymentStatusId(nodeId);
            
            let $container = $('#' + containerId);
            let total = 0;
            let unpaidCount = 0;
            
            if (!items || items.length === 0) {
                $container.html('<div style="color:#999; text-align:center; padding:20px;">暂无收费项目</div>');
                $('#' + totalId).text('0.00');
                $('#' + statusId).text('无费用').removeClass('status-unpaid').addClass('status-paid');
                return;
            }
            
            let html = '';
            items.forEach(function(item) {
                let amount = parseFloat(item.amount) || 0;
                total += amount;
                let isPaid = item.orderStatus === '已支付';
                if (!isPaid) unpaidCount++;
                
                html += '<div class="payment-item">';
                html += '<div class="payment-item-info">';
                html += '<div class="payment-item-name">' + (item.orderContent || '未命名项目');
                html += '<span class="payment-item-status ' + (isPaid ? 'status-paid' : 'status-unpaid') + '">' + (item.orderStatus || '未支付') + '</span>';
                html += '</div>';
                html += '<div style="font-size:12px; color:#666;">创建时间：' + (item.createAt || '-') + '</div>';
                html += '</div>';
                html += '<div class="payment-item-amount">¥' + amount.toFixed(2) + '</div>';
                html += '<div class="payment-item-actions">';
                if (!isPaid) {
                    html += '<button class="btn-small btn-cancel" onclick="deletePaymentOrder(' + item.orderId + ', ' + nodeId + ')">删除</button>';
                } else {
                    html += '<span style="color:#27ae60; font-size:12px;"><i class="fa fa-check-circle"></i> 已支付</span>';
                }
                html += '</div>';
                html += '</div>';
            });
            
            $container.html(html);
            $('#' + totalId).text(total.toFixed(2));
            
            if (unpaidCount === 0) {
                $('#' + statusId).text('已缴费').removeClass('status-unpaid').addClass('status-paid');
            } else {
                $('#' + statusId).text('待缴费 (' + unpaidCount + '项)').removeClass('status-paid').addClass('status-unpaid');
            }
        }
        
        function getPaymentContainerId(nodeId) {
            let ids = { 3: 'examinationPaymentItems', 4: 'treatmentPaymentItems', 5: 'medicationPaymentItems' };
            return ids[nodeId] || 'paymentItems';
        }
        
        function getTotalAmountId(nodeId) {
            let ids = { 3: 'examinationTotalAmount', 4: 'treatmentTotalAmount', 5: 'medicationTotalAmount' };
            return ids[nodeId] || 'totalAmount';
        }
        
        function getPaymentStatusId(nodeId) {
            let ids = { 3: 'examinationPaymentStatus', 4: 'treatmentPaymentStatus', 5: 'medicationPaymentStatus' };
            return ids[nodeId] || 'paymentStatus';
        }
        
        function openPaymentModal(nodeId) {
            $('#currentPaymentNodeId').val(nodeId);
            $('#paymentItemName').val('');
            $('#paymentItemAmount').val('');
            $('#paymentItemQuantity').val('1');
            $('#paymentItemDesc').val('');
            $('#paymentModal').css('display', 'flex');
        }
        
        function closePaymentModal() {
            $('#paymentModal').hide();
        }
        
        function savePaymentItem() {
            let nodeId = $('#currentPaymentNodeId').val();
            let name = $('#paymentItemName').val();
            let amount = parseFloat($('#paymentItemAmount').val()) || 0;
            let quantity = parseInt($('#paymentItemQuantity').val()) || 1;
            let desc = $('#paymentItemDesc').val();
            
            if (!name) {
                showToast('请输入项目名称', 'error');
                return;
            }
            if (amount <= 0) {
                showToast('请输入有效金额', 'error');
                return;
            }
            
            let totalAmount = amount * quantity;
            let orderContent = name + (quantity > 1 ? ' x' + quantity : '') + (desc ? ' (' + desc + ')' : '');
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: {
                    action: 'addPaymentOrder',
                    processId: currentProcessId,
                    nodeId: nodeId,
                    orderContent: orderContent,
                    amount: Math.round(totalAmount * 100) / 100,
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('收费项目添加成功', 'success');
                        closePaymentModal();
                        loadPaymentItems(parseInt(nodeId));
                    } else {
                        showToast('添加失败: ' + (res.message || ''), 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('添加失败', 'error');
                }
            });
        }
        
        function deletePaymentOrder(orderId, nodeId) {
            if (!confirm('确认删除该收费项目？')) return;
            
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'deletePaymentOrder', orderId: orderId, format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success) {
                        showToast('删除成功', 'success');
                        loadPaymentItems(nodeId);
                    } else {
                        showToast('删除失败', 'error');
                    }
                },
                error: function() {
                    showToast('删除失败', 'error');
                }
            });
        }
        
        // ========== 药品选择 ==========
        function openMedicineModal() {
            selectedMedicines = [];
            renderMedicineList();
            $('#medicineModal').css('display', 'flex');
        }
        
        function closeMedicineModal() {
            $('#medicineModal').hide();
        }
        
        function renderMedicineList() {
            let $container = $('#medicineSelectionList');
            
            if (!medicineList || medicineList.length === 0) {
                $container.html('<div class="loading-placeholder"><i class="fa fa-inbox"></i><p>暂无药品数据</p></div>');
                return;
            }
            
            let html = '';
            medicineList.forEach(function(med) {
                let selected = selectedMedicines.find(m => m.id == med.medicineId);
                let quantity = selected ? selected.quantity : 0;
                let isSelected = quantity > 0;
                
                html += '<div class="medicine-selection-item ' + (isSelected ? 'selected' : '') + '" data-id="' + med.medicineId + '">';
                html += '<div style="display:flex; justify-content:space-between; align-items:center; width:100%;">';
                html += '<div style="flex-grow:1; min-width:0;">';
                html += '<strong style="font-size:14px;">' + (med.name || '未知药品') + '</strong>';
                html += '<div style="font-size:12px; color:#666; margin-top:5px;">规格：' + (med.dosage || '-') + '</div>';
                html += '<div style="font-size:12px; color:#28a745; margin-top:3px;">单价：¥' + (parseFloat(med.price) || 0).toFixed(2) + '</div>';
                html += '</div>';
                html += '<div style="display:flex; align-items:center; gap:8px; min-width:120px;">';
                html += '<button type="button" class="quantity-btn minus" onclick="changeMedicineQty(' + med.medicineId + ', -1)">-</button>';
                html += '<input type="number" class="medicine-quantity-input" data-id="' + med.medicineId + '" value="' + quantity + '" min="0" max="999" onchange="updateMedicineQty(' + med.medicineId + ', this.value)">';
                html += '<button type="button" class="quantity-btn plus" onclick="changeMedicineQty(' + med.medicineId + ', 1)">+</button>';
                html += '</div></div></div>';
            });
            
            $container.html(html);
            updateMedicineSummary();
        }
        
        function changeMedicineQty(medicineId, delta) {
            let $input = $('.medicine-quantity-input[data-id="' + medicineId + '"]');
            let current = parseInt($input.val()) || 0;
            let newVal = Math.max(0, Math.min(999, current + delta));
            $input.val(newVal);
            updateMedicineQty(medicineId, newVal);
        }
        
        function updateMedicineQty(medicineId, quantity) {
            quantity = parseInt(quantity) || 0;
            let med = medicineList.find(m => m.medicineId == medicineId);
            if (!med) return;
            
            let existingIndex = selectedMedicines.findIndex(m => m.id == medicineId);
            
            if (quantity === 0) {
                if (existingIndex !== -1) {
                    selectedMedicines.splice(existingIndex, 1);
                }
            } else {
                if (existingIndex !== -1) {
                    selectedMedicines[existingIndex].quantity = quantity;
                    selectedMedicines[existingIndex].totalPrice = (parseFloat(med.price) || 0) * quantity;
                } else {
                    selectedMedicines.push({
                        id: medicineId,
                        name: med.name || '',
                        price: parseFloat(med.price) || 0,
                        quantity: quantity,
                        totalPrice: (parseFloat(med.price) || 0) * quantity
                    });
                }
            }
            
            // 更新选中状态
            let $item = $('.medicine-selection-item[data-id="' + medicineId + '"]');
            if (quantity > 0) {
                $item.addClass('selected');
            } else {
                $item.removeClass('selected');
            }
            
            updateMedicineSummary();
        }
        
        function updateMedicineSummary() {
            let count = selectedMedicines.length;
            let totalQty = 0;
            let totalPrice = 0;
            
            selectedMedicines.forEach(function(m) {
                totalQty += m.quantity;
                totalPrice += m.totalPrice;
            });
            
            $('#selectedMedicineCount').text(count);
            $('#selectedMedicineQuantity').text(totalQty);
            $('#selectedMedicineTotal').text(totalPrice.toFixed(2));
        }
        
        function filterMedicines() {
            let keyword = $('#medicineSearchInput').val().toLowerCase();
            $('.medicine-selection-item').each(function() {
                let name = $(this).find('strong').text().toLowerCase();
                if (name.indexOf(keyword) >= 0) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        function saveMedicineSelection() {
            let validMedicines = selectedMedicines.filter(m => m.quantity > 0);
            
            if (validMedicines.length === 0) {
                showToast('请至少选择一个药品', 'error');
                return;
            }
            
            // 计算总金额
            let totalAmount = 0;
            let orderContent = '药品费用：' + validMedicines.map(function(m) {
                totalAmount += m.totalPrice;
                return m.name + ' ' + m.quantity + '盒×¥' + m.price.toFixed(2);
            }).join('、');
            
            $('#loadingOverlay').css('display', 'flex');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: {
                    action: 'addPaymentOrder',
                    processId: currentProcessId,
                    nodeId: 5,
                    orderContent: orderContent,
                    amount: Math.round(totalAmount),
                    format: 'json'
                },
                dataType: 'json',
                success: function(res) {
                    $('#loadingOverlay').hide();
                    if (res.success) {
                        showToast('药品已添加，总金额：¥' + totalAmount.toFixed(2), 'success');
                        closeMedicineModal();
                        loadPaymentItems(5);
                    } else {
                        showToast('添加失败: ' + (res.message || ''), 'error');
                    }
                },
                error: function() {
                    $('#loadingOverlay').hide();
                    showToast('添加失败', 'error');
                }
            });
        }
        
        function viewCurrentRecord() {
            if (currentProcessId) {
                viewRecord(currentProcessId, currentPatientName);
            }
        }
        
        // ========== 病历管理 ==========
        function loadRecordPage() {
            console.log('加载病历管理页面...');
            $.ajax({
                url: 'doctor_record.jsp',
                type: 'GET',
                dataType: 'html',
                success: function(response) {
                    $('#recordContainer').html(response);
                    loadRecordList();
                    dataLoaded.record = true;
                },
                error: function() {
                    $('#recordContainer').html('<div class="no-data"><i class="fa fa-exclamation-circle"></i><p>加载失败</p></div>');
                }
            });
        }
        
        function loadRecordList() {
            console.log('加载病历列表...');
            $.ajax({
                url: 'DiseaseManagement',
                type: 'GET',
                data: { action: 'getAllPatientProcesses', format: 'json' },
                dataType: 'json',
                success: function(res) {
                    if (res.success && res.data && res.data.length > 0) {
                        renderRecordList(res.data);
                    } else {
                        $('#recordListContainer').html('<div class="no-data" style="padding:40px;"><i class="fa fa-folder-open-o"></i><p>暂无病历记录</p></div>');
                    }
                },
                error: function() {
                    $('#recordListContainer').html('<div class="no-data"><p>加载失败</p></div>');
                }
            });
        }
        
        function renderRecordList(records) {
            let html = '';
            records.forEach(function(r) {
                let activeClass = currentRecordProcessId == r.processId ? 'active' : '';
                html += '<div class="record-list-item ' + activeClass + '" onclick="selectRecord(\'' + r.processId + '\', \'' + (r.patientName || '') + '\')">';
                html += '<div class="item-name">' + (r.patientName || '未知') + '</div>';
                html += '<div class="item-info">流程ID: ' + r.processId + ' | ' + (r.createdAt || '-') + '</div>';
                html += '</div>';
            });
            $('#recordListContainer').html(html);
        }
        
        function viewRecord(processId, patientName) {
            currentRecordProcessId = processId;
            switchTab('record');
            
            // 等待页面加载完成后选择病历
            setTimeout(function() {
                selectRecord(processId, patientName);
            }, 300);
        }
        
        function selectRecord(processId, patientName) {
            currentRecordProcessId = processId;
            currentPatientName = patientName;  // 更新全局变量

            // 更新列表选中状态
            $('.record-list-item').removeClass('active');
            $('.record-list-item').each(function() {
                if ($(this).find('.item-info').text().indexOf('流程ID: ' + processId) >= 0) {
                    $(this).addClass('active');
                }
            });
            
            // 显示病历详情
            $('#recordEmptyState').hide();
            $('#recordDetailContent').show();
            
            $('#recordPatientName').text(patientName || '患者');
            $('#recordProcessId').text('流程ID: ' + processId);
            
            loadRecordData(processId);
        }
        
        function loadRecordData(processId) {
            // 加载所有节点数据
            let allNodeData = {};
            let loadedCount = 0;
            
            for (let i = 1; i <= 6; i++) {
                (function(nodeId) {
                    $.ajax({
                        url: 'DiseaseManagement',
                        type: 'GET',
                        data: { action: 'getNodeInfo', processId: processId, nodeId: nodeId, format: 'json' },
                        dataType: 'json',
                        success: function(res) {
                            allNodeData[nodeId] = res.data || {};
                            loadedCount++;
                            if (loadedCount === 6) {
                                fillRecordForm(allNodeData);
                            }
                        },
                        error: function() {
                            loadedCount++;
                            if (loadedCount === 6) {
                                fillRecordForm(allNodeData);
                            }
                        }
                    });
                })(i);
            }
        }
        
        function fillRecordForm(allData) {
            // 填充病历表单
            $('#r_patientName').val(currentPatientName || '');
            $('#r_visitDate').val(allData[1]?.createdAt || new Date().toLocaleDateString());
            $('#r_doctorName').val('<%= doctorName %>');
            $('#r_status').val(allData[6]?.diagnosisText ? '已完成' : '进行中');
            
            // 问诊数据
            $('#r_chiefComplaint').val(allData[2]?.chiefComplaint || '');
            $('#r_diagnosis').val(allData[2]?.diagnosisText || '');
            
            // 检查数据
            $('#r_examination').val(allData[3]?.diagnosisText || '');
            // 加载检查图片预览
            loadRecordExistingImages(allData[3]?.pictures || '');
            
            // 治疗数据
            $('#r_treatment').val(allData[4]?.diagnosisText || '');
            
            // 用药数据
            $('#r_prescription').val(allData[5]?.diagnosisText || '');
            $('#r_medicationReminder').val(allData[5]?.reminder || '');
            
            // 医嘱
            $('#r_advice').val(allData[2]?.reminder || allData[4]?.reminder || '');
        }
        
        // ========== 病历页面图片处理 ==========
        let recordUploadedImages = []; // 存储病历页面已上传的图片
        
        function loadRecordExistingImages(picturesData) {
            recordUploadedImages = [];
            if (picturesData && picturesData.trim() !== '') {
                const dataArray = picturesData.split('|||');
                dataArray.forEach(function(data, index) {
                    if (data.trim() !== '') {
                        recordUploadedImages.push({
                            name: '图片' + (index + 1),
                            data: data,
                            id: 'record_existing_' + index + '_' + Date.now()
                        });
                    }
                });
            }
            updateRecordImagePreview();
            updateRecordHiddenField(); // 同步更新隐藏字段
        }
        
        function handleRecordImageUpload(input) {
            const files = input.files;
            if (!files || files.length === 0) return;
            
            const maxSize = 10 * 1024 * 1024; // 10MB限制（压缩前）
            
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                
                if (!file.type.startsWith('image/')) {
                    showToast('请选择图片文件', 'error');
                    continue;
                }
                
                if (file.size > maxSize) {
                    showToast('图片 ' + file.name + ' 超过10MB限制', 'error');
                    continue;
                }
                
                // 使用压缩函数
                (function(currentFile) {
                    compressImage(currentFile, function(compressedData) {
                        recordUploadedImages.push({
                            name: currentFile.name,
                            data: compressedData,
                            id: Date.now() + '_' + Math.random().toString(36).substr(2, 9)
                        });
                        updateRecordImagePreview();
                        updateRecordHiddenField();
                        showToast('图片 ' + currentFile.name + ' 已压缩上传', 'success');
                    });
                })(file);
            }
            
            input.value = '';
        }
        
        function updateRecordImagePreview() {
            const container = $('#recordImagePreview');
            container.empty();
            
            recordUploadedImages.forEach(function(img, index) {
                const previewItem = $('<div class="image-preview-item"></div>');
                let removeBtn = isRecordEditMode ? 
                    '<button class="remove-image" onclick="removeRecordImage(\'' + img.id + '\')" title="删除图片"><i class="fa fa-times"></i></button>' : '';
                previewItem.html(
                    '<img src="' + img.data + '" alt="' + img.name + '" onclick="openRecordImageFullscreen(' + index + ')">' + removeBtn
                );
                container.append(previewItem);
            });
        }
        
        function removeRecordImage(imageId) {
            recordUploadedImages = recordUploadedImages.filter(function(img) {
                return img.id !== imageId;
            });
            updateRecordImagePreview();
            updateRecordHiddenField();
        }
        
        function updateRecordHiddenField() {
            const dataArray = recordUploadedImages.map(function(img) {
                return img.data;
            });
            $('#r_pictures').val(dataArray.join('|||'));
        }
        
        function openRecordImageFullscreen(index) {
            const img = recordUploadedImages[index];
            if (img) {
                const win = window.open('', '_blank');
                win.document.write('<html><head><title>' + img.name + '</title><style>body{margin:0;display:flex;justify-content:center;align-items:center;min-height:100vh;background:#1a1a1a;}img{max-width:100%;max-height:100vh;object-fit:contain;}</style></head><body><img src="' + img.data + '" alt="' + img.name + '"></body></html>');
            }
        }
        
        function toggleEditMode() {
            isRecordEditMode = !isRecordEditMode;
            
            if (isRecordEditMode) {
                // 进入编辑模式
                $('#btnEditRecord').html('<i class="fa fa-eye"></i> 查看');
                $('.record-textarea, #r_medicationReminder').removeAttr('readonly');
                $('#recordFormActions').show();
                $('#recordImageUploadArea').show(); // 显示上传按钮
            } else {
                // 退出编辑模式
                $('#btnEditRecord').html('<i class="fa fa-edit"></i> 编辑');
                $('.record-textarea, #r_medicationReminder').attr('readonly', true);
                $('#recordFormActions').hide();
                $('#recordImageUploadArea').hide(); // 隐藏上传按钮
            }
            updateRecordImagePreview(); // 更新预览（显示/隐藏删除按钮）
        }
        
        function cancelEdit() {
            isRecordEditMode = false;
            $('#btnEditRecord').html('<i class="fa fa-edit"></i> 编辑');
            $('.record-textarea, #r_medicationReminder').attr('readonly', true);
            $('#recordFormActions').hide();
            $('#recordImageUploadArea').hide();
            loadRecordData(currentRecordProcessId);
        }
        
        function saveRecord() {
            // 保存病历数据
            $('#loadingOverlay').css('display', 'flex');
            
            // 保存各个阶段的数据
            let savePromises = [];
            
            // 问诊数据
            savePromises.push($.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateConsultation',
                    processId: currentRecordProcessId,
                    diagnosisText: $('#r_diagnosis').val(),
                    reminder: $('#r_advice').val()
                }
            }));
            
            // 检查数据
            savePromises.push($.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateExamination',
                    processId: currentRecordProcessId,
                    diagnosisText: $('#r_examination').val(),
                    pictures: $('#r_pictures').val()
                }
            }));
            
            // 治疗数据
            savePromises.push($.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateTreatment',
                    processId: currentRecordProcessId,
                    diagnosisText: $('#r_treatment').val()
                }
            }));
            
            // 用药数据
            savePromises.push($.ajax({
                url: 'DiseaseManagement',
                type: 'POST',
                data: {
                    action: 'updateMedication',
                    processId: currentRecordProcessId,
                    diagnosisText: $('#r_prescription').val(),
                    reminder: $('#r_medicationReminder').val()
                }
            }));
            
            $.when.apply($, savePromises).then(function() {
                $('#loadingOverlay').hide();
                showToast('病历保存成功！', 'success');
                cancelEdit();
            }).fail(function() {
                $('#loadingOverlay').hide();
                showToast('部分数据保存失败', 'error');
            });
        }
        
        function printRecord() {
            window.print();
        }
        
        function searchRecords() {
            let keyword = $('#recordSearchInput').val().trim().toLowerCase();
            if (!keyword) {
                loadRecordList();
                return;
            }
            
            $('.record-list-item').each(function() {
                let text = $(this).text().toLowerCase();
                if (text.indexOf(keyword) >= 0) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
        
        // ========== 工具函数 ==========
        function showToast(msg, type) {
            let bgColor = '#059669';
            let icon = '✓';
            
            if (type === 'error') {
                bgColor = '#dc3545';
                icon = '✗';
            } else if (type === 'info') {
                bgColor = '#17a2b8';
                icon = 'ℹ';
            }
            
            let toast = $('<div style="position:fixed;top:20px;left:50%;transform:translateX(-50%);background:' + bgColor + ';color:#fff;padding:12px 24px;border-radius:8px;font-size:15px;z-index:9999;box-shadow:0 4px 12px rgba(0,0,0,0.15);">' + icon + ' ' + msg + '</div>');
            $('body').append(toast);
            setTimeout(function() { toast.fadeOut(function() { toast.remove(); }); }, 3000);
        }
        
        window.onpopstate = function(event) {
            if (event.state && event.state.tab) {
                switchTab(event.state.tab);
            }
        };
        
        // 绑定回车搜索
        $(document).on('keypress', '#recordSearchInput', function(e) {
            if (e.which === 13) searchRecords();
        });
    </script>
</body>
</html>
