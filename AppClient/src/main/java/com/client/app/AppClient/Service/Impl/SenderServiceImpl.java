package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.FshReq;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.Service.SenderService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;


@Component
public class SenderServiceImpl implements SenderService {

    private static final Logger LOGGER = Logger.getLogger(SenderServiceImpl.class.getName());

    @Value("${serverAddress}")
    private String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<?> reqReceiver(ReqData senderReqData) {
        //logic to send req to server for receiver
        String url = "http://" + serverAddress +"/fshServer/reqReceiver";
        String reqDataJson = senderReqData.toString();
        RequestBody reqBody = RequestBody.create(reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();

            Response response = client.newCall(req).execute();
            return ResponseEntity.status(response.code()).body(response.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> initFS(FshReq fshReq) {
        String fileName = fshReq.getFilePath().substring(fshReq.getFilePath().lastIndexOf("/")+1);
        //logic to upload file - sharding - sending shards to receiver
        try {
            InputStream inputStream = new FileInputStream(fshReq.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] shard = new byte[4 * 1024];
            RequestBody reqBody = RequestBody.create(shard, MediaType.parse("text/plain; charset=utf-8"));

            while (bufferedInputStream.read(shard, 0, shard.length) != -1) {
                //logic to send this file shard to receiver
                String url = "http://" + fshReq.getReceiver().getAddress() + "/fshClient/getFileShard";
                Request req = new Request.Builder()
                        .header("fileName", fileName)
                        .url(url)
                        .post(reqBody).build();

                Response response = client.newCall(req).execute();

                if(response.body().string().contains("NACK")) {
                    LOGGER.info("F.SH failure at receiver's end.");
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("NACK: F.SH failure at receiver's end.");
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

}
