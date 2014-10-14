package com.thangiee.LoLHangouts.views

import android.app.AlertDialog
import android.content.{Context, DialogInterface}
import android.view.{View, Window}
import android.widget.TextView
import com.thangiee.LoLHangouts.R
import org.scaloid.common._

case class ConfirmDialog(ctx: Context) extends AlertDialog(ctx) {
  override def show(): Unit = {
    this.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    super.show()

    // set the buttons background and text color
    List(DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE).map(button ⇒ {
      this.getButton(button).setBackgroundColor(ctx.getResources.getColor(R.color.my_tran_dark_blue2))
      this.getButton(button).setTextColor(ctx.getResources.getColor(R.color.my_orange))
    })
  }
}

object ConfirmDialog {
  def apply(msg: String, code2run: => Unit, btnTitle: String = "Ok")(implicit ctx: Context): ConfirmDialog = {
    val view = View.inflate(ctx, R.layout.confirm_dialog_view, null)
    view.find[TextView](R.id.confirm_dialog_title).setText(msg)

    val dialog = ConfirmDialog(ctx)
    dialog.setView(view)
    dialog.setButton(DialogInterface.BUTTON_POSITIVE, btnTitle, (d: DialogInterface, i: Int) => { code2run; d.dismiss()} )
    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", (d: DialogInterface, i: Int) => d.dismiss())
    dialog
  }
}
