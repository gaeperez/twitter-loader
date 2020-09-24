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
@Table(name = "tweet_knowledge")
public class TweetKnowledgeEntity implements Serializable, Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "polarity", length = 45)
    private String polarity;
    @Column(name = "subjectivity", length = 45)
    private String subjectivity;
    @Column(name = "curated")
    private Boolean curated;
    @Column(name = "parsed", nullable = false)
    private boolean parsed;
    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens = 0;
    @Column(name = "self_repeated_count", nullable = false)
    private Integer selfRepeatedCount = 0;
    @Column(name = "other_repeated_count", nullable = false)
    private Integer otherRepeatedCount = 0;
    @Column(name = "source_chain", nullable = false)
    private boolean isSourceChain;
    @Column(name = "source_tweet_id", nullable = false)
    private long sourceTweetId;
    @Column(name = "thematic", length = 45)
    private String thematic;
    @Column(name = "checked_duplicity", nullable = false)
    private boolean checkedDuplicity;
    @Column(name = "short_message", nullable = false)
    private boolean shortMessage;
    @Column(name = "text_annotated_replace", nullable = false, length = 240)
    private String textAnnotatedReplace;
    @Column(name = "annotated_html", nullable = false, length = 240)
    private String annotatedHtml;
    @Column(name = "sentiment_compound")
    private double sentimentCompound;
    @Column(name = "word_count")
    private Integer wordCount = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private TweetEntity tweetEntity;

    public TweetKnowledgeEntity clone() {
        TweetKnowledgeEntity obj = new TweetKnowledgeEntity();
        obj.setPolarity(this.polarity);
        obj.setThematic(this.thematic);
        obj.setParsed(this.parsed);
        obj.setTotalTokens(this.totalTokens);
        obj.setSubjectivity(this.subjectivity);
        obj.setCurated(this.curated);
        return obj;
    }
}