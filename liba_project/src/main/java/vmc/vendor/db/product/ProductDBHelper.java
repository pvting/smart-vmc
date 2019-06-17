package vmc.vendor.db.product;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <b>Create Date:</b> 26/10/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class ProductDBHelper extends SQLiteOpenHelper {
    public static final String PRODUCT_DB_NAME = "product.db";
    public static final int PRODUCT_DB_VErSION = 1;


    public ProductDBHelper(Context context) {
        super(context, PRODUCT_DB_NAME, null, PRODUCT_DB_VErSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: 26/10/2016 创建数据库
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 26/10/2016 升级数据库
    }
}
