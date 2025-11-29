package beeve.biz.fitness.entity

import beeve.biz.member.enum.Gender
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "rank_standard")
class RankStandard(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val rankStandardId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    val gender: Gender,   // 'M', 'F'

    @Column(nullable = false)
    val ageMin: Int,

    @Column(nullable = false)
    val ageMax: Int,

    @Column(nullable = false)
    val grade: Int,       // 1~3 (4는 3 이하의 수치)

    @Column(nullable = false)
    val pushUpReps: Int,

    @Column(nullable = false)
    val stepTestVo2max: BigDecimal,   // 심폐지구력

    @Column(nullable = false)
    val crossCrunchReps: Int,         // 근지구력

    @Column(nullable = false)
    val sitAndReach: BigDecimal,      // 유연성

    val reactionTime: BigDecimal? = null,  // 민첩성 (1,2 등급 기준만 있음)

    val flightTime: BigDecimal? = null,    // 순발력 (1,2 등급 기준만 있음)

    // null 기본 값이 있어야 자동으로 들어감
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
    val deletedYn: String = "N",
)