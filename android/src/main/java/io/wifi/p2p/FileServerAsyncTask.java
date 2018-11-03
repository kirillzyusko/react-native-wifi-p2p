package io.wifi.p2p;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.facebook.react.bridge.Callback;

import static io.wifi.p2p.Utils.copyBytes;

/**
 * Created by kiryl on 18.7.18.
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private Callback callback;

    /**
     * @param context
     */
    public FileServerAsyncTask(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            System.out.println("Server: Socket opened");
            Socket client = serverSocket.accept();
            System.out.println("Server: connection done");
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");
            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            System.out.println("server: copying files " + f.toString());
            InputStream inputstream = client.getInputStream();
            copyBytes(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            System.out.println("File copied - " + result);
            callback.invoke(result);
        }
    }
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        System.out.println("Opening a server socket");
    }
}
