package io.wifi.p2p;

import static io.wifi.p2p.Utils.copyBytes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.facebook.react.bridge.Callback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Kiryl on 18.7.18.
 *
 * <p>A simple server socket that accepts connection and writes some data on the stream.
 */
public class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
  private static final String TAG = "RNWiFiP2P";
  private Callback callback;
  private CustomDefinedCallback customDefinedCallback;
  private String destination;

  /**
   * @param context
   * @param callback
   * @param destination
   */
  public FileServerAsyncTask(
      Context context,
      Callback callback,
      String destination,
      CustomDefinedCallback customDefinedCallback) {
    this.callback = callback;
    this.destination = destination;
    this.customDefinedCallback = customDefinedCallback;
  }

  @Override
  protected String doInBackground(Void... params) {
    try {
      ServerSocket serverSocket = new ServerSocket(8988);
      Log.i(TAG, "Server: Socket opened");
      Socket client = serverSocket.accept();
      Log.i(TAG, "Server: connection done");
      final File f = new File(destination);
      File dirs = new File(f.getParent());
      if (!dirs.exists()) dirs.mkdirs();
      f.createNewFile();
      Log.i(TAG, "Server: copying files " + f.toString());
      InputStream inputstream = client.getInputStream();
      copyBytes(inputstream, new FileOutputStream(f));
      serverSocket.close();
      return f.getAbsolutePath();
    } catch (IOException e) {
      Log.e(TAG, e.getMessage());
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
      Log.i(TAG, "File copied - " + result);
      callback.invoke(result);
      customDefinedCallback.invoke(null);
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
