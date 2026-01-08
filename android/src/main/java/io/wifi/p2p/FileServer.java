package io.wifi.p2p;

import static io.wifi.p2p.Utils.copyBytes;

import android.os.Bundle;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by kiryl on 18.7.18. Refactor by viniciuscb on 20.03.23
 *
 * <p>A simple server socket that accepts connection and writes some data on the stream.
 */
public class FileServer {
  private static final String TAG = "RNWiFiP2P";
  private final Executor executor;
  private volatile ServerSocket serverSocket;

  public FileServer() {
    this.executor = Executors.newSingleThreadExecutor();
  }

  public void start(
      String destination,
      ReadableMap extraProps,
      CustomDefinedCallback customDefinedCallback,
      Callback callback) {
    executor.execute(
        () -> {
          try {
            Boolean returnMeta = false;
            serverSocket = new ServerSocket(8988);
            Log.i(TAG, "Server: Socket opened to receive file");

            if (extraProps != null) {
              Bundle bundle = Arguments.toBundle(extraProps);
              returnMeta = bundle.getBoolean("meta");
            }

            Socket client = serverSocket.accept();
            String clientAddress = client.getInetAddress().getHostAddress();
            Log.i(TAG, "Server: connection done (receiveFile)");

            final File f = new File(destination);
            File dirs = new File(f.getParent());
            if (!dirs.exists()) dirs.mkdirs();
            f.createNewFile();
            Log.i(TAG, "Server: copying files " + f.toString());
            InputStream inputstream = client.getInputStream();
            copyBytes(inputstream, new FileOutputStream(f));

            client.close();

            String result = f.getAbsolutePath();

            Log.i(TAG, "File copied - " + result);

            if (returnMeta) {
              WritableMap map = Arguments.createMap();
              map.putString("file", result);
              map.putString("fromAddress", clientAddress);
              callback.invoke(map);
            } else {
              callback.invoke(result);
            }
            customDefinedCallback.invoke(null);

            this.stop();
          } catch (IOException e) {
            Log.e(TAG, e.getMessage());
          }
        });
  }

  public void stop() {
    if (serverSocket != null) {
      try {
        serverSocket.close();
        Log.i(TAG, "Server: Socket closed to receive file");
      } catch (IOException e) {
        Log.e(TAG, e.getMessage());
      }
    }
  }
}
