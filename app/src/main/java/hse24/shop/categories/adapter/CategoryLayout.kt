package hse24.shop.categories.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import com.hse24.challenge.R
import hse24.common.android.adapter.ItemRenderer
import hse24.common.extension.applyLayoutParams
import hse24.common.extension.doInRuntime
import hse24.common.extension.selfInflate
import hse24.common.extension.visible

@SuppressLint("ViewConstructor")
class CategoryLayout @JvmOverloads constructor(
    context: Context,
    private val clickListener: CategoriesAdapter.CategoryItemCallbackClickListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    ItemRenderer<CategoryItemViewModel> {

    private val categoryName by lazy {
        findViewById<TextView>(R.id.categories_category_name)
    }

    private val openedDropdown by lazy {
        findViewById<ImageView>(R.id.categories_category_collapse_up)
    }

    private val closedDropdown by lazy {
        findViewById<ImageView>(R.id.categories_category_collapse_down)
    }

    private lateinit var viewModel: CategoryItemViewModel.CategoryViewModel

    init {
        selfInflate(R.layout.categories_category_layout)
        doInRuntime {
            applyLayoutParams()
            setBackgroundColor(getColor(context, R.color.light_grey))
            setOnClickListener {
                clickListener.onCategoryClick(
                    viewModel.id,
                    viewModel.name,
                    viewModel.isExpanded,
                    viewModel.hasSubcategories
                )
            }
        }
    }

    override fun render(data: CategoryItemViewModel) {
        if (data is CategoryItemViewModel.CategoryViewModel) {
            with(data) {
                viewModel = this
                categoryName.text = name

                when (hasSubcategories) {
                    true -> {
                        openedDropdown.visible = false
                        closedDropdown.visible = true
                    }
                    false -> {
                        openedDropdown.visible = hasSubcategories
                        closedDropdown.visible = hasSubcategories
                    }
                }

                clickListener.onCategoryRendered(id, this@CategoryLayout)
            }
        }
    }

    fun toggleExpanded() {
        AnimationUtils.loadAnimation(
            context,
            when (viewModel.isExpanded) {
                true -> R.anim.dropdown_rotation_ccw
                false -> R.anim.dropdown_rotation_cw
            }
        )
            .apply {
                openedDropdown.visible = viewModel.isExpanded.not()
                closedDropdown.visible = viewModel.isExpanded
                when (viewModel.isExpanded) {
                    false -> openedDropdown.startAnimation(this)
                    true -> closedDropdown.startAnimation(this)
                }

                this.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {
                        //no-op
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        openedDropdown.visible = viewModel.isExpanded
                        closedDropdown.visible = viewModel.isExpanded.not()
                    }

                    override fun onAnimationStart(p0: Animation?) {
                        //no-op
                    }
                })
            }
    }
}
