package com.example.anbdapi.support.response

class AuthResponseCode {
    companion object {
        const val AUTH_01 = "Auth-001" // TokenExpiredException
        const val AUTH_02 = "Auth-002" // JwtException
        const val AUTH_03 = "Auth-004" // GoogleAuthException
    }
}
