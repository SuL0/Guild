package kr.sul.guild.messagequeue

import java.io.Serializable

/**
 * @param nbtKeyForIdentification NBT key 를 통해 아이템 일치여부 확인
 */
data class ItemStackSimplified(val nbtKeyForIdentification: String, val amount: Int): Serializable {

}