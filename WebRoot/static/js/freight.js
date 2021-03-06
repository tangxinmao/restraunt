/**
 * 验证及提示框
 */
function alertWarn(message){
	var htmls = "<div class='layout'>"+
			"<b class='ico_close' onclick='closed()'>关闭</b>"+
			"<div class='main'>"+
				"<div class='bd_p10 clearfix'>"+
                	"<div class='popup_tips_title pl150 mt25 mb20 ml20'><b class='ico fl'></b></div><p class='tac fontSize120 colorOrange mb20'>"+message+"</p></div>"+
				"</div>"+
				"<div class='freightTemplet_btns mt15'>"+
					"<input type='button' value='OK' onclick='closed()' class='btn btn-sm btn-sgGreen'>"+
				"</div>"+
			"</div>"+
		"</div>";
	$(".shade_layout").show();
	$("#open_returnPop").css("display","block");
	$('#open_returnPop').empty().html(htmls);
	pop_center ('500');
}	
/**
* 添加城市
 */
function addCity(){
	var root = $("#rootsite").val();
	var serializeArray=$('#cityWinForm').serializeArray();
for(i in serializeArray){
	if(serializeArray[i].name=='cityId')
		continue;
	var text=$('#cityWinForm input[name="'+serializeArray[i].name+'"]').prev('lebel').children().last().text().replace(':','');
	if(!serializeArray[i].value&&serializeArray[i].value==''){
		alertWarn(text+' can\'t be empty.');
		return ;
	}else{
		var regInt = /^[0-9]*[1-9][0-9]*$/ ; 
		if(serializeArray[i].name=='freight'||serializeArray[i].name=='freightBaseAmount')
		if(!regInt.test(serializeArray[i].value)){
			alertWarn('please input integer about in '+text+'!');
			return;
		}
	}
}
	$.post(root+"/freight/saveCity", serializeArray,
			function(j){
	    if(j.code != undefined && j.code != "1") {
	        alertWarn(j.result)
	        return;
        }
		    var data=JSON.parse(j);
			if(data.code=="1"){
				goThisPage($('#currentPage').val());
				close();
			}
			else{
				alertWarn("Unavailable city can not be the default city.");
			}
			
	});
}
function deleteCity(cityId){
	var root = $("#rootsite").val();
	
	$.post(root+"/freight/deleteFreight", {cityId:cityId},
			function(j){
		var data=JSON.parse(j);
			if(data.code=="1"){
				goThisPage($('#currentPage').val());
				close();
			}
			else{
				alertWarn(data.result);
			}
			
	});
}
/**
 * 修改枚举值弹出层
 * @param prodPropEnumId 枚举值ID
 * @param obj DOM对象
 */
function editCityWin(cityId,obj){
	$('#cityWin .hd h2').text('Edit City');
	$(".shade_layout").show();
	$("#cityWin").show();
	pop_center ('560'); 
	var $td = $(obj).parent().parent().find("td");
	$('#cityWinForm input[name="cityId"]').val(cityId);
	$('#cityWinForm input[name="name"]').val($td.eq(0).text());
	$('#cityWinForm input[name="provinceName"]').val($td.eq(1).text());
	
	 $('#cityWinForm input[name="isDredged"]').each(function(i){
         if( $(this).val()==$td.eq(2).find('input').val())
              $(this).prop("checked","checked");
         else
      	    $(this).removeProp("checked");
   
          });
	 $('#cityWinForm input[name="isHot"]').each(function(i){
         if( $(this).val()==$td.eq(3).find('input').val())
              $(this).prop("checked","checked");
         else
      	    $(this).removeProp("checked");
   
          });
	 $('#cityWinForm input[name="isDefault"]').each(function(i){
         if( $(this).val()==$td.eq(4).find('input').val())
              $(this).prop("checked","checked");
         else
      	    $(this).removeProp("checked");
   
          });

	$('#cityWinForm input[name="freight"]').val(parseInt($td.eq(5).find('input').val()));
	$('#cityWinForm input[name="freightBaseAmount"]').val(parseInt($td.eq(6).find('input').val()));
	
}

/**
	 * 父分类弹出层
	 */
	function addCityWin(){
		$('#cityWinForm')[0].reset();
		$('#cityWinForm input[name="cityId"]').val(null);
		$('#cityWin .hd h2').text('Add City');
		$(".shade_layout").show();
		$("#cityWin").show();
		pop_center ('560'); 
	}
$(function() {
	$( "#rackOnStartTimeStr" ).datepicker({
		showOtherMonths: true,
		//defaultDate: "+1w",
		changeMonth: true,
		dateFormat:"yy-mm-dd",
		showButtonPanel: true,
		numberOfMonths: 1,
//		beforeShow:function(){
//			$( "#rackOnStartTimeStr" ).datepicker( "option", "maxDate", new Date() );
//		},
		onClose: function( selectedDate ) {
			$( "#rackOnEndTimeStr" ).datepicker( "option", "minDate", selectedDate );
		}
	});
	$( "#rackOnEndTimeStr" ).datepicker({
		showOtherMonths: true,
		//defaultDate: "+1w",
		changeMonth: true,
		dateFormat:"yy-mm-dd",
		showButtonPanel: true,
		numberOfMonths: 1,
//		beforeShow:function(){
//			$( "#rackOnEndTimeStr" ).datepicker( "option", "minDate", new Date() );
//		},
		onClose: function( selectedDate ) {
			$( "#rackOnStartTimeStr" ).datepicker( "option", "maxDate", selectedDate );
		}
	});
	
	$( "#rackDownStartTimeStr" ).datepicker({
		showOtherMonths: true,
		//defaultDate: "+1w",
		changeMonth: true,
		dateFormat:"yy-mm-dd",
		showButtonPanel: true,
		numberOfMonths: 1,
//		beforeShow:function(){
//			$( "#rackDownStartTimeStr" ).datepicker( "option", "maxDate", new Date() );
//		},
		onClose: function( selectedDate ) {
			//$( "#rackDownEndTimeStr" ).datepicker( "option", "minDate", selectedDate );
		}
	});
	$( "#rackDownEndTimeStr" ).datepicker({
		showOtherMonths: true,
		//defaultDate: "+1w",
		changeMonth: true,
		dateFormat:"yy-mm-dd",
		showButtonPanel: true,
		numberOfMonths: 1,
//		beforeShow:function(){
//			$( "#rackDownEndTimeStr" ).datepicker( "option", "minDate", new Date() );
//		},2
		onClose: function( selectedDate ) {
			$( "#rackDownStartTimeStr" ).datepicker( "option", "maxDate", selectedDate );
		}
	});
//	$( "#timeupupup" ).datetimepicker({showSecond:true,changeMonth : true,dateFormat:"yy-mm-dd",timeFormat: "HH:mm:ss"});
//	$( "#timedowndowndown" ).datetimepicker({showSecond:true,changeMonth : true,dateFormat:"yy-mm-dd",timeFormat: "HH:mm:ss"});
	$("#updateStorage").click(function(){
		$("#skuStorageTbody").find("tr").each(function(){
		})
	});
	$("#prodStatus").val($("#d_prod_status").val());
	
});
function clearTime(id){
	$("input[id="+id+"]").val("");
}
var goThisPage=function(ta) {
	var currentPage = '';
	if(ta == 'search'){
		currentPage = 1;
	}else if(ta == '+'){//下一页
		//当前页
		currentPage = parseInt($('#currentPage').val()) + 1;
	}else if(ta == '-'){
		currentPage = parseInt($('#currentPage').val()) - 1;
	}else if(ta == '='){
		currentPage = $("#page_currentPage").val();
		var reg =/^\d+$/;
		if(!currentPage.match(reg) || parseInt(currentPage) <= 0 ){
			$("#pageNm_tip").text('Please input Positive integer!');
			return;
		}
		var pageSize = $("#page_pageSize").val();
		var totalRow = $("#page_totalRows").val();
		var totalPage = Math.ceil(parseInt(totalRow)/parseInt(pageSize));
		if (parseInt(currentPage) > totalPage) {
			$("#pageNm_tip").text('The total page is :' + totalPage);
			return;
		}
	}else if(ta == 'status0'){
		currentPage = 1;
		$("#isDredge").find("option[value='']").prop("selected",true);
	}else if(ta == 'status1'){
		currentPage = 1;
		$("#isDredge").find("option[value='1']").prop("selected",true);
	}else if(ta == 'status2'){
		currentPage = 1;
		$("#isDredge").find("option[value='0']").prop("selected",true);
	}else if(ta == 'sort'){
		var isDesc = "DREDGE_TIME desc";
		currentPage = 1;
		if($("#isDesc").prop("checked")){
			isDesc = "DREDGE_TIME asc";
		}
		else{
			isDesc = "DREDGE_TIME desc";
		}
	}
	else{
		currentPage = ta;
	}
	//搜索条件
	//商品名称
	var cityName = $("#cityName").val();
	//商品编号
	var isDredge = $("#isDredge").val();
	//商品状态
	var isHot = $("#isHot").val();
	var provinceName=$('#provinceName').val();
	
	
$.post(getRootPath()+"/freight/freight",{"currentPage":currentPage,
	name:cityName,
	isDredged:isDredge,
	isHot:isHot,
	orderBy:isDesc,
	provinceName:provinceName
	},
	function(data){ 
		$("#productListDiv").empty().html(data);
		$(".sortArea ul").find("li").each(function(){
			$(this).removeClass("selcted");
		});
		if(isDredge==""){
			$(".sortArea ul li").eq(0).addClass("selcted");
		}
		else if(isDredge==1){
			$(".sortArea ul li").eq(1).addClass("selcted");
		}
		else if(isDredge==0){
			$(".sortArea ul li").eq(2).addClass("selcted");
		}
		
		if(isDesc=="DREDGE_TIME asc"){
			$("#isDesc").prop("checked",false);
			$(".dot-bottom").addClass("checked");
			$(".dot-top").removeClass("checked");
		}
		else{
			$("#isDesc").prop("checked",true);
			
			$(".dot-top").addClass("checked");
			$(".dot-bottom").removeClass("checked");
			
		}
	/*	var status = $("#status_hide").val();
		$("select option").each(function (){  
			if($(this).val()==status){   
				$(this).attr("selected",true); 
		}}); */
	});
};

function getRootPath(){
	return $('#rootPath').val();
}

/**
 * 查看SKU库存
 * @param prodId
 */
function searchSkuStorage(prodId) {
	$.ajax({
		url : getRootPath() + "/product/search_sku_storage?prodId=" + prodId+ '&time='+ new Date().getTime(),
		type : "GET",
		dataType : "json",
		success : function(result) {
			var jsonArr = eval(result);
			var returnInfo = "";
			for(var i = 0; i < jsonArr.length; i++) {
				var info = "<tr><td width='152'>" + jsonArr[i].PROD_ID + "</td><td width='149'>" + jsonArr[i].PROD_PROP_VAL + "</td><td width='205'>" + jsonArr[i].PROD_STORAGE + "</td><td style='padding-left: 30px;'>" + jsonArr[i].SKU_PRICE + "</td></tr>";
				returnInfo += info;
			}
			
			$("#skuStorageTbody").html(returnInfo);
			$("#skuStorageDiv1").css("display","block");
			$(".shade_layout").show();
			pop_center('790');
		}
	});
}

/**
* 查看商品库存
* @param prodId
*/
function searchProdStorage(prodId) {
	$.ajax({
		url : getRootPath() + "/product/search_prod_storage?prodId=" + prodId+ '&time='+ new Date().getTime(),
		type : "GET",
		dataType : "json",
		success : function(result) {
			var jsonArr = eval(result);
			var returnInfo = "";
			for(var i = 0; i < jsonArr.length; i++) {
				var info = "<tr><td width='182'>" + jsonArr[i].PROD_ID + "</td><td width='119'>" + jsonArr[i].CITY_NAME + "</td><td width='115'>" + jsonArr[i].PROD_STORAGE + "</td><td width='105'>" + jsonArr[i].PROD_NAME + "</td></tr>";
				returnInfo += info;
			}
			
			$("#skuStorageTbody4").html(returnInfo);
			$("#skuStorageDiv4").css("display","block");
			$(".shade_layout").show();
			pop_center('790');
		}
	});
}


/**
 * 修改SKU库存
 * @param prodId
 */
function updateSkuStorage(prodId) {
	$.ajax({
		url : getRootPath() + "/product/search_sku_storage?prodId=" + prodId + '&time='+ new Date().getTime(), 
		type : "GET",
		dataType : "json",
		success : function(result) {
			var jsonArr = eval(result);
			var returnInfo = "";
			for(var i = 0; i < jsonArr.length; i++) {
				var info = "<tr><td width='152'>" + jsonArr[i].PROD_ID + "</td>" + jsonArr[i].CITY_NAME + "</td><td width='149'>" + jsonArr[i].PROD_PROP_VAL + "</td><td width='205'><div class='amount_wrap'><a class='minus_ico' id='reduce' href='javascript:void(0)' onclick='reduceNum(this)'>-</a><input type='text' maxlength='8' class='width40' name='prodStorage' id='prodStorage' value='" + jsonArr[i].PROD_STORAGE + "'><a class='plus_ico'  id='increase' href='javascript:void(0)'  onclick='increaseNum(this)'>+</a></div></td><td style='padding-left: 30px;'>" + jsonArr[i].SKU_PRICE + "</td></tr>";
				returnInfo += info;
			}
			
			$("#skuStorageTbody2").html(returnInfo);
			$("#skuStorageDiv2").show();
			$(".shade_layout").show();
			pop_center('790');
		}
	});
}
/**
 * 修改商品库存
 */
function updateProdStorage(prodId){
	$.ajax({
		url : getRootPath() + "/product/search_prod_storage?prodId=" + prodId+ '&time='+ new Date().getTime(),
		type : "GET",
		dataType : "json",
		success : function(result) {
			var jsonArr = eval(result);
			var returnInfo = "";
			for(var i = 0; i < jsonArr.length; i++) {
				var info = "<tr><td width='182'>" + jsonArr[i].PROD_ID + "</td><td width='119'><input type='hidden' name='cityId' value='"+jsonArr[i].ADDRESS_ID+"'>" + jsonArr[i].CITY_NAME + "</td><td width='115'><div class='amount_wrap'><a class='minus_ico' id='reduce' href='javascript:void(0)' onclick='reduceNum(this)'>-</a><input type='text' maxlength='8' class='width40' name='prodStorage' id='prodStorage' value='" + jsonArr[i].PROD_STORAGE + "'><a class='plus_ico'  id='increase' href='javascript:void(0)'  onclick='increaseNum(this)'>+</a></div></td><td width='105'>" + jsonArr[i].PROD_NAME + "</td></tr>";
				returnInfo += info;
			}
			
			$("#skuStorageTbody3").html(returnInfo);
			$("#skuStorageDiv3").css("display","block");
			$(".shade_layout").show();
			pop_center('790');
		}
	});
}
function reduceNum(e){
	//$("#prodStorage").attr("value",parseInt(value)-1);
	var value = $(e).parent().children("input").val();
	if(parseInt(value)<=0){
		alertWarn("库存不能为负数！");
		return 0;
	}
	 $(e).parent().children("input").attr("value",parseInt(value)-1);
	
}
function increaseNum(e){
	var value = $(e).parent().children("input").val();
	 $(e).parent().children("input").attr("value",parseInt(value)+1);
}

function beginUpdateSkuStorage(Obj) {
	var jsondrr = new Array();
	var i=0;
	$("#skuStorageTbody2").find("tr").each(function(){
		jsondrr[i] = $(this).find("td").eq(0).text()+",,,"+$(this).find("td").eq(1).find("input[name='cityId']").val()+",,,"+$(this).find("td").find("div input[name=prodStorage]").val();
		i++;
	});
	$.ajax({
			type : "POST",
		//contentType : 'charset=utf-8',
		url : "update_sku_storage",
		dataType : 'json',
		cache : false,
		data : {
			json : JSON.stringify(jsondrr),
		},
		success : function(data) {
			var jsonObjArr = JSON.parse(data);
			if(jsonObjArr.code=="1"){
				goThisPage("1");
				closeStorageDiv();
			}
			else{
				alertWarn("修改失败，请联系系统管理员！");
			}
		}
	});
	
}
/**
 * 修改商品库存
 * @param Obj
 */
function beginUpdateProdStorage(Obj) {
	var jsondrr = new Array();
	var i=0;
	$("#skuStorageTbody3").find("tr").each(function(){
		jsondrr[i] = $(this).find("td").eq(0).text()+",,,"+$(this).find("td").eq(1).find("input[name='cityId']").val()+",,,"+$(this).find("td").find("div input[name=prodStorage]").val();
		i++;
	});
	$.ajax({
			type : "POST",
		//contentType : 'charset=utf-8',
		url : "update_sku_storage",
		dataType : 'json',
		cache : false,
		data : {
			json : JSON.stringify(jsondrr),
		},
		success : function(data) {
			var jsonObjArr = JSON.parse(data);
			if(jsonObjArr.code=="1"){
				goThisPage($("#currentPage").val());
				closeStorageDiv();
			}
			else{
				alertWarn("修改失败，请联系系统管理员！");
			}
		}
	});
	
}

/**
 * 关闭查看库存DIV
 */
function closeStorageDiv() {
	$(".shade_layout").hide();
	$(".pop_windows").hide();
}
//去左空格;   
function ltrim(s){     
   return s.replace(/(^\s*)/, "");  
}   
//去右空格;   
function rtrim(s){   
 return s.replace(/(\s*$)/, "");  
}   
//去左右空格;   
function trim(s){  
  //s.replace(/(^\s*)|(\s*$)/, "");  
 return rtrim(ltrim(s));   

}

/**
 * 上架商品
 * @param prodId
 */
function begiputUpGoods(prodId,opt){
	var currentPage = $("#currentPage").val();
	var url = getRootPath()+"/product/prod_put_up_down?prodId="+prodId+"&action=up&currentPage="+currentPage;
	$.post(url,{},function(data){
		if(data=="-1"){
			
		}
		if(opt=="putUpPublish"){
			self.location.href = getRootPath()+"/product/init";
		}
		$("#productListDiv").empty().html(data);
		var status = $("#status_hide").val();
		$("select option").each(function (){  
			if($(this).val()==status){   
				$(this).attr("selected",true); 
		}});
		closed();
	});
}
function begiputUpDownGoods(Obj){
	var prodId = $(Obj).attr("myvalue");
	var currentPage = $("#currentPage").val();
	
	var putawayTime = $("#timeupupup").val();
	var putdownTime = $("#timedowndowndown").val();
	var startdate=new Date(putawayTime.replace(/-/g,"/"));
    var enddate=new Date(putdownTime.replace(/-/g,"/"));             
   // var date = new Date(); 
       
       if(startdate>enddate)
       {
    	   $(".shade_layout").hide();
   		   $("#putgoodsup").hide();
    	   prompt("商品下架时间不能在上架时间前！");
           return false;
       }
	var url = getRootPath()+"/product/prod_put_up_down?prodId="+prodId+"&action=up&currentPage="+currentPage+"&putawayTime="+putawayTime
				+"&putdownTime="+putdownTime;
	$.post(url,{},function(data){
		$("#productListDiv").empty().html(data);
		var status = $("#status_hide").val();
		$("select option").each(function (){  
			if($(this).val()==status){   
				$(this).attr("selected",true); 
		}});
		$(".shade_layout").hide();
		$("#putgoodsup").hide();
	});
	
}
//定时上下架
function putUpDownGoodsByTime(prodId){
	$(".shade_layout").show();
	$("#putgoodsup").show();
	pop_center ('560');
	$("#upupsubmit").attr("myvalue",prodId);
}
function putUpGoods(prodId,operation){
	tShowCanclePop(prodId,operation);
	
//	var url=getRootPath()+"/product/checktempate/"+prodId;
//	$.post(url,{},function(data){
//		var jsonObjArr = JSON.parse(data);
//		if(jsonObjArr.code=="1"){
//			checkstore(prodId,operaction);
//		}
//		else{
//			prompt("商品无运费模板，请添加运费模板后执行上架操作！");
//		}
//	});
}
function beginputDownGoods(prodId){
	var currentPage = $("#currentPage").val();
	var url = getRootPath()+"/product/prod_put_up_down?prodId="+prodId+"&action=down&currentPage="+currentPage;
	$.post(url,{},function(data){
		$("#productListDiv").empty().html(data);
		var status = $("#status_hide").val();
		$("select option").each(function (){  
			if($(this).val()==status){   
				$(this).attr("selected",true); 
		}});
		closed();
	});
}
function putDownGoods(prodId,operaction){
	$(".shade_layout").show();
	$("#putgoodsdown").show();
	pop_center ('560');
	$("#downdownsubmit").attr("myvalue",prodId);
}
function checkstore(prodid,operation){
	$.post(getRootPath()+"/product/checkstorestatus",{"prodId":prodid},function(data){
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="-1"){
			closed();
			if(operation=="updategoods"){
				prompt("当前店铺状态不允许修改商品！");
			}
			else {
				prompt("当前店铺状态不允许上架商品！");
			}
			return 0;
		}
		else{
			tShowCanclePop(prodid,operation);
		}
	});
}
function deleteProd(prodId){
	self.location=getRootPath()+"/product/deleteProd?prodId="+prodId;
}
//修改、删除、上下架商品确认弹出框
function tShowCanclePop(prodId,operaction){
	$(".shade_layout").show();
	$("#open_returnPop").show();
	var opt="";
	var url="";
	if(operaction=="updategoods"){
		opt="Are you sure to edit product?";
		$("#formprodId").attr("value",prodId);
		url="javascript:$('#updategoods').submit();";
	}
	if(operaction=="deleteProd"){
		opt="Are you sure to delete product?";
		url="javascript:self.location='"+getRootPath()+"/product/"+"deleteProd?prodId="+prodId+"'";
	}
	if(operaction=="putUp"){
		opt="Are you sure to put on the product?";
		url="begiputUpGoods('"+prodId+"')";
	}
	if(operaction=="putUpPublish"){
		opt="是否确认上架商品";
		url="begiputUpGoods('"+prodId+"','putUpPublish')";
	}
	if(operaction=="putDown"){
		opt="Are you sure set the product to out of stock?";
		url="beginputDownGoods('"+prodId+"')";
	}
	var htmls = "<div class='layout'>"+
	"<b class='ico_close' onclick='closed()'>关闭</b>"+
	"<div class='main'>"+
		"<div class='bd_p10 clearfix'>"+
        	"<div class='popup_tips_title pl150 mt25 mb20 ml20'>"+
        	"<b class='ico fl'></b><span class='fl ml15'>Prompt</span></div>"+
            "<p class='tac fontSize120 colorOrange mb20'>"+opt+"</p>"+
		"</div>"+
		"<div class='freightTemplet_btns mt15'>"+
			"<input type='button' value='OK' onclick=\""+url+"\"  class='btn btn-sm btn-sgGreen'>"+
			"<input type='button' value='Cancel' class='btn btn-sm btn-grey' onclick='closed()'>"+
		"</div>"+
	"</div>"+
"</div>";
//	$("#merchantPopIframe").removeClass("height");
	$('#open_returnPop').empty().html(htmls);
//	var height = $("#merchantPopIframe").height();
//	$("#merchantPopIframe").css({height:height});
	pop_center ('560');
}
/**
 * 验证及提示框
 */
function prompt(message){
	var htmls = "<div class='layout'>"+
			"<b class='ico_close' onclick='closed()'>关闭</b>"+
			"<div class='main'>"+
				"<div class='bd_p10 clearfix'>"+
                	"<div class='popup_tips_title pl150 mt25 mb20 ml20'><b class='ico fl'></b><span class='fl ml15'>Prompt</span></div><p class='tac fontSize120 colorOrange mb20'>"+message+"</p></div>"+
				"</div>"+
				"<div class='freightTemplet_btns mt15'>"+
					"<input type='button' value='OK' onclick='closed()' class='btn btn-sm btn-sgGreen'>"+
				"</div>"+
			"</div>"+
		"</div>";
	$(".shade_layout").show();
	$("#open_returnPop").css("display","block");
	$('#open_returnPop').empty().html(htmls);
	pop_center ('500');
}
function closed(){
	$("#open_returnPop").hide();
	$(".shade_layout").hide();
}

/**
 * 关联商品函数
 */
function relateProducts(prodId) {
	var url = getRootPath() + "/product/prod_relate_prod?prodId=" + prodId;
	window.location.href = url;
}

$(document).delegate(".sortDiv","click",function(){  
});

