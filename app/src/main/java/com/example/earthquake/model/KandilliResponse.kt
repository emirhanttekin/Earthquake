package com.example.earthquake.model

data class KandilliResponse(
    val status: Boolean,
    val metadata: Metadata,
    val result: List<KandilliItem>
)
