package dev.studentpp1.streamingservice.users.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class AppRegex {
    // (?=<some chars>) -> check if string has <some chars>
    // []{8,} -> allow any symbols in [] min 8 times and greater
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
