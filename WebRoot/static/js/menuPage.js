var map  = new Map(); 
$(function(){
	 var result = getUrlParam("result");
	 if(result == "1" || result == "3"){
		 alert("The dish status in the shopping cart has been changed. Please resubmit the order. ");
	 }
	  var cartJson = JSON.parse(localStorage.getItem("cart"));
	  $.post("cartinfo",{"json":JSON.stringify(cartJson)},function(json){
		  if(json!=""){
			  cartJson = JSON.parse(json);
			  localStorage.setItem("cart", JSON.stringify(cartJson));
		  }
		  initData(cartJson);
	  });
	  
	  $('.cartIcon').on('touchend',function(){
		  	var cartItem = localStorage.getItem("cart");
		  	if(cartItem==null || cartItem=="[]"){
		  		return;
		    }
		    var cartJson = JSON.parse(cartItem);
		    initData(cartJson);
	      	$(".shade_layout").show();
	      	$(".cartarea").show();
	      	$(".cartarea").addClass("cart_area_show");
	      	$("body").addClass("scorll");
	   });
	  
	  $(document).delegate(".cnums .add","touchend",function(){ 
		    // $('.nums .add').on('touchend',function(){
    	 var prodSkuId = $(this).parent().find("input[name='prodSkuId']").val();
		 var num = parseInt($(this).parent().find("span").text())+1;
		 var cart = parseInt($(".total").text()) + 1;
		 var prodPrice = $(this).parent().find("input[name='price']").val();
		 var totalPrice = parseInt($("#tprice").val()) + parseInt(prodPrice);
		 var prodId = $(this).parent().find("input[name='prodId']").val();
		 var prodName = $(this).parent().parent().find("div[class='textDesc'] h2").text();
		 //$(this).parent().find("span").text(num); 
		 var $prodDiv = $(".foodArea").find("div[id='p"+prodId+"']");
		 for(var i in $prodDiv){
			 var totalNum = parseInt($prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).text());
			 $prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).text(totalNum+1);
			 if(num==1){
				  $prodDiv.eq(i).find("div[class='nums']").find("img").eq(0).show();
				  $prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).show();
		   	 }
		 }
		 var priceManner = $(".foodArea").find("div[id='p"+prodId+"']").find("input[name='priceManner']").val();
		 if(priceManner == "2" && num>1){
			return;
		 }
		 $(this).parent().find("span").eq(0).text(num);
		 if(cart==1){
			 $(".total").show();
		 }
		 $(".total").text(cart);
		 $("#totalPrice").text(format_number(totalPrice));
		 $("#tprice").val(totalPrice);
		 
		 var onePrice = parseInt(prodPrice) * num;
		 $(this).parent().parent().find("p[class='cprice']").text("Rp "+format_number(onePrice));
		 var cartJson = JSON.parse(localStorage.getItem("cart"));
		 var totalJson = JSON.parse(localStorage.getItem("num"));
		 if(prodSkuId!=""){
			 for(var i in cartJson){
				 if(cartJson[i].prodId==prodId && cartJson[i].prodSkuId==prodSkuId){
					 cartJson[i].prodNum++;
				 }
			 }
    	 }
		 else{
			 for(var i in cartJson){
				 if(cartJson[i].prodId==prodId){
					 cartJson[i].prodNum++;
				 }
			 }
		 }
		 for(var i in totalJson){
			 if(totalJson[i].prodId==prodId){
				 totalJson[i].totalNum++;
			 } 
		 }
		 localStorage.setItem("cart", JSON.stringify(cartJson));
		 localStorage.setItem("num", JSON.stringify(totalJson));
     });
     $(document).delegate(".cnums .reduce","touchend",function(){ 
     //$('.nums .reduce').on('touchend',function(){
    	 var prodSkuId = $(this).parent().find("input[name='prodSkuId']").val();
   	  var num = parseInt($(this).parent().find("span").eq(0).text())-1;
   	  var cart = parseInt($(".total").text()) - 1;
   	  var totalPrice = parseInt($("#tprice").val()) - parseInt($(this).parent().find("input[name='price']").val());
   	  // $(this).parent().find("span").text(num); 
   	  
   	  var prodId = $(this).parent().find("input[name='prodId']").val();
   	  var $prodDiv = $(".foodArea").find("div[id='p"+prodId+"']");
	  for(var i in $prodDiv){
		  var totalNum = parseInt($prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).text())-1;
		  $prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).text(totalNum);
		  if(totalNum==0){
			  $prodDiv.eq(i).find("div[class='nums']").find("img").eq(0).hide();
			  $prodDiv.eq(i).find("div[class='nums']").find("span").eq(0).hide();
	  	  }
	   	  if(cart==0){
	   		$(".total").hide();
	   	  }
	  }
	  if(num==0){
		 $(this).parent().parent().remove();
  	  }
	  else{
		  $(this).parent().find("span").eq(0).text(num);
	  }
	  var prodPrice = $(this).parent().find("input[name='price']").val();
	  var onePrice = parseInt(prodPrice) * num;
	  $(this).parent().parent().find("p[class='cprice']").text("Rp "+format_number(onePrice));
	  var cartJson = JSON.parse(localStorage.getItem("cart"));
   	  var totalJson = JSON.parse(localStorage.getItem("num"));
   	  if(prodSkuId!=""){
		 for(var i in cartJson){
			 if(cartJson[i].prodId==prodId && cartJson[i].prodSkuId==prodSkuId){
				 if(cartJson[i].prodNum>1){
		 			cartJson[i].prodNum--;
		 			flag = true;
	 			 }
	 			 else if(cartJson[i].prodNum==1){
	 				cartJson.splice(i,1);
	 			 }
			 }
		 }
	  }
	  else{
		 for(var i in cartJson){
			 if(cartJson[i].prodId==prodId){
				 if(cartJson[i].prodNum>1){
		 			cartJson[i].prodNum--;
		 			flag = true;
	 			 }
	 			 else if(cartJson[i].prodNum==1){
	 				cartJson.splice(i,1);
	 			 }
			 }
		 }
	  }
	  for(var i in totalJson){
		 if(totalJson[i].prodId==prodId){
			 if(totalJson[i].totalNum>1){
 	 			totalJson[i].totalNum--;
 	 			flag = true;
  			}
  			else if(totalJson[i].totalNum==1){
  				totalJson.splice(i,1);
  			}
		 } 
	  }
	  
   	 localStorage.setItem("cart", JSON.stringify(cartJson));
   	 localStorage.setItem("num", JSON.stringify(totalJson));
   	  $(".total").text(cart);
   	  $("#totalPrice").text(format_number(totalPrice));
	  $("#tprice").val(totalPrice);
     });
});
function initData(cartJson){
//	  var cartJson = JSON.parse(localStorage.getItem("cart"));
	  var totalJson = JSON.parse(localStorage.getItem("num"));
	  var html = "";
	  var total = 0;
	  var totalPrice = 0;
	  for(var i in totalJson){
		  map.put(totalJson[i].prodId, totalJson[i].totalNum);
	  }
	  for(var i in cartJson){
		  
	      total += cartJson[i].prodNum;
	      totalPrice += (parseInt(cartJson[i].price) * cartJson[i].prodNum);
	      var skuinfos = cartJson[i].skuinfos;
	      if(skuinfos == null){
	    	  skuinfos = "";
	      }
	      var $cartDiv = $(".foodArea").find("div[id='p"+cartJson[i].prodId+"']");
//		  if($cartDiv.length>1){
		  for(var k in $cartDiv){
			  $cartDiv.eq(k).find("div[class='nums'] span").eq(0).text(map.get(cartJson[i].prodId));
			  $cartDiv.eq(k).find("div[class='nums'] span").eq(0).show();
			  $cartDiv.eq(k).find("div[class='nums'] img").eq(0).show();
		  }
//		  }
//		  else{
//			  $cartDiv.find("div[class='nums'] span").eq(0).text(cartJson[i].prodNum);
//			  $cartDiv.find("div[class='nums'] span").eq(0).show();
//			  $cartDiv.find("div[class='nums'] img").eq(0).show();
//		  }
	      var prodSkuId = cartJson[i].prodSkuId;
	      if(prodSkuId == null){
	    	  prodSkuId = "";
	      }
	      var priceFormat = format_number(cartJson[i].price*cartJson[i].prodNum);
		  html += '<div class="cart_one" prodid='+cartJson[i].prodId+'>'
		        + '<div class="fl">'
		        + '<h2>'+cartJson[i].prodName+'</h2>'
		        + '<p class="skus">'+skuinfos+'</p>'
		        + '</div>'
		        + '<div class="fl">'
		        + '<p class="cprice">Rp '+priceFormat+'</p>'
		        + '</div>'
		        + '<div class="cnums">'
		        + '<input type="hidden" name="prodId" value="'+cartJson[i].prodId+'">'
		        + '<input type="hidden" name="price" value="'+cartJson[i].price+'">'
		        + '<input type="hidden" name="prodSkuId" value="'+prodSkuId+'">'
		        + '<img class="reduce fl" alt="" src="/restaurant/static/images/food/btn_reduce.png">'
		        + '<span>'+cartJson[i].prodNum+'</span>'
		        + '<img class="add" alt="" src="/restaurant/static/images/food/btn_add.png">'
		        + '</div>'
		        + '</div>';
	  }
	  if(total>0){
		  $(".total").show();
	  }
	  $(".cart_content").html(html);
	  $(".total").text(total);
	  $("#totalPrice").text(format_number(totalPrice));
	  $("#tprice").val(totalPrice);
}

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