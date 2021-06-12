package com.client.app.FshClient.Commands;

import com.client.app.FshClient.DTO.ReqData;
import com.client.app.FshClient.DTO.User;
import com.client.app.FshClient.Service.AppService.SenderService;
import com.client.app.FshClient.Service.ShellService.ConsoleService;
import com.client.app.FshClient.Service.ShellService.FshPromptProvider;
import com.client.app.FshClient.Service.ShellService.ShellUserService;
import com.client.app.FshClient.Util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent()
public class SCommands {

    @Autowired
    private ConsoleService console;
    @Autowired
    private FshPromptProvider promptProvider;
    @Autowired
    private SenderService senderService;
    @Autowired
    private ShellUserService shellUserService;

    @ShellMethod(value = "Look for Receiver to Connect", group = "SENDER")
    public void sconnect(String id, String addr, String rid) {
        User sndr = new User(id, UserType.SENDER, addr);
        User rcvr = new User(rid, UserType.RECEIVER, null);
        ReqData reqData = new ReqData(sndr, rcvr);

        String resp = senderService.reqReceiver(reqData).getBody().toString();

        if(resp.equals("ACK"))
            console.writeACK("Connection req sent");
        else if(resp.contains("ConnectException"))
            console.writeNACK("F.SH connection server is down");
        else
            console.writeNACK(resp);
    }

    @ShellMethod(value = "Initiate File-Sharing", group = "SENDER")
    public void initfs(String path) {
        String resp = senderService.initFS(path).getBody().toString();
        if(resp.equals("ACK"))
            console.writeACK("F.SH initiated");
        else if(resp.contains("ConnectException")) {
            console.writeNACK("Receiver not available");
            console.updateByConnectionEvent(UserType.SENDER, false);
        }
        else
            console.writeNACK(resp);
    }

    @ShellMethod(value = "Stop File-Sharing", group = "SENDER")
    public void stopfs() {
        String resp = senderService.stopFS().getBody().toString();
        if(resp.equals("ACK"))
            console.writeACK("F.SH stopped");
        else if(resp.contains("ConnectException")) {
            console.writeNACK("Receiver disconnected");
            console.updateByConnectionEvent(UserType.SENDER, false);
        }
        else
            console.writeNACK(resp);
    }

    @ShellMethod(value = "Disconnect", group = "SENDER")
    public void sdisconnect() {
        String resp = senderService.disconnect().getBody().toString();
        if(resp.equals("ACK")) {
            console.writeACK("Disconnected");
            console.updateByConnectionEvent(UserType.SENDER, false);
        }
        else if(resp.contains("ConnectException")) {
            console.writeNACK("Receiver already disconnected");
            console.write(promptProvider.getPrompt());
            console.updateByConnectionEvent(UserType.SENDER, false);
        }
        else
            console.writeNACK(resp);
    }

    Availability connectAvailability() {
        return !senderService.isConnected() ?
                Availability.available() : Availability.unavailable("You're already connected");
    }

    Availability disconnectAvailability() {
        return senderService.isConnected() ?
                Availability.available() : Availability.unavailable("You're not connected");
    }

    @ShellMethodAvailability("*")
    Availability commandsAvailability() {
        return shellUserService.myProfile() == UserType.SENDER ?
                Availability.available() : Availability.unavailable("Receiver commands not available for Sender");
    }
}
