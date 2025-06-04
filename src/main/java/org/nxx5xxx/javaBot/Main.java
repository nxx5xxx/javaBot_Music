package org.nxx5xxx.javaBot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.nxx5xxx.javaBot.trash.Chat;
import org.nxx5xxx.javaBot.trash.Slash;

import java.util.EnumSet;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {
        TokenManager tokenManager = new TokenManager();
        String token = tokenManager.getDiscordBotToken();
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES
        );
        /*
        GATEWAYINTENT 에는 아래와 같은 것들이 있음
         GUILD_EMOJIS_AND_STICKERS      서버의 이모지와 스티커가 생성/삭제/수정될 때 이벤트를 수신합니다
         GUILD_EXPRESSIONS              서버에서 사용 가능한 표현(스티커/이모지/이모티콘 등)의 변경 정보를 받습니다. *(새로운 기능일 수 있음)*
         GUILD_WEBHOOKS                 서버의 웹훅(Webhook)이 생성/수정/삭제될 때 감지합니다.
         GUILD_INVITES                  초대 링크(Invite)가 생성되거나 제거될 때 이벤트를 수신합니다.
         GUILD_VOICE_STATES             사용자가 음성 채널에 입장/이동/퇴장하는 등의 상태 변화 정보를 받습니다. (뮤직 봇 필수)
         GUILD_PRESENCES                서버 멤버의 상태(온라인, 오프라인, 활동 등)를 감지합니다. Privileged Intent입니다.
         GUILD_MESSAGES                 서버의 텍스트 채널에서 메시지가 생성될 때 감지합니다. (MessageReceivedEvent 수신 가능)
         GUILD_MESSAGE_REACTIONS        메시지에 반응(이모지 리액션)이 추가/삭제될 때 이벤트 수신합니다.
         GUILD_MESSAGE_TYPING           서버에서 사용자가 메시지를 입력 중일 때(TypingEvent) 감지합니다.
         DIRECT_MESSAGES                DM(개인 메시지)이 생성될 때 이벤트 수신합니다.
         DIRECT_MESSAGE_REACTIONS       DM에서 메시지에 리액션이 추가/제거될 때 이벤트 수신합니다.
         DIRECT_MESSAGE_TYPING          DM에서 사용자가 메시지를 입력 중일 때 감지합니다.
         MESSAGE_CONTENT                메시지의 본문 내용을 읽을 수 있는 권한입니다. 없으면 event.getMessage().getContentRaw()가 비어 있음. Privileged Intent입니다.
         SCHEDULED_EVENTS               서버에서 예약된 이벤트(예: 음성 채널 모임, 일정 알림 등) 관련 정보를 수신합니다.
         AUTO_MODERATION_CONFIGURATION  자동 모더레이션 설정(필터링 정책 등)이 변경될 때 감지합니다.
         AUTO_MODERATION_EXECUTION      자동 모더레이션이 실행되었을 때 (예: 특정 단어 차단 등) 감지합니다.
         GUILD_MESSAGE_POLLS            서버에서 생성된 투표(poll) 메시지에 대한 이벤트를 수신합니다. *(2023 이후 신규 기능)*
         DIRECT_MESSAGE_POLLS           개인 DM에서 투표 메시지가 생성/변경될 때 이벤트를 수신합니다.
         */
        JDABuilder.createDefault(token)
                .enableIntents(intents)
                .setActivity(Activity.customStatus("만드는중..."))
                .addEventListeners(new Chat(),new Slash()).build();
        //JDABuilder.createDefault(token).enableIntents(intents).setActivity(Activity.customStatus("만드는중...")).build();
        /*
        JDABuilder.createDefault(token).setActivity(Activity.competing("")),build;
        Activity.competing(String name)
        ~에 참가 중
        Activity.playing(String name)
        ~하는 중
        Activity.listening(String name)
        ~듣는 중
        Activity.streaming(String name, String url)
        ~방송 중 (해당링크 클릭가능)
        Activity.watching(String name)
        ~ 시청 중
        Activity.customStatus(String name)
        ~~~~~ (스트링 내에것 표기됨)
         */
    }
}