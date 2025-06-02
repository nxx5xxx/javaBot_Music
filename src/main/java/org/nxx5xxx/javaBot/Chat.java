package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Chat extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //onMessageReceived는 수신 받았을때 호출되는 메서드

        String msg = event.getMessage().getContentRaw();
        //event.getMessage().getContentDisplay()
        /*
        event.getMessage().getContentRaw();
        event.getMessage().getContentDisplay()
        event.getMessage().getContentStripped()
        의 차이점은
        예시로 <@1234567890> **굵은 텍스트** <#4567891230> 가 있으면
        실제로는 @홍길동 굵은 텍스트 #일반채널 이렇게 보이나
        각 메서드에 따라 아래처럼 보이게 된다
        getContentRaw()       <@1234567890> **굵은 텍스트** <#4567891230>
        getContentDisplay()   @홍길동 굵은 텍스트 #일반채널
        getContentStripped()  홍길동 굵은 텍스트 일반채널
         */
        if(msg.equals("test")){
           event.getChannel().sendMessage("테스트가성공적으로 이루어졌습니다").queue();
        }
    }
}
