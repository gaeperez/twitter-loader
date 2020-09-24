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
@Table(name = "tweet_quote", uniqueConstraints =
@UniqueConstraint(name = "tweet_quote_unique", columnNames = {"quoted_tweet_id", "source_user_id", "quoted_user_id"}))
public class TweetQuoteEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    // The tweet that makes the quote
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "source_tweet_id", nullable = false)
    private TweetEntity sourceTweetEntity;

    // The original tweet that is quoted
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "quoted_tweet_id", nullable = false)
    private TweetEntity quotedTweetEntity;

    // Person who makes the quote
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id", nullable = false)
    private UserEntity sourceUserEntity;

    // Quoted person
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "quoted_user_id", nullable = false)
    private UserEntity targetUserEntity;
}
