package ir.co.common.dto

enum class Shape {

    UNDEFINE {
        override fun getValue(): Int = 0
    },
    CIRCLE {
        override fun getValue(): Int = 1
    },
    RECTANGLE {
        override fun getValue(): Int = 2
    },
    RADIUS {
        override fun getValue(): Int = 3
    },
    SQUARE {
        override fun getValue(): Int = 4
    };

    abstract fun getValue(): Int

    companion object {
        fun convert(value: Int): Shape {
            for (item in values()) {
                if (item.getValue() == value) {
                    return item
                }
            }
            return UNDEFINE
        }
    }
}
