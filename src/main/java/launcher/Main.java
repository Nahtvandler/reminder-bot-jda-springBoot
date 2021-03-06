package launcher;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;

@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(".")
public class Main extends ListenerAdapter {
    static JDA jda;

    public static void main(String[] args) throws LoginException {
        SpringApplication.run(Main.class);

        String token = System.getenv("DISCORD_BOT_TOKEN");
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new MainListener());
        jda = builder.build();

//        ScheduledThreadPoolExecutor implementation = (ScheduledThreadPoolExecutor) Scheduled_Executor_Service;
//        int size = implementation.getQueue().size();
    }

    //long time = System.currentTimeMillis();
//            channel.sendMessage("Pong!")
//                    .queue(response  -> {
//                        response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
//                    });
    //channel.sendMessage("Pong!").queue();
}
