package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Service
public class UserService {

    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    public boolean addFriend(Long userId, Long friendId) {
        if (!friendships.containsKey(userId)) {
            friendships.put(userId, new HashSet<>());
        }
        friendships.get(userId).add(friendId);

        if (!friendships.containsKey(friendId)) {
            friendships.put(friendId, new HashSet<>());
        }
        friendships.get(friendId).add(userId);
        return (friendships.get(userId).contains(friendId) && friendships.get(friendId).contains(userId));
    }

    public void removeFriend(Long userId, Long friendId) {
        if (friendships.containsKey(userId)) {
            friendships.get(userId).remove(friendId);
        }
        if (friendships.containsKey(friendId)) {
            friendships.get(friendId).remove(userId);
        }
    }

    public Set<Long> getCommonFriends(Long userId1, Long userId2) {
        Set<Long> commonFriends = new HashSet<>();
        if (friendships.containsKey(userId1) && friendships.containsKey(userId2)) {
            Set<Long> friends1 = friendships.get(userId1);
            Set<Long> friends2 = friendships.get(userId2);
            friends1.retainAll(friends2);
            commonFriends = friends1;
        }
        return commonFriends;
    }

    public Set<Long> getMyFriends(Long id) {
        return friendships.get(id);
    }
}