package launcher;

import enums.CommandEnum;
import exceptions.WrongParameterEception;
import net.dv8tion.jda.api.entities.MessageChannel;
import pojos.RemindTime;
import pojos.Reminder;
import utils.Const;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderService {
    private static ScheduledExecutorService scheduler;

    public static String setReminder(String content, MessageChannel channel) {
        List<String> params = new ArrayList<>();

        params.addAll(Arrays.asList(content.split(" ")));
        if (params.size() < CommandEnum.REMINDER.getParams().size()-1) {
            return "Not enought params";
        }

        Reminder reminder = new Reminder();
        try {
            reminder.setRemindTime(convertRemindTime(params.get(1)));
            reminder.setPeriod(Integer.parseInt(params.get(2)));

            String message = substringMessage(content);
            reminder.setMessage(message);
        } catch (IndexOutOfBoundsException  e) {
            return "wrong parameters!";
        } catch (WrongParameterEception e) {
            return e.getMessage();
        }
        //TODO сделать дни для исключения
        //reminder.setExcludeDays(Arrays.asList(params.get(5).split(",")));

        Calendar currDayCal = Calendar.getInstance();
        currDayCal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        RemindTime remindTime = reminder.getRemindTime();
        Calendar reminderDayCal = Calendar.getInstance();
        reminderDayCal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        reminderDayCal.set(Calendar.HOUR_OF_DAY, remindTime.getHour());
        reminderDayCal.set(Calendar.MINUTE, remindTime.getMinute());
        reminderDayCal.set(Calendar.SECOND, 00);
        if (reminderDayCal.after(currDayCal)) {
            reminderDayCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Long nextReminderDelay = (reminderDayCal.getTimeInMillis() - currDayCal.getTimeInMillis())/1000;
        Long periodMillis = TimeUnit.DAYS.toSeconds(reminder.getPeriod());

        ReminderAction reminderAction = new ReminderAction(channel, reminder.getMessage());
        getScheduler().scheduleAtFixedRate(reminderAction, nextReminderDelay-1, periodMillis, TimeUnit.SECONDS);
        return "Remind on " + remindTime.toString()
                + " with message " + "\"" + reminder.getMessage()
                + "\"" + " to be set. Next reminder in " + nextReminderDelay;
    }


    private static ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(3);
        }
        return scheduler;
    }

    private static RemindTime convertRemindTime(String content) throws WrongParameterEception {
        List<String> splintedTime = splitAndCheckParam(content, Const.COLON, 2);
        int hours = Integer.parseInt(splintedTime.get(0));
        int minutes = Integer.parseInt(splintedTime.get(1));

        return new RemindTime(hours, minutes);
    }

    private static String substringMessage(String content) {
        return content.substring(content.indexOf(Const.QUOTE_CHAR)+1, content.lastIndexOf(Const.QUOTE_CHAR));
    }

    private static List<String> splitAndCheckParam(String paramStr, String splitter, int cnt) throws WrongParameterEception {
        List<String> params = Arrays.asList(paramStr.split(splitter));

        if (params.size() < cnt) {
            throw new WrongParameterEception("Parameter with value: " + paramStr
                    + " must be splatted by " + splitter
                    + " and contains " + cnt + " parts.");
        }
        return params;
    }
}
