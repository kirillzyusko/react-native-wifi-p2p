package io.wifi.p2p;

import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;
import com.facebook.react.bridge.Callback;

/**
 * Created by kiryl on 3.5.18.
 */

public class CallbackPeerListener extends OnListChangedCallback {
    private Callback callback;

    public CallbackPeerListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onChanged(ObservableList sender) {
        System.out.println("observable list was changed!");
        callback.invoke(sender);
    }

    @Override
    public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {

    }

    @Override
    public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {

    }
}
