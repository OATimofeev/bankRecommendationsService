package ru.timofeev.recservice.telegram;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessagePattern {

    public static final Pattern RECOMMEND_PATTERN = Pattern.compile("^/recommend\\s+([a-zA-Z.]+)$");
}
