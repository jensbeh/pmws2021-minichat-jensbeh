package de.uniks.pmws2021.chat.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class User {
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_IP = "ip";
    public static final String PROPERTY_CHAT = "chat";
    public static final String PROPERTY_STATUS = "status";
   public static final String PROPERTY_MESSAGE = "message";
    private User copy;
    private String name;
    protected PropertyChangeSupport listeners;
    private String ip;
    private Chat chat;
    private int userId;
    private boolean status;
   private List<Message> message;

    public User() {
    }

    public String getName()
   {
      return this.name;
   }

    public User setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

    public String getIp()
   {
      return this.ip;
   }

    public User setIp(String value)
   {
      if (Objects.equals(value, this.ip))
      {
         return this;
      }

      final String oldValue = this.ip;
      this.ip = value;
      this.firePropertyChange(PROPERTY_IP, oldValue, value);
      return this;
   }

    public Chat getChat()
   {
      return this.chat;
   }

    public User setChat(Chat value)
   {
      if (this.chat == value)
      {
         return this;
      }

      final Chat oldValue = this.chat;
      if (this.chat != null)
      {
         this.chat = null;
         oldValue.withoutAvailableUser(this);
      }
      this.chat = value;
      if (value != null)
      {
         value.withAvailableUser(this);
      }
      this.firePropertyChange(PROPERTY_CHAT, oldValue, value);
      return this;
   }

    public boolean getStatus()
   {
      return this.status;
   }

    public User setStatus(boolean value)
   {
      if (value == this.status)
      {
         return this;
      }

      final boolean oldValue = this.status;
      this.status = value;
      this.firePropertyChange(PROPERTY_STATUS, oldValue, value);
      return this;
   }

   public List<Message> getMessage()
   {
      return this.message != null ? Collections.unmodifiableList(this.message) : Collections.emptyList();
   }

   public User withMessage(Message value)
   {
      if (this.message == null)
      {
         this.message = new ArrayList<>();
      }
      if (!this.message.contains(value))
      {
         this.message.add(value);
         value.setAvailableUser(this);
         this.firePropertyChange(PROPERTY_MESSAGE, null, value);
      }
      return this;
   }

   public User withMessage(Message... value)
   {
      for (final Message item : value)
      {
         this.withMessage(item);
      }
      return this;
   }

   public User withMessage(Collection<? extends Message> value)
   {
      for (final Message item : value)
      {
         this.withMessage(item);
      }
      return this;
   }

   public User withoutMessage(Message value)
   {
      if (this.message != null && this.message.remove(value))
      {
         value.setAvailableUser(null);
         this.firePropertyChange(PROPERTY_MESSAGE, value, null);
      }
      return this;
   }

   public User withoutMessage(Message... value)
   {
      for (final Message item : value)
      {
         this.withoutMessage(item);
      }
      return this;
   }

   public User withoutMessage(Collection<? extends Message> value)
   {
      for (final Message item : value)
      {
         this.withoutMessage(item);
      }
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

    @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getIp());
      return result.substring(1);
   }

    public void removeYou()
   {
      this.setChat(null);
      this.withoutMessage(new ArrayList<>(this.getMessage()));
   }

    public int getUserId() {
        return userId;
    }

    public User setUserId(int userId) {
        this.userId = userId;
        return this;
    }
}
