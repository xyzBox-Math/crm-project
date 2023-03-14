<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<!--  PAGINATION plugin -->
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.min.js"></script>
<script type="text/javascript">

	$(function(){
		$("#createActivityBtn").click(function(){
			//初始化清空数据
			$("#createForm")[0].reset();
			//打开模态窗口增加
			$("#createActivityModal").modal("show");

		});
		//日历显示插件
		$(".dateSheet").datetimepicker({
			language:'zh-CN',
			format:'yyyy-mm-dd',
			minView:'month',
			initialDate:new Date(),
			autoclose:true,
			todayBtn:true,
			clearBtn:true
		});
		$("#close").click(function(){
			//初始化清空数据(清空表单)
			$("#createForm")[0].reset();
			//关闭模态窗口增加
			$("#createActivityModal").modal("hide");
		});
		$("#save").click(function(){

			var owner=$("#create-marketActivityOwner").val();
			var name=$.trim($("#create-marketActivityName").val());
			var startDate=$("#create-startTime").val();
			var endDate=$("#create-endTime").val();
			var cost=$.trim($("#create-cost").val());
			var remark=$("#create-describe").val();

			//表单验证
			if(owner==""||name==""){
				alert("所有者或名称不能为空");
				return;
			}
			if(startDate!=""&&endDate!=""){
				//alert("");
				if(startDate>endDate){
					alert("开始日期不能大于结束日期");
					return;
				}
			}
			var regExp=/^(([1-9]\d*)|0)$/;
			if(cost!="") {
				if (!regExp.test(cost)) {
					alert("成本只能是非负整数");
					return;
				}
			}
			$.ajax({
				url:"workbench/activity/saveCreateActivity.do",
				data:{
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					remark:remark
				},
				type:'post',
				dataType:'json',
				success:function(data){
					//添加成功关闭模态窗口
					if(data.code=="1"){
						$("#createActivityModal").modal("hide");
						queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination("getOption","rowsPerPage"));
					}else{
						alert(data.message);
						$("#createActivityModal").modal("show");
					}
				}
			});
		});
		//首次启动加载数据
		queryActivityByConditionForPage(1,10);
		$("#selectActivity").click(function(){
			queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination("getOption","rowsPerPage"));
		});

		//全选和反选
		$("#checkAll").click(function(){
			$("#listShow input[type='checkbox']").prop("checked",this.checked);
		});

		//所有选中时，全选也选中
		/*$("#listShow input[type='checkbox']").click(function(){
			if($("#listShow input[type='checkbox']").size()==$("#listShow input[type='checkbox']:checked").size()){
				$("#checkAll").prop("checked",true);
			}else{
				$("#checkAll").prop("checked",false);
			}
		});*/
		$("#listShow").on("click","input",function(){
			if($("#listShow input[type='checkbox']").size()==$("#listShow input[type='checkbox']:checked").size()){
				$("#checkAll").prop("checked",true);
			}else{
				$("#checkAll").prop("checked",false);
			}
		});
		//删除活动
		$("#deleteActivity").click(function(){
			//收集参数
			//表单验证
			if($("#listShow input[type='checkbox']:checked").size()==0){
				alert("请至少选择一项市场活动");
				return;
			}
			var ids="";
			$.each($("#listShow input[type='checkbox']:checked"),function(){
				ids+=this.value+",";
			});
			ids=ids.substring(0,ids.length-1);
			//alert(ids);
			if(window.confirm("确定要删除吗")){
				$.ajax({
					url:"workbench/activity/deleteActivityByIds.do",
					data:{
						ids:ids
					},
					type:'post',
					dataType:'json',
					success:function(data){
						if(data.code=="1"){
							queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination("getOption","rowsPerPage"));
						}else{
							alert(data.message);
						}
					}
				});
			}
		});
		//修改活动:修改活动模态窗口数据展示
		$("#updateActivityBtn").click(function (){
			var checked=$("#listShow input[type='checkbox']:checked");
			if(checked.size()==0){
				alert("至少选中一条记录");
				return;
			}
			if(checked.size()>1){
				alert("一次只能修改一条记录");
				return;
			}
			var id=$.trim(checked[0].value);
			//发送请求
			$.ajax({
				url:"workbench/activity/queryActivityById.do",
				data:{
					id:id
				},
				type:'post',
				dataType:'json',
				success:function(data){
					//把查询出来的id给隐藏域便于修改时拿到id
					$("#updateById").val(data.id);
					//显示信息
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-describe").val(data.description);
					//弹出模态窗口
					$("#editActivityModal").modal("show");

				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					alert(XMLHttpRequest.readyState + XMLHttpRequest.status + XMLHttpRequest.responseText);
				}
			});
		});
		//修改保存
		$("#confirmUpdate").click(function(){
			//收集参数
			var id=$("#updateById").val();
			var owner=$("#edit-marketActivityOwner").val();
			var name=$("#edit-marketActivityName").val();
			var startDate=$("#edit-startTime").val();
			var endDate=$("#edit-endTime").val();
			var cost=$("#edit-cost").val();
			var description=$("#edit-describe").val();
			//表单验证
			if(owner==""||name==""){
				alert("所有者或名称不能为空");
				return;
			}
			if(startDate!=""&&endDate!=""){
				//alert("");
				if(startDate>endDate){
					alert("开始日期不能大于结束日期");
					return;
				}
			}
			var regExp=/^(([1-9]\d*)|0)$/;
			if(cost!="") {
				if (!regExp.test(cost)) {
					alert("成本只能是非负整数");
					return;
				}
			}
			//发起请求
			$.ajax({
				url:"workbench/activity/saveUpDateActivity.do",
				data:{
					id:id,
					name:name,
					owner:owner,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				type:'post',
				dataType:'json',
				success:function(data){
					if(data.code=="1"){
						queryActivityByConditionForPage($("#demo_pag1").bs_pagination("getOption","currentPage"),$("#demo_pag1").bs_pagination("getOption","rowsPerPage"));
						$("#editActivityModal").modal("hide");
					}else{
						alert(data.message);
						$("#editActivityModal").modal("show");
					}
				}
			});
		});
		//关闭修改模态窗口
		$("#closeActivityUpdate").click(function(){
			$("#editActivityModal").modal("hide");
		});
		//批量导出市场活动
		$("#exportActivityAllBtn").click(function(){
			window.location.href="workbench/activity/queryAllActivitiesToExcel.do";
		});
		//选择导出
		$("#exportActivityXzBtn").click(function(){
			//表单验证
			if($("#listShow input[type='checkbox']:checked").size()==0){
				alert("请至少选择一项市场活动");
				return;
			}
			var ids="";
			$.each($("#listShow input[type='checkbox']:checked"),function(){
				ids+=this.value+",";
			});
			ids=ids.substring(0,ids.length-1);
			window.location.href="workbench/activity/querySomeActivities.do?ids="+ids;

		});
	});

	//展示页面数据的函数
	function queryActivityByConditionForPage(beginNo,pageSize){
		//收集参数
		var name=$("#name").val();
		var owner=$("#owner").val();
		var startDate=$("#startTime").val();
		var endDate=$("#endTime").val();

		//发起请求
		$.ajax({
			url:"workbench/activity/queryActivity.do",
			data:{
				name:name,
				owner:owner,
				startDate:startDate,
				endDate:endDate,
				pageNo:beginNo,
				pageSize:pageSize
			},
			type:'post',
			dataType:'json',
			success:function(data){
				var htmlStr="";
				$.each(data.listActivity,function(index,obj){
					htmlStr+="<tr class=\"active\">";
					htmlStr+="<td><input type=\"checkbox\" value=\""+$.trim(obj.id)+" \"/></td>";
					htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='detail.html';\">"+obj.name+"</a></td>";
					htmlStr+="<td>"+obj.owner+"</td>";
					htmlStr+="<td>"+obj.startDate+"</td>";
					htmlStr+="<td>"+obj.endDate+"</td>";
					htmlStr+="</tr>";
				});
				$("#checkAll").prop("checked",false);
				$("#listShow").html(htmlStr);
				//分页
				//计算总页数
				var totalPages=1;
				if(data.totalRows%pageSize==0){
					totalPages=data.totalRows/pageSize;
				}else{
					totalPages=parseInt(String(data.totalRows/pageSize))+1;
				}
				$("#demo_pag1").bs_pagination({
					currentPage:beginNo,//当前页号
					rowsPerPage:pageSize,//每页显示条数
					totalPages: totalPages,//总页数，必填
					visiblePageLinks: 5,//最大显示卡片数
					showGoToPage: true,//是否显示”跳转到部分“，默认显示
					showRowsPerPage: true,//是否显示”每页显示条数“，默认显示
					showRowsInfo: true,//是否显示记录的信息，默认显示
					//每次切换页号自动触发
					//返回新的页号和显示条数
					//切换页后自动发起请求
					onChangePage:function(event,pagObj){
						queryActivityByConditionForPage(pagObj.currentPage,pagObj.rowsPerPage);
					}
				});

			}
		});
	}
</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="createForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								  <c:forEach items="${requestScope.userList}" var="u">
									  <option value="${u.id}">${u.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control dateSheet" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control dateSheet" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" id="close">关闭</button>
					<button type="button" class="btn btn-primary" id="save">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="updateById">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${requestScope.userList}" var="u">
										<option value="${u.id}">${u.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control dateSheet" id="edit-startTime" value="2020-10-10">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control dateSheet" id="edit-endTime" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"  id="closeActivityUpdate">关闭</button>
					<button type="button" class="btn btn-primary" id="confirmUpdate">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon" >开始日期</div>
					  <input class="form-control dateSheet" type="text" id="startTime" readonly/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon" >结束日期</div>
					  <input class="form-control dateSheet" type="text" id="endTime" readonly/>
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="selectActivity">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="updateActivityBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivity"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="listShow">
					<!--
						<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        -->
					</tbody>
				</table>
				<div id="demo_pag1"></div>
			</div>


		</div>
		
	</div>
</body>
</html>