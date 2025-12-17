package dev.studentpp1.streamingservice.common.time;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class ClockService {
    private Clock clock = Clock.systemDefaultZone();

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
