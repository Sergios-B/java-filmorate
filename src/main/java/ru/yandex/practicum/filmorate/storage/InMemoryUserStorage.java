package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

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
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
    }

    @Override
    public void removeUser(long userId) {
        users.remove(userId);
    }

    @Override
    public void modifyUser(User updatedUser) {
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public long getMaxUserId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
    }

    @Override
    public boolean findUserById(long id) {
        return users.containsKey(id);
    }

    @Override
    public User findUserByID(long id) {
        return users.get(id);
    }

    @Override
    public boolean addFriend(long id, long idFriend) {
        return userService.addFriend(id, idFriend);
    }

    @Override
    public void removeFriend(long id, long idFriend) {
        userService.removeFriend(id, idFriend);
    }

    @Override
    public Set<User> getCommonFriends(Long id, Long idUser) {
        Set<User> ourFriends = new HashSet<>();
        for (Long idOurFriend : userService.getCommonFriends(id, idUser)) {
            ourFriends.add(users.get(idOurFriend));
        }
        return ourFriends;
    }

    @Override
    public Set<User> getMyFriends(Long id) {
        Set<User> myFriends = new HashSet<>();
        for (Long idFriend : userService.getMyFriends(id)) {
            myFriends.add(users.get(idFriend));
        }
        return myFriends;
    }
}