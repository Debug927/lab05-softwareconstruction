/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

	/*

	* Testing strategy for writtenBy(tweets, username):
	* number of tweets: 0, 1, > 1
	* number of tweets by username: 0, 1, > 1
	* case of username: same as author, different from author
	*
	* Testing strategy for inTimespan(tweets, timespan):
	* number of tweets: 0, > 1
	* tweets inside timespan: none, some, all
	* tweet exactly on start or end of timespan: yes, no
	*
	* Testing strategy for containing(tweets, words):
	* number of words: 1, > 1
	* matching tweets: none, some, all
	* case of word: same as in tweet, different
	* word appears only as part of a bigger word: yes, no
	*
	* Every result is also checked for keeping the input order.
	  */

    

private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

private static final Tweet tweet1 =
        new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);

private static final Tweet tweet2 =
        new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

private static final Tweet tweet3 =
        new Tweet(3, "Alyssa", "Talking about the pset tonight", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }

@Test
public void testWrittenByDifferentCaseMultipleResults() {
    List<Tweet> writtenBy = Filter.writtenBy(
            Arrays.asList(tweet1, tweet2, tweet3), "ALYSSA");

    assertEquals("expected two tweets in order",
            Arrays.asList(tweet1, tweet3), writtenBy);
}

@Test
public void testInTimespanEndpointsIncluded() {
    List<Tweet> inTimespan = Filter.inTimespan(
            Arrays.asList(tweet1, tweet2, tweet3),
            new Timespan(d2, d3));

    assertEquals("expected tweets on the endpoints",
            Arrays.asList(tweet2, tweet3), inTimespan);
}

@Test
public void testContainingCaseAndWholeWords() {
    List<Tweet> containing = Filter.containing(
            Arrays.asList(tweet1, tweet2, tweet3),
            Arrays.asList("TALK"));

    assertEquals("expected tweets 1 and 2 only",
            Arrays.asList(tweet1, tweet2), containing);
}


@Test
public void testWrittenByNoMatches() {
    List<Tweet> result = Filter.writtenBy(
            Arrays.asList(tweet1, tweet2, tweet3), "nobody");

    assertTrue("expected empty list", result.isEmpty());
}

@Test
public void testWrittenBySingleTweet() {
    List<Tweet> result = Filter.writtenBy(
            Arrays.asList(tweet1), "alyssa");

    assertEquals(Arrays.asList(tweet1), result);
}

@Test
public void testInTimespanNoMatches() {
    Instant start = Instant.parse("2016-02-17T13:00:00Z");
    Instant end = Instant.parse("2016-02-17T14:00:00Z");

    List<Tweet> result = Filter.inTimespan(
            Arrays.asList(tweet1, tweet2, tweet3),
            new Timespan(start, end));

    assertTrue("expected empty list", result.isEmpty());
}

@Test
public void testContainingNoMatches() {
    List<Tweet> result = Filter.containing(
            Arrays.asList(tweet1, tweet2, tweet3),
            Arrays.asList("banana"));

    assertTrue("expected empty list", result.isEmpty());
}

@Test
public void testContainingMultipleWords() {
    List<Tweet> result = Filter.containing(
            Arrays.asList(tweet1, tweet2, tweet3),
            Arrays.asList("pset", "rivest"));

    assertEquals("expected matching tweets in input order",
            Arrays.asList(tweet1, tweet2, tweet3), result);
}
    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
