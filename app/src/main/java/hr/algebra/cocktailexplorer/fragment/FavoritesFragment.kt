package hr.algebra.cocktailexplorer.fragment

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import hr.algebra.cocktailexplorer.models.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val FAVORITES_LOADER_ID = 2

class FavoritesFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var _binding: FragmentCocktailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CocktailAdapter

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
        showLoading(true)
        LoaderManager.getInstance(this).initLoader(FAVORITES_LOADER_ID, null, this)
    }

    override fun onResume() {
        super.onResume()
        LoaderManager.getInstance(this).restartLoader(FAVORITES_LOADER_ID, null, this)
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

            LoaderManager.getInstance(this@FavoritesFragment)
                .restartLoader(FAVORITES_LOADER_ID, null, this@FavoritesFragment)
        }
    }

    private fun onCocktailClicked(cocktail: Cocktail) {
        val bundle = bundleOf("cocktail_id" to cocktail.id)
        findNavController().navigate(R.id.action_favorites_to_detail, bundle)
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            requireContext(),
            COCKTAIL_PROVIDER_CONTENT_URI,
            null,
            "${CocktailsTable.COLUMN_IS_FAVORITE} = ?",
            arrayOf("1"),
            "${CocktailsTable.COLUMN_NAME} ASC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val cocktails = withContext(Dispatchers.IO) {
                requireContext().cursorToCocktailList(data, defaultIsFavorite = true)
            }

            showLoading(false)
            if (cocktails.isEmpty()) {
                binding.tvEmpty.text = getString(R.string.no_favorites)
                showEmpty(true)
            } else {
                showEmpty(false)
                adapter.submitList(cocktails)
            }
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
