package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    void addUser(User user);

    void removeUser(long userId);

    void modifyUser(User updatedUser);

    Collection<User> findAllUsers();

    long getMaxUserId();

    boolean findUserById(long id);

    User findUserByID(long id);

    boolean addFriend(long id, long idFriend);

    void removeFriend(long id, long idFriend);

    Set<User> getCommonFriends(Long id, Long idUser);

    Set<User> getMyFriends(Long id);
}
