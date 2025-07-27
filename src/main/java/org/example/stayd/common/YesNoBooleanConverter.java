// 작성자 : 이원석
package org.example.stayd.common;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class YesNoBooleanConverter {
    public static boolean toBoolean(char characterValue) {
        return characterValue == 'Y';
    }

    public static char toChar(boolean booleanValue) {
        return booleanValue ? 'Y' : 'N';
    }
}
