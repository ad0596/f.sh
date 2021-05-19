package com.client.app.AppClient.Controller;

import com.client.app.AppClient.DTO.FshReq;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import com.client.app.AppClient.Service.ReceiverService;
import com.client.app.AppClient.Service.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/fshClient")
public class BootController {

    private static final Logger LOGGER = Logger.getLogger(BootController.class.getName());

    @Autowired
    SenderService senderService;
    @Autowired
    CommonService commonService;
    @Autowired
    ReceiverService receiverService;

    // SERVER
    @PostMapping(path = "/shareFile", consumes = "application/json")
    public ResponseEntity<?> shareFile(@RequestBody FshReq fshReq) {
        LOGGER.info("Invoked EndPoint : '/shareFile'");
        LOGGER.info("Initiating File Sharing...");
        Instant start = Instant.now();
        ResponseEntity<?> resp = senderService.initFS(fshReq);
        if(resp.getBody().equals("ACK")) {
            Instant end = Instant.now();
            LOGGER.info("File Sharing Finished. [Time taken: " + Duration.between(start, end).getSeconds() + "sec ].");
        } else
            LOGGER.info("Error in File Sharing.");
        return resp;
    }

    // SENDER
    @PostMapping(path = "/reqReceiver", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqReceiver(@RequestBody ReqData sndrReqData) {
        LOGGER.info("Invoked EndPoint : '/reqReceiver'");
        return senderService.reqReceiver(sndrReqData);
    }

    // COMMON
    @PostMapping(path =  "/disconnect", consumes = "application/json")
    public ResponseEntity<?> disconnect(@RequestBody User user) {
        LOGGER.info("Invoked EndPoint : '/disconnect'");
        return commonService.disconnect(user);
    }

    // RECEIVER
    @PostMapping(path = "/reqSender", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqSender(@RequestBody ReqData rcvrReqData) {
        LOGGER.info("Invoked EndPoint : '/reqSender'");
        return receiverService.reqSender(rcvrReqData);
    }

    // RECEIVER
    @PostMapping(path = "/getFileShard", produces = "application/json")
    public ResponseEntity<?> getFileShard(@RequestBody byte [] shard, HttpServletRequest request) {
        String fileName = request.getHeader("fileName");
        return receiverService.getShard(shard, fileName);
    }

}
