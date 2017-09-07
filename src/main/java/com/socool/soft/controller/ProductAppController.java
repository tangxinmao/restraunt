package com.socool.soft.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socool.soft.bo.RcAppProdHot;
import com.socool.soft.bo.RcCity;
import com.socool.soft.bo.RcFileInfo;
import com.socool.soft.bo.RcImageSpace;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcOrderProdEvaluation;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdPropInfo;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.RcProdSkuPropEnum;
import com.socool.soft.bo.RcProdSkuPropInfo;
import com.socool.soft.bo.constant.ImageSpaceTypeEnum;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.service.IAppProdHotService;
import com.socool.soft.service.ICityService;
import com.socool.soft.service.IFileInfoService;
import com.socool.soft.service.IImageSpaceService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProductAppService;
import com.socool.soft.service.IProductManagerService;
import com.socool.soft.util.JsonUtil;
import com.socool.soft.util.VOConversionUtil;
import com.socool.soft.util.ViewUtil;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.ProductPublishedDTO;
import com.socool.soft.vo.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 商品APP接口服务
 *
 * @author yh
 */
@Controller
@RequestMapping(value = "/prod")
public class ProductAppController extends BaseController {
    @Autowired
    private IProdService prodService;
    @Autowired
    private IProductAppService productAppService;
    @Autowired
    private IMerchantService merchantService;

    @Autowired
    private ICityService cityService;
    @Autowired
    private IAppProdHotService appProdHotService;
    @Autowired
    private PropertyConstants propertyConstants;
    @Autowired
    private IFileInfoService fileInfoService;
    @Autowired
    private IImageSpaceService imageSpaceService;
    @Autowired
    private IProductManagerService productManagerService;

    private final static Log log = LogFactory.getLog(ProductManagerController.class);

    /**
     * 商品详情APP接口
     *
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public String prodDetail(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException, JsonProcessingException {
        Date date = new Date();
        log.debug("===开始时间:" + ViewUtil.dateFormat(date));
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
        int prodId = json.getInt("prodId");
        int buyerId = 0;
        if (json.containsKey("memberId")) {
            buyerId = json.getInt("memberId");
        }
        String cityName = json.getString("city");
//		int prodId = 12;
//		int memberId = 0;
//		String city = "Bandung"; 
        RcCity city = cityService.findCityByName(cityName);
        RcProd prod = productAppService.findProdForAndroid(prodId, city.getCityId(), buyerId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        String result = objectMapper.writeValueAsString(prod);
        //String result = JsonUtil.toJson(prod);
        Date date1 = new Date();
        log.debug("===结束时间:" + ViewUtil.dateFormat(date1));
        long time = date1.getTime() - date.getTime();
        log.debug("=====耗时：" + time);
        return "{\"code\":\"1\",\"result\":\"" + time + "\",\"data\":" + result + "}";
    }

    /**
     * 商品详情描述html
     *
     * @param model
     * @param prodId 商品ID
     * @return
     */
    @RequestMapping(value = "/content")
    public String DetailContent(Model model, int prodId) {
        RcProd product = prodService.findProdById(prodId);
        model.addAttribute("html", product.getDetail());
        return "detailContent";
    }

    /**
     * 获取SKU信息
     *
     * @return
     */
    @RequestMapping(value = "/skuinfo")
    @ResponseBody
    public String getSkuInfo(String data, String timestamp, String nonceStr, String product, String signature) {
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
//		 String jsonStr = "{\"prodId\": \"12\",\"city\":\"Jakarta Barat\",\"prodSkuPropInfo\":[{\"prodPropId\":\"3\",\"prodPropEnumId\":\"2\"},{\"prodPropId\":\"4\",\"prodPropEnumId\":\"5\"}]}";
//					JSONObject json = JSONObject.fromObject(jsonStr);
        int prodId = json.getInt("prodId");
        JSONArray prodSkuPropInfos = json.getJSONArray("prodSkuPropInfo");
        List<Integer> prodSkuPropEnumIds = new ArrayList<Integer>();
        for (Object prodSkuPropInfo : prodSkuPropInfos) {
            JSONObject jo = JSONObject.fromObject(prodSkuPropInfo);
            prodSkuPropEnumIds.add(jo.getInt("prodPropEnumId"));
        }
        RcProdSku prodSku = productAppService.findSelectedProdSku(prodId, prodSkuPropEnumIds);
        String result = JsonUtil.toJson(prodSku);
        return "{\"code\":\"1\",\"result\":\"成功！\",\"data\":" + result + "}";
    }

    /**
     * 商品规格参数接口
     *
     * @return
     */
    @RequestMapping(value = "/propomfpinfo")
    @ResponseBody
    public String propomfpinfo(String data, String timestamp, String nonceStr, String product, String signature) {
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
        int prodId = json.getInt("prodId");
        List<RcProdPropInfo> infos = productAppService.findProdPropInfosByProdId(prodId);
        String result = JsonUtil.toJson(infos);
        return "{\"code\":\"1\",\"result\":\"成功！\",\"data\":" + result + "}";
    }

    /**
     * 商品评论列表
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/evaluationlist")
    @ResponseBody
    public String evaluationList(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException, JsonProcessingException {
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
        int prodId = json.getInt("prodId");
        Integer prodSkuPropEnumId = null;
        try {
            prodSkuPropEnumId = json.getInt("prodPropEnumId");
        } catch (Exception e) {
        }
        Integer currentPage = null;
        try {
            currentPage = json.getInt("currentPage");
        } catch (Exception e) {
            currentPage = 1;
        }
        // 分页
        Page page = new Page();
        // 初始化时第一页
        page.setPagination(true);
        page.setPageSize(10);
        page.setCurrentPage(currentPage);
        List<RcOrderProdEvaluation> list = productAppService.findPagedOrderProdEvaluationsByProdIdAndPackageServiceSkuPropEnumId(prodId, prodSkuPropEnumId, page);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        String resultStr = objectMapper.writeValueAsString(list);

        return "{\"code\":\"1\",\"result\":\"成功！\",\"data\":" + resultStr + "}";
    }

    /**
     * 获取供应商详情
     *
     * @return
     */
    @RequestMapping(value = "/merchantinfo")
    @ResponseBody
    public String merchantinfo(String data, String timestamp, String nonceStr, String product, String signature) {
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
        int prodId = json.getInt("prodId");
        RcMerchant merchant = merchantService.findMerchantByProdId(prodId);
        VOConversionUtil.Entity2VO(merchant, new String[]{"merchantId", "name", "description", "logoUrl"}, null);
        String result = JsonUtil.toJson(merchant);
        return "{\"code\":\"1\",\"result\":\"成功！\",\"data\":" + result + "}";
    }

    /**
     * 获取施工队sku
     *
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/applicatorsku")
    @ResponseBody
    public Result<List<RcProdSkuPropEnum>> applicatorSku(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException {
        Result<List<RcProdSkuPropEnum>> result = new Result<List<RcProdSkuPropEnum>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int prodId = json.getInt("prodId");
        List<RcProdSkuPropEnum> list = productAppService.findServiceSkuPropEnums(prodId);
        result.setData(list);
        return result;
    }

    /**
     * 2.5. 添加/移除热门商品
     *
     * @return
     */
    @RequestMapping(value = "/edithot")
    @ResponseBody
    public Result<Map<String, Object>> edithot(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        Integer prodId = null;
        try {
            prodId = json.getInt("prodId");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("prodId不能为空！");
            return result;
        }
        Integer action = null;
        try {
            action = json.getInt("action");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("action不能为空！");
            return result;
        }
        RcAppProdHot appProdHot = new RcAppProdHot();
        RcProd prod = prodService.findProdById(prodId);
        appProdHot.setMerchantId(prod.getMerchantId());
        appProdHot.setProdId(prodId);
        if (action == 1) {
            appProdHot.setSeq(1);

            appProdHotService.addAppProdHot(appProdHot);
        } else if (action == 2) {
            appProdHotService.removeAppProdHots(appProdHot);
        }
        return result;
    }

    /**
     * 2.6. 设置商品状态
     *
     * @return
     */
    @RequestMapping(value = "/editstatus")
    @ResponseBody
    public Result<Map<String, Object>> editStatus(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        Integer prodId = null;
        try {
            prodId = json.getInt("prodId");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("prodId不能为空！");
            return result;
        }
        Integer status = null;
        try {
            status = json.getInt("status");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("status");
            return result;
        }
        RcProd prod = new RcProd();
        prod.setProdId(prodId);
        prod.setStatus(status);
        prodService.modifyProd(prod);
        return result;
    }

    /**
     * 2.6. 设置商品状态
     *
     * @return
     */
    @RequestMapping(value = "/appremove")
    @ResponseBody
    public Result<Map<String, Object>> appremove(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        Integer prodId = null;
        try {
            prodId = json.getInt("prodId");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("prodId不能为空！");
            return result;
        }
        Integer delFlag = null;
        try {
            delFlag = json.getInt("delFlag");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("delFlag不能为空！");
            return result;
        }
        RcProd prod = new RcProd();
        prod.setProdId(prodId);
        prod.setDelFlag(delFlag.byteValue());
        prodService.modifyProd(prod);
        return result;
    }

    /**
     * 商品图片上传
     *
     * @param request
     * @param product
     * @param data
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload")
    @ResponseBody
    public Result<Map<String, String>> uploadImage(HttpServletRequest request, String data, String timestamp, String nonceStr, String product, String signature) throws Exception {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        Integer merchantUserId = null;
        try {
            merchantUserId = json.getInt("merchantUserId");
        } catch (Exception e) {
            result.setCode("0");
            result.setResult("merchantUserId不能为空！");
            return result;
        }
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Iterator<String> iter = multipartRequest.getFileNames();
            while (iter.hasNext()) {
                MultipartFile multipartFile = multipartRequest.getFile(iter.next());
                if (multipartFile != null) {
                    String fileName = multipartFile.getOriginalFilename();
                    if (StringUtils.isNotBlank(fileName)) {
                        String imgUrl = "";
                        if (multipartFile.getSize() > 5 * 1024 * 1024) {
                            result.setCode("0");
                            result.setResult("图片大小不能大于5M！");
                            return result;
                        } else {
                            String fileInfoId = UUID.randomUUID().toString().replaceAll("-", "");
                            int pos = fileName.lastIndexOf(".");
                            String originalName = fileName.substring(0, pos);
                            String suffixName = fileName.substring(pos + 1, fileName.length());
                            String realFileName = fileInfoId;
                            Calendar cal = Calendar.getInstance();
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH) + 1;
                            int day = cal.get(Calendar.DATE);
                            String relativePath = year + File.separator + month + File.separator + day + File.separator;
                            String dirPath = propertyConstants.systemFileServerRoot + relativePath;
                            File dir = new File(dirPath);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dirPath + realFileName + "." + suffixName);
                            multipartFile.transferTo(file);
                            RcFileInfo fileInfo = new RcFileInfo();
                            fileInfo.setFileInfoId(fileInfoId);
                            fileInfo.setOriginalName(originalName);
                            fileInfo.setRealName(realFileName);
                            fileInfo.setSuffixName(suffixName);
                            fileInfo.setSize(multipartFile.getSize());
                            fileInfo.setRelativePath(relativePath);
                            fileInfo.setMemberId(merchantUserId);
                            fileInfoService.addFileInfo(fileInfo);

                            RcImageSpace dirImageSpaceParam = new RcImageSpace();
                            dirImageSpaceParam.setMemberId(merchantUserId);
                            dirImageSpaceParam.setParentId(0l);
                            dirImageSpaceParam.setType(ImageSpaceTypeEnum.FOLDER.getValue());
                            dirImageSpaceParam.setName(format.format(new Date()));
                            long parentId = 0;
                            RcImageSpace dirImageSpace = imageSpaceService.findImageSpace(dirImageSpaceParam);
                            if (dirImageSpace == null) {
                                parentId = imageSpaceService.addImageSpace(dirImageSpaceParam);
                            } else {
                                parentId = dirImageSpace.getImageSpaceId();
                            }

                            RcImageSpace imageSpace = new RcImageSpace();
                            imageSpace.setFileInfoId(fileInfoId);
                            imageSpace.setMemberId(merchantUserId);
                            imageSpace.setParentId(parentId);
                            imageSpace.setType(ImageSpaceTypeEnum.IMAGE.getValue());
                            imageSpace.setName(originalName + "." + suffixName);
                            imageSpace.setImgSize(fileInfo.getSize());
                            try {
                                BufferedImage image = ImageIO.read(new FileInputStream(file));
                                if (image != null) {
                                    imageSpace.setImgWidth(image.getWidth());
                                    imageSpace.setImgHeight(image.getHeight());
                                }
                            } catch (IOException e) {
                            }
                            imageSpaceService.addImageSpace(imageSpace);

                            imgUrl = propertyConstants.systemFileServerUrl + fileInfo.getPath();
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("imgUrl", imgUrl);
                            result.setData(map);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 2.8. 发布商品
     *
     * @return
     */
    @RequestMapping(value = "/publish")
    @ResponseBody
    public Result<Map<String, Object>> publish(String data, String timestamp, String nonceStr, String product, String signature)
            throws UnsupportedEncodingException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
//		String a = "{\"name\":\"testapp\",\"ad\":\"testbbbb\",\"price\":100,\"prodCatId\":66,\"priceManner\":1,\"status\":1,\"isHot\":1,\"prodImgUrl\":\"xx\",\"merchantId\":51,\"skus\":[{\"prodSkuPropVal\":\"sku1\",\"price\":100},{\"prodSkuPropVal\":\"sku2\",\"price\":200}]}";
//		JSONObject json = JSONObject.fromObject(a);
        ProductPublishedDTO productPublishedDTO = new ProductPublishedDTO();
        productPublishedDTO.setBaseProdId(0);
        //true：新增操作；false:修改操作
        productPublishedDTO.setIfAdded(true);
        //true：APP操作；false:后台操作
        productPublishedDTO.setIfAPP(true);
        //商品名称
        productPublishedDTO.setProdName(json.getString("name"));
        //广告词
        productPublishedDTO.setProdAdword(json.getString("ad"));
        //是否设置为热门商品
        productPublishedDTO.setIsHot(json.getInt("isHot"));
        //计价方式
        productPublishedDTO.setPriceManner(json.getInt("priceManner"));
        //单位
        if (productPublishedDTO.getPriceManner().equals(2)) {
            productPublishedDTO.setProdMeasureUnit(json.getString("prodMeasureUnit"));
            productPublishedDTO.setProdMeasureUnitCount(json.getInt("prodMeasureUnitCount"));//数量
        }
            //当前价
            productPublishedDTO.setProdPrice(new BigDecimal(json.getString("price")));
            //商品类型
            productPublishedDTO.setProductType(1);
            List<String> prodimgs = new ArrayList<String>();
            prodimgs.add(json.getString("prodImgUrl"));
            //商品图片
            productPublishedDTO.setProdImgUrls(prodimgs);
            //品类ID
            productPublishedDTO.setProdCatId(json.getInt("prodCatId"));
            //商品状态
            productPublishedDTO.setStatus(json.getInt("status"));
            //商户ID
            int merchantId = json.getInt("merchantId");
            productPublishedDTO.setMerchantId(merchantId);
            //商品品牌ID
            productPublishedDTO.setProdBrandId(0);
            //其他品牌名称
            productPublishedDTO.setProdBrandName("");
            List<RcProdPropInfo> prodPropInfos = new ArrayList<RcProdPropInfo>();
            productPublishedDTO.setProdPropInfos(prodPropInfos);
            JSONArray skus = new JSONArray();
            if (json.containsKey("skus")) {
                skus = json.getJSONArray("skus");
            }
            List<RcProdSku> prodSkus = new ArrayList<RcProdSku>();
            Map<Integer, List<RcProdSkuPropInfo>> prodSkuPropInfoMap = new HashMap<Integer, List<RcProdSkuPropInfo>>();
            Map<Integer, List<String>> prodSkuImgUrlsMap = new HashMap<Integer, List<String>>();
            int i = 0;
            for (Object o : skus) {
                JSONObject jsonO = JSONObject.fromObject(o);
                List<RcProdSkuPropInfo> prodSkuPropInfo = new ArrayList<RcProdSkuPropInfo>();
                RcProdSku prodSku = new RcProdSku();
                prodSku.setPrice(new BigDecimal(jsonO.getString("price")));
                RcProdSkuPropInfo info = new RcProdSkuPropInfo();
                info.setProdPropVal(jsonO.getString("prodSkuPropVal"));
                prodSkus.add(prodSku);
                prodSkuPropInfo.add(info);
                prodSkuPropInfoMap.put(i, prodSkuPropInfo);
                i++;
            }
            //添加SKU属性
            productPublishedDTO.setProdSkus(prodSkus);
            productPublishedDTO.setProdSkuPropInfoMap(prodSkuPropInfoMap);
            productPublishedDTO.setProdSkuImgUrlsMap(prodSkuImgUrlsMap);
            productManagerService.publishProduct(productPublishedDTO);
            return result;
        }

        /**
         * 2.10. 编辑商品
         * @return
         */
        @RequestMapping(value = "/modify")
        @ResponseBody
        public Result<Map<String, Object>> modify (String data, String timestamp, String nonceStr, String
        product, String signature)
			throws UnsupportedEncodingException {
            Result<Map<String, Object>> result = new Result<Map<String, Object>>();
            JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
            if (json == null) {
                result.setCode("-1");
                result.setResult("token验证失败！");
                return result;
            }
//		String a = "{\"name\":\"testapp\",\"ad\":\"testbbbb\",\"price\":100,\"prodCatId\":66,\"priceManner\":1,\"status\":1,\"isHot\":1,\"prodImgUrl\":\"xx\",\"merchantId\":51,\"skus\":[{\"prodSkuPropVal\":\"sku1\",\"price\":100},{\"prodSkuPropVal\":\"sku2\",\"price\":200}]}";
//		JSONObject json = JSONObject.fromObject(a);
            ProductPublishedDTO productPublishedDTO = new ProductPublishedDTO();
            productPublishedDTO.setBaseProdId(0);
            //true：新增操作；false:修改操作
            productPublishedDTO.setIfAdded(false);
            //true：APP操作；false:后台操作
            productPublishedDTO.setIfAPP(true);
            //商品ID
            productPublishedDTO.setProdId(json.getInt("prodId"));
            //商品名称
            productPublishedDTO.setProdName(json.getString("name"));
            //广告词
            productPublishedDTO.setProdAdword(json.getString("ad"));
            //是否设置为热门商品
            productPublishedDTO.setIsHot(json.getInt("isHot"));
            //商品状态
            if(json.containsKey("status"))
            productPublishedDTO.setStatus(json.getInt("status"));
            //计价方式
            productPublishedDTO.setPriceManner(json.getInt("priceManner"));
            if (productPublishedDTO.getPriceManner().equals(2)) {
                productPublishedDTO.setProdMeasureUnit(json.getString("prodMeasureUnit"));
                productPublishedDTO.setProdMeasureUnitCount(json.getInt("prodMeasureUnitCount"));//数量
            }
            //当前价
            productPublishedDTO.setProdPrice(new BigDecimal(json.getString("price")));
            //商品类型
            productPublishedDTO.setProductType(1);
            List<String> prodimgs = new ArrayList<String>();
            prodimgs.add(json.getString("prodImgUrl"));
            //商品图片
            productPublishedDTO.setProdImgUrls(prodimgs);
            //品类ID
            productPublishedDTO.setProdCatId(json.getInt("prodCatId"));
            //商户ID
            int merchantId = json.getInt("merchantId");
            productPublishedDTO.setMerchantId(merchantId);
            //商品品牌ID
            productPublishedDTO.setProdBrandId(0);
            //其他品牌名称
            productPublishedDTO.setProdBrandName("");
            List<RcProdPropInfo> prodPropInfos = new ArrayList<RcProdPropInfo>();
            productPublishedDTO.setProdPropInfos(prodPropInfos);
            JSONArray skus = new JSONArray();
            if (json.containsKey("skus")) {
                skus = json.getJSONArray("skus");
            }
            List<RcProdSku> prodSkus = new ArrayList<RcProdSku>();
            Map<Integer, List<RcProdSkuPropInfo>> prodSkuPropInfoMap = new HashMap<Integer, List<RcProdSkuPropInfo>>();
            Map<Integer, List<String>> prodSkuImgUrlsMap = new HashMap<Integer, List<String>>();
            int i = 0;
            for (Object o : skus) {
                JSONObject jsonO = JSONObject.fromObject(o);
                List<RcProdSkuPropInfo> prodSkuPropInfo = new ArrayList<RcProdSkuPropInfo>();
                RcProdSku prodSku = new RcProdSku();
                prodSku.setPrice(new BigDecimal(jsonO.getString("price")));
                RcProdSkuPropInfo info = new RcProdSkuPropInfo();
                info.setProdPropVal(jsonO.getString("prodSkuPropVal"));
                prodSkus.add(prodSku);
                prodSkuPropInfo.add(info);
                prodSkuPropInfoMap.put(i, prodSkuPropInfo);
                i++;
            }
            //添加SKU属性
            productPublishedDTO.setProdSkus(prodSkus);
            productPublishedDTO.setProdSkuPropInfoMap(prodSkuPropInfoMap);
            productPublishedDTO.setProdSkuImgUrlsMap(prodSkuImgUrlsMap);
            productManagerService.publishProduct(productPublishedDTO);
            return result;
        }
        /**
         * 2.9. 获取商品详情
         * @return
         */
        @RequestMapping(value = "/getinfo")
        @ResponseBody
        public Result<Map<String, Object>> getinfo (String data, String timestamp, String nonceStr, String
        product, String signature)
			throws UnsupportedEncodingException {
            Result<Map<String, Object>> result = new Result<Map<String, Object>>();
            JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
            if (json == null) {
                result.setCode("-1");
                result.setResult("token验证失败！");
                return result;
            }
            Integer prodId = null;
            try {
                prodId = json.getInt("prodId");
            } catch (Exception e) {
                result.setCode("0");
                result.setResult("prodId不能为空！");
                return result;
            }
            Map<String, Object> map = productAppService.findProdForPad(prodId);
            result.setData(map);
            return result;
        }
    }
