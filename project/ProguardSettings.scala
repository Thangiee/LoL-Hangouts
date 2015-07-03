
object ProguardSettings {
  val buildSettings = Seq(
    "-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt",
    "-dontwarn scala.collection.**",
    "-dontwarn scala.collection.mutable.**",
    "-dontwarn scala.**",
    "-dontwarn org.slf4j.**",
    "-dontwarn com.squareup.okhttp.**",
    "-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry",
    "-dontwarn javax.xml.bind.DatatypeConverter",
    "-dontwarn javax.annotation.**",
    "-dontwarn javax.inject.**",
    "-dontwarn sun.misc.Unsafe",
    "-dontwarn com.facebook.**",
    "-dontnote org.xbill.DNS.spi.DNSJavaNameServiceDescriptor",
    "-dontnote com.parse.**",
    "-dontwarn com.parse.**",
    "-dontwarn org.xbill.DNS.spi.DNSJavaNameServiceDescriptor",
    "-dontwarn org.xmlpull.v1.**",
    "-dontwarn javax.xml.namespace.QName",
    "-dontwarn org.jivesoftware.smack.**",
    "-dontwarn android.webkit.*",
    "-keep class * implements org.jivesoftware.smack.provider.IQProvider",
    "-keep class * implements org.jivesoftware.smack.provider.PacketExtensionProvider",
    "-keep class de.measite.smack.AndroidDebugger { *; }",
    "-keep class org.jivesoftware.smack.** { *; }",
    "-keep class org.jivesoftware.smackx.** { *; }",
    "-keep class com.thangiee.lolhangouts.**",
    "-keepclassmembers public class * extends com.skocken.efficientadapter.lib.viewholder.AbsViewHolder {public <init>(...);}",
    "-keepclassmembers class android.support.v7.widget.Toolbar {*;}",
    "-keepclassmembers class android.support.v7.widget.SearchView { *; }",
    "-keepclassmembers class ** {public void onEvent*(**);}"
  )
}