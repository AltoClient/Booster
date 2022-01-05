package me.jacobtread.mck.booster.data

import java.net.IDN
import java.util.*
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext

class ServerAddress private constructor(private val ipAddress: String, val port: Int) {
    val ip: String get() = IDN.toASCII(ipAddress)

    companion object {
        fun fromString(string: String): ServerAddress {
            var parts = string.split(":").toTypedArray()
            if (string.startsWith("[")) {
                val endIndex = string.indexOf("]")
                if (endIndex > 0) {
                    val s = string.substring(1, endIndex)
                    var s1 = string.substring(endIndex + 1).trim { it <= ' ' }
                    if (s1.startsWith(":") && s1.isNotEmpty()) {
                        s1 = s1.substring(1)
                        parts = arrayOf(s, s1)
                    } else {
                        parts = arrayOf(s)
                    }
                }
            }
            if (parts.size > 2) {
                parts = arrayOf(string)
            }
            var ip = parts[0]
            var port = if (parts.size > 1) parseIntWithDefault(parts[1]) else 25565
            if (port == 25565) {
                val parts2 = getDNSAddress(ip)
                ip = parts2[0]
                port = parseIntWithDefault(parts2[1])
            }
            return ServerAddress(ip, port)
        }

        private fun getDNSAddress(address: String): Array<String> {
            return try {
                Class.forName("com.sun.jndi.dns.DnsContextFactory")
                val context = Hashtable<String, String>()
                context["java.naming.factory.initial"] = "com.sun.jndi.dns.DnsContextFactory"
                context["java.naming.provider.url"] = "dns:"
                context["com.sun.jndi.dns.timeout.retries"] = "1"
                val dirContext: DirContext = InitialDirContext(context)
                val attributes = dirContext.getAttributes("_minecraft._tcp.$address", arrayOf("SRV"))
                val srv = attributes["srv"]
                    .get()
                    .toString()
                    .split(' ', limit = 4).toTypedArray()
                arrayOf(srv[3], srv[2])
            } catch (var6: Throwable) {
                arrayOf(address, 25565.toString())
            }
        }

        private fun parseIntWithDefault(intValue: String): Int {
            return try {
                intValue.trim { it <= ' ' }.toInt()
            } catch (var3: Exception) {
                25565
            }
        }
    }
}