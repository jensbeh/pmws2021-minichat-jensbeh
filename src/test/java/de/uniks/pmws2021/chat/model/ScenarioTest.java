package de.uniks.pmws2021.chat.model;
import org.junit.Test;

public class ScenarioTest
{

   @Test
   public void miniChat()
   {
      User user = new User();
      user.setName("user");
      Chat chat = new Chat();
      chat.setCurrentUsername("chat");
      user.setChat(chat);
   }
}
