package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SenderService {
    ResponseEntity<?> reqReceiver(ReqData senderReqData);
    ResponseEntity<?> connectionAck(User rcvr);
    ResponseEntity<?> disconnectAck();
    ResponseEntity<?> initFS(String filePath);
    ResponseEntity<?> stopFS();
    ResponseEntity<?> disconnect();
}
