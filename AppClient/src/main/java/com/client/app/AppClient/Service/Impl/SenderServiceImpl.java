package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.SenderService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;


@Component
public class SenderServiceImpl implements SenderService {

    @Value("${srcFilePath}")
    private String filePath = null;
    @Value("${serverAddress}")
    private String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean reqReceiver(ReqData senderReqData) {
        //logic to send req to server for receiver
        String url = "http://" + serverAddress +"/fshServer/reqReceiver";
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
    public boolean initFS(User rcvr) {
        //logic to upload file - sharding - sending shards to receiver
        try {
            InputStream inputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            //OutputStream outputStream = new FileOutputStream(target);

            int count = 0;
            byte[] shard = new byte[4 * 1024];
            RequestBody reqBody = RequestBody.create(shard, MediaType.parse("text/plain; charset=utf-8"));
            int read;
            while ((read = bufferedInputStream.read(shard, 0, shard.length)) != -1) {
                //logic to send this file shard to receiver

                String url = "http://" + rcvr.getAddress() + "/fshClient/getFileShard";
                Request req = new Request.Builder()
                        .url(url)
                        .post(reqBody).build();

                ResponseBody responseBody = client.newCall(req).execute().body();

                //
                if(responseBody.string().equals("false")) {
                    System.out.println("Failure at receiver's end. Re-Sending shard.");
                    //re-send same shard
                    //responseBody = client.newCall(req).execute().body();
                    return false;
                }
                System.out.println("count:" + count++ + "\nreadLen: " + read);
            }
            return true;
        } catch (Exception ex) {
                System.out.println(ex);
                return false;
        }
    }

}
