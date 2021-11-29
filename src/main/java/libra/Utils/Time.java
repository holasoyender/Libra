package libra.Utils;

public class Time {

    public Time(String time) {
        getTime();
        ms(time);
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static long ms(String time) {

        if(time.endsWith("s")) {
            time = time.replace("s", "");
            try {
                return Long.parseLong(time) * 1000;
            }catch (Exception e) {
                return 0;
            }
        }
        if(time.endsWith("m")) {
            time = time.replace("m", "");
            try {
                return Long.parseLong(time) * 60000;
            } catch (Exception e) {
                return 0;
            }
        }
        if(time.endsWith("h")) {
            time = time.replace("h", "");
            try {
                return Long.parseLong(time) * 3600000;
            } catch (Exception e) {
                return 0;
            }
        }
        if(time.endsWith("d")) {
            time = time.replace("d", "");
            try {
                return Long.parseLong(time) * 86400000;
            } catch (Exception e) {
                return 0;
            }
        }
            try {
                return Long.parseLong(time);
            }catch (Exception e) {
                return 0;
            }
    }
}
