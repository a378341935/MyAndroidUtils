package myandroidutils.lfx.com.myandroidutils.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create By Fangxing Liu
 *
 * 封装OKHttpUtils
 * 功能：线程池管理，单例，回调中直接进行UI操作，建议在app中初始化
 */
public class OkHttpUtil {
    private Handler handler;
    private OkHttpClient okHttpClient;
    private ExecutorService mExecutor;

    private OkHttpUtil() {
        okHttpClient = new OkHttpClient();
        mExecutor = Executors.newFixedThreadPool(5);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                OkHttpCallBack mCallback;
                switch (message.what) {
                    case 0:
                        mCallback = (OkHttpCallBack) message.obj;
                        mCallback.onFailure();
                        break;
                    case 1:
                        Object[] objs = (Object[]) message.obj;
                        String result = (String) objs[0];
                        mCallback = (OkHttpCallBack) objs[1];
                        mCallback.onSuccess(result);
                        break;
                }
                return false;
            }
        });
    }

    ;

    private static class InstanceFactory {
        private static OkHttpUtil okHttpUtil = new OkHttpUtil();
    }

    public OkHttpUtil getInstance(Context context) {
        return InstanceFactory.okHttpUtil;
    }

    public void sendGetRequest(final String url, final OkHttpCallBack okCallback) {
        final Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = handler.obtainMessage();
                msg.what = 0;
                msg.obj = okCallback;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message msg = handler.obtainMessage();
                msg.what = 1;
                Object[] objs = {string, okCallback};
                msg.obj = objs;
                handler.sendMessage(msg);
            }
        };
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                okHttpClient.newCall(request).enqueue(callback);
            }
        });
    }

    public void sendPostRequest(final String url, HashMap<String, String> map, final OkHttpCallBack callback) {
        FormBody.Builder eBuilder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            eBuilder.add(entry.getKey(), entry.getValue());
        }
        final RequestBody requestBody = eBuilder.build();
        final Callback responseCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = handler.obtainMessage();
                msg.what = 0;
                msg.obj = callback;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = handler.obtainMessage();
                String string = response.body().string();
                Object[] obj = {string, callback};
                msg.what = 0;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        };

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).post(requestBody).build();
                okHttpClient.newCall(request).enqueue(responseCallback);
            }
        });
    }

    public interface OkHttpCallBack {
        /**
         * @param result
         * Connection&Callback Success
         */
        public void onSuccess(String result);

        /**
         * Connection|Callback Fail
         */
        public void onFailure();
    }

}
