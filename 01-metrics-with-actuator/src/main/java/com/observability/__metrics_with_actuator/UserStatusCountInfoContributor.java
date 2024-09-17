package com.observability.__metrics_with_actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserStatusCountInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Integer> userStatusMap = Map.of("active", 100, "inActive", 50);
        builder.withDetail("userStatus", userStatusMap);
    }
}
