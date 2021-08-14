package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.Status;
import de.uniks.pmws2021.chat.model.User;

public class ChatEditor {

    // Connection to model root object
    private Chat chat;

    public User haveUser(String userName, String ip) {

        int maxId = 0;

        if (!chat.getAvailableUser().isEmpty()) {
            for (User user : chat.getAvailableUser()) {
                if (user.getName().equals(userName)) {
                    user.setStatus(Status.online);
                    return user;
                }
                if (user.getUserId() > maxId) {
                    maxId = user.getUserId();
                }
            }
        }

        User newUser = new User().setName(userName).setIp(ip).setStatus(Status.online);
        newUser.setUserId(maxId);
        maxId++;
        chat.withAvailableUser(newUser);
        return newUser;
    }

    public Chat haveChat() {
        chat = new Chat();
        return chat;
    }

    public Chat getChat() {
        return this.chat;
    }
}