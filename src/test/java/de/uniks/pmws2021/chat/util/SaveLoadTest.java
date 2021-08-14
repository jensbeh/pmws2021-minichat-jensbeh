package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.model.Status;
import de.uniks.pmws2021.chat.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SaveLoadTest {
    private static final Path USERS_FOLDER = Path.of("saves");
    private static final Path USERS_FILE = USERS_FOLDER.resolve("users.yaml");

    @Test
    public void saveLoadTest() {
        try {
            Files.deleteIfExists(USERS_FILE);
        } catch (Exception e) {
            System.err.println("Error while loading " + USERS_FILE);
            e.printStackTrace();
        }

        User user1 = new User().setName("User 1").setIp("192.168.0.1").setStatus(Status.online).setUserId(0);
        User user2 = new User().setName("User 2").setIp("192.168.0.2").setStatus(Status.offline).setUserId(1);

        ResourceManager.saveServerUsers(user1);
        ResourceManager.saveServerUsers(user2);

        Assert.assertTrue(Files.exists(USERS_FILE));

        List<User> users = ResourceManager.loadServerUsers();

        Assert.assertEquals(users.size(), 2);

        User userNew1;
        User userNew2;

        if (user1.getName().equals(users.get(0).getName())) {
            userNew1 = users.get(0);
            userNew2 = users.get(1);
        } else {
            userNew1 = users.get(1);
            userNew2 = users.get(0);
        }

        Assert.assertEquals(user1.getName(), userNew1.getName());
        Assert.assertEquals(user1.getIp(), userNew1.getIp());
        Assert.assertEquals(user1.getStatus(), userNew1.getStatus());
        Assert.assertEquals(user1.getUserId(), userNew1.getUserId());

        Assert.assertEquals(user2.getName(), userNew2.getName());
        Assert.assertEquals(user2.getIp(), userNew2.getIp());
        Assert.assertEquals(user2.getStatus(), userNew2.getStatus());
        Assert.assertEquals(user2.getUserId(), userNew2.getUserId());
    }
}
