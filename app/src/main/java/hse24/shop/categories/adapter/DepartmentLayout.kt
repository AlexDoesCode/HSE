package hse24.shop.categories.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate

@SuppressLint("ViewConstructor")
class DepartmentLayout @JvmOverloads constructor(
    context: Context,
    clickListener: CategoriesAdapter.CategoryItemCallbackClickListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ItemRenderer<DepartmentViewModel> {

    private val departmentName by lazy {
        findViewById<TextView>(R.id.categories_department_name)
    }

    private var departmentId: Int? = null

    init {
        selfInflate(R.layout.categories_department_layout)
        doInRuntime {
            applyLayoutParams()
            setBackgroundResource(R.drawable.shape_filled_white_border)

            setOnClickListener {
                departmentId?.let {
                    clickListener.onDepartmentClick(it)
                }
            }
        }
    }

    override fun render(data: DepartmentViewModel) {
        with(data) {
            departmentId = id
            departmentName.text = name
        }
    }
}
