package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.SenderReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.SenderService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;


@Service
public class SenderServiceImpl implements SenderService {

    @Value("${srcFilePath}")
    private static String filePath = null;
    @Value("${serverAddress}")
    private static final String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean reqReceiver(SenderReqData senderReqData) {
        //logic to send req to server for receiver
        String url = "https://" + serverAddress +"/reqReceiver";
        String reqDataJson = senderReqData.toString();
        RequestBody reqBody = RequestBody.create(
                reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();

            ResponseBody responseBody = client.newCall(req).execute().body();
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

    @Override
    public void initFS(User rcvr) {
        //logic to upload file - sharding -sending shards to receiver

        try {
            InputStream inputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            //OutputStream outputStream = new FileOutputStream(target);

            byte[] shard = new byte[4 * 1024];
            while ((bufferedInputStream.read(shard, 0, shard.length)) != -1) {
                //logic to send this file shard to receiver

                String url = "https://" + rcvr.getAddress() + "/getFileShard";
                Request req = new Request.Builder()
                        .url(url)
                        .get().build();

                ResponseBody responseBody = client.newCall(req).execute().body();

                while(responseBody.string().equals("false")) {
                    System.out.println("Failure at receiver's end. Re-Sending shard.");
                    //re-send same shard
                    responseBody = client.newCall(req).execute().body();
                }
            }

        } catch (Exception ex) {
                System.out.println(ex);
        }
    }

}
