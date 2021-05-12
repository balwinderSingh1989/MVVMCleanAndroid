package com.sample.core.domain.remote.exception

import java.io.IOException

class HTTPNotFoundException constructor(message: String) : IOException(message) {
    constructor() : this("No data found")
}

class HTTPBadRequest constructor(message: String) : IOException(message) {
    constructor() : this("Bad Request")
}

class ServerNotAvailableException(message: String?) : IOException(message)

class AuthorizationException(message: String) : IOException(message)

class NetworkException(throwable: Throwable) :
    IOException(throwable.message, throwable)

class ProxyException(message: String?) : IOException(message)

class NoContentException(message: String?) : IOException(message)

class SessionExpireException constructor(message: String) : IOException(message) {
    constructor() : this("Bearer token can not be null")
}
