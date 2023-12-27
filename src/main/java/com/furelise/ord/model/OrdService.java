package com.furelise.ord.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furelise.mem.repository.MemRepository;
import com.furelise.orddetail.model.OrdDetail;
import com.furelise.product.model.Product;
import com.furelise.sale.model.SaleService;
import com.furelise.shopcart.model2.ShopCartService;
//import com.furelise.shopcart.model2.ShopCart;
//import com.furelise.shopcart.model2.ShopCartService;

@Service
public class OrdService {

	@Autowired
	OrdRepository dao;

	@Autowired
	MemRepository memDao;

	@Autowired
	SaleService saleDao;

	@Autowired
	ShopCartService scSvc;

	// 創建訂單 用OrdDTO抓
	public Ord createOrder(OrdDTO req, Model model, BigDecimal totalAmount) {

		Ord ord = new Ord();

		ord.setOrdDate(new Timestamp(System.currentTimeMillis()));
		ord.setMemID(req.getMemID());
		ord.setPayment(req.getPayment());
		ord.setDeliver(req.getDeliver());
		ord.setAddress(req.getAddress());
		ord.setCityCode(req.getCityCode()); 
		ord.setDeliverDate(null); //出貨時間初始為null
		ord.setSum(req.getSum()); 
		ord.setShipping(req.getShipping());
		ord.setTotal(totalAmount);
		ord.setSaleID(saleDao.getSaleByCoupon(req.getCoupon()).getSaleID());
		ord.setOrdStatus(0); // 預設都為0(處理中)
		ord.setArrival(null); //送達時間初始為null

		

		// 創建訂單明細OrdDetail
		Map<Product,String> shopCartItems = scSvc.getCartProducts(req.getMemID().toString());
		List<OrdDetail> ordDetails = new ArrayList<>();

		for (Map.Entry<Product, String> entry : shopCartItems.entrySet()) {
		    String pID = entry.getKey().getPID().toString();
		    String quantityString = entry.getValue();

		    // 轉換數量字串為整數
		    int quantity = Integer.parseInt(quantityString);

		    // 設置 OrdDetail 的相關屬性
		    OrdDetail ordDetail = new OrdDetail();

		    // 這裡將 ordDetail 的 ordID 設置為 ord 的 ID
		    ordDetail.setOrdID(ord.getOrdID());

		    // pID 應該是由 shopCartItems 中取得
		    ordDetail.setPID(Integer.parseInt(pID));

		    ordDetail.setDetaQty(quantity);
		    ordDetail.setFeedback(null); // 訂單明細創建初始時評價為 null
		    ordDetail.setLevel(null); // 訂單明細創建初始時評價等級為 null
		    ordDetail.setFbTime(null); // 訂單明細創建初始時評價內容為 null

		    ordDetails.add(ordDetail);
	    }

	    ord.setOrdDetails(ordDetails);
	    
	    dao.save(ord);

	    return ord;
	}

	//
	public Ord updateOrd(Integer ordID,Integer ordStatus) {
		Ord ord = new Ord();
		ord.setOrdStatus(ordStatus);
		dao.save(ord);
		return ord;

	}
	
	public List<Ord> getAllOrds(){
		
		return dao.findAll();
	}
	
	public List<Ord> getByStatus(Integer ordStatus){
		return dao.findByOrdStatus(ordStatus);
	}


	// 創建訂單 從購物車直接抓
//	public Ord createOrder(Map<String, String> shopCartItems, BigDecimal totalAmount) {
//
//		Ord ord = new Ord();
//
//		ord.setOrdDate(new Timestamp(System.currentTimeMillis()));
//		ord.setMemID(null);
//		ord.setPayment(null);
//		ord.setDeliver(null);
//		ord.setAddress(null);
//		ord.setCityCode(null);
//		ord.setDeliverDate(null);
//		ord.setSum(totalAmount);
//		ord.setShipping(totalAmount);
//		ord.setTotal(totalAmount);
//		ord.setSaleID(saleDao.getSaleByCoupon(null).getSaleID());
//		ord.setOrdStatus(0); // 預設都為0(處理中)
//		ord.setArrival(null);
//
//		// 創建訂單明細OrdDetail
//		List<OrdDetail> ordDetails = convertShopCartItemsToOrdDetails(shopCartItems, ord);
//		ord.setOrdDetails(ordDetails);
//
//		dao.save(ord);
//
//		return ord;
//	}
	// 將購物車商品轉為訂單明細（OrdDetail）
//	private List<OrdDetail> convertShopCartItemsToOrdDetails(Map<String, String> shopCartItems, Ord ord) {
//
//		List<OrdDetail> ordDetails = new ArrayList<>();
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		// 抓出購物車中的所有商品
//		for (int i = 0; i < shopCartItems.size(); i++) {
//
//			String shopCartItemString = shopCartItems.get(i);
//
//			try {
//				// 字串轉換為ShopCart
//				ShopCart shopCartItem = objectMapper.readValue(shopCartItemString, ShopCart.class);
//
//				// 創建OrdDetail
//				// 評價部分的feedback,level,fbTime在創建初始皆為null
//				OrdDetail ordDetail = new OrdDetail(ord.getOrdID(), shopCartItem.getPID(), shopCartItem.getQuantity(),
//						null, null, null);
//
//				ordDetails.add(ordDetail);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return ordDetails;
//		}
//		return ordDetails;
//	}


}
