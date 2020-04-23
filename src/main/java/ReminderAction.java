import net.dv8tion.jda.api.entities.MessageChannel;

public class ReminderAction implements Runnable {
    MessageChannel channel;
    String message;

    public ReminderAction(MessageChannel channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    @Override
    public void run() {
        //if ()
        channel.sendMessage(message).queue();
    }
}
