package com.vmc.core.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.vmc.core.model.ContextInfo;
import com.vmc.core.model.Model;


/**
 * <b>Project:</b> Odoo<br>
 * <b>Create Date:</b> 15/12/31<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b>
 * User Info Data model.
 * <br>
 */
public class UserInfo extends Model {

    /** User name*/
    public String username;
    /** User id*/
    public int uid;
    /** User database*/
    public String db;
    /** Company id*/
    public int company_id;
    /** Session id*/
    public String session_id;
    /** User context info*/
    public ContextInfo user_context;
    /**User role*/
    public String role;


    public UserInfo() {}

    protected UserInfo(Parcel in) {
        super(in);
        this.username = in.readString();
        this.uid = in.readInt();
        this.db = in.readString();
        this.company_id = in.readInt();
        this.session_id = in.readString();
        this.user_context = in.readParcelable(ContextInfo.class.getClassLoader());
        this.role = in.readString();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
               "company_id=" + company_id +
               ", username='" + username + '\'' +
               ", uid=" + uid +
               ", db='" + db + '\'' +
               ", session_id='" + session_id + '\'' +
                ", role='" + role + '\'' +
               ", user_context=" + user_context +
               "} " + super.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.username);
        dest.writeInt(this.uid);
        dest.writeString(this.db);
        dest.writeInt(this.company_id);
        dest.writeString(this.session_id);
        dest.writeParcelable(this.user_context, 0);
        dest.writeString(this.role);
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel source) {return new UserInfo(source);}

        public UserInfo[] newArray(int size) {return new UserInfo[size];}
    };
}
