package beeve.biz.member.repository

import beeve.biz.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByMemberIdAndDeletedYn(memberId: Long, deletedYn: String = "N"): Optional<Member>
}