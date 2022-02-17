package com.xtjmap.aric.bootzkclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author AricSun
 * @date 2022.02.16 14:51
 */
@Data
@Component
@ConfigurationProperties(prefix = "curator")
public class WrapperZK {
    private int retryCount;
    private int elapsedTimeMs;
    private String connectString;
    private int sessionTimoutMs;
    private int connectionTimeoutMs;
}
