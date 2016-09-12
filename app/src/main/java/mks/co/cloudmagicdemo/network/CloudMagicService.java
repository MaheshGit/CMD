package mks.co.cloudmagicdemo.network;

import java.util.ArrayList;

import mks.co.cloudmagicdemo.network.model.FullMessage;
import mks.co.cloudmagicdemo.network.model.Message;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Mahesh on 7/9/16.
 */
public interface CloudMagicService {
    // complete url : http://127.0.0.1:8088/api/message/

    @GET("message/")
    Call<ArrayList<Message>> getCloudMessages();

    @GET("message/{id}/")
    Call<FullMessage> getMessageDetail(@Path("id") int id);

    @DELETE("message/{id}/")
    Call<Void> deleteMessage(@Path("id") int id);
}
