package com.example.foody.ui.fragments.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.R
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.util.NetworkResult
import com.example.foody.util.observeOnce
import com.example.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.view.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {


    private lateinit var mView: View
    private val recipeAdapter by lazy { RecipesAdapter() }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recipes, container, false)
        setupRecyclerView()
        readDatabase()
        return mView
    }

    private fun setupRecyclerView() {

        mView.recyclerView.adapter = recipeAdapter
        mView.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called")
                    recipeAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    private fun requestApiData() {
        Log.d("RecipesFragment", "requestApiData called")

        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipeAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache(response.message)
                }
                is NetworkResult.Loading -> {
                    mView.error_image_view.visibility = View.GONE
                    mView.error_text_view.visibility = View.GONE
                    showShimmerEffect()
                }
            }
        })
    }

    private fun loadDataFromCache(message: String?) {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    recipeAdapter.setData(database[0].foodRecipe)
                } else {
                    mView.error_image_view.visibility = View.VISIBLE
                    mView.error_text_view.text = message
                }
            })
        }
    }

    private fun showShimmerEffect() {
        mView.recyclerView.showShimmer()
    }


    private fun hideShimmerEffect() {
        mView.recyclerView.hideShimmer()
    }
}