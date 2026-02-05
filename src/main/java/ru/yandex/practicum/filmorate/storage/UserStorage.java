package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    Collection<User> getCommonFriends(Long id, Long idUser);

    Collection<User> getMyFriends(Long id);

    void removeUser(long userId);

    User modifyUser(User updatedUser);

    Collection<User> findAllUsers();

    long getMaxUserId();

    boolean findUserById(long id);

    User findUserByID(long id);

    boolean addFriend(long id, long idFriend);

    void removeFriend(long id, long idFriend);
}