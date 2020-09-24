package es.uvigo.ei.sing.twitterloader.entities;

import es.uvigo.ei.sing.twitterloader.utils.Functions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tweet")
public class TweetEntity implements Serializable {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    @Column(name = "annotated_text", columnDefinition = "TEXT")
    private String annotatedText;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "geo_lat", precision = 10, scale = 5)
    private BigDecimal geoLat;
    @Column(name = "geo_long", precision = 10, scale = 5)
    private BigDecimal geoLong;
    @Column(name = "retweeted", nullable = false)
    private boolean retweeted;
    @Column(name = "retweet_count", nullable = false)
    private int retweetCount;
    @Column(name = "favourite", nullable = false)
    private boolean favourite;
    @Column(name = "favourite_count", nullable = false)
    private int favouriteCount;
    @Column(name = "lang", nullable = false, length = 200)
    private String lang;
    @Column(name = "country", length = 200)
    private String country;
    @Column(name = "country_code", length = 3)
    private String countryCode;
    @Column(name = "possibly_sensitive", nullable = false)
    private boolean possiblySensitive;
    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;
    @Column(name = "length", nullable = false)
    private int length;
    @Column(name = "in_reply_to_status_id", nullable = false)
    private Long inReplyToStatusId;
    @Column(name = "in_reply_to_screen_name", length = 20)
    private String inReplyToScreenName;
    @Column(name = "contributors", columnDefinition = "TEXT")
    private String contributors;
    @Column(name = "source", columnDefinition = "TEXT")
    private String source;
    @Column(name = "has_contributors", nullable = false)
    private boolean hasContributors;
    @Column(name = "has_media", nullable = false)
    private boolean hasMedia;
    @Column(name = "in_reply_to_user_id")
    private Long inReplyToUserId;
    @Column(name = "withheld_in_countries", columnDefinition = "TEXT")
    private String withheldInCountries;
    @Column(name = "parsed")
    private boolean parsed;
    @Column(name = "mentions_count")
    private int mentionsCount;
    @Column(name = "current_user_retweet_id")
    private Long currentUserRetweetId;
    @Column(name = "truncated", nullable = false)
    private boolean truncated;
    @Column(name = "access_level", nullable = false)
    private int accessLevel;
    @Column(name = "hashtag_count", nullable = false)
    private int hashtagCount;
    @Column(name = "media_count", nullable = false)
    private int mediaCount;
    @Column(name = "url_count", nullable = false)
    private int urlCount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    // Save the mentions when saving the tweet
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceTweetEntity", cascade = CascadeType.ALL)
    private Set<TweetMentionEntity> tweetMentionEntities = new HashSet<>(0);

    // Save the retweets when saving the tweet
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalTweetEntity", cascade = CascadeType.ALL)
    private Set<TweetRetweetEntity> tweetRetweetEntities = new HashSet<>(0);

    // Save the quotes when saving the tweet
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quotedTweetEntity", cascade = CascadeType.ALL)
    private Set<TweetQuoteEntity> tweetQuoteEntities = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "query_tweet", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "query_id", nullable = false, updatable = false)})
    private Set<QueryEntity> queries = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_media", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "media_id", nullable = false, updatable = false)})
    private Set<MediaEntity> medias = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_hashtag", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "hashtag_id", nullable = false, updatable = false)})
    private Set<HashtagEntity> hashtagEntities = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_url", joinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)})
    private Set<UrlEntity> urlEntities = new HashSet<>(0);

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tweetEntity", cascade = CascadeType.ALL)
    @Transient
    private TweetCacheEntity tweetCacheEntity = null;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tweetEntity", cascade = CascadeType.ALL)
    private TweetKnowledgeEntity tweetKnowledgeEntity = null;

    public TweetEntity(Status status) {
        this.id = status.getId();
        this.text = status.getText().trim();
        this.created = Functions.convertToLocalDateTime(status.getCreatedAt());
        // Check tweet geolocation
        GeoLocation geoLocation = status.getGeoLocation();
        if (geoLocation != null) {
            this.geoLat = BigDecimal.valueOf(geoLocation.getLatitude());
            this.geoLong = BigDecimal.valueOf(geoLocation.getLongitude());
        }
        this.retweeted = status.isRetweeted();
        this.retweetCount = status.getRetweetCount();
        this.favourite = status.isFavorited();
        this.favouriteCount = status.getFavoriteCount();
        this.lang = status.getLang();
        // Check tweet place
        Place place = status.getPlace();
        if (place != null) {
            this.country = place.getCountry();
            this.countryCode = place.getCountryCode();
        }
        this.possiblySensitive = status.isPossiblySensitive();
        this.modified = LocalDateTime.now();
        this.length = this.text.length();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        // Check tweet contributors
        long[] contributors = status.getContributors();
        if (contributors != null && contributors.length > 0) {
            this.contributors = Arrays.toString(contributors);
            this.hasContributors = !this.contributors.isEmpty();
        }
        this.source = status.getSource().replaceAll("<[^>]*>", "").trim();
        // Check tweet medias
        twitter4j.MediaEntity[] medias = status.getMediaEntities();
        this.mediaCount = medias != null ? medias.length : 0;
        this.hasMedia = mediaCount > 0;
        this.inReplyToUserId = status.getInReplyToUserId();
        // Check tweet withheld in countries
        String[] withheldInCountries = status.getWithheldInCountries();
        if (withheldInCountries != null)
            this.withheldInCountries = String.join(",", withheldInCountries);
        this.parsed = false;
        // Check tweet mentions
        UserMentionEntity[] mentions = status.getUserMentionEntities();
        this.mentionsCount = mentions != null ? mentions.length : 0;
        this.currentUserRetweetId = status.getCurrentUserRetweetId();
        this.truncated = status.isTruncated();
        this.accessLevel = status.getAccessLevel();
        twitter4j.HashtagEntity[] hashtags = status.getHashtagEntities();
        this.hashtagCount = hashtags != null ? hashtags.length : 0;
        twitter4j.URLEntity[] urls = status.getURLEntities();
        this.urlCount = urls != null ? urls.length : 0;
    }

    // Two tweets are equals if they have the same ID
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TweetEntity))
            return false;
        if (obj == this)
            return true;
        return this.getId() == ((TweetEntity) obj).getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
