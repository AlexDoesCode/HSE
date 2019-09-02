package hse24.shop.categories.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate

@SuppressLint("ViewConstructor")
class SubcategoryLayout @JvmOverloads constructor(
    context: Context,
    clickListener: CategoriesAdapter.CategoryItemCallbackClickListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    ItemRenderer<CategoryItemViewModel> {

    private val subcategoryName by lazy {
        findViewById<TextView>(R.id.categories_subcategory_name)
    }

    private lateinit var viewModel: CategoryItemViewModel.SubcategoryViewModel

    init {
        selfInflate(R.layout.categories_subcategory_layout)
        doInRuntime {
            applyLayoutParams()

            setOnClickListener {
                clickListener.onSubcategoryClick(viewModel.id, viewModel.name)
            }
        }
    }

    override fun render(data: CategoryItemViewModel) {
        if (data is CategoryItemViewModel.SubcategoryViewModel) {
            with(data) {
                viewModel = data
                subcategoryName.text = name
            }
        }
    }
}
