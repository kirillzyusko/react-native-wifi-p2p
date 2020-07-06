package io.wifi.p2p;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.facebook.react.bridge.Callback;

import static io.wifi.p2p.Utils.CHARSET;

/**
 * Created by kiryl on 18.7.18.
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class MessageServerAsyncTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "RNWiFiP2P";
    private Callback callback;

    /**
     * @param callback
     */
    public MessageServerAsyncTask(Callback callback) {
        this.callback = callback;
    }

    protected String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(Math.max(16, is.available()));
        char[] tmp = new char[4096];

        try {
            InputStreamReader reader = new InputStreamReader(is, CHARSET);
            for(int cnt; (cnt = reader.read(tmp)) > 0;)
                sb.append( tmp, 0, cnt );
        } finally {
            is.close();
        }
        return sb.toString();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.i(TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.i(TAG, "Server: connection done");

            InputStream inputstream = client.getInputStream();
            String result = convertStreamToString(inputstream);
            serverSocket.close();
            callback.invoke(result);

            return result;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "Opening a server socket");
    }
}
