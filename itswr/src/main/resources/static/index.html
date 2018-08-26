<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>SYS Resource</title>
<jsp:include page="layout/resource-css.jsp" />
</head>
<body class="gray-bg">

<div class="p10 animated fadeInRight">
	<div class="row">
		<div class="col-sm-12">
			<div class="panel panel-default mb10">
				<div class="panel-body">
					<button type="button" class="btn btn-sm btn-outline btn-info refresh">刷新页面</button>
					<button type="button" id="addFirstLevelNav" class="btn btn-sm btn-outline btn-info">添加一级导航菜单</button>
					<button type="button" id="batchEnableResources" class="btn btn-sm btn-outline btn-info">批量启用</button>
					<button type="button" id="batchDisableResources" class="btn btn-sm btn-outline btn-info">批量禁用</button>
					<button type="button" id="batchDeleteResources" class="btn btn-sm btn-outline btn-info">批量删除</button>
					<button type="button" id="checkAll" class="btn btn-sm btn-outline btn-info">全部选中</button>
					<button type="button" id="inverseAll" class="btn btn-sm btn-outline btn-info">全部反选</button>
					<button type="button" id="uncheckAll" class="btn btn-sm btn-outline btn-info">全不选中</button>
					<button type="button" id="collapseAll" class="btn btn-sm btn-outline btn-info">折叠所有</button>
					<button type="button" id="expandAll" class="btn btn-sm btn-outline btn-info">展开所有</button>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-sm-12">
			<div class="panel panel-default mb0">
				<div class="panel-body">
					<div class="row">
						<div class="col-sm-4">
							<div class="panel panel-default mb0">
								<div class="panel-body">
									<div id="zTree" class="ztree" style="height:calc(100vh - 157px);overflow:auto;"></div>
								</div>
							</div>
						</div>
						<div class="col-sm-8">
							<div class="panel panel-default mb0">
								<div class="panel-body" style="height:calc(100vh - 127px);overflow:auto;">
									<form role="form" class="form-horizontal" id="updateResourceForm">
										<input type="hidden" id="id" name="id"/>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源类型 - type</label>
					                        <div class="col-sm-8">
					                        	<select name="type" id="type" class="form-control pointer" required>
					                        		<option></option>
					                        		<option value="navigation">导航</option>
					                        		<!-- 目前只提供菜单类型的资源,以后再根据情况扩展 -->
					                        		<!-- <option value="button">按钮</option> -->
					                        	</select>
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源名称 - name</label>
					                        <div class="col-sm-8">
						                        <input type="text" id="name" name="name" class="form-control" required>
					                        </div>
					                    </div>
										<div class="form-group"">
					                        <label class="control-label col-sm-4">资源描述 - description</label>
					                        <div class="col-sm-8">
						                        <input type="text" id="description" name="description" class="form-control">
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源图标 - ico</label>
					                        <div class="col-sm-8">
					                        	<div class="input-group">
							                        <input type="text" id="ico" name="ico" class="form-control">
	    											<span class="ml10 input-group-addon">图标 <i id="icon" class=""></i></span>
					                        	</div>
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源连接 - url</label>
					                        <div class="col-sm-8">
						                        <input type="text" id="url" name="url" class="form-control">
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源显示顺序 - priority</label>
					                        <div class="col-sm-8">
						                        <input type="text" id="priority" name="priority" class="form-control">
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源权限字符串 - permission</label>
					                        <div class="col-sm-8">
						                        <input type="text" id="permission" name="permission" class="form-control">
					                        </div>
					                    </div>
										<div class="form-group">
					                        <label class="control-label col-sm-4">资源是否启用 - available</label>
					                        <div class="col-sm-8" style="height: 34px;">
												<label for="availableTrue" class="radio-inline pointer pl0">
													<input type="radio" id="availableTrue" name="available" value="true" required>启用
												</label>
												<label for="availableFalse" class="radio-inline pointer">
						                       		<input type="radio" id="availableFalse" name="available" value="false" required>禁用
												</label>
					                        </div>
					                    </div>
					                    <div class="col-sm-offset-4 pl10">
						                    <button type="button" id="updateResourceSubmitBtn" class="btn btn-sm btn-outline btn-info">保存</button>
				                        </div>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
	
<jsp:include page="layout/resource-js.jsp" />
<script>
//可以通过该方法获取zTree对象,而不必保存全局变量: var zTree = $.fn.zTree.getZTreeObj("zTree");
var zNode;
var zSetting = {
	data: {
		simpleData: {
			enable: true,
			idKey: "id",
			pIdKey: "parentId"
		},
		key: {
			url: "null"
		}
	},
	callback: {
		onClick: function(event, treeId, treeNode) {
			resetUpdateResourceForm();
			zNode = treeNode;
			$("#id").val(treeNode.id);
			$("#description").val(treeNode.description);
			$("#type").val(treeNode.type);
			$("#name").val(treeNode.name);
			$("#ico").val(treeNode.ico);
			$("#icon").attr("class", treeNode.ico);
			$("#url").val(treeNode.url);
			$("#priority").val(treeNode.priority);
			$("#permission").val(treeNode.permission);
			$("input[name=available][value="+treeNode.available+"]").iCheck("check");
		}
	},
	view: {
		addHoverDom: function(treeId, treeNode) {
			var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0 || $("#removeBtn_"+treeNode.tId).length>0) return;
			var addStr = "<span class='button add' id='addBtn_"+treeNode.tId+"' title='添加新的资源节点' onfocus='this.blur();'></span>";
			var removeStr = '<span class="button remove" id="removeBtn_'+treeNode.tId+'" title="移除资源结点"></span>';
			// 非navigation类型的资源节点不允许添加子资源节点
			if (treeNode.type === "navigation") {
				sObj.append(addStr);
			}
			// 包含子资源节点的父资源结点 不允许删除
			if (!(treeNode.hasOwnProperty("children") && treeNode.children.length !== 0)) {
				sObj.append(removeStr)
			}
			var addBtn = $("#addBtn_"+treeNode.tId);
			if (addBtn) addBtn.bind("click", function(){
				$.postJson("resource/insert", {parentId: treeNode.id}, false, function(response) {
					if (response.status == 1) {
						$.fn.zTree.getZTreeObj("zTree").addNodes(treeNode, response.data.resource);
					} else if (response.status == 0) {
						$.msg.info("操作失败");
					} else if (response.status == -1) {
						$.msg.warning(response.message);
					}
				}, function() {
					$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
				});
				return false;
			});
			var removeBtn = $("#removeBtn_"+treeNode.tId);
			if (removeBtn) removeBtn.bind("click", function(){
				var index = layer.open({
					title: "删除资源结点",
					icon: 0,
					content: "确认要删除该资源结点吗?",
					btn: "确认",
					yes: function() {
						layer.close(index);
						$.postJson("resource/delete", {resourceIds: [treeNode.id]}, false, function(response) {
							if (response.status == 1) {
								$.msg.success("操作成功");
								$.fn.zTree.getZTreeObj("zTree").removeNode(treeNode);
								resetUpdateResourceForm();
							} else if (response.status == 0) {
								$.msg.info("操作失败");
							} else if (response.status == -1) {
								$.msg.warning(response.message);
							}
						}, function() {
							$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
						});
					}
				});
				return false;
			});
        },
		removeHoverDom: function(treeId, treeNode) {
			$("#addBtn_"+treeNode.tId).unbind().remove();
			$("#removeBtn_"+treeNode.tId).unbind().remove();
		},
		selectedMulti: false
	},
	check: {
		enable: true
	}
};
function resetUpdateResourceForm() {
	$("#id").val("");
	$("#updateResourceForm").validate().resetForm();
	document.getElementById("updateResourceForm").reset();
	$("#ico").next().find("i").attr("class", "");
	// iCheck的radio不能取消选中么? $().iCheck("uncheck");没效果
	$("input[name=available]").iCheck("destroy").prop("checked", false).iCheck({
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green'
	});
}
$(document).ready(function() {
	$("input").iCheck({
		checkboxClass : 'icheckbox_square-green',
		radioClass : 'iradio_square-green'
	});
	$(document).on("click", "#checkAll", function() {
		$.fn.zTree.getZTreeObj("zTree").checkAllNodes(true);
	});
	$(document).on("click", "#inverseAll", function() {
		var zTree = $.fn.zTree.getZTreeObj("zTree");
		var nodes = zTree.transformToArray(zTree.getNodes());
		$.each(nodes, function(index, node) {
			if (!node.getCheckStatus().half) {
				$.log(node.name);
				node.checked = !node.checked;
				zTree.updateNode(node);
			}
		});
	});
	$(document).on("click", "#uncheckAll", function() {
		$.fn.zTree.getZTreeObj("zTree").checkAllNodes(false);
	});
	$(document).on("click", "#expandAll", function() {
		$.fn.zTree.getZTreeObj("zTree").expandAll(true);
	});
	$(document).on("click", "#collapseAll", function() {
		$.fn.zTree.getZTreeObj("zTree").expandAll(false);
	});
	$(document).on("click", "#addFirstLevelNav", function() {
		$.postJson("resource/insert", {parentId: 0}, false, function(response) {
			if (response.status == 1) {
				$.fn.zTree.getZTreeObj("zTree").addNodes(null, response.data.resource);
			} else if (response.status == 0) {
				$.msg.info("操作失败");
			} else if (response.status == -1) {
				$.msg.warning(response.message);
			}
		}, function() {
			$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
		});
	});
	$(document).on("click", "#batchEnableResources", function() {
		var nodes = $.fn.zTree.getZTreeObj("zTree").getCheckedNodes(true);
		if (nodes.length === 0) {
			$.msg.warning("需要选择至少一个资源节点才能执行此操作");
			return false;
		}
		var resourceIds = new Array();
		var willBeEnabledNodes = new Array();
		$.each(nodes, function(index, node) {
			if (!node.getCheckStatus().half) {
				resourceIds.push(node.id);
				willBeEnabledNodes.push(node);
			}
		});
		$.postJson("resource/enable", {resourceIds: resourceIds}, false, function(response) {
			if (response.status == 1) {
				$.msg.success("操作成功");
				var zTree = $.fn.zTree.getZTreeObj("zTree");
				$.each(willBeEnabledNodes, function(index, node) {
					node.available = true;
					zTree.updateNode(node);
				});
				resetUpdateResourceForm();
			} else if (response.status == 0) {
				$.msg.info("操作失败");
			} else if (response.status == -1) {
				$.msg.warning(response.message);
			}
		}, function() {
			$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
		});
	});
	$(document).on("click", "#batchDisableResources", function() {
		var nodes = $.fn.zTree.getZTreeObj("zTree").getCheckedNodes(true);
		if (nodes.length === 0) {
			$.msg.warning("需要选择至少一个资源节点才能执行此操作");
			return false;
		}
		var resourceIds = new Array();
		var willBeDisabledNodes = new Array();
		$.each(nodes, function(index, node) {
			if (!node.getCheckStatus().half) {
				resourceIds.push(node.id);
				willBeDisabledNodes.push(node);
			}
		});
		$.postJson("resource/disable", {resourceIds: resourceIds}, false, function(response) {
			if (response.status == 1) {
				$.msg.success("操作成功");
				var zTree = $.fn.zTree.getZTreeObj("zTree");
				$.each(willBeDisabledNodes, function(index, node) {
					node.available = false;
					zTree.updateNode(node);
				});
				resetUpdateResourceForm();
			} else if (response.status == 0) {
				$.msg.info("操作失败");
			} else if (response.status == -1) {
				$.msg.warning(response.message);
			}
		}, function() {
			$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
		});
	});
	$(document).on("click", "#batchDeleteResources", function() {
		var nodes = $.fn.zTree.getZTreeObj("zTree").getCheckedNodes(true);
		if (nodes.length === 0) {
			$.msg.warning("需要选择至少一个资源节点才能执行此操作");
			return false;
		}
		var resourceIds = new Array();
		var willBeRemovedNodes = new Array();
		$.each(nodes, function(index, node) {
			if (!node.getCheckStatus().half) {
				willBeRemovedNodes.push(node);
				resourceIds.push(node.id);
			}
		});
		var index = layer.open({
			title: "删除资源结点",
			icon: 0,
			content: "确认要删除选中的资源结点吗?",
			btn: "确认",
			yes: function() {
				layer.close(index);
				$.postJson("resource/delete", {resourceIds: resourceIds}, false, function(response) {
					if (response.status == 1) {
						$.msg.success("操作成功");
						var zTree = $.fn.zTree.getZTreeObj("zTree");
						$.each(willBeRemovedNodes, function(index, node) {
							zTree.removeNode(node);
						});
						resetUpdateResourceForm();
						// 更新树节点的选中状态, 全部取消选中就好了
						zTree.checkAllNodes(false);
					} else if (response.status == 0) {
						$.msg.info("操作失败");
					} else if (response.status == -1) {
						$.msg.warning(response.message);
					}
				}, function() {
					$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
				});
			}
		});
	});
	/* change不管用,换成input却可以 */
	$(document).on("input blur", "#ico", function() {
		$("#icon").attr("class", $(this).val());
	});
	// 初始化tree数据
	$.postJson("resource/all", null, false, function(response) {
		if (response.status == 1) {
			$.fn.zTree.init($("#zTree"), zSetting, response.data.resources).expandAll(true);
		} else if (response.status == 0) {
			$.msg.info("初始化数据失败");
		} else if (response.status == -1) {
			$.msg.warning(response.message);
		}
	}, function() {
		$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
	});
	$(document).on("click", "#updateResourceSubmitBtn", function() {
		var id = $("#id").val();
		if ($.isNone(id)) {
			$.msg.warning("请先选择资源结点");
			return false;
		}
		if (id === "0") {
			$.msg.warning("顶层资源节点不允许操作");
			return false;
		}
		if ($("#type").val() === "navigation" && zNode.isParent && $("#url").val() !== "#") {
			$.msg.warning("类型为菜单的父资源结点的url属性必须是'#'");
			return false;
		}
		if (!$("#updateResourceForm").valid()) {
	        return false;
		}
		var data = $("#updateResourceForm").serializeObject();
		$.postJson("resource/update", data, false, function(response) {
			if (response.status == 1) {
				$.msg.success("操作成功");
				zNode.description = data.description;
				zNode.type = data.type;
				zNode.name = data.name;
				zNode.ico = data.ico;
				zNode.url = data.url;
				zNode.priority = data.priority;
				zNode.permission = data.permission;
				zNode.available = data.available;
				$.fn.zTree.getZTreeObj("zTree").updateNode(zNode);
			} else if (response.status == 0) {
				$.msg.info("操作失败");
			} else if (response.status == -1) {
				$.msg.warning(response.message);
			}
		}, function() {
			$.msg.error("请稍后重试或联系系统管理员解决", "系统异常");
		});
	});
});
</script>
</body>
</html>