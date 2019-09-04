package hse24.shop.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hse24.challenge.R
import hse24.common.android.BaseFragment
import hse24.common.extension.visible
import hse24.shop.catalog.CatalogFragment
import hse24.shop.categories.adapter.CategoriesAdapter
import hse24.shop.categories.adapter.CategoryLayout
import hse24.shop.categories.adapter.DepartmentsAdapter
import hse24.shop.categories.mvi.CategoriesIntention
import hse24.shop.categories.mvi.CategoriesPresenter
import hse24.shop.categories.mvi.CategoriesState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = CategoriesFragment()
    }

    private val intentionsSubject = PublishSubject.create<CategoriesIntention>()

    private val adapterClickListener =
        object : CategoriesAdapter.CategoryItemCallbackClickListener {
            override fun onDepartmentClick(id: Int) {
                Timber.d("Department $id clicked")
                intentionsSubject.onNext(CategoriesIntention.GetDepartmentCategories(id))
            }

            override fun onCategoryClick(
                id: Int,
                name: String,
                isExpanded: Boolean,
                hasSubcategories: Boolean
            ) {
                Timber.d("Category $id clicked")

                if (hasSubcategories) {
                    categoriesLayoutsMap[id]?.toggleExpanded()
                    prevSelectedCategoryId = when (isExpanded) {
                        false -> {
                            intentionsSubject.onNext(CategoriesIntention.GetSubcategories(id))
                            prevSelectedCategoryId?.let {
                                categoriesLayoutsMap[it]?.toggleExpanded()
                            }
                            id
                        }
                        true -> {
                            intentionsSubject.onNext(CategoriesIntention.ResetSubcategories)
                            null
                        }
                    }
                } else {
                    replaceFragment(
                        CatalogFragment.newInstance(id, name),
                        R.id.shopping_activity_root,
                        false
                    )
                }
            }

            override fun onCategoryRendered(id: Int, view: View) {
                categoriesLayoutsMap[id] = view as CategoryLayout
            }

            override fun onSubcategoryClick(id: Int, name: String) {
                Timber.d("Subcategory $id clicked")
                replaceFragment(
                    CatalogFragment.newInstance(id, name),
                    R.id.shopping_activity_root,
                    false
                )
            }
        }

    private var prevSelectedCategoryId: Int? = null
    private val categoriesLayoutsMap = mutableMapOf<Int, CategoryLayout>()

    private var adapterDisposable: Disposable? = null
    private var departmentsAdapter = DepartmentsAdapter(adapterClickListener)
    private var categoriesAdapter = CategoriesAdapter(adapterClickListener)

    private lateinit var departmentsRecycler: RecyclerView
    private lateinit var categoriesRecycler: RecyclerView

    @Inject
    lateinit var presenter: CategoriesPresenter

    private lateinit var disposables: CompositeDisposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departmentsRecycler = view.findViewById(R.id.categories_fragment_departments_recycler)
        categoriesRecycler = view.findViewById(R.id.categories_fragment_categories_recycler)

        departmentsRecycler.apply {
            adapter = this@CategoriesFragment.departmentsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        categoriesRecycler.apply {
            adapter = this@CategoriesFragment.categoriesAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    override fun onStart() {
        super.onStart()

        disposables = CompositeDisposable(
            presenter.states()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::render),
            presenter.processIntentions(
                intentions()
            )
        )
    }

    override fun onStop() {
        disposables.dispose()
        super.onStop()
    }

    override fun onScopeFinished() {
        presenter.destroy()
        super.onScopeFinished()
    }

    @UiThread
    private fun render(state: CategoriesState) {
        Timber.d("State is $state")
        with(state) {
            departments?.let {
                if (categories == null) {
                    adapterDisposable?.dispose()
                    adapterDisposable = departmentsAdapter.setItems(it)
                        .subscribe()
                }
            }

            categories?.let {
                departmentsRecycler.visible = false
                categoriesRecycler.visible = true

                val allCategories = when (subCategories.isNullOrEmpty()) {
                    true -> it
                    false -> it + subCategories
                }

                adapterDisposable?.dispose()
                adapterDisposable = categoriesAdapter.setItems(allCategories)
                    .subscribe()
            }
        }
    }

    private fun intentions() = Observable.merge(
        listOf(
            Observable.just(CategoriesIntention.Init),
            intentionsSubject
        )
    )
}
