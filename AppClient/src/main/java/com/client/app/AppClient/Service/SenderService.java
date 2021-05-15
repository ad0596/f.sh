package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.SenderReqData;
import com.client.app.AppClient.DTO.User;
import org.springframework.stereotype.Service;

@Service
public interface SenderService {

    public boolean reqReceiver(SenderReqData senderReqData);
    public void initFS(User rcvr);

}
