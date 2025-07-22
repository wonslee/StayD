package org.example.stayd.common;

import java.util.Optional;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class YesNullableConverter {
        public static boolean toBoolean(Optional<String> c) {
            if (c.isEmpty()) {
                return false;
            }
            return c.get().equals("Y");
        }

        public static char toChar(boolean booleanValue) {
            return booleanValue ? 'Y' : 'N';
        }
}
