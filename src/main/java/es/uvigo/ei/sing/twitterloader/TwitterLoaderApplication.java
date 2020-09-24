package es.uvigo.ei.sing.twitterloader;

import es.uvigo.ei.sing.twitterloader.controllers.TwitterController;
import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.services.QueryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class TwitterLoaderApplication implements CommandLineRunner {

    private final TwitterController twitterController;
    private final QueryService queryService;

    @Autowired
    public TwitterLoaderApplication(TwitterController twitterController, QueryService queryService) {
        this.twitterController = twitterController;
        this.queryService = queryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(TwitterLoaderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Since_id and max_id: https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
        // Rate limits: https://developer.twitter.com/en/docs/developer-utilities/rate-limit-status/api-reference/get-application-rate_limit_status
        // TODO: 13/02/2020 Make rate limit validations (try/catch exceptions)

        // Get input query
        QueryEntity queryEntity = queryService.findById(2);
        log.info("Executing query {} with type {}", queryEntity.getQuery(), queryEntity.getType());
        switch (queryEntity.getType()) {
            case QUERY:
                // Search for tweets
                twitterController.searchForQuery(queryEntity.getQuery(), true, queryEntity);
                break;
            case TREND:
                break;
            case STREAM:
                break;
            case TIMELINE:
                // Search for user timeline
                twitterController.searchForUserTimeline(Long.parseLong(queryEntity.getQuery()), 200, true,
                        true, queryEntity);
                break;
        }

        /// Usage examples
        // Search for favourite tweets
        // twitterController.searchForFavouriteTweets("XXX", 200, true, twitter);

        // Search for user followers
        // twitterController.searchMostRecentFollowers("XXX", twitter);

        // Search for user followings
        // twitterController.searchMostRecentFollowings("XXX", twitter);

        // Search for user mentions
        // twitterController.searchForMentions(200, false, twitter);

        // Search for user retweets
        // twitterController.searchForRetweets(200, false, twitter);

        // Search for trends in Barcelona
        // twitterController.searchForTrends(753692, twitter);

        // Streaming API
        // TwitterStream twitterStream = twitterController.instanceTwitterStream();
        // twitterController.publicStreamingAPI("", "XXX,XXX", twitterStream);
    }
}
