package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Chat extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();

        if(msg.equals("test")){
           event.getChannel().sendMessage("테스트가성공적으로 이루어졌습니다").queue();
        }
    }
}
