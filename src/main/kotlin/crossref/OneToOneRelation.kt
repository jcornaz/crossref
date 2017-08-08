package crossref

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class OneToOneRelation<Left : Any, Right : Any> {

    private val rightByLeft = HashMap<Left, Right>()
    private val leftByRight = HashMap<Right, Left>()

    @Synchronized
    fun set(left: Left?, right: Right?) {
        if (left != null) {
            rightByLeft[left]?.let {
                leftByRight -= it
            }

            if (right != null)
                rightByLeft[left] = right
            else
                rightByLeft -= left
        }

        if (right != null) {
            leftByRight[right]?.let {
                rightByLeft -= it
            }

            if (left != null)
                leftByRight[right] = left
            else
                leftByRight -= right
        }
    }

    fun getLeft(right: Right): Left? = synchronized(this) {
        leftByRight[right]
    }

    fun getRight(left: Left): Right? = synchronized(this) {
        rightByLeft[left]
    }

    fun right() = object : ReadWriteProperty<Left, Right?> {
        override fun getValue(thisRef: Left, property: KProperty<*>) = getRight(thisRef)
        override fun setValue(thisRef: Left, property: KProperty<*>, value: Right?) = set(thisRef, value)
    }

    fun left() = object : ReadWriteProperty<Right, Left?> {
        override fun getValue(thisRef: Right, property: KProperty<*>) = getLeft(thisRef)
        override fun setValue(thisRef: Right, property: KProperty<*>, value: Left?) = set(value, thisRef)
    }
}
