package com.ruigoncalo.currencies.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ruigoncalo.currencies.R
import com.ruigoncalo.currencies.injection.ViewModelFactory
import com.ruigoncalo.currencies.model.RateRequest
import com.ruigoncalo.currencies.model.RatesViewEntity
import com.ruigoncalo.currencies.presentation.RatesViewModel
import com.ruigoncalo.social.presentation.ViewResource
import com.ruigoncalo.social.presentation.ViewResourceState
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import polanski.option.OptionUnsafe
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: RatesViewModel

    private val ratesAdapter by lazy {
        RatesAdapter(this) { viewEntity, inputValue ->
            viewModel.retrieveRates(RateRequest(viewEntity.currencyCode, inputValue))
            openSoftInput()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidInjection.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(RatesViewModel::class.java)

        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getRatesLiveData().observe(this,
                Observer<ViewResource<RatesViewEntity>> { resource ->
                    resource?.let {
                        when (it.state) {
                            ViewResourceState.SUCCESS -> {
                                if (it.data.isSome) {
                                    val viewEntity = OptionUnsafe.getUnsafe(it.data)
                                    updateRates(viewEntity)
                                }
                            }

                            ViewResourceState.ERROR -> {
                                if (it.message.isSome) {
                                    showError(OptionUnsafe.getUnsafe(it.message))
                                }
                            }

                            ViewResourceState.LOADING -> {
                                // empty
                            }
                        }
                    }
                })
    }

    private fun setupViews() {
        with(currenciesRecyclerView) {
            adapter = ratesAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }

    private fun updateRates(rates: RatesViewEntity) {
        ratesAdapter.update(rates.rates)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun openSoftInput() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
                window.decorView.windowToken,
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
