package com.want.vmc.home.advert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.vmc.core.model.ads.AdList;
import com.vmc.core.model.ads.Ads;
import com.vmc.core.utils.AdsUtils;
import com.vmc.core.worker.ads.AdsDownloadWorker;
import com.vmc.core.worker.ads.AdsUpdaterWorker;
import com.want.base.sdk.framework.app.mvp.AbsViewModel;

import java.io.File;

import vmc.core.log;

//import vmc.vendor.model.ads.AdsDownloader;

/**
 * <b>Create Date:</b> 10/21/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class AdvertViewModel extends AbsViewModel implements PLMediaPlayer.OnCompletionListener,
                                                             PLMediaPlayer.OnErrorListener {
    private static final String TAG = "AdvertViewModel";
    public static int LoadingChangeTag = 0;
    private int mVideoNum=0;
    private Context mContext;
    private PLVideoView mVideoView;
    private AdList mAdList;
    private BroadcastReceiver mAdsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            log.v(TAG, "mAdsUpdateReceiver, 视频更新广播 ");
            if (AdsUpdaterWorker.ACTION_ADS_UPDATE.equals(action)) {
                if (!mVideoView.isPlaying()) {
                    log.v(TAG, "mAdsUpdateReceiver  更新绑定数据 ");
                    notifyChange();
                }
            }
        }
    };

    private BroadcastReceiver mAdsDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (AdsDownloadWorker.ACTION.equals(action)) {
                // 只接收处理一次
                context.unregisterReceiver(mAdsDownloadReceiver);
                mAdsDownloadReceiver = null;
                notifyChange();
            }
        }
    };


    public AdvertViewModel(PLVideoView view,Context  context) {
        this.mVideoView = view;
        this.mContext=context;
    }

    public AdvertViewModel(Context context) {
        super(context);
    }

    @Bindable
    public String getVideoPath() {

        String videoPath = getVideoPathByNum();

        log.v(TAG, "getVideoPath, 视频路径: " + videoPath);

        if (videoPath == null) {
            return "";
        } else {
            return videoPath;
        }
    }


    public int getDisplayAspectRatio() {
        return PLVideoView.ASPECT_RATIO_PAVED_PARENT;
    }


    public void onResume() {
        mVideoView.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AdsUpdaterWorker.ACTION_ADS_UPDATE);//广告
        mContext.registerReceiver(mAdsUpdateReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(AdsDownloadWorker.ACTION);
        mContext.registerReceiver(mAdsDownloadReceiver, filter);
    }

    public void onPause() {
        mVideoView.pause();
    }

    public void onDestroy() {
        mVideoView.stopPlayback();
        if (null != mAdsUpdateReceiver) {
            mContext.unregisterReceiver(mAdsUpdateReceiver);
        }

        if (null != mAdsDownloadReceiver) {
            mContext.unregisterReceiver(mAdsDownloadReceiver);
        }
    }

    /**
     *  重新获取本地视频列表，循环播放
     * @param plMediaPlayer
     */
    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        log.v(TAG, "onCompletion , 视频轮播的下标:" + mVideoNum);

        mVideoNum++;
        if (null == mAdList || null == mAdList.records || mAdList.records.size() == mVideoNum) {
            mVideoNum = 0;
        }

        String videoPath = getVideoPathByNum();
        log.v(TAG, "onCompletion , videoPath=:" + videoPath);

        if (videoPath == null) {
            notifyChange();
            return;
        }


        mVideoView.setVideoPath(videoPath);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.start();
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
        //该对象用于监听播放器的错误消息，一旦播放过程中产生任何错误信息，SDK 都会回调该接口，
        //返回值决定了该错误是否已经被处理，如果返回 false，则代表没有被处理，下一步则会触发 onCompletion 消息。
        log.v(TAG, "onError, 监听播放器的错误消息: " + errorCode);

        switch (errorCode) {
            case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                log.v(TAG, "onError:Invalid URL ! -2 无效的 URL");
                break;
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                log.v(TAG, "onError:Network IO Error ! -5 网络异常");
                break;
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                log.v(TAG, "onError:Stream disconnected ! -11 与服务器连接断开");
                break;
            case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                log.v(TAG, "onError:Empty playlist ! -541478725 空的播放列表 ");
                break;
            case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                log.v(TAG, "onError:404 resource not found ! -875574520 播放资源不存在");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                log.v(TAG, "onError:Connection refused ! -111 服务器拒绝连接");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                log.v(TAG, "onError:Connection timeout ! -110 连接超时");
                break;
            case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                log.v(TAG, "onError:Unauthorized Error ! -825242872 未授权，播放一个禁播的流");
                break;
            case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                log.v(TAG, "onError:Prepare timeout ! -2001 播放器准备超时 ");
                break;
            case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                log.v(TAG, "onError:Read frame timeout ! -2002 读取数据超时 ");
                break;
            case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                 log.v(TAG, "onError:unknown error ! -1 未知错误");
                break;
        }

        if (null == mAdList || null == mAdList.records || mAdList.records.size() == 0) {
            return true;
        }
        mVideoView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mVideoView.isPlaying()) {
                    mVideoNum++;
                    if (null != mAdList && null != mAdList.records && mAdList.records.size() == mVideoNum) {
                        mVideoNum = 0;
                    }
                    notifyChange();
                }
            }
        }, 300);
        return true;
    }

    /**
     * 重新获取更新后的视频列表数据，如果有视频更新可以获取更新内容
     * @return  返回视频本地或网络路径
     */
    private String getVideoPathByNum() {
        mAdList = AdsUtils.getAdList(mContext, Ads.AdType.VIDEO);
        if (null != mAdList && null != mAdList.records && mAdList.records.size() > 0) {
            log.v(TAG, "getVideoPathByNum, 视频轮播的数量: " + mAdList.records.size());
            if (mAdList.records.size() <=mVideoNum) {
                mVideoNum = 0;
            }
            String url = mAdList.records.get(mVideoNum).ad_url;
            if (!TextUtils.isEmpty(url)) {
                log.v(TAG, "getVideoPathByNum, 视频链接: " + mVideoNum + ", " +
                           mAdList.records.get(mVideoNum).ad_url);
                File file = AdsUtils.getCacheFile(mContext, mAdList.records.get(mVideoNum).ad_url);
                if(!file.exists()){
                   return null;
                }else{
                    int length =AdsUtils.getDownloadFileAttr(mContext,file.getName());
                    if(length!=-1&&length!=file.length()){
                        file.delete();
                        return null;
                    }

                }
                String videoPath = file.getAbsolutePath();
                log.v(TAG, "getVideoPathByNum, 播放视频本地链接: " + videoPath);
                return videoPath;
            } else {
                log.w(TAG, "getVideoPathByNum, 播放视频链接: " + mVideoNum + ", 为空");
            }
        }
        return null;
    }
   @Bindable
    public int getIsLoading(){



       /**
        * 如果本地有可以播放的视频，就隐藏图片
        */
       if(haveVideo()){
           mVideoView.setVisibility(View.VISIBLE);
           return View.GONE;
       }
       mVideoView.setVisibility(View.INVISIBLE);
       final String path = getVideoPathByNum();

       return TextUtils.isEmpty(path) ? View.VISIBLE :
              new File(path).exists() ? View.GONE : View.VISIBLE;
   }

    /**
     * 根据后台的url查看本地有没有下好的视频
     * @return 有或者没有
     */
    private boolean haveVideo() {
        boolean result = false;
        AdList adList = AdsUtils.getAdList(mContext, Ads.AdType.VIDEO);
        if (null != adList && null != adList.records && adList.records.size() > 0) {
           for(int i = 0;i<adList.records.size();i++){
               String url = adList.records.get(i).ad_url;
               if (!TextUtils.isEmpty(url)) {
                   File file = AdsUtils.getCacheFile(mContext, url);
                   if(file.exists()){
                       result = true;
                       break;
                   }else{
                       result =  false;
                   }
               }
           }

        }
        return result;
    }
}
