package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.FileInfo;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.ReceiverService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

@Component
public class ReceiverServiceImpl implements ReceiverService {

    private static final Logger LOGGER = Logger.getLogger(ReceiverServiceImpl.class.getName());

    @Value("${destFileDir}")
    private String fileDir = null;
    @Value("${serverAddress}")
    private String serverAddress = null;
    @Value("${local}")
    private String conn;

    private boolean isConnected = false;
    private String fileName;
    private long fileSize;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<?> reqSender(ReqData rcvrReqData) {
        if(isConnected)
            ResponseEntity.status(HttpStatus.OK).body("ACK: Already Connected.");
        //logic to req server to find sender
        String url = conn + serverAddress + "/fshServer/reqSender";
        String reqDataJson = rcvrReqData.toString();
        RequestBody reqBody = RequestBody.create(reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();
            Response resp = client.newCall(req).execute();
            isConnected = true;
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> disconnectAck() {
        if(isConnected) {
            isConnected = false;
            LOGGER.info("Disconnected");
        }
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> shareFileInfo(FileInfo fileInfo) {
        this.fileName = fileInfo.getFileName();
        this.fileSize = fileInfo.getFileSize();
        // set unique name for file in existing directory
        File file = new File(fileDir + fileName);
        while(file.exists()) {
            fileName = "_" + fileName;
            file = new File(fileDir + fileName);
        }
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> getShard(byte[] shard) {
        if(!isConnected) {
            LOGGER.info("getShard NOT_ALLOWED. Not connected with Sender.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
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

    @Override
    public ResponseEntity<?> stopFsAlert() {
        if(!isConnected) {
            LOGGER.info("stopFsAlert NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        LOGGER.info("F.SH stopped by Sender.");
        File file = new File(fileDir + fileName);
        // Delete incomplete file
        if(file.length() != fileSize)
            file.delete();
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> finishFsAlert() {
        if(!isConnected) {
            LOGGER.info("finishFsAlert NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        LOGGER.info("F.SH finished.");
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> disconnect(User user) {
        if(!isConnected)
            return ResponseEntity.status(HttpStatus.OK).body("ACK: Already Disconnected.");
        try {
            String url = conn + user.getAddress() + "/fshClient/disconnectAckS";
            isConnected = false;
            Request req = new Request.Builder()
                    .url(url)
                    .build();
            Response resp = client.newCall(req).execute();
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NACK: " + ex);
        }
    }

}
