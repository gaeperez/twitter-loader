package es.uvigo.ei.sing.twitterloader.controllers;

import com.google.gson.Gson;
import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import es.uvigo.ei.sing.twitterloader.services.QueryService;
import es.uvigo.ei.sing.twitterloader.services.TweetService;
import es.uvigo.ei.sing.twitterloader.services.UserService;
import es.uvigo.ei.sing.twitterloader.utils.Functions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import twitter4j.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class TwitterController {

    private final Twitter twitter;
    private final TwitterStream twitterStream;
    private final UserService userService;
    private final TweetService tweetService;
    private final QueryService queryService;

    @Autowired
    public TwitterController(Twitter twitter, TwitterStream twitterStream, UserService userService, TweetService tweetService, QueryService queryService) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
        this.userService = userService;
        this.tweetService = tweetService;
        this.queryService = queryService;
    }

    public void publicStreamingAPI(String followInput, String trackInput) {
        log.debug("Usage: java twitter4j.examples.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]");

        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                try {
                    Path output = Paths.get("E:\\my-path\\").resolve(status.getId() + ".json");
                    Files.write(output, Collections.singletonList(new Gson().toJson(status)), StandardOpenOption.CREATE_NEW);
                    log.info("@{} - {}", status.getUser().getScreenName(), status.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                log.warn("Got a status deletion notice id: {}", statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                log.warn("Got track limitation notice: {}", numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                log.warn("Got scrub_geo event userId: {}, upToStatusId: {}", userId, upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                log.warn("Got stall warning: {}", warning);
            }

            @Override
            public void onException(Exception ex) {
                log.error("Error while retrieving tweets: {}", ex.getMessage());
                ex.printStackTrace();
            }
        });

        ArrayList<Long> follow = new ArrayList<>();
        ArrayList<String> track = new ArrayList<>();

        // Parse input queries
        if (Functions.isNumericalArgument(followInput))
            for (String id : followInput.split(","))
                follow.add(Long.parseLong(id));
        else
            track.addAll(Arrays.asList(trackInput.split(",")));

        long[] followArray = new long[follow.size()];
        for (int i = 0; i < follow.size(); i++)
            followArray[i] = follow.get(i);

        String[] trackArray = track.toArray(new String[0]);

        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        twitterStream.filter(new FilterQuery(0, followArray, trackArray));
    }

    public void searchForTrends(int woeid) throws TwitterException {
        // Current 50 active trends
        Trends availableTrends = twitter.trends().getPlaceTrends(woeid);
        for (Trend trend : availableTrends.getTrends()) {
            log.info("Query: {}, Name: {}, #Tweets: {}, URL: {}", trend.getQuery(), trend.getName(),
                    trend.getTweetVolume(), trend.getURL());
        }

        log.debug(twitter.getRateLimitStatus().get("/trends/available"));
    }

    public void searchForMyRetweets(int maxNumber, boolean getAll) throws TwitterException {
        Paging page;
        long maxId = 0L;
        List<Status> possibleStatuses;
        Set<Status> userRetweets = new LinkedHashSet<>();
        int totalRetweets = 0;

        do {
            // Get the current page (up to 100)
            page = new Paging(1, maxNumber);
            // Set maxId
            if (maxId != 0L)
                page.setMaxId(maxId);

            // Get user mentions (most recent)
            possibleStatuses = twitter.getRetweetsOfMe(page);
            userRetweets.addAll(possibleStatuses);

            for (Status status : possibleStatuses) {
                log.info("@{} retweeted your following tweet '{}'", status.getUser().getScreenName(), status.getText());
                totalRetweets = status.getRetweetCount();
            }

            // Refresh maxId (used to get older tweets). If the ID do not change, then there are no more tweets to get
            if (!possibleStatuses.isEmpty())
                maxId = possibleStatuses.stream().min(Comparator.comparing(Status::getId)).get().getId() - 1;
        } while (getAll && !possibleStatuses.isEmpty());

        log.info("{} retweeted tweets a total of {} times (average number of {}) from {} to {}", userRetweets.size(),
                totalRetweets, (float) totalRetweets / userRetweets.size(),
                userRetweets.stream().map(Status::getCreatedAt).min(Date::compareTo).get(),
                userRetweets.stream().map(Status::getCreatedAt).max(Date::compareTo).get());

        log.debug(twitter.getRateLimitStatus().get("/statuses/retweets_of_me"));
    }

    public void searchForMyMentions(int maxNumber, boolean getAll) throws TwitterException {
        Paging page;
        long maxId = 0L;
        List<Status> possibleStatuses;
        Set<Status> userMentions = new LinkedHashSet<>();

        do {
            // Get the current page
            page = new Paging(1, maxNumber);
            // Set maxId
            if (maxId != 0L)
                page.setMaxId(maxId);

            // Get user mentions (most recent), up to 800
            possibleStatuses = twitter.getMentionsTimeline(page);
            userMentions.addAll(possibleStatuses);

            for (Status status : possibleStatuses) {
                log.info("@{} mentions you in the following tweet '{}'", status.getUser().getScreenName(), status.getText());
            }

            // Refresh maxId (used to get older tweets). If the ID do not change, then there are no more tweets to get
            if (!possibleStatuses.isEmpty())
                maxId = possibleStatuses.stream().min(Comparator.comparing(Status::getId)).get().getId() - 1;
        } while (getAll && !possibleStatuses.isEmpty());

        log.info("{} mentions from {} to {}", userMentions.size(),
                userMentions.stream().map(Status::getCreatedAt).min(Date::compareTo).get(),
                userMentions.stream().map(Status::getCreatedAt).max(Date::compareTo).get());

        log.debug(twitter.getRateLimitStatus().get("/statuses/mentions_timeline"));
    }

    public void searchMostRecentFollowings(String user) throws TwitterException {
        // Use the cursor as -1l to get the most recent followings (5,000)
        IDs friendsIDs = twitter.getFriendsIDs(user, -1L);
        // Show the 100 first followings
        ResponseList<User> followings = twitter.lookupUsers(Arrays.copyOfRange(friendsIDs.getIDs(), 0, 100));

        // Show the info about the followings
        for (User following : followings)
            log.info("@{} is following {} - @{}", user, following.getName(), following.getScreenName());

        Map<String, RateLimitStatus> mapRateLimitStatus = twitter.getRateLimitStatus();
        log.debug(mapRateLimitStatus.get("/friends/ids"));
        log.debug(mapRateLimitStatus.get("/users/lookup"));
    }

    public void searchMostRecentFollowers(String user) throws TwitterException {
        // Use the cursor as -1l to get the most recent followers (5,000)
        IDs followerIds = twitter.getFollowersIDs(user, -1L);
        // Show the 100 first followers
        ResponseList<User> followers = twitter.lookupUsers(Arrays.copyOfRange(followerIds.getIDs(), 0, 100));

        // Show the info about the followers
        for (User follower : followers)
            log.info("{} - @{} is following @{}", follower.getName(), follower.getScreenName(), user);

        Map<String, RateLimitStatus> mapRateLimitStatus = twitter.getRateLimitStatus();
        log.debug(mapRateLimitStatus.get("/followers/ids"));
        log.debug(mapRateLimitStatus.get("/users/lookup"));
    }

    public void searchForFavouriteTweets(String user, int maxNumber, boolean getAll) throws TwitterException {
        Paging page;
        long maxId = 0L;
        int totalFavourites = 0;
        List<Status> possibleStatuses;
        Set<Status> userFavourites = new LinkedHashSet<>();

        do {
            // Get the current page
            page = new Paging(1, maxNumber);
            // Set maxId
            if (maxId != 0L)
                page.setMaxId(maxId);

            // Get user favourites (most recent)
            possibleStatuses = twitter.getFavorites(user, page);
            userFavourites.addAll(possibleStatuses);
            // Count the number of analyzed tweets
            totalFavourites += possibleStatuses.size();

            for (Status status : possibleStatuses) {
                log.info("@{} liked the tweet '{}'", user, status.getText());
            }

            // Refresh maxId (used to get older tweets). If the ID do not change, then there are no more tweets to get
            if (!possibleStatuses.isEmpty())
                maxId = possibleStatuses.stream().min(Comparator.comparing(Status::getId)).get().getId() - 1;
        } while (getAll && !possibleStatuses.isEmpty());

        log.info("{} including {} in the last 30 days", totalFavourites, userFavourites.stream()
                .map(Status::getCreatedAt).map(Functions::convertToLocalDateTime)
                .filter(date -> date.isAfter(LocalDateTime.now().minusDays(30)))
                .count());

        log.debug(twitter.getRateLimitStatus().get("/favorites/list"));
    }

    public void searchForUserTimeline(long userId, int maxNumber, boolean getAll, boolean getRTs,
                                      QueryEntity queryEntity) throws TwitterException {
        Set<Long> parsedTweetIds = new HashSet<>();
        Paging page;
        long maxId = queryEntity.getMaxId();
        // Max number of tweets to retrieve in a request (max value is 200)
        if (getAll || maxNumber > 200)
            maxNumber = 200;
        List<Status> possibleStatuses;

        // Check if source user was already inserted in database
        UserEntity sourceUserEntity = userService.searchForUser(userId);
        sourceUserEntity.getQueries().add(queryEntity);

        log.debug("Showing @{}'s user timeline", sourceUserEntity.getScreenName());
        do {
            // Get the current page
            page = new Paging(1, maxNumber);
            // Set maxId
            if (maxId != 0L)
                page.setMaxId(maxId);

            // Get the most recent tweets for the current user (can only return up to 3,200 tweets)
            possibleStatuses = twitter.getUserTimeline(sourceUserEntity.getId(), page);
            // Sort the results by date (new goes first)
            possibleStatuses = possibleStatuses.stream().sorted(Comparator.comparing(Status::getCreatedAt).reversed()).collect(Collectors.toList());

            // Iterate all retrieved statuses
            boolean isRetweet, getTweet = true;
            String text;
            for (Status status : possibleStatuses) {
                // Check if the tweet is a RT
                text = status.getText();
                isRetweet = text.startsWith("RT");
                if (isRetweet)
                    getTweet = getRTs;

                if (getTweet) {
                    // Check if the tweet is already inserted
                    if (!parsedTweetIds.contains(status.getId())) {
                        // Save the tweets using a transactional method
                        TweetEntity tweetEntity = tweetService.parseOrRetrieveTweet(status, sourceUserEntity, queryEntity);
                        tweetService.saveTransactional(tweetEntity);
                        parsedTweetIds.add(tweetEntity.getId());
                    }
                }
                log.debug("@{} - {}", sourceUserEntity.getScreenName(), text);
            }

            // Refresh maxId (used to get older tweets). If the ID do not change, then there are no more tweets to get
            if (!possibleStatuses.isEmpty())
                maxId = possibleStatuses.stream().min(Comparator.comparing(Status::getId)).get().getId() - 1;
        } while (getAll && !possibleStatuses.isEmpty());

        // Update maxId
        queryEntity.setMaxId(maxId);
        queryService.save(queryEntity);

        log.debug(twitter.getRateLimitStatus().get("/statuses/user_timeline"));
    }

    public void searchForQuery(String toSearch, boolean getAll, QueryEntity queryEntity) throws TwitterException {
        Set<Long> parsedTweetIds = new HashSet<>();
        Query query;
        QueryResult result;
        // SinceId gives the most recent tweet for the input query
        long sinceId = queryEntity.getSinceId();
        do {
            // Create the query
            query = new Query(toSearch);
            // Max number of tweets allowed
            query.setCount(100);
            // Set sinceId
            if (sinceId != 0L)
                query.setSinceId(sinceId);

            // Perform the query and retrieve results
            result = twitter.search(query);

            // Get tweets
            List<Status> tweets = result.getTweets();
            for (Status status : tweets) {
                // Check if source user was already inserted in database
                UserEntity sourceUserEntity = userService.searchForUser(status.getUser().getId());
                sourceUserEntity.getQueries().add(queryEntity);

                // Check if the tweet is already inserted
                if (!parsedTweetIds.contains(status.getId())) {
                    // Save the tweets using a transactional method
                    TweetEntity tweetEntity = tweetService.parseOrRetrieveTweet(status, sourceUserEntity, queryEntity);
                    tweetService.saveTransactional(tweetEntity);
                    parsedTweetIds.add(tweetEntity.getId());
                }
            }

            // Refresh sinceId
            if (!tweets.isEmpty())
                sinceId = tweets.stream().max(Comparator.comparing(Status::getId)).get().getId();
        } while (getAll && result.hasNext());

        // Update sinceId
        queryEntity.setSinceId(sinceId);
        queryService.save(queryEntity);

        log.debug(twitter.getRateLimitStatus().get("/search/tweets"));
    }
}
