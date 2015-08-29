package org.heat.world.players.notifications;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {
    public static int[] uncompressNotifications(final int[] output) {
        List<Integer> notifications = new ArrayList<>();

        for(int offset=0; offset < output.length; ++offset) {
            int value = output[offset];
            for(int bit=0; bit < 32; ++bit) {
                if((value & 1)==1)
                    notifications.add((bit + (offset * 32)));
                value >>= 1;
            }
        }

        return notifications.stream().mapToInt(i -> i).toArray();
    }

    public static int[] compressNotifications(int[] input) {
        if(input.length == 0) return input;

        int msb = 0;
        for(int notification : input)
            if(notification > msb)
                msb = notification;

        int[] output = new int[(msb/32)+1];

        for(int notification : input)
            output[notification/32] |= 1<<notification;

        return output;
    }
}
