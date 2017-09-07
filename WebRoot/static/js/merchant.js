/**
 * 上传商户logo
 */
function uploadCover(){
	var root = $("#rootsite").val();
	$.ajaxFileUpload
	(
		{
			url:root+'/manager/uploadcover',
			secureuri:false,
			fileElementId:'cover',
			dataType: 'json',
			data:{"merchantId":$("#merchantId").val()},
			success: function (data, status)
			{
				if( data.success )
				{
					$("#photocover").prop("src",data.imgUrl);
					$("#fileId").attr("value",data.fileid);
					$("#photocover").show();
				} else {
					alertWarn("333");
//					alertWarn(data.errorMsg);
					closed_add_shade_layout();
					alertWarnWarn( data.errorMsg != '' ? data.errorMsg : '上传图片出错,请联系管理员');
				}
				createColverUploadHtml();
			},
			error: function (data, status, e)
			{
				alertWarn("444");
			}
		}
	);
}
/**
 * 重新绑定onchange事件
 */
function createColverUploadHtml(){
	var $new = $('<input t="'+ new Date().getTime() +'" size="1" type="file" name="cover" id="cover" value="上传图片" style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 0; left: 240px; top: 28px; width: 63px;height: 29px;" />');
	$('#cover').replaceWith( $new );
	$new.change(uploadCover);
}
function next(){
	var merchantName = $("#merchantName").val();
	var fileId = $("#fileId").val();
	var merchantDesc = $("#merchantDesc").val();
	var mobile = $("#mobile").val();
	var taxRate = $("#taxRate").val();
	var serviceCharge = $("#serviceCharge").val();
	var regInt = /^[0-9]+[0-9]*]*$/; //正数
    var check= /^\d+(\.{0,1}\d+){0,1}$/;  //非负数
	if(merchantName==""){
		alertWarn("please input the value about Restaurant Name!");
		$("#merchantName").focus();
		return 0;
	}
	if(taxRate == ""){
		alertWarn("please input the value about Tax Rate!");
		$("#taxRate").focus();
		return 0;
	}
	if(!check.test(taxRate)){
		alertWarn("Tax Rate Must be a nonnegative number!");
		$("#taxRate").focus();
		return 0;
	}
	if(parseInt(taxRate)>100){
		alertWarn("Tax Rate Must less than 100!");
		$("#taxRate").focus();
		return 0;
	}
	/*if(parseInt(taxRate)==0){
		alertWarn("Tax Rate Must more than 0!");
		$("#taxRate").focus();
		return 0;
	}*/
	
	if(serviceCharge == ""){
		alertWarn("please input the value about Service Charge!");
		$("#serviceCharge").focus();
		return 0;
	}
	if(!check.test(serviceCharge)){
		alertWarn("Service Charge Must be a nonnegative number!");
		$("#serviceCharge").focus();
		return 0;
	}
	if(parseInt(serviceCharge)>100){
		alertWarn("Service Charge Must less than 100!");
		$("#serviceCharge").focus();
		return 0;
	}
/*	if(parseInt(serviceCharge)==0){
		alertWarn("Service Charge Must more than 0!");
		$("#serviceCharge").focus();
		return 0;
	}*/
	if(fileId==""){
		alertWarn("please upload the logo file!");
		
		return 0;
	}
/*	if(merchantDesc==""){
		alertWarn("please input the value about Description!");
		$("#merchantDesc").focus();
		return 0;
	}*/
	if(mobile==""){
		alertWarn("please input the value about Telephone!");
		$("#mobile").focus();
		return 0;
	}
	$("#screen1").hide();
	$("#screen2").show();
	$("#backSt").show();
	$("#sTit").text("Add Restaurant > Step2");
	var html = '<p><label class="colortitle">Role Seller:</label>Restaurant Seller</p>'
			 + '<p><label class="colortitle"><span class="colorRed">*</span>Username:</label><input class="text" type="text" style="width:400px" name="account" id="account" value=""></p>'
		     + '<p><label class="colortitle"><span class="colorRed">*</span>Password:</label><input class="text" type="password" style="width:400px" name="password" id="password" value="" autocomplete="new-password"></p>'
		     + '<p><label class="colortitle"><span class="colorRed">*</span>Email:</label><input class="text" type="text" style="width:400px" name="email" id="email" value=""></p>'
		     + '<p><label class="colortitle"><span class="colorRed" hidden>*</span>Description:</label><textarea class="text" id="memberDesc" style="width: 410px; height: 180px; margin-top:10px;"></textarea></p>'
		     + '<div><button class="btnG bwt100 bht30 bfz16" style="margin: 20px 0 20px 291px;" onclick="save()">Save</button></div>';
	$("#s2d").html(html);
}
function backStep(){
	$("#screen1").show();
	$("#screen2").hide();
	$("#backSt").hide();
	$("#sTit").text("Add Restaurant > Step1");
	$("#s2d").html("");
}
/**
 * 保存商户资料
 */
function save(){
	var root = $("#rootsite").val();
	var merchantId = $("#merchantId").val();
	var merchantName = $("#merchantName").val();
	var fileId = $("#fileId").val();
	var taxRate = $("#taxRate").val();
	var serviceCharge = $("#serviceCharge").val();
	var logoUrl = $("#photocover").prop("src");
	var merchantDesc = $("#merchantDesc").val();
	var mobile = $("#mobile").val();
	var vendorId = $("#vendor").val();
	var cityId = $("#city").val();
	var contact = $("#contact").val();
	var address = $("#address").val();
	var mealStyle = $("#mealStyle").val();

	var bankName = $("#bankName").val();
	var bankAccount = $("#bankAccount").val();
	var bankAccountName = $("#bankAccountName").val();
	
	var account = $("#account").val();
	var password = $("#password").val();
	var email = $("#email").val();
	var memberDesc = $("#memberDesc").val();
	
	if(account==""){
		alertWarn("please input the value about Username!");
		return 0;
	}
	if(password==""){
		alertWarn("please input the value about password!");
		return 0;
	}
	if(email==""){
		alertWarn("please input the value about email!");
		return 0;
	}
    var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(!myreg.test(email)){
        alertWarn('The email fomat is incorrect!');
        return 0;
    }
	
	var url = root + "/manager/savemerchant";
	$.ajax({
		type : "POST",
	//contentType : 'charset=utf-8',
	url : url,
	dataType : 'json',
	cache : false,
	data : {
		"merchantId": merchantId,
		"merchantName": merchantName,
		"fileId":fileId,
		"merchantDesc":merchantDesc,
		"mobile":mobile,
		"account":account,
		"password":password,
		"email":email,
		"vendorId":vendorId,
		"cityId":cityId,
		"contactPerson":contact,
		"address":address,
		"bankName":bankName,
		"bankAccount":bankAccount,
		"bankAccountName":bankAccountName,
		"memberDesc":memberDesc,
		"logoUrl":logoUrl,
		"taxRate":taxRate,
		"serviceCharge":serviceCharge,
		"mealStyle":mealStyle
		
	},
	success : function(data) {
        if(data.code != undefined && data.code != "1") {
            alertWarn(data.result);
            return;
        }
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="1"){
			$("#screen1").hide();
			$("#screen2").hide();
			$("#screen3").show();
		}
		else if(jsonObjArr.code=="2"){
			alertWarn("Fail,The account is existed!");
		}
		else if(jsonObjArr.code=="3"){
			alertWarn("Fail,The Email is existed!");
		}
		else{
			alertWarn("Fail,please contact the administrator");
		}
	}
});
}

function Continue1(){
	window.location.reload();
}
/**
 * 弹出商户资料窗口
 * @param merchantId 商户编号
 */
function showModifyPopupWin(merchantId,obj){
	$(".shade_layout").show();
	$("#popwin").show();
	$("#merchantId").val(merchantId);
	//$("#popup_btn_con").attr("myvalue",merchantId);
	var $td = $(obj).parent().parent().find("td");
	$("#merchantName").val($td.eq(1).text());
	$("#mobile").val($.trim($td.eq(3).text()));
	$("#merchantDesc").val($td.eq(4).find("input[name='mdesc']").val());
	$("#photocover").prop("src",$td.eq(4).find("input[name='logoUrl']").val());
	$("#fileId").val($td.eq(4).find("input[name='logoId']").val());
	var vendor = $td.eq(5).find("input[name='vendorId']").val();
	$("#vendor").val(vendor);
	var province = $td.eq(6).find("input[name='province']").val();
	$("#province option[value='"+province+"']").attr("selected","true");
	var root = $("#rootsite").val();
	var provinceName = $td.eq(6).find("input[name='province']").val();
	$("#province").find("option[value='"+provinceName+"']").prop("selected","true");
	var taxRate = parseInt($td.eq(0).find("input[name='taxRate']").val());
	$("#taxRate").val(taxRate);
	var serviceCharge = parseInt($td.eq(0).find("input[name='serviceCharge']").val());
	$("#serviceCharge").val(serviceCharge);
	var mealStyle = $td.eq(0).find("input[name='mealStyle']").val();
	$("#mealStyle").find("option[value='"+mealStyle+"']").prop("selected",true);
	$.post(root+"/manager/changeprovince", {"provinceName":provinceName},
		function(data){
			$("#city").html("");
			var jsonObjArr = JSON.parse(data);
		    for(var i in jsonObjArr){
		    	 $("#city").append("<option value='"+jsonObjArr[i].cityId+"'>"+jsonObjArr[i].cityName+"</option>");  
		    }
		    var city = $td.eq(6).find("input[name='cityId']").val();
			$("#city option[value='"+city+"']").attr("selected","true");
			var address = $td.eq(6).find("input[name='myaddress']").val();
			$("#address").val($.trim(address));
			$.trim($("#contact").val($td.eq(2).text()));
			pop_center ('560');
	    }
	);
}

function showAccountPopupWin(merchantId,obj){
	var root = $("#rootsite").val();
	$(".shade_layout").show();
	$("#accountpopwin").show();
	$("#merchantId").val(merchantId);
	$.post(root+"/manager/checkmemberinfo", {"merchantId":merchantId},
		function(data){
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="1"){
			//$("#memberAccount").attr("readonly","true");
			$("#memberAccount").val(jsonObjArr.account);
			$("#accountSpan").text(jsonObjArr.account);
			$("#memberPwd").val("");
			$("#upemail").val(jsonObjArr.email);
			$("#memberDesc").val(jsonObjArr.memberDesc);
		}
//		else{
//			$("#memberAccount").removeAttr("readonly");
//			$("#memberAccount").val("");
//			$("#memberPwd").val("");
//			$("#email").val("");
//		}
	});
	pop_center ('560',480);
}
function showQRCodePopupWin(merchantId,obj){
	var root = $("#rootsite").val();
	$(".shade_layout").show();
	$("#qrcodePopup").show();
	$("#merchantId").val(merchantId);
	pop_center ('560');
}
function processPrompt(message){
	var htmls = "<div class='layout'>"+
			""+
			"<div class='main'>"+
				"<div class='bd_p10 clearfix'>"+
                	"<div class='popup_tips_title pl150 mt25 mb20 ml20'><span class='fl ml15'><img src='"+siteurl+"/static/images/ico/loading.gif'>"+message+"！</span></div>"+
				"</div>"+
			"</div>"+
		"</div>";
	$(".shade_layout").show();
	$("#open_returnPop").css("display","block");
	$('#open_returnPop').empty().html(htmls);
	pop_center ('500');
}
function generateQRCode(merchantId){
	var root = $("#rootsite").val();
	var merchantId = merchantId;
//	var tableNumberFrom = $("#tableNumberFrom").val();
//	var tableNumberTo = $("#tableNumberTo").val();
	var url = root + "/manager/generateqrcode";
	processPrompt("In processing...");
	$.ajax({
		type : "POST",
	//contentType : 'charset=utf-8',
	url : url,
	dataType : 'json',
	cache : false,
	data : {
		"merchantId": merchantId,
//		"tableNumberFrom": tableNumberFrom,
//		"tableNumberTo": tableNumberTo			
	},
	success : function(data) {
		close();
		var jsonObjArr;
		if(typeof data == "string") {
			jsonObjArr = JSON.parse(data);
		} else {
			jsonObjArr = data;
		}
		if(jsonObjArr.code=="1"){
			if($("#action"+merchantId).attr("hasQRCodes") == '0') {
				$("#action"+merchantId).append('<a href="downloadqrcode?merchantId=' + merchantId + '">Download QR Code</a>"');
				$("#action"+merchantId).attr("hasQRCodes", '1');
			}
			alertWarn("success");
		} else if(jsonObjArr.code=="0"){
			alertWarn(jsonObjArr.result);
		} else{
			alertWarn("Fail,please contact the administrator");
		}
	}
	});
}
function showTableNumberInput(obj) {
	if($(obj).val()=="1") {
		$(obj).parent().parent().next().show();
	} else {
		$(obj).parent().parent().next().hide();
		$("#tableNumberFrom").val("");
		$("#tableNumberTo").val("");
	}
}

function modifyAccount(){
	var root = $("#rootsite").val();
	var merchantId = $("#merchantId").val();
	var memberAccount = $("#memberAccount").val();
	var memberPwd = $("#memberPwd").val();
	var email = $("#upemail").val();
	var memberDesc = $("#memberDesc").val();
	if(memberAccount==""){
		alertWarn("please input the value about memberAccount!");
		return 0;
	}
//	if(memberPwd==""){
//		alertWarn("please input the value about password!");
//		return 0;
//	}
	if(email==""){
		alertWarn("please input the value about email!");
		return 0;
	}
    var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if(!myreg.test(email)){
       alertWarn('The email fomat is incorrect!');
       return 0;
    }
	$.post(root+"/manager/modifyaccount", {"merchantId":merchantId,"memberPwd":memberPwd,"email":email,"memberDesc":memberDesc},
			function(data){
			var jsonObjArr = JSON.parse(data);
			if(jsonObjArr.code=="1"){
				alertWarn("success");
				goThisPage($('#currentPage').val());
				close();
			}
			else if(jsonObjArr.code=="2"){
				alertWarn("The account is existed");
			}
			else if(jsonObjArr.code=="3"){
				alertWarn("The email is existed");
			}
		});
}

/**
 * 修改商户资料
 */
function modify(){
	var root = $("#rootsite").val();
	var merchantId = $("#merchantId").val();
	var merchantName = $("#merchantName").val();
	var fileId = $("#fileId").val();
	var merchantDesc = $("#merchantDesc").val();
	var mobile = $("#mobile").val();
	var taxRate = $("#taxRate").val();
	var serviceCharge = $("#serviceCharge").val();
	var mealStyle = $("#mealStyle").val();
	
	var vendorId = $("#vendor").val();
	//var cityId = $("#city").val();
	var contact = $("#contact").val();
	var address = $("#address").val();
	var photocover = $("#photocover").prop("src");
	
	var regInt = /^[0-9]+[0-9]*]*$/;  //正整数
	
	if(taxRate == ""){
		alertWarn("please input the value about Tax Rate!");
		return 0;
	}
	if(!regInt.test(taxRate)){
		alertWarn("Tax Rate Must be a positive integer!");
		return 0;
	}
	if(parseInt(taxRate)>100){
		alertWarn("Tax Rate Must less than 100!");
		return 0;
	}
	if(parseInt(taxRate)==0){
		alertWarn("Tax Rate Must more than 0!");
		return 0;
	}
	if(!regInt.test(serviceCharge)){
		alertWarn("Service Charge Must be a positive integer!");
		return 0;
	}
	if(parseInt(serviceCharge)>100){
		alertWarn("Service Charge Must less than 100!");
		return 0;
	}
	if(parseInt(serviceCharge)==0){
		alertWarn("Service Charge Must more than 0!");
		return 0;
	}
	if(merchantName==""){
		alertWarn("please input the value about merchantName!");
		return 0;
	}
	if(mobile==""){
		alertWarn("please input the value about mobile!");
		return 0;
	}
    if(!regInt.test(mobile)){
        alertWarn("Telephone Must be a positive integer!");
        return 0;
    }
/*	if(merchantDesc==""){
		alertWarn("please input the value about merchantDesc!");
		return 0;
	}*/
	var url = root + "/manager/modifymerchant"
	$.ajax({
		type : "POST",
	//contentType : 'charset=utf-8',
	url : url,
	dataType : 'json',
	cache : false,
	data : {
		"merchantId": merchantId,
		"merchantName": merchantName,
		"fileId":fileId,
		"merchantDesc":merchantDesc,
		"mobile":mobile,
		"vendorId":vendorId,
		"contact":contact,
		"address":address,
		"logoUrl":photocover,
		"taxRate":taxRate,
		"serviceCharge":serviceCharge,
		"mealStyle":mealStyle
	},
	success : function(data) {
        if(data.code != undefined && data.code != "1") {
            alertWarn(data.result);
            return;
        }
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="1"){
			alertWarn("success!");
			goThisPage($('#currentPage').val());
			close();
		}
		else{
			alertWarn(jsonObjArr.result);
		}
	}
});
}

function deleteOne(merchantId){
	var root = $("#rootsite").val();
	$.post(root+"/manager/deleteone", {"merchantId":merchantId},
			function(data){
			var jsonObjArr = JSON.parse(data);
			if(jsonObjArr.code=="1"){
				close();
				goThisPage($('#currentPage').val());
			}
			else if(jsonObjArr.code=="2"){
				close();
				alertWarn("Can't deleted now because this item contains data.");
			}
			else{
				alertWarn(jsonObjArr.result);
			}
	});
}
function enableOne(merchantId){
    var root = $("#rootsite").val();
    $.post(root+"/manager/enableOne", {"merchantId":merchantId},
        function(data){
            var jsonObjArr = JSON.parse(data);
            if(jsonObjArr.code=="1"){
                close();
                goThisPage($('#currentPage').val());
            }
            else if(jsonObjArr.code=="2"){
                close();
                alertWarn("Can't deleted now because this item contains data.");
            }
            else{
                alertWarn(jsonObjArr.result);
            }
        });
}

function authApplicator(merchantId){
	 var root = $("#rootsite").val();
	 $('#searchForm').form('clear');
	 $("#tb input[name='merchantId']").val(merchantId);
	 search();
		$('#dgHot').datagrid('load',root+'/merchant/authApplicatorList?condition=rms.MERCHANT_ID is  null'+'&merchantId='+$("#tb input[name='merchantId']").val());
	 $('#windowApplicator').window('open');
}
function formatterImageUrl(val,row){
	return '<img style="width:100px;height:100px;"src="'+val+'?rs=150_150">';
}
function formatterImageUrlHot(val,row){
	return '<img style="width:100%;height:100%"src="'+val+'?rs=150_150">';
}

function search(){
	var root = $("#rootsite").val();
	$('#dg').datagrid('load',root+'/merchant/authApplicatorList?conditionHot=rms.MERCHANT_ID is  null'+
			'&applicatorId='+$("#tb input[name='applicatorId']").val()+
			'&merchantName='+$("#tb input[name='merchantName']").val()+
			'&merchantId='+$("#tb input[name='merchantId']").val()+
			'&mobile='+$("#tb input[name='mobile']").val()+
			'&city='+$("#tb input[name='city']").val()	
	);
	

}


/**
 * 品牌授权弹出窗口
 * @param prodCatId
 */
function authBrandChoseBox(merchantId){
	 var root = $("#rootsite").val();
	 var iTop = (window.screen.availHeight-30-350-200)/2;
	 var iLeft = (window.screen.availWidth-10-700)/2; 
	 window.open (root+'/manager/authbrandchosebox?merchantId='+merchantId,'newwindow','height=600,width=1000,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
}

/**
 * 编辑商户银行账户信息弹出窗口
 */
function showBankPopupWin(merchantId,obj){
	$("#bankName").val("");
	$("#bankAccount").val("");
	$("#bankAccountName").val("");
	var root = $("#rootsite").val();
	$(".shade_layout").show();
	$("#vendorPopup").show();
	$("#bankMerchantId").val(merchantId);
	$.post(root+"/manager/checkmerchantbankinfo", {"merchantId":merchantId},
		function(data){
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="1"){
			$("#bankName").val(jsonObjArr.bankName);
			$("#bankAccount").val(jsonObjArr.bankAccount);
			$("#bankAccountName").val(jsonObjArr.bankAccountName);
		}
	});
	pop_center ('560');
}
/**
 * 保存商户银行账户信息
 */
function saveBankInfo(){
	var root = $("#rootsite").val();
	var bankMerchantId = $("#bankMerchantId").val();
	var bankName = $("#bankName").val();
	var bankAccount = $("#bankAccount").val();
	var bankAccountName = $("#bankAccountName").val();
	$.post(root+"/manager/savebankinfo", {"merchantId":bankMerchantId,"bankName":bankName,"bankAccount":bankAccount,"bankAccountName":bankAccountName},
		function(data){
		var jsonObjArr = JSON.parse(data);
		if(jsonObjArr.code=="1"){
			alertWarn("success!");
			close();
			goThisPage($('#currentPage').val());
		}
		else{
			alertWarn(jsonObjArr.result);
		}
	});
}
