package net.yested.bootstrap

import net.yested.ParentComponent
import net.yested.HTMLParentComponent
import net.yested.ButtonType
import net.yested.Button

enum class ButtonLook(val code:String) {
    DEFAULT: ButtonLook("default")
    PRIMARY: ButtonLook("primary")
    SUCCESS: ButtonLook("success")
    INFO: ButtonLook("info")
    WARNING: ButtonLook("warning")
    DANGER: ButtonLook("danger")
    LINK: ButtonLook("link")
}

enum class ButtonSize(val code:String) {
    DEFAULT: ButtonSize("default")
    LARGE: ButtonSize("lg")
    SMALL: ButtonSize("sm")
    EXTRA_SMALL: ButtonSize("xs")
}

class BtsButton(type: ButtonType = ButtonType.BUTTON,
             label:ParentComponent.()-> Unit,
             look:ButtonLook = ButtonLook.DEFAULT,
             size:ButtonSize = ButtonSize.DEFAULT,
             block:Boolean = false,
             onclick:() -> Unit ) :  ParentComponent("button") {

    var onclick: Function0<Unit>
        get() = element.onclick
        set(f) {
            element.onclick = f;
        }

    var disabled:Boolean
        get() = element.getAttribute("disabled") == "disabled"
        set(value) {
            element.setAttribute("disabled", if (value) "disabled" else "")
        }

    {
        setAttribute("class", "btn btn-${look.code} btn-${size.code} ${if (block) "btn-block" else ""}")
        setAttribute("type", type.code)
        this.label()
        this.onclick = onclick
    }

}

fun HTMLParentComponent.btsButton(type: ButtonType = ButtonType.BUTTON,
                                  label:ParentComponent.()-> Unit,
                                  look:ButtonLook = ButtonLook.DEFAULT,
                                  size:ButtonSize = ButtonSize.DEFAULT,
                                  block:Boolean = false,
                                  onclick:() -> Unit):BtsButton {
    val btn = BtsButton(type = type, label = label, look = look, size = size, block = block, onclick = onclick)
    this.add(btn)
    return btn
}


