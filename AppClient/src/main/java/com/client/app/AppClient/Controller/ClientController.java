package com.client.app.AppClient.Controller;

import com.client.app.AppClient.DTO.SenderReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import com.client.app.AppClient.Service.ReceiverService;
import com.client.app.AppClient.Service.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/fshClient")
public class ClientController {

    @Autowired
    SenderService senderService;
    @Autowired
    CommonService commonService;
    @Autowired
    ReceiverService receiverService;

    // SENDER
    @PostMapping(path = "/shareRcvrInfo", consumes = "application/json")
    public void getReceiverInfo(User rcvr) {
        senderService.initFS(rcvr);
    }

    // SENDER
    @PostMapping(path = "/reqReceiver", consumes = "application/json")
    public boolean reqReceiver(SenderReqData sndrReqData) {
        return senderService.reqReceiver(sndrReqData);
    }

    // COMMON
    @PostMapping(path =  "/disconnect", consumes = "application/json")
    public void disconnect(User user) {
        commonService.disconnect(user);
    }

    // RECEIVER
    @PostMapping(path = "/reqSender", consumes = "application/json")
    public boolean reqSender(User user) {
        return receiverService.reqSender(user);
    }

    // RECEIVER
    @PostMapping(path = "/getFileShard")
    public boolean getFileShard(byte [] shard) {
        return receiverService.getShard(shard);
    }
    
}
