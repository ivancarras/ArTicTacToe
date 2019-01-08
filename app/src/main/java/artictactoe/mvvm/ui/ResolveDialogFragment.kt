package artictactoe.mvvm.ui

/**
 * Created by Ivan Carrasco on 22/12/2018.
 */
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams

/** A DialogFragment for the Resolve Dialog Box.  */
class ResolveDialogFragment : DialogFragment() {

    interface OkListener {
        fun onOkPressed(dialogValue: String)
    }
    private var okListener: OkListener? = null

    private val shortCodeField: EditText by lazy{
        EditText(context)
    }

    /**
     * Creates a simple layout for the dialog. This contains a single user-editable text field whose
     * input type is retricted to numbers only, for simplicity.
     */
    private val dialogLayout: LinearLayout
        get() {
            val context = context
            val layout = LinearLayout(context)
            shortCodeField.inputType = InputType.TYPE_CLASS_NUMBER
            shortCodeField.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            shortCodeField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
            layout.addView(shortCodeField)
            layout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            return layout
        }

    /** Sets a listener that is invoked when the OK button on this dialog is pressed.  */
    internal fun setOkListener(okListener: OkListener) {
        this.okListener = okListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder
            .setView(dialogLayout)
            .setTitle("Resolve Anchor")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                val shortCodeText = shortCodeField.text
                if (shortCodeText.isNotEmpty()) {
                    // Invoke the callback with the current checked item.
                    okListener?.onOkPressed(shortCodeText.toString())
                }
            }
            .setNegativeButton("Cancel") { dialog, which -> }
        return builder.create()
    }
}