package io.wifi.p2p;

import static io.wifi.p2p.Utils.copyBytes;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by zyusk on 15.07.2018.
 *
 * <p>A service that process each file transfer request i.e Intent by opening a socket connection
 * with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {
  private static final int SOCKET_TIMEOUT = 5000;
  public static final String ACTION_SEND_FILE = "io.wifi.p2p.SEND_FILE";
  public static final String EXTRAS_FILE_PATH = "file_url";
  public static final String EXTRAS_ADDRESS = "go_host";
  public static final String EXTRAS_PORT = "go_port";
  public static final String REQUEST_RECEIVER_EXTRA = "REQUEST_RECEIVER_EXTRA";
  private static final String TAG = "RNWiFiP2P";

  public FileTransferService(String name) {
    super(name);
  }

  public FileTransferService() {
    super("FileTransferService");
  }

  /*
   * (non-Javadoc)
   * @see android.app.IntentService#onHandleIntent(android.content.Intent)
   */
  @Override
  protected void onHandleIntent(Intent intent) {
    long start = System.currentTimeMillis();
    Context context = getApplicationContext();
    if (intent.getAction().equals(ACTION_SEND_FILE)) {
      String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
      String host = intent.getExtras().getString(EXTRAS_ADDRESS);
      int port = intent.getExtras().getInt(EXTRAS_PORT);
      ResultReceiver rec = intent.getParcelableExtra(REQUEST_RECEIVER_EXTRA);
      Socket socket = new Socket();
      Bundle bundle = new Bundle();

      try {
        Log.i(TAG, "Opening client socket - ");
        socket.bind(null);
        socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

        Log.i(TAG, "Client socket connected - " + socket.isConnected());
        OutputStream stream = socket.getOutputStream();
        ContentResolver cr = context.getContentResolver();
        InputStream is = null;

        try {
          is = cr.openInputStream(Uri.parse(fileUri));
        } catch (FileNotFoundException e) {
          Log.e(TAG, e.getMessage());
        }

        copyBytes(is, stream);
        Log.i(TAG, "Client: Data written");

        long time = System.currentTimeMillis() - start;
        bundle.putLong("time", time);
        bundle.putString("file", fileUri);

        rec.send(0, bundle);
      } catch (IOException e) {
        Log.e(TAG, e.getMessage());

        bundle.putString("error", e.getMessage());
        rec.send(1, bundle);
      } finally {
        if (socket != null) {
          if (socket.isConnected()) {
            try {
              socket.close();
            } catch (IOException e) {
              // Give up
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
}
