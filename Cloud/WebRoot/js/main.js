$(document).ready(function() {
    // 处理所有表单提交的默认行为（防止跳转）
    $('form').submit(function(e) {
        e.preventDefault();
    });

    // 处理日期输入框默认值（当前日期）
    const today = new Date().toISOString().split('T')[0];
    $('input[type="date"]').each(function() {
        if (!$(this).val()) {
            $(this).val(today);
        }
    });

    // 云服务选择交互
    $('.service-item').click(function() {
        $('.service-item').removeClass('active');
        $(this).addClass('active');
    });
    $('#enterCloud').click(function() {
        let selected = $('.service-item.active').data('target');
        if (selected === 'healthcare') {
            window.location.href = 'healthcare.html';
        } else {
            alert('该云服务暂未开放，敬请期待！');
        }
    });

    // 通用提示框函数
    window.showAlert = function(message) {
        alert(message);
    };
});