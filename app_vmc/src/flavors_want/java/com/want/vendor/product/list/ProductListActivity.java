package com.want.vendor.product.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import com.want.vmc.R;
import com.want.vendor.product.list.category.ProductCategoryContract;
import com.want.vendor.product.list.category.ProductCategoryFragment;
import com.want.vendor.product.list.category.ProductCategoryPresenter;
import com.want.vendor.product.list.list.ProductListContract;
import com.want.vendor.product.list.list.ProductListFragment;
import com.want.vendor.product.list.list.ProductListPresenter;
import com.want.vendor.product.list.page.PageContract;
import com.want.vendor.product.list.page.PageFragment;
import com.want.vendor.product.list.page.PagePresenter;

import vmc.vendor.VActivity;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductListActivity extends VActivity implements OnCategroyAndPageChangeListener {

    private static final String FRAGMENT_TAG_LIST = "list";
    private static final String FRAGMENT_TAG_CATEGORY = "category";
    private static final String FRAGMENT_TAG_PAGE = "page";
    private String categroyIndex="全部";
    private PagePresenter presenter;
    private boolean canNext = true;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProductListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestToolbar(false);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vendor_product_list_activity);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // 商品列表内容区
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_LIST);
        if (null == fragment && null != findViewById(R.id.product_list_content)) {
            fragment = ProductListFragment.newInstance(getResources().getConfiguration().orientation);
            ft.add(R.id.product_list_content, fragment, FRAGMENT_TAG_LIST);
        }
        if (null != fragment) {
            new ProductListPresenter((ProductListContract.View) fragment) {
                @Override
                public void onCanNext(boolean arrow) {
                    canNext = arrow;
                    isShowNext(arrow);
                }

                @Override
                public void onCanPrevious(boolean arrow) {
                  isShowonPrevious(arrow);
                }
            };
        }

        //商品分类
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_CATEGORY);
        if (null == fragment && null != findViewById(R.id.product_list_categroy)) {
            fragment = ProductCategoryFragment.newInstance();
            ft.add(R.id.product_list_categroy, fragment, FRAGMENT_TAG_CATEGORY);
        }
        if (null != fragment) {
            new ProductCategoryPresenter((ProductCategoryContract.View) fragment);
        }

        //商品分页
        fragment = fm.findFragmentByTag(FRAGMENT_TAG_PAGE);
        if (null == fragment && null != findViewById(R.id.product_list_pager)) {
            fragment = PageFragment.newInstance();
            ft.add(R.id.product_list_pager, fragment, FRAGMENT_TAG_PAGE);
        }

        if (fragment != null) {
            presenter = new PagePresenter((PageContract.View) fragment) {

                @Override
                public void onNextPage() {//下一页
                    super.onNextPage();
                    if (canNext) {//可以翻页
                        currentIndex++;
                        onCategroyAndPageChange(categroyIndex, currentIndex);
                    }
                }

                @Override
                public void onPreviousPage() {//上一页
                    super.onPreviousPage();
                    if (currentIndex > 0) {
                        currentIndex--;
                    }
                    onCategroyAndPageChange(categroyIndex, currentIndex);
                }
            };
        }

        if (null != ft) {
            ft.commit();
        }
    }

    @Override
    public void onCategroyAndPageChange(String pos, int pageIndex) {
        if (!pos.equals(categroyIndex)) {
            presenter.currentIndex = 0;
        }
        if(pageIndex==0){
            presenter.currentIndex=0;
        }

        categroyIndex = pos;
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentByTag(FRAGMENT_TAG_LIST);
        if (frag instanceof ProductListFragment) {
            ((ProductListFragment) frag).onCategroyAndPageChange(pos, pageIndex);
        }

    }


    @Override
    protected void onResume() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PAGE);
        if (null != fragment && fragment instanceof PageFragment) {
            final PageFragment f = (PageFragment) fragment;
            f.onTimeRest();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PAGE);
        if (null != fragment && fragment instanceof PageFragment) {
            final PageFragment f = (PageFragment) fragment;
            f.onTimeStop();
        }
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        // FIXME: 04/11/2016 暂时每次点击都重置计时器，后续根据需要来决定是否修改
        if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PAGE);
            if (fragment instanceof PageFragment) {
                final PageFragment f = (PageFragment) fragment;
                f.onTimeRest();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void isShowNext(boolean show){
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PAGE);
        if (fragment instanceof PageFragment) {
            final PageFragment f = (PageFragment) fragment;
                f.isShowNext(show);
        }

    }
    public void isShowonPrevious(boolean show){
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_PAGE);
        if (fragment instanceof PageFragment) {
            final PageFragment f = (PageFragment) fragment;
                f.isShowonPrevious(show);
        }

    }
}

