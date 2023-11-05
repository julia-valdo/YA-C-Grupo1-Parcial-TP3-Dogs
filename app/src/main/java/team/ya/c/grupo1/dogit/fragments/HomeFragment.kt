package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.adapters.DogAdapter
import team.ya.c.grupo1.dogit.databinding.FragmentHomeBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener


class HomeFragment : Fragment(), OnViewItemClickedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()


    private lateinit var view : View
    private lateinit var dogAdapter : DogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate( inflater, container, false)
        view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()
        setupVariables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewItemDetail(item: Any) {
        val dog = if (item is DogEntity) item else return

        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(dog)
        this.findNavController().navigate(action)
    }

    private fun setupVariables() {
        binding.progressBarHome.visibility = View.VISIBLE
        binding.progressBarHomeBottom.visibility = View.GONE
        setupRecyclerView()
        setupSwipeRefreshSettings()
    }

    private fun setupRecyclerView() {
        val query = db.collection("dogs")
            .whereEqualTo("adopterName", "")

        val config = PagingConfig(20,10,  false)

        val options = FirestorePagingOptions.Builder<DogEntity>()
            .setLifecycleOwner(this)
            .setQuery(query, config, DogEntity::class.java)
            .build()

        setupRecyclerViewSettings(binding.recyclerHomeDogs)
        dogAdapter = DogAdapter(options, this)

        setupLoadStateSettings()

        binding.recyclerHomeDogs.adapter = dogAdapter
    }

    private fun setupLoadStateSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            dogAdapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh){
                    is LoadState.Loading -> {
                        binding.progressBarHome.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarHome.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.homeLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarHome.visibility = View.GONE
                    }
                }

                when(loadStates.append){
                    is LoadState.Loading -> {
                        binding.progressBarHomeBottom.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading -> {
                        binding.progressBarHomeBottom.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        Toast.makeText(context, resources.getString(R.string.homeLoadingDogsFailed), Toast.LENGTH_SHORT).show()
                        binding.progressBarHomeBottom.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupSwipeRefreshSettings() {
        binding.swipeRefreshHome.setOnRefreshListener {
            dogAdapter.refresh()
            binding.swipeRefreshHome.isRefreshing = false
        }
    }

    private fun setupRecyclerViewSettings(recycler : RecyclerView) {
        recycler.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
    }
}