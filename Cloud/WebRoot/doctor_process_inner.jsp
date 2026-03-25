<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="process-empty-state" id="processEmptyState">
    <div class="empty-icon">
        <i class="fa fa-user-plus"></i>
    </div>
    <h3>请选择一位患者</h3>
    <p>从待诊患者列表中选择一位患者开始就诊</p>
    <button class="btn-back-to-list" onclick="switchTab('waiting')">
        <i class="fa fa-arrow-left"></i> 返回待诊列表
    </button>
</div>

<!-- 就诊进程内容（选择患者后显示） -->
<div class="process-card" id="processContent" style="display:none;">
    <!-- 患者信息头部 -->
    <div class="process-header">
        <div class="current-patient-info">
            <div class="current-patient-avatar">
                <i class="fa fa-user"></i>
            </div>
            <div class="current-patient-details">
                <h3 id="currentPatientName">患者姓名</h3>
                <p id="currentPatientMeta">流程ID: -- | 当前阶段: --</p>
            </div>
        </div>
        <div class="process-actions">
            <button class="btn-action btn-refresh" onclick="refreshCurrentStage()" title="刷新当前阶段数据">
                <i class="fa fa-refresh"></i> 刷新
            </button>
            <button class="btn-action btn-view" onclick="viewCurrentRecord()">
                <i class="fa fa-file-text-o"></i> 查看病历
            </button>
            <button class="btn-action btn-back" onclick="switchTab('waiting')">
                <i class="fa fa-arrow-left"></i> 返回列表
            </button>
        </div>
    </div>
    
    <!-- 进度时间线 -->
    <div class="progress-timeline">
        <div class="progress-line-fill" id="progressLineFill" style="width:0%"></div>
        <div class="progress-node" data-stage="1" onclick="loadNodeForm(1)">
            <div class="node-circle"><i class="fa fa-clipboard"></i></div>
            <div class="node-label">挂号</div>
        </div>
        <div class="progress-node" data-stage="2" onclick="loadNodeForm(2)">
            <div class="node-circle"><i class="fa fa-stethoscope"></i></div>
            <div class="node-label">问诊</div>
        </div>
        <div class="progress-node" data-stage="3" onclick="loadNodeForm(3)">
            <div class="node-circle"><i class="fa fa-flask"></i></div>
            <div class="node-label">检查</div>
        </div>
        <div class="progress-node" data-stage="4" onclick="loadNodeForm(4)">
            <div class="node-circle"><i class="fa fa-medkit"></i></div>
            <div class="node-label">治疗</div>
        </div>
        <div class="progress-node" data-stage="5" onclick="loadNodeForm(5)">
            <div class="node-circle"><i class="fa fa-plus-square"></i></div>
            <div class="node-label">取药</div>
        </div>
        <div class="progress-node" data-stage="6" onclick="loadNodeForm(6)">
            <div class="node-circle"><i class="fa fa-check-circle"></i></div>
            <div class="node-label">完成</div>
        </div>
    </div>
    
    <!-- 阶段表单区域 -->
    <div class="stage-forms">
        
        <!-- 阶段1: 挂号 -->
        <div class="stage-form" id="stageForm1" style="display:none;">
            <div class="form-section">
                <h4><i class="fa fa-clipboard"></i> 挂号信息</h4>
                
                <div class="form-group">
                    <label>患者状态</label>
                    <div id="registerStatus">
                        <span class="status-badge status-inprogress">已挂号</span>
                    </div>
                </div>
                
                <div class="form-group">
                    <label>患者基本信息</label>
                    <div class="patient-basic-info" id="patientBasicInfo">
                        <!-- 动态填充 -->
                    </div>
                </div>
                
                <div class="form-group">
                    <label>挂号备注</label>
                    <div class="reminder-display" id="registerReminder">暂无备注</div>
                </div>
                
                <div class="btn-group-actions">
                    <button class="btn-next-stage" onclick="goToNextStage()">
                        <i class="fa fa-arrow-right"></i> 下一阶段
                    </button>
                </div>
            </div>
        </div>
        
        <!-- 阶段2: 问诊 -->
        <div class="stage-form" id="stageForm2" style="display:none;">
            <div class="form-section">
                <h4><i class="fa fa-stethoscope"></i> 问诊信息</h4>
                
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fa fa-map-marker"></i> 就诊地点</label>
                        <select class="form-control" id="consultationLocation">
                            <option value="">请选择就诊地点</option>
                        </select>
                        <div class="location-info" id="consultationLocationInfo" style="display:none;"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-file-text-o"></i> 诊断结果</label>
                    <textarea class="form-control" id="consultationDiagnosis" placeholder="请输入诊断结果..."></textarea>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-bell"></i> 医嘱备注</label>
                    <textarea class="form-control" id="consultationReminder" placeholder="请输入医嘱备注..."></textarea>
                </div>
                
                <div class="btn-group-actions">
                    <button class="btn-prev-stage" onclick="goToPrevStage()">
                        <i class="fa fa-arrow-left"></i> 上一阶段
                    </button>
                    <button class="btn-save" onclick="saveConsultation()">
                        <i class="fa fa-save"></i> 保存
                    </button>
                    <button class="btn-next-stage" onclick="goToNextStage()">
                        <i class="fa fa-arrow-right"></i> 下一阶段
                    </button>
                </div>
            </div>
        </div>
        
        <!-- 阶段3: 检查 -->
        <div class="stage-form" id="stageForm3" style="display:none;">
            <div class="form-section">
                <h4><i class="fa fa-flask"></i> 检查信息</h4>
                
                <!-- 支付状态 -->
                <div class="payment-status-bar">
                    <span>支付状态：</span>
                    <span class="status-badge" id="examinationPaymentStatus">待缴费</span>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fa fa-map-marker"></i> 检查地点</label>
                        <select class="form-control" id="examinationLocation">
                            <option value="">请选择检查地点</option>
                        </select>
                        <div class="location-info" id="examinationLocationInfo" style="display:none;"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-money"></i> 检查项目与费用</label>
                    <button type="button" class="btn-add-payment" onclick="openPaymentModal(3)">
                        <i class="fa fa-plus"></i> 添加检查项目
                    </button>
                    <div class="payment-items-list" id="examinationPaymentItems">
                        <!-- 动态加载 -->
                    </div>
                    <div class="payment-total">
                        总计：¥<span id="examinationTotalAmount">0.00</span>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-file-text-o"></i> 检查情况分析</label>
                    <textarea class="form-control" id="examinationAnalysis" placeholder="请输入检查情况分析..."></textarea>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-image"></i> 检查图片</label>
                    <input type="hidden" id="examinationPictures" value="">
                    <div class="image-upload-area">
                        <input type="file" id="examinationPictureUpload" accept="image/*" multiple style="display:none;" onchange="handleImageUpload(this)">
                        <button type="button" class="btn-upload-image" onclick="document.getElementById('examinationPictureUpload').click()">
                            <i class="fa fa-cloud-upload"></i> 选择图片上传
                        </button>
                        <span class="upload-hint">支持 JPG, PNG, GIF 格式，可多选</span>
                    </div>
                    <div class="image-preview-container" id="examinationImagePreview">
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-bell"></i> 检查备注</label>
                    <textarea class="form-control" id="examinationReminder" placeholder="请输入检查备注..."></textarea>
                </div>
                
                <div class="btn-group-actions">
                    <button class="btn-prev-stage" onclick="goToPrevStage()">
                        <i class="fa fa-arrow-left"></i> 上一阶段
                    </button>
                    <button class="btn-save" onclick="saveExamination()">
                        <i class="fa fa-save"></i> 保存
                    </button>
                    <button class="btn-next-stage" id="btnNextToTreatment" onclick="goToNextStage()">
                        <i class="fa fa-arrow-right"></i> 下一阶段
                    </button>
                </div>
            </div>
        </div>
        
        <!-- 阶段4: 治疗 -->
        <div class="stage-form" id="stageForm4" style="display:none;">
            <div class="form-section">
                <h4><i class="fa fa-medkit"></i> 治疗信息</h4>
                
                <!-- 支付状态 -->
                <div class="payment-status-bar">
                    <span>支付状态：</span>
                    <span class="status-badge" id="treatmentPaymentStatus">待缴费</span>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fa fa-map-marker"></i> 治疗地点</label>
                        <select class="form-control" id="treatmentLocation">
                            <option value="">请选择治疗地点</option>
                        </select>
                        <div class="location-info" id="treatmentLocationInfo" style="display:none;"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-file-text-o"></i> 治疗方案</label>
                    <textarea class="form-control" id="treatmentPlan" placeholder="请输入治疗方案..."></textarea>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-money"></i> 治疗项目与费用</label>
                    <button type="button" class="btn-add-payment" onclick="openPaymentModal(4)">
                        <i class="fa fa-plus"></i> 添加治疗项目
                    </button>
                    <div class="payment-items-list" id="treatmentPaymentItems">
                        <!-- 动态加载 -->
                    </div>
                    <div class="payment-total">
                        总计：¥<span id="treatmentTotalAmount">0.00</span>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-bell"></i> 治疗备注</label>
                    <textarea class="form-control" id="treatmentReminder" placeholder="请输入治疗备注..."></textarea>
                </div>
                
                <div class="btn-group-actions">
                    <button class="btn-prev-stage" onclick="goToPrevStage()">
                        <i class="fa fa-arrow-left"></i> 上一阶段
                    </button>
                    <button class="btn-save" onclick="saveTreatment()">
                        <i class="fa fa-save"></i> 保存
                    </button>
                    <button class="btn-next-stage" id="btnNextToMedication" onclick="goToNextStage()">
                        <i class="fa fa-arrow-right"></i> 下一阶段
                    </button>
                </div>
            </div>
        </div>
        
        <!-- 阶段5: 取药 -->
        <div class="stage-form" id="stageForm5" style="display:none;">
            <div class="form-section">
                <h4><i class="fa fa-pills"></i> 取药信息</h4>
                
                <!-- 支付状态 -->
                <div class="payment-status-bar">
                    <span>支付状态：</span>
                    <span class="status-badge" id="medicationPaymentStatus">待缴费</span>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fa fa-map-marker"></i> 取药地点</label>
                        <select class="form-control" id="medicationLocation">
                            <option value="">请选择取药地点</option>
                        </select>
                        <div class="location-info" id="medicationLocationInfo" style="display:none;"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-list"></i> 药品清单</label>
                    <button type="button" class="btn-add-payment" onclick="openMedicineModal()">
                        <i class="fa fa-plus"></i> 添加药品
                    </button>
                    <div class="payment-items-list" id="medicationPaymentItems">
                        <!-- 动态加载药品费用 -->
                    </div>
                    <div class="payment-total">
                        药品费用总计：¥<span id="medicationTotalAmount">0.00</span>
                    </div>
                </div>
                
                <div class="form-group">
                    <label><i class="fa fa-bell"></i> 用药提醒</label>
                    <textarea class="form-control" id="medicationReminder" placeholder="请输入用药注意事项..."></textarea>
                </div>
                
                <div class="btn-group-actions">
                    <button class="btn-prev-stage" onclick="goToPrevStage()">
                        <i class="fa fa-arrow-left"></i> 上一阶段
                    </button>
                    <button class="btn-save" onclick="saveMedication()">
                        <i class="fa fa-save"></i> 保存
                    </button>
                    <button class="btn-next-stage" id="btnNextToComplete" onclick="goToNextStage()">
                        <i class="fa fa-arrow-right"></i> 下一阶段
                    </button>
                </div>
            </div>
        </div>
        
        <!-- 阶段6: 完成 -->
        <div class="stage-form" id="stageForm6" style="display:none;">
            <div class="form-section completion-section">
                <div class="completion-icon">
                    <i class="fa fa-check-circle"></i>
                </div>
                <h3>就诊流程已完成！</h3>
                <p>患者已完成全部治疗流程，祝患者早日康复！</p>
                
                <div class="completion-summary" id="completionSummary">
                    <!-- 动态填充流程总结 -->
                </div>
                
                <div class="btn-group-actions" style="justify-content: center;">
                    <button class="btn-prev-stage" onclick="goToPrevStage()">
                        <i class="fa fa-arrow-left"></i> 上一阶段
                    </button>
                    <button class="btn-save" onclick="completeProcess()">
                        <i class="fa fa-check"></i> 完成流程
                    </button>
                </div>
            </div>
        </div>
        
    </div>
</div>

<!-- 收费项目添加弹窗 -->
<div class="modal-overlay" id="paymentModal" style="display:none;">
    <div class="modal-content">
        <div class="modal-header">
            <h4><i class="fa fa-money"></i> 添加收费项目</h4>
            <button class="modal-close" onclick="closePaymentModal()"><i class="fa fa-times"></i></button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label>项目名称</label>
                <input type="text" class="form-control" id="paymentItemName" placeholder="请输入项目名称">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>金额（元）</label>
                    <input type="number" class="form-control" id="paymentItemAmount" placeholder="0.00" step="0.01" min="0">
                </div>
                <div class="form-group">
                    <label>数量</label>
                    <input type="number" class="form-control" id="paymentItemQuantity" value="1" min="1">
                </div>
            </div>
            <div class="form-group">
                <label>项目描述</label>
                <textarea class="form-control" id="paymentItemDesc" placeholder="请输入项目描述" rows="2"></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn-cancel-modal" onclick="closePaymentModal()">取消</button>
            <button class="btn-save-modal" onclick="savePaymentItem()">保存</button>
        </div>
        <input type="hidden" id="currentPaymentNodeId" value="">
    </div>
</div>

<!-- 药品选择弹窗 -->
<div class="modal-overlay" id="medicineModal" style="display:none;">
    <div class="modal-content medicine-modal-content">
        <div class="modal-header">
            <h4><i class="fa fa-pills"></i> 选择药品</h4>
            <button class="modal-close" onclick="closeMedicineModal()"><i class="fa fa-times"></i></button>
        </div>
        <div class="modal-body">
            <div class="medicine-search-box">
                <input type="text" class="form-control" id="medicineSearchInput" placeholder="搜索药品名称..." oninput="filterMedicines()">
            </div>
            <div class="medicine-selection-list" id="medicineSelectionList">
                <!-- 动态加载药品列表 -->
                <div class="loading-placeholder">
                    <i class="fa fa-spinner fa-spin"></i>
                    <p>正在加载药品...</p>
                </div>
            </div>
            <div class="selected-medicines-summary">
                已选择 <span id="selectedMedicineCount">0</span> 种药品，
                共 <span id="selectedMedicineQuantity">0</span> 盒，
                合计 ¥<span id="selectedMedicineTotal">0.00</span>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn-cancel-modal" onclick="closeMedicineModal()">取消</button>
            <button class="btn-save-modal" onclick="saveMedicineSelection()">确认选择</button>
        </div>
    </div>
</div>

<!-- 药品详情悬停提示 -->
<div id="medicineTooltip" class="medicine-tooltip"></div>
