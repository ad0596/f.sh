package com.client.app.FshClient.Controller;

import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import com.client.app.FshClient.Service.AppService.SenderService;
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
    @PostMapping(path = "/connectionAck")
    public ResponseEntity<?> connectionAck(@RequestBody User rcvr) {
        LOGGER.info("Server Invoked Endpoint: '/connectionAck'");
        return senderService.connectionAck(rcvr);
    }

    // Hit by RECEIVER
    @GetMapping(path = "/disconnectAck")
    public ResponseEntity<?> disconnectAck() {
        LOGGER.info("Receiver Invoked Endpoint : '/disconnectAckS'");
        return senderService.disconnectAck();
    }

    // SENDER
    @PostMapping(path = "/initFS", consumes = "text/plain")
    public ResponseEntity<?> initFS(@RequestBody String filePath) {
        LOGGER.info("Invoked EndPoint : '/shareFile'");
        return senderService.initFS(filePath);
    }

    // SENDER
    @GetMapping(path = "/stopFS", produces = "application/json")
    public ResponseEntity<?> stopFS() {
        LOGGER.info("Invoked Endpoint : '/stopFS'");
        return senderService.stopFS();
    }

    // SENDER
    @GetMapping(path =  "/disconnect")
    public ResponseEntity<?> disconnect() {
        LOGGER.info("Invoked EndPoint : '/disconnect'");
        return senderService.disconnect();
    }

}
