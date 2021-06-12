package com.client.app.FshClient.Service.AppService.Impl;

import com.client.app.FshClient.DTO.FileInfo;
import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import com.client.app.FshClient.Service.AppService.ReceiverService;
import com.client.app.FshClient.Service.ShellService.ConsoleService;
import com.client.app.FshClient.Service.ShellService.FshPromptProvider;
import com.client.app.FshClient.Util.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ConsoleService console;
    @Autowired
    private FshPromptProvider promptProvider;

    @Value("${destFileDir}")
    private String fileDir = null;
    @Value("${serverAddress}")
    private String serverAddress = null;
    @Value("${conn}")
    private String conn;

    private boolean isConnected = false;
    private User sndrInfo = new User();
    private String fileName;
    private long fileSize;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void setConnectionStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public ResponseEntity<?> setDestDirPath(String destDirPath) {
        this.fileDir = destDirPath;
        File directory = new File(fileDir);
        boolean dirExists = true;
        if (!directory.exists()){
            dirExists = false;
            dirExists = directory.mkdirs();
        }
        if(dirExists)
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Unable to create directory (Invalid Path)");
    }

    @Override
    public ResponseEntity<?> reqSender(ReqData rcvrReqData) {
        if(this.isConnected)
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
            int respCode = resp.code();
            String respBody = resp.body().string();
            if(respCode == 200) {
                this.isConnected = true;
                ObjectMapper mapper = new ObjectMapper();
                sndrInfo = mapper.readValue(respBody, User.class);
                return ResponseEntity.status(respCode).body("ACK");
            } else
                return ResponseEntity.status(respCode).body(respBody);
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
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
        console.updateByConnectionEvent(UserType.RECEIVER, this.isConnected);
        console.write(promptProvider.getPrompt());

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

        // Shell output
        console.writeInfo("F.SH started");
        console.write(promptProvider.getPrompt());

        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> getShard(byte[] shard) {
        if(!this.isConnected) {
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
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: " + ex);
        }
    }

    @Override
    public ResponseEntity<?> stopFsAlert() {
        if(!this.isConnected) {
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
        if(!this.isConnected) {
            LOGGER.info("finishFsAlert NOT_ALLOWED. Not connected with receiver.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NACK: Not connected.");
        }
        LOGGER.info("F.SH finished.");

        // Shell output
        console.writeInfo("F.SH finished\nFile Path : " + this.fileDir + this.fileName);
        console.write(promptProvider.getPrompt());

        return ResponseEntity.status(HttpStatus.OK).body("ACK");
    }

    @Override
    public ResponseEntity<?> disconnect() {
        if(!this.isConnected)
            return ResponseEntity.status(HttpStatus.OK).body("ACK: Already Disconnected.");
        try {
            String url = conn + sndrInfo.getAddress() + "/fshClient/s/disconnectAck";
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
