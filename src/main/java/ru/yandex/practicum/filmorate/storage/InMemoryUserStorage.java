package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public User addUser(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User modifyUser(User updatedUser) {
        if (!users.containsKey(updatedUser.getId())) {
            throw new NotFoundException("Пользователь с id " + updatedUser.getId() + " не найден");
        }
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
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
    public void removeUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        users.remove(userId);
    }

    @Override
    public boolean addFriend(long id, long idFriend) {
        User user = findUserByID(id);
        User friend = findUserByID(idFriend);
        if (user != null && friend != null) {
            user.getFriends().add(idFriend);
            friend.getFriends().add(id);
            return true;
        }
        return false;
    }

    @Override
    public void removeFriend(long id, long idFriend) {
        User user = findUserByID(id);
        User friend = findUserByID(idFriend);
        if (user != null && friend != null) {
            user.getFriends().remove(idFriend);
            friend.getFriends().remove(id);
        }
    }

    @Override
    public Collection<User> getMyFriends(Long id) {
        User user = findUserByID(id);
        if (user == null) return Collections.emptyList();
        return user.getFriends().stream()
                .map(this::findUserByID)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = findUserByID(id);
        User other = findUserByID(otherId);
        if (user == null || other == null) return Collections.emptyList();

        Set<Long> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());

        return commonIds.stream()
                .map(this::findUserByID)
                .collect(Collectors.toList());
    }

    @Override
    public boolean findUserById(long id) {
        return users.containsKey(id);
    }

    @Override
    public long getMaxUserId() {
        return currentId - 1;
    }
}