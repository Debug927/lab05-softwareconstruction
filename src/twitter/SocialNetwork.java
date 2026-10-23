/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SocialNetwork {

    /**
     * Guess who might follow whom from evidence in tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(
            List<Tweet> tweets) {

        Map<String, Set<String>> graph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase(Locale.ROOT);

            // Extract expects a list, so pass a list containing this tweet.
            List<Tweet> singleTweet = new ArrayList<>();
            singleTweet.add(tweet);

            Set<String> mentionedUsers =
                    Extract.getMentionedUsers(singleTweet);

            // Ensure the author is represented, even without mentions.
            graph.putIfAbsent(author, new HashSet<String>());

            for (String mentioned : mentionedUsers) {
                String followed = mentioned.toLowerCase(Locale.ROOT);

                // A user cannot follow themselves.
                if (!author.equals(followed)) {
                    graph.get(author).add(followed);
                }

                // The mentioned user is also a person in the network.
                graph.putIfAbsent(followed, new HashSet<String>());
            }
        }

        return graph;
    }

    /**
     * Find all distinct usernames in descending order of follower count.
     */
    public static List<String> influencers(
            Map<String, Set<String>> followsGraph) {

        Map<String, Integer> followerCounts = new HashMap<>();

        // Every username appearing as a key is part of the network.
        for (String user : followsGraph.keySet()) {
            followerCounts.putIfAbsent(
                    user.toLowerCase(Locale.ROOT), 0);
        }

        // Count distinct followers of each username.
        for (Map.Entry<String, Set<String>> entry
                : followsGraph.entrySet()) {

            String follower = entry.getKey().toLowerCase(Locale.ROOT);

            for (String followed : entry.getValue()) {
                String username = followed.toLowerCase(Locale.ROOT);

                if (!follower.equals(username)) {
                    followerCounts.put(
                            username,
                            followerCounts.getOrDefault(username, 0) + 1
                    );
                }
            }
        }

        List<String> result =
                new ArrayList<>(followerCounts.keySet());

        // Highest follower count first.
        // Alphabetical order is just a tie-breaking choice.
        result.sort((user1, user2) -> {
            int comparison = Integer.compare(
                    followerCounts.get(user2),
                    followerCounts.get(user1)
            );

            if (comparison != 0) {
                return comparison;
            }

            return user1.compareTo(user2);
        });

        return result;
    }
}