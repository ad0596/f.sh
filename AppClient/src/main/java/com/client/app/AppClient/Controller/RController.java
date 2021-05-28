package com.client.app.AppClient.Controller;

import com.client.app.AppClient.DTO.FileInfo;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/fshClient/r")
public class RController {

    private static final Logger LOGGER = Logger.getLogger(RController.class.getName());

    @Autowired
    ReceiverService receiverService;

    // RECEIVER
    @PostMapping(path = "/reqSender", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqSender(@RequestBody ReqData rcvrReqData) {
        LOGGER.info("Invoked EndPoint : '/reqSender'");
        return receiverService.reqSender(rcvrReqData);
    }

    // Hit by SENDER
    @GetMapping(path = "/disconnectAck")
    public ResponseEntity<?> disconnectAck() {
        LOGGER.info("Server Invoked Endpoint : '/disconnectAck'");
        return receiverService.disconnectAck();
    }

    // RECEIVER
    @PostMapping(path = "/shareFileInfo", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> shareFileInfo(@RequestBody FileInfo fileInfo) {
        LOGGER.info("Invoked Endpoint : '/shareFileInfo'");
        return receiverService.shareFileInfo(fileInfo);
    }

    // RECEIVER
    @PostMapping(path = "/getFileShard", produces = "application/json")
    public ResponseEntity<?> getFileShard(@RequestBody byte [] shard, HttpServletRequest request) {
        String fileName = request.getHeader("fileName");
        return receiverService.getShard(shard);
    }

    // Hit by SENDER
    @GetMapping(path = "/stopFsAlert", produces = "application/json")
    public ResponseEntity<?> stopFsAlert() {
        LOGGER.info("Sender invoked Endpoint : '/stopFsAlert'");
        return receiverService.stopFsAlert();
    }

    @GetMapping(path = "/finishFsAlert", produces = "application/json")
    public ResponseEntity<?> finishFsAlert() {
        LOGGER.info("Sender invoked Endoint : '/finishFsAlert'");
        return receiverService.finishFsAlert();
    }

    // RECEIVER
    @PostMapping(path =  "/disconnect", consumes = "application/json")
    public ResponseEntity<?> disconnect(@RequestBody User user) {
        LOGGER.info("Invoked EndPoint : '/disconnect'");
        return receiverService.disconnect(user);
    }
}
