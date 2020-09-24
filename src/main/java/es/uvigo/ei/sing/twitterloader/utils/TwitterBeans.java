package es.uvigo.ei.sing.twitterloader.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Configuration
public class TwitterBeans {

    @Bean
    public Twitter twitter() {
        return TwitterFactory.getSingleton();
    }

    @Bean
    public TwitterStream twitterStream() {
        return TwitterStreamFactory.getSingleton();
    }
}
