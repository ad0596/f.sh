package com.client.app.FshClient.Service.AppService.Impl;

import com.client.app.FshClient.DTO.FileInfo;
import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import com.client.app.FshClient.Service.AppService.SenderService;
import com.client.app.FshClient.Service.ShellService.ConsoleService;
import com.client.app.FshClient.Service.ShellService.FshPromptProvider;
import com.client.app.FshClient.Util.UserType;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;


@Component
public class SenderServiceImpl implements SenderService {

    private static final Logger LOGGER = Logger.getLogger(SenderServiceImpl.class.getName());
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private ConsoleService console;
    @Autowired
    private FshPromptProvider promptProvider;

    @Value("${serverAddress}")
    private String serverAddress = null;
    @Value("${conn}")
    private String conn;

    private int kb = 4;
    private boolean isConnected = false;
    private User rcvrInfo;
    private FutureTask<ResponseEntity<?>> futureTask;

    @Override
    public Boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void setConnectionStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public ResponseEntity<?> reqReceiver(ReqData senderReqData) {
        if(this.isConnected)
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
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> connectionAck(User rcvr) {
        this.isConnected = true;
        LOGGER.info("Connected");
        rcvrInfo = rcvr;

        // Shell output
        console.writeInfo("Connected");
        // connection event for shell
        console.updateByConnectionEvent(UserType.SENDER, this.isConnected);
        console.write(promptProvider.getPrompt());

        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> disconnectAck() {
        if(this.isConnected) {
            this.isConnected = false;
            LOGGER.info("Disconnected");
        }

        // Shell output
        console.writeInfo("Disconnected");
        // connection event for shell
        console.updateByConnectionEvent(UserType.SENDER, this.isConnected);
        console.write(promptProvider.getPrompt());

        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> initFS(String filePath) {
        if(!this.isConnected) {
            LOGGER.info("initFS NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        ResponseEntity resp = shareFileInfo(filePath);
        if(resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR || resp.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Failed to share fileInfo.\n" + resp.getBody().toString());
        LOGGER.info("Initiating File Sharing.");

        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Callable<ResponseEntity<?>> callableTask = new Callable<ResponseEntity<?>>() {
                @Override
                public ResponseEntity<?> call() throws Exception {
                    ResponseEntity<?> resp = shareFile(filePath);
                    HttpStatus respCode = resp.getStatusCode();
                    String respBody = resp.getBody().toString();
                    if(respCode == HttpStatus.OK) {
                        console.writeInfo("F.SH finished");
                        console.write(promptProvider.getPrompt());
                        try {
                            String url = conn + rcvrInfo.getAddress() + "/fshClient/r/finishFsAlert";
                            Request req = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(req).execute();
                        } catch (Exception ex) {
                            LOGGER.info(ex.toString());
                            LOGGER.info(ex.getStackTrace().toString());
                        }
                    }
                    else if(respBody.contains("ConnectException")) {
                        setConnectionStatus(false);
                        console.writeInfo("F.SH failed\nReceiver Disconnected Abruptly");
                        console.write(promptProvider.getPrompt());
                    }
                    return resp;
                }
            };
            futureTask = new FutureTask<>(callableTask);
            executorService.execute(futureTask);
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            LOGGER.info(ex.getStackTrace().toString());
            if(!ex.toString().contains("CancellationException") && !ex.toString().contains("InterruptedIOException"))
                LOGGER.info(ex.getStackTrace().toString());
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    private ResponseEntity<?> shareFileInfo(String filePath) {
        if(!this.isConnected) {
            LOGGER.info("shareFileInfo NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
        File file = new File(filePath);
        long fileSize = file.length();
        // set kb factor for large files
        if(fileSize >= 1000000) // 1GB+
            kb = (int)(fileSize/2000);
        try {
            String url = conn + rcvrInfo.getAddress() + "/fshClient/r/shareFileInfo";
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
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    private ResponseEntity<?> shareFile(String filePath) {
        if(!this.isConnected) {
            LOGGER.info("shareFile NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        // logic to upload file - sharding - sending shards to receiver
        try {
            InputStream inputStream = new FileInputStream(filePath);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] shard = new byte[kb * 1024];
            RequestBody reqBody = RequestBody.create(shard, MediaType.parse("text/plain; charset=utf-8"));
            Instant start = Instant.now();
            while (bufferedInputStream.read(shard, 0, shard.length) != -1) {
                //logic to send this file shard to receiver
                String url = conn + rcvrInfo.getAddress() + "/fshClient/r/getFileShard";
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
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> stopFS() {
        if(!this.isConnected) {
            LOGGER.info("stopFS NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        if(!futureTask.isDone())
                futureTask.cancel(true);
        LOGGER.info("F.SH stopped.");
        try {
            String url = conn + rcvrInfo.getAddress() + "/fshClient/r/stopFsAlert";
            Request req = new Request.Builder()
                    .url(url)
                    .build();
            Response resp = client.newCall(req).execute();
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        }  catch (Exception ex) {
            LOGGER.info(ex.toString());
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> disconnect() {
        if(!this.isConnected)
            return ResponseEntity.status(HttpStatus.OK).body("ACK: Already Disconnected.");
        try {
            String url = conn + rcvrInfo.getAddress() + "/fshClient/r/disconnectAck";
            this.isConnected = false;
            Request req = new Request.Builder()
                    .url(url)
                    .build();
            Response resp = client.newCall(req).execute();
            return ResponseEntity.status(resp.code()).body(resp.body().string());
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NACK: " + ex);
        }
    }

}
