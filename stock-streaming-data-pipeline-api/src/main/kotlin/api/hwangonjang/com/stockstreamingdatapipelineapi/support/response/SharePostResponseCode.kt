package com.example.anbdapi.support.response

class SharePostResponseCode {
    companion object {
        const val SHAREPOST_01 = "SharePost-001" // SharePostNotFoundException
        const val SHAREPOST_02 = "SharePost-002" // SharePostLikeBadRequestException
        const val SHAREPOST_03 = "SharePost-003" // SharePostGeminiException
        const val SHAREPOST_04 = "SharePost-004" // BiddingNotFoundException
        const val SHAREPOST_05 = "SharePost-005" // BiddingBadRequestException
    }
}
