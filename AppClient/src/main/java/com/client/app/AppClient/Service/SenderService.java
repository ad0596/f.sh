package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import org.springframework.stereotype.Service;

@Service
public interface SenderService {

    public boolean reqReceiver(ReqData senderReqData);
    public boolean initFS(User rcvr);

}
