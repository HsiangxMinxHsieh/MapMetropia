package com.timmy.mapmetropia.listener

interface BackListener {
    /**
     * @return true:已處理 false:略過
     */
    fun onBackPressed(): Boolean
}