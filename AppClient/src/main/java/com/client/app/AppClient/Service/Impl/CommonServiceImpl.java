package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CommonServiceImpl implements CommonService {

    @Value("${serverAddress}")
    private static final String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean disconnect(User user) {
        //req server to get disconnected
        // TODO: Notify both sender and receiver about partner getting disconnected
        String reqDataJson = user.toString();
        String url = "http://" + serverAddress + "/fshServer/disconnect";
        RequestBody reqBody = RequestBody.create(
                reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();

            ResponseBody responseBody = client.newCall(req).execute().body();
            return responseBody.string().equals("true");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

}
