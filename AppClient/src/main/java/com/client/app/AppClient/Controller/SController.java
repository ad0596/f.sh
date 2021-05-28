package com.client.app.AppClient.Controller;

import com.client.app.AppClient.DTO.FshReq;
import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/fshClient/s")
public class SController {

    private static final Logger LOGGER = Logger.getLogger(SController.class.getName());

    @Autowired
    SenderService senderService;

    // SENDER
    @PostMapping(path = "/reqReceiver", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqReceiver(@RequestBody ReqData sndrReqData) {
        LOGGER.info("Invoked EndPoint : '/reqReceiver'");
        return senderService.reqReceiver(sndrReqData);
    }

    // Hit by SERVER
    @GetMapping(path = "/connectionAck")
    public ResponseEntity<?> connectionAck() {
        LOGGER.info("Server Invoked Endpoint: '/connectionAck'");
        return senderService.connectionAck();
    }

    // Hit by RECEIVER
    @GetMapping(path = "/disconnectAck")
    public ResponseEntity<?> disconnectAck() {
        LOGGER.info("Receiver Invoked Endpoint : '/disconnectAckS'");
        return senderService.disconnectAck();
    }

    // SENDER
    @PostMapping(path = "/initFS", consumes = "application/json")
    public ResponseEntity<?> initFS(@RequestBody FshReq fshReq) {
        LOGGER.info("Invoked EndPoint : '/shareFile'");
        return senderService.initFS(fshReq);
    }

    // SENDER
    @PostMapping(path = "/stopFS", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> stopFS(@RequestBody  User user) {
        LOGGER.info("Invoked Endpoint : '/stopFS'");
        return senderService.stopFS(user);
    }

    // SENDER
    @PostMapping(path =  "/disconnect", consumes = "application/json")
    public ResponseEntity<?> disconnect(@RequestBody User user) {
        LOGGER.info("Invoked EndPoint : '/disconnect'");
        return senderService.disconnect(user);
    }

}
