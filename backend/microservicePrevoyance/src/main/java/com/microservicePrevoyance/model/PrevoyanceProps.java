package com.microservicePrevoyance.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "prevoyance")
public class PrevoyanceProps {
    private List<String> keywords;
    private int maxNotes = 200;
}
