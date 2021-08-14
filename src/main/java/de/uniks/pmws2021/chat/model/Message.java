package de.uniks.pmws2021.chat.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Message {
    public static final String PROPERTY_SENDER = "sender";
    public static final String PROPERTY_RECIPIENT = "recipient";
    public static final String PROPERTY_CONTENT = "content";
    public static final String PROPERTY_MESSAGE_ID = "messageId";
    public static final String PROPERTY_CHAT = "chat";
    public static final String PROPERTY_AVAILABLE_USER = "availableUser";

    private String sender;
    private String recipient;
    private String content;
    private int messageId;
    protected PropertyChangeSupport listeners;
    private Chat chat;
    private User availableUser;

    public Message() {

    }

    public Message(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public Message(String sender, String recipient, String content, int messageId) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.messageId = messageId;
    }

    public String getSender()
   {
      return this.sender;
   }

    public String getRecipient()
   {
      return this.recipient;
   }

    public String getContent()
   {
      return this.content;
   }

    public int getMessageId()
   {
      return this.messageId;
   }

    public Message setMessageId(int value)
   {
      if (value == this.messageId)
      {
         return this;
      }

      final int oldValue = this.messageId;
      this.messageId = value;
      this.firePropertyChange(PROPERTY_MESSAGE_ID, oldValue, value);
      return this;
   }

    public Message setSender(String value)
   {
      if (Objects.equals(value, this.sender))
      {
         return this;
      }

      final String oldValue = this.sender;
      this.sender = value;
      this.firePropertyChange(PROPERTY_SENDER, oldValue, value);
      return this;
   }

    public Message setRecipient(String value)
   {
      if (Objects.equals(value, this.recipient))
      {
         return this;
      }

      final String oldValue = this.recipient;
      this.recipient = value;
      this.firePropertyChange(PROPERTY_RECIPIENT, oldValue, value);
      return this;
   }

    public Message setContent(String value)
   {
      if (Objects.equals(value, this.content))
      {
         return this;
      }

      final String oldValue = this.content;
      this.content = value;
      this.firePropertyChange(PROPERTY_CONTENT, oldValue, value);
      return this;
   }

    public Chat getChat()
   {
      return this.chat;
   }

    public Message setChat(Chat value)
   {
      if (this.chat == value)
      {
         return this;
      }

      final Chat oldValue = this.chat;
      if (this.chat != null)
      {
         this.chat = null;
         oldValue.withoutMessage(this);
      }
      this.chat = value;
      if (value != null)
      {
         value.withMessage(this);
      }
      this.firePropertyChange(PROPERTY_CHAT, oldValue, value);
      return this;
   }

    public User getAvailableUser()
   {
      return this.availableUser;
   }

    public Message setAvailableUser(User value)
   {
      if (this.availableUser == value)
      {
         return this;
      }

      final User oldValue = this.availableUser;
      if (this.availableUser != null)
      {
         this.availableUser = null;
         oldValue.withoutMessage(this);
      }
      this.availableUser = value;
      if (value != null)
      {
         value.withMessage(this);
      }
      this.firePropertyChange(PROPERTY_AVAILABLE_USER, oldValue, value);
      return this;
   }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

    public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

    public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

    public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

    public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

    public void removeYou()
   {
      this.setChat(null);
      this.setAvailableUser(null);
   }

    @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getSender());
      result.append(' ').append(this.getRecipient());
      result.append(' ').append(this.getContent());
      return result.substring(1);
   }
}
