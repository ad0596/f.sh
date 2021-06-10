package com.server.app.FshServer.Controller;

import com.server.app.FshServer.DTO.ReqData;
import com.server.app.FshServer.Service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/fshServer")
public class BootController {

    private static final Logger LOGGER = Logger.getLogger(BootController.class.getName());

    @Autowired
    MainService mainService;

    // SENDER
    @PostMapping(value = "/reqReceiver", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqReceiver(@RequestBody ReqData reqData) {
        LOGGER.info("Invoked EndPoint : '/reqReceiver'");
        return mainService.reqReceiver(reqData);
    }

    // RECEIVER
    @PostMapping(value = "/reqSender", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> reqSender(@RequestBody ReqData reqData) {
        LOGGER.info("Invoked EndPoint : '/reqSender'");
        return mainService.reqSender(reqData);
    }

}
