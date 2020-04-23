package pojos;

import java.util.ArrayList;
import java.util.List;

public class Reminder {
    RemindTime remindTime;
    int period;
    String message;
    List<String> excludeDays = new ArrayList<>();

    public RemindTime getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(RemindTime remindTime) {
        this.remindTime = remindTime;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getExcludeDays() {
        return excludeDays;
    }

    public void setExcludeDays(List<String> excludeDays) {
        this.excludeDays = excludeDays;
    }
}
