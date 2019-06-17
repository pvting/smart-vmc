package com.vmc.core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vmc.core.BLLController;
import com.vmc.core.ReplenishAction;
import com.vmc.core.model.product.BLLCategory;
import com.vmc.core.model.product.BLLProduct;
import com.vmc.core.model.product.BLLStackProduct;
import com.vmc.core.model.product.FreeBie;
import com.vmc.core.model.product.OdooProduct;
import com.vmc.core.model.product.OdooProductList;
import com.vmc.core.model.product.OdooPromotion;
import com.vmc.core.model.product.OdooPromotionList;
import com.vmc.core.model.product.OdooStock;
import com.vmc.core.model.product.OdooStockList;
import com.vmc.core.model.product.PromotionDetails;
import com.vmc.core.model.product.SupplyProduct;
import com.vmc.core.model.product.SupplyProductList;
import com.vmc.core.model.stock.Stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import vmc.core.log;
import vmc.machine.core.VMCContoller;
import vmc.machine.core.model.VMCStackProduct;

import static android.content.Context.MODE_PRIVATE;

/**
 * <b>Create Date:</b>2017/2/7,下午1:33<br>
 * <b>Author:</b>huyunqiang<br>
 * <b>Description:</b> <br>
 */
public class BLLProductUtils {

    private static final String TAG = "BLLProductUtils";
    private static final String SP_NAME = "products";

    private BLLProductUtils() {
        //no instance
    }

    /**
     * 商品品集合
     */
    public static HashMap<Integer, BLLProduct> sBLLProductMap = new HashMap<>();

    public static volatile int[] sBLLLock = new int[0];

    /**
     * 分类下的商品
     */
    public static HashMap<String, BLLCategory> sBLLCategoryMap = new HashMap<>();

    /**
     * 通过货道获取某个商品的集合  Key "box_no*stack_no"
     */
    public static HashMap<String, BLLStackProduct> sBLLProductsByRoadMap = new HashMap<>();

    /**
     * 促销集合  Key "product_id-promotion_id"
     */
    public static HashMap<String, OdooPromotion> sBLLPromotionMap = new HashMap<>();


    /**
     * 初始化数据源
     *
     * @param productList 商品列表
     */
    public static synchronized void initProduct(OdooProductList productList, Context context) {
        synchronized (sBLLLock) {
            sBLLProductMap.clear();
            sBLLCategoryMap.clear();
            sBLLProductsByRoadMap.clear();
            sBLLPromotionMap.clear();
            for (OdooProduct op : productList.records) {
                BLLStackProduct bsp = new BLLStackProduct();//创建商品
                bsp.product_id = op.id;
                bsp.name = op.name;
                bsp.box_no = Integer.parseInt(op.box_no);
                bsp.stack_no = Integer.parseInt(op.stack_no);
                bsp.price = op.price;
                bsp.origin_stack_no = op.stack_no;
                bsp.image_url = op.image_url;
                bsp.seq_no = op.seq_no;
                bsp.category_name = op.product_type;
                bsp.net_weight = op.net_weight;
                bsp.product_details_image_url = op.product_details_image_url;
                //初始化通过货柜和货道获取商品的Map
                sBLLProductsByRoadMap.put(bsp.box_no + "*" + bsp.stack_no, bsp);
            }

            for (String key : sBLLProductsByRoadMap.keySet()) {
                BLLStackProduct bsp = sBLLProductsByRoadMap.get(key);
                BLLProduct bp = sBLLProductMap.get(bsp.product_id);

                if (bp == null) {//如果是空的  表示加入到去重复的Map
                    bp = new BLLProduct();//创建一个商品对象
                    bp.product_id = bsp.product_id;
                    bp.price = bsp.price;
                    bp.image_url = bsp.image_url;
                    bp.product_details_image_url = bsp.product_details_image_url;
                    bp.name = bsp.name;
                    bp.net_weight = bsp.net_weight;
                    bp.category_name = bsp.category_name;

                    sBLLProductMap.put(bp.product_id, bp);
                }
                DecimalFormat format = new DecimalFormat("00");
                int min_box_stack = Integer.parseInt(bp.fristBoxNo + format.format(bp.fristStackNo));
                int current_box_stack = Integer.parseInt(bsp.box_no + format.format(bsp.stack_no));
                if (min_box_stack == 0 || min_box_stack >= current_box_stack) {
                    bp.fristBoxNo = bsp.box_no;
                    bp.fristStackNo = bsp.stack_no;
                }
                bp.mBLLStackProducts.add(bsp);

                //分类
                BLLCategory bc = sBLLCategoryMap.get(bsp.category_name);
                if (bc == null) {
                    bc = new BLLCategory();

                    bc.category_name = bsp.category_name;

                    sBLLCategoryMap.put(bc.category_name, bc);
                }
                bc.mBLLProductHashMap.put(bp.product_id, bp);
            }
            BLLController.getInstance().syncStackSaleableState();
            saveProductsToSP(context);
        }
    }

    /**
     * 通过本地初始化数据源
     */
    public static synchronized void initProductFromSP(Context context) {

        synchronized (sBLLLock) {
            getProductsFromSP(context);

            getPromotionsFromSP(context);


            for (String key : sBLLProductsByRoadMap.keySet()) {
                BLLStackProduct bsp = sBLLProductsByRoadMap.get(key);
                BLLProduct bp = sBLLProductMap.get(bsp.product_id);

                if (bp == null) {//如果是空的  表示加入到去重复的Map
                    bp = new BLLProduct();//创建一个商品对象
                    bp.product_id = bsp.product_id;
                    bp.price = bsp.price;
                    bp.image_url = bsp.image_url;
                    bp.product_details_image_url = bsp.product_details_image_url;
                    bp.name = bsp.name;
                    bp.net_weight = bsp.net_weight;
                    bp.category_name = bsp.category_name;
                    sBLLProductMap.put(bp.product_id, bp);
                }
                DecimalFormat format = new DecimalFormat("00");
                int min_box_stack = Integer.parseInt(bp.fristBoxNo + format.format(bp.fristStackNo));
                int current_box_stack = Integer.parseInt(bsp.box_no + format.format(bsp.stack_no));
                if (min_box_stack == 0 || min_box_stack >= current_box_stack) {
                    bp.fristBoxNo = bsp.box_no;
                    bp.fristStackNo = bsp.stack_no;
                }
                bp.mBLLStackProducts.add(bsp);
                BLLCategory bc = sBLLCategoryMap.get(bsp.category_name);
                if (bc == null) {
                    bc = new BLLCategory();
                    bc.category_name = bsp.category_name;
                    sBLLCategoryMap.put(bc.category_name, bc);
                }
                bc.mBLLProductHashMap.put(bp.product_id, bp);
            }
        }
        BLLController.getInstance().syncStackSaleableState();
    }


    /**
     * 保存商品数据
     *
     * @param c
     */
    public static void saveProductsToSP(Context c) {

        if (sBLLProductsByRoadMap == null || sBLLProductsByRoadMap.size() == 0) {
            return;
        }
        Gson gson = new Gson();
        String sourceStr = gson.toJson(sBLLProductsByRoadMap);
        SharedPreferences sp = c.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString("stackProducts", sourceStr);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }


    /**
     * 保存促销数据
     *
     * @param c
     */
    public static synchronized void savePromotionToSP(Context c) {
        Gson gson = new Gson();
        String sourceStr = gson.toJson(sBLLPromotionMap);
        SharedPreferences sp = c.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString("promotions", sourceStr);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }


    /**
     * 获取促销数据
     *
     * @param context
     */
    public static void getPromotionsFromSP(Context context) {
        sBLLPromotionMap.clear();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String soruceStr = sp.getString("promotions", null);
        if (soruceStr != null) {
            Gson gson = new Gson();
            sBLLPromotionMap =
                    gson.fromJson(soruceStr,
                                  new TypeToken<HashMap<String, OdooPromotion>>() {}.getType());
            if (sBLLPromotionMap == null) {
                sBLLPromotionMap = new HashMap<>();
            }
        }
    }


    /**
     * 获取商品数据
     *
     * @param c
     */
    public static void getProductsFromSP(Context c) {
        sBLLProductsByRoadMap.clear();
        sBLLProductMap.clear();
        sBLLCategoryMap.clear();
        sBLLPromotionMap.clear();
        SharedPreferences sp = c.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String soruceStr = sp.getString("stackProducts", null);
        if (soruceStr != null) {
            Gson gson = new Gson();
            sBLLProductsByRoadMap =
                    gson.fromJson(soruceStr,
                                  new TypeToken<HashMap<String, BLLStackProduct>>() {}.getType());
            if (sBLLProductsByRoadMap == null) {
                sBLLProductsByRoadMap = new HashMap<>();
            }
        }
    }


    /**
     * 更新促销信息
     *
     * @param odooPromotionList
     * @param context
     */
    public static void updatePromotionList(OdooPromotionList odooPromotionList, Context context) {
        synchronized (sBLLLock) {
            sBLLPromotionMap.clear();
            for (OdooPromotion promotion : odooPromotionList.records) {
                if (null == promotion.promotion_details) {
                    continue;
                }
                sBLLPromotionMap.put(promotion.product_id + "-" + promotion.promotion_details.promotion_id,
                                     promotion);
            }
            savePromotionToSP(context);
        }
        updatePromotionDetailOfProduct(context);
    }


    /**
     * 拉取库存 更新库存
     *
     * @param stockList
     * @param context
     */
    public static void updateStackProductStock(OdooStockList stockList, Context context) {
        synchronized (sBLLLock) {
            for (OdooStock stock : stockList.records) {
                int box_no;
                int stack_no;
                try {
                    box_no = Integer.parseInt(stock.box_no);
                    stack_no = Integer.parseInt(stock.stack_no);
                } catch (Exception e) {
                    log.e(TAG, "updateStackProductStock: 库存解析异常");
                    return;
                }
                BLLStackProduct bsp = sBLLProductsByRoadMap.get(box_no + "*" + stack_no);

                if (bsp != null) {
                    if (stock.stock >= 0) {
                        bsp.quantity = stock.stock;
                    } else {
                        bsp.quantity = 0;
                    }
                    log.i(TAG,
                          "updateStackProductStock: 初始化库存：货柜" +
                          bsp.box_no +
                          ",货道" +
                          bsp.stack_no +
                          ",个数:" +
                          bsp.quantity);
                }
            }
            saveProductsToSP(context);
        }
    }


    /**
     * 转换为VMC写入的数据类型
     * 全部转换
     */
    public static List<VMCStackProduct> makeVmcProductList() {
        synchronized (sBLLLock) {
            List<VMCStackProduct> products = new ArrayList<>();

            for (Integer key : sBLLProductMap.keySet()) {
                BLLProduct bp = sBLLProductMap.get(key);
                for (BLLStackProduct bsp : bp.mBLLStackProducts) {
                    VMCStackProduct p = new VMCStackProduct();

                    p.boxId = bsp.box_no;
                    p.roadId = bsp.stack_no;
                    p.price = bsp.price;
                    p.seqNo = bsp.seq_no;
                    p.stock = bsp.quantity;
                    products.add(p);
                }
            }

            return products;
        }
    }


    /**
     * 转换为VMC写入的数据类型
     *
     * @param splist 补货app发送的数据源
     *
     * @return
     */
    public static List<VMCStackProduct> makeVmcProductListformSupply(ArrayList<SupplyProduct> splist) {
        List<VMCStackProduct> products = new ArrayList<>();
        synchronized (sBLLLock) {
            for (SupplyProduct sp : splist) {
                VMCStackProduct p = new VMCStackProduct();
                try {
                    p.boxId = Integer.parseInt(sp.getBox_no());
                    p.roadId = Integer.parseInt(sp.getStack_no());
                } catch (Exception e) {
                    log.e(TAG, "" + e.getMessage());
                }
                p.price = sp.getPrice();
                p.seqNo = sp.getStack_no();
                p.stock = sp.getQuantity();
                products.add(p);
            }

        }
        return products;
    }


    /**
     * 转换为VMC写入的数据类型
     *
     * @param list 需要转换的集合
     *
     * @return
     */
    public static List<VMCStackProduct> makeVmcProductList(ArrayList<BLLProduct> list) {
        synchronized (sBLLLock) {
            List<VMCStackProduct> products = new ArrayList<>();

            for (BLLProduct bp : list) {
                for (BLLStackProduct bsp : bp.mBLLStackProducts) {
                    VMCStackProduct p = new VMCStackProduct();
                    p.boxId = bsp.box_no;
                    bsp.price = bp.price;
                    p.roadId = bsp.stack_no;
                    p.price = bsp.price;
                    p.seqNo = bsp.seq_no;
                    p.stock = bsp.quantity;
                    products.add(p);
                }
            }

            return products;
        }
    }


    /**
     * 提供UI使用
     *
     * @param categroyName 下标索引
     * @param pageIndex    分类索引
     *
     * @return
     */
    public static ArrayList<BLLProduct> getProductListByPageIndex(String categroyName, int pageIndex) {
        synchronized (sBLLLock) {
            return getProductListByPageIndex(categroyName, pageIndex, 9);
        }
    }


    /**
     * 获取分类列表productList
     *
     * @return
     */
    public static ArrayList<BLLCategory> getCategoryList() {
        synchronized (sBLLLock) {
            ArrayList<BLLCategory> list = new ArrayList<>();
            BLLCategory categoryALL = new BLLCategory();
            categoryALL.category_name = "全部";
            for (String key : sBLLCategoryMap.keySet()) {
                BLLCategory category = sBLLCategoryMap.get(key);
                list.add(category);
                categoryALL.mBLLProductHashMap.putAll(category.mBLLProductHashMap);
            }
            list.add(0, categoryALL);
            return list;
        }
    }


    /**
     * 提供UI使用
     *
     * @param categroyName 下标索引
     * @param pageIndex    分类索引
     * @param pageSize     每页格式
     *
     * @return
     */
    public static ArrayList<BLLProduct> getProductListByPageIndex(String categroyName,
                                                                  int pageIndex,
                                                                  int pageSize) {
        synchronized (sBLLLock) {
            ArrayList<BLLProduct> categoryProducts = new ArrayList<>();

            ArrayList<BLLProduct> categoryProductsByPage = new ArrayList<>();

            ArrayList<BLLProduct> havePromotionProducts = new ArrayList<>();

            ArrayList<BLLProduct> noPromotionProducts = new ArrayList<>();


            if (categroyName.equals("全部")) {//表示全部
                for (Integer key : sBLLProductMap.keySet()) {
                    BLLProduct bp = sBLLProductMap.get(key);
                    categoryProducts.add(bp);
                }
            } else {
                BLLCategory categoryProduct = sBLLCategoryMap.get(categroyName);
                for (Integer key : categoryProduct.mBLLProductHashMap.keySet()) {
                    BLLProduct bp = categoryProduct.mBLLProductHashMap.get(key);
                    categoryProducts.add(bp);
                }
            }
            // 获取到的商品可能会乱序, 根据货道重新排序
            Collections.sort(categoryProducts, new Comparator<BLLProduct>() {
                @Override
                public int compare(BLLProduct lhs, BLLProduct rhs) {
                    return lhs.getFristStackNoInt() - rhs.getFristStackNoInt();
                }
            });


            for (BLLProduct bp : categoryProducts) {
                if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 0) {
                    if (bp.mPromotionDetail != null) {
                        havePromotionProducts.add(bp);
                    } else {
                        noPromotionProducts.add(bp);
                    }
                }
            }
            categoryProducts.clear();
            categoryProducts.addAll(havePromotionProducts);
            categoryProducts.addAll(noPromotionProducts);
            int
                    max =
                    ((pageIndex + 1) * pageSize) > categoryProducts.size()
                    ? categoryProducts.size()
                    : (pageIndex + 1) * pageSize;
            for (int
                 i =
                 pageIndex * pageSize > categoryProducts.size()
                 ? categoryProducts.size()
                 : pageIndex * pageSize;
                 i < max; i++) {
                categoryProductsByPage.add(categoryProducts.get(i));
            }


            return categoryProductsByPage;
        }
    }


    /**
     * 提供UI使用
     *
     * @param categroyName 下标索引
     * @param pageIndex    分类索引
     * @param pageSize     每页个数
     *
     * @return
     */
    public static boolean haveNextPage(String categroyName, int pageIndex, int pageSize) {


        synchronized (sBLLLock) {
            int categoryAllProdctsSize = 0;

            int categoryPageProdctsSize = 0;

            if (categroyName.equals("全部")) {//表示全部

                for (Integer key : sBLLProductMap.keySet()) {
                    BLLProduct bp = sBLLProductMap.get(key);
                    if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 0) {
                        categoryAllProdctsSize += 1;
                    }
                }
            } else {
                BLLCategory categoryProduct = sBLLCategoryMap.get(categroyName);
                for (Integer key : categoryProduct.mBLLProductHashMap.keySet()) {
                    BLLProduct bp = categoryProduct.mBLLProductHashMap.get(key);
                    if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 0) {
                        categoryAllProdctsSize += 1;
                    }
                }
            }
            int max =
                    ((pageIndex + 2) * pageSize) > categoryAllProdctsSize
                    ? categoryAllProdctsSize
                    : (pageIndex + 2) * pageSize;
            for (int
                 i =
                 (pageIndex + 1) * pageSize > categoryAllProdctsSize
                 ? categoryAllProdctsSize
                 : (pageIndex + 1) * pageSize; i < max; i++) {
                categoryPageProdctsSize += 1;
            }
            if (categoryPageProdctsSize > 0) {
                return true;
            }

            return false;
        }
    }

    public static boolean isActive(PromotionDetails mPromotionDetail) {
        synchronized (sBLLLock) {
            boolean isPromotion = false;
            if (mPromotionDetail != null &&
                mPromotionDetail.promotion_id > 0 &&
                mPromotionDetail.promotion_type != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (mPromotionDetail.promotion_time_type.equals("every_day")) {
                    if (TextUtils.isEmpty(mPromotionDetail.start_date) ||
                        TextUtils.isEmpty(mPromotionDetail.end_date)) {
                        return isPromotion;
                    }

                    try {
                        //开始日期
                        Date startDate = sdf.parse(mPromotionDetail.start_date);
                        Date endDate = sdf.parse(mPromotionDetail.end_date);//结束日期
                        Date nowDate = new Date();//当前时间

                        //如果开始日期早于当前日期并且结束时间在当前日期之后
                        if ((startDate.before(nowDate) || isSameDate(startDate, nowDate)) &&
                            (endDate.after(nowDate) || isSameDate(endDate, nowDate))) {
                            //每天开始时间
                            String startTime = TextUtils.isEmpty(mPromotionDetail.start_time) ? "00:00:00"
                                                                                              : mPromotionDetail.start_time;
                            //每天结束时间
                            String endTime = TextUtils.isEmpty(mPromotionDetail.end_time) ? "23:59:59"
                                                                                          : mPromotionDetail.end_time;
                            //当前日期
                            String nowTime = sdf.format(nowDate);//当前日期

                            String todayStartTime = nowTime + " " + startTime;//今天开始时间
                            String todayEndTime = nowTime + " " + endTime;//今天结束时间
                            //今天开始时间早于现在时间 并且 今天 结束时间晚于现在时间
                            if (sdfTime.parse(todayStartTime).before(nowDate) &&
                                sdfTime.parse(todayEndTime).after(nowDate)) {
                                isPromotion = true;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        log.e(TAG, "isActive: 时间解析异常");
                    }
                } else if (mPromotionDetail.promotion_time_type.equals("cross_day")) {
                    if (TextUtils.isEmpty(mPromotionDetail.time_period_start) ||
                        TextUtils.isEmpty(mPromotionDetail.time_period_end)) {
                        return isPromotion;
                    }
                    try {
                        Date startDate = sdfTime.parse(mPromotionDetail.time_period_start);
                        Date endDate = sdfTime.parse(mPromotionDetail.time_period_end);//结束日期
                        Date nowDate = new Date();//当前时间
                        if ((startDate.before(nowDate) || startDate.getTime() == nowDate.getTime()) &&
                            (endDate.after(nowDate) || endDate.getTime() == nowDate.getTime())) {
                            isPromotion = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        log.e(TAG, "isActive: 时间解析异常");
                    }
                }

            }
            return isPromotion;
        }
    }

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                              && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                             && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    public static ArrayList<BLLProduct> getProductList(String categoryName) {
        synchronized (sBLLLock) {
            ArrayList<BLLProduct> list = new ArrayList<>();

            if (categoryName.equals("全部")) {
                for (Integer id : sBLLProductMap.keySet()) {
                    BLLProduct bp = sBLLProductMap.get(id);
                    list.add(bp);
                }
                Collections.sort(list, new Comparator<BLLProduct>() {
                    @Override
                    public int compare(BLLProduct lhs, BLLProduct rhs) {
                        return lhs.getFristStackNoInt() - rhs.getFristStackNoInt();
                    }
                });
                return list;
            }
            BLLCategory category = sBLLCategoryMap.get(categoryName);
            if (category == null) {
                return list;
            }
            for (Integer id : category.mBLLProductHashMap.keySet()) {
                BLLProduct bp = category.mBLLProductHashMap.get(id);
                list.add(bp);
            }

            Collections.sort(list, new Comparator<BLLProduct>() {
                @Override
                public int compare(BLLProduct lhs, BLLProduct rhs) {
                    return lhs.getFristStackNoInt() - rhs.getFristStackNoInt();
                }
            });

            return list;
        }
    }


    /**
     * 通过净含量排序
     *
     * @param categoryName
     *
     * @return
     */
    public static ArrayList<BLLProduct> getProductListByWegiht(String categoryName) {
        synchronized (sBLLLock) {
            ArrayList<BLLProduct> list = new ArrayList<>();

            if (categoryName.equals("全部")) {
                for (Integer id : sBLLProductMap.keySet()) {
                    BLLProduct bp = sBLLProductMap.get(id);
                    list.add(bp);
                }
            } else {
                BLLCategory category = sBLLCategoryMap.get(categoryName);
                if (category == null) {
                    return list;
                }
                for (Integer id : category.mBLLProductHashMap.keySet()) {
                    BLLProduct bp = category.mBLLProductHashMap.get(id);
                    list.add(bp);
                }


            }
            Collections.sort(list, new Comparator<BLLProduct>() {
                @Override
                public int compare(BLLProduct lhs, BLLProduct rhs) {
                    return ((int) (lhs.getWegiht()) - ((int) rhs.getWegiht()));
                }
            });

            return list;
        }
    }


    /**
     * 给商品更新促销信息
     */
    public static void updatePromotionDetailOfProduct(Context context) {
        synchronized (sBLLLock) {
            if (BLLProductUtils.sBLLPromotionMap == null) {
                return;
            }
            for (Integer id : sBLLProductMap.keySet()) {
                BLLProduct bp = sBLLProductMap.get(id);
                bp.mPromotionDetail = null;
            }
            for (String key : BLLProductUtils.sBLLPromotionMap.keySet()) {
                OdooPromotion promotion = sBLLPromotionMap.get(key);
                if (null == promotion) {
                    continue;
                }
                BLLProduct bp = BLLProductUtils.sBLLProductMap.get(promotion.product_id);
                if (bp == null) {
                    continue;
                }
                if (isActive(promotion.promotion_details)) {
                    bp.mPromotionDetail = promotion.promotion_details;
                }
            }
            saveProductsToSP(context);
        }
    }

    /**
     * 根据货道 获取商品
     *
     * @param boxNo
     * @param stackNO
     *
     * @return
     */
    public static BLLStackProduct getStackProduct(int boxNo, int stackNO) {
        synchronized (sBLLLock) {
            BLLStackProduct bsp;

            bsp = sBLLProductsByRoadMap.get(boxNo + "*" + stackNO);

            return bsp;
        }
    }

    /**
     * 冲减库存
     */
    public static void writeDownProductStock(int boxNo, int stackNo, Context context) {
        synchronized (sBLLLock) {
            BLLStackProduct bsp = sBLLProductsByRoadMap.get(boxNo + "*" + stackNo);

            if (bsp == null) {
                log.e(TAG, "writeDownProductStock: 未找到对应货道");
                return;
            }

            log.i(TAG,
                  "writeDownProductStock: 出货前,货柜:" +
                  bsp.getBoxNoInt() +
                  ",货道:" +
                  bsp.getStackNoInt() +
                  ",库存:" +
                  bsp.quantity);

            if (bsp.quantity > 0) {
                bsp.quantity -= 1;
            }

            log.i(TAG,
                  "writeDownProductStock: 出货后,货柜:" +
                  bsp.getBoxNoInt() +
                  ",货道:" +
                  bsp.getStackNoInt() +
                  ",库存:" +
                  bsp.quantity);


            BLLProduct bp = sBLLProductMap.get(bsp.product_id);
            if (bp == null) {
                log.e(TAG, "writeDownProductStock: 未找到对应商品");
                return;
            }
            saveProductsToSP(context);
        }
    }

    /**
     * 根据id 获取商品
     *
     * @param product_id
     *
     * @return
     */
    public static BLLProduct getProductById(int product_id) {
        synchronized (sBLLLock) {
            if (null == sBLLProductMap) {
                log.e(TAG, "getProductById: 商品集合列表为空");
                return null;
            }

            return sBLLProductMap.get(product_id);
        }
    }


    /**
     * 通过货柜，货道号 获取商品
     *
     * @param boxNo
     * @param stackNo
     *
     * @return
     */
    public static BLLProduct getProductByRoadId(int boxNo, int stackNo) {
        synchronized (sBLLLock) {
            if (null == sBLLProductsByRoadMap) {
                log.e(TAG, "getProductByRoadId: 商品集合列表为空");
                return null;
            }
            BLLStackProduct bsp = sBLLProductsByRoadMap.get(boxNo + "*" + stackNo);

            if (null == bsp) {
                log.e(TAG, "getProductByRoadId: 商品详情为空");
                return null;
            }
            return sBLLProductMap.get(bsp.product_id);
        }
    }


    public static BLLStackProduct getPromotionStackProduct(int productId) {

        synchronized (sBLLLock) {

            BLLProduct mProduct = BLLController.getInstance().getSelectProduct();

            if (null==mProduct){
                log.e(TAG, "getPromotionStackProduct: 选中的商品为空");
                mProduct = getProductById(productId);
            }

            if (null==mProduct){
                log.e(TAG, "getPromotionStackProduct: 商品为空");
                return null;
            }


            if (null == mProduct.mPromotionDetail) {
                log.i(TAG, "getPromotionStackProduct: 促销商品详情为空");
                return null;
            }
            if (null == mProduct.mPromotionDetail.freebie || mProduct.mPromotionDetail.freebie.size() == 0) {
                log.i(TAG, "getPromotionStackProduct: 促销详情为空");
                return null;
            }
            if (!"one_more".endsWith(mProduct.mPromotionDetail.promotion_type)) {

                return null;
            }

            ArrayList<BLLProduct> freebie = new ArrayList<>();
            for (FreeBie freeBie : mProduct.mPromotionDetail.freebie) {
                BLLProduct bp = BLLProductUtils.getProductById(freeBie.id);
                if (BLLController.getInstance().getSaleableStackProductByProductCount(bp) > 0) {
                    freebie.add(bp);
                }
            }
            if (freebie.size() == 0) {

                return null;
            }

            Random random = new Random();

            int randomProductNumbler = random.nextInt(freebie.size());//随机数


            BLLProduct bp = freebie.get(randomProductNumbler);


            return BLLController.getInstance().getSaleableStackProductByProduct(bp);
        }
    }


    /**
     * 库存盘点
     *
     * @param context
     */
    public static void takeStock(Context context, SupplyProductList list) {
        synchronized (sBLLLock) {
            Intent intent = new Intent(ReplenishAction.STOCK_SYNC_ACTION);
            intent.putExtra("msg", "success");
            intent.putExtra("result", "售卖APP同步中");
            intent.putExtra("code", "3");
            context.sendBroadcast(intent);
            for (SupplyProduct sp : list.data) {
                int box_no = Integer.parseInt(sp.getBox_no());
                int stack_no = Integer.parseInt(sp.getStack_no());
                BLLStackProduct bsp = sBLLProductsByRoadMap.get(box_no + "*" + stack_no);
                if (bsp != null) {

                    log.i(TAG,
                          "statusSync: 盘点前：货柜: " +
                          bsp.box_no +
                          ",货道: " +
                          bsp.stack_no +
                          ",个数: " +
                          bsp.quantity);

                    bsp.quantity = sp.getQuantity();

                    log.i(TAG,
                          "statusSync: 盘点后：货柜: " +
                          bsp.box_no +
                          ",货道: " +
                          bsp.stack_no +
                          ",个数: " +
                          bsp.quantity);
                }
            }
            saveProductsToSP(context);
            Intent intentResult = new Intent(ReplenishAction.STOCK_SYNC_RESULT);
            intentResult.putExtra("msg", "success");
            intentResult.putExtra("result", "售卖APP同步完成");
            intentResult.putExtra("code", "3");
            context.sendBroadcast(intentResult);//因为要同步给补货，用全局广播发送
        }
    }


    /**
     * 补货|换货
     *
     * @param context 上下文
     * @param list    补货app传递过来的数据
     */
    public static void stockSync(Context context, SupplyProductList list) {
        synchronized (sBLLLock) {
            Intent intent = new Intent(ReplenishAction.STOCK_SYNC_ACTION);
            intent.putExtra("msg", "success");
            intent.putExtra("result", "售卖APP同步中");
            intent.putExtra("code", "2");
            context.sendBroadcast(intent);
            for (SupplyProduct sp : list.data) {
                int box_no = Integer.parseInt(sp.getBox_no());
                int stack_no = Integer.parseInt(sp.getStack_no());
                if (sp.getType() == 0) {
                    BLLStackProduct bsp = sBLLProductsByRoadMap.get(box_no + "*" + stack_no);
                    if (bsp != null) {

                        log.i(TAG,
                              "statusSync: 补货前：货柜: " +
                              bsp.box_no +
                              ",货道: " +
                              bsp.stack_no +
                              ",个数: " +
                              bsp.quantity);

                        bsp.quantity = sp.getQuantity();

                        log.i(TAG,
                              "statusSync: 补货后：货柜: " +
                              bsp.box_no +
                              ",货道: " +
                              bsp.stack_no +
                              ",个数: " +
                              bsp.quantity);
                    }
                } else {//换货   先移除旧的 再新增新的
                    BLLStackProduct bsp = sBLLProductsByRoadMap.get(box_no + "*" + stack_no);
                    //如果获取到对应货道上的商品 则移除改商品
                    if (bsp != null) {

                        log.i(TAG,
                              "statusSync: 换货前: 货柜: " +
                              box_no +
                              ",料道: " +
                              stack_no +
                              ",名称: " +
                              bsp.name +
                              ",数量: " +
                              bsp.quantity);

                        //移除该货道
                        sBLLProductsByRoadMap.remove(box_no + "*" + stack_no);
                        //该货道上对应的商品
                        BLLProduct bp = sBLLProductMap.get(bsp.product_id);
                        if (bp != null) {//若果该商品存在
                            bp.mBLLStackProducts.remove(bsp);//该商品移除该货道
                            if (bp.mBLLStackProducts.size() == 0) {//如果一个货道都没有 则移除整个商品
                                sBLLProductMap.remove(bp.product_id);
                                sBLLCategoryMap.get(bp.category_name).mBLLProductHashMap.remove(bp.product_id);
                            }
                            if (sBLLCategoryMap.get(bp.category_name).mBLLProductHashMap.size() ==
                                0) {//如果该分类没有一个商品  则也移除这个分类
                                sBLLCategoryMap.remove(bp.category_name);
                            }
                        }
                    }
                    //表示新增了一个商品货道
                    bsp = new BLLStackProduct();
                    bsp.product_id = sp.getProduct_id();
                    bsp.name = sp.getName();
                    bsp.price = sp.getPrice();
                    bsp.origin_stack_no = sp.getStack_no();
                    bsp.stack_no = Integer.parseInt(sp.getStack_no());
                    bsp.box_no = Integer.parseInt(sp.getBox_no());
                    bsp.image_url = sp.getImage_url();
                    bsp.quantity = sp.getQuantity();
                    bsp.net_weight = sp.getNet_weight();
                    bsp.product_details_image_url = sp.getProduct_details_image_url();
                    bsp.seq_no = sp.getSeq_no();
                    bsp.stack_no = stack_no;
                    bsp.category_name = sp.getCategory_name();

                    log.i(TAG,
                          "statusSync: 换货后: 货柜: " +
                          box_no +
                          ",料道: " +
                          stack_no +
                          ",名称: " +
                          bsp.name +
                          ",数量: " +
                          bsp.quantity);

                    sBLLProductsByRoadMap.put(bsp.box_no + "*" + bsp.stack_no, bsp);

                    BLLProduct bp = sBLLProductMap.get(bsp.product_id);

                    if (bp != null) {
                        //已存在的商品  则新增了一个货道 并添加它的库存
                        bp.mBLLStackProducts.add(bsp);
                    } else {
                        //否者 新增了一个商品并新增一个货道
                        bp = new BLLProduct();
                        bp.product_id = bsp.product_id;
                        bp.name = bsp.name;
                        bp.price = bsp.price;
                        bp.net_weight = bsp.net_weight;
                        bp.image_url = bsp.image_url;
                        bp.category_name = bsp.category_name;
                        bp.product_details_image_url = bsp.product_details_image_url;
                        bp.mBLLStackProducts.add(bsp);
                        sBLLProductMap.put(bp.product_id, bp);
                        BLLCategory category = sBLLCategoryMap.get(bp.category_name);
                        if (category == null) {  //新增一个新的分类的商品
                            category = new BLLCategory();

                            category.category_name = bp.category_name;
                            sBLLCategoryMap.put(category.category_name, category);
                        }
                        category.mBLLProductHashMap.put(bp.product_id, bp);
                    }

                    DecimalFormat format = new DecimalFormat("00");
                    int min_box_stack = Integer.parseInt(bp.fristBoxNo + format.format(bp.fristStackNo));

                    int current_box_stack = Integer.parseInt(bsp.box_no + format.format(bsp.stack_no));
                    if (min_box_stack == 0 || min_box_stack >= current_box_stack) {
                        bp.fristBoxNo = bsp.box_no;
                        bp.fristStackNo = bsp.stack_no;
                    }

                }
            }
            saveProductsToSP(context);
            Intent intentResult = new Intent(ReplenishAction.STOCK_SYNC_RESULT);
            intentResult.putExtra("msg", "success");
            intentResult.putExtra("result", "售卖APP同步完成");
            intentResult.putExtra("code", "2");
            context.sendBroadcast(intentResult);//因为要同步给补货，用全局广播发送
        }
    }


    /**
     * 获取售卖商品信息
     *
     * @return
     */
    public static synchronized String getSVMProductInfo(Context context) {
        synchronized (sBLLLock) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                if (sBLLProductsByRoadMap == null || sBLLProductsByRoadMap.size() == 0) {
                    jsonObject.put("msg", "error");

                    SharedPreferences sp = context.getSharedPreferences("user", MODE_PRIVATE);
                    String username = sp.getString("name", "");
                    String password = sp.getString("password", "");

                    if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                        jsonObject.put("result", "售卖APP未登录");
                        log.e(TAG, "getSVMProductInfo: 售卖APP未登录");
                    } else {
                        jsonObject.put("result", "售卖APP库存异常,请检查售货机...");
                        log.e(TAG, "getSVMProductInfo: 售货机库存异常,请检查售货机...");
                    }

                    jsonObject.put("records", jsonArray);
                    return jsonObject.toString();
                }
                jsonObject.put("msg", "success");
                jsonObject.put("factoryCode", VMCContoller.getInstance().getVendingMachineId() + "");
                jsonObject.put("result", "获取库存成功");
                log.i(TAG, "getSVMProductInfo: 获取库存成功");
                for (String key : sBLLProductsByRoadMap.keySet()) {
                    BLLStackProduct bsp = sBLLProductsByRoadMap.get(key);
                    if (null != bsp) {
                        JSONObject jb = new JSONObject();
                        jb.put("product_id", bsp.product_id);
                        jb.put("name", bsp.name);
                        jb.put("box_no", bsp.box_no);
                        jb.put("stack_no", bsp.stack_no);
                        jb.put("origin_stack_no", bsp.origin_stack_no);
                        jb.put("price", bsp.price);
                        jb.put("image_url", bsp.image_url);
                        jb.put("quantity", bsp.quantity);
                        jb.put("seq_no", bsp.seq_no);
                        jb.put("category_name", bsp.category_name);
                        jb.put("net_weight", bsp.net_weight);
                        jb.put("product_details_image_url", bsp.product_details_image_url);
                        jsonArray.put(jb);
                    }
                }
                jsonObject.put("records", jsonArray);
            } catch (JSONException e) {
                jsonObject.put("msg", "error");
                jsonObject.put("result", "售货机异常");
                log.e(TAG, "getSVMProductInfo: 售货机库存异常");
                e.printStackTrace();
            } finally {
                return jsonObject.toString();
            }
        }
    }

    public static List<Stock> getStocks() {

        synchronized (sBLLLock) {
            ArrayList<Stock> stocks = new ArrayList<>();
            for (String key : sBLLProductsByRoadMap.keySet()) {
                BLLStackProduct bsp = sBLLProductsByRoadMap.get(key);
                if (bsp != null) {
                    Stock stock = new Stock();
                    stock.box_no = bsp.box_no + "";
                    stock.stack_no = bsp.origin_stack_no;
                    stock.stock = bsp.quantity;
                    stocks.add(stock);
                }
            }
            return stocks;
        }
    }

}
