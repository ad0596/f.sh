package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.FileInfo;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverService {
    ResponseEntity<?> reqSender(ReqData rcvrReqData);
    ResponseEntity<?> disconnectAck();
    ResponseEntity<?> shareFileInfo(FileInfo fileInfo);
    ResponseEntity<?> getShard(byte [] shard);
    ResponseEntity<?> stopFsAlert();
    ResponseEntity<?> finishFsAlert();
    ResponseEntity<?> disconnect(User user);
}
