package kr.sul.guild.guild

enum class RankInGuild(
    val canKick: Boolean,
    val canInvite: Boolean,
    val displayName: String
): Comparable<RankInGuild> {
    ADAPTER(false, false, "§a어댑터"),
    SCOUTER(false, true, "§e스카우터"),
    DOMINION(true, true, "§c도미니언"),
    OWNER(true, true, "§4오피니언");
    // Compare rank with default compareTo method that is based on ordinal(첫번째 enum: 0 다음 것: 이전+1)

    override fun toString(): String {
        return this.displayName
    }


    companion object {
        fun getDefaultRank(): RankInGuild {
            return ADAPTER
        }
    }
}