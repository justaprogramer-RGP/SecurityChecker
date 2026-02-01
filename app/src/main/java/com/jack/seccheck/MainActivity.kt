/*
 * SecurityChecker
 * Copyright (C) 2026 Jack
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jack.seccheck

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import java.io.File

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val report = StringBuilder()
        var riskScore = 0

        // Android version
        report.append("Android Version: ${Build.VERSION.RELEASE}\n")
        report.append("SDK Level: ${Build.VERSION.SDK_INT}\n")
        if (Build.VERSION.SDK_INT < 29) {
            report.append("WARNING: Old Android version detected.\n")
            report.append("This device may contain known vulnerabilities.\n\n")
            riskScore += 20
        } else {
            report.append("OK: Android version is relatively modern.\n\n")
        }

        // Security patch
        val patchLevel = Build.VERSION.SECURITY_PATCH
        report.append("Security Patch Level: $patchLevel\n\n")
        if (patchLevel < "2024-01-01") riskScore += 20

        // USB Debugging
        val adbEnabled = Settings.Global.getInt(
            contentResolver,
            Settings.Global.ADB_ENABLED,
            0
        )
        if (adbEnabled == 1) {
            report.append("WARNING: USB Debugging is ENABLED.\n\n")
            riskScore += 30
        } else {
            report.append("OK: USB Debugging is disabled.\n\n")
        }

        // Root detection
        val rootDetected =
            File("/system/bin/su").exists() ||
            File("/system/xbin/su").exists() ||
            File("/sbin/su").exists()
        if (rootDetected) {
            report.append("WARNING: Possible ROOT detected.\n\n")
            riskScore += 30
        } else {
            report.append("OK: No obvious root binaries found.\n\n")
        }

        // Risk Score
        report.append("Risk Score: $riskScore / 100\n")

        // عرض التقرير
        val textView = TextView(this)
        textView.text = report.toString()
        textView.textSize = 14f
        textView.setPadding(24, 24, 24, 24)

        // تغيير لون النص حسب مستوى الخطورة
        textView.setTextColor(
            when {
                riskScore >= 70 -> 0xFFFF0000.toInt() // أحمر
                riskScore >= 40 -> 0xFFFFA500.toInt() // برتقالي
                else -> 0xFF00AA00.toInt() // أخضر
            }
        )

        setContentView(textView)
    }
}
