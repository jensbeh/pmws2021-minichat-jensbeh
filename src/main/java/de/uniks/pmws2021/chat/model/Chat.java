package de.uniks.pmws2021.chat.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;

public class Chat {
    public static final String PROPERTY_CURRENT_USERNAME = "currentUsername";
    public static final String PROPERTY_AVAILABLE_USER = "availableUser";
   public static final String PROPERTY_MESSAGE = "message";
    protected PropertyChangeSupport listeners;
    private String currentUsername;
    private List<User> availableUser;
   private List<Message> message;

    public String getCurrentUsername()
   {
      return this.currentUsername;
   }

    public Chat setCurrentUsername(String value)
   {
      if (Objects.equals(value, this.currentUsername))
      {
         return this;
      }

      final String oldValue = this.currentUsername;
      this.currentUsername = value;
      this.firePropertyChange(PROPERTY_CURRENT_USERNAME, oldValue, value);
      return this;
   }

    public List<User> getAvailableUser()
   {
      return this.availableUser != null ? Collections.unmodifiableList(this.availableUser) : Collections.emptyList();
   }

    public Chat withAvailableUser(User value)
   {
      if (this.availableUser == null)
      {
         this.availableUser = new ArrayList<>();
      }
      if (!this.availableUser.contains(value))
      {
         this.availableUser.add(value);
         value.setChat(this);
         this.firePropertyChange(PROPERTY_AVAILABLE_USER, null, value);
      }
      return this;
   }

    public Chat withAvailableUser(User... value)
   {
      for (final User item : value)
      {
         this.withAvailableUser(item);
      }
      return this;
   }

    public Chat withAvailableUser(Collection<? extends User> value)
   {
      for (final User item : value)
      {
         this.withAvailableUser(item);
      }
      return this;
   }

    public Chat withoutAvailableUser(User value)
   {
      if (this.availableUser != null && this.availableUser.remove(value))
      {
         value.setChat(null);
         this.firePropertyChange(PROPERTY_AVAILABLE_USER, value, null);
      }
      return this;
   }

    public Chat withoutAvailableUser(User... value)
   {
      for (final User item : value)
      {
         this.withoutAvailableUser(item);
      }
      return this;
   }

    public Chat withoutAvailableUser(Collection<? extends User> value)
   {
      for (final User item : value)
      {
         this.withoutAvailableUser(item);
      }
      return this;
   }

   public List<Message> getMessage()
   {
      return this.message != null ? Collections.unmodifiableList(this.message) : Collections.emptyList();
   }

   public Chat withMessage(Message value)
   {
      if (this.message == null)
      {
         this.message = new ArrayList<>();
      }
      if (!this.message.contains(value))
      {
         this.message.add(value);
         value.setChat(this);
         this.firePropertyChange(PROPERTY_MESSAGE, null, value);
      }
      return this;
   }

   public Chat withMessage(Message... value)
   {
      for (final Message item : value)
      {
         this.withMessage(item);
      }
      return this;
   }

   public Chat withMessage(Collection<? extends Message> value)
   {
      for (final Message item : value)
      {
         this.withMessage(item);
      }
      return this;
   }

   public Chat withoutMessage(Message value)
   {
      if (this.message != null && this.message.remove(value))
      {
         value.setChat(null);
         this.firePropertyChange(PROPERTY_MESSAGE, value, null);
      }
      return this;
   }

   public Chat withoutMessage(Message... value)
   {
      for (final Message item : value)
      {
         this.withoutMessage(item);
      }
      return this;
   }

   public Chat withoutMessage(Collection<? extends Message> value)
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

    public void removeYou()
   {
      this.withoutAvailableUser(new ArrayList<>(this.getAvailableUser()));
      this.withoutMessage(new ArrayList<>(this.getMessage()));
   }

    @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getCurrentUsername());
      return result.substring(1);
   }
}
