package org.example.domain;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    private ResultType type = ResultType.SUCCESS;
    private ArrayList<String> messages = new ArrayList<>();
    private T payload;

    public ResultType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public void addMessage(ResultType type, String message) {
        this.type = type;
        messages.add(message);
    }

    public boolean isSuccess() {
        return messages.size() == 0;
    }
}