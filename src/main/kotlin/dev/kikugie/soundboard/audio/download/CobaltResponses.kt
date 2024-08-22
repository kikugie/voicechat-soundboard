package dev.kikugie.soundboard.audio.download

sealed interface CobaltResponse

class StreamResponse(val url: String) : CobaltResponse
class ErrorResponse(val message: String) : CobaltResponse