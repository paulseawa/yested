package net.yested.layout

import jquery.jq
import jquery.ui.draggable
import net.yested.*
import net.yested.utils.css
import net.yested.utils.jqStatic
import net.yested.utils.on

public enum class ScrollBarOrientation(val directionProperty:String, val nonDirectionProperty:String, val axis:String, val cssPosProperty:String) {
    VERTICAL: ScrollBarOrientation(directionProperty = "height", nonDirectionProperty = "width", axis = "y", cssPosProperty = "top")
    HORIZONTAL: ScrollBarOrientation(directionProperty = "width", nonDirectionProperty = "height", axis = "x", cssPosProperty = "left")
}

/**
 * https://github.com/cowboy/jquery-throttle-debounce
 */
public class ScrollBar(
        val orientation: ScrollBarOrientation,
        val size: String,
        var numberOfItems: Int,
        var visibleItems: Int,
        val className:String? = null,
        val positionHandler:(Int) -> Unit) : Component {

    override val element = createElement("div")

    private val handle = Div()

    private var currentPosition:Int = 0

    private var trackerDimension:Int = 0

    private var handleDimension:Int = 0

    public fun setTrackerVisible(visibleTracker:Boolean) {
        if (visibleTracker) {
            jq(handle.element).css("visibility", "visible")
        } else {
            jq(handle.element).css("visibility", "hidden")
        }
    }

    init {

        element.appendChild(handle.element)
        element.setAttribute("style", "${orientation.directionProperty}: ${size};")
        element.setAttribute("position", "absolute")

        if (className != null) {
            handle.clazz = className
        } else {
            handle.style = "${orientation.nonDirectionProperty}: 15px; background-color: #5c92e7; cursor: move; position: relative; ${orientation.cssPosProperty}: 0"
        }

        jq(handle.element).draggable(
                json(
                        Pair("axis", orientation.axis),
                        Pair("containment", "parent"),
                        Pair("drag", {
                            val top = parseInt(jq(handle.element).css(orientation.cssPosProperty))
                            updatePosition(top)
                        })
                ))

        jq(element).on("mousewheel") { event ->
            val e = event.originalEvent
            val delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail) as Int));
            event.preventDefault()
            if (delta < 0) {
                if (currentPosition < numberOfItems) {
                    currentPosition += delta*-1
                    changePositionOfHandler()
                    positionHandler(currentPosition)
                }
            } else {
                if (currentPosition > 0) {
                    currentPosition += delta*-1
                    changePositionOfHandler()
                    positionHandler(currentPosition)
                }
            }
        }

        var touchStartMouse:Int = 0
        var touchStartTop:Int = 0

        jq(handle.element).on("touchstart", { event->
            touchStartTop = parseInt(jq(handle.element).css(orientation.cssPosProperty))
            touchStartMouse = getMouseTouchPosition(event)
            event.preventDefault()
        })

        jq(handle.element).on("touchmove", { event ->
            event.preventDefault()
            val newMousePos = getMouseTouchPosition(event)
            val diff: Int = newMousePos - touchStartMouse
            val newPosition = Math.max(0, Math.min(touchStartTop + diff, trackerDimension - handleDimension))
            jq(handle.element).css(orientation.cssPosProperty, "${newPosition}px")
            updatePosition(newPosition)
        })

        handle.element.whenAddedToDom { recalculate() }

    }

    private fun getMouseTouchPosition(event: dynamic) = if (orientation == ScrollBarOrientation.VERTICAL) {
            event.originalEvent.touches[0].clientY
        } else {
            event.originalEvent.touches[0].clientX
        }

    private fun updatePosition(top: Int) {
        currentPosition = (numberOfItems.toDouble() * top / (trackerDimension - handleDimension)).toInt()
        positionHandler(currentPosition)
    }

    public var position:Int
        get() = currentPosition
        set(value:Int) {
            currentPosition = value
            changePositionOfHandler()
        }

    private fun changePositionOfHandler() {
        val position = (currentPosition.toDouble() * (trackerDimension - handleDimension) / numberOfItems).toInt()
        jq(handle.element).css(orientation.cssPosProperty, "${position}px")
    }

    public fun setup(numberOfItems: Int, visibleItems: Int) {
        this.numberOfItems = numberOfItems
        this.visibleItems = visibleItems
        position = 0
        element.whenAddedToDom { recalculate() }
    }

    private fun recalculate() {
        trackerDimension = trackerDimension()
        handleDimension = handleDimension().toInt()
        jq(handle.element).css(orientation.directionProperty, "${handleDimension}")
    }

    private fun trackerDimension() =
            if (orientation == ScrollBarOrientation.VERTICAL) {
                jq(element).height().toInt()
            } else {
                jq(element).width().toInt()
            }

    private fun handleDimension() = Math.max(30, (trackerDimension * visibleItems/numberOfItems).toInt())

}