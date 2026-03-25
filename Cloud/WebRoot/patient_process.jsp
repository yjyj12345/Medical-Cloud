<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 无进程提示（默认隐藏） -->
<div id="noProcessHintContainer" style="display: none;">
    <div class="no-process-hint">
        <div class="no-process-icon"><i class="fa fa-calendar-check-o"></i></div>
        <h3>暂无进行中的就医进程</h3>
        <p>您当前没有进行中的就医进程。如需就医，请先进行预约挂号。</p>
        <button class="btn-go-registration" onclick="switchTab('registration')">
            <i class="fa fa-plus-circle"></i> 去预约挂号
        </button>
    </div>
</div>

<!-- 就医进程内容（有进程时显示） -->
<div id="processContentContainer" class="process-section" style="display: none;">
    <h2 class="process-title" id="patientName">加载患者就医进程...</h2>
    <div class="text-right" style="margin-bottom: 10px;">
        <small class="text-muted" id="processInfo">进程ID: 正在获取...</small>
    </div>
    
    <div class="progress-container">
        <div class="progress-line"></div>
        <div class="progress-line-active" id="progressBar" style="width: 8.33%;"></div>
        <ul class="progress-steps" id="progressSteps"></ul>
    </div>
    
    <div class="step-details" id="currentStepDetails">
        <div class="text-center" style="padding: 50px 0;">
            <i class="fa fa-spinner fa-spin fa-2x" style="color: #e85a4f;"></i>
            <p>正在加载步骤数据...</p>
        </div>
    </div>
    
    <div class="text-center" style="margin-top: 30px;">
        <button type="button" class="btn btn-info btn-action" onclick="refreshProcess()" style="background-color: #e85a4f; border-color: #e85a4f;">
            <i class="fa fa-refresh"></i> 刷新进程
        </button>
    </div>
    
    <input type="hidden" id="processId" value="">
    <input type="hidden" id="patientId" value="">
</div>

<!-- 加载中提示 -->
<div id="processLoadingContainer" class="text-center" style="padding: 80px 0;">
    <i class="fa fa-spinner fa-spin fa-3x" style="color: #e85a4f;"></i>
    <p style="margin-top: 20px; color: #666;">正在加载就医进程...</p>
</div>
