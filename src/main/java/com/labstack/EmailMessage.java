package com.labstack;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the email message.
 */
public class EmailMessage {
    private String from;
    private String to;
    private String subject;
    private String body;
    @Json(name = "inlines")
    protected List<EmailFile> inlineFiles = new ArrayList<>();
    @Json(name = "attachments")
    protected List<EmailFile> attachmentFiles = new ArrayList<>();
    protected transient List<String> inlines = new ArrayList<>();
    protected transient List<String> attachments = new ArrayList<>();

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addInline(String file) {
        inlines.add(file);
    }

    public void addAttachment(String file) {
        attachments.add(file);
    }
}

class EmailFile {
    private String name;
    private String type;
    private String content;

    public EmailFile(String name, String type, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }
}