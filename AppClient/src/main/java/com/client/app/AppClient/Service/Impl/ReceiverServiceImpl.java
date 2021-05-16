package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.Service.ReceiverService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class ReceiverServiceImpl implements ReceiverService {

    @Value("${destFilePath}")
    private static String filePath = null;
    @Value("${serverAddress}")
    private static final String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean reqSender(ReqData rcvrReqData) {
        //logic to req server to find sender
        String url = "https://" + serverAddress + "/reqSender";
        String reqDataJson = rcvrReqData.getReceiver().toString();
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
            return false;
        }
    }

    @Override
    public boolean getShard(byte[] shard) {
        //logic to save file shard
        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(shard, 0, shard.length);
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

}
