package vmc.vendor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;


import com.vmc.core.model.product.Product;
import com.vmc.core.model.product.ProductList;
import com.want.base.sdk.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import vmc.machine.core.VMCContoller;

/**
 * <b>Create Date:</b> 8/23/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductUtils {

    private static final String SP_NAME = "products";

    private ProductUtils() {
        //no instance
    }


    /**
     * 根据货道号获取产品
     *
     * @param context
     * @param roadId
     * @return {@link Product} or null.
     */
    public static Product getProductByRoadId(Context context,int boxId, int roadId) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> strings = sp.getStringSet("product", new HashSet<String>());
        if (null == strings) {
            return null;
        }

        Product product;
        for (String string : strings) {
            product = JsonUtils.fromJson(string, Product.class);
            if (roadId == product.getStackNoInt() && boxId == product.getBoxNoInt()) {
                return product;
            }
        }
        return null;
    }

    /**
     * 从本地加载商品列表
     *
     * @param context
     * @return
     */
    public static ProductList getProductList(Context context) {

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> products = sp.getStringSet("product", new HashSet<String>());
        if (null != products && 0 != products.size()) {
            ProductList productList = new ProductList();
            productList.records = new ArrayList<>();
            Product product;
            for (String s : products) {
                product = JsonUtils.fromJson(s, Product.class);
                productList.records.add(product);
            }
            productList.total = productList.records.size();//总商品数量

            ArrayList<ArrayList<Product>> listGroup = new ArrayList<>();//分组list
            ArrayList<Integer> ids = new ArrayList<>();//拿到商品Id集合

            for (int i = 0; i < productList.total; i++) {

                ids.add(productList.records.get(i).id);//记录id序列
            }

            ArrayList<Integer> arraylist = new ArrayList<>(new HashSet<>(ids));//id
            for (int i = 0; i < arraylist.size(); i++) {
                ArrayList<Product> sameProductList = new ArrayList<>();
                for (int j = 0; j < ids.size(); j++) {
                    if (arraylist.get(i) == (productList.records.get(j).id)) {
                        sameProductList.add(productList.records.get(j));
                    }
                }
                listGroup.add(sameProductList);
            }

            ArrayList<Product> dataLast = new ArrayList<>();

            for (int i = 0; i < listGroup.size(); i++) {
                boolean hasStack = false;
                for (int j = 0; j < listGroup.get(i).size(); j++) {
                    if (VMCContoller.getInstance().getStockByRoad(listGroup.get(i).get(j).getBoxNoInt(),listGroup.get(i).get(j).getStackNoInt()) > 0) {
                        hasStack = true;
                        dataLast.add(listGroup.get(i).get(j));
                        break;
                    }
                }
                if (!hasStack) {
                    dataLast.add(listGroup.get(i).get(listGroup.get(i).size() - 1));
                }
            }


            // 获取到的商品可能会乱序, 根据货道重新排序
            Collections.sort(dataLast, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    return lhs.getStackNoInt() - rhs.getStackNoInt();
                }
            });
            ArrayList<Product> data = dataLast;//总数据
            ArrayList<Product> noStockIndex = new ArrayList<Product>();//记录无货List
            for (Product item : data) {//先合并相同商品,把无货挑出来
                if (VMCContoller.getInstance().getStockByRoad(item.getBoxNoInt(),item.getStackNoInt()) == 0) {
                    noStockIndex.add(item);
                }
            }
            for (Product item : noStockIndex) {//移除无货的
                data.remove(item);
            }
            data.addAll(noStockIndex);//把无货加在后面
            ProductList productList2 = new ProductList();
            productList2.records = data;
            return  productList2;
        }
        return null;
    }


    /**
     * 获取商品
     *
     * @param context
     * @param categroyIndex 分类索引
     * @param pageIndex         分页索引
     * @return 商品列表    
     */
    public static ProductList getProductListByPageIndex(Context context, int categroyIndex, int pageIndex) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> products = sp.getStringSet("product", new HashSet<String>());
        if (null != products && 0 != products.size()) {
            ProductList productList = new ProductList();
            productList.records = new ArrayList<>();
            Product product;
            for (String s : products) {
                product = JsonUtils.fromJson(s, Product.class);
                productList.records.add(product);
            }
            productList.total = productList.records.size();//总商品数量

            ArrayList<ArrayList<Product>>  listGroup= new ArrayList<>();//分组list
            ArrayList<Integer> ids = new ArrayList<>();//拿到商品Id集合

            for (int i = 0; i < productList.total ; i++) {

                ids.add(productList.records.get(i).id);//记录id序列
            }

            ArrayList<Integer> arraylist = new ArrayList<>(new HashSet<>(ids));//id
            for (int i = 0; i <arraylist.size(); i++) {
                ArrayList<Product> sameProductList = new ArrayList<>();
                for (int j = 0; j < ids.size(); j++) {
                    if (arraylist.get(i)==(productList.records.get(j).id)){
                        sameProductList.add(productList.records.get(j));
                    }
                }
                listGroup.add(sameProductList);
            }

            ArrayList<Product> dataLast = new ArrayList<>();

            for (int i = 0; i < listGroup.size(); i++) {
                boolean hasStack = false;
                for (int j = 0; j < listGroup.get(i).size(); j++) {
                    if (VMCContoller.getInstance().getStockByRoad(listGroup.get(i).get(j).getBoxNoInt(),listGroup.get(i).get(j).getStackNoInt())>0){
                        hasStack = true;
                        dataLast.add(listGroup.get(i).get(j));
                        break;
                    }
                }
                if(!hasStack){
                    dataLast.add(listGroup.get(i).get(listGroup.get(i).size()-1));
                }
            }


            // 获取到的商品可能会乱序, 根据货道重新排序
            Collections.sort(dataLast, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    return lhs.getStackNoInt() - rhs.getStackNoInt();
                }
            });
            ArrayList<Product> data = dataLast;//总数据
            ArrayList<Product> noStockIndex = new ArrayList<Product>();//记录无货List
            ArrayList<Integer> productIdList = new ArrayList<>();
            for (Product item : data) {//先合并相同商品,把无货挑出来
                if (VMCContoller.getInstance().getStockByRoad(item.getBoxNoInt(),item.getStackNoInt()) <= 0) {
                    noStockIndex.add(item);
                }
            }
            for (Product item : noStockIndex) {//移除无货的
                data.remove(item);
            }
            data.addAll(noStockIndex);//把无货加在后面
            ArrayList<String> categroys = getProductCategory(context);//所有分类
            ProductList productList2 = new ProductList();//返回的分类的所有商品对象
            productList2.records = new ArrayList<>();//返回的分类所有商品列表
            if (categroyIndex == 0) {//如果是第一个分类
                int max = ((pageIndex + 1) * 9) > data.size() ? data.size() : (pageIndex + 1) * 9;
                for (int i = pageIndex * 9 > data.size() ? data.size() : pageIndex * 9; i < max; i++) {
                    productList2.records.add(data.get(i));
                }
                return productList2;
            } else {//先拿到所有分类的所有商品，再进行分页
                productList2.records.clear();
                for (int j = 0; j < data.size(); j++) {
                    if (categroys.get(categroyIndex).equals(data.get(j).product_type)) {
                        productList2.records.add(data.get(j));
                    }
                }
                ProductList productList3 = new ProductList();
                productList3.records = new ArrayList<>();
                //进行分页
                int max = ((pageIndex + 1) * 9) > productList2.records.size() ? productList2.records.size() : (pageIndex + 1) * 9;
                for (int i = pageIndex * 9 > productList2.records.size() ? productList2.records.size() : pageIndex * 9; i < max; i++) {
                    productList3.records.add(productList2.records.get(i));
                }
                return productList3;
            }
        }
        return null;
    }

    /**
     * 判断是否存在下一页
     *
     * @param context
     * @param categroyIndex 分类
     * @param page          页数
     * @return
     */
    public static boolean haveNextPage(Context context, int categroyIndex, int page) {


        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

        Set<String> products = sp.getStringSet("product", new HashSet<String>());

        if (null != products && 0 != products.size()) {
            ProductList productList = new ProductList();
            productList.records = new ArrayList<>();
            Product product;
            for (String s : products) {
                product = JsonUtils.fromJson(s, Product.class);
                productList.records.add(product);
            }
            productList.total = productList.records.size();

            ArrayList<ArrayList<Product>>  listGroup= new ArrayList<>();//分组list
            ArrayList<Integer> ids = new ArrayList<>();//拿到商品Id集合

            for (int i = 0; i < productList.total ; i++) {
                ids.add(productList.records.get(i).id);//记录id序列
            }

            ArrayList<Integer> arraylist = new ArrayList<>(new HashSet<>(ids));//id
            for (int i = 0; i <arraylist.size(); i++) {
                ArrayList<Product> samePrductList = new ArrayList<>();
                for (int j = 0; j < ids.size(); j++) {
                    if (arraylist.get(i)==(productList.records.get(j).id)){
                        samePrductList.add(productList.records.get(j));
                    }
                }
                listGroup.add(samePrductList);
            }

            ArrayList<Product> dataLast = new ArrayList<>();

            for (int i = 0; i < listGroup.size(); i++) {
                boolean hasStack = false;
                for (int j = 0; j < listGroup.get(i).size(); j++) {
                    if (VMCContoller.getInstance().getStockByRoad(listGroup.get(i).get(j).getBoxNoInt(),listGroup.get(i).get(j).getStackNoInt())>0){
                        hasStack = true;
                        dataLast.add(listGroup.get(i).get(j));
                        break;
                    }
                }
                if(!hasStack){
                    dataLast.add(listGroup.get(i).get(listGroup.get(i).size()-1));
                }
            }

            // 获取到的商品可能会乱序, 根据货道重新排序
            Collections.sort(dataLast, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    return lhs.getStackNoInt() - rhs.getStackNoInt();
                }
            });

            ArrayList<Product> data = dataLast;//总数据
            ArrayList<String> categroys = getProductCategory(context);
            ProductList productList2 = new ProductList();
            productList2.records = new ArrayList<>();
            if (categroyIndex == 0) {
                return ((page + 1) * 9) < data.size();
            } else {//先拿到所有分类的所有商品，再进行分页
                for (int j = 0; j < data.size(); j++) {
                    if (categroys.get(categroyIndex).equals(data.get(j).product_type)) {
                        productList2.records.add(data.get(j));
                    }
                }

                return ((page + 1) * 9) < productList2.records.size();
            }


        }


        return false;

    }


    /**
     * 获取商品分类
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getProductCategory(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> productCateSet = sp.getStringSet("categroy", new HashSet<String>());
        ArrayList categroy = new ArrayList<String>(productCateSet);
        categroy.add(0, "全部");
        return categroy;
    }


    /**
     * 把商品存储到本地
     *
     * @param context
     * @param productList
     */
    public static void setProductList(Context context, ProductList productList) {
        if (null == productList || null == productList.records) {
            return;
        }

        Set<String> products = new HashSet<>();
        for (Product record : productList.records) {
            products.add(JsonUtils.toJson(record));
        }
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("product", products);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 保存商品类别
     *
     * @param context
     * @param productList
     */
    public static void setProductCategory(Context context, ProductList productList) {
        if (null == productList || null == productList.product_type_list) {
            return;
        }
        Set set = new HashSet(productList.product_type_list);
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet("categroy", set);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }


    public  static ArrayList<Product> getProductsById(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Set<String> products = sp.getStringSet("product", new HashSet<String>());
        if (null != products && 0 != products.size()) {
            ArrayList<Product> productList = new ArrayList<>();
            Product product;
            for (String s : products) {
                product = JsonUtils.fromJson(s, Product.class);
                if (product.id == id &&
                    VMCContoller.getInstance()
                                .getStockByRoad(product.getBoxNoInt(), product.getStackNoInt()) > 0) {
                    productList.add(product);
                }
            }
            return productList;
        }
        return new ArrayList<Product>();
    }

}
