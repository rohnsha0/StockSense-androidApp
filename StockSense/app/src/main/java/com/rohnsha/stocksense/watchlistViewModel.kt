package com.rohnsha.stocksense

import watchlistDC

object watchlistViewModel {
    object SharedDataSource {
        private var watchlistAdapter: watchlistAdapter? = null

        fun setWatchlistAdapter(adapter: watchlistAdapter) {
            watchlistAdapter = adapter
        }

        fun addItem(item: watchlistDC) {
            watchlistAdapter?.apply {
                watchlists = watchlists + item
                notifyDataSetChanged()
            }
        }

        fun getWatchlistAdapter(): watchlistAdapter? {
            return watchlistAdapter
        }
    }
}