package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tweet_retweet", uniqueConstraints =
@UniqueConstraint(name = "tweet_retweet_unique", columnNames = {"original_tweet_id", "source_user_id", "target_user_id"}))
public class TweetRetweetEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    // The retweet
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "source_tweet_id", nullable = false)
    private TweetEntity sourceTweetEntity;

    // The original tweet that is retweeted
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "original_tweet_id", nullable = false)
    private TweetEntity originalTweetEntity;

    // Person who makes the retweet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id", nullable = false)
    private UserEntity sourceUserEntity;

    // Person who receives the retweet
    // Save the target user when saving the retweet. The source user and the source tweet should be save beforehand
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "target_user_id", nullable = false)
    private UserEntity targetUserEntity;
}
