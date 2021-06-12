package com.client.app.FshClient.Controller;

import com.client.app.FshClient.DTO.FileInfo;
import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.Service.AppService.CommonService;
import com.client.app.FshClient.Service.AppService.ReceiverService;
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
    private CommonService commonService;
    @Autowired
    private ReceiverService receiverService;

    // RECEIVER
    @GetMapping(path = "/networkInfo")
    public ResponseEntity<?> networkInfo() {
        LOGGER.info("Invoked Endpoint: '/networkInfo'");
        return commonService.networkInfo();
    }

    // RECEIVER
    @PostMapping(path = "/setDestDirPath", consumes = "text/plain")
    public ResponseEntity<?> setDestDirPath(@RequestBody String destDirPath) {
        LOGGER.info("Invoked Endpoint : '/setDestDirPath'");
        return receiverService.setDestDirPath(destDirPath);
    }

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

    // Hit by SENDER
    @PostMapping(path = "/shareFileInfo", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> shareFileInfo(@RequestBody FileInfo fileInfo) {
        LOGGER.info("Invoked Endpoint : '/shareFileInfo'");
        return receiverService.shareFileInfo(fileInfo);
    }

    // Hit By SENDER
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

    // Hit bt SENDER
    @GetMapping(path = "/finishFsAlert", produces = "application/json")
    public ResponseEntity<?> finishFsAlert() {
        LOGGER.info("Sender invoked Endoint : '/finishFsAlert'");
        return receiverService.finishFsAlert();
    }

    // RECEIVER
    @GetMapping(path =  "/disconnect")
    public ResponseEntity<?> disconnect() {
        LOGGER.info("Invoked EndPoint : '/disconnect'");
        return receiverService.disconnect();
    }
}
