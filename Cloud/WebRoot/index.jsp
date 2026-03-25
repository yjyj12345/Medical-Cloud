<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>智慧云平台</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
            height: 100vh;
            overflow: hidden;
            background: #FAF8F6;
        }
        
        :root {
            --theme-patient: #e85a4f;
            --theme-patient-hover: #d14b41;
            --theme-doctor: #059669;
            --theme-doctor-hover: #047857;
            --theme-current: #e85a4f;
            --theme-current-hover: #d14b41;
        }
        
        .page {
            display: flex;
            flex-direction: column;
            height: 100vh;
        }
        
        /* 顶部导航 */
        .header {
            padding: 16px 40px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            z-index: 20;
        }
        
        .logo {
            font-size: 18px;
            font-weight: 700;
            color: #1e293b;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .nav-links {
            display: flex;
            gap: 28px;
        }
        
        .nav-link {
            color: #64748b;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: color 0.2s;
        }
        
        .nav-link:hover { color: #1e293b; }
        
        /* 滑动容器 */
        .slides-container {
            flex: 1;
            position: relative;
            overflow: hidden;
        }
        
        .slides-wrapper {
            display: flex;
            height: 100%;
            transition: transform 0.7s cubic-bezier(0.32, 0.72, 0, 1);
        }
        
        .slide {
            min-width: 100%;
            height: 100%;
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            padding: 60px 128px 80px;
        }
        
        /* 左侧标题 */
        .slide-title-section {
            padding-top: 120px;
            padding-left: 20px;
            transition: opacity 0.6s, transform 0.6s;
        }
        
        .slide:not(.active) .slide-title-section {
            opacity: 0.3;
        }
        
        .slide-title {
            font-family: 'Courier New', Monaco, monospace;
            font-size: 96px;
            font-weight: 700;
            line-height: 1.1;
            color: #1e293b;
        }
        
        .slide-subtitle {
            font-family: 'Courier New', Monaco, monospace;
            font-size: 96px;
            font-weight: 700;
            line-height: 1.1;
            color: #1e293b;
            margin-left: 240px;
        }
        
        /* 右侧按钮 */
        .slide-button-section {
            padding-top: 260px;
            padding-right: 200px;
            transition: opacity 0.4s, transform 0.4s;
        }
        
        .slide:not(.active) .slide-button-section {
            opacity: 0.3;
            transform: scale(0.9);
        }
        
        .enter-btn {
            background: #1e293b;
            color: #fff;
            border: 2px solid #1e293b;
            padding: 28px 48px;
            font-size: 22px;
            font-weight: 700;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            white-space: nowrap;
        }
        
        .enter-btn:hover {
            background: #0f172a;
            transform: scale(1.05);
        }
        
        .enter-btn.disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .enter-btn.disabled:hover {
            transform: none;
            background: #1e293b;
        }
        
        /* 导航箭头 */
        .nav-arrow {
            position: fixed;
            top: 50%;
            transform: translateY(-50%);
            z-index: 10;
            background: transparent;
            border: none;
            cursor: pointer;
            padding: 16px;
            transition: opacity 0.2s;
        }
        
        .nav-arrow:hover { opacity: 0.6; }
        .nav-arrow.prev { left: 24px; }
        .nav-arrow.next { right: 24px; }
        .nav-arrow.hidden { opacity: 0; pointer-events: none; }
        
        .nav-arrow svg {
            width: 40px;
            height: 40px;
            stroke: #1e293b;
            stroke-width: 3;
            fill: none;
        }
        
        /* 页面指示器 */
        .indicators {
            position: fixed;
            bottom: 32px;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 12px;
            z-index: 10;
        }
        
        .indicators.hidden { opacity: 0; pointer-events: none; }
        
        .indicator {
            width: 8px;
            height: 8px;
            border-radius: 4px;
            background: #cbd5e1;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .indicator.active {
            width: 32px;
            background: #1e293b;
        }
        
        .indicator:hover { background: #64748b; }
        
        /* 遮罩层 */
        .overlay {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0);
            z-index: 998;
            pointer-events: none;
            transition: background 0.4s;
        }
        
        .overlay.show {
            background: rgba(0,0,0,0.4);
            pointer-events: all;
        }
        
        /* 展开表单 */
        .expand-form {
            position: fixed;
            background: #fff;
            z-index: 1000;
            overflow: hidden;
            pointer-events: none;
            opacity: 0;
        }
        
        .expand-form.animating {
            pointer-events: all;
            opacity: 1;
            transition: 
                top 0.32s cubic-bezier(0.32, 0.72, 0, 1),
                left 0.32s cubic-bezier(0.32, 0.72, 0, 1),
                width 0.32s cubic-bezier(0.32, 0.72, 0, 1),
                height 0.32s cubic-bezier(0.32, 0.72, 0, 1),
                border-radius 0.32s cubic-bezier(0.32, 0.72, 0, 1);
        }
        
        .expand-form.expanded { background: #fff; }
        
        .expand-form.closing {
            transition: 
                top 0.15s cubic-bezier(0.32, 0.72, 0, 1),
                left 0.15s cubic-bezier(0.32, 0.72, 0, 1),
                width 0.15s cubic-bezier(0.32, 0.72, 0, 1),
                height 0.15s cubic-bezier(0.32, 0.72, 0, 1),
                border-radius 0.15s cubic-bezier(0.32, 0.72, 0, 1),
                opacity 0.05s 0.12s;
        }
        
        .btn-text {
            position: absolute;
            inset: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 22px;
            font-weight: 700;
            transition: opacity 0.08s;
        }
        
        .expand-form.animating .btn-text { opacity: 0; }
        
        .form-wrapper {
            position: absolute;
            inset: 0;
            padding: 36px;
            opacity: 0;
            overflow: hidden;
        }
        
        .expand-form.expanded .form-wrapper {
            opacity: 1;
        }
        
        .form-close {
            position: absolute;
            top: 16px;
            right: 16px;
            background: transparent;
            border: none;
            cursor: pointer;
            color: #94a3b8;
            font-size: 28px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s;
            z-index: 10;
        }
        
        .form-close:hover {
            color: #1e293b;
            transform: rotate(90deg);
        }
        
        .form-title {
            font-size: 24px;
            font-weight: 800;
            color: #1e293b;
            margin-bottom: 16px;
        }
        
        .mode-tabs {
            display: flex;
            border-bottom: 1px solid #e2e8f0;
            margin-bottom: 14px;
        }
        
        .mode-tab {
            flex: 1;
            padding: 10px;
            border: none;
            background: transparent;
            color: #94a3b8;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            position: relative;
            transition: color 0.2s;
        }
        
        .mode-tab.active { color: var(--theme-current); }
        
        .mode-tab.active::after {
            content: '';
            position: absolute;
            bottom: -1px;
            left: 0;
            right: 0;
            height: 2px;
            background: var(--theme-current);
        }
        
        .role-tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 14px;
        }
        
        .role-tab {
            flex: 1;
            padding: 8px;
            border: 1px solid #e2e8f0;
            background: #fff;
            border-radius: 6px;
            cursor: pointer;
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #64748b;
            transition: all 0.2s;
        }
        
        .role-tab.active {
            border-color: var(--theme-current);
            background: var(--theme-current);
            color: #fff;
        }
        
        .form-section { display: none; }
        .form-section.active { display: block; }
        
        .form-group { margin-bottom: 12px; }
        
        .form-label {
            display: block;
            font-size: 13px;
            color: #64748b;
            margin-bottom: 4px;
            font-weight: 600;
        }
        
        .form-input, .form-select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            transition: border-color 0.2s;
            background: #fff;
        }
        
        .form-input:focus, .form-select:focus {
            outline: none;
            border-color: var(--theme-current);
        }
        
        .form-row {
            display: flex;
            gap: 12px;
        }
        
        .form-row .form-group { flex: 1; }
        
        .form-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
            font-size: 14px;
        }
        
        .form-options a {
            color: var(--theme-current);
            text-decoration: none;
        }
        
        .btn-submit {
            width: 100%;
            background: var(--theme-current);
            color: #fff;
            border: none;
            padding: 12px;
            border-radius: 6px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-submit:hover { background: var(--theme-current-hover); }
        
        .doctor-fields {
            display: none;
            background: #ecfdf5;
            padding: 10px;
            border-radius: 6px;
            margin-bottom: 12px;
            border: 1px dashed #059669;
        }
        
        .doctor-fields.show { display: block; }
        
        .doctor-label {
            font-size: 11px;
            color: #059669;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 12px;
        }
    </style>
</head>
<body>
    <div class="page">
        <header class="header">
            <div class="logo">智慧云平台</div>
            <nav class="nav-links">
                <a href="#" class="nav-link">关于我们</a>
                <a href="#" class="nav-link">帮助中心</a>
            </nav>
        </header>
        
        <div class="slides-container">
            <div class="slides-wrapper" id="slidesWrapper">
                <!-- 医疗云 -->
                <div class="slide active" data-cloud="healthcare" data-enabled="true">
                    <div class="slide-title-section">
                        <h1 class="slide-title">医疗情况</h1>
                        <h1 class="slide-subtitle">触手可及</h1>
                    </div>
                    <div class="slide-button-section">
                        <button class="enter-btn" onclick="openForm(event, '医疗云')">进入医疗云</button>
                    </div>
                </div>
                
                <!-- 防疫云 -->
                <div class="slide" data-cloud="epidemic" data-enabled="false">
                    <div class="slide-title-section">
                        <h1 class="slide-title">健康监测</h1>
                        <h1 class="slide-subtitle">安全守护</h1>
                    </div>
                    <div class="slide-button-section">
                        <button class="enter-btn disabled" onclick="showDisabled()">进入防疫云</button>
                    </div>
                </div>
                
                <!-- 教育云 -->
                <div class="slide" data-cloud="education" data-enabled="false">
                    <div class="slide-title-section">
                        <h1 class="slide-title">知识共享</h1>
                        <h1 class="slide-subtitle">智慧未来</h1>
                    </div>
                    <div class="slide-button-section">
                        <button class="enter-btn disabled" onclick="showDisabled()">进入教育云</button>
                    </div>
                </div>
                
                <!-- 企业云 -->
                <div class="slide" data-cloud="enterprise" data-enabled="false">
                    <div class="slide-title-section">
                        <h1 class="slide-title">高效协同</h1>
                        <h1 class="slide-subtitle">智能办公</h1>
                    </div>
                    <div class="slide-button-section">
                        <button class="enter-btn disabled" onclick="showDisabled()">进入企业云</button>
                    </div>
                </div>
                
                <!-- 政府云 -->
                <div class="slide" data-cloud="government" data-enabled="false">
                    <div class="slide-title-section">
                        <h1 class="slide-title">数字政务</h1>
                        <h1 class="slide-subtitle">便民服务</h1>
                    </div>
                    <div class="slide-button-section">
                        <button class="enter-btn disabled" onclick="showDisabled()">进入政府云</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 导航箭头 -->
        <button class="nav-arrow prev" onclick="prevSlide()">
            <svg viewBox="0 0 24 24"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <button class="nav-arrow next" onclick="nextSlide()">
            <svg viewBox="0 0 24 24"><path d="M9 18l6-6-6-6"/></svg>
        </button>
        
        <!-- 页面指示器 -->
        <div class="indicators" id="indicators">
            <button class="indicator active" onclick="goToSlide(0)"></button>
            <button class="indicator" onclick="goToSlide(1)"></button>
            <button class="indicator" onclick="goToSlide(2)"></button>
            <button class="indicator" onclick="goToSlide(3)"></button>
            <button class="indicator" onclick="goToSlide(4)"></button>
        </div>
    </div>
    
    <!-- 遮罩 -->
    <div class="overlay" id="overlay" onclick="closeForm()"></div>
    
    <!-- 展开表单 -->
    <div class="expand-form" id="expandForm">
        <span class="btn-text" id="btnText">进入医疗云</span>
        
        <div class="form-wrapper">
            <button class="form-close" onclick="closeForm()">×</button>
            
            <h2 class="form-title">医疗云平台</h2>
            
            <div class="mode-tabs">
                <button type="button" class="mode-tab active" onclick="switchMode('login')">登录</button>
                <button type="button" class="mode-tab" onclick="switchMode('register')">注册</button>
            </div>
            
            <div class="role-tabs">
                <div class="role-tab active" onclick="switchRole('patient')"><i class="fa fa-user"></i> 患者</div>
                <div class="role-tab" onclick="switchRole('doctor')"><i class="fa fa-user-md"></i> 医生</div>
            </div>
            
            <!-- 登录表单 -->
            <div class="form-section active" id="loginSection">
                <form action="<%=path%>/UserManageService_Tool" method="post">
                    <input type="hidden" name="action" value="login">
                    <input type="hidden" name="role" id="loginRole" value="patient">
                    
                    <div class="form-group">
                        <label class="form-label">用户名</label>
                        <input name="username" type="text" class="form-input" placeholder="请输入用户名" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label">密码</label>
                        <input name="password" type="password" class="form-input" placeholder="请输入密码" required>
                    </div>
                    <div class="form-options">
                        <label style="display:flex;align-items:center;gap:6px;color:#64748b;cursor:pointer">
                            <input type="checkbox"> 记住我
                        </label>
                        <a href="#">忘记密码？</a>
                    </div>
                    <button type="submit" class="btn-submit">登录</button>
                </form>
            </div>
            
            <!-- 注册表单 -->
            <div class="form-section" id="registerSection">
                <form action="<%=path%>/UserManageService_Tool" method="post" onsubmit="return validateRegister()">
                    <input type="hidden" name="action" value="register">
                    <input type="hidden" name="role" id="registerRole" value="patient">
                    
                    <div class="form-group">
                        <label class="form-label">用户名</label>
                        <input name="username" type="text" class="form-input" placeholder="请输入用户名" required>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">密码</label>
                            <input name="password" id="regPwd" type="password" class="form-input" placeholder="设置密码" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">确认密码</label>
                            <input name="repassword" id="regPwd2" type="password" class="form-input" placeholder="再次输入" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">手机号</label>
                            <input name="phoneNum" type="text" class="form-input" placeholder="请输入手机号" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">性别</label>
                            <select name="gender" class="form-select" required>
                                <option value="">请选择</option>
                                <option value="男">男</option>
                                <option value="女">女</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="doctor-fields" id="doctorFields">
                        <div class="doctor-label">医生信息</div>
                        <div class="form-row">
                            <div class="form-group" style="margin:0">
                                <label class="form-label">职称</label>
                                <select name="title" class="form-select">
                                    <option value="医师">医师</option>
                                    <option value="主治医师">主治医师</option>
                                    <option value="副主任医师">副主任医师</option>
                                    <option value="主任医师">主任医师</option>
                                </select>
                            </div>
                            <div class="form-group" style="margin:0">
                                <label class="form-label">科室</label>
                                <select name="specialty" class="form-select">
                                    <option value="全科">全科</option>
                                    <option value="内科">内科</option>
                                    <option value="外科">外科</option>
                                    <option value="儿科">儿科</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <button type="submit" class="btn-submit">注册</button>
                </form>
            </div>
        </div>
    </div>
    
    <script>
        let currentIndex = 0;
        const totalSlides = 5;
        let buttonRect = null;
        let isOpen = false;
        
        function updateSlides() {
            const wrapper = document.getElementById('slidesWrapper');
            wrapper.style.transform = `translateX(-${currentIndex * 100}%)`;
            
            document.querySelectorAll('.slide').forEach((s, i) => {
                s.classList.toggle('active', i === currentIndex);
            });
            
            document.querySelectorAll('.indicator').forEach((ind, i) => {
                ind.classList.toggle('active', i === currentIndex);
            });
        }
        
        function prevSlide() {
            if (isOpen) return;
            currentIndex = (currentIndex - 1 + totalSlides) % totalSlides;
            updateSlides();
        }
        
        function nextSlide() {
            if (isOpen) return;
            currentIndex = (currentIndex + 1) % totalSlides;
            updateSlides();
        }
        
        function goToSlide(index) {
            if (isOpen) return;
            currentIndex = index;
            updateSlides();
        }
        
        function showDisabled() {
            alert('该云服务暂未开放，敬请期待！');
        }
        
        function openForm(e, cloudName) {
            if (isOpen) return;
            isOpen = true;
            
            const btn = e.currentTarget;
            const rect = btn.getBoundingClientRect();
            buttonRect = rect;
            
            const form = document.getElementById('expandForm');
            const overlay = document.getElementById('overlay');
            
            // 更新按钮文字
            document.getElementById('btnText').textContent = '进入' + cloudName.replace('云', '') + '云';
            
            const formW = 420, formH = 680;
            let finalX = rect.left - (formW - rect.width) / 2;
            let finalY = rect.top - 280;
            
            finalX = Math.max(40, Math.min(finalX, window.innerWidth - formW - 40));
            finalY = Math.max(40, Math.min(finalY, window.innerHeight - formH - 40));
            
            // 初始状态
            form.style.top = rect.top + 'px';
            form.style.left = rect.left + 'px';
            form.style.width = rect.width + 'px';
            form.style.height = rect.height + 'px';
            form.style.borderRadius = '8px';
            form.classList.remove('closing', 'expanded');
            form.classList.add('animating');
            
            overlay.classList.add('show');
            
            // 隐藏导航
            document.querySelectorAll('.nav-arrow').forEach(a => a.classList.add('hidden'));
            document.getElementById('indicators').classList.add('hidden');
            
            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    form.style.top = finalY + 'px';
                    form.style.left = finalX + 'px';
                    form.style.width = formW + 'px';
                    form.style.height = formH + 'px';
                    form.style.borderRadius = '16px';
                    
                    setTimeout(() => form.classList.add('expanded'), 80);
                });
            });
        }
        
        function closeForm() {
            if (!isOpen || !buttonRect) return;
            
            const form = document.getElementById('expandForm');
            const overlay = document.getElementById('overlay');
            
            form.classList.remove('expanded');
            form.classList.add('closing');
            
            setTimeout(() => {
                form.style.top = buttonRect.top + 'px';
                form.style.left = buttonRect.left + 'px';
                form.style.width = buttonRect.width + 'px';
                form.style.height = buttonRect.height + 'px';
                form.style.borderRadius = '8px';
            }, 50);
            
            overlay.classList.remove('show');
            
            setTimeout(() => {
                form.classList.remove('animating', 'closing');
                document.querySelectorAll('.nav-arrow').forEach(a => a.classList.remove('hidden'));
                document.getElementById('indicators').classList.remove('hidden');
                isOpen = false;
            }, 300);
        }
        
        function switchMode(mode) {
            document.querySelectorAll('.mode-tab').forEach(t => t.classList.remove('active'));
            event.target.classList.add('active');
            
            document.getElementById('loginSection').classList.toggle('active', mode === 'login');
            document.getElementById('registerSection').classList.toggle('active', mode === 'register');
            
            if (mode === 'login') {
                document.getElementById('doctorFields').classList.remove('show');
            } else {
                const role = document.getElementById('registerRole').value;
                if (role === 'doctor') {
                    document.getElementById('doctorFields').classList.add('show');
                }
            }
        }
        
        function switchRole(role) {
            document.querySelectorAll('.role-tab').forEach(t => t.classList.remove('active'));
            event.target.classList.add('active');
            
            document.getElementById('loginRole').value = role;
            document.getElementById('registerRole').value = role;
            
            const isRegister = document.getElementById('registerSection').classList.contains('active');
            if (isRegister && role === 'doctor') {
                document.getElementById('doctorFields').classList.add('show');
            } else {
                document.getElementById('doctorFields').classList.remove('show');
            }
            
            // 根据角色切换主题色
            if (role === 'doctor') {
                document.documentElement.style.setProperty('--theme-current', 'var(--theme-doctor)');
                document.documentElement.style.setProperty('--theme-current-hover', 'var(--theme-doctor-hover)');
            } else {
                document.documentElement.style.setProperty('--theme-current', 'var(--theme-patient)');
                document.documentElement.style.setProperty('--theme-current-hover', 'var(--theme-patient-hover)');
            }
        }
        
        function validateRegister() {
            const pwd = document.getElementById('regPwd').value;
            const pwd2 = document.getElementById('regPwd2').value;
            if (pwd !== pwd2) {
                alert('两次密码不一致');
                return false;
            }
            return true;
        }
        
        // 键盘导航
        document.addEventListener('keydown', e => {
            if (isOpen) {
                if (e.key === 'Escape') closeForm();
                return;
            }
            if (e.key === 'ArrowLeft') prevSlide();
            if (e.key === 'ArrowRight') nextSlide();
        });
    </script>
</body>
</html>
