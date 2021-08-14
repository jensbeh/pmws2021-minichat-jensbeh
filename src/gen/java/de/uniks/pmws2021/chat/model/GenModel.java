package de.uniks.pmws2021.chat.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

public class GenModel implements ClassModelDecorator {
    class User {
        String name;
        String ip;
        boolean status;

        @Link("availableUser")
        Chat chat;

        @Link("availableUser")
        List<Message> message;
    }

    class Chat {
        String currentUsername;

        @Link("chat")
        List<User> availableUser;

        @Link("chat")
        List<Message> message;
    }

    class Message {
        String sender;
        String recipient;
        String content;
        int messageId;

        @Link("message")
        Chat chat;

        @Link("message")
        User availableUser;
    }

    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}
