package org.nxx5xxx.javaBot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TokenManager {
    private String DiscordBotToken;

    public TokenManager(){
        settingToken();
    }

    private void settingToken(){
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("Token.properties")){
            Properties prop = new Properties();
            prop.load(input);
            DiscordBotToken = prop.getProperty("Token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getDiscordBotToken(){
        return DiscordBotToken;
    }
}
