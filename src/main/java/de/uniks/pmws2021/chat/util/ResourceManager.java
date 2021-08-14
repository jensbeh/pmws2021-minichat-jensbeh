package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.model.Message;
import de.uniks.pmws2021.chat.model.User;
import org.fulib.yaml.YamlIdMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResourceManager {

    private static final Path USERS_FOLDER = Path.of("saves");
    private static final Path CHAT_FOLDER = Path.of("saves/chats");
    private static final Path USERS_FILE = USERS_FOLDER.resolve("users.yaml");

    // static constructor magic to create the file if absent
    static {
        try {
            if (!Files.isDirectory(USERS_FOLDER)) {
                Files.createDirectory(USERS_FOLDER);
            }
            if (!Files.exists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
        } catch (Exception e) {
            System.err.println("Error while loading " + USERS_FILE);
            e.printStackTrace();
        }
    }

    public static List<User> loadServerUsers() {
        List<User> result = new ArrayList<>();

        try {
            // try to read userList from File
            String usersString = Files.readString(USERS_FILE);

            // parse yaml-string to YamlIdMap
            YamlIdMap yamlIdMap = new YamlIdMap(User.class.getPackageName());

            // decode map
            yamlIdMap.decode(usersString);

            // map the decoded yaml data to real java objects and return list of users
            for (Object object : yamlIdMap.getObjIdMap().values()) {
                if (object instanceof User) {
                    result.add((User) object);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveServerUsers(User user) {
        try {
            // load all existing users
            List<User> oldUsers = loadServerUsers();

            // delete existing user with the same name as the oldUser
            oldUsers.removeIf(oldUser -> oldUser.getName().equals((user.getName())));

            // add copy of user to list
            User toSave = new User().setName(user.getName()).setIp(user.getIp()).setStatus(user.getStatus()).setUserId(user.getUserId());
            oldUsers.add(toSave);

            // serialize as yaml
            YamlIdMap yamlIdMap = new YamlIdMap(User.class.getPackageName());
            yamlIdMap.discoverObjects(oldUsers);
            String yamlData = yamlIdMap.encode();

            // save as .yaml
            Files.writeString(USERS_FILE, yamlData);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while saving User: File -> " + USERS_FILE);
        }
    }


    public static ArrayList<Message> loadChat(String from, String to) {
        ArrayList<Message> result = new ArrayList<>();

        try {
            // try to read userList from File
            try {
                if (!Files.isDirectory(CHAT_FOLDER)) {
                    Files.createDirectory(CHAT_FOLDER);
                }
                if (!Files.exists(CHAT_FOLDER.resolve(from + "-" + to + ".yaml"))) {
                    Files.createFile(CHAT_FOLDER.resolve(from + "-" + to + ".yaml"));
                }
            } catch (Exception e) {
                System.err.println("Error while loading " + CHAT_FOLDER.resolve(from + "-" + to + ".yaml"));
                e.printStackTrace();
            }

            String chatString = Files.readString(CHAT_FOLDER.resolve(from + "-" + to + ".yaml"));

            // parse yaml-string to YamlIdMap
            YamlIdMap yamlIdMap = new YamlIdMap(Message.class.getPackageName());

            // decode map
            yamlIdMap.decode(chatString);


            // map the decoded yaml data to real java objects and return list of users
            for (Object object : yamlIdMap.getObjIdMap().values()) {
                if (object instanceof Message) {
                    result.add((Message) object);
                }
            }

            result.sort(Comparator.comparingInt(Message::getMessageId));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveChat(String from, String to, ArrayList<Message> chat) {
        try {
            // serialize as yaml
            YamlIdMap yamlIdMap = new YamlIdMap(Message.class.getPackageName());
            yamlIdMap.discoverObjects(chat);
            String yamlData = yamlIdMap.encode();

            Files.writeString(CHAT_FOLDER.resolve(from + "-" + to + ".yaml"), yamlData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}