package com.github.aakumykov.file_lister_navigator_selector.file_lister

import com.github.aakumykov.file_lister_navigator_selector.R

enum class SimpleSortingMode {
    NAME {
        override val sortingName: Int = R.string.sorting_mode_name
    },
    SIZE {
        override val sortingName: Int = R.string.sorting_mode_size
    },
    C_TIME {
        override val sortingName: Int = R.string.sorting_mode_c_time
    },
    M_TIME {
        override val sortingName: Int = R.string.sorting_mode_m_time
    };

    abstract val sortingName: Int
}

/*enum class SimpleSortingMode {
    NAME {
        override val directName: Int = R.string.sorting_mode_name_direct
        override val reverseName: Int = R.string.sorting_mode_name_reverse
    },

    C_TIME {
        override val directName: Int = R.string.sorting_mode_c_time_direct
        override val reverseName: Int = R.string.sorting_mode_c_time_reverse
   },

    M_TIME {
        override val directName: Int = R.string.sorting_mode_m_time_direct
        override val reverseName: Int = R.string.sorting_mode_m_time_reverse
   },

    SIZE {
        override val directName: Int = R.string.sorting_mode_size_direct
        override val reverseName: Int = R.string.sorting_mode_size_reverse
         },
    ;

    *//*@StringRes abstract fun name(): Int
    @StringRes abstract fun reverseModeName(): Int*//*

    abstract val directName: Int
    abstract val reverseName: Int
}*/


/*enum class SimpleSortingMode(
    @StringRes private val directModeName: Int,
    @StringRes private val reverseModeName: Int
) {
    NAME(R.string.sorting_mode_name_direct, R.string.sorting_mode_name_reverse){},

    C_TIME(R.string.sorting_mode_c_time_direct, R.string.sorting_mode_c_time_reverse){},

    M_TIME(R.string.sorting_mode_m_time_direct, R.string.sorting_mode_m_time_reverse){},

    SIZE(R.string.sorting_mode_size_direct, R.string.sorting_mode_size_reverse){};

    abstract fun name(): String
    abstract fun reverseModeName(): String
}*/