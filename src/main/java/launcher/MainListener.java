package launcher;

import enums.CommandEnum;
import exceptions.NotEnoughParamsException;
import music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Const;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.*;

//TODO зарефакторить команды, сделать интерфейс и хандлер для их обработки
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
        try {
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
            } else if (content.contains(CommandEnum.JOIN.getCommand())) {
                joinToChannel(event);
            } else if (content.contains(CommandEnum.LEAVE.getCommand())) {
                leaveFromChannel(event);
            } else if (content.contains(CommandEnum.PLAY.getCommand())) {
                if (content.equals(CommandEnum.PLAY.getCommand())) {
                    playDefaultTrack(event);
                } else {
                    playMusic(event, getFirstParametr(content));
                }
            } else if (content.contains(CommandEnum.STOP.getCommand())) {
                stopMusic(event);
            } else if (content.contains(CommandEnum.VOLUME.getCommand())) {
                setVolume(event, getFirstParametr(content));
            } else if (content.contains(CommandEnum.NEXT.getCommand())) {
                nextTrack(event);
            } else if (content.contains(CommandEnum.TRACK.getCommand())) {
                channel.sendMessage(prepareInfoAboutCurrentTrack(event)).queue();
            }
        } catch (NotEnoughParamsException e) {
            channel.sendMessage(e.getMessage()).queue();
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
        playDefaultTrack(event);

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

    private void joinToChannel(MessageReceivedEvent event) {
        GuildChannel guildChannel = event.getGuild().getGuildChannelById(event.getChannel().getId());
        MessageChannel messageChannel = event.getChannel();

        if(!event.getGuild().getSelfMember().hasPermission(guildChannel, Permission.VOICE_CONNECT)) {
            messageChannel.sendMessage("I do not have permissions to join a voice channel!").queue();
            return;
        }

        VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
        if(connectedChannel == null) {
            messageChannel.sendMessage("You are not connected to a voice channel!").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        if(audioManager.isAttemptingToConnect()) {
            messageChannel.sendMessage("The bot is already trying to connect! Enter the chill zone!").queue();
            return;
        }

        audioManager.openAudioConnection(connectedChannel);
        messageChannel.sendMessage("Connected to the voice channel!").queue();
    }

    public void leaveFromChannel(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        if(connectedChannel == null) {
            channel.sendMessage("I am not connected to a voice channel!").queue();
            return;
        }

        event.getGuild().getAudioManager().closeAudioConnection();
        channel.sendMessage("Disconnected from the voice channel!").queue();
    }

    private void playDefaultTrack(MessageReceivedEvent event) {
        playMusic(event, "https://www.youtube.com/watch?v=6c9Rf1FPTzY");
    }

    private void playMusic(MessageReceivedEvent event, String trackUrl) {
        TextChannel textChannel = event.getGuild().getTextChannelById(event.getChannel().getId());

        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.loadAndPlay(textChannel, trackUrl);

        playerManager.getGuildMusicManager(event.getGuild()).player.setVolume(50);
    }

    private void stopMusic(MessageReceivedEvent event) {
        PlayerManager.getInstance().stop(event.getGuild());

    }

    private void setVolume(MessageReceivedEvent event, String volume) {
        PlayerManager.getInstance().setVolume(event.getGuild(), volume);
    }

    private void nextTrack(MessageReceivedEvent event) {
        PlayerManager.getInstance().nextTrack(event.getGuild());
    }

    private String prepareInfoAboutCurrentTrack(MessageReceivedEvent event) {
        return PlayerManager.getInstance().getPlayingTrackInfo(event.getGuild());
    }

}
