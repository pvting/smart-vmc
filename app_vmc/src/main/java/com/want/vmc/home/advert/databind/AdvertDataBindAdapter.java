package com.want.vmc.home.advert.databind;

import android.databinding.BindingMethod;
import android.databinding.BindingMethods;

import com.pili.pldroid.player.widget.PLVideoView;

/**
 * <b>Create Date:</b> 10/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
@BindingMethods(
        {
                @BindingMethod(type = PLVideoView.class,
                               attribute = "onCompletion",
                               method = "setOnCompletionListener"),
                @BindingMethod(type = PLVideoView.class,
                               attribute = "onError",
                               method = "setOnErrorListener"),
                @BindingMethod(type = PLVideoView.class,
                               attribute = "onPrepared",
                               method = "setOnPreparedListener")
        })
public class AdvertDataBindAdapter {


}
