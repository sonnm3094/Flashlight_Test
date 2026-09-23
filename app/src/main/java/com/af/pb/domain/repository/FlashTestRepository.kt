package com.af.pb.domain.repository

import android.content.Context

interface FlashTestRepository {
    fun startTest(context: Context, speedOnMs: Long, speedOffMs: Long)
    fun stopTest(context: Context)
}
