<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 错误提示 -->
<c:if test="${not empty errorMsg}">
    <div class="error-alert">
        <i class="fa fa-exclamation-circle"></i>${errorMsg}
    </div>
</c:if>

<!-- 病历数据展示 -->
<c:choose>
    <c:when test="${not empty medicalHistoryList}">
        <c:forEach items="${medicalHistoryList}" var="process">
            <div class="history-card">
                <div class="history-header">
                    <h3 style="display:flex; align-items:center; gap:10px; margin:0;">
                        <span>诊疗流程 #${process.processId}</span>
                        <c:choose>
                            <c:when test="${process.processStatus == '已完成' or process.processStatus == 'completed'}">
                                <span class="label label-success" style="vertical-align:middle;">已完成</span>
                            </c:when>
                            <c:when test="${process.processStatus == '进行中' or process.processStatus == 'inprogress' or process.processStatus == 'in_progress' or process.processStatus == '已预约'}">
                                <a href="javascript:void(0)" onclick="switchTab('process')" class="label label-warning" style="text-decoration:none; vertical-align:middle;">
                                    进行中 <i class="fa fa-arrow-right"></i>
                                </a>
                            </c:when>
                            <c:otherwise><span class="label label-default" style="vertical-align:middle;">${process.processStatus}</span></c:otherwise>
                        </c:choose>
                    </h3>
                    <div class="history-meta">
                        <span><i class="fa fa-calendar"></i>创建时间：${process.createdAt}</span>
                        <c:if test="${not empty process.completedAt}">
                            <span><i class="fa fa-check-circle"></i>完成时间：${process.completedAt}</span>
                        </c:if>
                    </div>
                </div>

                <!-- 进程结点时间线 -->
                <div class="timeline">
                    <c:choose>
                        <c:when test="${not empty process.processNodes and process.processNodes.size() > 0}">
                            <!-- 1. 挂号（nodeId=1） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 1}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <c:if test="${not empty node.reminder}"><i class="fa fa-bell"></i>${node.reminder}<br></c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- 2. 问诊（nodeId=2） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 2}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <c:if test="${not empty node.diagnosisText}"><i class="fa fa-stethoscope"></i>${node.diagnosisText}<br></c:if>
                                            <c:if test="${not empty node.reminder}"><i class="fa fa-bell"></i>${node.reminder}<br></c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- 3. 检查（nodeId=3） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 3}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <c:if test="${not empty node.diagnosisText}"><i class="fa fa-stethoscope"></i>${node.diagnosisText}<br></c:if>
                                            <c:if test="${not empty node.reminder}"><i class="fa fa-bell"></i>${node.reminder}<br></c:if>
                                            <c:if test="${not empty node.pictures}">
                                                <div class="examination-images">
                                                    <i class="fa fa-picture-o"></i><span>检查影像：</span>
                                                    <div class="patient-image-preview" data-pictures="${node.pictures}">
                                                    </div>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- 4. 治疗（nodeId=4） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 4}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <c:if test="${not empty node.diagnosisText}"><i class="fa fa-stethoscope"></i>${node.diagnosisText}<br></c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- 5. 取药（nodeId=5） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 5}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <c:if test="${not empty node.reminder}"><i class="fa fa-bell"></i>${node.reminder}<br></c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- 6. 完成（nodeId=6） -->
                            <c:forEach items="${process.processNodes}" var="node">
                                <c:if test="${node.nodeId == 6}">
                                    <div class="timeline-item">
                                        <c:choose>
                                            <c:when test="${node.nodeStatus == '已完成' or node.nodeStatus == 'completed'}">
                                                <div class="timeline-dot completed"></div>
                                                <span class="timeline-label completed">${node.nodeName}</span>
                                            </c:when>
                                            <c:when test="${node.nodeStatus == '进行中' or node.nodeStatus == 'inprogress' or node.nodeStatus == 'in_progress'}">
                                                <div class="timeline-dot active"></div>
                                                <span class="timeline-label active">${node.nodeName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="timeline-dot pending"></div>
                                                <span class="timeline-label pending">${node.nodeName}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="timeline-time">
                                            <c:if test="${not empty node.createAt}">${node.createAt}</c:if>
                                        </span>
                                        <div class="timeline-content">
                                            <i class="fa fa-check-circle"></i>诊疗流程已完成
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="timeline-item">
                                <div class="timeline-dot pending"></div>
                                <span class="timeline-label">暂无结点</span>
                                <div class="timeline-content">该诊疗流程暂无进程结点记录</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="no-history-hint">
            <div class="no-history-icon"><i class="fa fa-file-text-o"></i></div>
            <h3>暂无看病历史</h3>
            <p>您目前还没有任何就诊记录。完成就诊后，您的看病历史将会显示在这里。</p>
            <button class="btn-go-registration" onclick="switchTab('registration')">
                <i class="fa fa-plus-circle"></i> 去预约挂号
            </button>
        </div>
    </c:otherwise>
</c:choose>
