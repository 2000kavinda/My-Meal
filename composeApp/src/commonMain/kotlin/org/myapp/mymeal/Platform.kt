package org.myapp.mymeal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform