// 刷新Tab.iframe子页面, btn添加class:refresh
$(".refresh").on("click", function() {
	frameElement.src = frameElement.src;
	/*top.window.location.href = location.href;*/
});

// 使用toastr的自定义通知
// positionClass:toast-top-left,right,center,full-width, toast-bottom-right,left,center,full-width
toastr.options = {
	"closeButton": true,// 是否显示关闭按钮
	"debug": false,// 是否使用debug模式
	"progressBar": true,// 是否显示进度条
	"positionClass": "toast-top-right",// 弹出窗的位置
	"onclick": null,// 点击事件
	"showDuration": "1000",// 显示的动画时间
	"hideDuration": "1000",// 消失的动画时间
	"timeOut": "5000",// 展现时间
	"extendedTimeOut": "1000",// 加长展示时间
	"showEasing": "swing",// 显示时的动画缓冲方式
	"hideEasing": "linear",// 消失时的动画缓冲方式
	"showMethod": "fadeIn",// 显示时的动画方式
	"hideMethod": "fadeOut"// 消失时的动画方式
}
$.msg = {
	success : function(content, title) {
		toastr.success(content, title);
	},
	warning : function(content, title) {
		toastr.warning(content, title);
	},
	error : function(content, title) {
		toastr.error(content, title);
	},
	info : function(content, title) {
		toastr.info(content, title);
	}
};

// DataTables 汉化
var datatables = {
	language: {
		"sProcessing": "处理中...",
        "sLengthMenu": "显示 _MENU_ 项",
        "sZeroRecords": "没有匹配结果",
        "sInfo": "显示第 _START_ 至 _END_ 项，共 _TOTAL_ 项",
        "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
        "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
        "sInfoPostFix": "",
        "sSearch": "搜索:",
        "sUrl": "",
        "sEmptyTable": "表中数据为空",
        "sLoadingRecords": "载入中...",
        "sInfoThousands": ",",
        "oPaginate": {
            "sFirst": "首页",
            "sPrevious": "上页",
            "sNext": "下页",
            "sLast": "尾页"
        },
        "oAria": {
            "sSortAscending": ": 以升序排列此列",
            "sSortDescending": ": 以降序排列此列"
        }
	}
};
