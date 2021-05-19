package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Component
public class CommonServiceImpl implements CommonService {

    private static final Logger LOGGER = Logger.getLogger(CommonServiceImpl.class.getName());

    @Value("${serverAddress}")
    private static final String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<?> disconnect(User user) {
        //req server to get disconnected
        String reqDataJson = user.toString();
        String url = "http://" + serverAddress + "/fshServer/disconnect";
        RequestBody reqBody = RequestBody.create(
                reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();

            Response response = client.newCall(req).execute();
            return ResponseEntity.status(response.code()).body(response.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex.toString());
        }
    }

}
