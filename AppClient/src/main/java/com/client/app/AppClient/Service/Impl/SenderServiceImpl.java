package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.FileInfo;
import com.client.app.AppClient.DTO.FshReq;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.SenderService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.logging.Logger;


@Component
public class SenderServiceImpl implements SenderService {

    private static final Logger LOGGER = Logger.getLogger(SenderServiceImpl.class.getName());
    private final OkHttpClient client = new OkHttpClient();

    @Value("${serverAddress}")
    private String serverAddress = null;
    @Value("${local}")
    private String conn;

    private int kb = 4;
    private boolean isConnected = false;
    private FutureTask<ResponseEntity<?>> futureTask;

    @Override
    public ResponseEntity<?> reqReceiver(ReqData senderReqData) {
        if(isConnected)
            return ResponseEntity.status(HttpStatus.OK).body("ACK: Already connected.");
        //logic to send req to server for receiver
        String url = conn + serverAddress +"/fshServer/reqReceiver";
        String reqDataJson = senderReqData.toString();
        RequestBody reqBody = RequestBody.create(reqDataJson, MediaType.parse("application/json"));
        try {
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody).build();
            Response resp = client.newCall(req).execute();
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> connectionAck() {
        isConnected = true;
        LOGGER.info("Connected");
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
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
    public ResponseEntity<?> initFS(FshReq fshReq) {
        if(!isConnected) {
            LOGGER.info("initFS NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        ResponseEntity resp = shareFileInfo(fshReq);
        if(resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR || resp.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Failed to share fileInfo.");
        LOGGER.info("Initiating File Sharing.");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<ResponseEntity<?>> callable = new Callable<ResponseEntity<?>>() {
            @Override
            public ResponseEntity<?> call() throws Exception {
                return shareFile(fshReq);
            }
        };
        futureTask = new FutureTask<>(callable);
        executorService.execute(futureTask);
        try {
            return ResponseEntity.status(futureTask.get().getStatusCode()).body(futureTask.get().getBody().toString());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            if(!ex.toString().contains("CancellationException") && !ex.toString().contains("InterruptedIOException"))
                ex.printStackTrace();
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    private ResponseEntity<?> shareFileInfo(FshReq fshReq) {
        if(!isConnected) {
            LOGGER.info("shareFileInfo NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        String fileName = fshReq.getFilePath().substring(fshReq.getFilePath().lastIndexOf("/")+1);
        File file = new File(fshReq.getFilePath());
        long fileSize = file.length();
        // set kb factor for large files
        if(fileSize >= 1000000) // 1GB+
            kb = (int)(fileSize/2000);
        try {
            String url = conn + fshReq.getReceiver().getAddress() + "/fshClient/r/shareFileInfo";
            FileInfo fileInfo = new FileInfo(fileName, fileSize);
            RequestBody reqBody = RequestBody.create(fileInfo.toString(), MediaType.parse("application/json"));
            Request req = new Request.Builder()
                    .url(url)
                    .post(reqBody)
                    .build();
            Response resp = client.newCall(req).execute();
            String respContent = resp.body().string();
            if(respContent.contains("NACK")) {
                LOGGER.info("Failed to share file info with receiver.");
            }
            LOGGER.info("Sent File info to Receiver.");
            return ResponseEntity.status(resp.code()).body(respContent);
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    private ResponseEntity<?> shareFile(FshReq fshReq) {
        if(!isConnected) {
            LOGGER.info("shareFile NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        // logic to upload file - sharding - sending shards to receiver
        try {
            InputStream inputStream = new FileInputStream(fshReq.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] shard = new byte[kb * 1024];
            RequestBody reqBody = RequestBody.create(shard, MediaType.parse("text/plain; charset=utf-8"));
            Instant start = Instant.now();
            while (bufferedInputStream.read(shard, 0, shard.length) != -1) {
                //logic to send this file shard to receiver
                String url = conn + fshReq.getReceiver().getAddress() + "/fshClient/r/getFileShard";
                Request req = new Request.Builder()
                        .url(url)
                        .post(reqBody).build();

                Response resp = client.newCall(req).execute();

                if(resp.body().string().contains("NACK")) {
                    LOGGER.info("F.SH failure at receiver's end.");
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("NACK: F.SH failure at receiver's end.");
                }
            }
            Instant end = Instant.now();
            LOGGER.info("File Sharing Finished. [Time taken: " + Duration.between(start, end).getSeconds() + "sec ].");
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> stopFS(User user) {
        if(!isConnected) {
            LOGGER.info("stopFS NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        if(!futureTask.isDone())
                futureTask.cancel(true);
        LOGGER.info("F.SH stopped.");
        try {
            String url = conn + user.getAddress() + "/fshClient/r/stopFsAlert";
            Request req = new Request.Builder()
                    .url(url)
                    .build();
            Response resp = client.newCall(req).execute();
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        }  catch (Exception ex) {
            LOGGER.info(ex.toString());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Failed to alert receiver about stopFS.");
        }
    }

    @Override
    public ResponseEntity<?> disconnect(User user) {
        if(!isConnected)
            return ResponseEntity.status(HttpStatus.OK).body("ACK: Already Disconnected.");
        try {
            String url = conn + user.getAddress() + "/fshClient/r/disconnectAck";
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
