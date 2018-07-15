package io.wifi.p2p;

import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;
import com.facebook.react.bridge.Callback;
import com.google.gson.Gson;

/**
 * Created by kiryl on 3.5.18.
 */
public class CallbackPeerListener extends OnListChangedCallback {
    private Callback callback;
    private boolean isCallbackCalledOnce = false;

    public CallbackPeerListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onChanged(ObservableList sender) {
        System.out.println("observable list was changed!");
        String json = new Gson().toJson(sender);
        //callback.invoke(json);
    }

    @Override
    public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
        System.out.println("observable list was changed1!");
        String json = new Gson().toJson(sender);
        //callback.invoke(json);
    }

    @Override
    public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
        System.out.println("observable list was changed2!");
        String json = new Gson().toJson(sender);
        System.out.println(json);
        System.out.println(callback);
        //callback.invoke(234);
        if (!isCallbackCalledOnce) {
            callback.invoke(json);
            isCallbackCalledOnce = true;
        }
    }

    @Override
    public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
        System.out.println("observable list was changed3!");
        String json = new Gson().toJson(sender);
        //callback.invoke(json);
    }

    @Override
    public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
        System.out.println("observable list was changed4!");
        String json = new Gson().toJson(sender);
        System.out.println(json);
        System.out.println(callback);
        //callback.invoke(234);
        //callback.invoke(json);
    }
}
