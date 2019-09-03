package hse24.common.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

class DeviceUtils {

    companion object {
        private var screenWidth = 0

        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            if (screenWidth == 0) {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val screenSize = Point()
                display.getSize(screenSize)
                screenWidth = screenSize.x
            }
            return screenWidth
        }
    }
}
