package mks.co.cloudmagicdemo.network;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import mks.co.cloudmagicdemo.CloudApplication;
import mks.co.cloudmagicdemo.utils.CommonUtilities;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahesh on 7/9/16.
 */
public class CloudMagicManager {
    private static CloudMagicService API_MANAGER;
    private static long SIZE_OF_CACHE = 100 * 1024 * 1024;

    public static void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.40:8088/api/")
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API_MANAGER = retrofit.create(CloudMagicService.class);
    }

    public static CloudMagicService getApiManager() {
        if (API_MANAGER == null) {
            init();
        }
        return API_MANAGER;
    }


    private static final Interceptor OFFLINE_CACHE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            // max-age = 86400 = 1 day and max-stale=2419200 = 4 weeks
            String cacheHeaderValue = CommonUtilities.isConnectedToInternet(CloudApplication.context)
                    ? "public, max-age=86400"
                    : "public, only-if-cached, max-stale=2419200";
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheHeaderValue)
                    .build();
        }
    };

    private static Response getCachedResponse(Interceptor.Chain chain) throws IOException {
        CacheControl cacheControl = new CacheControl
                .Builder()
                .onlyIfCached()
                .maxStale(5, TimeUnit.MINUTES).build();

        Request request = chain.request().newBuilder()
                .cacheControl(cacheControl)
                .removeHeader("Pragma")
                .build();

        Response forceCacheResponse = chain.proceed(request);
        return forceCacheResponse.newBuilder()
                .build();
    }

    private static OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(provideCache())
                .addNetworkInterceptor(OFFLINE_CACHE_INTERCEPTOR)
                //.addNetworkInterceptor(loggingInterceptor)
                .build();
        return okHttpClient;
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            if (cache == null)
                cache = new Cache(new File(CloudApplication.context.getCacheDir(), "mg-cache"), SIZE_OF_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Cache Error", "Could not create cache");
        }
        return cache;
    }

    public static Interceptor provideOfflineInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (CommonUtilities.isConnectedToInternet(CloudApplication.context)) {
                    request = request.newBuilder().header("Cache-Control", "only-if-cached").build();
                } else {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder().cacheControl(cacheControl).build();
                    // request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                }
                Response originalResponse = chain.proceed(request);
                return originalResponse;
            }
        };
    }
}
