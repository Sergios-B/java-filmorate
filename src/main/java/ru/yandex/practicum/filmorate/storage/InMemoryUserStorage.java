package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final UserService userService;

    @Autowired
    public InMemoryUserStorage(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void modifyUser(User updatedUser) {
        users.put(updatedUser.getId(), updatedUser);
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUserByID(long id) {
        return users.get(id);
    }

    @Override
    public boolean findUserById(long id) {
        return users.containsKey(id);
    }

    @Override
    public long getMaxUserId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0);
    }

    @Override
    public boolean addFriend(long id, long idFriend) {
        userService.addFriend(id, idFriend);
        return true;
    }

    @Override
    public void removeFriend(long id, long idFriend) {
        userService.removeFriend(id, idFriend);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long idUser) {
        return userService.getCommonFriends(id, idUser).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMyFriends(Long id) {
        return userService.getMyFriends(id).stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUser(long userId) {
        users.remove(userId);
    }
}
