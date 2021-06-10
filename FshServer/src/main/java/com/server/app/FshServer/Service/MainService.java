package com.server.app.FshServer.Service;

import com.server.app.FshServer.DTO.ReqData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MainService {
    ResponseEntity<?> reqReceiver(ReqData reqData);
    ResponseEntity<?> reqSender(ReqData reqData);
}
