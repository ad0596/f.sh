package com.client.app.FshClient.Commands;

import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import com.client.app.FshClient.Service.AppService.ReceiverService;
import com.client.app.FshClient.Service.ShellService.ConsoleService;
import com.client.app.FshClient.Service.ShellService.FshPromptProvider;
import com.client.app.FshClient.Service.ShellService.ShellUserService;
import com.client.app.FshClient.Util.UserConnectionEvent;
import com.client.app.FshClient.Util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class RCommands {

    @Autowired
    private ConsoleService console;
    @Autowired
    private FshPromptProvider promptProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ReceiverService receiverService;
    @Autowired
    private ShellUserService shellUserService;

    @ShellMethod(value = "Set Destination file Directory", group = "RECEIVER")
    public void destDiir(String path) {
        String resp = receiverService.setDestDirPath(path).getBody().toString();
        if(resp.equals("ACK"))
            console.writeACK("Destination File Directory changed to : " + path);
    }

    @ShellMethod(value = "Look for Sender to Connect", group = "RECEIVER")
    public void rconnect(String id, String addr, String sid) {
        User rcvr = new User(id, UserType.RECEIVER, addr);
        User sndr = new User(sid, UserType.SENDER, null);
        ReqData reqData = new ReqData(sndr, rcvr);

        String resp = receiverService.reqSender(reqData).getBody().toString();

        if(resp.contains("ConnectException"))
            console.writeNACK("F.SH connection server is down");
        else if(resp.equals("ACK")) {
            // connection event for shell
            UserConnectionEvent connectionEvent = new UserConnectionEvent(this, true);
            eventPublisher.publishEvent(connectionEvent);
            console.writeACK("Connected");
        }
        else
            console.writeNACK(resp);
    }

    @ShellMethod(value = "Disconnect", group = "RECEIVER")
    public void rdisconnect() {
        String resp = receiverService.disconnect().getBody().toString();
        if(resp.contains("ConnectException")) {
            console.writeNACK("Sender already disconnected");
            // connection event for shell
            UserConnectionEvent connectionEvent = new UserConnectionEvent(this, false);
            eventPublisher.publishEvent(connectionEvent);
            console.write(promptProvider.getPrompt());
        }
        else if(resp.equals("ACK")) {
            console.writeACK("Disconnected");
            // connection event for shell
            UserConnectionEvent connectionEvent = new UserConnectionEvent(this, false);
            eventPublisher.publishEvent(connectionEvent);
        }
        else
            console.writeNACK(resp);
    }

    Availability connectAvailability() {
        return !receiverService.isConnected() ?
                Availability.available() : Availability.unavailable("You're already connected");
    }

    Availability disconnectAvailability() {
        return receiverService.isConnected() ?
                Availability.available() : Availability.unavailable("You're not connected");
    }

    @ShellMethodAvailability("*")
    Availability commandsAvailability() {
        return shellUserService.myProfile() == UserType.RECEIVER ?
                Availability.available() : Availability.unavailable("Receiver commands not available for Sender");
    }
}
