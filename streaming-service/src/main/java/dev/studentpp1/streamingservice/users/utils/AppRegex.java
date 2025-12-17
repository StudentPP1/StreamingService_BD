package dev.studentpp1.streamingservice.users.utils;

public final class AppRegex {
    private AppRegex() {}
    // min one [a-z], [A-Z], digit, specials
    // allowed range of symbols in []{8,} with min size = 8
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}
