package com.client.app.FshClient.Service.ShellService;

import com.client.app.FshClient.Util.UserConnectionEvent;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class FshPromptProvider implements PromptProvider {

    @Autowired
    ShellUserService shellUserService;

    private boolean isConnected;

    @Override
    public AttributedString getPrompt() {
        String msg;
        if(shellUserService.myProfile() == null) {
            msg = "F.SH> ";
            return new AttributedString(msg, AttributedStyle.BOLD.foreground(AttributedStyle.BRIGHT));
        } else {
            msg = String.format("F.SH::[%s]> ", shellUserService.myProfile().toString());
            AttributedStyle style = isConnected ?
                                    AttributedStyle.BOLD.foreground(AttributedStyle.CYAN) :
                                    AttributedStyle.BOLD.foreground(AttributedStyle.BRIGHT);
            return new AttributedString(msg, style);
        }
    }

    @EventListener
    public void handle(UserConnectionEvent connectionEvent) {
        this.isConnected = connectionEvent.getConnectionStatus();
    }
}
