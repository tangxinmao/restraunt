//笛卡儿积组合  
function descartes(list){  
	//parent上一级索引;count指针计数  
	var point = {};  
	var result = [];  
	var pIndex = null;  
	var tempCount = 0;  
	var temp  = [];  
	var skuHtml = "";
	//根据参数列生成指针对象  
	for(var index in list){  
		if(typeof list[index] == 'object'){  
			point[index] = {'parent':pIndex,'count':0};  
			pIndex = index;  
		}  
	}  
	//单维度数据结构直接返回  
	if(pIndex == null){  
		return list;  
	}  
	//动态生成笛卡尔积  
	while(true){  
		var sku_val = "";
		for(var index in list){  
			tempCount = point[index]['count'];  
			temp.push(list[index][tempCount]);  
			var prop_value = list[index][tempCount].value;
			var prop_id = $(list[index][tempCount]).attr("id");
			var enumId = $(list[index][tempCount]).attr("enumId");
			var prop_name = $(list[index][tempCount]).attr("pval");
			skuHtml += "<td class='"+enumId+"' par='"+prop_id+"'>"+prop_value+" <input type='hidden' name='skuvalue' value='"+prop_id+",,,"+enumId+",,,"+prop_value+",,,"+prop_name+"'></td>";
			if(sku_val=="")
				sku_val = prop_id+",,,"+enumId+",,,"+prop_value+",,,"+prop_name;
			else
				sku_val = sku_val+"@@@"+prop_id+",,,"+enumId+",,,"+prop_value+",,,"+prop_name;
		} 
		var storageNum_val="";
		if(skumap.get("storageNum"+sku_val)!=null){
			storageNum_val=skumap.get("storageNum"+sku_val);
		}
		var price_val="";
		if(skumap.get("price"+sku_val)!=null){
			price_val=skumap.get("price"+sku_val);
		}
		var merchantProdId_val="";
		if(skumap.get("merchantProdId"+sku_val)!=null){
		merchantProdId_val=skumap.get("merchantProdId"+sku_val);
		}
		var originprice_val="0";
		if(skumap.get("originprice"+sku_val)!=null){
			originprice_val=skumap.get("originprice"+sku_val);
		}
		skuHtml=skuHtml+"<td hidden><input type='text' class='text' maxlength='8' name='prop_originprice' size='10' value='"+originprice_val+"' onblur=\"skumap.put('originprice"+sku_val+"',this.value);\" readonly></td><td><input name='prop_skuid' type='hidden' value='"+sku_val+"'><input name='prop_skuval' type='hidden' value=''><input type='text' class='text' maxlength='8' name='prop_price' size='10' value='"+price_val+"' onblur=\"skumap.put('price"+sku_val+"',this.value);\"></td><td><a href='javascript:void(0);' onclick='delPropCascade(this)'>Delete</a></td></tr>";
		//压入结果数组  
		result.push(temp);  
		temp = [];  
		//检查指针最大值问题  
		while(true){  
			if(point[index]['count']+1 >= list[index].length){  
				point[index]['count'] = 0;  
				pIndex = point[index]['parent'];  
				if(pIndex == null)  
				{  
				return skuHtml;  
				}  
				//赋值parent进行再次检查  
				index = pIndex;  
			}  
			else{  
				point[index]['count']++;  
				break;  
			}  
		}  
	}  
}  


var skumap  = new Map(); //记录输入的SKU库存，原价及商品编号
var isfillsku=0; //1=生成SKU表格，0=未生成SKU表格
function Map() {
	 var struct = function(key, value) {
	  this.key = key;
	  this.value = value;
	 };
	 
	 var put = function(key, value){
	  for (var i = 0; i < this.arr.length; i++) {
	   if ( this.arr[i].key === key ) {
	    this.arr[i].value = value;
	    return;
	   }
	  }
	   this.arr[this.arr.length] = new struct(key, value);
	 };
	 
	 var get = function(key) {
	  for (var i = 0; i < this.arr.length; i++) {
	   if ( this.arr[i].key === key ) {
	     return this.arr[i].value;
	   }
	  }
	  return null;
	 };
	 
	 var remove = function(key) {
	  var v;
	  for (var i = 0; i < this.arr.length; i++) {
	   v = this.arr.pop();
	   if ( v.key === key ) {
	    continue;
	   }
	   this.arr.unshift(v);
	  }
	 };
	 
	 var size = function() {
	  return this.arr.length;
	 };
	 
	 var isEmpty = function() {
	  return this.arr.length <= 0;
	 } ;
	 this.arr = new Array();
	 this.get = get;
	 this.put = put;
	 this.remove = remove;
	 this.size = size;
	 this.isEmpty = isEmpty;
	}

$( function(){
	
	
	$("#yhtest").click(function(){
		
	});
	//其他品牌显示品牌名称
	if($("#prodBrandId").val()=="0"){
		$("#brandnamediv").html("<p><label>Brand Name:</label><input type='text' value='' name='prodBrandName' id='prodBrandName' class='text' /></p>");
	}
	$("#prodBrandId").change(function(){
		if($("#prodBrandId").val()=="0"){
			$("#brandnamediv").html("<p><label>Brand Name:</label><input type='text' value='' name='prodBrandName' id='prodBrandName' class='text' /></p>");
		}
		else
			$("#brandnamediv").html("");
	});
	//商品图片空间按钮
	$("#prodshowimages").bind("click",function(){
		if($(this).parent().parent().find(".imgManage_bd").css("display")=="none"){
			$(".imgManage_bd").each(function(){
				$(this).hide().removeClass('ctl_image_select_show');
			});
			$(this).parent().parent().find(".imgManage_bd").show().addClass('ctl_image_select_show');
		}
		else if($(this).parent().parent().find(".imgManage_bd").css("display")=="block")
			$(this).parent().parent().find(".imgManage_bd").hide().removeClass('ctl_image_select_show');
	});
	
	//ckeditor图片空间
	$("#ckimagespace").bind("click",function(){
		if($(this).parent().parent().parent().find(".imgManage_bd").css("display")=="none"){
			$(".imgManage_bd").each(function(){
				$(this).hide().removeClass('ctl_image_select_show');
			});
			$(this).parent().parent().parent().find(".imgManage_bd").show().addClass('ctl_image_select_show');;
		}
		else if($(this).parent().parent().parent().find(".imgManage_bd").css("display")=="block")
			$(this).parent().parent().parent().find(".imgManage_bd").hide().removeClass('ctl_image_select_show');;
	});
	$('body').on('click',function(event){
//		$(".imgManage_bd").each(function(){
			if($(event.target).closest('.imgManage_bd').length==0
					&& $('.ctl_image_select_show').length > 0
					&& !($(event.target).hasClass('btn_upfile'))){
				$(".imgManage_bd").hide().removeClass('ctl_image_select_show');
			}
//		});
	});
	
	$("#addDIYprop").click(function(){
		$(".shade_layout").show();
		$("#pop_windows0").show();
		$("#customsizename").val("");
		$("#customsizevalue").val("");
		pop_center('540');
		
	});
	$(".ico_close").click(function(){
		$(".shade_layout").hide();
		$(".pop_windows").hide();
	});
	//新增自定义属性枚举值
	$("#addcuspropsubmit").click(function(){
		var abc = $("#customsizetable").find("tbody tr").eq(0).html();
		if(typeof(abc)=="undefined"){
			$("#addcuspropsubmit").attr("myvalue",0) ;
			$("#customsizetable").find("tbody").append("<tr><th>Property Name</th><th>Property Value</th><th>Actions</th></tr>");
		}
		var num = parseInt($("#addcuspropsubmit").attr("myvalue")) + 1;
		var name = $("#customsizename").val();
		var value = $("#customsizevalue").val();
		var re =/[~#^$@%&!*]/gi;
        if (re.test(name)) {
            alertWarn("Property name contains special character!");
            return 0;
        }
        if (re.test(value)) {
            alertWarn("Property value contains special character!");
            return 0;
        }
		if(name==null || name==""){
			alertWarn("Please input Property name!");
			return 0;
		}
		if(value==null || name==""){
			alertWarn("Please input Property value!");
			return 0;
		}
		$("#customsizetable").find("tbody").append("<tr  num="+num+"><td>"+name+"</td><td>"+value+"</td><td width='105'><a href='javascript:void(0)' onclick=\"updatecustomprop("+num+")\">Edit</a> | <a href='javascript:void(0)' onclick=$(this).parent().parent().remove()>Delete</a></td></tr>");
		$(".shade_layout").hide();
		$(".pop_windows").hide();
		$("#customsizename").val("");
		$("#customsizevalue").val("");
		num = parseInt($("#addcuspropsubmit").attr("myvalue")) + 1;
		$("#addcuspropsubmit").attr("myvalue",num) ;
	});
	
	//修改自定义属性枚举值
	$("#updatecuspropsubmit").click(function(){
		var num = $("#updatecuspropsubmit").attr("myvalue");
		var name = $("#customsizename2").val();
		var value = $("#customsizevalue2").val();
		var re =/[~#^$@%&!*]/gi;
        if (re.test(name)) {
            alertWarn("Property name contains special character!");
            return 0;
        }
        if (re.test(value)) {
            alertWarn("Property value contains special character!");
            return 0;
        }
		if(name==null || name==""){
			alertWarn("Please input Property name!");
			return 0;
		}
		if(value==null || name==""){
			alertWarn("Please input Property value!");
			return 0;
		}
		$("#customsizetable tbody").find("tr[num="+num+"]").html("<td>"+name+"</td><td>"+value+"</td><td width='105'><a href='javascript:void(0)' onclick=\"updatecustomprop("+num+")\">Edit</a> | <a href='javascript:void(0)' onclick=$(this).parent().parent().remove()>Delete</a></td>");
		$(".shade_layout").hide();
		$(".pop_windows").hide();
		$("#customsizename2").val("");
		$("#customsizevalue2").val("");
	});
	
});

/**
 * 修改自定义属性枚举值弹出窗
 */
function updatecustomprop(num){
	var name = $("#customsizetable tbody").find("tr[num="+num+"] td").eq(0).text();
	var value = $("#customsizetable tbody").find("tr[num="+num+"] td").eq(1).text();
	$("#customsizename2").val(name);
	$("#customsizevalue2").val(value);
	$(".shade_layout").show();
	$("#pop_windows1").show();
	$("#updatecuspropsubmit").attr("myvalue",num) ;
	pop_center('540');
}

/**
 * 图片空间文件夹显示
 */
function showTree(parentid,buz){
	$(".imgList").html("");
	dataForSend = {"parentId":parentid};
	$.post("queryimage",jQuery.param(dataForSend),function(json){ 
		var jsonObjArr = JSON.parse(json);
		if(parentid!="0")
			$(".imgList").html('<li><img alt="" src="'+siteurl+'/static/images/sellercenter_bg/imgs/imgitem1.png"  ondblclick=showTree($(this).attr(\'rel\'),$(this).attr(\'buz\')); buz="'+parentid+'" rel="'+buz+'"  width="80px" height="80px" /><p>back</p></li>');
		
		for(var i in jsonObjArr){
			var jsonObj = jsonObjArr[i];
			if(jsonObj["type"]==1){
				$(".imgList").append('<li><img alt="" src="'+siteurl+'/static/images/sellercenter_bg/imgs/imgitem1.png"  ondblclick=showTree($(this).attr(\'rel\'),$(this).attr(\'buz\')); buz="'+parentid+'" rel="'+jsonObj["imageSpaceId"]+'" width="80px" height="80px" /><p>'+jsonObj["name"]+'</p></li>');
			}
			else{
				$(".imgList").append('<li><img alt="" src="'+jsonObj["imgUrl"]+'?rs=120_120" originurl="'+jsonObj["imgUrl"]+'" title="'+jsonObj["name"]+'" imgid="'+jsonObj["imageSpaceId"]+'" width="80px" height="80px" /></li>');
			}
			
		}
		$(".imgList img").click(function(){
			var url=$(this).attr("src");
			var originurl = $(this).attr("originurl");
			var rel = $(this).attr("rel");
			var imgid = $(this).attr("imgid");
			$(this).parent().parent().parent().parent().parent().find("div[class=imgUpFile] li").each(function(){
				if($(this).find("img").attr("isedit")=="0" && typeof(rel) == 'undefined'){
					$(this).find("img").attr("src",url);
					$(this).find("img").attr("originurl",originurl);
					$(this).find("img").attr("imgid",imgid);
					$(this).find("img").attr("isedit","1"); 
					return false;
				}
			});
		});
		$("#ckeditorspace img").click(function(){
			var url=$(this).attr("originurl");
			var rel = $(this).attr("rel");
			var imgid = $(this).attr("imgid");
			if(typeof(rel) == 'undefined'){
				CKEDITOR.instances['prodDetailDescContent'].insertHtml("<div><img imgid="+imgid+" src="+url+"></div>");
			}
		});
	});
	
}

/**
 * 上传颜色图片
 */
function uploadColorImg(e,num){
	var filename = $("#inputfile"+e+"_"+num).val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		jQuery('#picUploadForm'+e+'_'+num).submit(); 
		var $new = $("<input type='file' t='"+ new Date().getTime() +"' name='imagefile' style='opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;' size=1 id='inputfile"+e+"_"+num+"'>");
		$new.on('change', function(args){
			return function(){
				uploadColorImg(args.e,args.num);
			};
		}({e:e,num:num}));
		$("#inputfile"+e+"_"+num).replaceWith( $new );

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}  
}
/**
 * 上传商品图片
 */
$(document).delegate("#inputfile1","change",function(){  
	var filename = $("#inputfile1").val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		var picUploadForm1=document.getElementById("picUploadForm1");
		picUploadForm1.submit(); 
//		var objFile = document.getElementById("inputfile1");
//		objFile.value="";
		//$new.on('change', uploadImage );
		$('#inputfile1').remove();
		$("#picUploadForm1 div").append('<input t="'+ new Date().getTime() +'" type="file" name="imagefile"  style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;" size=1  id="inputfile1" />');

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}
});
$(document).delegate("#inputfile2","change",function(){  
	var filename = $("#inputfile2").val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		var picUploadForm2=document.getElementById("picUploadForm2");
		picUploadForm2.submit(); 
//		var objFile = document.getElementById("inputfile2");
//		objFile.value="";
		//$new.on('change', uploadImage );
		$('#inputfile2').remove();
		$("#picUploadForm2 div").append('<input t="'+ new Date().getTime() +'" type="file" name="imagefile"  style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;" size=1  id="inputfile2" />');

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}
});
$(document).delegate("#inputfile3","change",function(){  
	var filename = $("#inputfile3").val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		var picUploadForm3=document.getElementById("picUploadForm3");
		picUploadForm3.submit(); 
//		var objFile = document.getElementById("inputfile3");
//		objFile.value="";
		//$new.on('change', uploadImage );
		$('#inputfile3').remove();
		$("#picUploadForm3 div").append('<input t="'+ new Date().getTime() +'" type="file" name="imagefile"  style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;" size=1  id="inputfile3" />');

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}
});
$(document).delegate("#inputfile4","change",function(){  
	var filename = $("#inputfile4").val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		var picUploadForm4=document.getElementById("picUploadForm4");
		picUploadForm4.submit(); 
//		var objFile = document.getElementById("inputfile4");
//		objFile.value="";
		//$new.on('change', uploadImage );
		$('#inputfile4').remove();
		$("#picUploadForm4 div").append('<input t="'+ new Date().getTime() +'" type="file" name="imagefile"  style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;" size=1  id="inputfile4" />');

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}
});
$(document).delegate("#inputfile5","change",function(){  
	var filename = $("#inputfile5").val().split("\\").pop();
	var parm = /\.(GIF|gif|jpg|JPG|jpeg|JPEG|png|PNG|ico|ICO|bmp|BMP)$/;
	if(parm.test(filename)){
		var picUploadForm5=document.getElementById("picUploadForm5");
		picUploadForm5.submit(); 
//		var objFile = document.getElementById("inputfile1");
//		objFile.value="";
		//$new.on('change', uploadImage );
		$('#inputfile5').remove();
		$("#picUploadForm5 div").append('<input t="'+ new Date().getTime() +'" type="file" name="imagefile"  style="opacity:0; filter: Alpha(Opacity=0); cursor:pointer;z-index:1;position: absolute;left: 9px; width:62px; overflow:hidden; top: 0;" size=1  id="inputfile5" />');

	}
	else{
		alertWarn("The file format you upload is incorrect!");
	}
});
function ckeditorUpload(){
	$.ajaxFileUpload
	(
		{
			url:'uploadckeditor?prodId='+$("input[name=prodId]").val(),
			secureuri:false,
			fileElementId:'image',
			dataType: 'json',
			success: function (data, status)
			{
				if( data.success )
				{
					CKEDITOR.instances['prodDetailDescContent'].insertHtml("<div><img imgid="+data.fileid+" src="+data.imgUrl+"></div>");
				} else {
//					alert(data.errorMsg);
				}
				createNewUploadHtml();
			},
			error: function (data, status, e)
			{
				createNewUploadHtml();
			}
		}
	);
}
//重新绑定onchange事件
function createNewUploadHtml(){
	var $new = $('<input t="'+ new Date().getTime() +'" size="1" type="file" name="image" id="image" class="upload link_btn" value="上传图片" style="opacity:0; filter: Alpha(Opacity=0); height:24px; cursor:pointer;z-index:1;position: absolute;left: -17px; width:62px; overflow:hidden; top: -25px;" />');
	$('#image').replaceWith($new);
	$new.change(ckeditorUpload);
}


//SKU
/**
 * 点击单品属性Checkbox，设置值为可编辑
 */
function changeEdit(obj){
	var value = $(obj).val();
	if($(obj).prop("checked")){
		$(obj).parent().find("span").html('<input type="text" name="" id="propskuvalue" value="'+value+'" class="text"  style="width:90px" onblur="updateSkuVal(this)">');
	}
	else{
		$(obj).parent().find("span").html(value);
	}
		
}
function getSumSkuNum(){
	var sumStorage = 0;
	var regInt = /^[0-9]+[0-9]*]*$/;
	$("#skuproptable").find("tr").each(function(){
		if(typeof($(this).find("input[name=prop_storageNum]").val()) != 'undefined'){
			var checkstorage = $(this).find("input[name=prop_storageNum]").val();
			if(regInt.test(checkstorage)){
				sumStorage = sumStorage + parseInt($(this).find("input[name=prop_storageNum]").val());
			}
		}
	});
	$("#prodStorage").val(sumStorage);
}
/**
 * 修改Sku属性的枚举值
 */
function updateSkuVal(obj){
	$(obj).parent().parent().find('input[type=checkbox]').attr('value',obj.value);
	$("#skuproptable").find("td").each(function(){
		if($(this).attr("class")==$(obj).parent().find('input[type=checkbox]').attr('enumid')){
			$(this).text(obj.value);
			showtable();
		}
	});
}

function showcustomsizeprop(obj,propid){
	var iscustom = $(obj).find("option:selected").attr('iscustom');
	if(iscustom!=undefined){
		$("#div"+propid).html('<input type="text" name="" id="prodpropvalue" value="'+$(obj).find("option:selected").val()+'" class="text" style="width:90px" maxlength="20" onblur="changeselectval(this,this.value)">');
	}
	else{
		$("#div"+propid).html("");
	}
}

function changeselectval(obj,value){
	$(obj).parent().parent().find("select option:selected").attr("value",value);
	
}

/**
 * 删除sku单品，取消checkbox选中项及颜色图片上传区域
 */
function delPropCascade(obj){
	var delList=new Array();
	var propTr=$(obj).parent().parent();
	for(var i=0;i<$(propTr).children("td").size()-1;i++){
//		alert($(propTr).find("td").eq(i).children("input:text").size());
		if($(propTr).find("td").eq(i).children("input:text").size()==0){
			var cl=$(propTr).find("td").eq(i).attr("class");
			var par=$(propTr).find("td").eq(i).attr("par");
			var key=cl+"-"+par;
//			alert($("#skuproptable").find("td[class='"+cl+"'][par='"+par+"']").size());
			//判断该属性在该table中是否还有别的引用
			//如果为1则只有该属性定义框引用
			if($("#skuproptable").find("td[class='"+cl+"'][par='"+par+"']").size()==1){
				delList.push(key);
			}
		}
	}
//	alert(delList.length);
	for(var i=0;i<delList.length;i++){
		var key=new Array();
		key=delList[i].split("-");
		var cl=key[0];
		var par=key[1];
		$("input[name='"+par+"'][enumid='"+cl+"']").prop("checked","");
		$("div[enumid='"+cl+"']").remove();
	}
	$(obj).parent().parent().remove();
	showimage();
//	alert(.find("td:first").attr("par"));
//	alert($(propTr).arrt("par"));
} 

/**
 * 根据颜色属性枚举值ID显示图片空间
 * @param e  颜色属性枚举值ID
 */
function showimages(e){
	if($("#show_"+e).css("display")=="none"){
		$(".imgManage_bd").each(function(){
			$(this).hide().removeClass('ctl_image_select_show');
		});
		$("#show_"+e).show().addClass('ctl_image_select_show');
	}
	else if($("#show_"+e).css("display")=="block")
		$("#show_"+e).hide().removeClass('ctl_image_select_show');
	//选择颜色图片空间图片
	$("#show_"+e).find("ul > img").click(function(){
		var url=$(this).attr("src");
		var originurl = $(this).attr("originurl");
		var rel = $(this).attr("rel");
		var imgid = $(this).attr("imgid");
		$("#imgarea_"+e).find("li").each(function(){
			if($(this).find("img").attr("isedit")=="0" && typeof(rel) == 'undefined'){
				$(this).find("img").attr("src",url); 
				$(this).find("img").attr("originurl",originurl); 
				$(this).find("img").attr("imgid",imgid); 
				$(this).find("img").attr("isedit","1"); 
				return false;
			}
		});
	});
	//点击图片显示框取消颜色图片
	$("#imgarea_"+e).find("ul li img").click(function(){
		$(this).attr("src",siteurl+"/static/images/ico/icon_image.png");
		$(this).attr("isedit","0");
	});
}

/**
 * 成功及提示框
 */
function friendlyPrompt(message){
	var htmls = "<div class='layout'>"+
			"<b class='ico_close' onclick='closed()'>关闭</b>"+
			"<div class='main'>"+
				"<div class='bd_p10 clearfix'>"+
                	"<div class='popup_tips_title pl150 mt25 mb20 ml20'><b class='ico success fl'></b><span class='fl ml15'>"+message+"</span></div>"+
				"</div>"+
				"<div class='freightTemplet_btns mt15'>"+
					"<input type='button' value='OK' onclick=\"javascript:self.location='init'\" class='btn btn-sm btn-sgGreen'>"+
				"</div>"+
			"</div>"+
		"</div>";
	$(".shade_layout").show();
	$("#open_returnPop").css("display","block");
	$('#open_returnPop').empty().html(htmls);
	pop_center ('500');
}
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
 * 处理中提示框
 */
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
/**
 * 取消编辑商品弹出框
 */
function tShowCanclePop(message){
	var htmls = "<div class='layout'>"+
	"<b class='ico_close' onclick='closed()'>关闭</b>"+
	"<div class='main'>"+
		"<div class='bd_p10 clearfix'>"+
        	"<div class='popup_tips_title pl150 mt25 mb20 ml20'>"+
        	"<b class='ico fl'></b><span class='fl ml15'>Prompt</span></div>"+
            "<p class='tac fontSize120 colorOrange mb20'>"+message+"</p>"+
		"</div>"+
		"<div class='freightTemplet_btns mt15'>"+
			"<input type='button' value='OK' onclick=\"javascript:self.location='init'\"  class='btn btn-sm btn-sgGreen'>"+
			"<input type='button' value='Cancel' class='btn btn-sm btn-grey' onclick='closed()'>"+
		"</div>"+
	"</div>"+
	"</div>";
	$(".shade_layout").show();
	$("#open_returnPop").show();
	$('#open_returnPop').empty().html(htmls);
	pop_center ('500');
}
function innerInit(){
	javascript:self.location='init';
}


function closed(){
	$(".shade_layout").hide();
	$(".pop_windows").hide();
}

/**
 *  组装商品发布的基础数据
 */
function papreJsonDate(){
	var dataArr = new Array();
	var columnNameArr = new Array();
	columnNameArr[0] = "prodName";
	columnNameArr[1] = "prodStockWarning";
	columnNameArr[2] = "prodMeasureUnit";
	columnNameArr[3] = "prodOriginPrice";
	columnNameArr[4] = "prodPrice";
	columnNameArr[5] = "prodDetailDescContent";
	columnNameArr[7] = "prodImgUrls";
	columnNameArr[8] = "prodSkuImgUrlsMap";
	columnNameArr[9] = "merchantId";
	columnNameArr[10] = "prodFreightTempId";
	columnNameArr[11] = "prodStorage";
	columnNameArr[12] = "prodCatId";
	columnNameArr[13] = "prodBrandId";
	columnNameArr[14] = "propurls";
	columnNameArr[15] = "prodAdword";
	columnNameArr[16] = "customPropArr";
	columnNameArr[17] = "prodBrandName";
	columnNameArr[18] = "weight";
	columnNameArr[19] = "volume";
	columnNameArr[20] = "strockDeductionType";
	columnNameArr[21] = "prodId";
	columnNameArr[22] = "citys";
	columnNameArr[23] = "productType";
	columnNameArr[24] = "priceManner";
	dataArr[0] = columnNameArr;
	//商品图片URL
	var prodimageurls="";
	$("#prodimgarea").find("li").each(function(){
		if($(this).find("img").attr("isedit")=="1"){
			if(prodimageurls=="")
				prodimageurls=$(this).find("img").attr("originurl");
			else 
				prodimageurls=prodimageurls+",,,"+$(this).find("img").attr("originurl");
		}
	});
	
	//颜色图片URL
	var propurls = new Array();
	
	for(var i in colorenumids){
		var propurl = "";
		$("#imgarea_"+colorenumids[i]).find("li").each(function(){
			if($(this).find("img").attr("isedit")=="1"){
				if(propurl==""){
					propurl=$(this).find("img").attr("originurl");
				}
				else
					propurl=propurl+",,,"+$(this).find("img").attr("originurl");
			}
		});
		propurls[i] = propurl;
	}
	
	//用户自定义属性值
	var customPropArr = new Array();
	var customi = 0;
	$("#customsizetable").find("tr").each(function(){
		if($(this).find("td").eq(0).text()!=""){
			customPropArr[customi] = $(this).find("td").eq(0).text()+",,,"+$(this).find("td").eq(1).text();
			customi++;
		}
	});
	
	
	var weight="";
	if($("#weight").val()==""){
		weight=null;
	}
	else{
		weight=$("#weight").val();
	}
	var volume="";
	if($("#volume").val()==""){
		volume=null;
	}
	else{
		volume=$("#volume").val();
	}
	//城市信息
	var citys = "";
	$(".like_type_list").find("span").each(function(){
		if($(this).hasClass("selected1")){
			if(citys==""){
				citys = $(this).attr("myval");
			}
			else{
				citys = citys + ",,," +  $(this).attr("myval");
			}
		}
		
	});
	var columnValueArr = new Array();
	columnValueArr[0] = $("#prodName").val();
	columnValueArr[1] = $("#prodStockWarning").val();
	columnValueArr[2] = $("#prodMeasureUnit").val();
	columnValueArr[3] = $("#prodOriginPrice").val();
	columnValueArr[4] = $("#prodPrice").val();
	columnValueArr[5] = CKEDITOR.instances['prodDetailDescContent'].getData();
	columnValueArr[7] = prodimageurls;
	columnValueArr[9] = merchantid;
	columnValueArr[10] = $("#prodFreightTempId").val();
	columnValueArr[11] = $("#prodStorage").val();
	columnValueArr[12] = prodCatIdinfo;
	columnValueArr[13] = $("#prodBrandId").val();
	columnValueArr[15] = $("#prodAdword").val();
	columnValueArr[17] = $("#prodBrandName").val();
	columnValueArr[18] = weight;
	columnValueArr[19] = volume;
	columnValueArr[20] = $("#strockDeductionType").val();
	columnValueArr[21] = $("#prodId").val();
	columnValueArr[22] = citys;
	columnValueArr[23] = $("#prodType").val();
	columnValueArr[24] = $("input[name='priceManner']:checked").val();
	dataArr[1] = columnValueArr;
	dataforsent={"prodName":columnValueArr[0],"prodStockWarning":columnValueArr[1],"prodMeasureUnit":columnValueArr[2],"prodOriginPrice":columnValueArr[3],"prodPrice":columnValueArr[4],"prodDetailDescContent":columnValueArr[5],"prodImgUrls":columnValueArr[7],"merchantId":columnValueArr[9],"prodFreightTempId":columnValueArr[10],"prodStorage":columnValueArr[11],"prodCatId":columnValueArr[12],"prodBrandId":columnValueArr[13],"propurls":propurls,"prodAdword":columnValueArr[15],"customPropArr":customPropArr,"prodBrandName":columnValueArr[17],"weight":columnValueArr[18],"volume":columnValueArr[19],"strockDeductionType":columnValueArr[20],"prodId":columnValueArr[21],"citys":columnValueArr[22],"productType":columnValueArr[23],"priceManner":columnValueArr[24]};
	return dataforsent;
}

function publishProduct(operation){
	var prodStockWarning = $("#prodStockWarning").val();
	var prodMeasureUnit = $("#prodMeasureUnit").val();
	var prodName = $("#prodName").val();
	var prodOriginPrice = $("#prodOriginPrice").val();
	var prodPrice = $("#prodPrice").val();
	var reg = /^\d{0,15}\.{0,1}(\d{1,8})?$/;  // 数字
	var regInt = /^[0-9]+[0-9]*]*$/;  //正整数
	
	if(prodName==""){
		alertWarn("Please input product name!");
		$("#prodName").focus();
		return 0;
	}
	var re =/[~#^$@%&!*]/gi;
    if (re.test(prodName)) {
        alertWarn("Product name can not contains special character!");
        $("#prodName").focus();
        return 0;
    }
    var prodBrand = $("#prodBrandId").val();
    if(prodBrand==null || prodBrand==""){
    	alertWarn("Brand name can not be null!");
		$("#prodBrandId").focus();
		return 0;
    }
    
    var propOK = 1;
    $("select[isrequired=1]").each(function(){
    	if($(this).val()=="-1"){
    		alertWarn("Please chose product Propertie:"+$(this).attr("propname"));
    		$(this).focus();
    		propOK=0;
    		return false;
    	}
    });
    if(propOK==0) return 0;
    
	
	/*if(prodOriginPrice==""){
		alertWarn("Please input product origin price!");
		$("#prodOriginPrice").focus();
		return 0;
	}
	if(!reg.test(prodOriginPrice)){
		alertWarn("Product origin price must be numbers!");
		$("#prodOriginPrice").focus();
		return 0;
	}*/
	if(prodPrice==""){
		alertWarn("Please input product current price!");
		$("#prodPrice").focus();
		return 0;
	}
	if(!reg.test(prodPrice)){
		alertWarn("Product current price must be numbers!");
		$("#prodPrice").focus();
		return 0;
	}
	if($("#propItems input:checkbox:checked").length == 1) {
		alertWarn("Please choose at least 2 specifications!");
		return 0;
	}
	if($("#propItems input:checkbox:checked").length > 8) {
		alertWarn("Please choose at most 8 specifications!");
		return 0;
	}
	
//	//判断是否填写SKU信息
//	if($(".goodsAttr_bd input[type=checkbox]").length>0 && isfillsku==0){
//		alertWarn("Please chose and input sku properties information!");
//		$("#volume").focus();
//		return 0;
//	}
	
	var i = 0;
	var skuvalue =  new Array();
	var ok = true;  //判断输入数据是否合法
	$("#skuproptable").find("tr").each(function(){
//		skuvalue=$(this).find("td").attr("par")+",,,"+$(this).find("td").text()+",,,"+$(this).find("td input[name=prop_storageNum]").val()
//		          +",,,"+$(this).find("td input[name=prop_price]").val()",,,"+$(this).find("td input[name=prop_merchantProdId]").val();
		
		
		if(typeof($(this).find("input[name=prop_price]").val()) != 'undefined'){
			skuattr[i] = $(this).find("input[name=prop_skuid]").val();
			var checkprice = $(this).find("input[name=prop_price]").val();
//			var checkstorage = $(this).find("input[name=prop_storageNum]").val();
			var checkoriginprice=$(this).find("input[name=prop_originprice]").val();
//			if(checkstorage==""){
//				alertWarn("Please input SKU property storage!");
//				$("input[name=prop_storageNum]").eq(i).focus();
//				ok = false;
//				return false;
//			}
//			if(!regInt.test(checkstorage)){
//				alertWarn("SKU property storage must be positive integer!");
//				$("input[name=prop_storageNum]").eq(i).focus();
//				ok = false;
//				return false;
//			}
			/*if(checkoriginprice==""){
				alertWarn("Please input SKU origin price!");
				$("input[name=prop_originprice]").eq(i).focus();
				ok = false;
				return false;
			}
			if(!reg.test(checkoriginprice)){
				alertWarn("SKU origin price must be numbers!");
				$("input[name=prop_originprice]").eq(i).focus();
				ok = false;
				return false;
			}*/
			if(checkprice==""){
				alertWarn("Please input SKU current price!");
				$("input[name=prop_price]").eq(i).focus();
				ok = false;
				return false;
			}
			if(!reg.test(checkprice)){
				alertWarn("SKU current price must be numbers!");
				$("input[name=prop_price]").eq(i).focus();
				ok = false;
				return false;
			}
			skuvalue[i]=+$(this).find("input[name=prop_originprice]").val()+",,,"+$(this).find("input[name=prop_price]").val()+",,,"+$(this).find("input[name=prop_merchantProdId]").val();
			//skumap.put(i,skuvalue);
			i++;
		}
		
	});
	if(!ok)return 0;
	
	//商品图片URL
	var prodimageurls="";
	var prodimgids="";
	$("#prodimgarea").find("li").each(function(){
		if($(this).find("img").attr("isedit")=="1"){
			if(prodimageurls==""){
				prodimageurls=$(this).find("img").attr("src");
				prodimgids=$(this).find("img").attr("imgid");
			}
			else{ 
				prodimageurls=prodimageurls+",,,"+$(this).find("img").attr("src");
				prodimgids=prodimgids+",,,"+$(this).find("img").attr("imgid");
			}
		}
	});
	if(prodimageurls==""){
		alertWarn("Please chose or upload product picture!");
		$("#inputfile1").focus();
		return 0;
	}
	//颜色图片URL
	var propurls = new Array();
	var colorenumid = "";  //颜色枚举值ID
	var colorimgid = "";   //引用颜色图片ID
	var urlstring ="";  //用于判断是否有颜色图片
	for(var i in colorenumids){
		var propurl = "";
		$("#imgarea_"+colorenumids[i]).find("li").each(function(){
			if($(this).find("img").attr("isedit")=="1"){
				if(propurl==""){
					propurl=$(this).find("img").attr("src");
					colorimgid=$(this).find("img").attr("imgid");
				}
				else{
					propurl=propurl+",,,"+$(this).find("img").attr("src");
					colorimgid=colorimgid+",,,"+$(this).find("img").attr("imgid");
				}
			}
		});
		propurls[i] = propurl;
		urlstring += propurls[i]; 
		if(propurls[i]==""){  //只要某项颜色属性没设置图片就将判断变量值设为空
			urlstring="";
			break;
		}
		if(colorenumid=="")colorenumid=colorenumids[i];
		else
			colorenumid = colorenumid+",,,"+colorenumids[i];
	}
	if(urlstring=="" && havacolorImg==1){
		alertWarn("Please chose or upload color picture!");
		$("#prodshowimages").focus();
		return 0;
	}
	
	var detailContent = CKEDITOR.instances['prodDetailDescContent'].getData();
//	if(detailContent=="" || detailContent==null){
//		alertWarn("Product description can not be empty!");
//		$("#prodDetailDescContent").focus();
//		return 0;
//	}
	var k = CKEDITOR.instances["prodDetailDescContent"].document.getElementsByTag("a");
	for(var m=0;m<k.count();m++){
		var obj = k.getItem(m);
		var hrefurl = $(obj).attr("href");
		var sitedomain = $("#sitedomain").val();
//		if(hrefurl.indexOf(sitedomain)==-1){
//			alertWarn("请勿使用本商城以外的超链接！");
//			return 0;
//		}
	}
	var skupropnum = $("#skupropnum").val();
	
	//alert(JSON.stringify(skumap));
	propdrr = setprodpropmap();
	var json = {};
	json = papreJsonDate();
	var url="savegoods";
	processPrompt("In processing...");
	$("input[value='Save']").attr("disabled",'true');
	$.ajax({
			type : "POST",
		//contentType : 'charset=utf-8',
		url : url,
		dataType : 'json',
		cache : false,
		data : {
			json : JSON.stringify(json),
			skuvalue:JSON.stringify(skuvalue),
			skuattr:JSON.stringify(skuattr),
			propdrr:JSON.stringify(propdrr),
			colorenumid:colorenumid,
			operation:operation,
			skupropnum:skupropnum
		},
		success : function(data) {
            if(data.code != undefined && data.code != "") {
                // 关闭loading层
                $(".shade_layout").hide();
                $("#open_returnPop").css("display","none");
                $('#open_returnPop').empty().html("");

                alertWarn(data.result);
                return;
            }
			var jsonObjArr = JSON.parse(data);
			if(jsonObjArr.code=="1"){
				friendlyPrompt("Publish product success!");
				$("input[value='Save']").removeAttr("disabled");
				window.scroll(0, 300);
				//self.location="init";
			}
			else if(jsonObjArr.code=="2"){
				self.location="proviewpro?prodId="+jsonObjArr.prodId;
			}
			else if(jsonObjArr.code=="3"){
				$("input[value='Save']").removeAttr("disabled");
				alertWarn(jsonObjArr.result);
			}
			else if(jsonObjArr.code=="-1"){
				alertWarn(jsonObjArr.result);
			}
			else{
				alertWarn("Fail operation,please contact system administrator!");
				$("input[value='Save']").removeAttr("disabled");
			}
		}
	});
}