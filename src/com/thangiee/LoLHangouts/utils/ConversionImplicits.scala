package com.thangiee.LoLHangouts.utils

import java.io.InputStream

import android.content.Context
import android.graphics.drawable.Drawable

trait ConversionImplicits extends org.scaloid.common.InterfaceImplicits{

  implicit class ResourceTo(id: Int) {
    def r2String(implicit  ctx: Context): String = ctx.getResources.getString(id)
    def r2StringArray(implicit  ctx: Context): Array[String] = ctx.getResources.getStringArray(id)
    def r2Drawable(implicit  ctx: Context): Drawable = ctx.getResources.getDrawable(id)
    def r2Color(implicit  ctx: Context): Int = ctx.getResources.getColor(id)
    def r2Raw(implicit  ctx: Context): InputStream = ctx.getResources.openRawResource(id)
  }
}
