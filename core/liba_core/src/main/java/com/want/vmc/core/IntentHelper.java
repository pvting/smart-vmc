package com.want.vmc.core;

import android.content.Context;

import cn.campusapp.router.Router;
import cn.campusapp.router.route.ActivityRoute;

/**
 * <b>Project:</b> project_vmc<br>
 * <b>Create Date:</b> 8/10/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * 完全解耦的Activity路由器。具体参见: https://github.com/campusappcn/AndRouter
 * <br>
 */
public class IntentHelper {

    /**
     * DEMO: 启动商品详情页面
     *
     * @param context
     * @param value1
     * @param value2
     */
    public static void startProduct(Context context, int value1, long value2) {
        // ::demo::

        // activity product: url
        // params1, params2
        ActivityRoute router = (ActivityRoute) Router.getRoute("url");
        router.withParams("key1", value1)
              .withParams("key2", value2);
        router.open();
    }
}
