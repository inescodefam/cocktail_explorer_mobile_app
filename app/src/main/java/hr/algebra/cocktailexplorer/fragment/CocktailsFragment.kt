package hr.algebra.cocktailexplorer.fragment

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import hr.algebra.cocktailexplorer.COCKTAIL_PROVIDER_CONTENT_URI
import hr.algebra.cocktailexplorer.R
import hr.algebra.cocktailexplorer.adapter.CocktailAdapter
import hr.algebra.cocktailexplorer.adapter.SwipeToDeleteCallback
import hr.algebra.cocktailexplorer.data.local.database.CocktailsTable
import hr.algebra.cocktailexplorer.databinding.FragmentCocktailsBinding
import hr.algebra.cocktailexplorer.framework.cursorToCocktailList
import hr.algebra.cocktailexplorer.framework.deleteCocktailFromDatabase
import hr.algebra.cocktailexplorer.framework.extractCategories
import hr.algebra.cocktailexplorer.framework.filterByCategory
import hr.algebra.cocktailexplorer.framework.filterByName
import hr.algebra.cocktailexplorer.models.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOADER_ID = 1

class CocktailsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var _binding: FragmentCocktailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CocktailAdapter
    
    private var allCocktails: List<Cocktail> = emptyList()
    private var selectedCategory: String? = null
    private var categories: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCocktailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupCategoryFilter()
        showLoading(true)
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            applyFilters(query, selectedCategory)
        }
    }


    private fun setupCategoryFilter() {
        binding.spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
            binding.btnClearCategory.visibility = View.VISIBLE
            applyFilters(binding.etSearch.text.toString().trim(), selectedCategory)
        }

        binding.btnClearCategory.setOnClickListener {
            clearCategoryFilter()
        }
    }

    private fun populateCategoryDropdown(cocktails: List<Cocktail>) {
        categories = cocktails.extractCategories()

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )

        binding.spinnerCategory.setAdapter(categoryAdapter)
    }


    private fun clearCategoryFilter() {
        selectedCategory = null
        binding.spinnerCategory.setText("", false)
        binding.btnClearCategory.visibility = View.GONE
        applyFilters(binding.etSearch.text.toString().trim(), null)
    }


    private fun applyFilters(searchQuery: String, category: String?) {
        val filteredList = allCocktails
            .filterByCategory(category)
            .filterByName(searchQuery)

        adapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            showEmpty(true)
        } else {
            showEmpty(false)
        }
    }


    private fun setupRecyclerView() {
        adapter = CocktailAdapter { cocktail ->
            onCocktailClicked(cocktail)
        }
        binding.rvCocktails.adapter = adapter

        setupSwipeToDelete()
    }


    private fun setupSwipeToDelete() {
        val swipeCallback = SwipeToDeleteCallback(requireContext()) { position ->
            val cocktail = adapter.currentList.getOrNull(position)
            cocktail?.let {
                showDeleteConfirmationDialog(it, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCocktails)
    }


    private fun showDeleteConfirmationDialog(cocktail: Cocktail, position: Int) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.delete_cocktail_title)
            setMessage(getString(R.string.delete_cocktail_message, cocktail.name))
            setPositiveButton(R.string.delete) { _, _ ->
                deleteCocktail(cocktail)
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                adapter.notifyItemChanged(position)
            }
            setOnCancelListener {
                adapter.notifyItemChanged(position)
            }
            show()
        }
    }


    private fun deleteCocktail(cocktail: Cocktail) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                requireContext().deleteCocktailFromDatabase(cocktail.id)
            }

            Toast.makeText(
                requireContext(),
                getString(R.string.cocktail_deleted, cocktail.name),
                Toast.LENGTH_SHORT
            ).show()

            LoaderManager.getInstance(this@CocktailsFragment)
                .restartLoader(LOADER_ID, null, this@CocktailsFragment)
        }
    }

    private fun onCocktailClicked(cocktail: Cocktail) {
        val bundle = bundleOf("cocktail_id" to cocktail.id)
        findNavController().navigate(R.id.action_cocktails_to_detail, bundle)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
       // tražilica
//        val query = args?.getString("query")
//
//        val selection: String?
//        val selectionArgs: Array<String>?
//
//        if (!query.isNullOrBlank()) {
//            selection = "${CocktailsTable.COLUMN_NAME} LIKE ?"
//            selectionArgs = arrayOf("%$query%")
//        } else {
//            selection = null
//            selectionArgs = null
//        }
        //

        return CursorLoader(
            requireContext(),
            COCKTAIL_PROVIDER_CONTENT_URI,
            null,
            null,
            null,
            "${CocktailsTable.COLUMN_NAME} ASC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        val cocktails = requireContext().cursorToCocktailList(data)

        allCocktails = cocktails

        populateCategoryDropdown(cocktails)

        showLoading(false)
        if (cocktails.isEmpty()) {
            showEmpty(true)
        } else {
            showEmpty(false)
            applyFilters(binding.etSearch.text.toString().trim(), selectedCategory)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.submitList(emptyList())
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvCocktails.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmpty(show: Boolean) {
        binding.tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvCocktails.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
