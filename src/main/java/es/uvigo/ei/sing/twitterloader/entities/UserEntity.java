package es.uvigo.ei.sing.twitterloader.entities;

import es.uvigo.ei.sing.twitterloader.utils.Functions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private long id;
    @Column(name = "screen_name", nullable = false, length = 22)
    private String screenName;
    @Column(name = "name")
    private String name;
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;
    @Column(name = "location", length = 200)
    private String location;
    @Column(name = "url", columnDefinition = "TEXT")
    private String url;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description = "";
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "followers_count")
    private Integer followersCount;
    @Column(name = "friends_count")
    private Integer friendsCount;
    @Column(name = "statuses_count")
    private Integer statusesCount;
    @Column(name = "time_zone", length = 50)
    private String timeZone;
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
    @Column(name = "lang", length = 5)
    private String lang;
    @Column(name = "utc_ofset")
    private Integer utcOffset;
    @Column(name = "verified", nullable = false)
    private boolean verified;
    @Column(name = "inserted", nullable = false)
    private LocalDateTime inserted;
    @Column(name = "status_geo_lat", precision = 10, scale = 5)
    private BigDecimal statusGeoLat;
    @Column(name = "status_geo_long", precision = 10, scale = 5)
    private BigDecimal statusGeoLong;
    @Column(name = "status_country", length = 200)
    private String statusCountry;
    @Column(name = "status_country_code", length = 5)
    private String statusCountryCode;
    @Column(name = "suspended", nullable = false)
    private boolean suspended;
    @Column(name = "url_entity", columnDefinition = "TEXT")
    private String urlExpanded;
    @Column(name = "contributor", nullable = false)
    private boolean contributor;
    @Column(name = "translator", nullable = false)
    private boolean translator;
    @Column(name = "default_profile", nullable = false)
    private boolean defaultProfile;
    @Column(name = "default_profile_image", nullable = false)
    private boolean defaultProfileImage;
    @Column(name = "show_all_inline_media", nullable = false)
    private boolean showAllInlineMedia;
    @Column(name = "geo_enabled", nullable = false)
    private boolean geoEnabled;
    @Column(name = "protected_user", nullable = false)
    private boolean protectedUser;
    @Column(name = "email")
    private String email;
    @Column(name = "favourites_count", nullable = false)
    private Integer favoritesCount;
    @Column(name = "access_level", nullable = false)
    private Integer accessLevel;
    @Column(name = "listed_count", nullable = false)
    private Integer listedCount;
    @Column(name = "original_profile_image_url", columnDefinition = "TEXT")
    private String originalProfileImageUrl;
    @Column(name = "original_profile_image_url_https", columnDefinition = "TEXT")
    private String originalProfileImageUrlHttps;
    @Column(name = "bigger_profile_image_url", columnDefinition = "TEXT")
    private String biggerProfileImageUrl;
    @Column(name = "bigger_profile_image_url_https", columnDefinition = "TEXT")
    private String biggerProfileImageUrlHttps;
    @Column(name = "profile_text_color", length = 7)
    private String profileTextColor;
    @Column(name = "profile_link_color", length = 7)
    private String profileLinkColor;
    @Column(name = "profile_sidebar_fill_color", length = 7)
    private String profileSidebarFillColor;
    @Column(name = "profile_sidebar_border_color", length = 7)
    private String profileSidebarBorderColor;
    @Column(name = "profile_background_color", length = 7)
    private String profileBackgroundColor;
    @Column(name = "parsed")
    private boolean parsed;
    @Column(name = "withheld_in_countries", columnDefinition = "TEXT")
    private String withheldInCountries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userEntity", cascade = {CascadeType.ALL})
    private Set<TweetEntity> tweetEntities = new HashSet<>(0);

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userEntity", cascade = {CascadeType.ALL})
    private UserKnowledgeEntity userKnowledgeEntity = null;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userEntity", cascade = {CascadeType.ALL})
    private UserCacheEntity userCacheEntity = null;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "query_user", joinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "query_id", nullable = false, updatable = false)})
    private Set<QueryEntity> queries = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceUserEntity")
    private Set<TweetMentionEntity> sourceTweetMentionEntities = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "targetUserEntity")
    private Set<TweetMentionEntity> targetTweetMentionEntities = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceUserEntity")
    private Set<TweetRetweetEntity> sourceTweetRetweetEntities = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "targetUserEntity")
    private Set<TweetRetweetEntity> targetTweetRetweetEntities = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceUserEntity")
    private Set<TweetQuoteEntity> sourceTweetQuoteEntities = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "targetUserEntity")
    private Set<TweetQuoteEntity> targetTweetQuoteEntities = new HashSet<>(0);

    public UserEntity(User twitterUser) {
        this.id = twitterUser.getId();
        this.screenName = twitterUser.getScreenName();
        this.name = twitterUser.getName();
        this.profileImageUrl = twitterUser.getProfileImageURL();
        this.location = twitterUser.getLocation();
        this.url = twitterUser.getURL();
        this.description = twitterUser.getDescription();
        this.created = Functions.convertToLocalDateTime(twitterUser.getCreatedAt());
        this.followersCount = twitterUser.getFollowersCount();
        this.friendsCount = twitterUser.getFriendsCount();
        this.statusesCount = twitterUser.getStatusesCount();
        this.timeZone = twitterUser.getTimeZone();
        this.lastUpdate = LocalDateTime.now();
        this.lang = twitterUser.getLang();
        this.utcOffset = twitterUser.getUtcOffset();
        this.verified = twitterUser.isVerified();
        this.inserted = LocalDateTime.now();
        // Check user status
        Status userStatus = twitterUser.getStatus();
        if (userStatus != null) {
            // Check status geolocation
            GeoLocation geoLocation = userStatus.getGeoLocation();
            if (geoLocation != null) {
                this.statusGeoLat = BigDecimal.valueOf(geoLocation.getLatitude());
                this.statusGeoLong = BigDecimal.valueOf(geoLocation.getLongitude());
            }
            // Check status place
            Place place = userStatus.getPlace();
            if (place != null) {
                this.statusCountry = place.getCountry();
                this.statusCountryCode = place.getCountryCode();
            }
        }
        this.suspended = false;
        this.urlExpanded = twitterUser.getURLEntity().getExpandedURL();
        this.contributor = twitterUser.isContributorsEnabled();
        this.translator = twitterUser.isTranslator();
        this.defaultProfile = twitterUser.isDefaultProfile();
        this.defaultProfileImage = twitterUser.isDefaultProfileImage();
        this.showAllInlineMedia = twitterUser.isShowAllInlineMedia();
        this.geoEnabled = twitterUser.isGeoEnabled();
        this.protectedUser = twitterUser.isProtected();
        this.email = twitterUser.getEmail();
        this.favoritesCount = twitterUser.getFavouritesCount();
        this.accessLevel = twitterUser.getAccessLevel();
        this.listedCount = twitterUser.getListedCount();
        this.originalProfileImageUrl = twitterUser.getOriginalProfileImageURL();
        this.originalProfileImageUrlHttps = twitterUser.getOriginalProfileImageURLHttps();
        this.biggerProfileImageUrl = twitterUser.getBiggerProfileImageURL();
        this.biggerProfileImageUrlHttps = twitterUser.getBiggerProfileImageURLHttps();
        this.profileTextColor = twitterUser.getProfileTextColor();
        this.profileLinkColor = twitterUser.getProfileLinkColor();
        this.profileSidebarFillColor = twitterUser.getProfileSidebarFillColor();
        this.profileSidebarBorderColor = twitterUser.getProfileSidebarBorderColor();
        this.profileBackgroundColor = twitterUser.getProfileBackgroundColor();
        this.parsed = false;
        // Check tweet withheld in countries
        String[] withheldInCountries = twitterUser.getWithheldInCountries();
        if (withheldInCountries != null)
            this.withheldInCountries = String.join(",", withheldInCountries);
    }
}
