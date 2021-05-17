package com.server.app.AppServer.Service;

import com.server.app.AppServer.DTO.ReqData;
import org.springframework.stereotype.Service;

@Service
public interface MainService {

    public boolean reqReceiver(ReqData reqData);
    public boolean reqSender(ReqData reqData);
}
