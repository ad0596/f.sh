package com.client.app.FshClient.Service.AppService;

import com.client.app.FshClient.DTO.FileInfo;
import com.client.app.FshClient.DTO.ReqData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverService {
    Boolean isConnected();
    void setConnectionStatus(boolean isConnected);
    ResponseEntity<?> setDestDirPath(String destDirPath);
    ResponseEntity<?> reqSender(ReqData rcvrReqData);
    ResponseEntity<?> disconnectAck();
    ResponseEntity<?> shareFileInfo(FileInfo fileInfo);
    ResponseEntity<?> getShard(byte [] shard);
    ResponseEntity<?> stopFsAlert();
    ResponseEntity<?> finishFsAlert();
    ResponseEntity<?> disconnect();
}
