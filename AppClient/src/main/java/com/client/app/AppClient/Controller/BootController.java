package com.client.app.AppClient.Controller;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import com.client.app.AppClient.Service.ReceiverService;
import com.client.app.AppClient.Service.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/fshClient")
public class BootController {

    @Autowired
    SenderService senderService;
    @Autowired
    CommonService commonService;
    @Autowired
    ReceiverService receiverService;

    // SERVER
    @PostMapping(path = "/shareFile", consumes = "application/json")
    public boolean shareFile(@RequestBody User rcvr) {
        return senderService.initFS(rcvr);
    }

    // SENDER
    @PostMapping(path = "/reqReceiver", consumes = "application/json", produces = "application/json")
    public boolean reqReceiver(@RequestBody ReqData sndrReqData) {
        return senderService.reqReceiver(sndrReqData);
    }

    // COMMON
    @PostMapping(path =  "/disconnect", consumes = "application/json")
    public boolean disconnect(@RequestBody User user) {
        return commonService.disconnect(user);
    }

    // RECEIVER
    @PostMapping(path = "/reqSender", consumes = "application/json", produces = "application/json")
    public boolean reqSender(@RequestBody ReqData rcvrReqData) {
        return receiverService.reqSender(rcvrReqData);
    }

    // RECEIVER
    @PostMapping(path = "/getFileShard", produces = "application/json")
    public boolean getFileShard(@RequestBody byte [] shard) {
        return receiverService.getShard(shard);
    }

    @GetMapping(value = "/intiateFileWriter")
    public void initiateFW () {
        receiverService.initializeFileWriter();
    }
    
}
