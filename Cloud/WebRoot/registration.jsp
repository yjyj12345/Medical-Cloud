<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>

<!-- 预约挂号提示 -->
<div class="reg-alert reg-alert-success" id="regSuccessAlert" style="display:none;"></div>
<div class="reg-alert reg-alert-error" id="regErrorAlert" style="display:none;"></div>

<div class="reg-wrapper">
    <!-- 左侧：预约表单 -->
    <div class="reg-form-panel">
        <form id="appointmentForm">
            <!-- 步骤指示器 -->
            <div class="reg-steps">
                <div class="reg-step active" data-step="1">
                    <span class="step-num">1</span>
                    <span class="step-text">选择科室</span>
                </div>
                <div class="reg-step-line"></div>
                <div class="reg-step" data-step="2">
                    <span class="step-num">2</span>
                    <span class="step-text">选择医生</span>
                </div>
                <div class="reg-step-line"></div>
                <div class="reg-step" data-step="3">
                    <span class="step-num">3</span>
                    <span class="step-text">选择时间</span>
                </div>
            </div>
            
            <!-- 选择科室 -->
            <div class="reg-field">
                <label><i class="fa fa-stethoscope"></i> 选择科室</label>
                <select class="reg-select" id="specialtySelect" onchange="onSpecialtyChange()">
                    <option value="">-- 请选择科室 --</option>
                </select>
            </div>
            
            <!-- 选择医生 -->
            <div class="reg-field">
                <label><i class="fa fa-user-md"></i> 选择医生</label>
                <div class="reg-doctor-list" id="doctorList">
                    <div class="reg-empty-hint">请先选择科室</div>
                </div>
            </div>
            
            <!-- 选择日期时间 -->
            <div class="reg-field-row">
                <div class="reg-field half">
                    <label><i class="fa fa-calendar"></i> 预约日期</label>
                    <input type="date" class="reg-input" id="appointmentDate" min="">
                </div>
                <div class="reg-field half">
                    <label><i class="fa fa-clock-o"></i> 时段</label>
                    <select class="reg-select" id="timeSlot">
                        <option value="上午">上午 (8:00-12:00)</option>
                        <option value="下午">下午 (14:00-17:30)</option>
                    </select>
                </div>
            </div>

            <input type="hidden" id="selectedDoctorId" value="">
            <input type="hidden" id="selectedOrgId" value="">
            
            <!-- 提交按钮 -->
            <div class="reg-submit-area">
                <button type="submit" class="reg-submit-btn" id="submitBtn" disabled>
                    <i class="fa fa-check-circle"></i> 确认预约
                </button>
            </div>
        </form>
    </div>
    
    <!-- 右侧：我的预约 -->
    <div class="reg-list-panel">
        <div class="reg-list-header">
            <h4><i class="fa fa-list-alt"></i> 我的预约</h4>
            <button type="button" class="reg-refresh-btn" onclick="loadAppointments()">
                <i class="fa fa-refresh"></i>
            </button>
        </div>
        
        <div class="reg-list-body" id="appointmentList">
            <div class="reg-loading">
                <i class="fa fa-spinner fa-spin"></i> 加载中...
            </div>
        </div>
    </div>
</div>

