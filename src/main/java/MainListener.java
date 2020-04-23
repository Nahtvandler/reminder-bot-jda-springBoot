import enums.CommandEnum;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import utils.Const;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class MainListener extends ListenerAdapter {

    @Override
    @SubscribeEvent
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        //TODO посмотреть что за дерьмо с кирилицей
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        String content = msg.getContentRaw();

        if (content.equals(CommandEnum.HELP.getCommand())) {
            channel.sendMessage(prepareHelpMessage()).queue();
            return;
        } else if (content.contains(CommandEnum.REMINDER.getCommand())) {
            if (content.equals(CommandEnum.REMINDER.getCommand())) {
                channel.sendMessage(prepareReminderParamsMessage()).queue();
                return;
            } else {
                String result = ReminderService.setReminder(content, channel);
                channel.sendMessage(result).queue();
                return;
            }

        }
    }

    private String prepareHelpMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("Available commands:").append(Const.LN);

        List<CommandEnum> commandEnumList = Arrays.asList(CommandEnum.values());
        commandEnumList.forEach((ce) -> {
            sb.append(Const.TABULATE).append(ce.toString()).append(Const.LN);
        });

        return sb.toString();
    }

    private String prepareReminderParamsMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(CommandEnum.REMINDER.getCommand()).append(Const.SPACE);
        CommandEnum.REMINDER.getParams().forEach((par) -> {
            sb.append(Const.SQUARE_BRACKET_L).append(par).append(Const.SQUARE_BRACKET_R).append(Const.SPACE);
        });

        String example = "$reminder 12:00 1 \"@here daily meeting is coming!\"";
        sb.append(Const.LN).append("Example: ").append(example);
        return sb.toString();
    }
}
