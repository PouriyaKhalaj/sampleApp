package ir.co.common.utils

import android.content.Intent
import android.net.Uri

/**
 * Created by pooriettaw on 18,October,2020
 */
object IntentManager {
    fun sendEmail(mailTo: String) = Intent.createChooser(
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$mailTo")
        }, "Send Email"
    )

}