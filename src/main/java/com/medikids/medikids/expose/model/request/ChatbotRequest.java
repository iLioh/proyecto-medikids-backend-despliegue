package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ChatbotRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private List<ChatMessage> history;

    @Setter
    @Getter
    public static class ChatMessage implements Serializable {
        private static final long serialVersionUID = 1L;

        private String role;
        private String content;
    }
}
