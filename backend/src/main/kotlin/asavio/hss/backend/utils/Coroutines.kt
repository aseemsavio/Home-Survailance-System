package asavio.hss.backend.utils

import kotlinx.coroutines.CoroutineName

/**
 * Returns a [CoroutineName] object with the provided [String].
 */
val String.coroutineName: CoroutineName get() = CoroutineName(this)

