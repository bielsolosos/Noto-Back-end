package space.bielsolososdev.noto.domain.media.model;

import jakarta.persistence.*;
import lombok.*;
import space.bielsolososdev.noto.domain.users.model.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_r2")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaR2 {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

}