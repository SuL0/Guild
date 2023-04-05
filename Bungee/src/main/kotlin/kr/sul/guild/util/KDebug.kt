package kr.sul.guild.util


// Thread.currentThread().getStackTrace()[2].getClassName() 로 대체 가능한 듯?


//object KDebug {
//    fun getCallerClassName(): String? {
//        val stElements = Thread.currentThread().stackTrace
//        for (i in 1 until stElements.size) {
//            val ste = stElements[i]
//            if (ste.className != KDebug::class.java.name && ste.className.indexOf("java.lang.Thread") != 0) {
//                return ste.className
//            }
//        }
//        return null
//    }
//}