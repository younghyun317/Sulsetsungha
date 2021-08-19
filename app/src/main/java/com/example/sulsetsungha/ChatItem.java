package com.example.sulsetsungha;

public class ChatItem {

    String content;
    String name;
    int view;

    public ChatItem(String content, String name, int view) {
        this.content = content;
        this.name = name;
        this.view = view;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public int getView() {
        return view;
    }
}
