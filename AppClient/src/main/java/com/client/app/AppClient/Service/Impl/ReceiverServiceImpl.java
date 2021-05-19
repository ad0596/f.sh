package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.Service.ReceiverService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.logging.Logger;

@Component
public class ReceiverServiceImpl implements ReceiverService {

    private static final Logger LOGGER = Logger.getLogger(ReceiverServiceImpl.class.getName());

    @Value("${destFileDir}")
    private String fileDir = null;
    @Value("${serverAddress}")
    private String serverAddress = null;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<?> reqSender(ReqData rcvrReqData) {
        //logic to req server to find sender
        String url = "http://" + serverAddress + "/fshServer/reqSender";
        String reqDataJson = rcvrReqData.toString();
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
    public ResponseEntity<?> getShard(byte[] shard, String fileName) {
        //logic to save file shard
        try {
            FileOutputStream fos = new FileOutputStream(fileDir + fileName, true);
            fos.write(shard);
            fos.close();
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

}
