
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy for guessFollowsGraph(tweets):
     * Number of tweets: 0, 1, > 1
     * Mentions in a tweet: 0, 1, > 1
     * Tweets by the same author: 1, > 1 with different mentions
     * Case of a mentioned username: same as elsewhere, different
     *
     * The specification is underdetermined: usernames may use any case,
     * and users with empty sets may be present or absent.
     * Tests check only the follows relationships required by the spec.
     *
     * Testing strategy for influencers(followsGraph):
     * Number of users: 0, 1, > 1
     * Followers of the top user: 0, 1, > 1
     * Follower counts: all different, some tied
     */

    private static final Instant d1 =
            Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 =
            Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet noMention =
            new Tweet(1, "alyssa",
                    "is it reasonable to talk about rivest so much?", d1);

    private static final Tweet oneMention =
            new Tweet(2, "bbitdiddle",
                    "saving a seat for @Alyssa", d1);

    private static final Tweet twoMentions =
            new Tweet(3, "evaluator",
                    "thanks @bbitdiddle and @alyssa", d2);

    private static final Tweet bbitAgain =
            new Tweet(4, "BBitDiddle",
                    "@evaluator see you at lecture", d2);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // Task 3a: guessFollowsGraph tests

    // Covers: 0 tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", graph.isEmpty());
    }

    // Covers: 1 tweet, 0 mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(noMention));

        for (Set<String> follows : graph.values()) {
            assertTrue("expected no follows", follows.isEmpty());
        }
    }

    // Covers: 1 tweet, 1 mention, different case
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(oneMention));

        assertTrue("expected bbitdiddle follows alyssa",
                follows(graph, "bbitdiddle", "alyssa"));
    }

    // Covers: 1 tweet, more than 1 mention
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(twoMentions));

        assertTrue(follows(graph, "evaluator", "bbitdiddle"));
        assertTrue(follows(graph, "evaluator", "alyssa"));
    }

    // Covers: more than 1 tweet by the same user
    @Test
    public void testGuessFollowsGraphMultipleTweetsOneUser() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(
                        Arrays.asList(oneMention, bbitAgain));

        assertTrue(follows(graph, "bbitdiddle", "alyssa"));
        assertTrue(follows(graph, "bbitdiddle", "evaluator"));
    }

    // Task 3a: influencers tests

    // Covers: 0 users
    @Test
    public void testInfluencersEmpty() {
        List<String> influencers =
                SocialNetwork.influencers(new HashMap<>());

        assertTrue("expected empty list", influencers.isEmpty());
    }

    // Covers: 1 user, 0 followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alyssa", new HashSet<>());

        List<String> influencers = SocialNetwork.influencers(graph);

        assertEquals(1, influencers.size());
        assertEquals("alyssa",
                influencers.get(0).toLowerCase());
    }

    // Covers: more than 1 user, top user has 1 follower
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("bbitdiddle",
                new HashSet<>(Arrays.asList("alyssa")));

        List<String> influencers = SocialNetwork.influencers(graph);

        assertEquals(2, influencers.size());
        assertEquals("alyssa",
                influencers.get(0).toLowerCase());
    }

    // Covers: multiple influencers, all counts different
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("bbitdiddle",
                new HashSet<>(Arrays.asList("alyssa")));
        graph.put("evaluator",
                new HashSet<>(Arrays.asList("alyssa", "bbitdiddle")));

        List<String> influencers = SocialNetwork.influencers(graph);

        assertEquals(3, influencers.size());
        assertEquals("alyssa",
                influencers.get(0).toLowerCase());
        assertEquals("bbitdiddle",
                influencers.get(1).toLowerCase());
        assertEquals("evaluator",
                influencers.get(2).toLowerCase());
    }

    // Covers: some follower counts tied
    @Test
    public void testInfluencersTied() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("evaluator",
                new HashSet<>(Arrays.asList("alyssa", "bbitdiddle")));

        List<String> influencers = SocialNetwork.influencers(graph);

        Set<String> topTwo = new HashSet<>();
        topTwo.add(influencers.get(0).toLowerCase());
        topTwo.add(influencers.get(1).toLowerCase());

        assertEquals(new HashSet<>(Arrays.asList(
                "alyssa", "bbitdiddle")), topTwo);
        assertEquals("evaluator",
                influencers.get(2).toLowerCase());
    }

    /*
     * Helper: checks whether follower follows followed,
     * ignoring username capitalization.
     */
    private static boolean follows(
            Map<String, Set<String>> graph,
            String follower, String followed) {

        for (Map.Entry<String, Set<String>> entry
                : graph.entrySet()) {

            if (entry.getKey().equalsIgnoreCase(follower)) {
                for (String name : entry.getValue()) {
                    if (name.equalsIgnoreCase(followed)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
}
