package com.observability.__metrics_with_actuator;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Endpoint(id = "release-notes") // same like @Controller ==> /actuator/release-notes
public class CustomActuatorEndpoint {
    private Map<String, List<String>> releaseNotes = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        this.releaseNotes.put("v1.0", List.of("Home Page created", "Logo added to the navbar"));
        this.releaseNotes.put("v1.1", List.of("Login Page created", "session implemented", "cookies implemented"));
        this.releaseNotes.put("v1.2.", List.of("SSO implemented"));
    }

    @ReadOperation // GET  /actuator/release-notes
    public Map<String, List<String>> getReleaseNotes() {
        return this.releaseNotes;
    }

    @ReadOperation // GET /actuator/release-notes/{version}
    public List<String> getReleaseNotesByVersion(@Selector String version) {
        return this.releaseNotes.get(version);
    }

    @WriteOperation // POST  /actuator/release-notes
    public void addReleaseNotes(@Selector String version, @Selector String releaseNotes) {
        List<String> notes = Arrays.stream(releaseNotes.split(", ")).toList();
        this.releaseNotes.put(version, notes);
    }

    @DeleteOperation // DELETE  /actuator/release-notes
    public void deleteNotes(@Selector String version) {
       this.releaseNotes.remove(version);
    }

}
