<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%
    Long patientId = (Long) session.getAttribute("patientId");
    String username = (String) session.getAttribute("username");
%>

<!-- 消息提示 -->
<div class="profile-alert profile-alert-success" id="profileSuccessAlert" style="display:none;"></div>
<div class="profile-alert profile-alert-error" id="profileErrorAlert" style="display:none;"></div>

<div class="profile-layout">
    <!-- 左侧：用户头像区 -->
    <div class="profile-sidebar">
        <div class="profile-avatar-box">
            <div class="profile-avatar-circle">
                <i class="fa fa-user-md"></i>
            </div>
            <h3 class="profile-username" id="displayName"><%=username != null ? username : "用户"%></h3>
            <p class="profile-patient-id">ID: <%=patientId != null ? patientId : ""%></p>
            <div class="profile-status">
                <span class="status-dot"></span> 账户正常
            </div>
        </div>
        
        <div class="profile-stats">
            <div class="stat-item">
                <div class="stat-num" id="visitCount">--</div>
                <div class="stat-label">就诊次数</div>
            </div>
            <div class="stat-item">
                <div class="stat-num" id="pendingFee">--</div>
                <div class="stat-label">待缴费用</div>
            </div>
        </div>
    </div>
    
    <!-- 右侧：表单区 -->
    <div class="profile-main">
        <!-- 基本信息 -->
        <div class="profile-section">
            <div class="profile-section-header">
                <h4><i class="fa fa-id-card-o"></i> 基本信息</h4>
            </div>
            <form id="profileForm">
                <div class="profile-form-grid">
                    <div class="profile-form-item">
                        <label>用户名</label>
                        <input type="text" class="profile-input" id="profileUsername" name="username" placeholder="请输入用户名">
                    </div>
                    <div class="profile-form-item">
                        <label>手机号码</label>
                        <input type="tel" class="profile-input" id="profilePhoneNum" name="phoneNum" placeholder="请输入手机号码" maxlength="11">
                    </div>
                    <div class="profile-form-item">
                        <label>性别</label>
                        <div class="profile-radio-group">
                            <label class="profile-radio">
                                <input type="radio" name="profileGender" value="男">
                                <span class="radio-custom"></span>
                                男
                            </label>
                            <label class="profile-radio">
                                <input type="radio" name="profileGender" value="女">
                                <span class="radio-custom"></span>
                                女
                            </label>
                        </div>
                    </div>
                    <div class="profile-form-item profile-form-actions">
                        <button type="submit" class="profile-btn profile-btn-save">
                            <i class="fa fa-check"></i> 保存
                        </button>
                        <button type="button" class="profile-btn profile-btn-reset" onclick="loadProfileFormData()">
                            <i class="fa fa-undo"></i> 重置
                        </button>
                    </div>
                </div>
            </form>
        </div>
        

        <div class="profile-section">
            <div class="profile-section-header">
                <h4><i class="fa fa-shield"></i> 安全设置</h4>
            </div>
            <form id="passwordForm">
                <div class="profile-form-grid">
                    <div class="profile-form-item">
                        <label>当前密码</label>
                        <div class="profile-input-wrap">
                            <input type="password" class="profile-input" id="oldPassword" name="oldPassword" placeholder="请输入当前密码">
                            <button type="button" class="profile-eye-btn" onclick="togglePassword('oldPassword', this)">
                                <i class="fa fa-eye"></i>
                            </button>
                        </div>
                    </div>
                    <div class="profile-form-item">
                        <label>新密码</label>
                        <div class="profile-input-wrap">
                            <input type="password" class="profile-input" id="newPassword" name="newPassword" placeholder="至少6位字符" oninput="checkPasswordStrength()">
                            <button type="button" class="profile-eye-btn" onclick="togglePassword('newPassword', this)">
                                <i class="fa fa-eye"></i>
                            </button>
                        </div>
                        <div class="profile-password-bar">
                            <div class="profile-password-fill" id="strengthBar"></div>
                        </div>
                        <span class="profile-hint" id="strengthHint">密码强度</span>
                    </div>
                    <div class="profile-form-item">
                        <label>确认密码</label>
                        <div class="profile-input-wrap">
                            <input type="password" class="profile-input" id="confirmPassword" name="confirmPassword" placeholder="再次输入新密码">
                            <button type="button" class="profile-eye-btn" onclick="togglePassword('confirmPassword', this)">
                                <i class="fa fa-eye"></i>
                            </button>
                        </div>
                    </div>
                    <div class="profile-form-item profile-form-actions">
                        <button type="submit" class="profile-btn profile-btn-save">
                            <i class="fa fa-lock"></i> 修改密码
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
