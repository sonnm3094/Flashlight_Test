package com.af.pb.dialog

import android.content.Context
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import com.af.pb.R
import com.af.pb.base.dialog.BaseDialog
import com.af.pb.databinding.DialogRateBinding

class RateDialog(private val context: Context) : BaseDialog<DialogRateBinding>(context) {
    private var rating = 0f

    override fun provideViewBinding(): DialogRateBinding {
        return DialogRateBinding.inflate(layoutInflater)
    }

    override fun initViews() = with(viewBinding) {
        super.initViews()
        setCancelable(true)

        updateUiForRating(0)

        ratingBar.onRatingBarChangeListener = OnRatingBarChangeListener { _, ratingValue, _ ->
            this@RateDialog.rating = ratingValue
            updateUiForRating(ratingValue.toInt())
        }

        btnRate.setOnClickListener {
            dismiss()
            Toast.makeText(context, context.resources.getString(R.string.thanks_for_feedback), Toast.LENGTH_SHORT).show()
        }

        btnClose.setOnClickListener { dismiss() }
    }

    private fun updateUiForRating(stars: Int) = with(viewBinding) {
        when (stars) {
            1 -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_1)
                tvTitle.text = context.getString(R.string.rate_1_title)
                tvMessage.text = context.getString(R.string.rate_1_message)
            }
            2 -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_2)
                tvTitle.text = context.getString(R.string.rate_2_title)
                tvMessage.text = context.getString(R.string.rate_2_message)
            }
            3 -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_3)
                tvTitle.text = context.getString(R.string.rate_3_title)
                tvMessage.text = context.getString(R.string.rate_3_message)
            }
            4 -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_4)
                tvTitle.text = context.getString(R.string.rate_4_title)
                tvMessage.text = context.getString(R.string.rate_4_message)
            }
            5 -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_6)
                tvTitle.text = context.getString(R.string.rate_5_title)
                tvMessage.text = context.getString(R.string.rate_5_message)
            }
            else -> {
                imgHeaderRate.setImageResource(R.drawable.ic_rate_0)
                tvTitle.text = context.getString(R.string.rate_title, context.getString(R.string.app_name))
                tvMessage.text = context.getString(R.string.rate_message)
            }
        }
        btnRate.text = context.getString(R.string.submit)
    }
}
