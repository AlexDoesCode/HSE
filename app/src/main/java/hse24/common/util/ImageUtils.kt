package hse24.common.util

class ImageUtils {

    companion object {

        private const val BASE_URL = "https://pic.hse24-dach.net/media/de/products/"
        private const val SMALL_IMAGE_SUFFIX = "pics480.jpg"
        private const val LARGE_IMAGE_SUFFIX = "pics480.jpg"

        @JvmStatic
        fun getImageUrl(imageUrl: String, size: ImageSize): String {

            val suffix = when (size) {
                ImageSize.SMALL -> SMALL_IMAGE_SUFFIX
                ImageSize.LARGE -> LARGE_IMAGE_SUFFIX
            }

            return "$BASE_URL${imageUrl}$suffix"
        }
    }
}

enum class ImageSize {
    SMALL, LARGE
}
