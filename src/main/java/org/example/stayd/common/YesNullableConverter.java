package org.example.stayd.common;

import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class YesNullableConverter {
    public static boolean toBoolean(String c) {
        if (Objects.isNull(c)) {
            return false;
        }

        return c.equals("Y");
    }

    public static char toChar(boolean booleanValue) {
        return booleanValue ? 'Y' : 'N';
    }
}
