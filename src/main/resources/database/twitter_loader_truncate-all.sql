SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE twitter_loader.hashtag;
TRUNCATE TABLE twitter_loader.media;
TRUNCATE TABLE twitter_loader.query_tweet;
TRUNCATE TABLE twitter_loader.query_user;
TRUNCATE TABLE twitter_loader.tweet;
TRUNCATE TABLE twitter_loader.tweet_cache;
TRUNCATE TABLE twitter_loader.tweet_hashtag;
TRUNCATE TABLE twitter_loader.tweet_knowledge;
TRUNCATE TABLE twitter_loader.tweet_media;
TRUNCATE TABLE twitter_loader.tweet_mention;
TRUNCATE TABLE twitter_loader.tweet_quote;
TRUNCATE TABLE twitter_loader.tweet_retweet;
TRUNCATE TABLE twitter_loader.tweet_url;
TRUNCATE TABLE twitter_loader.url;
TRUNCATE TABLE twitter_loader.user;
TRUNCATE TABLE twitter_loader.user_cache;
TRUNCATE TABLE twitter_loader.user_knowledge;

SET FOREIGN_KEY_CHECKS=1;
