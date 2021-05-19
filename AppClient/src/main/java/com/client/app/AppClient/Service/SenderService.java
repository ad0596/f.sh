package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.FshReq;
import com.client.app.AppClient.DTO.ReqData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SenderService {
    ResponseEntity<?> reqReceiver(ReqData senderReqData);
    ResponseEntity<?> initFS(FshReq fshReq);
}
