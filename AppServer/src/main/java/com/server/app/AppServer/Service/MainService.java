package com.server.app.AppServer.Service;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.DTO.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MainService {
    ResponseEntity<?> reqReceiver(ReqData reqData);
    ResponseEntity<?> reqSender(ReqData reqData);
    ResponseEntity<?> disconnect(User user);
}
