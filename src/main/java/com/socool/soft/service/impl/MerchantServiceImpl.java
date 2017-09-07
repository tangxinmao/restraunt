package com.socool.soft.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.zxing.WriterException;
import com.socool.soft.bo.RcCity;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantQRCode;
import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdSkuProp;
import com.socool.soft.bo.constant.MerchantMealStyleEnum;
import com.socool.soft.bo.constant.OrderSellerStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.dao.RcMerchantMapper;
import com.socool.soft.dao.RcMerchantQRCodeMapper;
import com.socool.soft.service.ICityService;
import com.socool.soft.service.IMerchantSectionService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.service.IOrderService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProdSkuPropService;
import com.socool.soft.service.IVendorService;
import com.socool.soft.util.QRCodeUtil;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;

@Service
public class MerchantServiceImpl implements IMerchantService {
	
	@Autowired
	private IMerchantUserService merchantUserService;
	
	@Autowired
	private IProdService prodService;
	@Autowired
	private IProdSkuPropService prodskuPropService;
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private RcMerchantMapper merchantMapper;
	@Autowired
	private RcMerchantQRCodeMapper merchantQCCodeMapper;
	@Autowired
	private IVendorService vendorService;
	@Autowired
	private ICityService cityService;
	@Autowired
	private PropertyConstants propertyConstants;
	@Autowired
	private IMerchantSectionService merchantSectionService;

	@Override
	public List<RcMerchant> findAllEnabledMerchants() {
		RcMerchant param = new RcMerchant();
		param.setDelFlag(YesOrNoEnum.NO.getValue());
		return merchantMapper.select(param);
	}

	@Override
	public RcMerchant findMerchantById(int merchantId) {
		return merchantMapper.selectByPrimaryKey(merchantId);
	}

	@Override
	public int addMerchant(RcMerchant merchant) {
		merchant.setCreateTime(new Date());
		if(merchantMapper.insert(merchant) > 0) {
//			RcProdSkuProp tasteProdSkuProp = new RcProdSkuProp();
//			tasteProdSkuProp.setName("Rasa");
//			tasteProdSkuProp.setMerchantId(merchant.getMerchantId());
//			tasteProdSkuProp.setHasImg(YesOrNoEnum.NO.getValue());
//			tasteProdSkuProp.setIsPackService(YesOrNoEnum.NO.getValue());
//			prodskuPropService.addProdSkuProp(tasteProdSkuProp);
			RcProdSkuProp specProdSkuProp = new RcProdSkuProp();
			specProdSkuProp.setName("Spesifikasi");
			specProdSkuProp.setMerchantId(merchant.getMerchantId());
			specProdSkuProp.setHasImg(YesOrNoEnum.NO.getValue());
			specProdSkuProp.setIsPackService(YesOrNoEnum.NO.getValue());
			prodskuPropService.addProdSkuProp(specProdSkuProp);
			return merchant.getMerchantId();
		}
		return 0;
	}

	@Override
	public List<RcMerchant> findAllMerchants() {
		RcMerchant param = new RcMerchant();
		return merchantMapper.select(param);
	}

	@Override
	public List<RcMerchant> findPagedMerchants(RcMerchant param, Page page) {
		if(StringUtils.isNotBlank(param.getCityName())) {
			List<RcCity> cities = cityService.findCitiesByName(param.getCityName());
			if(CollectionUtils.isEmpty(cities)) {
				return new ArrayList<RcMerchant>();
			}
			List<Integer> cityIds = new ArrayList<Integer>();
			for(RcCity e : cities) {
				cityIds.add(e.getCityId());
			}
			param.setCityIds(cityIds);
		}
		PageContext.setPage(page);
		List<RcMerchant> merchants = null;
		if(param.getMerchantId() != null) {
			RcMerchant merchant = findMerchantById(param.getMerchantId());
			if(merchant == null) {
				merchants = new ArrayList<RcMerchant>();
			} else {
				merchants = Arrays.asList(merchant);
			}
		} else {
			merchants = merchantMapper.select(param);
		}
		for(RcMerchant merchant : merchants) {
			merchant.setMember(merchantUserService.findMerchantSuperAdmin(merchant.getMerchantId()));
			merchant.setVendor(vendorService.findVendorById(merchant.getVendorId()));
			merchant.setCity(cityService.findCityById(merchant.getCityId()));
			merchant.setHasQRCodes(CollectionUtils.isEmpty(findMerchantQRCodesByMerchantId(merchant.getMerchantId())) ? (byte)0 : (byte)1);
		}
		return merchants;
	}

	@Override
	public List<RcMerchant> findMerchants(RcMerchant param) {
		if(StringUtils.isNotBlank(param.getCityName())) {
			List<RcCity> cities = cityService.findCitiesByName(param.getCityName());
			if(CollectionUtils.isEmpty(cities)) {
				return new ArrayList<RcMerchant>();
			}
			List<Integer> cityIds = new ArrayList<Integer>();
			for(RcCity e : cities) {
				cityIds.add(e.getCityId());
			}
			param.setCityIds(cityIds);
		}
		List<RcMerchant> merchants = null;
		if(param.getMerchantId() != null) {
			RcMerchant merchant = findMerchantById(param.getMerchantId());
			if(merchant == null) {
				merchants = new ArrayList<RcMerchant>();
			} else {
				merchants = Arrays.asList(merchant);
			}
		} else {
			merchants = merchantMapper.select(param);
		}
		for(RcMerchant merchant : merchants) {
			merchant.setMember(merchantUserService.findMerchantSuperAdmin(merchant.getMerchantId()));
			merchant.setVendor(vendorService.findVendorById(merchant.getVendorId()));
			merchant.setCity(cityService.findCityById(merchant.getCityId()));
		}
		return merchants;
	}

	@Override
	public int modifyMerchant(RcMerchant merchant) {
		if(merchantMapper.updateByPrimaryKey(merchant) > 0) {
			return merchant.getMerchantId();
		}
		return 0;
	}

	@Override
	public int removeMerchant(int merchantId) {
		// todo: 存在上架商品、未处理订单时不能禁用
		RcMerchant merchant = findMerchantById(merchantId);
		merchant.setDelFlag(YesOrNoEnum.YES.getValue());
		if(modifyMerchant(merchant) > 0) {
			List<RcMerchantUser> merchantUsers = merchantUserService.findMerchantUsersByMerchantId(merchant.getMerchantId());
			for(RcMerchantUser merchantUser : merchantUsers) {
				merchantUserService.removeMerchantUser(merchantUser.getMemberId());
			}
			return 1;
		}
		return 0;
	}

//	@Override
//	public List<List<RcProdBrand>> findMerchantBrands(int merchantId) {
//		List<RcProdBrand> allProdBrands = prodBrandService.findAllBrands();
//		List<RcProdBrand> unselectedProdBrands = new ArrayList<RcProdBrand>();
//		List<RcProdBrand> selectedProdBrands = prodBrandService.findMerchantBrands(merchantId);
//		for(RcProdBrand prodBrand : allProdBrands) {
//			boolean isFound = false;
//			for(RcProdBrand selectedProdBrand : selectedProdBrands) {
//				if(prodBrand.getProdBrandId().equals(selectedProdBrand.getProdBrandId())) {
//					isFound = true;
//					break;
//				}
//			}
//			if(!isFound) {
//				unselectedProdBrands.add(prodBrand);
//			}
//		}
//		List<List<RcProdBrand>> result = new ArrayList<List<RcProdBrand>>();
//		result.add(unselectedProdBrands);
//		result.add(selectedProdBrands);
//		return result;
//	}

//	@Override
//	public int removeAddedInfo(int merchantId) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//	@Override
//	public int addRcProdBrandRela(RcProdBrandRel rcProdBrandRel) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

	@Override
	public List<RcMerchant> findMerchantsByVendorId(int vendorId) {
		RcMerchant param = new RcMerchant();
		param.setVendorId(vendorId);
		return merchantMapper.select(param);
	}

	@Override
	public RcMerchant findMerchantByMerchantUserId(int merchantUserId) {
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		if(merchantUser != null) {
			return findMerchantById(merchantUser.getMerchantId());
		}
		return null;
	}

//	@Override
//	public List<RcMerchant> searchMerchant(String searchKey) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public RcMerchant findMerchantByProdId(int prodId) {
		RcProd prod = prodService.findProdById(prodId);
		if(prod != null) {
			return findMerchantById(prod.getMerchantId());
		}
		return null;
	}

	@Override
	public List<RcMerchant> findMerchantByName(String name) {
		RcMerchant param = new RcMerchant();
		param.setName(name);
		return merchantMapper.select(param);
	}

	@Override
	public List<RcMerchant> findSortedMerchantByName(String name) {
		RcMerchant param = new RcMerchant();
		param.setName(name);
		param.setOrderBy("NAME ASC");
		return merchantMapper.select(param);
	}

//	@Override
//	public int removeMerchantBrands(int merchantId) {
//		return prodBrandService.removeMerchantBrands(merchantId);
//	}

	@Override
	public BigDecimal findYIncomeByMerchantId(int merchantId) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0); 
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.SECOND,0);
		Date  createTimeTo= cal.getTime();
		cal.roll(Calendar.DATE, -1);
		Date createTimeFrom = cal.getTime();
		RcOrder param = new RcOrder();
		param.setMerchantId(merchantId);
		param.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
		param.setCreateTimeFrom(createTimeFrom);
		param.setCreateTimeTo(createTimeTo);
		List<RcOrder> orders = orderService.findOrders(param);
		BigDecimal totalOrderAmount = new BigDecimal(0);
		for (RcOrder order : orders) {
			 totalOrderAmount = totalOrderAmount.add(order.getPayPrice());
		}
		return totalOrderAmount;
	}

	@Override
	public Integer findTTransByMerchantId(int merchantId) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0); 
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.SECOND,0);
		Date createTimeFrom = cal.getTime();
		cal.roll(Calendar.DATE, 1);
		Date createTimeTo = cal.getTime();
		RcOrder param = new RcOrder();
		param.setMerchantId(merchantId);
		param.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
		param.setCreateTimeFrom(createTimeFrom);
		param.setCreateTimeTo(createTimeTo);
		List<RcOrder> orders = orderService.findOrders(param);
		if(org.apache.commons.collections.CollectionUtils.isEmpty(orders)){
			return 0;
		}
		return orders.size();
	}

	@Override
	public BigDecimal findTIncomeByMerchantId(int merchantId) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0); 
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.SECOND,0);
		Date  createTimeFrom = cal.getTime();
		cal.roll(Calendar.DATE, 1);
		Date createTimeTo = cal.getTime();
		RcOrder param = new RcOrder();
		param.setMerchantId(merchantId);
		param.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
		param.setCreateTimeFrom(createTimeFrom);
		param.setCreateTimeTo(createTimeTo);
		List<RcOrder> orders = orderService.findOrders(param);
		BigDecimal totalOrderAmount = new BigDecimal(0);
		for (RcOrder order : orders) {
			 totalOrderAmount = totalOrderAmount.add(order.getPayPrice());
		}
		return totalOrderAmount;
	}

	@Override
	public int addMerchantQRCodes(int merchantId, String backgroundImgPath) {
		RcMerchant merchant = findMerchantById(merchantId);
		if(merchant.getMealStyle() == MerchantMealStyleEnum.EAT_IN.getValue() || merchant.getMealStyle() == MerchantMealStyleEnum.EAT_IN_AND_TAKE_OUT.getValue()) {
			RcMerchantSection param = new RcMerchantSection();
			param.setMerchantId(merchantId);
			List<RcMerchantSection> merchantSections = merchantSectionService.findMerchantSections(param);
			if(CollectionUtils.isEmpty(merchantSections)) {
				return -1;
			}
		}
		if(merchant.getMealStyle() != MerchantMealStyleEnum.EAT_IN.getValue()) {
			addMerchantQRCode(merchantId, null, null, null, backgroundImgPath);
		}
		if(merchant.getMealStyle() != MerchantMealStyleEnum.TAKE_OUT.getValue()) {
			RcMerchantSection param = new RcMerchantSection();
			param.setMerchantId(merchantId);
			List<RcMerchantSection> merchantSections = merchantSectionService.findMerchantSections(param);
			if(!CollectionUtils.isEmpty(merchantSections)) {
				for(RcMerchantSection merchantSection : merchantSections) {
					String sectionName = merchantSection.getName();
					List<RcMerchantTable> merchantTables = merchantSection.getTables();
					if(!CollectionUtils.isEmpty(merchantSections)) {
						for(RcMerchantTable merchantTable : merchantTables) {
							int tableId = merchantTable.getTableId();
							int tableNumber = merchantTable.getTableNumber();
							addMerchantQRCode(merchantId, tableId, sectionName, tableNumber, backgroundImgPath);
						}
					}
				}
			}
		}
		return 1;
	}

	@Override
	public long addMerchantQRCode(int merchantId, Integer tableId, String sectionName, Integer tableNumber, String backgroundImgPath) {
		String fileInfoId = UUID.randomUUID().toString().replaceAll("-", "");
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		String relativePath = year + File.separator + month + File.separator + day + File.separator;
		String dirPath = propertyConstants.systemFileServerRoot + relativePath;
		try {
			String qrcodePath = QRCodeUtil.encode(propertyConstants.systemServerUrl + "restaurant/page/menu?merchantId=" + merchantId + (tableId == null ? "" : ("&tableId=" + tableId + "&sectionName=" + sectionName + "&tableNumber=" + tableNumber)), 
					dirPath, fileInfoId, "png", 280, 280);
			BufferedImage qrcode = ImageIO.read(new File(qrcodePath));
			BufferedImage background = ImageIO.read(new File(backgroundImgPath));
			Graphics g = background.getGraphics();
	        g.drawImage(qrcode, 70, 74, 280, 280, null);
	        if(tableId != null) {
		        g.setFont(new Font(null, Font.BOLD, 40));
		        g.setColor(new Color(72, 43, 6));
		        String tableNumberStr;
		        if(tableNumber < 10) {
		        	tableNumberStr = "00" + tableNumber;
		        } else if(tableNumber < 100) {
		        	tableNumberStr = "0" + tableNumber;
		        } else {
		        	tableNumberStr = tableNumber + "";
		        }
		        String desc = sectionName + " - " + tableNumberStr;
		        FontMetrics fm = g.getFontMetrics();
		        int stringWidth = fm.stringWidth(desc);
		        int x = (background.getWidth() - stringWidth) / 2;
		        g.drawString(desc, x, 60);
	        }
	        File realQrcode = new File(dirPath + fileInfoId + "." + "png");
	        ImageIO.write(background, "png", realQrcode);
		} catch (WriterException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		
		RcMerchantQRCode merchantQRCode = new RcMerchantQRCode();
		merchantQRCode.setMerchantId(merchantId);
		merchantQRCode.setTableId(tableId == null ? 0 : tableId);
		merchantQRCode.setSectionName(sectionName);
		merchantQRCode.setTableNumber(tableNumber);
		merchantQRCode.setImgUrl(propertyConstants.systemFileServerUrl + relativePath.replaceAll("\\\\", "/") + fileInfoId + ".png");
		if(merchantQCCodeMapper.insert(merchantQRCode) > 0) {
			return merchantQRCode.getMerchantQRCodeId();
		}
		return 0;
	}

	@Override
	public int removeMerchantQRCode(int merchantId) {
		RcMerchantQRCode param = new RcMerchantQRCode();
		param.setMerchantId(merchantId);
		return merchantQCCodeMapper.delete(param);
	}

	@Override
	public List<RcMerchantQRCode> findMerchantQRCodesByMerchantId(int merchantId) {
		RcMerchantQRCode param = new RcMerchantQRCode();
		param.setMerchantId(merchantId);
		return merchantQCCodeMapper.select(param);
	}

	@Override
	public void coverMerchant(int merchantId) {
		RcMerchant merchant = findMerchantById(merchantId);
		merchant.setDelFlag(YesOrNoEnum.NO.getValue());
		modifyMerchant(merchant);
		RcMerchantUser rcMerchantUser=merchantUserService.findMerchantSuperAdmin(merchantId);
		rcMerchantUser.setDelFlag(YesOrNoEnum.NO.getValue());
		merchantUserService.modifyMerchantUser(rcMerchantUser);
	}
}
