package project.capstone.event;

import lombok.Getter;
import project.capstone.dto.Read;

@Getter
public class ReadStatusUpdatedEvent {
    private final Read read;

    public ReadStatusUpdatedEvent(Read read) {
        this.read = read;
    }
}
