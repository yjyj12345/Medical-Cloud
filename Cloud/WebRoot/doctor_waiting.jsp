<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 待诊患者列表模板 -->
<div class="waiting-card">
    <div class="waiting-header">
        <div class="waiting-header-left">
            <h3><i class="fa fa-clock-o"></i> 今日待诊</h3>
            <p class="waiting-subtitle">实时更新患者就诊状态</p>
        </div>
        <div class="waiting-stats">
            <div class="waiting-stat">
                <span class="stat-num" id="waitingCount">0</span>
                <span class="stat-label">等待中</span>
            </div>
            <div class="waiting-stat">
                <span class="stat-num" id="inprogressCount">0</span>
                <span class="stat-label">就诊中</span>
            </div>
            <div class="waiting-stat">
                <span class="stat-num" id="completedCount">0</span>
                <span class="stat-label">已完成</span>
            </div>
        </div>
    </div>
    
    <!-- 筛选工具栏 -->
    <div class="waiting-toolbar">
        <div class="toolbar-left">
            <select class="filter-select" id="stageFilter" onchange="filterPatients()">
                <option value="">全部阶段</option>
                <option value="挂号">挂号</option>
                <option value="问诊">问诊</option>
                <option value="检查">检查</option>
                <option value="治疗">治疗</option>
                <option value="取药">取药</option>
                <option value="完成">完成</option>
            </select>
            <select class="filter-select" id="statusFilter" onchange="filterPatients()">
                <option value="">全部状态</option>
                <option value="等待中">等待中</option>
                <option value="进行中">进行中</option>
                <option value="已完成">已完成</option>
            </select>
        </div>
        <div class="toolbar-right">
            <button class="btn-refresh" onclick="refreshWaitingList()">
                <i class="fa fa-refresh"></i> 刷新列表
            </button>
        </div>
    </div>
    
    <!-- 患者列表表格 -->
    <div id="waitingTableContainer">
        <div class="loading-placeholder">
            <i class="fa fa-spinner fa-spin fa-2x"></i>
            <p>正在加载患者列表...</p>
        </div>
    </div>
</div>

