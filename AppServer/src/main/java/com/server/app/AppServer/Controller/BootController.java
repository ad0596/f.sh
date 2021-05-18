package com.server.app.AppServer.Controller;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.DTO.User;
import com.server.app.AppServer.Service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/fshServer")
public class BootController {

    @Autowired
    MainService mainService;

    // SENDER
    @PostMapping(value = "/reqReceiver", consumes = "application/json", produces = "application/json")
    public @ResponseBody boolean reqReceiver(@RequestBody ReqData reqData) {
        return mainService.reqReceiver(reqData);
    }

    // RECEIVER
    @PostMapping(value = "/reqSender", consumes = "application/json", produces = "application/json")
    public @ResponseBody boolean reqSender(@RequestBody ReqData reqData) {
        return mainService.reqSender(reqData);
    }

    // DISCONNECT
    @PostMapping(value = "/disconnect", consumes = "application/json", produces = "application/json")
    public @ResponseBody boolean disconnect(User user) {
	// TODO: Implement disconnect method
        return false;
    }
}
