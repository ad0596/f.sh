package com.client.app.FshClient.Service.AppService;

import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SenderService {
    Boolean isConnected();
    void setConnectionStatus(boolean isConnected);
    ResponseEntity<?> reqReceiver(ReqData senderReqData);
    ResponseEntity<?> connectionAck(User rcvr);
    ResponseEntity<?> disconnectAck();
    ResponseEntity<?> initFS(String filePath);
    ResponseEntity<?> stopFS();
    ResponseEntity<?> disconnect();
}
