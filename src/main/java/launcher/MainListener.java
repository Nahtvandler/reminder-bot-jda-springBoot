package launcher;

import enums.CommandEnum;
import exceptions.NotEnoughParamsException;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import utils.Const;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.*;

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
        } else if (content.contains(CommandEnum.RANDOM.getCommand())) {
            if (content.equals(CommandEnum.RANDOM.getCommand())) {
                channel.sendMessage(prepareReminderParamsMessage()).queue();
            } else {
                channel.sendMessage(prepareRandomUserForChannelMessage(event, content)).queue();
            }
            return;
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
        String example = "$reminder 12:00 1 \"@here daily meeting is coming!\"";

        StringBuffer sb = generateCommandMainInfoBufferWithExample(CommandEnum.REMINDER, example);
        return sb.toString();
    }

    private String prepareRandomParamsMessage() {
        String example = "$random основной";

        StringBuffer sb = generateCommandMainInfoBufferWithExample(CommandEnum.RANDOM, example);
        return sb.toString();
    }

    private StringBuffer generateCommandMainInfoBuffer(CommandEnum command) {
        StringBuffer sb = new StringBuffer();
        sb.append(command.getCommand()).append(Const.SPACE);
        CommandEnum.REMINDER.getParams().forEach((par) -> {
            sb.append(Const.SQUARE_BRACKET_L).append(par).append(Const.SQUARE_BRACKET_R).append(Const.SPACE);
        });

        return sb;
    }

    private StringBuffer generateCommandMainInfoBufferWithExample(CommandEnum command, String example) {
        StringBuffer sb = generateCommandMainInfoBuffer(command);
        sb.append(Const.LN).append("Example: ").append(example);

        return sb;
    }

    private String prepareRandomUserForChannelMessage(MessageReceivedEvent event, String content) {
        String channelName = null;
        try {
            channelName = getFirstParametr(content);
        } catch (NotEnoughParamsException e) {
            return e.getMessage();
        }

        List<VoiceChannel> channels = event.getGuild().getVoiceChannelsByName(channelName, true);
        if (channels == null || channels.isEmpty()) {
            return MessageFormat.format("No channels with the name {0} found", channelName);
        }

        List<Member> members = channels.get(0).getMembers();
        if (members == null || members.isEmpty()) {
            return "No users are connected to the channel";
        }

        Random rnd = new Random();
        int index = rnd.nextInt(members.size() - 0);
        Member member = members.get(index);
        String messagePattern = "@{0} YOU ARE SACRIFICE!";

        return MessageFormat.format(messagePattern, member.getUser().getName());
    }

    private String getFirstParametr(String content) throws NotEnoughParamsException {
        List<String> params = new ArrayList<>();

        params.addAll(Arrays.asList(content.split(Const.SPACE)));
        if (params.size() < CommandEnum.REMINDER.getParams().size()-1) {
            throw new NotEnoughParamsException();
        }

        return params.get(1);
    }
}
