<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 消息提示 -->
<div class="doctor-alert doctor-alert-success" id="recordSuccessAlert" style="display:none;"></div>
<div class="doctor-alert doctor-alert-error" id="recordErrorAlert" style="display:none;"></div>

<!-- 病历管理页面 -->
<div class="record-wrapper">
    <!-- 左侧：病历列表 -->
    <div class="record-list-panel">
        <div class="record-list-header">
            <h4><i class="fa fa-folder-open"></i> 病历记录</h4>
            <div class="search-box-mini">
                <input type="text" class="search-input-mini" id="recordSearchInput" placeholder="搜索患者...">
                <button class="btn-search-mini" onclick="searchRecords()"><i class="fa fa-search"></i></button>
            </div>
        </div>
        <div class="record-list-body" id="recordListContainer">
            <div class="loading-placeholder">
                <i class="fa fa-spinner fa-spin"></i>
                <p>正在加载...</p>
            </div>
        </div>
    </div>
    
    <!-- 右侧：病历详情/编辑 -->
    <div class="record-detail-panel">
        <!-- 空状态 -->
        <div class="record-empty-state" id="recordEmptyState">
            <div class="empty-icon">
                <i class="fa fa-file-text-o"></i>
            </div>
            <h3>选择一条病历查看详情</h3>
            <p>从左侧列表中选择患者的病历进行查看或编辑</p>
        </div>
        
        <!-- 病历详情 -->
        <div class="record-detail-content" id="recordDetailContent" style="display:none;">
            <div class="record-detail-header">
                <div class="record-patient-info">
                    <span class="record-patient-name" id="recordPatientName">患者姓名</span>
                    <span class="record-id" id="recordProcessId">流程ID: --</span>
                </div>
                <div class="record-detail-actions">
                    <button class="btn-edit" id="btnEditRecord" onclick="toggleEditMode()">
                        <i class="fa fa-edit"></i> 编辑
                    </button>
                    <button class="btn-print" onclick="printRecord()">
                        <i class="fa fa-print"></i> 打印
                    </button>
                </div>
            </div>
            
            <!-- 病历表单 -->
            <form id="recordForm">
                <!-- 基本信息 -->
                <div class="record-section">
                    <h5><i class="fa fa-user"></i> 基本信息</h5>
                    <div class="record-grid">
                        <div class="record-item">
                            <label>患者姓名</label>
                            <input type="text" class="record-input" id="r_patientName" readonly>
                        </div>
                        <div class="record-item">
                            <label>就诊日期</label>
                            <input type="text" class="record-input" id="r_visitDate" readonly>
                        </div>
                        <div class="record-item">
                            <label>接诊医生</label>
                            <input type="text" class="record-input" id="r_doctorName" readonly>
                        </div>
                        <div class="record-item">
                            <label>当前状态</label>
                            <input type="text" class="record-input" id="r_status" readonly>
                        </div>
                    </div>
                </div>
                
                <!-- 主诉与诊断 -->
                <div class="record-section">
                    <h5><i class="fa fa-stethoscope"></i> 主诉与诊断</h5>
                    <div class="record-full-width">
                        <label>主诉症状</label>
                        <textarea class="record-textarea" id="r_chiefComplaint" rows="3" placeholder="患者主诉症状描述..."></textarea>
                    </div>
                    <div class="record-full-width">
                        <label>诊断结果</label>
                        <textarea class="record-textarea" id="r_diagnosis" rows="3" placeholder="医生诊断结果..."></textarea>
                    </div>
                </div>
                
                <!-- 检查结果 -->
                <div class="record-section">
                    <h5><i class="fa fa-flask"></i> 检查结果</h5>
                    <div class="record-full-width">
                        <label>检查项目及结果</label>
                        <textarea class="record-textarea" id="r_examination" rows="3" placeholder="检查项目及结果..."></textarea>
                    </div>
                    <div class="record-full-width">
                        <label>检查图片</label>
                        <input type="hidden" id="r_pictures" value="">
                        <div class="record-image-upload-area" id="recordImageUploadArea" style="display:none;">
                            <input type="file" id="recordPictureUpload" accept="image/*" multiple style="display:none;" onchange="handleRecordImageUpload(this)">
                            <button type="button" class="btn-upload-image" onclick="document.getElementById('recordPictureUpload').click()">
                                <i class="fa fa-cloud-upload"></i> 选择图片上传
                            </button>
                            <span class="upload-hint">支持 JPG, PNG, GIF 格式，可多选</span>
                        </div>
                        <div class="image-preview-container" id="recordImagePreview">
                        </div>
                    </div>
                </div>
                
                <!-- 治疗方案 -->
                <div class="record-section">
                    <h5><i class="fa fa-medkit"></i> 治疗方案</h5>
                    <div class="record-full-width">
                        <label>治疗方案</label>
                        <textarea class="record-textarea" id="r_treatment" rows="3" placeholder="治疗方案描述..."></textarea>
                    </div>
                </div>
                
                <!-- 用药处方 -->
                <div class="record-section">
                    <h5><i class="fa fa-pills"></i> 用药处方</h5>
                    <div class="record-full-width">
                        <label>处方药品</label>
                        <textarea class="record-textarea" id="r_prescription" rows="3" placeholder="处方药品信息..."></textarea>
                    </div>
                    <div class="record-full-width">
                        <label>用药提醒</label>
                        <input type="text" class="record-input" id="r_medicationReminder" placeholder="用药注意事项...">
                    </div>
                </div>
                
                <!-- 医嘱 -->
                <div class="record-section">
                    <h5><i class="fa fa-bell"></i> 医嘱</h5>
                    <div class="record-full-width">
                        <label>医嘱内容</label>
                        <textarea class="record-textarea" id="r_advice" rows="3" placeholder="医嘱及注意事项..."></textarea>
                    </div>
                </div>
                
                <!-- 操作按钮 -->
                <div class="record-form-actions" id="recordFormActions" style="display:none;">
                    <button type="button" class="btn-save-record" onclick="saveRecord()">
                        <i class="fa fa-save"></i> 保存病历
                    </button>
                    <button type="button" class="btn-cancel-edit" onclick="cancelEdit()">
                        <i class="fa fa-times"></i> 取消
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

