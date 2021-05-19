package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.ReqData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverService {
    ResponseEntity<?> reqSender(ReqData rcvrReqData);
    ResponseEntity<?> getShard(byte [] shard, String fileName);
}
