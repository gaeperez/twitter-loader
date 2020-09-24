package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_knowledge", uniqueConstraints = @UniqueConstraint(columnNames = {"id", "user_id"}))
public class UserKnowledgeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "sex", length = 1)
    private String sex;
    @Column(name = "years")
    private Integer years;
    @Column(name = "classification", length = 254)
    private String classification;
    @Column(name = "role", length = 254)
    private String role;
    @Column(name = "city", length = 254)
    private String city;
    @Column(name = "country_code", length = 45)
    private String countryCode;
    @Column(name = "country", length = 254)
    private String country;
    @Column(name = "continent", length = 254)
    private String continent;
    @Column(name = "is_influencer")
    private boolean isInfluencer;
    @Column(name = "image_blob")
    private byte[] imageBlob;
    @Column(name = "curated")
    private boolean curated;
    @Column(name = "parsed", nullable = false)
    private boolean parsed;
    @Column(name = "geo_lat", precision = 10, scale = 5)
    private BigDecimal geoLat;
    @Column(name = "geo_long", precision = 10, scale = 5)
    private BigDecimal geoLong;
    @Column(name = "detected_gender_by", length = 254)
    private String detectedGenderBy;
    @Column(name = "influencer_measure")
    private Double influencerMeasure = 0.0d;
    @Column(name = "ftf_ratio")
    private Double ftfRatio = 0.0d;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
}
